
package org.linagora.linshare.webservice.test.soap;

import javax.xml.ws.WebFault;


/**
 * This class was generated by Apache CXF 2.7.0
 * 2012-11-20T17:29:03.125+01:00
 * Generated source version: 2.7.0
 */

@WebFault(name = "BusinessException", targetNamespace = "http://webservice.linshare.linagora.org/")
public class BusinessException_Exception extends Exception {
    
    private org.linagora.linshare.webservice.test.soap.BusinessException businessException;

    public BusinessException_Exception() {
        super();
    }
    
    public BusinessException_Exception(String message) {
        super(message);
    }
    
    public BusinessException_Exception(String message, Throwable cause) {
        super(message, cause);
    }

    public BusinessException_Exception(String message, org.linagora.linshare.webservice.test.soap.BusinessException businessException) {
        super(message);
        this.businessException = businessException;
    }

    public BusinessException_Exception(String message, org.linagora.linshare.webservice.test.soap.BusinessException businessException, Throwable cause) {
        super(message, cause);
        this.businessException = businessException;
    }

    public org.linagora.linshare.webservice.test.soap.BusinessException getFaultInfo() {
        return this.businessException;
    }
}
