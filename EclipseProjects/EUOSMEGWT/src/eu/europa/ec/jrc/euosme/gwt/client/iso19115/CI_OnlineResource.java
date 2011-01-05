/***LICENSE START
 * Copyright 2011 EUROPEAN UNION
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * 
 * Date: 03 January 2011
 * Authors: Marzia Grasso, Angelo Quaglia, Massimo Craglia
LICENSE END***/

package eu.europa.ec.jrc.euosme.gwt.client.iso19115;

import com.google.gwt.core.client.GWT;

import eu.europa.ec.jrc.euosme.gwt.client.CheckFunctions;
import eu.europa.ec.jrc.euosme.gwt.client.EUOSMEGWT;
import eu.europa.ec.jrc.euosme.gwt.client.AppModes;
import eu.europa.ec.jrc.euosme.gwt.client.i18n.iso19115Constants;
import eu.europa.ec.jrc.euosme.gwt.client.i18n.iso19115Messages;
import eu.europa.ec.jrc.euosme.gwt.client.widgets.CI;
import eu.europa.ec.jrc.euosme.gwt.client.widgets.CharacterString;
import eu.europa.ec.jrc.euosme.gwt.client.widgets.CodeList;

/**
 * Create CI_OnlineResource model
 * It refers to information about on-line sources from which the dataset, specification, 
 * or community profile name and extended metadata elements can be obtained.
 * 
 * @version 4.0 - October 2010
 * @author 	Marzia Grasso
 */
public class CI_OnlineResource extends CI {

	/** Constants declaration */
	protected iso19115Constants constants = GWT.create(iso19115Constants.class);
	
	/** Messages declaration */
	protected iso19115Messages messages = GWT.create(iso19115Messages.class);
	
	/** linkage control declaration */
	CharacterString linkageObj = new CharacterString(constants.linkage(),"resourceLocator",true,CheckFunctions.URL);
	
	/** protocol control declaration */
	CharacterString protocolObj = new CharacterString(constants.protocol(),"",false, CheckFunctions.normal);
	
	/** applicationProfile control declaration */
	CharacterString applicationProfileObj = new CharacterString(constants.applicationProfile(),"",false,CheckFunctions.normal);
	
	/** name control declaration */
	CharacterString nameObj = new CharacterString(constants.name(),"",false,CheckFunctions.normal);
	
	/** description control declaration */
	CharacterString descriptionObj = new CharacterString(constants.description(),"",false,CheckFunctions.normal);
	
	/** function control declaration */
	CodeList functionObj = new CodeList(constants.function(),"",false,"3","");
	
	/** 
	 * constructor CI_OnlineResource model
	 * 
	 * @param label		{@link String} = the header
     * @param required	{@link Boolean} = if true, it is required
     * @param multiple	{@link Boolean} = if true, it could be added more than ones
     *  
	 * @return	the widget composed by CI_OnlineResource fields
	 */
	public CI_OnlineResource(String label, boolean required, boolean multiple) {
		super(label, required, multiple);		
		fieldsGroup.add(linkageObj);
		fieldsGroup.add(protocolObj);
		fieldsGroup.add(applicationProfileObj);
		fieldsGroup.add(nameObj);
		fieldsGroup.add(descriptionObj);
		fieldsGroup.add(functionObj);
		setInterface(-1);
	}
	
	@Override
	public void myCheck() {
		super.myCheck();
		if (this.getParent().isVisible()) {
			linkageObj.myCheck();
			protocolObj.myCheck();
			applicationProfileObj.myCheck();
			nameObj.myCheck();
			descriptionObj.myCheck();
			functionObj.myCheck();			
		}
	}

	@Override
	public void setFormName(String name) {
		super.setFormName(name);
		linkageObj.setFormName(name + ".linkage[1].url[1]");
		protocolObj.setFormName(name + ".protocol[1].characterstring[1]");
		applicationProfileObj.setFormName(name + ".applicationprofile[1].characterstring[1]");
		nameObj.setFormName(name + ".name[1].characterstring[1]");
		descriptionObj.setFormName(name + ".description[1].characterstring[1]");
		functionObj.setFormName(name + ".function[1].ci_onlinefunctioncode[1]");	
	}
	
	@Override
	public void setInterface(int i) {
		if ((EUOSMEGWT.appMode.equalsIgnoreCase(AppModes.GEOPORTAL.toString()))) {
			protocolObj.setVisible(false);
			applicationProfileObj.setVisible(false);
			nameObj.setVisible(false);
			descriptionObj.setVisible(false);
			functionObj.setVisible(false);			
		}		
	}
}