package ext.tmt.part.listener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;

import ext.tmt.utils.Contants;
import ext.tmt.utils.Debug;
import ext.tmt.utils.DocUtils;
import ext.tmt.utils.EPMDocUtil;
import ext.tmt.utils.IBAUtils;
import ext.tmt.utils.PartUtil;
import ext.tmt.utils.StringUtil;
import ext.tmt.utils.WindchillUtil;


import wt.change2.WTChangeRequest2;
import wt.csm.navigation.ClassificationNode;
import wt.epm.EPMDocument;
import wt.fc.Identified;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceManagerEvent;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.iba.definition.StringDefinition;
import wt.iba.value.ReferenceValue;
import wt.iba.value.StringValue;
import wt.org.WTOrganization;
import wt.org.WTUser;
import wt.part.PartType;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.pds.StatementSpec;
import wt.pom.PersistenceException;
import wt.pom.Transaction;
import wt.query.ClassAttribute;
import wt.query.ConstantExpression;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SQLFunction;
import wt.query.SearchCondition;
import wt.query.TableColumn;
import wt.query.WhereExpression;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.type.TypeDefinitionReference;
import wt.type.TypedUtility;
import wt.util.WTAttributeNameIfc;
import wt.util.WTException;
import wt.vc.VersionControlHelper;
import wt.vc.wip.WorkInProgressHelper;

public class PartHelper implements Serializable {
	private static final long serialVersionUID = 2304592616754675533L;

	/**
	 * ����WTPart�¼�
	 * @param target
	 * @param eventType
	 * @throws Exception
	 */
	public static void listenerWTPart(WTPart wtPart, String eventType)
			throws Exception {
		Debug.P("�¼�����---->" + eventType);
		Debug.P(wtPart.getNumber());
		String newNumber="";
		String partNumber="";
		String partType="";
		String types="";
        if (eventType.equals(PersistenceManagerEvent.POST_STORE)) {
			boolean flag = true;
			try {
				flag = SessionServerHelper.manager.setAccessEnforced(false);
				
				// ����Ǽ���ټ���
				if (WorkInProgressHelper.isCheckedOut(wtPart)) {
					return;
				}
//				// ������½��޶��汾�����½���ͼ�汾
//				if (WindchillUtil.isReviseVersion(wtPart)){
//					IBAUtils ibaUtil = new IBAUtils(wtPart);
//					ibaUtil.setIBAValue("IStoERP", "");
//					ibaUtil.updateIBAPart(wtPart);
//					return;
//				}
			   EPMDocument epmdoc = null;
			   String productName="";	
			   String prefix="";
			   partType=DocUtils.getType(wtPart);
			   partNumber=wtPart.getNumber();
			   if(StringUtils.isNotEmpty(partType)){
				   partType=partType.replaceAll(" ", "").trim();
			   }
			   Debug.P(partNumber+"------------------->"+partType);
			   
			   //����AutoCadͼֽʱ��ϵͳ����ͼֽ��ϸ���Զ����������������������Ϊ����ͼ���򲻴���
			    if(partNumber.equals("��ͼ")){
			    Debug.P(partNumber+"------------------->"+partType);
			   // PersistenceHelper.manager.delete(wtPart);
			        wtPart =null;
			     //   throw new Exception("����������Ϊ����ͼ���Ĳ���");
			    	//return;
			    }
			    String epmPartType="";
			    if(partType.equals("wt.part.WTPart")){
			    	epmdoc=EPMDocUtil.getActiveEPMDocument(wtPart);
			    	Debug.P("1-->"+epmdoc);
			    	if(epmdoc==null){
			    		epmdoc=EPMDocUtil.getEPMDocByNumber(wtPart.getNumber());
			    	}
			    	Debug.P("2-->"+epmdoc);
			    	IBAUtils epmIba = new IBAUtils(epmdoc);
			    	epmPartType=epmIba.getIBAValue(Contants.PART_TYPE);
			    	if(StringUtils.isNotEmpty(epmPartType)){
			    		epmPartType=epmPartType.replaceAll(" ", "").trim();
			    	}
			    	Debug.P(epmPartType);
			    	if(StringUtils.isNotEmpty(epmPartType)){
						if(epmPartType.equals("���Ʒ")){
							types="wt.part.WTPart|"+Contants.SEMIFINISHEDPRODUCT;
						}else if(epmPartType.equals("��Ʒ")){
							wtPart.setEndItem(true);
							types="wt.part.WTPart|"+Contants.PRODUCTPART;
						}else{
							return;
						}
						Debug.P(types);
						if(types.contains("Product")){
							partType=DocUtils.getDfmDocumentType(types).getIntHid();
							Debug.P(partType);
							TypeDefinitionReference typeDefinitionRef = TypedUtility.getTypeDefinitionReference(types);
							wtPart.setPartType(PartType.getPartTypeDefault());
							wtPart.setTypeDefinitionReference(typeDefinitionRef);
							if (!PersistenceHelper.isPersistent(wtPart)) {
								wtPart = (WTPart) PersistenceHelper.manager.save(wtPart);
								wtPart = (WTPart) PersistenceHelper.manager.refresh(wtPart);
							}else{
								PersistenceServerHelper.manager.update(wtPart);
							}
						}
					}
			    	partType=DocUtils.getType(wtPart);
			    	Debug.P(partNumber+"------------------->"+partType);
			    }else if(partType.contains(Contants.PRODUCTPART)){ //����ǳ�Ʒ
			    	wtPart.setEndItem(true);
			    	if (!PersistenceHelper.isPersistent(wtPart)) {
						wtPart = (WTPart) PersistenceHelper.manager.save(wtPart);
						wtPart = (WTPart) PersistenceHelper.manager.refresh(wtPart);
					}else{
						PersistenceServerHelper.manager.update(wtPart);
					}
			    	//					if(!wtPart.isEndItem()){
//						throw new Exception("���������ǲ�Ʒ���뽫���Ƿ�Ϊ��Ʒ����ֵ����Ϊ���ǡ���");
//					}
				//��Ʒ����=TX+��λ������+��λ��ˮ�롣���з�����Ϊ��Ʒ���ڲ�Ʒ���������Ƶ�ǰ�����ַ����Զ����ݳ�Ʒ���ڲ�Ʒ���ȡ��
					productName=wtPart.getContainerName();
					//�������벿��ʱ�������Ĳ������뺬��TX���޸Ĳ�������
					if(wtPart.getNumber().toUpperCase().contains("TX")){
						return;
					}
					Debug.P("productName--------->"+productName);
					prefix=productName.substring(0, prefix.indexOf("-"));
					if(!prefix.toUpperCase().contains("TX")){
						throw new Exception("��Ʒ:"+productName+"  δ����  TX ǰ׺��");
					}
					Debug.P("��Ʒǰ׺----��"+prefix);
					int i= 0;
					do {
						if(prefix.toUpperCase().trim().equals("TXA6")||prefix.toUpperCase().trim().equals("TXA7")||prefix.toUpperCase().trim().equals("TXA8")){
							partNumber=prefix+StringUtil.int2String(i,5);
						}else{
							partNumber=prefix+StringUtil.int2String(i,4);
						}
						if(PartUtil.getPartByNumber(partNumber)==null){
							newNumber=partNumber;
							break;
						}
						i++;
					} while (i < 100000);
					changePartNumber(wtPart,newNumber);
				}
			    else //����ǰ��Ʒ
				if(partType.contains(Contants.SEMIFINISHEDPRODUCT)){
					if(wtPart.isEndItem()){
						throw new Exception("���������ǰ��Ʒ���뽫���Ƿ�Ϊ��Ʒ����ֵ����Ϊ���񡱣�");
					}
					String isKHpart="";//�ջɲ�������
					IBAUtils iba = new IBAUtils(wtPart);
					isKHpart=iba.getIBAValue(Contants.AIRSPRINGCLASSIFICATION);
					Debug.P("WTPart -->"+isKHpart);
					//��������ϵ� �ջɲ������� ֵΪ�գ���Ӳ���������EPMDocument�ϻ�ȡ
					if(StringUtils.isEmpty(isKHpart)){
						IBAUtils epmIBA = new IBAUtils(epmdoc);
						isKHpart=epmIBA.getIBAValue(Contants.AIRSPRINGCLASSIFICATION);
						Debug.P("EPMDocument -->"+isKHpart);
					}
					if(StringUtils.isNotEmpty(isKHpart)){
						//prefix=productName.substring(0, 5);
						int i= 0;
						do {
							partNumber=isKHpart+StringUtil.int2String(i,4);
							if(PartUtil.getPartByNumber(partNumber)==null){
								newNumber=partNumber;
								break;
							}
							i++;
						} while (i < 100000);
						changePartNumber(wtPart,newNumber);
						
						//WCToPMHelper.CreatePartToPM(wtPart);
					}
				}
			}finally {
				SessionServerHelper.manager.setAccessEnforced(flag);
			}
		} 
	}

	

	/*
	 * ��ѯ�������±���
	 */
	public static boolean findNewPartNum(String newPartNum) throws WTException {
		boolean result = false;
		QuerySpec qs = new QuerySpec(WTPart.class);
		SearchCondition sc = new SearchCondition(WTPart.class, WTPart.NUMBER,
				"=", newPartNum);
		qs.appendSearchCondition(sc);
		qs.appendAnd();
		SearchCondition sc1 = VersionControlHelper.getSearchCondition(
				wt.part.WTPart.class, true);
		qs.appendSearchCondition(sc1);
		QueryResult qr = PersistenceHelper.manager.find(qs);
		if (qr.hasMoreElements()) {
			result = true;
		}
		return result;
	}

	/**
	 * ���ݱ�Ź���,Ϊ��Ʒ�ı���
	 * 
	 * @param part
	 * @param number
	 * @throws WTException
	 */
	private static void changePartNumber(WTPart part, String number)
			throws WTException {
		boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
		WTUser currentuser = (WTUser) SessionHelper.manager.getPrincipal();
		SessionHelper.manager.setAdministrator();
		Transaction tx = null;
		try {
			tx = new Transaction();
			tx.start();
			Identified identified = (Identified) part.getMaster();
			String name = part.getName();
			WTOrganization org = part.getOrganization();
			WTPartHelper.service.changeWTPartMasterIdentity(
					(WTPartMaster) identified, name, number, org);
			tx.commit();
			tx = null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (tx != null)
				tx.rollback();
			SessionHelper.manager.setPrincipal(currentuser
					.getAuthenticationName());
			SessionServerHelper.manager.setAccessEnforced(flag);
		}
	}
	public static void main(String[] args) {
		String str="TXA6-��  ��  Ʒ";
		System.out.println(str.replaceAll(" ", "").trim());
		System.out.println(str.substring(0,str.indexOf("-")));
	}
}