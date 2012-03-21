package org.cocktail.servaut.serveur.authcore;
/*
 * Copyright CRI - Universite de La Rochelle, 2001-2005 
 * 
 * This software is governed by the CeCILL license under French law and
 * abiding by the rules of distribution of free software. You can use, 
 * modify and/or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info". 
 * 
 * As a counterpart to the access to the source code and rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty and the software's author, the holder of the
 * economic rights, and the successive licensors have only limited
 * liability. 
 * 
 * In this respect, the user's attention is drawn to the risks associated
 * with loading, using, modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean that it is complicated to manipulate, and that also
 * therefore means that it is reserved for developers and experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or 
 * data to be ensured and, more generally, to use and operate it in the 
 * same conditions as regards security. 
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
import java.io.StringWriter;

import org.cocktail.servaut.serveur.Application;

import com.webobjects.appserver.WOApplication;
import com.webobjects.appserver.WORequest;
import com.webobjects.appserver.WOResponse;
import com.webobjects.foundation.NSData;

import fr.univlr.cri.util.LRXMLNode;
import fr.univlr.cri.util.LRXMLWriter;
import fr.univlr.cri.webapp.LRLog;

/**
 * Cette classe permet d'analyser les requetes et generer les reponses en
 * format SOAP.
 * 
 * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
 */
public abstract class AuthResponse {
  public static final Application app = (Application)WOApplication.application();
  private StringBuffer log;  
  private LRXMLNode rootNode;
  private String faultCode;
  private String faultString;
  private WORequest request;
  
  public AuthResponse(WORequest request) {
    this.request = request;
  }

  public StringBuffer newLog() {
    flushLog();
    log = new StringBuffer();
    return log;
  }
  
  public StringBuffer log() {
    return log;
  }
  
  public void flushLog() {
    if ((log != null) && (log.length() > 0))
      System.out.println(log.toString());
  }
  
  public void parseContent() {
    String contentString = null;
    try {
      LRLog.trace("request().content()");
      NSData content = request().content();
      LRLog.trace("contentString");
      contentString = new String(content.bytes(0, content.length())); 
      LRLog.trace("LRXMLNode.parse");
      rootNode = LRXMLNode.parse(contentString);
      LRLog.trace("parseContent - END");
    } catch(Throwable e) {
      // Parfois, on obtient OutOfMemoryError
      e.printStackTrace();
//      System.out.println("--- SOAP request : ---");
//      System.out.println(contentString);
      LRLog.trace("Exception : "+getMessageForException(e));
      setSoapErrorMessage(getMessageForException(e));
    }
    if (rootNode == null) {
      setSoapErrorMessage("la requette SOAP est vide");
//      app.trace("LRSOAPContent.initSOAP() - Empty Message");
    }
  }
  
  public WORequest request() {
    return request;
  }
  
  public void setFaultCode(String newCode) {
    faultCode = newCode;
  }
  
  public String getFaultCode() {
    return faultCode;
  }

  public void setFaultString(String newString) {
    faultString = newString;
  }
  
  public String getFaultString() {
    return faultString;
  }

  public void setSoapErrorMessage(String message) {
    setFaultCode("1002");
    setFaultString("Erreur lors de l'analyse SOAP : "+message);
  }
  
  public LRXMLNode documentRootNode() {
    return rootNode;
  }
  
  public String getValueForTagName(String tagName) {
    String tagValue = tryValueForTagName(tagName);
    if (tagValue == null) {
      setFaultCode("1001");
      setFaultString("La balise inconnue  : "+tagName);
    }
    return tagValue;
  }

  public String tryValueForTagName(String tagName) {
    try {
      LRXMLNode node = rootNode.findChild(tagName, true);
      if (node != null)
        return node.getCharacters();
    } catch(Exception ex) { }
    return null;
  }
  
  public String getMessageForException(Throwable e) {
    String msg = e.getClass().getName();
    if (e.getMessage() != null) msg += " : "+e.getMessage();
    return msg;
  }
  
  public NSData getResponseForContent(String answerContent) {
    try {
      LRLog.trace("getResponseForContent");
      StringWriter responseContent = new StringWriter();
      LRXMLWriter xmlWriter = new LRXMLWriter(responseContent);
      xmlWriter.setUseCompactMode(true);
      xmlWriter.setNamespace("SOAP-ENV", "http://schemas.xmlsoap.org/soap/envelope");
      xmlWriter.startDocument();
      xmlWriter.startElement("Envelope");
      xmlWriter.startElement("Body");
      if (getFaultCode() != null) {
        xmlWriter.startElement("Fault");
        LRLog.trace("Getting fault code");
        xmlWriter.writeString("<faultcode>"+getFaultCode()+"</faultcode>");
        LRLog.trace("Getting fault description");
        xmlWriter.writeString("<faultstring>"+getFaultString()+"</faultstring>");
        xmlWriter.endElement();
      } else {
        xmlWriter.writeString("<saut:Response xmlns:saut=\"http://www.univ-lr.fr/saut\">");
        xmlWriter.writeString(answerContent);
        xmlWriter.writeString("</saut:Response>");
      }
      xmlWriter.endElement();
      xmlWriter.endElement();
      xmlWriter.endDocument();
      xmlWriter.close();
      responseContent.close();
//      System.out.println("--- Response ---");
//      System.out.println(responseContent.toString());
//      System.out.println("--- END.Response ---");
      LRLog.trace("getResponseForContent - END");
      return new NSData(responseContent.toString().getBytes());
    } catch(Throwable e) {
      e.printStackTrace();
      LRLog.trace("exception : "+getMessageForException(e));
      setFaultCode("1003");
      setFaultString("Erreur de l'ecriture de la reponse : "+getMessageForException(e));
      return null;
    }
  }
  
  /**
   * Retourne l'objet WOResponse qui sera retourne au client SAUT.
   * Les sous classes doivent redefinir cette methode.
   * @return
   */
  public abstract WOResponse getResponse();
}
