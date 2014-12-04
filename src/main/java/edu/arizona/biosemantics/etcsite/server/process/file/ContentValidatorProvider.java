package edu.arizona.biosemantics.etcsite.server.process.file;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import edu.arizona.biosemantics.etcsite.server.Configuration;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileTypeEnum;

public class ContentValidatorProvider {

	private Map<FileTypeEnum, IContentValidator> fileTypeValidatorMap = new HashMap<FileTypeEnum, IContentValidator>();
	
	public ContentValidatorProvider() {
		fileTypeValidatorMap.put(FileTypeEnum.TAXON_DESCRIPTION, new XMLValidator(new File(Configuration.taxonDescriptionSchemaFile)));
		fileTypeValidatorMap.put(FileTypeEnum.MARKED_UP_TAXON_DESCRIPTION, new XMLValidator(new File(Configuration.markedUpTaxonDescriptionSchemaFile)));

	}
	
	public IContentValidator getValidator(FileTypeEnum fileTypeEnum) {
		return fileTypeValidatorMap.get(fileTypeEnum);
	}

}
