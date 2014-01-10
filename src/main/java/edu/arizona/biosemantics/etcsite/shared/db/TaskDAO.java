package edu.arizona.biosemantics.etcsite.shared.db;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class TaskDAO {

	private static TaskDAO instance;
	private String ownerQuery = "SELECT * FROM tasks WHERE user=?";
	private String sharedWithQuery = "SELECT t.* FROM tasks AS t, shares, shareinvitees WHERE t.id = shares.task AND shares.id = shareinvitees.share AND shareinvitees.inviteeuser=?";
	private String allUsersTasksQuery = "(" + ownerQuery + ") UNION (" + this.sharedWithQuery + ")";
	
	public static TaskDAO getInstance() {
		if(instance == null)
			instance = new TaskDAO();
		return instance;
	}
	
	public Task getTask(int id) throws SQLException, ClassNotFoundException, IOException {
		Task task = null;
		Query query = new Query("SELECT * FROM tasks WHERE id = ?");
		query.setParameter(1, id);
		ResultSet result = query.execute();
		while(result.next()) {
			task = createTask(result);
		}
		query.close();
		return task;
	}
	
	public Task getTask(Configuration configuration) throws SQLException, ClassNotFoundException, IOException {
		Task task = null;
		Query query = new Query("SELECT * FROM tasks WHERE configuration = ?");
		query.setParameter(1, configuration.getId());
		ResultSet result = query.execute();
		while(result.next()) {
			task = createTask(result);
		}
		query.close();
		return task;
	}
	
	
	public List<Task> getOwnedTasks(int userId) throws SQLException, ClassNotFoundException, IOException {
		List<Task> tasks = new LinkedList<Task>();
		Query query = new Query(ownerQuery);
		query.setParameter(1, userId);
		ResultSet result = query.execute();
		while(result.next()) {			
			Task task = createTask(result);
			tasks.add(task);
		}
		query.close();
		return tasks;
	}
	
	public List<Task> getSharedWithTasks(int userId) throws SQLException, ClassNotFoundException, IOException {
		List<Task> tasks = new LinkedList<Task>();
		Query query = new Query(sharedWithQuery);
		query.setParameter(1, userId);
		ResultSet result = query.execute();
		while(result.next()) {			
			Task task = createTask(result);
			tasks.add(task);
		}
		query.close();
		return tasks;
	}
	
	public List<Task> getAllTasks(int userId) throws ClassNotFoundException, SQLException, IOException {
		List<Task> tasks = new LinkedList<Task>();
		Query query = new Query(this.allUsersTasksQuery);
		query.setParameter(1, userId);
		query.setParameter(2, userId);
		ResultSet result = query.execute();
		while(result.next()) {			
			Task task = createTask(result);
			tasks.add(task);
		}
		query.close();
		return tasks;
	}

	private List<Task> getResumableTasks(int userId) throws ClassNotFoundException, SQLException, IOException {
		List<Task> tasks = new LinkedList<Task>();
		Query query = new Query("SELECT * FROM (" + allUsersTasksQuery + ")AS allTasks WHERE resumable=true");
		query.setParameter(1, userId);
		query.setParameter(2, userId);
		ResultSet result = query.execute();
		while(result.next()) {
			Task task = createTask(result);
			tasks.add(task);
		}
		query.close();
		return tasks;
	}
	
	private List<Task> getCompletedTasks(int userId) throws SQLException, ClassNotFoundException, IOException {
		List<Task> tasks = new LinkedList<Task>();
		Query query = new Query("SELECT * FROM (" + allUsersTasksQuery + ")AS allTasks WHERE complete=true");
		query.setParameter(1, userId);
		query.setParameter(2, userId);
		ResultSet result = query.execute();
		while(result.next()) {
			Task task = createTask(result);
			tasks.add(task);
		}
		query.close();
		return tasks;
	}

	public List<Task> getSharedWithTasks(String name) throws SQLException, ClassNotFoundException, IOException {
		ShortUser user = UserDAO.getInstance().getShortUser(name);
		return this.getSharedWithTasks(user.getId());
	}
	
	public List<Task> getAllTasks(String name) throws ClassNotFoundException, SQLException, IOException {
		ShortUser user = UserDAO.getInstance().getShortUser(name);
		return this.getAllTasks(user.getId());
	}
	
	public List<Task> getOwnedTasks(String name) throws SQLException, ClassNotFoundException, IOException {
		ShortUser user = UserDAO.getInstance().getShortUser(name);
		return this.getOwnedTasks(user.getId());
	}
	
	public List<Task> getCompletedTasks(String username) throws SQLException, ClassNotFoundException, IOException {
		ShortUser user = UserDAO.getInstance().getShortUser(username);
		return this.getCompletedTasks(user.getId());
	}

	public List<Task> getResumableTasks(String username) throws ClassNotFoundException, SQLException, IOException {
		ShortUser user = UserDAO.getInstance().getShortUser(username);
		return this.getResumableTasks(user.getId());
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
		switch(taskType.getTaskTypeEnum()) {
		case MATRIX_GENERATION:
			MatrixGenerationTaskStage matrixGenerationTaskStage = TaskStageDAO.getInstance().getMatrixGenerationTaskStage(taskStageId);
			MatrixGenerationConfiguration matrixGenerationConfiguration = MatrixGenerationConfigurationDAO.getInstance().getMatrixGenerationConfiguration(configurationId);
			return new Task(id, name, taskType, matrixGenerationTaskStage, matrixGenerationConfiguration, user, resumable, complete, completed, created);
		case SEMANTIC_MARKUP:
			SemanticMarkupTaskStage semanticMarkupTaskStage = TaskStageDAO.getInstance().getSemanticMarkupTaskStage(taskStageId);
			SemanticMarkupConfiguration semanticMarkupConfiguration = SemanticMarkupConfigurationDAO.getInstance().getSemanticMarkupConfiguration(configurationId);
			return new Task(id, name, taskType, semanticMarkupTaskStage, semanticMarkupConfiguration, user, resumable, complete, completed, created);
		case TAXONOMY_COMPARISON:
			break;
		case TREE_GENERATION:
			break;
		case VISUALIZATION:
			break;
		default:
			break;
		}
		return null;
	}

	public Task addTask(Task task) throws SQLException, ClassNotFoundException, IOException {
		Task result = null;
		Query query = new Query("INSERT INTO `tasks` (`name`, `tasktype`, `taskstage`, `configuration`, `user`, `resumable`, `complete`) VALUES " +
				"(?, ?, ?, ?, ?, ?, ?)");
		query.setParameter(1, task.getName());
		query.setParameter(2, task.getTaskType().getId());
		query.setParameter(3, task.getTaskStage().getId());
		query.setParameter(4, task.getConfiguration().getConfiguration().getId());
		query.setParameter(5, task.getUser().getId());
		query.setParameter(6, task.isResumable());
		query.setParameter(7, task.isComplete());
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
		int configurationId = task.getConfiguration().getConfiguration().getId();
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
		
		// remove shares
		List<Share> shares = ShareDAO.getInstance().getShares(task);
		for(Share share : shares) {
			ShareDAO.getInstance().removeShare(share);
		}
		
		// remove task output files
		TasksOutputFilesDAO.getInstance().removeOutputs(task);
		
		// remove files in use
		FilesInUseDAO.getInstance().removeFilesInUse(task);
		
		Query query = new Query("DELETE FROM tasks WHERE id = ?");
		query.setParameter(1, id);
		query.executeAndClose();
	}
}
