package edu.arizona.biosemantics.etcsite.server.process.file;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.server.Configuration;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileTypeEnum;

public class ContentValidatorProvider {

	private Map<FileTypeEnum, IContentValidator> fileTypeValidatorMap = new HashMap<FileTypeEnum, IContentValidator>();
	
	@Inject
	public ContentValidatorProvider() {
		fileTypeValidatorMap.put(FileTypeEnum.TAXON_DESCRIPTION, new XMLValidator(new File(Configuration.taxonDescriptionSchemaFile)));
		fileTypeValidatorMap.put(FileTypeEnum.MARKED_UP_TAXON_DESCRIPTION, new XMLValidator(new File(Configuration.markedUpTaxonDescriptionSchemaFile)));
		fileTypeValidatorMap.put(FileTypeEnum.PLAIN_TEXT, null);
	}
	
	public IContentValidator getValidator(FileTypeEnum fileTypeEnum) {
		return fileTypeValidatorMap.get(fileTypeEnum);
	}

}
