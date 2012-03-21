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
import org.cocktail.servaut.serveur.Session;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;

import fr.univlr.cri.webapp.CRIWebPage;
import fr.univlr.cri.webext.CRIAlertPage;
import fr.univlr.cri.webext.CRIWebMenu;
import fr.univlr.cri.webext.CRIWebMenuItemSet;
import fr.univlr.cri.webext.CRIWebMenuListener;

/**
 * Cette classe represente le menu de l'application.
 * 
 * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
 * @author Marc BOISIS-DELAVAUD <marc-henri.boisis-delavaud at univ-lr.fr>
 */
public class Menu
  extends CRIWebPage
  implements CRIWebMenuListener
{
  private CRIWebMenuItemSet menuItemSet;
//  private int prevMenuId;
  
  public Menu(WOContext context) {
    super(context);
  }
  
  public CRIWebMenuItemSet menuItemSet() {
    if (menuItemSet == null) {
      createMenu();
    } 
    return menuItemSet;
  }
  
  private void createMenu() {
    menuItemSet = new CRIWebMenuItemSet();
    // Initialisation de menu
    menuItemSet.addMenuItem(1, "Connexions BD", "Dictionnaires de connexion &agrave; la base de donn&eacute;es", "_top");
    menuItemSet.addMenuItem(2,"ZAP","Applications pr&eacute;sentes dans ZAP","_top");
  }

  public CRIWebMenuListener listener() {
    return this;
  }
  
  // Methodes de listener
  public void initMenu(CRIWebMenu newMenu) {
      newMenu.selectItemWithId(1);
  }

  public WOComponent selectMenu(CRIWebMenu aMenu, int id) {
    WOComponent resultPage;
    switch(id) {
      case 1 :
        resultPage = ((Session)criSession()).selectDBDico();
        break;
      case 2 :
      	resultPage = ((Session)criSession()).selectZAP();
      	break;
      default :
        return CRIAlertPage.newAlertPageWithCaller(criSession().getSavedPageWithName("MainSet"), "Op&eacute;ration indisponible"+id, "Cette op&eacute;ration est indisponible", "OK", CRIAlertPage.ATTENTION);
     }
//    prevMenuId = id;
    return resultPage;
  }
}