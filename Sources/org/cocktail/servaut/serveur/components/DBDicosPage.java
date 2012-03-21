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
import com.webobjects.appserver.WODisplayGroup;
import com.webobjects.eoaccess.EOUtilities;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOEnterpriseObject;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;

import fr.univlr.cri.util.StringCtrl;
import fr.univlr.cri.webapp.CRIWebComponent;
import fr.univlr.cri.webext.CRIAlertPage;
import fr.univlr.cri.webext.CRIAlertResponder;

/**
 * Cette page permet de gerer la liste des dictionnaires de connexion aux
 * bases de donnees. Elle affiche la liste de tous les dictionnaires et
 * propose les actions permettant d'ajouter, de modifier et de supprimer
 * les elements de cette liste.
 * 
 * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
 */
public class DBDicosPage extends CRIWebComponent {
  private EOEnterpriseObject selectedObject;
  public boolean isEditing;
  public String bdId;
  public String bdLibelle;
  public String bdNomUtilisateur;
  public String bdMotDePasse;
  public String bdNomInstance;
  public String bdUrl;
  public WODisplayGroup parametresSautBdDisplayGroup;

  public DBDicosPage(WOContext context) {
    super(context);
    isEditing = false;
    refetch();
    cleanFields();
  }

  private void refetch() {
    criApp.dataBus().fetchTable(parametresSautBdDisplayGroup, null, null, false);
    selectedObject = null;
    cleanFields();
  }

  public WOComponent ajouter() {
    isEditing = true;
    cleanFields();
    selectedObject = null;
    return null;
  }

  private void cleanFields() {
    bdId = "";
    bdLibelle = "";
    bdNomUtilisateur = "";
    bdMotDePasse = "";
    bdNomInstance = "";
    bdUrl = "";
  }

  public WOComponent rafraichir() {
    refetch();
    return null;
  }

  public WOComponent valider() {
    EOEditingContext ec;
    bdId = StringCtrl.normalize(bdId).toUpperCase();
    if (bdId.length() == 0)
      return CRIAlertPage.newAlertPageWithCaller(this, "Enregistrement de donn�es", "Vous devez saisir le code d'identification !", "  OK  ", CRIAlertPage.ATTENTION);
    if (StringCtrl.normalize(bdNomUtilisateur).length() == 0)
      return CRIAlertPage.newAlertPageWithCaller(this, "Enregistrement de donn�es", "Vous devez saisir nom d'utilisateur !", "  OK  ", CRIAlertPage.ATTENTION);
    if (StringCtrl.normalize(bdMotDePasse).length() == 0)
      return CRIAlertPage.newAlertPageWithCaller(this, "Enregistrement de donn�es", "Vous devez saisir le mot de passe !", "  OK  ", CRIAlertPage.ATTENTION);
    if (StringCtrl.normalize(bdNomInstance).length() == 0)
      return CRIAlertPage.newAlertPageWithCaller(this, "Enregistrement de donn�es", "Vous devez saisir le nom d'instance de la base de donn�es !", "  OK  ", CRIAlertPage.ATTENTION);
    if (StringCtrl.normalize(bdUrl).length() == 0)
      return CRIAlertPage.newAlertPageWithCaller(this, "Enregistrement de donn�es", "Vous devez saisir la description de connexion � la base de donn�es !", "  OK  ", CRIAlertPage.ATTENTION);
    if (StringCtrl.normalize(bdLibelle).length() == 0)
      return CRIAlertPage.newAlertPageWithCaller(this, "Enregistrement de donn�es", "Vous devez saisir la description (le libell�) !", "  OK  ", CRIAlertPage.ATTENTION);
    ec = new EOEditingContext();
    if (selectedObject == null) {
      ec = new EOEditingContext();
      selectedObject = EOUtilities.createAndInsertInstance(ec, "BdDico");
    } else {
      ec = selectedObject.editingContext();
    }
    selectedObject.takeStoredValueForKey(bdId, "bdId");
    selectedObject.takeStoredValueForKey(bdNomUtilisateur, "bdUtilisateur");
    selectedObject.takeStoredValueForKey(bdMotDePasse, "bdPasse");
    selectedObject.takeStoredValueForKey(bdNomInstance, "bdInstance");
    selectedObject.takeStoredValueForKey(bdUrl, "bdUrl");
    selectedObject.takeStoredValueForKey(bdLibelle, "bdLibelle");
    try {
      ec.saveChanges();
    } catch(Exception ex) {
      ex.printStackTrace();
      return CRIAlertPage.newAlertPageWithCaller(this, "Enregistrement de donn�es", "L'enregistremment des donn�es a �chou�!<br><b>Erreur :</b></br>"+ex.getMessage(), "  OK  ", CRIAlertPage.ERROR);
    }
    isEditing = false;
    refetch();
    return null;
  }

  public WOComponent annuler() {
    cleanFields();
    isEditing = false;
    return null;
  }


  public WOComponent supprimer() {
    selectedObject = (EOEnterpriseObject)parametresSautBdDisplayGroup.selectedObject();
    if (selectedObject == null) {
      return CRIAlertPage.newAlertPageWithCaller(this, "Suppression de donn�es", "Aucun objet n'a �t� s�lectionn�.<br>Les donn�es ne peuvent pas etre supprim�es.", "  OK  ", CRIAlertPage.ERROR);
    } else {
      String message = "<b>Code d'identification :</b> "+selectedObject.valueForKey("bdId");
      message += "<br><b>Nom d'utilisateur :</b> "+selectedObject.valueForKey("bdUtilisateur");
      message += "<br><b>Nom d'instance :</b> "+selectedObject.valueForKey("bdInstance");
      message += "<br><b>URL de connexion :</b> "+selectedObject.valueForKey("bdUrl");
      message += "<br><b>Description :</b> "+selectedObject.valueForKey("bdLibelle");
      message += "<br><br>Voulez-vous vraiment supprimer ces donn�es ?";
      return CRIAlertPage.newAlertPageWithResponder(this, "Suppresion de donn�es",
                                                 message, "  Oui  ", "  Non  ", null,
                                                 CRIAlertPage.QUESTION, new DeleteResponder());
    }
  }

  public WOComponent modifier() {
    selectedObject = (EOEnterpriseObject)parametresSautBdDisplayGroup.selectedObject();
    if (selectedObject != null) {
      bdId = (String)selectedObject.valueForKey("bdId");
      bdNomUtilisateur = (String)selectedObject.valueForKey("bdUtilisateur");
      bdMotDePasse = (String)selectedObject.valueForKey("bdPasse");
      bdNomInstance = (String)selectedObject.valueForKey("bdInstance");
      bdUrl = (String)selectedObject.valueForKey("bdUrl");
      bdLibelle = (String)selectedObject.valueForKey("bdLibelle");
      isEditing = true;
    } else {
      cleanFields();
    }
    return null;
  }

  private class DeleteResponder implements CRIAlertResponder {
    public WOComponent respondToButton(int response) {
      WOComponent answerPage = DBDicosPage.this;
      if (response == 1)  {
        if (selectedObject != null) {
          EOQualifier condition = EOQualifier.qualifierWithQualifierFormat("bdId = %@", new NSArray(selectedObject.valueForKey("bdId")));
          if (criApp.dataBus().deleteFromTable(null, "ParametresSautBd", condition) == -1) {
            answerPage = CRIAlertPage.newAlertPageWithCaller(DBDicosPage.this, "Suppression de donn�es", "Les donn&eacute;es n'ont pas pu &ecirc;tre supprim&eacute;es.", "  OK  ", CRIAlertPage.ERROR);
          }
        }
      }
      refetch();
      return answerPage;
    }
  }
}