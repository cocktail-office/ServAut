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
import java.util.Properties;

import com.webobjects.appserver.WOApplication;
import com.webobjects.appserver.WOTimer;
import com.webobjects.foundation.NSMutableDictionary;

import fr.univlr.cri.util.CRIpto;
import fr.univlr.cri.util.SAUTClient;
import fr.univlr.cri.util.wo5.DateCtrl;
import fr.univlr.cri.webapp.CRIWebApplication;
import fr.univlr.cri.webapp.LRLog;

/**
 * Cette classe represente l'instance de l'application en cours d'execution.
 * 
 * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
 */
public class Application extends CRIWebApplication {
  /** L'identifiant de parametre de configuration avec le dictionnaire de
   * connexion a la base de donnees */
  public static String DBDicoParamName = "APP_DB_DICO";
  
  //Dictionnaire contenant les associations PGTIOU/PGT pour CAS
  private NSMutableDictionary pgtDico = new NSMutableDictionary(); 

  public static void main(String argv[]) {
    WOApplication.main(argv, Application.class);
  }

  static {
//    LRLog.setLevel(LRLog.LEVEL_DEBUG);
  }
  
  public Application() {
    super();
  }

  public void initApplication() {
    LRLog.log("Demarrage du service d'authentification...");
    LRLog.rawLog("  Date de lancement : "+DateCtrl.currentDateTimeString());
    LRLog.rawLog("  Version : "+version());
    super.initApplication();
  }
  
  public void addPgtForPgtIou(String pgt, String pgtIou) {
	LRLog.log("ajout d'un PGT");
	pgtDico.takeValueForKey(pgt, pgtIou);
	//on programme la suppression du pgt d'apr�s le param�tre CAS_GT_TIMEOUT
	WOTimer deletePgtTimer = new WOTimer((new Integer((String)config().valueForKey("CAS_GT_TIMEOUT"))).longValue()*1000, this, "removePgtForPgtIou", pgtIou, pgtIou.getClass(), false);
	deletePgtTimer.schedule();
  }
  
  public String pgtForPgtIou(String pgtIou) {
	return (String)pgtDico.objectForKey(pgtIou);
  }
  
  public void removePgtForPgtIou(String pgtIou)
  {
	LRLog.log("suppression d'un PGT");
	pgtDico.removeObjectForKey(pgtIou);
  }
  
  public void startRunning() {
    LRLog.log("Le service d'authentification demarre");
  }

  public String configFileName() {
    return "ServAut.config";
  }
  
  public String configTableName() {
    return "GrhumParameters";
  }
  
  public String copyright() {
    return "&copy; 2001-2005, Universit&eacute; de La Rochelle";
  }
  
  public String version() {
    return "2.0.0";
  }

  public String imageLigneSrc() {
    return getImageDefaultURL("ligneApplisDegradee.gif");
  }
  
  public String imageClefsSrc() {
    return getImageDefaultURL("clefs.gif");
  }
  
  /**
   * Retourne le dictionnaire de connexion a la base de donnees pris a partir
   * du fichier de configuration.
   */
  private NSMutableDictionary configConnectionDicoForID(String id) {
    NSMutableDictionary dico = new NSMutableDictionary();
    // Un utilise le dictionnaire dans le fichier de config
    String cfgDico = config().stringForKey(DBDicoParamName+"."+id);
    if (cfgDico == null)
      cfgDico = config().stringForKey(DBDicoParamName);
    if (cfgDico != null) {
      cfgDico = CRIpto.decrypt(cfgDico);
      LRLog.trace("\nDico : "+cfgDico);
      Properties props = SAUTClient.toProperties(cfgDico);
      if ((props.get("username") != null) &&
          (props.get("password") != null) &&
          (props.get("URL") != null))
      {
        LRLog.trace("\nProperties : "+props);
        dico.takeValueForKey(props.get("userName"), "username");
        dico.takeValueForKey(props.get("password"), "password");
        dico.takeValueForKey(props.get("URL"), "URL");
      }
    }
    return dico;
  }
  
  /**
   * Retourne le dictionnaire de connexion a la base de donnees correspondant
   * a l'identifiant <code>id</code>. Cette methode redefinit la methode
   * de <code>CRIWebApplication</code> en prenant le dictionnaire a partir du
   * fichier de configuration.
   */
  public NSMutableDictionary connectionDictionaryForID(String id) {
    LRLog.trace("------- Dico : START ("+id+") ---------------");
    NSMutableDictionary dico = configConnectionDicoForID(id);
    if (dico.count() == 0) {
      LRLog.log("  Connexion BD : configuration inconnue pour le dictionnaire "+id);
    }
    LRLog.trace("Dico ID : "+id);
    LRLog.trace("     definition : "+dico);
    LRLog.trace("------- Dico : END ---------------");
    return dico;
  }
}