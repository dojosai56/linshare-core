package org.linagora.linshare.webservice.test.soap;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * This class was generated by Apache CXF 2.7.0
 * 2012-11-20T17:29:02.906+01:00
 * Generated source version: 2.7.0
 * 
 */
@WebService(targetNamespace = "http://webservice.linshare.linagora.org/", name = "DocumentSoapService")
@XmlSeeAlso({ObjectFactory.class, org.linagora.linshare.webservice.jaxrs.ObjectFactory.class})
public interface DocumentSoapService {

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "addDocumentXop", targetNamespace = "http://webservice.linshare.linagora.org/", className = "org.linagora.linshare.webservice.test.soap.AddDocumentXop")
    @WebMethod
    @ResponseWrapper(localName = "addDocumentXopResponse", targetNamespace = "http://webservice.linshare.linagora.org/", className = "org.linagora.linshare.webservice.test.soap.AddDocumentXopResponse")
    public org.linagora.linshare.webservice.test.soap.Document addDocumentXop(
        @WebParam(name = "arg0", targetNamespace = "")
        org.linagora.linshare.webservice.test.soap.DocumentAttachement arg0
    ) throws BusinessException_Exception;

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "getAvailableSize", targetNamespace = "http://webservice.linshare.linagora.org/", className = "org.linagora.linshare.webservice.test.soap.GetAvailableSize")
    @WebMethod
    @ResponseWrapper(localName = "getAvailableSizeResponse", targetNamespace = "http://webservice.linshare.linagora.org/", className = "org.linagora.linshare.webservice.test.soap.GetAvailableSizeResponse")
    public org.linagora.linshare.webservice.test.soap.SimpleLongValue getAvailableSize() throws BusinessException_Exception;

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "getDocuments", targetNamespace = "http://webservice.linshare.linagora.org/", className = "org.linagora.linshare.webservice.test.soap.GetDocuments")
    @WebMethod
    @ResponseWrapper(localName = "getDocumentsResponse", targetNamespace = "http://webservice.linshare.linagora.org/", className = "org.linagora.linshare.webservice.test.soap.GetDocumentsResponse")
    public java.util.List<org.linagora.linshare.webservice.test.soap.Document> getDocuments() throws BusinessException_Exception;

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "getUserMaxFileSize", targetNamespace = "http://webservice.linshare.linagora.org/", className = "org.linagora.linshare.webservice.test.soap.GetUserMaxFileSize")
    @WebMethod
    @ResponseWrapper(localName = "getUserMaxFileSizeResponse", targetNamespace = "http://webservice.linshare.linagora.org/", className = "org.linagora.linshare.webservice.test.soap.GetUserMaxFileSizeResponse")
    public org.linagora.linshare.webservice.test.soap.SimpleLongValue getUserMaxFileSize() throws BusinessException_Exception;
}
