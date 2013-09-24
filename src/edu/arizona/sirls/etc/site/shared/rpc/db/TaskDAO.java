package edu.arizona.sirls.etc.site.shared.rpc.db;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import edu.arizona.sirls.etc.site.shared.rpc.TaskType;

public class TaskDAO extends AbstractDAO {

	private static TaskDAO instance;

	public TaskDAO() throws IOException, ClassNotFoundException {
		super();
	}
	
	public Task getTask(int id) throws SQLException {
		Task task = null;
		this.openConnection();
		PreparedStatement statement = this.executeSQL("SELECT " +
				"tasks.id, users.name, tasks.time, tasktypes.name, tasks.configuration, taskstages.name, tasks.name, tasks.resumable " +
				"FROM tasks, taskstages, tasktypes, users " +
				"WHERE tasks.id = " + id + " tasks.user = users.id AND tasks.taskstage = taskstages.id AND taskstages.tasktype = tasktypes.id");
		ResultSet result = statement.getResultSet();
		while(result.next()) {
			id = result.getInt(0);
			String user = result.getString(1);
			Calendar time = Calendar.getInstance();
			time.setTimeInMillis(result.getLong(2));
			String taskName = result.getString(3);
			int configuration = result.getInt(4);
			String stage = result.getString(5);
			String name = result.getString(6);
			boolean resumable = result.getBoolean(7);
			
			task = new Task(id, user, time, name, TaskType.valueOf(taskName), configuration, stage, resumable);
		}
		this.closeConnection();
		return task;
	}
	
	public List<Task> getUsersTasks(int id) throws SQLException {
		List<Task> tasks = new LinkedList<Task>();
		this.openConnection();
		PreparedStatement statement = this.executeSQL("SELECT " +
				"tasks.id, users.name, tasks.time, tasktypes.name, tasks.configuration, taskstages.name, tasks.name, tasks.resumable " +
				"FROM tasks, taskstages, tasktypes, users " +
				"WHERE tasks.user = " + id + " tasks.user = users.id AND tasks.taskstage = taskstages.id AND taskstages.tasktype = tasktypes.id");
		ResultSet result = statement.getResultSet();
		while(result.next()) {
			id = result.getInt(0);
			String user = result.getString(1);
			Calendar time = Calendar.getInstance();
			time.setTimeInMillis(result.getLong(2));
			String taskName = result.getString(3);
			int configuration = result.getInt(4);
			String stage = result.getString(5);
			String name = result.getString(6);
			boolean resumable = result.getBoolean(7);
			Task task = new Task(id, user, time, name, TaskType.valueOf(taskName), configuration, stage, resumable);
			tasks.add(task);
		}
		this.closeConnection();
		return tasks;
	}

	
	public List<Task> getUsersTasks(String name) throws SQLException {
		List<Task> tasks = new LinkedList<Task>();
		this.openConnection();
		PreparedStatement statement = this.executeSQL("SELECT " +
				"tasks.id, users.name, tasks.time, tasktypes.name, tasks.configuration, taskstages.name, tasks.name, tasks.resumable " +
				"FROM tasks, taskstages, tasktypes, users " +
				"WHERE users.name = " + name + " tasks.user = users.id AND tasks.taskstage = taskstages.id AND taskstages.tasktype = tasktypes.id");
		ResultSet result = statement.getResultSet();
		while(result.next()) {
			int id = result.getInt(0);
			String user = result.getString(1);
			Calendar time = Calendar.getInstance();
			time.setTimeInMillis(result.getLong(2));
			String taskName = result.getString(3);
			int configuration = result.getInt(4);
			String stage = result.getString(5);
			name = result.getString(6);
			boolean resumable = result.getBoolean(7);
			Task task = new Task(id, user, time, name, TaskType.valueOf(taskName), configuration, stage, resumable);
			tasks.add(task);
		}
		this.closeConnection();
		return tasks;
	}

	public static TaskDAO getInstance() throws ClassNotFoundException, IOException {
		if(instance == null)
			instance = new TaskDAO();
		return instance;
	}

	public void addTask(Task task) throws SQLException, ClassNotFoundException, IOException {
		this.openConnection();
		int userId = UsersDAO.getInstance().getUser(task.getUser()).getId();
		int taskStageId = TaskStageDAO.getInstance().getTaskStage(task.getTask(), task.getStage());
		this.executeSQL("INSERT INTO tasks ('user', 'time', 'taskstage', 'configuration', 'name', 'resumable') VALUES (" + userId + 
				", " + task.getCalendar().getTimeInMillis() + 
				", " + taskStageId + 
				", " + task.getConfiguration() +
				", '" + task.getName() + "'" +
				", " + task.isResumable() + ")");
		this.closeConnection();
	}

}
