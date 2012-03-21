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
import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WODirectAction;
import com.webobjects.appserver.WORequest;

import fr.univlr.cri.util.CryptoCtrl;
import fr.univlr.cri.util.StringCtrl;

/**
 * Definit la directe action premettant acceder a la page de definition de
 * mot de passe d'acces a une application. Cette page permette de saisir
 * le mot de passe et ensuite le crypter. La valeur obtenue peut etre ajoutee
 * dans le fichier de configuration.
 * 
 * <p>Cette action ne cree pas de sessions.</p>
 * 
 * @see DAPasswdPage
 * 
 * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
 */
public class DAPasswd extends WODirectAction {

  public DAPasswd(WORequest request) {
    super(request);
  }

  public WOActionResults defaultAction() {
    return pageWithName("DAPasswdPage");
  }
  
  public WOActionResults validerAction() {
    DAPasswdPage page = (DAPasswdPage)pageWithName("DAPasswdPage");
    String val = StringCtrl.normalize((String)request().formValueForKey("passwd"));
    // page.setPasswd(StringCtrl.emptyString());
    page.setPasswd(val);
    if (val.length() > 0) {
        // val = CRIpto.passCrypt(val);
        try {
			val = CryptoCtrl.cryptPass(CryptoCtrl.JAVA_METHOD_CRYPT_UNIX, val);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    page.setCryptedPasswd(val);
    return page;
  }
}
