package edu.arizona.sirls.etc.site.shared.rpc.db;

public class MatrixGenerationConfiguration {

	private int id;
	private String input;
	private String glossary;
	private int otoId;
	
	public MatrixGenerationConfiguration() { }
	
	public MatrixGenerationConfiguration(int id, String input, String glossary, int otoId) {
		super();
		this.id = id;
		this.input = input;
		this.glossary = glossary;
		this.otoId = otoId;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getInput() {
		return input;
	}
	public void setInput(String input) {
		this.input = input;
	}
	public String getGlossary() {
		return glossary;
	}
	public void setGlossary(String glossary) {
		this.glossary = glossary;
	}
	public int getOtoId() {
		return otoId;
	}
	public void setOtoId(int otoId) {
		this.otoId = otoId;
	}
	
}
