package edu.arizona.biosemantics.etcsite.shared.file.semanticmarkup;

import edu.arizona.biosemantics.etcsite.shared.file.semanticmarkup.XmlModel.Treatment;

public class XmlModelFile {

	private String fileName;
	private Treatment treatment;
	private String error = "";
	
	public XmlModelFile() { }
	
	public XmlModelFile(String fileName, Treatment treatment, String error) {
		super();
		this.fileName = fileName;
		this.treatment = treatment;
		this.error = error;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public Treatment getTreatment() {
		return treatment;
	}
	public void setTreatment(Treatment treatment) {
		this.treatment = treatment;
	}
	public String getError() {
		return error.trim();
	}
	
	public void appendError(String error) {
		this.error += error + "\n";
	}
	
	public void setError(String error) {
		this.error = error;
	}
	
	public boolean hasError() {
		return this.error != null && !this.error.isEmpty();
	}
	
}
