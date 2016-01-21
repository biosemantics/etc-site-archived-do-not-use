package edu.arizona.biosemantics.etcsite.filemanager.server.upload;

import edu.arizona.biosemantics.etcsite.filemanager.server.rpc.ContentValidatorProvider;
import edu.arizona.biosemantics.etcsite.filemanager.server.rpc.XmlNamespaceManager;

public class MarkedUpTaxonDescriptionUploader extends TaxonDescriptionUploader {

	public MarkedUpTaxonDescriptionUploader(
			XmlNamespaceManager xmlNamespaceManager,
			ContentValidatorProvider contentValidatorProvider) {
		super(xmlNamespaceManager, contentValidatorProvider);
	}

}
