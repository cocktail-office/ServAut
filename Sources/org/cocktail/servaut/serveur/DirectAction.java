package org.cocktail.servaut.serveur;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.cocktail.servaut.serveur.authcore.AuthDB;
import org.cocktail.servaut.serveur.authcore.AuthUSR;
import org.cocktail.servaut.serveur.components.Main;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WODirectAction;
import com.webobjects.appserver.WORequest;
import com.webobjects.appserver.WOResponse;

import fr.univlr.cri.util.StringCtrl;
import fr.univlr.cri.webapp.LRDataResponse;
import fr.univlr.cri.webapp.LRLog;


/**
 * Gere les actions directes "Web" pouvant etre executes sur l'application.
 * 
 * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
 * @author Marc BOISIS-DELAVAUD <marc-henri.boisis-delavaud at univ-lr.fr>
 * @author Hugues VILLESUZANNE <hugues.villesuzanne at univ-lr.fr>
 */
public class DirectAction extends WODirectAction {
	
  /**
   * Cree une nouvelle action pour gerer la requete <code>aRequest</code>.
   */
	public DirectAction(WORequest aRequest) {
		super(aRequest);
	}
	
  /**
   * L'action par defaut retourne la page d'accueil.
   */
	public WOActionResults defaultAction() {
		return pageWithName("Main");
	}
	
	/**
	 * Pour garder la compatibilite avec l'ancienne version.
   * 
   * @deprecated Utiliser l'action <code>dbAction</code>.
   * 
   * @see #dbAction()
	 */
	public WOActionResults bdAction() {
		return dbAction();
	}
	
	/**
	 * Cette action retourne au client le dictionnaire crypte de connexion
	 * a la base de donnees. Le dictionnaire correspond a l'identifiant de
     * modele donne dans la requete adressee a cette action. La requete et
     * la reponse sont envoyees en format SOAP.
	 */
	public WOActionResults dbAction() {
		LRLog.trace("Create AuthDB");
		AuthDB soapBD = new AuthDB(request());
		LRLog.trace("soapBD.parseContent()");
		soapBD.parseContent();
		LRLog.trace("soapBD.getResponse()");
		return soapBD.getResponse();
	}
	
  /**
   * Cette action retourne au client les informations sur l'utilisateur
   * dont le login et le mot de passe sont adresse a l'action. La requete et
   * la reponse sont envoyees en format SOAP.
   */
	public WOActionResults usrAction() {
		AuthUSR soapUSR = new AuthUSR(request());
		soapUSR.parseContent();
		return soapUSR.getResponse();
	}
	
	/**
	 * Retourne <i>true</i> si la requette en cours utilise le protocole HTTPS.
	 */
	private boolean isHTTPSRequest() {
		try {
			return request().headersForKey("https").toString().equalsIgnoreCase("(\"on\")");
		} catch(Exception e) {
			return false;
		}
	}
  
  /**
   * Retourne la page avec la description des services et directActions
   * de l'application.
   */
  public WOActionResults servicesAction() {
    return pageWithName("Services");
  }
	
	/**
	 * Cette action envoie le fichier XML avec la description des applications
     * pour ZAP.
	 */
	public WOActionResults getXMLZAPAction() {
		LRLog.log("getXMLZAP IP : " + (String)((Application)Application.application()).getRequestIPAddress(request()));
		LRDataResponse rep = new LRDataResponse();
		Main page = (Main)pageWithName("Main");
		String str = StringCtrl.emptyString();
		try {
			str = GenerateXMLZAP.getXml((Session)session(),(Application)page.application());
			if (str==null)
				return null;
		} catch (Exception e) {
			return null;
		}
		
		rep.setContent(str.getBytes(),LRDataResponse.MIME_XML);
		return rep.generateResponse();	
	}
	
	/**
	 * Cette action (accessible seulement en HTTPS) est utilisee pour 
	 * le proxyCAS lorsque qu'une appli tierce (ZAP par exemple) demande 
	 * un Proxy-Granting-Ticket (PGT), CAS envoie le PGTIOU et son PGT associe 
	 * en https sur cette URL et ceux-ci sont stockes dans le dico pgtDico 
	 * de la classe Application.
	 */	
	public WOActionResults proxyCallBackAction() {
		WOResponse response = new WOResponse();
		if (!isHTTPSRequest())
			response.setStatus(WOResponse.HTTP_STATUS_FORBIDDEN);
		else
		{
			response.setStatus(WOResponse.HTTP_STATUS_OK);
			Application app = (Application)Application.application();
			app.addPgtForPgtIou((String)request().formValueForKey("pgtId"),(String)request().formValueForKey("pgtIou"));
		}
		return response;
	}

	/**
	 * Cette action (accessible seulement en HTTPS) est utilisee pour 
	 * le proxyCAS lorsque qu'une appli tierse (ZAP par exemple) possedant
	 * un PGTIOU connu de ServAut veut obtenir un Proxy-Ticket (PT). 
	 * L'appli Tierce fournit le PGTIOU et ServAut obtient et retourne le PT.
	 */	
	public WOActionResults getCasPTAction() {
		WOResponse response = new WOResponse();
		String pt="null";
		if (!isHTTPSRequest())
		{
			response.setStatus(WOResponse.HTTP_STATUS_FORBIDDEN);
			return response;
		}
		else
		{
			Application app = (Application)Application.application();
			try {
				pt = getCasPT((String)request().formValueForKey("targetService"), (String)app.pgtForPgtIou((String)request().formValueForKey("pgtIou")));
			}
			catch(IOException e) {}
			response.appendContentString(pt);
			return response;
		}
	}
	
	/**
	 * Methode permettant d'obtenir un Proxy-Ticket (PT) aupres de CAS 
	 * @param targetService URL du service final utilisant le PT
	 * @param pgtId String correspondant au Proxy-Granting-Ticket (PGT)
	 * @return String representant le Proxy-Ticket (PT) ou "null"
	 */	
	private String getCasPT(String targetService, String pgtId) throws IOException {
		String pt;
		BufferedReader r = null;
		StringBuffer resp = new StringBuffer();
		try {
			URL u = new URL((String)((Application)Application.application()).config().stringForKey("CAS_SERVICE_URL") + "/proxy?targetService="+URLEncoder.encode(targetService, "UTF-8")+"&pgt="+pgtId);
			URLConnection uc = u.openConnection();
			uc.setRequestProperty("Connection", "close");
			r = new BufferedReader(new InputStreamReader(uc.getInputStream()));
			String line;
			while((line = r.readLine()) != null)
				resp.append(line).append("\n");
			
			Pattern pattern = Pattern.compile("<cas:proxySuccess>.*<cas:proxyTicket>(.*)</cas:proxyTicket>.*</cas:proxySuccess>", Pattern.DOTALL | Pattern.MULTILINE);
			Matcher matcher = pattern.matcher(resp.toString());
							
			if ( matcher.find() )
					pt = matcher.group(1);
				else pt = "null";
				
		} finally {
			try {
				if (r != null) r.close();
			} catch(IOException ex) {}
		}
		return pt;
	}
	
//	/**
//	 * Cette action retourne le niveau d'authentification d'une application
//	 */
//	public WOActionResults getAuthAction() {
//		try{
//			LRDataResponse rep = new LRDataResponse();
//			String id = this.request().formValueForKey("application_id").toString();
//			//requete pour conna�tre le niveau d'authentification relatif
//			AuthDB soapDB = new AuthDB(this.request());
//			String level = soapDB.getAuthLevel(id);
//			rep.setContent(level.getBytes(),LRDataResponse.MIME_TXT);
//			return rep;
//		}catch (Exception e)
//		{
//			e.printStackTrace();
//			return null;
//		}
//	}
}
