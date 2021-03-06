package ext.tmt.integration.webservice.pm;


import java.io.ObjectInputStream;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebService;

import com.ptc.jws.servlet.JaxWsWebService;

import ext.tmt.integration.webservice.spm.SPMWebserviceImpl;
/**
 * PM系统的调用的Webservices接口
 * @author Administrator
 *
 */
@WebService()
public class PMWebservice extends JaxWsWebService
{

    @WebMethod(operationName="createFolder")
    public int createFolder(String[] result )throws Exception
    {
    	
         return PMWebserviceImpl.createFolderEntryList(result);
    }
    

    @WebMethod(operationName="editFolder")
    public int editFolder (String objectId,String newFolderName)throws Exception
    {
       return PMWebserviceImpl.modifyFolderEntry(objectId, newFolderName);
    }
   
    @WebMethod(operationName="deleteFolder")
    public int deleteFolder (String objectId)throws Exception
    {
       return PMWebserviceImpl.deleteFolderEntry(objectId);
    }
    
    @WebMethod(operationName="createDocument")
    public int createDocument (String objectId)throws Exception
    {
       return PMWebserviceImpl.createWTDocumentEntry(objectId);
    }
    
    @WebMethod(operationName="updateDocument")
    public int updateDocument (String objectId)throws Exception
    {
       return PMWebserviceImpl.updateWTDocumentEntry(objectId);
    }
    
    @WebMethod(operationName="moveDocument")
    public int moveDocument (String objectId)throws Exception
    {
       return PMWebserviceImpl.moveWTDocumentEntry(objectId);
    }
    
    @WebMethod(operationName="deleteDocument")
    public int deleteDocument (String objectType,String objectId,String deletAction)throws Exception
    {
       return PMWebserviceImpl.deleteWTDocumentEntry(objectType,objectId,deletAction);
    }
    
    @WebMethod(operationName="changeRevision")
    public void changeRevision (String objectId)throws Exception
    {
         PMWebserviceImpl.changeRevision(objectId); 
    }
    
    
    @WebMethod(operationName="changeLifecycleState")
    public void changeLifecycleState (String objectId)throws Exception
    {
         PMWebserviceImpl.changeLifecycleState(objectId);
    }
    
    @WebMethod(operationName="changePhase")
    public void changePhase (String objectId)throws Exception
    {
         PMWebserviceImpl.changePhase(objectId);
    }
    
    @WebMethod(operationName="queryWCObjectByPM")
    public List<String> queryWCObjectByPM(String pmid ,String class1,String class2,boolean flag)throws Exception
    {
         return PMWebserviceImpl.queryWCObjectByPM(pmid,class1,class2,true);
    }
   
    @WebMethod(operationName="getRepresentationByPM")
    public String getRepresentationByPM(String pmid)throws Exception
    {
         return PMWebserviceImpl.getRepresentationByPM(pmid);
    }
    
    
    
}
