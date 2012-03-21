package org.cocktail.servaut.serveur.components;
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
import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WORequest;

import fr.univlr.cri.webapp.CRIWebPage;
import fr.univlr.cri.webapp.LRLog;
import fr.univlr.cri.webext.LRAdminLoginResponder;

/**
 * Cette page correspond a la page d'acceuil a l'application. L'acces a
 * l'application est protege par un mot de passe.
 * 
 * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
 */
public class Main extends CRIWebPage {
  private LoginResponder responder;
  private WOContext currentContext;
  
  public Main(WOContext context) {
    super(context);
    currentContext = context;
  }

  public LoginResponder getLoginResponder() {
    if (responder == null) responder = new LoginResponder();
    return responder;
  }
  
  private class LoginResponder implements LRAdminLoginResponder {
    public WOComponent connectAccepted() {
      LRLog.log("Admin: login, IP: "+
                criApp.getRequestIPAddress(currentContext.request())+
                " - OK");
      return criSession().getSavedPageWithName("MainSet");
    }
  }

  public void takeValuesFromRequest(WORequest request, WOContext context) {
    currentContext = context;
    super.takeValuesFromRequest(request, context);
  }
}
