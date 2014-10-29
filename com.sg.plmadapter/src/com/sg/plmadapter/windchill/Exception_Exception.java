
package com.sg.plmadapter.windchill;

import javax.xml.ws.WebFault;


/**
 * This class was generated by Apache CXF 2.6.14
 * 2014-10-29T16:28:56.654+08:00
 * Generated source version: 2.6.14
 */

@WebFault(name = "Exception", targetNamespace = "http://pm.webservice.integration.tmt.ext/")
public class Exception_Exception extends java.lang.Exception {
    
    private com.sg.plmadapter.windchill.Exception exception;

    public Exception_Exception() {
        super();
    }
    
    public Exception_Exception(String message) {
        super(message);
    }
    
    public Exception_Exception(String message, Throwable cause) {
        super(message, cause);
    }

    public Exception_Exception(String message, com.sg.plmadapter.windchill.Exception exception) {
        super(message);
        this.exception = exception;
    }

    public Exception_Exception(String message, com.sg.plmadapter.windchill.Exception exception, Throwable cause) {
        super(message, cause);
        this.exception = exception;
    }

    public com.sg.plmadapter.windchill.Exception getFaultInfo() {
        return this.exception;
    }
}
