package edu.arizona.biosemantics.etcsite.server.db;

import java.io.File;
import java.sql.ResultSet;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.etcsite.server.db.Query.QueryException;
import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileInUse;

public class FilesInUseDAO {

	private TaskDAO taskDAO;
	
	public void setTaskDAO(TaskDAO taskDAO) {
		this.taskDAO = taskDAO;
	}
	
	public void setInUse(boolean value, String input, Task task) {
		try (Query query = new Query("SELECT id FROM etcsite_filesinuse WHERE file = ?")) {
			query.setParameter(1, input);
			ResultSet resultFileId = query.execute();
			if(!value) {
				if(resultFileId.next()) {
					int fileInUseId = resultFileId.getInt(1);
					try(Query deleteTasksFiles = new Query("DELETE FROM etcsite_tasksfiles WHERE fileinuse = ? AND task = ?")) {
						deleteTasksFiles.setParameter(1, fileInUseId);
						deleteTasksFiles.setParameter(2, task.getId());
						deleteTasksFiles.execute();
					}
					
					try(Query checkIfEmtpyTaskFiles = new Query("SELECT * FROM etcsite_tasksfiles WHERE fileinuse = ?")) {
						checkIfEmtpyTaskFiles.setParameter(1, fileInUseId);
						ResultSet resultSetCheckEmpty = checkIfEmtpyTaskFiles.execute();
						if(!resultSetCheckEmpty.next()) {
							try (Query deleteFileInUse = new Query("DELETE FROM etcsite_filesinuse WHERE id = ?")) {
								deleteFileInUse.setParameter(1, fileInUseId);
								deleteFileInUse.execute();
							}
						}
					}
				}
			} else {
				int fileInUseId = -1;
				if(resultFileId.next()) {
					fileInUseId = resultFileId.getInt(1);
				} else {
					try(Query insertFileInUse = new Query("INSERT INTO etcsite_filesinuse (file) VALUES (?)")) {
						insertFileInUse.setParameter(1, input);
						insertFileInUse.execute();
						ResultSet generatedKeys = insertFileInUse.getGeneratedKeys();
						if(generatedKeys.next()) {
							fileInUseId = generatedKeys.getInt(1);
						}
					}
				}
				
				if(fileInUseId != -1) {
					try(Query insertFileInUse = new Query("INSERT INTO etcsite_tasksfiles (fileinuse, task) VALUES (?, ?)")) {
						insertFileInUse.setParameter(1, fileInUseId);
						insertFileInUse.setParameter(2, task.getId());
						insertFileInUse.execute();
					}
				}
				
			}
		} catch(Exception e) {
			log(LogLevel.ERROR, "Couldn't set in use files", e);
		}
	}

	public boolean isInUse(String input) {
		return !this.getUsingTasks(input).isEmpty();
	}

	public List<Task> getUsingTasks(String input) {
		List<Task> result = new LinkedList<Task>();
		//returns //FNA row for //FNA//1.xml, which is ok but also
		//returns //FNA for //FNA123 which is not ok
		
		try (Query query = new Query("SELECT id FROM etcsite_filesinuse WHERE INSTR(?, CONCAT(file, ?)) = 1 OR file = ?")) {
			query.setParameter(1, input);
			query.setParameter(2, File.separator);
			query.setParameter(3, input);
			ResultSet resultFileId = query.execute();
			while(resultFileId.next()) {
				int fileInUseId = resultFileId.getInt(1);
				result = getUsingTasks(fileInUseId);
			}
		} catch(Exception e) {
			log(LogLevel.ERROR, "Couldn't get using tasks of files in use", e);
		}
		return result;
	}
	
	public List<Task> getUsingTasks(int fileInUseId) {
		List<Task> result = new LinkedList<Task>();
		try(Query tasksFilesQuery = new Query("SELECT task FROM etcsite_tasksfiles WHERE fileinuse = ?")) {
			tasksFilesQuery.setParameter(1, fileInUseId);
			ResultSet tasksResult = tasksFilesQuery.execute();
			while(tasksResult.next()) {
				int taskId = tasksResult.getInt(1);
				result.add(taskDAO.getTask(taskId));
			}
		} catch(Exception e) {
			log(LogLevel.ERROR, "Couldn't get using tasks of files in use", e);
		}
		return result;
	}

	public List<FileInUse> getFilesInUse(Task task) {
		List<FileInUse> result = new LinkedList<FileInUse>();
		try (Query query = new Query("SELECT * FROM etcsite_tasksfiles WHERE task = ?")) {
			query.setParameter(1, task.getId());
			ResultSet resultSet = query.execute();
			while(resultSet.next()) {
				int fileInUseId = resultSet.getInt(1);
				FileInUse fileInUse = this.getFileInUse(fileInUseId);
				result.add(fileInUse);
			}
		} catch(Exception e) {
			log(LogLevel.ERROR, "Couldn't get files in use of task", e);
		}
		return result;
	}

	private FileInUse getFileInUse(int fileInUseId) {
		FileInUse result = null;
		try (Query query = new Query("SELECT * FROM etcsite_filesinuse WHERE id = ?")) {
			query.setParameter(1, fileInUseId);
			ResultSet resultSet = query.execute();
			if(resultSet.next()) {
				fileInUseId = resultSet.getInt(1);
				String filePath = resultSet.getString(2);
				Date created = resultSet.getTimestamp(3);
				List<Task> usingTasks = this.getUsingTasks(fileInUseId);
				result = new FileInUse(fileInUseId, filePath, usingTasks, created);
			}
		} catch(Exception e) {
			log(LogLevel.ERROR, "Couldn't get files in use of id", e);
		}
		return result;
	}
		
	public void removeFilesInUse(Task task) {
		Set<Integer> filesInUseByTask = new HashSet<Integer>();
		try(Query query = new Query("SELECT fileinuse FROM etcsite_tasksfiles WHERE task = ?")) {
			query.setParameter(1, task.getId());
			ResultSet resultSet = query.execute();
			while(resultSet.next()) {
				int fileInUseId = resultSet.getInt(1);
				filesInUseByTask.add(fileInUseId);
			}
		} catch(Exception e) {
			log(LogLevel.ERROR, "Couldn't get files in use of task for remove", e);
		}
		
		try(Query query = new Query("DELETE FROM etcsite_tasksfiles WHERE task = ?")) {
			query.setParameter(1, task.getId());
			query.execute();
		} catch(QueryException e) {
			log(LogLevel.ERROR, "Couldn't remove tasksfiles of task", e);
		}
		
		for(Integer fileInUseId : filesInUseByTask) {
			try(Query checkIfEmtpyTaskFiles = new Query("SELECT * FROM etcsite_tasksfiles WHERE fileinuse = ?")) {
				checkIfEmtpyTaskFiles.setParameter(1, fileInUseId);
				ResultSet resultSetCheckEmpty = checkIfEmtpyTaskFiles.execute();
				if(!resultSetCheckEmpty.next()) {
					try(Query deleteFileInUse = new Query("DELETE FROM etcsite_filesinuse WHERE id = ?")) {
						deleteFileInUse.setParameter(1, fileInUseId);
						deleteFileInUse.execute();
					}
				}
			} catch(Exception e) {
				log(LogLevel.ERROR, "Couldn't remove files in use of task", e);
			}
		}
	}

}
