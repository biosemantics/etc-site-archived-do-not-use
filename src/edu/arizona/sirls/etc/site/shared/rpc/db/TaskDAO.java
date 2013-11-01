package edu.arizona.sirls.etc.site.shared.rpc.db;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.i18n.shared.DateTimeFormat.PredefinedFormat;

import edu.arizona.sirls.etc.site.shared.rpc.matrixGeneration.TaskStageEnum;

public class TaskDAO {

	private static TaskDAO instance;
	
	public static TaskDAO getInstance() {
		if(instance == null)
			instance = new TaskDAO();
		return instance;
	}
	
	public Task getTask(int id) throws SQLException, ClassNotFoundException, IOException {
		Task task = null;
		Query query = new Query("SELECT * FROM tasks WHERE id = " + id);
		ResultSet result = query.execute();
		while(result.next()) {
			task = createTask(result);
		}
		query.close();
		return task;
	}
	
	public Task getTask(Configuration configuration) throws SQLException, ClassNotFoundException, IOException {
		Task task = null;
		Query query = new Query("SELECT * FROM tasks WHERE configuration = " + configuration.getId());
		ResultSet result = query.execute();
		while(result.next()) {
			task = createTask(result);
		}
		query.close();
		return task;
	}
	
	
	public List<Task> getUsersTasks(int id) throws SQLException, ClassNotFoundException, IOException {
		List<Task> tasks = new LinkedList<Task>();
		Query query = new Query("SELECT * FROM tasks WHERE user = " + id + " AND complete = false");
		ResultSet result = query.execute();
		while(result.next()) {			
			Task task = createTask(result);
			tasks.add(task);
		}
		query.close();
		return tasks;
	}

	
	public List<Task> getUsersTasks(String name) throws SQLException, ClassNotFoundException, IOException {
		ShortUser user = UserDAO.getInstance().getShortUser(name);
		return this.getUsersTasks(user.getId());
	}
	
	public List<Task> getUsersPastTasks(String username) throws SQLException, ClassNotFoundException, IOException {
		ShortUser user = UserDAO.getInstance().getShortUser(username);
		return this.getUsersPastTasks(user.getId());
	}


	private List<Task> getUsersPastTasks(int id) throws SQLException, ClassNotFoundException, IOException {
		List<Task> tasks = new LinkedList<Task>();
		Query query = new Query("SELECT * FROM tasks WHERE user = " + id + " AND complete=true");
		ResultSet result = query.execute();
		while(result.next()) {
			Task task = createTask(result);
			tasks.add(task);
		}
		query.close();
		return tasks;
	}

	private Task createTask(ResultSet result) throws SQLException, ClassNotFoundException, IOException {
		int id = result.getInt(1);
		String name = result.getString(2);
		int taskTypeId = result.getInt(3);
		int taskStageId = result.getInt(4);
		int configurationId = result.getInt(5);
		int userId = result.getInt(6);
		boolean resumable = result.getBoolean(7);
		boolean complete = result.getBoolean(8);
		Date completed = result.getTimestamp(9);
		Date created = result.getTimestamp(10);
		ShortUser user = UserDAO.getInstance().getShortUser(userId);
		TaskStage taskStage = TaskStageDAO.getInstance().getTaskStage(taskStageId);
		Configuration configuration = ConfigurationDAO.getInstance().getConfiguration(configurationId);
		TaskType taskType = TaskTypeDAO.getInstance().getTaskType(taskTypeId);
		
		/*Query queryNumberInput = new Query("SELECT numberofinputfiles FROM matrixgenerationconfigurations WHERE configuration = " + configurationId);
		ResultSet resultNumberInput = queryNumberInput.execute();
		int numberOfInputFiles = -1;
		if(resultNumberInput.next()) {
			numberOfInputFiles = resultNumberInput.getInt(1);
		}
		queryNumberInput.close();
		*/
		
		Task task = new Task(id, name, taskType, taskStage, configuration, user, resumable, complete, completed, created);
		return task;
	}

	public Task addTask(Task task) throws SQLException, ClassNotFoundException, IOException {
		Task result = null;
		Query query = new Query("INSERT INTO `tasks` (`name`, `tasktype`, `taskstage`, `configuration`, `user`, `resumable`, `complete`) VALUES " +
				"('" + task.getName() + "'" + 
				", " + task.getTaskType().getId() +
				", " + task.getTaskStage().getId() +
				", " + task.getConfiguration().getId() +
				", " + task.getUser().getId() +
				", " + task.isResumable() + "" +
				", " + task.isComplete() + ")");
		query.execute();
		ResultSet generatedKeys = query.getGeneratedKeys();
        if (generatedKeys.next()) {
            result = this.getTask(generatedKeys.getInt(1));
        }
		query.close();
		return result;
	}

	public void updateTask(Task task) throws SQLException, ClassNotFoundException, IOException {
		int id = task.getId();
		String name = task.getName();
		int taskTypeId = task.getTaskType().getId();
		int taskStageId = task.getTaskStage().getId();
		int configurationId = task.getConfiguration().getId();
		int userId = task.getUser().getId();
		boolean resumable = task.isResumable();
		boolean complete = task.isComplete();
		Date completed = task.getCompleted();
		String sql = "UPDATE tasks SET name = ?, tasktype = ?, taskstage=?, configuration=?, user=?, resumable=?, complete=?, completed=? WHERE id = ?";
		Query query = new Query(sql);
		query.setParameter(1, name);
		query.setParameter(2, taskTypeId);
		query.setParameter(3, taskStageId);
		query.setParameter(4, configurationId);
		query.setParameter(5, userId);
		query.setParameter(6, resumable);
		query.setParameter(7, complete);
		query.setParameter(8, (completed==null? null : new Timestamp(completed.getTime())));
		query.setParameter(9, id);
		
		//Query query = new Query("UPDATE tasks SET name = '" + name + "',  tasktype=" + taskTypeId + ", taskstage=" + taskStageId + ", configuration=" + configurationId + 
		//		", user=" + userId + ", resumable=" + resumable + ", complete=" + complete + ", completed=" + (completed==null? completed : completed.getTime()) + " WHERE id = " + id);
		query.executeAndClose();
	}

	public void removeTask(Task task) throws SQLException, ClassNotFoundException, IOException {
		int id = task.getId();
		Query query = new Query("DELETE FROM tasks WHERE id = " + id);
		query.executeAndClose();
	}

	public List<Task> getUsersResumableTasks(String username) throws ClassNotFoundException, SQLException, IOException {
		ShortUser user = UserDAO.getInstance().getShortUser(username);
		return this.getUsersResumableTasks(user.getId());
	}

	private List<Task> getUsersResumableTasks(int id) throws ClassNotFoundException, SQLException, IOException {
		List<Task> tasks = new LinkedList<Task>();
		Query query = new Query("SELECT * FROM tasks WHERE user = " + id + " AND resumable=true");
		ResultSet result = query.execute();
		while(result.next()) {
			Task task = createTask(result);
			tasks.add(task);
		}
		query.close();
		return tasks;
	}



}
