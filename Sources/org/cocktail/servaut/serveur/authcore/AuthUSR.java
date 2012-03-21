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
import org.cocktail.servaut.serveur.Application;

import com.webobjects.appserver.WOApplication;
import com.webobjects.appserver.WORequest;
import com.webobjects.appserver.WOResponse;

import fr.univlr.cri.util.CRIpto;
import fr.univlr.cri.util.StringCtrl;
import fr.univlr.cri.util.wo5.DateCtrl;
import fr.univlr.cri.webapp.LRDataResponse;
import fr.univlr.cri.webapp.LRUserInfo;
import fr.univlr.cri.webapp.LRUserInfoDB;

/**
 * Cette classe permet de generer une reponse en format SOAP contenant
 * la definition des applications. 
 * 
 * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
 */
public class AuthUSR extends AuthResponse {
  protected String userDico;

  public AuthUSR(WORequest request) {
    super(request);
    userDico = StringCtrl.emptyString();
  }

  protected Application sautApp() {
    return (Application)WOApplication.application();
  }
  
  public void parseContent() {
    super.parseContent();
    if (getFaultCode() != null) return;
    //
    // System.out.println("SOAP request : "+new String(context().request().content().bytes(0, context().request().content().length())));
    //
    String ulg = StringCtrl.normalize(CRIpto.decrypt(getValueForTagName("Login")));
    String pwd = StringCtrl.normalize(CRIpto.decrypt(getValueForTagName("Password")));
    newLog().append("[").append(DateCtrl.currentDateTimeString()).append("] user ").append(ulg);
    String alias = tryValueForTagName("Alias");
    if (alias != null) {
      log().append(" (").append(StringCtrl.normalize(CRIpto.decrypt(alias))).append(")");
    } 
    log().append(", adresse ").append(app.getRequestIPAddress(request()));
    log().append(" - ");
    if (ulg.length() == 0) {
      setSoapErrorMessage("le nom d'utilisateur n'a pas ete donne (login).");
    }
    // le resultat sera inclus dans la reponse SOAP
    userDico = getCryptedUserDictionary(ulg, pwd);
    flushLog();
    // System.out.println("User Dico : "+userDico);
  }

  public WOResponse getResponse() {
    LRDataResponse dataResponse = new LRDataResponse();
    String answerContent = "<UserDictionary>"+userDico+"</UserDictionary>";
    // dataResponse.setContent(getResponseForContent(answerContent), LRDataResponse.MIME_XML);
    dataResponse.setContent(getResponseForContent(answerContent), "text/xml");
    return dataResponse;
  }

  /**
   * Genere le dictionnaire crypte d'infos utilisateur.
   */
  private String getCryptedUserDictionary(String ulg, String pwd) {
    return CRIpto.crypt(getUserDictionary(ulg, pwd));
  }
  
  /**
   * Genere le dictionnaire non-crypte d'infos utisateur.
   */
  private String getUserDictionary(String ulg, String pwd) {
    StringBuffer dico = new StringBuffer();
    LRUserInfo userInfo = null;
    if (ulg.length() == 0) {
      dico.append("errno=1\n");
      dico.append("msg=Login non saisi");
      log().append("Echec (1)");
      return dico.toString();
    } else {
      userInfo = new LRUserInfoDB(app.dataBus());
      userInfo.setRootPass(null);
      userInfo.setAcceptEmptyPass(app.isAcceptEmptyPassword());
      userInfo.compteForLogin(ulg, pwd, true);
      if (userInfo.errorCode() != LRUserInfo.ERROR_NONE) {
        if (userInfo.errorCode() == LRUserInfo.ERROR_COMPTE) {
          dico.append("errno=2\n");
          dico.append("msg=Erreur de login");
          log().append("Echec (2)");
          return dico.toString();
        } else if (userInfo.errorCode() == LRUserInfo.ERROR_SOURCE) {
          dico.append("errno=3\n");
          dico.append("msg=Erreur dans la base de donnees");
          log().append("Echec (3)");
          return dico.toString();
        } else {
          dico.append("errno=4\n");
          dico.append("msg=Erreur dans mot de passe");
          log().append("Echec (4)");
          return dico.toString();
        }
      }
    }
    dico.append("errno=0\n");
    dico.append("msg=Succes\n");
    dico.append("nom=").append(userInfo.nom()).append("\n");
    dico.append("prenom=").append(userInfo.prenom()).append("\n");
    dico.append("login=").append(userInfo.login()).append("\n");
    dico.append("email=").append(userInfo.email()).append("\n");
    dico.append("noIndividu=").append(userInfo.noIndividu()).append("\n");
    dico.append("persId=").append(userInfo.persId()).append("\n");
    dico.append("noCompte=").append(userInfo.noCompte()).append("\n");
    dico.append("vLan=").append(userInfo.vLan());
    log().append("OK");
    return dico.toString();
  }
}
