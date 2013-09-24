package edu.arizona.sirls.etc.site.shared.rpc.db;

import java.io.Serializable;
import java.util.Calendar;

import edu.arizona.sirls.etc.site.shared.rpc.TaskType;

public class Task implements Serializable  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5715701929523907789L;
	private int id;
	private String user;
	private Calendar calendar;
	private String name;
	private TaskType task;
	private String stage;
	private boolean resumable;
	private int configuration;
	
	public Task() { }
	
	public Task(int id, String user, Calendar calendar, String name, TaskType task, int configuration, String stage, boolean resumable) {
		super();
		this.id = id;
		this.user = user;
		this.name = name;
		this.task = task;
		this.configuration = configuration;
		this.stage = stage;
		this.resumable = resumable;
		this.calendar = calendar;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public TaskType getTask() {
		return task;
	}
	public void setTask(TaskType task) {
		this.task = task;
	}
	public String getStage() {
		return stage;
	}
	public void setStage(String stage) {
		this.stage = stage;
	}
	public boolean isResumable() {
		return resumable;
	}
	public void setResumable(boolean resumable) {
		this.resumable = resumable;
	}
	public Calendar getCalendar() {
		return calendar;
	}
	public void setCalendar(Calendar calendar) {
		this.calendar = calendar;
	}

	public void setConfiguration(int configuration) {
		this.configuration = configuration;
	}

	public int getConfiguration() {
		return configuration;
	}
		
}
