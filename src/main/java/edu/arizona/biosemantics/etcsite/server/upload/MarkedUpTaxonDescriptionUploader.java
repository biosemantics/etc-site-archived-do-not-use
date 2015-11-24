package edu.arizona.biosemantics.etcsite.server.upload;

import edu.arizona.biosemantics.etcsite.server.process.file.ContentValidatorProvider;
import edu.arizona.biosemantics.etcsite.server.process.file.XmlNamespaceManager;

public class MarkedUpTaxonDescriptionUploader extends TaxonDescriptionUploader {

	public MarkedUpTaxonDescriptionUploader(
			XmlNamespaceManager xmlNamespaceManager,
			ContentValidatorProvider contentValidatorProvider) {
		super(xmlNamespaceManager, contentValidatorProvider);
	}


}
