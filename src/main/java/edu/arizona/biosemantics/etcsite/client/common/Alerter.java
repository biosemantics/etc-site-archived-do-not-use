package edu.arizona.biosemantics.etcsite.client.common;

import com.sencha.gxt.widget.core.client.box.AlertMessageBox;

public class Alerter {
	
	public static void failedToCreateTaxonDescription(Throwable caught) {
		AlertMessageBox alert = new AlertMessageBox("Create Taxon Description", "Failed to create taxon description.");
		alert.show();
		//logger.log(LogLevel.ERROR, "Failed to create taxon description", caught);
	}

	public static void failedToCreateFile(Throwable caught) {
		//logger.log(LogLevel.ERROR, "Failed to create file", caught);
	}

	public static void failedToCheckValidityOfTaxonDescription(Throwable caught) {
		//logger.log(LogLevel.ERROR, "Failed to check validity of taxon description.", caught);
	}

	public static void failedToSetContent(Throwable caught) {
		//logger.log(LogLevel.ERROR, "Failed to write file content", caught);
	}

}
