package edu.arizona.sirls.etc.site.server;

import java.io.File;

import net.sf.saxon.lib.NamespaceConstant;

public class Configuration extends edu.arizona.sirls.etc.site.shared.rpc.Configuration {

	public static final String fileBase = "C:" + File.separator + "test" + File.separator + "users";
	public static final String zipFileBase = "C:" + File.separator + "test" + File.separator + "zipFiles";
	public static final String taxonDescriptionSchemaFile = "resources" + File.separator + "io" + File.separator + "iplantInputTreatment.xsd";
	public static final String markedUpTaxonDescriptionSchemaFile = "resources" + File.separator + "io" + File.separator + "iplantOutputTreatment.xsd";
	public static final String xPathObjectModel = NamespaceConstant.OBJECT_MODEL_SAXON;

}
