package edu.arizona.sirls.etc.site.shared.rpc.db;

import java.io.Serializable;

public class MatrixGenerationConfiguration implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2388969377041543292L;
	private int id;
	private String input;
	private Glossary glossary;
	private int otoId;
	private String output;
	private Task task;
	private long created;
	
	public MatrixGenerationConfiguration() { }
	
	public MatrixGenerationConfiguration(int id, String input, Glossary glossary, int otoId, String output, Task task, long created) {
		super();
		this.id = id;
		this.input = input;
		this.glossary = glossary;
		this.otoId = otoId;
		this.task = task;
		this.created = created;
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
	public Glossary getGlossary() {
		return glossary;
	}
	public void setGlossary(Glossary glossary) {
		this.glossary = glossary;
	}
	public int getOtoId() {
		return otoId;
	}
	public void setOtoId(int otoId) {
		this.otoId = otoId;
	}

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}
	public long getCreated() {
		return created;
	}

	public void setCreated(long created) {
		this.created = created;
	}
	
	
}
