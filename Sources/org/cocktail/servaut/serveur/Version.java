/*
 * Copyright Cocktail, 2001-2008
 * 
 * This software is governed by the CeCILL license under French law and abiding
 * by the rules of distribution of free software. You can use, modify and/or
 * redistribute the software under the terms of the CeCILL license as circulated
 * by CEA, CNRS and INRIA at the following URL "http://www.cecill.info".
 * 
 * As a counterpart to the access to the source code and rights to copy, modify
 * and redistribute granted by the license, users are provided only with a
 * limited warranty and the software's author, the holder of the economic
 * rights, and the successive licensors have only limited liability.
 * 
 * In this respect, the user's attention is drawn to the risks associated with
 * loading, using, modifying and/or developing or reproducing the software by
 * the user in light of its specific status of free software, that may mean that
 * it is complicated to manipulate, and that also therefore means that it is
 * reserved for developers and experienced professionals having in-depth
 * computer knowledge. Users are therefore encouraged to load and test the
 * software's suitability as regards their requirements in conditions enabling
 * the security of their systems and/or data to be ensured and, more generally,
 * to use and operate it in the same conditions as regards security.
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */

package org.cocktail.servaut.serveur;

import fr.univlr.cri.webapp.VersionCocktail;
import fr.univlr.cri.webapp.VersionCocktailJava;
import fr.univlr.cri.webapp.VersionCocktailOracleServer;
import fr.univlr.cri.webapp.VersionCocktailWebObjects;


/**
 * Gestion du versioning. Modifiez cette classe pour g̩rer les d̩pendances de version. 
 * Modifiez la classe VersionMe pour les changements de version.
 */
public final class Version extends VersionCocktail {
	// Nom de l'appli
	public static final String APPLICATIONFINALNAME = VersionMe.APPLICATIONFINALNAME;
	public static final String APPLICATIONINTERNALNAME=VersionMe.APPLICATIONINTERNALNAME;
	public static final String APPLICATION_STRID = VersionMe.APPLICATION_STRID;
    
	// Version appli
    public static final long SERIALVERSIONUID = VersionMe.SERIALVERSIONUID;
    
    public static final int VERSIONNUMMAJ =     VersionMe.VERSIONNUMMAJ;
    public static final int VERSIONNUMMIN =     VersionMe.VERSIONNUMMIN;
    public static final int VERSIONNUMPATCH =   VersionMe.VERSIONNUMPATCH;
    public static final int VERSIONNUMBUILD =   VersionMe.VERSIONNUMBUILD;
    
    public static final String VERSIONDATE = VersionMe.VERSIONDATE;
    public static final String COMMENT = VersionMe.COMMENT;
    
    
//    /** Version de la base de donnees requise */
//    private static final String BD_VERSION_MIN = null;
//    private static final String BD_VERSION_MAX = null;
    
    /** Version du frmk CRIWebExt3 */
    private static final String CRIWEBEXT3_VERSION_MIN = "3.0";
    private static final String CRIWEBEXT3_VERSION_MAX = null;
    
    /** Version de WebObjects */
    private static final String WO_VERSION_MIN = "5.3.3.0";
    private static final String WO_VERSION_MAX = null;
    
    /** Version du JRE */
    private static final String JRE_VERSION_MIN = "1.5.0.0";
    private static final String JRE_VERSION_MAX = "1.6";
    
    /** Version d'ORACLE */
    private static final String ORACLE_VERSION_MIN = "9.0";
    private static final String ORACLE_VERSION_MAX = null;
    
//    /** Version de JasperReports */
//    private static final String JASPER_VERSION_MIN = "1.2.5";
//    private static final String JASPER_VERSION_MAX = null;
    

	public String name() {
		return APPLICATIONFINALNAME;
	}
	
	public String internalName() {
		return APPLICATIONINTERNALNAME;
	}

	public int versionNumBuild() {
		return VERSIONNUMBUILD;
	}

	public int versionNumMaj() {
		return VERSIONNUMMAJ;
	}

	public int versionNumMin() {
		return VERSIONNUMMIN;
	}

	public int versionNumPatch() {
		return VERSIONNUMPATCH;
	}
	
	public String date() {
		return VERSIONDATE;
	}
	
	public String comment() {
		return COMMENT;
	}
	
	
	/**
	 * Liste des dependances 
	 * @see fr.univlr.cri.webapp.VersionCocktail#dependencies()
	 */
	public VersionCocktailRequirements[] dependencies() {
	    return new VersionCocktailRequirements[]{
	    		new VersionCocktailRequirements(new VersionCocktailWebObjects(), WO_VERSION_MIN, WO_VERSION_MAX, true)
	    		,new VersionCocktailRequirements(new VersionCocktailJava() , JRE_VERSION_MIN, JRE_VERSION_MAX, true)
	    		,new VersionCocktailRequirements(new VersionCocktailOracleServer(), ORACLE_VERSION_MIN, ORACLE_VERSION_MAX, false)
	    		//,new VersionCocktailRequirements(new VersionDatabase(), BD_VERSION_MIN, BD_VERSION_MAX, false)
	    		,new VersionCocktailRequirements(new fr.univlr.cri.webext.Version(), CRIWEBEXT3_VERSION_MIN, CRIWEBEXT3_VERSION_MAX, true)
	    		//,new VersionCocktailRequirements(new VersionCocktailJar("net.sf.jasperreports.engine.JasperReport"), JASPER_VERSION_MIN, JASPER_VERSION_MAX, true)
	      };
	}
	    


}