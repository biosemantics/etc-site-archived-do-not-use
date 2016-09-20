package edu.arizona.biosemantics.etcsite.server.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.etcsite.shared.model.Configuration;
import edu.arizona.biosemantics.etcsite.shared.model.MatrixGenerationConfiguration;
import edu.arizona.biosemantics.etcsite.shared.model.MatrixGenerationTaskStage;
import edu.arizona.biosemantics.etcsite.shared.model.OntologizeConfiguration;
import edu.arizona.biosemantics.etcsite.shared.model.OntologizeTaskStage;
import edu.arizona.biosemantics.etcsite.shared.model.SemanticMarkupConfiguration;
import edu.arizona.biosemantics.etcsite.shared.model.SemanticMarkupTaskStage;
import edu.arizona.biosemantics.etcsite.shared.model.Share;
import edu.arizona.biosemantics.etcsite.shared.model.ShortUser;
import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.model.TaskStage;
import edu.arizona.biosemantics.etcsite.shared.model.TaskType;
import edu.arizona.biosemantics.etcsite.shared.model.TaxonomyComparisonConfiguration;
import edu.arizona.biosemantics.etcsite.shared.model.TaxonomyComparisonTaskStage;
import edu.arizona.biosemantics.etcsite.shared.model.TinyUser;
import edu.arizona.biosemantics.etcsite.shared.model.TreeGenerationConfiguration;
import edu.arizona.biosemantics.etcsite.shared.model.TreeGenerationTaskStage;

public class TaskDAO {

	private TasksOutputFilesDAO tasksOutputFilesDAO;
	private FilesInUseDAO filesInUseDAO;
	private ShareDAO shareDAO;
	private TaskTypeDAO taskTypeDAO;
	private ConfigurationDAO configurationDAO;
	private OntologizeConfigurationDAO ontologizeConfigurationDAO;
	private MatrixGenerationConfigurationDAO matrixGenerationConfigurationDAO;
	private SemanticMarkupConfigurationDAO semanticMarkupConfigurationDAO;
	private TreeGenerationConfigurationDAO treeGenerationConfigurationDAO;
	private TaxonomyComparisonConfigurationDAO taxonomyComparisonConfigurationDAO;
	private UserDAO userDAO;
	private TaskStageDAO taskStageDAO;
	private String ownerQuery = "SELECT * FROM etcsite_tasks WHERE user=?";
	private String sharedWithQuery = "SELECT t.* FROM etcsite_tasks AS t, etcsite_shares, etcsite_shareinvitees " +
			"WHERE t.id = etcsite_shares.task AND etcsite_shares.id = etcsite_shareinvitees.share AND etcsite_shareinvitees.inviteeuser=?";
	private String allUsersTasksQuery = "(" + ownerQuery + ") UNION (" + this.sharedWithQuery + ")";
		
	public void setTasksOutputFilesDAO(TasksOutputFilesDAO tasksOutputFilesDAO) {
		this.tasksOutputFilesDAO = tasksOutputFilesDAO;
	}

	public void setOntologizeConfigurationDAO(
			OntologizeConfigurationDAO ontologizeConfigurationDAO) {
		this.ontologizeConfigurationDAO = ontologizeConfigurationDAO;
	}



	public void setFilesInUseDAO(FilesInUseDAO filesInUseDAO) {
		this.filesInUseDAO = filesInUseDAO;
	}

	public void setShareDAO(ShareDAO shareDAO) {
		this.shareDAO = shareDAO;
	}

	public void setTaskTypeDAO(TaskTypeDAO taskTypeDAO) {
		this.taskTypeDAO = taskTypeDAO;
	}

	public void setConfigurationDAO(ConfigurationDAO configurationDAO) {
		this.configurationDAO = configurationDAO;
	}

	public void setMatrixGenerationConfigurationDAO(
			MatrixGenerationConfigurationDAO matrixGenerationConfigurationDAO) {
		this.matrixGenerationConfigurationDAO = matrixGenerationConfigurationDAO;
	}	

	public void setTreeGenerationConfigurationDAO(
			TreeGenerationConfigurationDAO treeGenerationConfigurationDAO) {
		this.treeGenerationConfigurationDAO = treeGenerationConfigurationDAO;
	}

	public void setSemanticMarkupConfigurationDAO(
			SemanticMarkupConfigurationDAO semanticMarkupConfigurationDAO) {
		this.semanticMarkupConfigurationDAO = semanticMarkupConfigurationDAO;
	}
	
	public void setTaxonomyComparisonConfigurationDAO(
			TaxonomyComparisonConfigurationDAO taxonomyComparisonConfigurationDAO) {
		this.taxonomyComparisonConfigurationDAO = taxonomyComparisonConfigurationDAO;
	}

	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	public void setTaskStageDAO(TaskStageDAO taskStageDAO) {
		this.taskStageDAO = taskStageDAO;
	}

	public Task getTask(int id) {
		Task task = null;
		try(Query query = new Query("SELECT * FROM etcsite_tasks WHERE id = ?")) {
			query.setParameter(1, id);
			ResultSet result = query.execute();
			while(result.next()) {
				task = createTask(result);
			}
		}catch(Exception e) {
			log(LogLevel.ERROR, "Couldn't get task", e);
		}
		return task;
	}
	
	public Task getTask(Configuration configuration) {
		Task task = null;
		try (Query query = new Query("SELECT * FROM etcsite_tasks WHERE configuration = ?")) {
			query.setParameter(1, configuration.getId());
			ResultSet result = query.execute();
			while(result.next()) {
				task = createTask(result);
			}
		}catch(Exception e) {
			log(LogLevel.ERROR, "Couldn't get task of configuration", e);
		}
		return task;
	}
	
	
	public List<Task> getOwnedTasks(int userId) {
		List<Task> tasks = new LinkedList<Task>();
		try(Query query = new Query(ownerQuery)) {
			query.setParameter(1, userId);
			ResultSet result = query.execute();
			while(result.next()) {			
				Task task = createTask(result);
				if(task != null)
					tasks.add(task);
			}
		}catch(Exception e) {
			log(LogLevel.ERROR, "Couldn't get owned tasks of user", e);
		}
		return tasks;
	}
	
	public List<Task> getSharedWithTasks(int userId) {
		List<Task> tasks = new LinkedList<Task>();
		try(Query query = new Query(sharedWithQuery)) {
			query.setParameter(1, userId);
			ResultSet result = query.execute();
			while(result.next()) {			
				Task task = createTask(result);
				if(task != null)
					tasks.add(task);
			}
		}catch(Exception e) {
			log(LogLevel.ERROR, "Couldn't get tasks shared with user", e);
		}
		return tasks;
	}
	
	public List<Task> getAllTasks(int userId) {
		List<Task> tasks = new LinkedList<Task>();
		try(Query query = new Query(this.allUsersTasksQuery)) {
			query.setParameter(1, userId);
			query.setParameter(2, userId);
			ResultSet result = query.execute();
			while(result.next()) {			
				Task task = createTask(result);
				if(task != null)
					tasks.add(task);
			}
		} catch(Exception e) {
			log(LogLevel.ERROR, "Couldn't get all tasks of user", e);
		}
		return tasks;
	}

	public List<Task> getResumableTasks(int userId) {
		List<Task> tasks = new LinkedList<Task>();
		try(Query query = new Query("SELECT * FROM (" + allUsersTasksQuery + ")AS allTasks WHERE resumable=true AND failed=false AND complete=false")) {
			query.setParameter(1, userId);
			query.setParameter(2, userId);
			ResultSet result = query.execute();
			while(result.next()) {
				Task task = createTask(result);
				if(task !=  null) 
					tasks.add(task);
			}
		} catch(Exception e) {
			log(LogLevel.ERROR, "Couldn't get resumable tasks of user", e);
		}
		return tasks;
	}
	
	public List<Task> getFailedTasks(int userId) {
		List<Task> tasks = new LinkedList<Task>();
		try(Query query = new Query("SELECT * FROM (" + allUsersTasksQuery + ")AS allTasks WHERE failed=true")) {
			query.setParameter(1, userId);
			query.setParameter(2, userId);
			ResultSet result = query.execute();
			while(result.next()) {
				Task task = createTask(result);
				if(task != null)
					tasks.add(task);
			}
		} catch(Exception e) {
			log(LogLevel.ERROR, "Couldn't get failed tasks of user", e);
		}
		return tasks;
	}
	
	public List<Task> getFailedAndIncompletedTasks(int userId) {
		List<Task> tasks = new LinkedList<Task>();
		try(Query query = new Query("SELECT * FROM (" + allUsersTasksQuery + ")AS allTasks WHERE failed=true "
				+ "AND complete=false")) {
			query.setParameter(1, userId);
			query.setParameter(2, userId);
			ResultSet result = query.execute();
			while(result.next()) {
				Task task = createTask(result);
				if(task != null)
					tasks.add(task);
			}
		} catch(Exception e) {
			log(LogLevel.ERROR, "Couldn't get failed tasks of user", e);
		}
		return tasks;
	}
	
	public List<Task> getCompletedTasks(int userId) {
		List<Task> tasks = new LinkedList<Task>();
		try(Query query = new Query("SELECT * FROM (" + allUsersTasksQuery + ")AS allTasks WHERE complete=true")) {
			query.setParameter(1, userId);
			query.setParameter(2, userId);
			ResultSet result = query.execute();
			while(result.next()) {
				Task task = createTask(result);
				if(task != null)
					tasks.add(task);
			}
		}catch(Exception e) {
			log(LogLevel.ERROR, "Couldn't get completed tasks of user", e);
		}
		return tasks;
	}

	private Task createTask(ResultSet result) throws SQLException {
		int id = result.getInt(1);
		String name = result.getString(2);
		int taskTypeId = result.getInt(3);
		int taskStageId = result.getInt(4);
		int configurationId = result.getInt(5);
		int userId = result.getInt(6);
		boolean resumable = result.getBoolean(7);
		boolean complete = result.getBoolean(8);
		Date completed = result.getTimestamp(9);
		boolean failed = result.getBoolean(10);
		Date failedTime = result.getTimestamp(11);
		Date created = result.getTimestamp(12);
		boolean tooLong = result.getBoolean(13);
		TinyUser user = userDAO.getTinyUser(userId);
		TaskStage taskStage = taskStageDAO.getTaskStage(taskStageId);
		Configuration configuration = configurationDAO.getConfiguration(configurationId);
		TaskType taskType = taskTypeDAO.getTaskType(taskTypeId);
		
		/*Query queryNumberInput = new Query("SELECT numberofinputfiles FROM matrixgenerationconfigurations WHERE configuration = " + configurationId);
		ResultSet resultNumberInput = queryNumberInput.execute();
		int numberOfInputFiles = -1;
		if(resultNumberInput.next()) {
			numberOfInputFiles = resultNumberInput.getInt(1);
		}
		queryNumberInput.close();
		*/
		switch(taskType.getTaskTypeEnum()) {
		case ONTOLOGIZE:
			OntologizeTaskStage ontologizeTaskStage = taskStageDAO.getOntologizeTaskStage(taskStageId);
			OntologizeConfiguration ontologizeConfiguration = ontologizeConfigurationDAO.getOntologizeConfiguration(configurationId);
			return new Task(id, name, taskType, ontologizeTaskStage, ontologizeConfiguration, user, resumable, 
					complete, completed, failed, failedTime, created, tooLong);
		case MATRIX_GENERATION:
			MatrixGenerationTaskStage matrixGenerationTaskStage = taskStageDAO.getMatrixGenerationTaskStage(taskStageId);
			MatrixGenerationConfiguration matrixGenerationConfiguration = matrixGenerationConfigurationDAO.getMatrixGenerationConfiguration(configurationId);
			return new Task(id, name, taskType, matrixGenerationTaskStage, matrixGenerationConfiguration, user, 
					resumable, complete, completed, failed, failedTime, created, tooLong);
		case SEMANTIC_MARKUP:
			SemanticMarkupTaskStage semanticMarkupTaskStage = taskStageDAO.getSemanticMarkupTaskStage(taskStageId);
			SemanticMarkupConfiguration semanticMarkupConfiguration = semanticMarkupConfigurationDAO.getSemanticMarkupConfiguration(configurationId);
			return new Task(id, name, taskType, semanticMarkupTaskStage, semanticMarkupConfiguration, user, 
					resumable, complete, completed, failed, failedTime, created, tooLong);
		case TAXONOMY_COMPARISON:
			TaxonomyComparisonTaskStage taxonomyComparisonTaskStage = taskStageDAO.getTaxonomyComparisonTaskStage(taskStageId);
			TaxonomyComparisonConfiguration taxonomyComparisonConfiguration = taxonomyComparisonConfigurationDAO.getTaxonomyComparisonConfiguration(configurationId);
			return new Task(id, name, taskType, taxonomyComparisonTaskStage, taxonomyComparisonConfiguration, user, 
					resumable, complete, completed, failed, failedTime, created, tooLong);
		case TREE_GENERATION:
			TreeGenerationTaskStage treeGenerationTaskStage = taskStageDAO.getTreeGenerationTaskStage(taskStageId);
			TreeGenerationConfiguration treeGenerationConfiguration = treeGenerationConfigurationDAO.getTreeGenerationConfiguration(configurationId);
			return new Task(id, name, taskType, treeGenerationTaskStage, treeGenerationConfiguration, user, 
					resumable, complete, completed, failed, failedTime, created, tooLong);
		case VISUALIZATION:
			break;
		default:
			break;
		}
		return null;
	}

	public Task addTask(Task task) {
		Task result = null;
		try(Query query = new Query("INSERT INTO `etcsite_tasks` (`name`, `tasktype`, `taskstage`, `configuration`, "
				+ "`user`, `resumable`, `complete`, `failed`, `toolong`) VALUES " +
				"(?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
			query.setParameter(1, task.getName());
			query.setParameter(2, task.getTaskType().getId());
			query.setParameter(3, task.getTaskStage().getId());
			query.setParameter(4, task.getConfiguration().getConfiguration().getId());
			query.setParameter(5, task.getUser().getId());
			query.setParameter(6, task.isResumable());
			query.setParameter(7, task.isComplete());
			query.setParameter(8, task.isFailed());
			query.setParameter(9, task.isTooLong());
			query.execute();
			ResultSet generatedKeys = query.getGeneratedKeys();
	        if (generatedKeys.next()) {
	            result = this.getTask(generatedKeys.getInt(1));
	        }
		}catch(Exception e) {
			log(LogLevel.ERROR, "Couldn't insert task", e);
		}
		return result;
	}

	public void updateTask(Task task) {
		int id = task.getId();
		String name = task.getName();
		int taskTypeId = task.getTaskType().getId();
		int taskStageId = task.getTaskStage().getId();
		int configurationId = task.getConfiguration().getConfiguration().getId();
		int userId = task.getUser().getId(); 
		boolean resumable = task.isResumable();
		boolean complete = task.isComplete();
		Date completed = task.getCompleted();
		boolean failed = task.isFailed();
		Date failedTime = task.getFailedTime();
		boolean tooLong = task.isTooLong();
		String sql = "UPDATE etcsite_tasks SET name = ?, tasktype = ?, taskstage=?, configuration=?, user=?, resumable=?, complete=?, "
				+ "completed=?, failed=?, failedtime=?, toolong=? WHERE id = ?";
		try(Query query = new Query(sql)) {
			query.setParameter(1, name);
			query.setParameter(2, taskTypeId);
			query.setParameter(3, taskStageId);
			query.setParameter(4, configurationId);
			query.setParameter(5, userId);
			query.setParameter(6, resumable);
			query.setParameter(7, complete);
			query.setParameter(8, (completed==null? null : new Timestamp(completed.getTime())));
			query.setParameter(9, failed);
			query.setParameter(10, (failedTime==null? null : new Timestamp(failedTime.getTime())));
			query.setParameter(11, tooLong);
			query.setParameter(12, id);
			
			
			//Query query = new Query("UPDATE tasks SET name = '" + name + "',  tasktype=" + taskTypeId + ", taskstage=" + taskStageId + ", configuration=" + configurationId + 
			//		", user=" + userId + ", resumable=" + resumable + ", complete=" + complete + ", completed=" + (completed==null? completed : completed.getTime()) + " WHERE id = " + id);
			query.execute();
		} catch(Exception e) {
			log(LogLevel.ERROR, "Couldn't update task", e);
		}
	}

	public void removeTask(Task task) {
		int id = task.getId();
		
		// remove shares
		List<Share> shares = shareDAO.getShares(task);
		for(Share share : shares) {
			shareDAO.removeShare(share);
		}
		
		// remove task output files
		tasksOutputFilesDAO.removeOutputs(task);
		
		// remove files in use
		filesInUseDAO.removeFilesInUse(task);
		
		try(Query query = new Query("DELETE FROM etcsite_tasks WHERE id = ?")) {
			query.setParameter(1, id);
			query.execute();
		}catch(Exception e) {
			log(LogLevel.ERROR, "Couldn't delete task", e);
		}
	}

	
}
