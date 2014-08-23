package edu.arizona.biosemantics.etcsite.server.process.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import edu.arizona.biosemantics.etcsite.server.Configuration;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileTypeEnum;

public class XmlNamespaceManager {

	private SAXBuilder sax = new SAXBuilder();
	private Namespace bioNamespace = Namespace.getNamespace("bio", Configuration.targetNamespace);
	private Namespace xsiNamespace = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
	private XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());

	private Map<FileTypeEnum, String> fileTypeSchemaMap = new HashMap<FileTypeEnum, String>(); 
	private Map<String, FileTypeEnum> schemaFileTypeMap = new HashMap<String, FileTypeEnum>(); 
	
	public XmlNamespaceManager() {
		fileTypeSchemaMap.put(FileTypeEnum.TAXON_DESCRIPTION, Configuration.taxonDescriptionSchemaFileWeb);
		fileTypeSchemaMap.put(FileTypeEnum.MARKED_UP_TAXON_DESCRIPTION, Configuration.markedUpTaxonDescriptionSchemaFileWeb);
		schemaFileTypeMap.put(Configuration.taxonDescriptionSchemaFileWeb, FileTypeEnum.TAXON_DESCRIPTION);
		schemaFileTypeMap.put(Configuration.markedUpTaxonDescriptionSchemaFileWeb, FileTypeEnum.MARKED_UP_TAXON_DESCRIPTION);
	}
		
	public FileTypeEnum getFileType(File file) throws JDOMException, IOException { 
		String schema = this.getSchema(file);
		return schemaFileTypeMap.get(schema);
	}
	
	public String getSchema(File file) throws JDOMException, IOException {
		Document doc = sax.build(file);
		return getSchema(doc);
	}
	
	private String getSchema(Document doc) {
		Element rootElement = doc.getRootElement();
		return rootElement.getAttributeValue("schemaLocation", xsiNamespace).replace(Configuration.targetNamespace, "").trim();
	}

	public String getSchema(FileTypeEnum fileTypeEnum) {
		return fileTypeSchemaMap.get(fileTypeEnum);
	}
	
	public String getSchema(String fileContent) {
		try (StringReader reader = new StringReader(fileContent)) {
			Document doc = sax.build(reader);
			return getSchema(doc);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void setXmlSchema(File file, FileTypeEnum fileTypeEnum) throws JDOMException, IOException {
		Document doc = sax.build(file);
		setXmlSchema(doc, fileTypeEnum);
		try(FileOutputStream fileOutputStream = new FileOutputStream(file)) {
			xmlOutputter.output(doc, fileOutputStream);
		}
	}

	public void setXmlSchema(Document doc, FileTypeEnum fileTypeEnum) {
		String schemaUrl = getSchema(fileTypeEnum);
		Element rootElement = doc.getRootElement();
		rootElement.setNamespace(bioNamespace);
		rootElement.addNamespaceDeclaration(bioNamespace);
		rootElement.addNamespaceDeclaration(xsiNamespace);
		rootElement.setAttribute("schemaLocation", Configuration.targetNamespace + " " + schemaUrl, xsiNamespace);
	}

	public String setXmlSchema(String content, FileTypeEnum fileTypeEnum) throws JDOMException, IOException {
		try(StringReader reader = new StringReader(content)) {
			Document doc = sax.build(reader);
			setXmlSchema(doc, fileTypeEnum);
			
			try(StringWriter stringWriter = new StringWriter()) {
				xmlOutputter.output(doc, stringWriter);
				stringWriter.flush();
				String result = stringWriter.toString();
				return result;
			}
		}
	}
	
	public void removeXmlSchema(File file) throws JDOMException, IOException {
		Document doc = sax.build(file);
		Element rootElement = doc.getRootElement();
		rootElement.setNamespace(null);
		rootElement.removeNamespaceDeclaration(bioNamespace);
		rootElement.removeNamespaceDeclaration(xsiNamespace);
		rootElement.removeAttribute("schemaLocation", xsiNamespace);
		try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
			xmlOutputter.output(doc, fileOutputStream);
		}
	}
	
}
