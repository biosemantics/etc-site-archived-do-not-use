package edu.arizona.biosemantics.etcsite.filemanager.server.upload;

import edu.arizona.biosemantics.etcsite.filemanager.server.process.XmlNamespaceManager;
import edu.arizona.biosemantics.etcsite.filemanager.server.process.validate.ContentValidatorProvider;

public class MarkedUpTaxonDescriptionUploader extends TaxonDescriptionUploader {

	public MarkedUpTaxonDescriptionUploader(
			XmlNamespaceManager xmlNamespaceManager,
			ContentValidatorProvider contentValidatorProvider) {
		super(xmlNamespaceManager, contentValidatorProvider);
	}

}
