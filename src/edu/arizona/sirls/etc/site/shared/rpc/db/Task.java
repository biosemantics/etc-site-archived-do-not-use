package edu.arizona.sirls.etc.site.shared.rpc.db;

import java.io.Serializable;
import java.util.Calendar;

import edu.arizona.sirls.etc.site.shared.rpc.TaskTypeEnum;

public class Task implements Serializable  {

	private static final long serialVersionUID = 5053756810897454852L;
	private boolean resumable;
	private String name;
	private TaskStage taskStage;
	private long time;
	private User user;
	private int id;
	
	public Task() { }

	public Task(int id, User user, long time, TaskStage taskStage, String name, boolean resumable) {
		this.id = id;
		this.user = user;
		this.time = time;
		this.taskStage = taskStage;
		this.name = name;
		this.resumable = resumable;
	}

	public boolean isResumable() {
		return resumable;
	}

	public void setResumable(boolean resumable) {
		this.resumable = resumable;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public TaskStage getTaskStage() {
		return taskStage;
	}

	public void setTaskStage(TaskStage taskStage) {
		this.taskStage = taskStage;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	

	
		
}
