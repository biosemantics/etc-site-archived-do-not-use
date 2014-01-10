package edu.arizona.biosemantics.etcsite.shared.db;

import java.io.Serializable;
import java.util.Date;

import edu.arizona.biosemantics.etcsite.shared.rpc.TaskTypeEnum;

public class TaskType implements Serializable {

	private static final long serialVersionUID = -655524918747896723L;
	private int id;
	private TaskTypeEnum taskTypeEnum;
	private FileType inputFileType;
	private boolean inputDirectory;
	private FileType outputFileType;
	private boolean outputDirectory;
	private Date created;
	
	public TaskType() { }
	
	public TaskType(int id, TaskTypeEnum taskTypeEnum, FileType inputFileType, boolean inputDirectory, FileType outputFileType, boolean outputDirectory, Date created) {
		super();
		this.id = id;
		this.taskTypeEnum = taskTypeEnum;
		this.inputFileType = inputFileType;
		this.inputDirectory = inputDirectory;
		this.outputFileType = outputFileType;
		this.outputDirectory = outputDirectory;
		this.created = created;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public TaskTypeEnum getTaskTypeEnum() {
		return taskTypeEnum;
	}
	public void setTaskTypeEnum(TaskTypeEnum taskTypeEnum) {
		this.taskTypeEnum = taskTypeEnum;
	}
	
	public FileType getInputFileType() {
		return this.inputFileType;
	}

	public boolean isInputDirectory() {
		return inputDirectory;
	}

	public void setInputDirectory(boolean inputDirectory) {
		this.inputDirectory = inputDirectory;
	}

	public FileType getOutputFileType() {
		return outputFileType;
	}

	public void setOutputFileType(FileType outputFileType) {
		this.outputFileType = outputFileType;
	}

	public boolean isOutputDirectory() {
		return outputDirectory;
	}

	public void setOutputDirectory(boolean outputDirectory) {
		this.outputDirectory = outputDirectory;
	}

	public void setInputFileType(FileType inputFileType) {
		this.inputFileType = inputFileType;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}
	
	@Override
	public int hashCode() {
		return id;
	}
	
	@Override
	public boolean equals(Object object) {
		if(object == null)
			return false;
		if (getClass() != object.getClass()) {
	        return false;
	    }
		TaskType taskType = (TaskType)object;
		if(taskType.getId()==this.id)
			return true;
		return false;
	}
}
