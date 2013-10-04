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

public class TaskDAO extends AbstractDAO {

	private static TaskDAO instance;

	private TaskDAO() throws IOException, ClassNotFoundException, SQLException {
		super();
	}
	
	public static TaskDAO getInstance() throws ClassNotFoundException, IOException, SQLException {
		if(instance == null)
			instance = new TaskDAO();
		return instance;
	}
	
	public Task getTask(int id) throws SQLException, ClassNotFoundException, IOException {
		Task task = null;
		Query query = new Query("SELECT * FROM tasks WHERE id = " + id);
		query.execute();
		ResultSet result = query.getResultSet();
		while(result.next()) {
			id = result.getInt(1);
			int userId = result.getInt(2);
			int taskStageId = result.getInt(3);
			String name = result.getString(4);
			boolean resumable = result.getBoolean(5);
			boolean completed = result.getBoolean(6);
			Date created = result.getTimestamp(7);
			User user = UserDAO.getInstance().getUser(userId);
			TaskStage taskStage = TaskStageDAO.getInstance().getTaskStage(taskStageId);
			task = new Task(id, user, taskStage, name, resumable, completed, created);
		}
		query.close();
		return task;
	}
	
	
	public List<Task> getUsersTasks(int id) throws SQLException, ClassNotFoundException, IOException {
		List<Task> tasks = new LinkedList<Task>();
		Query query = new Query("SELECT * FROM tasks WHERE user = " + id + " AND completed = false");
		query.execute();
		ResultSet result = query.getResultSet();
		while(result.next()) {
			id = result.getInt(1);
			int userId = result.getInt(2);
			int taskStageId = result.getInt(3);
			String name = result.getString(4);
			boolean resumable = result.getBoolean(5);
			boolean completed = result.getBoolean(6);
			Date created = result.getTimestamp(7);
			User user = UserDAO.getInstance().getUser(userId);
			TaskStage taskStage = TaskStageDAO.getInstance().getTaskStage(taskStageId);
			Task task = new Task(id, user, taskStage, name, resumable, completed, created);
			tasks.add(task);
		}
		query.close();
		return tasks;
	}

	
	public List<Task> getUsersTasks(String name) throws SQLException, ClassNotFoundException, IOException {
		User user = UserDAO.getInstance().getUser(name);
		return this.getUsersTasks(user.getId());
	}
	
	public List<Task> getUsersPastTasks(String username) throws SQLException, ClassNotFoundException, IOException {
		User user = UserDAO.getInstance().getUser(username);
		return this.getUsersPastTasks(user.getId());
	}


	private List<Task> getUsersPastTasks(int id) throws SQLException, ClassNotFoundException, IOException {
		List<Task> tasks = new LinkedList<Task>();
		Query query = new Query("SELECT * FROM tasks WHERE user = " + id + " AND completed=true");
		query.execute();
		ResultSet result = query.getResultSet();
		while(result.next()) {
			id = result.getInt(1);
			int userId = result.getInt(2);
			int taskStageId = result.getInt(3);
			String name = result.getString(4);
			boolean resumable = result.getBoolean(5);
			boolean completed = result.getBoolean(6);
			Date created = result.getTimestamp(7);
			User user = UserDAO.getInstance().getUser(userId);
			TaskStage taskStage = TaskStageDAO.getInstance().getTaskStage(taskStageId);
			Task task = new Task(id, user, taskStage, name, resumable, completed, created);
			tasks.add(task);
		}
		query.close();
		return tasks;
	}

	public Task addTask(Task task) throws SQLException, ClassNotFoundException, IOException {
		Task result = null;
		Query query = new Query("INSERT INTO `tasks` (`user`, `taskstage`, `name`, `resumable`, `completed`) VALUES " +
				"(" + task.getUser().getId() + 
				", " + task.getTaskStage().getId() + 
				", '" + task.getName() + "'" +
				", " + task.isResumable() + "" +
				", " + task.isCompleted() + ")");
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
		int taskStageId = task.getTaskStage().getId();
		int userId = task.getUser().getId();
		boolean resumable = task.isResumable();
		boolean completed = task.isCompleted();
		Query query = new Query("UPDATE tasks SET name = '" + name + "', taskstage=" + taskStageId + ", user=" + userId + ", resumable=" + 
				resumable + ", completed=" + completed + " WHERE id = " + id);
		query.executeAndClose();
	}

	public void removeTask(Task task) throws SQLException, ClassNotFoundException, IOException {
		int id = task.getId();
		Query query = new Query("DELETE FROM tasks WHERE id = " + id);
		query.executeAndClose();
	}

	public List<Task> getUsersResumableTasks(String username) throws ClassNotFoundException, SQLException, IOException {
		User user = UserDAO.getInstance().getUser(username);
		return this.getUsersResumableTasks(user.getId());
	}

	private List<Task> getUsersResumableTasks(int id) throws ClassNotFoundException, SQLException, IOException {
		List<Task> tasks = new LinkedList<Task>();
		Query query = new Query("SELECT * FROM tasks WHERE user = " + id + " AND resumable=true");
		query.execute();
		ResultSet result = query.getResultSet();
		while(result.next()) {
			id = result.getInt(1);
			int userId = result.getInt(2);
			int taskStageId = result.getInt(3);
			String name = result.getString(4);
			boolean resumable = result.getBoolean(5);
			boolean completed = result.getBoolean(6);
			Date created = result.getTimestamp(7);
			User user = UserDAO.getInstance().getUser(userId);
			TaskStage taskStage = TaskStageDAO.getInstance().getTaskStage(taskStageId);
			Task task = new Task(id, user, taskStage, name, resumable, completed, created);
			tasks.add(task);
		}
		query.close();
		return tasks;
	}

}
