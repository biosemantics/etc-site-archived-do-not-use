package edu.arizona.sirls.etc.site.shared.rpc.db;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import edu.arizona.sirls.etc.site.shared.rpc.matrixGeneration.TaskStageEnum;

public class TaskDAO extends AbstractDAO {

	private static TaskDAO instance;

	private TaskDAO() throws IOException, ClassNotFoundException {
		super();
	}
	
	public static TaskDAO getInstance() throws ClassNotFoundException, IOException {
		if(instance == null)
			instance = new TaskDAO();
		return instance;
	}
	
	public Task getTask(int id) throws SQLException, ClassNotFoundException, IOException {
		Task task = null;
		this.openConnection();
		PreparedStatement statement = this.executeSQL("SELECT * FROM tasks WHERE id = " + id);
		ResultSet result = statement.getResultSet();
		
		while(result.next()) {
			id = result.getInt(1);
			int userId = result.getInt(2);
			int taskStageId = result.getInt(3);
			String name = result.getString(4);
			boolean resumable = result.getBoolean(5);
			long created = result.getLong(6);
			User user = UserDAO.getInstance().getUser(userId);
			TaskStage taskStage = TaskStageDAO.getInstance().getTaskStage(taskStageId);
			task = new Task(id, user, taskStage, name, resumable, created);
		}
		this.closeConnection();
		return task;
	}
	
	
	public List<Task> getUsersTasks(int id) throws SQLException, ClassNotFoundException, IOException {
		List<Task> tasks = new LinkedList<Task>();
		this.openConnection();
		PreparedStatement statement = this.executeSQL("SELECT * FROM tasks WHERE user = " + id);
		ResultSet result = statement.getResultSet();
		while(result.next()) {
			id = result.getInt(1);
			int userId = result.getInt(2);
			int taskStageId = result.getInt(3);
			String name = result.getString(4);
			boolean resumable = result.getBoolean(5);
			long created = result.getLong(6);
			User user = UserDAO.getInstance().getUser(userId);
			TaskStage taskStage = TaskStageDAO.getInstance().getTaskStage(taskStageId);
			Task task = new Task(id, user, taskStage, name, resumable, created);
			tasks.add(task);
		}
		this.closeConnection();
		return tasks;
	}

	
	public List<Task> getUsersTasks(String name) throws SQLException, ClassNotFoundException, IOException {
		User user = UserDAO.getInstance().getUser(name);
		return this.getUsersTasks(user.getId());
	}


	public Task addTask(Task task) throws SQLException, ClassNotFoundException, IOException {
		Task result = null;
		this.openConnection();
		PreparedStatement statement =  this.executeSQL("INSERT INTO tasks ('user', 'taskstage', 'name', 'resumable') VALUES (" + task.getUser().getId() + 
				", " + task.getTaskStage().getId() + 
				", '" + task.getName() + "'" +
				", " + task.isResumable() + ")");
		ResultSet generatedKeys = statement.getGeneratedKeys();
        if (generatedKeys.next()) {
            result = this.getTask(generatedKeys.getInt(1));
        }
		this.closeConnection();
		return result;
	}

	public void updateTask(Task task) throws ClassNotFoundException, SQLException, IOException {
		this.openConnection();
		this.executeSQL("UPDATE tasks SET task.stage = " + task.getTaskStage().getId() + " WHERE id = " + task.getId());
		this.closeConnection();
	}

	public void updateTask(int id, edu.arizona.sirls.etc.site.shared.rpc.TaskTypeEnum taskType, TaskStageEnum step) throws SQLException, ClassNotFoundException, IOException {
		this.openConnection();
		TaskType dbTaskType = TaskTypeDAO.getInstance().getTaskType(taskType.toString());
		TaskStage taskStage = TaskStageDAO.getInstance().getTaskStage(dbTaskType, step.toString());
		this.executeSQL("UPDATE tasks SET task.stage = " + taskStage.getId() + " WHERE id = " + id);
		this.closeConnection();
	}

}
