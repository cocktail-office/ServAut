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
import com.webobjects.eocontrol.EOGenericRecord;
import com.webobjects.eocontrol.EOSortOrdering;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;

import fr.univlr.cri.util.StringCtrl;
import fr.univlr.cri.webapp.CRIDataBus;
import fr.univlr.cri.webapp.CRIWebComponent;
import fr.univlr.cri.webapp.LRLog;
import fr.univlr.cri.webapp.LRRecord;

/**
 * Cette classe represente la page de la gestion de contenu pour ZAP.
 * Elle permet de definir les themes des applications et y enregistrer
 * les applications.
 * 
 * @author Marc BOISIS-DELAVAUD <marc-henri.boisis-delavaud at univ-lr.fr>
 */
public class AjoutApp extends CRIWebComponent {
	public boolean isEditing;
    public WODisplayGroup appliThemeDisplayGroup;
    public WODisplayGroup appliDisplayGroup;
    public EOGenericRecord aTheme;
    public EOGenericRecord anAppli;
    public EOGenericRecord anAssociatedTheme;
    
    private   NSArray themes;
    public NSArray thSelected;
    public String thSelectedName;
    public String thSelectedComment;
    public NSArray thAssociated;
    
    public NSArray apps;
    public NSArray appSelected;
    public String appSelectedLgName;
    public String appSelectedShName;
    public String appSelectedComment;
    public String appSelectedType;
    public String appSelectedURL;
    public String appSelectedIcone;
    public String appSelectedAuth;
    
    public boolean thFormIncomplete=false;
    public boolean appFormIncomplete=false;
    private EOEditingContext context;
    
    public String type;
    public String auth;
    
    public AjoutApp(WOContext context) {
        super(context);
        this.context = new EOEditingContext();
    }

    /**
     * Soumettre le formulaire de theme
     * @return
     */
    public WOComponent doThFormChangeSubmit() {
        //LRLog.trace(null);
        remplirTheme();
        this.apps=
        	(NSArray) ((EOGenericRecord)(thSelected.objectAtIndex(0))).valueForKeyPath("repartThemeApplis.application");
        if(this.apps==null)
        	this.apps= new NSArray();
        this.resetAppField();
		this.thAssociated = this.thSelected;
        return null;
      }
    
    /**
     * Soumettre le formulaire application
     * @return
     */
    public WOComponent doAppFormChangeSubmit() {
        remplirAppli();      
        return null;
      }
    
    /**
     * Remplir les champs du formulaire theme
     *
     */
    public void remplirTheme()
    {
    	//Si un theme est selectionn� dans le browser
    	if(thSelected != null && thSelected.count()>0)
    	{
    		thSelectedName =
    			(String)((EOGenericRecord)thSelected.objectAtIndex(0)).valueForKey("themeName");
    		thSelectedComment =
    			(String)((EOGenericRecord)thSelected.objectAtIndex(0)).valueForKey("themeComment");
    	}
    	else
    		resetThField();
    }

    /**
     * Remplir les champs du formulaire application
     *
     */
    public void remplirAppli()
    {
    	if(appSelected != null && appSelected.count()>0)
    	{
    		appSelectedLgName  =
    			(String)((EOGenericRecord)appSelected.objectAtIndex(0)).valueForKey("appliLongName");
    		appSelectedShName =
    			(String)((EOGenericRecord)appSelected.objectAtIndex(0)).valueForKey("appliShortName");
    		appSelectedComment =
    			(String)((EOGenericRecord)appSelected.objectAtIndex(0)).valueForKey("appliComment");
    		appSelectedURL =
    			(String)((EOGenericRecord)appSelected.objectAtIndex(0)).valueForKey("appliUrl");
    		appSelectedType =getTypeFromCode(
    			(String)((EOGenericRecord)appSelected.objectAtIndex(0)).valueForKey("appliType"));
    		appSelectedIcone =
    			(String)((EOGenericRecord)appSelected.objectAtIndex(0)).valueForKey("appliIcone");
    		appSelectedAuth = 
    			(String)((EOGenericRecord)appSelected.objectAtIndex(0)).valueForKey("appliAuth");
    		this.thAssociated = 
    			(NSArray) ((EOGenericRecord)(this.appSelected.objectAtIndex(0))).valueForKeyPath("repartThemeApplis.appliTheme");
    	}
    }
    
    /**
     * Remise � zero du formulaire theme
     * @return
     */
    public WOComponent resetThField()
    {
    	this.themes = this.getThemes();
    	this.thSelectedName = StringCtrl.emptyString();
    	this.thSelectedComment = StringCtrl.emptyString();
    	thSelected = null;
    	return null;
    }
    
    /**
     * Remise � zero des champs du formulaire application
     * @return
     */
    public WOComponent resetAppField()
    {
    	if(thSelected != null)
		{
			((EOGenericRecord)thSelected.objectAtIndex(0))
				.editingContext().invalidateObjectsWithGlobalIDs(
					new NSArray(((EOGenericRecord)thSelected.objectAtIndex(0))
							.editingContext().globalIDForObject((EOEnterpriseObject)thSelected.objectAtIndex(0))));
    	this.apps= 
        	(NSArray) ((EOGenericRecord)(thSelected.objectAtIndex(0))).valueForKeyPath("repartThemeApplis.application");
    	
    	if(this.apps==null)
        	this.apps= new NSArray();
    	else
    		this.apps = this.orderAppli(this.apps);
		}	
			
    	this.appSelectedComment=StringCtrl.emptyString();
    	this.appSelectedIcone=StringCtrl.emptyString();
    	this.appSelectedLgName=StringCtrl.emptyString();
    	this.appSelectedShName=StringCtrl.emptyString();
    	this.appSelectedURL=StringCtrl.emptyString();
    	this.appSelectedAuth=null;
    	this.appSelectedType=null;
    	this.appSelected = null;
    	this.thAssociated = null;
    	return null;
    }
    
    /**
     * Trier les applications selon leur position
     * @param tab
     * @return
     */
    private NSArray orderAppli(NSArray tab)
    {
		LRLog.trace("tri des application par postion");
    	NSMutableArray array = new NSMutableArray(tab);
		EOSortOrdering positionOrdering = EOSortOrdering.sortOrderingWithKey("appliPosition",
				EOSortOrdering.CompareAscending);
    	NSMutableArray sortOrderings = new NSMutableArray();
    	sortOrderings.addObject(positionOrdering);
    	EOSortOrdering.sortArrayUsingKeyOrderArray(array,sortOrderings);
    	return array.immutableClone();
    }
    
    
    /**
     * Ajouter un th�me
     * @return
     */
    public WOComponent doThAjouter() {
    	//formulaire incomplet
    	if(thSelectedName== null || thSelectedName.equalsIgnoreCase(StringCtrl.emptyString()))
    	{
    		this.thFormIncomplete = true;
    		return null;
    	}
    	
    	EOEnterpriseObject recAppliTh = EOUtilities.createAndInsertInstance(context, "AppliTheme");
    	context.lock();
    	LRRecord recAppId =
    		(LRRecord)criApp.dataBus().fetchObject(context, "AppliThemeID", null);
    	if (recAppId != null)
    	  recAppliTh.takeStoredValueForKey(new Integer(recAppId.intForKey("maxId")+1), "themeId");
    	else
    	{
    		recAppliTh.takeStoredValueForKey(new Integer(1), "themeId");
    	}
    	LRRecord recThPos = 
    		(LRRecord)criApp.dataBus().fetchObject(context, "themePosition", null);
    	if (recThPos != null)
        	  recAppliTh.takeStoredValueForKey(new Integer(recThPos.intForKey("maxPosition")+1), "themePosition");
        	else
        		recAppliTh.takeStoredValueForKey(new Integer(1), "themePosition");
    	
    	recAppliTh.takeStoredValueForKey(thSelectedName, "themeName");
    	recAppliTh.takeStoredValueForKey(thSelectedComment, "themeComment");
    	context.saveChanges();
    	context.unlock();
    	
    	resetThField();
    	appliThemeDisplayGroup.fetch();
    	return null;
    }
    
    /**
     * Modifier un theme existant
     * @return
     */
    public WOComponent doThModifier() {
    	
    	if(thSelectedName== null || thSelectedName.equalsIgnoreCase(""))
    	{
    		this.thFormIncomplete = true;
    		return null;
    	}
    	
    	EOEnterpriseObject recAppliTh = (EOEnterpriseObject)thSelected.lastObject();
    	EOEditingContext ec = recAppliTh.editingContext();
    	ec.lock();
    	recAppliTh.takeStoredValueForKey(thSelectedName, "themeName");
    	recAppliTh.takeStoredValueForKey(thSelectedComment, "themeComment");
    	ec.saveChanges();
    	ec.unlock();
    	
    	resetThField();
    	return null;
    }

    /**
     * Supprimer un th�me
     * @return
     */
    public WOComponent doThSupprimer() {
    	EOEnterpriseObject recAppliTh = (EOEnterpriseObject)thSelected.lastObject();
    	EOEditingContext ec = recAppliTh.editingContext();
    	ec.lock();
    	ec.deleteObject(recAppliTh);
    	ec.saveChanges();
    	ec.unlock();
    	
    	resetThField();
    	
    	return null;
    }
    
    
    /**
     * Ajouter une nouvelle application
     * @return
     */
    public WOComponent doAppAjouter() {
    	
    	if(appSelectedLgName== null || appSelectedLgName.equalsIgnoreCase(StringCtrl.emptyString())
    			|| appSelectedShName == null || appSelectedShName.equalsIgnoreCase(StringCtrl.emptyString())
				|| appSelectedType == null || appSelectedType.equalsIgnoreCase(StringCtrl.emptyString())
				|| appSelectedAuth == null || appSelectedAuth.equalsIgnoreCase(StringCtrl.emptyString())
				|| appSelectedURL == null || appSelectedURL.equalsIgnoreCase(StringCtrl.emptyString())
				|| thAssociated.count()<1)
				
    	{
    		this.appFormIncomplete = true;
    		return null;
    	}
    	
    	EOEditingContext ec = new EOEditingContext();
    	ec.lock();
    	EOEnterpriseObject recAppli = EOUtilities.createAndInsertInstance(ec, "Application");
    	
    	LRRecord recAppId =
    		(LRRecord)criApp.dataBus().fetchObject(ec, "AppliID", null);
    	
    	if (recAppId != null)
    	  recAppli.takeStoredValueForKey(new Integer(recAppId.intForKey("maxId")+1), "appliId");
    	else
    		recAppli.takeStoredValueForKey(new Integer(1), "appliId");
    	
		LRRecord recAppPos =
			(LRRecord)criApp.dataBus().fetchObject(context, "AppliPosition", null);
    	if (recAppPos != null)
      	  recAppli.takeStoredValueForKey(new Integer(recAppPos.intForKey("maxPosition")+1), "appliPosition");
      	else
      		recAppli.takeStoredValueForKey(new Integer(1), "appliPosition");
    	
    	recAppli.takeStoredValueForKey(appSelectedLgName, "appliLongName");
    	recAppli.takeStoredValueForKey(appSelectedShName, "appliShortName");
    	recAppli.takeStoredValueForKey(getCodeFromType(appSelectedType), "appliType");
    	recAppli.takeStoredValueForKey(appSelectedComment, "appliComment");
    	recAppli.takeStoredValueForKey(appSelectedAuth, "appliAuth");
    	recAppli.takeStoredValueForKey(appSelectedURL, "appliUrl");
    	recAppli.takeStoredValueForKey(appSelectedIcone, "appliIcone");
    	
    	StringBuffer condition = new StringBuffer();
    	for(int i=0;i<thAssociated.count();i++) {
    		if (condition.length() > 0) condition.append(" or ");
    		condition.append("themeId=");
			condition.append(((EOGenericRecord)(thAssociated.objectAtIndex(i))).valueForKey("themeId").toString());
    	}
    	NSArray themes = criApp.dataBus().fetchArray(ec, "AppliTheme", CRIDataBus.newCondition(condition.toString()),null);
    	for(int i=0;i<themes.count();i++)
    		recAppli.addObjectToBothSidesOfRelationshipWithKey((EOGenericRecord)themes.objectAtIndex(i),"appliTheme");
    	ec.saveChanges();
    	ec.unlock();
    	
    	this.resetAppField();
    	return null;
    }
    
    
    
    /**
     * Modifier une application
     * @return
     */
    public WOComponent doAppModifier() {
    	if(appSelectedLgName== null || appSelectedLgName.equalsIgnoreCase(StringCtrl.emptyString())
    			|| appSelectedShName == null || appSelectedShName.equalsIgnoreCase(StringCtrl.emptyString())
				|| appSelectedType == null || appSelectedType.equalsIgnoreCase(StringCtrl.emptyString())
				|| appSelectedAuth == null || appSelectedAuth.equalsIgnoreCase(StringCtrl.emptyString())
				|| appSelectedURL == null || appSelectedURL.equalsIgnoreCase(StringCtrl.emptyString())
				|| thAssociated.count()<1)
				
    	{
    		this.appFormIncomplete = true;
    		return null;
    	}    	
    	
    	EOEnterpriseObject recAppli = (EOEnterpriseObject)appSelected.lastObject();
    	EOEditingContext ec = recAppli.editingContext();
    	ec.lock();

    	recAppli.takeStoredValueForKey(appSelectedLgName, "appliLongName");
    	recAppli.takeStoredValueForKey(appSelectedShName, "appliShortName");
    	recAppli.takeStoredValueForKey(getCodeFromType(appSelectedType), "appliType");
    	recAppli.takeStoredValueForKey(appSelectedComment, "appliComment");
    	recAppli.takeStoredValueForKey(appSelectedAuth, "appliAuth");
    	recAppli.takeStoredValueForKey(appSelectedURL, "appliUrl");
    	recAppli.takeStoredValueForKey(appSelectedIcone, "appliIcone");
    	
    	//condition permettant de connaitre les theme � supprimer de repartTheme
    	StringBuffer condition = new StringBuffer();
    	for(int i=0;i<thAssociated.count();i++) {
    		if (condition.length() > 0) condition.append(" or ");
    		condition.append("themeId!=");
			condition.append(((EOGenericRecord)(thAssociated.objectAtIndex(i))).valueForKey("themeId").toString());
    	}
    	NSArray themesToDelete = criApp.dataBus().fetchArray(ec, "AppliTheme", CRIDataBus.newCondition(condition.toString()),null);
    	for(int i=0;i<themesToDelete.count();i++)
    		recAppli.removeObjectFromBothSidesOfRelationshipWithKey((EOGenericRecord)themesToDelete.objectAtIndex(i),"appliTheme");
    	
    	condition = new StringBuffer();
    	for(int i=0;i<thAssociated.count();i++) {
    		if (condition.length() > 0) condition.append(" or ");
    		condition.append("themeId=");
			condition.append(((EOGenericRecord)(thAssociated.objectAtIndex(i))).valueForKey("themeId").toString());
    	}
    	NSArray themesToAdd = criApp.dataBus().fetchArray(ec, "AppliTheme", CRIDataBus.newCondition(condition.toString()),null);
    	for(int i=0;i<themesToAdd.count();i++)
    		recAppli.addObjectToBothSidesOfRelationshipWithKey((EOGenericRecord)themesToAdd.objectAtIndex(i),"appliTheme");
    	
    	ec.saveChanges();
    	ec.unlock();
    	
    	this.resetAppField();
    	return null;
    }

    /**
     * Supprimer une application
     * @return
     */
    public WOComponent doAppSupprimer() {
    	EOEnterpriseObject recAppli = (EOEnterpriseObject)appSelected.lastObject();
    	EOEditingContext ec = recAppli.editingContext();
    	ec.lock();
    	ec.deleteObject(recAppli);
    	
    	ec.saveChanges();
    	ec.unlock();
    	this.resetAppField();
    	return null;
    }
    
    
    public static String getTypeFromCode(String code)
    {
    	if(code.equalsIgnoreCase("WS"))
    		return "WebStart";
    	if(code.equalsIgnoreCase("WE"))
    		return "Web";
    	if(code.equalsIgnoreCase("EW"))
    		return "ExeWindows";
    	if(code.equalsIgnoreCase("EU"))
    		return "ExeUnix";
    	return null;
    }
    
    public static String getCodeFromType(String type)
    {
    	if(type.equalsIgnoreCase("WebStart"))
    		return "WS";
    	if(type.equalsIgnoreCase("Web"))
    		return "WE";
    	if(type.equalsIgnoreCase("ExeWindows"))
    		return "EW";
    	if(type.equalsIgnoreCase("ExeUnix"))
    		return "EU";
    	return null;
    }
    
    /**
     * Obtenir tous les types d'applications possibles
     * @return
     */
    public NSArray getTypes()
    {
    	NSMutableArray types = new NSMutableArray();
    	types.addObject("WebStart");
    	types.addObject("Web");
    	types.addObject("ExeUnix");
    	types.addObject("ExeWindows");
    	return types;
    }
    
    /**
     * Obtenir tous les types d'authentification possibles
     * @return
     */
    public NSArray getAuths()
    {
    	NSMutableArray auths = new NSMutableArray();
    	auths.addObject("login");
    	auths.addObject("login+certificat");
    	auths.addObject("none");
    	return auths;
    }
    
    /**
     * Effectuer une action sur le formulaire theme
     * @return
     */
    public WOComponent doThValider() {
    	
    	if(thSelected == null || thSelected.count()<1)
    		return this.doThAjouter();
    	else
    		return this.doThModifier();
    }

    /**
     * Effectuer une action sur le formulaire application
     * @return
     */
    public WOComponent doAppValider() {
    	
    	if(appSelected == null || appSelected.count()<1)
    		return this.doAppAjouter();
    	else
    		return this.doAppModifier();
    }
	
	
    public WOComponent createSeparateur() {
    	EOEditingContext ec = new EOEditingContext();
    	ec.lock();
    	EOEnterpriseObject recAppli = EOUtilities.createAndInsertInstance(ec, "Application");
    	
    	LRRecord recAppId =
    		(LRRecord)criApp.dataBus().fetchObject(ec, "AppliID", null);
    	if (recAppId != null)
      	  recAppli.takeStoredValueForKey(new Integer(recAppId.intForKey("maxId")+1), "appliId");
      	else
      		recAppli.takeStoredValueForKey(new Integer(1), "appliId");
      	
      	recAppli.takeStoredValueForKey("separateur", "appliLongName");
      	recAppli.takeStoredValueForKey("separateur", "appliShortName");
      	recAppli.takeStoredValueForKey(getCodeFromType("Web"), "appliType");
      	recAppli.takeStoredValueForKey("login", "appliAuth");
      	recAppli.takeStoredValueForKey("separateur", "appliUrl");
      	
      	StringBuffer condition = new StringBuffer();
    	for(int i=0;i<thAssociated.count();i++) {
    		if (condition.length() > 0) condition.append(" or ");
    		condition.append("themeId=");
			condition.append(((EOGenericRecord)(thAssociated.objectAtIndex(i))).valueForKey("themeId").toString());
    	}
    	NSArray themes = criApp.dataBus().fetchArray(ec, "AppliTheme", CRIDataBus.newCondition(condition.toString()),null);
    	for(int i=0;i<themes.count();i++)
    		recAppli.addObjectToBothSidesOfRelationshipWithKey((EOGenericRecord)themes.objectAtIndex(i),"appliTheme");
    	ec.saveChanges();
      	ec.unlock();
      	
      	this.resetAppField();
        return null;
    }

    /**
     * Descendre une application dans la liste, les application sont class� dans l'odre croissant.
	 Il faut donc incr�menter la valeur position
     * @return
     */
    public WOComponent increaseAppPosition() {
    	
    	if(this.appSelected.count()==0)
			return null;
		EOEditingContext ec = new EOEditingContext();
    	
		//position de l'application s�lectionn�e
		int position = ((Integer)(((EOGenericRecord)(this.appSelected.objectAtIndex(0))).valueForKey("appliPosition"))).intValue();
    	LRLog.trace("position:"+position);
		
    	//On selectionne l'application du dessous
    	String condition = new String("appliPosition = "+(position+1));
    	NSArray apps = criApp.dataBus().fetchArray(ec, "Application", CRIDataBus.newCondition(condition),null);
    	EOEnterpriseObject recAppli = (EOEnterpriseObject)apps.lastObject(); 
		
		//si l'application n'est pas d�ja en bas liste
		if(apps.count() != 0)
		{
			ec = recAppli.editingContext();
			ec.lock();
			//L'application du dessous prend sa place
			recAppli.takeStoredValueForKey(new Integer(position), "appliPosition");
			ec.saveChanges();
			ec.unlock();
			
			recAppli = (EOEnterpriseObject)appSelected.lastObject();
			ec = recAppli.editingContext();
			ec.lock();
			
			//incrementation de la position de l'application s�lectionn�e
			recAppli.takeStoredValueForKey(new Integer(position+1), "appliPosition");
			
			ec.saveChanges();
			ec.unlock();
			
			this.resetAppField();
		}
    	return null;
    }

    /**
     * Monter une appli dans la liste
     * @return
     */
    public WOComponent decreaseAppPosition() {
        
    	if(this.appSelected.count()==0)
			return null;
			
		EOEditingContext ec = new EOEditingContext();
    	Integer pos = (Integer)(((EOGenericRecord)(this.appSelected.objectAtIndex(0))).valueForKey("appliPosition"));
    	//position de l'application selectionn�e
		int position = pos.intValue();
    	
    	if(position<=1)
    		return null;
    	
    	// selection de l'application du dessus
    	String condition = new String("appliPosition = "+(position-1));
    	NSArray apps = criApp.dataBus().fetchArray(ec, "Application", CRIDataBus.newCondition(condition),null);
    	if(apps.count()==0)
    		return null;
		
			//l'application du dessus prend sa place	
    	EOEnterpriseObject recAppli = (EOEnterpriseObject)apps.objectAtIndex(0); 
    	ec = recAppli.editingContext();
    	ec.lock();
    	recAppli.takeStoredValueForKey(new Integer(position), "appliPosition");
    	ec.saveChanges();
    	ec.unlock();
    	
    	recAppli = (EOEnterpriseObject)appSelected.lastObject();
    	ec = recAppli.editingContext();
    	ec.lock();
    	
    	//decrementation de la position de l'application s�lectionn�e
    	recAppli.takeStoredValueForKey(new Integer(position-1), "appliPosition");
    	
    	ec.saveChanges();
    	ec.unlock();
    	
    	this.resetAppField();
    	return null;
    }

    /**
     * Monter un theme dans la liste
     * @return
     */
    public WOComponent decreaseThPosition() {
        
		if(this.thSelected.count()==0)
			return null;
			
    	EOEditingContext ec = new EOEditingContext();
    	Integer pos = (Integer)(((EOGenericRecord)(this.thSelected.objectAtIndex(0))).valueForKey("themePosition"));
    	int position = pos.intValue();
    	
    	if(position<=1)
    		return null;
    	
    	//    	inccrementation de la postion de l'application du dessus
    	String condition = new String("themePosition = "+(position-1));
    	NSArray LowerTheme = criApp.dataBus().fetchArray(ec, "AppliTheme", CRIDataBus.newCondition(condition),null);
    	if(LowerTheme.count()==0)
    		return null;
    	EOEnterpriseObject recTheme = (EOEnterpriseObject)LowerTheme.objectAtIndex(0); 
    	ec = recTheme.editingContext();
    	ec.lock();
    	recTheme.takeStoredValueForKey(new Integer(position), "themePosition");
    	ec.saveChanges();
    	ec.unlock();
    	
    	recTheme = (EOEnterpriseObject)thSelected.objectAtIndex(0);
    	ec = recTheme.editingContext();
    	ec.lock();
    	
    	//decrementation de la position de l'application s�lectionn�e
    	recTheme.takeStoredValueForKey(new Integer(position-1), "themePosition");
    	
    	ec.saveChanges();
    	ec.unlock();
    	
    	this.resetThField();
    	return null;
    }

    /**
     * Descendre une application dans la liste
     * @return
     */
    public WOComponent increaseThPosition() {
		if(this.thSelected.count()==0)
			return null;
	
    	EOEditingContext ec = new EOEditingContext();
    	int position = ((Integer)(((EOGenericRecord)(this.thSelected.objectAtIndex(0))).valueForKey("themePosition"))).intValue();
    	
    	//    	decrementation de la postion de l'application du dessus
    	String condition = new String("themePosition = "+(position+1));
    	NSArray UpperTheme = criApp.dataBus().fetchArray(ec, "AppliTheme", CRIDataBus.newCondition(condition),null);
    	if(UpperTheme.count()==0)
    		return null;
    		
    	EOEnterpriseObject recTheme = (EOEnterpriseObject)UpperTheme.lastObject(); 
    	ec = recTheme.editingContext();
    	ec.lock();
    	recTheme.takeStoredValueForKey(new Integer(position), "themePosition");
    	ec.saveChanges();
    	ec.unlock();
    	
    	recTheme = (EOEnterpriseObject)thSelected.lastObject();
    	ec = recTheme.editingContext();
    	ec.lock();
    	
    	//incrementation de la position de l'application s�lectionn�e
    	recTheme.takeStoredValueForKey(new Integer(position+1), "themePosition");
    	
    	ec.saveChanges();
    	ec.unlock();
    	
    	this.resetThField();
    	return null;
    }
    
    public NSArray createTheme()
    {
    	NSMutableArray sort = new NSMutableArray();
    	sort.addObject(EOSortOrdering.sortOrderingWithKey("themePosition", EOSortOrdering.CompareAscending));
    	return criApp.dataBus().fetchArray(context,"AppliTheme",null,sort);
    }

	/**
	 * @return Returns the themes.
	 */
	public NSArray getThemes() {
		return createTheme();
	}
}