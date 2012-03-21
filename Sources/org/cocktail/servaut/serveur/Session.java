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
import com.webobjects.appserver.WOComponent;

import fr.univlr.cri.webapp.CRIWebSession;

/**
 * Cette classe represente une session de connexion avec l'application.
 * 
 * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
 * @author Marc BOISIS-DELAVAUD <marc-henri.boisis-delavaud at univ-lr.fr>
 */
public class Session extends CRIWebSession {
  private String currentContentName;
  private String currentContentTitle;
  
  public Session() {
    super();
    currentContentName = "DBDicosPage";
    currentContentTitle = "Connexion &agrave; la base de donn&eacute;es";
  }
  
  public WOComponent selectDBDico() {
    currentContentName = "DBDicosPage";
    currentContentTitle = "Connexion &agrave; la base de donn&eacute;es";
    return getSavedPageWithName("MainSet");
  }
  
  public WOComponent selectZAP() {
	currentContentName = "AjoutApp";
    currentContentTitle = "Gestion du contenu de ZAP";
    return getSavedPageWithName("MainSet");
}
  
  public String contentTitle() {
    return currentContentTitle;
  }
  
  public WOComponent contentPage() {
    return getSavedPageWithName(currentContentName);
  }
}