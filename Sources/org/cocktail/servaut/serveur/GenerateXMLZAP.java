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

import java.io.IOException;
import java.io.StringWriter;
import java.util.Hashtable;

import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOGenericRecord;
import com.webobjects.eocontrol.EOSortOrdering;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;

import fr.univlr.cri.util.LRXMLWriter;

/**
 * 
 * Classe permettant de generer la description XML pour l'application ZAP.
 */
public class GenerateXMLZAP
{

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
	/**
	 * Generer le xml pour ZAP
	 * @param session
	 * @param app
	 * @return
	 * @throws IOException
	 */
	public static String getXml(Session session,Application app) throws IOException {
		
		//R�cuperation de tous les th�mes
		EOEditingContext context = new EOEditingContext();
		NSMutableArray sort = new NSMutableArray();
		sort.addObject(EOSortOrdering.sortOrderingWithKey("themePosition",EOSortOrdering.CompareAscending));
		NSArray listeTheme = app.dataBus().fetchArray(context,"AppliTheme", null, sort);
		
		StringWriter str = new StringWriter();
		LRXMLWriter w = new LRXMLWriter(str);
		w.startDocument();
		w.writeComment("Liste des applications disponibles � l'universit� de La Rochelle");
		Hashtable dico = new Hashtable();
		dico.put("xmlns:xs","http://www.w3.org/2001/XMLSchema-instance");
		dico.put("xsi:noNamespaceSchemaLocation","AppliULR.xsd");
		dico.put("name","Toutes les applications");
		dico.put("comment","ZAP Application");
		w.startElement("ListeApplication",dico);
		
		//pour chacun des th�mes
		for (int i = 0; i < listeTheme.count(); i++) {
			String thName =(String)((EOGenericRecord)(listeTheme.objectAtIndex(i))).storedValueForKey("themeName");
			String thComment = (String)((EOGenericRecord)(listeTheme.objectAtIndex(i))).storedValueForKey("themeComment");
			NSArray apps =(NSArray) ((EOGenericRecord)(listeTheme.objectAtIndex(i))).valueForKeyPath("repartThemeApplis.application");
			EOSortOrdering positionOrdering = EOSortOrdering.sortOrderingWithKey("appliPosition",EOSortOrdering.CompareAscending);
			NSMutableArray pos = new NSMutableArray();
			pos.addObject(positionOrdering);
			apps= EOSortOrdering.sortedArrayUsingKeyOrderArray(apps,pos.immutableClone());
			
			dico.clear();
			if(thName!=null)
				dico.put("name",thName);
			if(thComment!=null)
			dico.put("comment",thComment);
			w.startElement("ThemeApplication",dico);
			
			//pour chacune des applications
			for(int j=0;j<apps.count();j++)
			{
				String appliName =(String)((EOGenericRecord)apps.objectAtIndex(j)).storedValueForKey("appliLongName");
				String appliType = getTypeFromCode((String)((EOGenericRecord)apps.objectAtIndex(j)).storedValueForKey("appliType"));
				String appliComment = (String)((EOGenericRecord)apps.objectAtIndex(j)).storedValueForKey("appliComment");
				String appliURL = (String)((EOGenericRecord)apps.objectAtIndex(j)).storedValueForKey("appliUrl");
			    String appliIcone = (String)((EOGenericRecord)apps.objectAtIndex(j)).storedValueForKey("appliIcone");
			    String appliShortName = (String)((EOGenericRecord)apps.objectAtIndex(j)).storedValueForKey("appliShortName");
			    String appliAuth = (String)((EOGenericRecord)apps.objectAtIndex(j)).storedValueForKey("appliAuth");
			    dico.clear();
			    if(appliName !=null)
			    	dico.put("name",appliName);
			    if(appliType!=null)
			    	dico.put("type",appliType);
			    if(appliComment!=null)
			    	dico.put("comment",appliComment);
			    if(appliURL!=null)
			    	dico.put("url",appliURL);
				if(appliIcone!=null)
					dico.put("iconUrl",appliIcone);
				if(appliShortName!=null)
					dico.put("shortName",appliShortName);
				if(appliAuth !=null)
					dico.put("authentification",appliAuth);
				w.startElement("Application",dico);
				w.endElement();
			}
			w.endElement();
		}
		w.endElement();
		w.endDocument();
		w.close();
		return str.toString();	
	}
}
