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
import com.webobjects.appserver.WORequest;
import com.webobjects.appserver.WOResponse;
import com.webobjects.eocontrol.EOEnterpriseObject;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;

import fr.univlr.cri.util.CRIpto;
import fr.univlr.cri.util.StringCtrl;
import fr.univlr.cri.util.wo5.DateCtrl;
import fr.univlr.cri.webapp.LRDataResponse;
import fr.univlr.cri.webapp.LRLog;

/**
 * Cette classe permet de generer les reponses en format SOAP contenant
 * le dictionnaire de connexion pour un model de donnees.
 * 
 * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
 */
public class AuthDB extends AuthResponse {
  protected String connectionDico;
  
  public AuthDB(WORequest request) {
    super(request);
    LRLog.trace(null);
    connectionDico = StringCtrl.emptyString();
//    LRLog.rawLog("AuthDB.<init> 1 : "+request._remoteAddress());
//    LRLog.rawLog("AuthDB.<init> 2 : "+request.headers());
  }

  public void parseContent() {
    LRLog.trace("parseContent");
    super.parseContent();
    if (getFaultCode() != null) return;
    //
    // System.out.println("SOAP request : "+new String(context().request().content().bytes(0, context().request().content().length())));
    //
    LRLog.trace("get form values");
    String cid = StringCtrl.normalize(getValueForTagName("ClientID"));
    String cal = StringCtrl.normalize(tryValueForTagName("ClientAlias"));
    LRLog.trace("construct log");
    newLog().append("[").append(DateCtrl.currentDateTimeString()).append("] bd client ").append(cid);
    if (cal.length() > 0) log().append(" (").append(cal).append(")");
    log().append(", adresse ").append(app.getRequestIPAddress(request())).append(" - ");
    if (cid.length() == 0) {
      setSoapErrorMessage("le ID de client n'a pas ete donne.");
    }
    // le resultat sera inclus dans la reponse SOAP
    LRLog.trace("Getting dico");
    connectionDico = getCryptedConnectionDictionary(cid);
    LRLog.trace("Writing Log");
    flushLog();
    LRLog.trace("parseContent - END");
    // System.out.println("Connection Dico : "+connectionDico);
  }

  public WOResponse getResponse() {
    LRLog.trace("getResponse");
    LRDataResponse dataResponse = new LRDataResponse();
    String answerContent = "<ConnectionDictionary>"+connectionDico+"</ConnectionDictionary>";
    // dataResponse.setContent(getResponseForContent(answerContent), LRDataResponse.MIME_XML);
    LRLog.trace("Creating response for content");
    dataResponse.setContent(getResponseForContent(answerContent), "text/xml");
    LRLog.trace("getResponse - END");
    return dataResponse;
  }
  
  /**
   * Genere le dictionnaire crypte de connexion correspondant
   * a l'application avec le code donne.
   */
  private String getCryptedConnectionDictionary(String cid) {
    return CRIpto.crypt(getConnectionDictionary(cid));
  }

  /**
   * Genere le dictionnaire non-crypte de connexion correspondant
   * a l'application avec le code donne.
   */
  private String getConnectionDictionary(String cid) {
    StringBuffer dico = new StringBuffer();
    EOQualifier condition = EOQualifier.qualifierWithQualifierFormat("bdId = '"+cid+"'", null);
    NSArray objects = app.dataBus().fetchArray("BdDico", condition, null);
    EOEnterpriseObject rec;
    // Teste si le resultat est correcte
    if (objects.count() == 0) {
      dico.append("errno=1\n");
      dico.append("msg=Client avec le code ").append(cid).append(" est inconnu");
      log().append("Echec (1)");
      return dico.toString();
    }
    if (objects.count() > 1) {
      dico.append("errno=2\n");
      dico.append("msg=Plusieurs clients avec le code ").append(cid).append(" sont trouves");
      log().append("Echec (2)");
      return dico.toString();
    }
    rec = (EOEnterpriseObject)objects.objectAtIndex(0);
    dico.append("errno=0\n");
    dico.append("msg=Succes\n");
    dico.append("userName=").append(rec.valueForKey("bdUtilisateur")).append("\n");
    dico.append("password=").append(rec.valueForKey("bdPasse")).append("\n");
    dico.append("serverId=").append(rec.valueForKey("bdInstance")).append("\n");
    dico.append("URL=").append(rec.valueForKey("bdUrl"));
    log().append("OK");
    return dico.toString();
  }
  
  /**
   * Genere le dictionnaire d'authnetification pour l'application
   */
  public String getAuthLevel(String aid)
  {
  	StringBuffer dico = new StringBuffer();
    EOQualifier condition = EOQualifier.qualifierWithQualifierFormat("appliId = '"+aid+"'", null);
    NSArray objects = app.dataBus().fetchArray("Application", condition, null);
    EOEnterpriseObject rec;
    // Teste si le resultat est correcte
    if (objects.count() == 0) {
      dico.append("errno=1\n");
      dico.append("msg=Application avec le code ").append(aid).append(" est inconnu");
      log().append("Echec (1)");
      return dico.toString();
    }
    if (objects.count() > 1) {
      dico.append("errno=2\n");
      dico.append("msg=Plusieurs applications avec le code ").append(aid).append(" sont trouves");
      log().append("Echec (2)");
      return dico.toString();
    }
    rec = (EOEnterpriseObject)objects.objectAtIndex(0);
    dico.append("errno=0\n");
    dico.append("msg=Succes\n");
    dico.append("authLevel=").append(rec.valueForKey("appliAuth")).append("\n");
    //log().append("OK");
    System.out.println(dico.toString());
    return dico.toString();
  }
}
