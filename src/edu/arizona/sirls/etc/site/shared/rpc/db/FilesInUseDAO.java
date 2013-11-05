package edu.arizona.sirls.etc.site.shared.rpc.db;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class FilesInUseDAO {

	private static FilesInUseDAO instance;

	public static FilesInUseDAO getInstance() {
		if(instance == null)
			instance = new FilesInUseDAO();
		return instance;
	}
	
	public FilesInUseDAO() {
		
	}

	public void setInUse(boolean value, String input, Task task) throws SQLException, ClassNotFoundException, IOException {
		Query query = new Query("SELECT id FROM filesinuse WHERE file = ?");
		query.setParameter(1, input);
		ResultSet resultFileId = query.execute();
		if(!value) {
			if(resultFileId.next()) {
				int fileInUseId = resultFileId.getInt(1);
				query.close();				
				Query deleteTasksFiles = new Query("DELETE FROM tasksfiles WHERE fileinuse = ? AND task = ?");
				deleteTasksFiles.setParameter(1, fileInUseId);
				deleteTasksFiles.setParameter(2, task.getId());
				deleteTasksFiles.executeAndClose();
				
				Query checkIfEmtpyTaskFiles = new Query("SELECT * FROM tasksfiles WHERE fileinuse = ?");
				checkIfEmtpyTaskFiles.setParameter(1, fileInUseId);
				ResultSet resultSetCheckEmpty = checkIfEmtpyTaskFiles.execute();
				if(!resultSetCheckEmpty.next()) {
					Query deleteFileInUse = new Query("DELETE FROM filesinuse WHERE id = ?");
					deleteFileInUse.setParameter(1, fileInUseId);
					deleteFileInUse.executeAndClose();
				}
			}
		} else {
			int fileInUseId = -1;
			if(resultFileId.next()) {
				fileInUseId = resultFileId.getInt(1);
			} else {
				Query insertFileInUse = new Query("INSERT INTO filesinuse (file) VALUES (?)");
				insertFileInUse.setParameter(1, input);
				insertFileInUse.execute();
				ResultSet generatedKeys = insertFileInUse.getGeneratedKeys();
				if(generatedKeys.next()) {
					fileInUseId = generatedKeys.getInt(1);
				}
				insertFileInUse.close();
			}
			
			if(fileInUseId != -1) {
				Query insertFileInUse = new Query("INSERT INTO tasksfiles (fileinuse, task) VALUES (?, ?)");
				insertFileInUse.setParameter(1, fileInUseId);
				insertFileInUse.setParameter(2, task.getId());
				insertFileInUse.executeAndClose();
			}
			
		}
		query.close();
	}

	public boolean isInUse(String input) throws SQLException, ClassNotFoundException, IOException {
		return !this.getUsingTasks(input).isEmpty();
	}

	public List<Task> getUsingTasks(String input) throws ClassNotFoundException, SQLException, IOException {
		List<Task> result = new LinkedList<Task>();
		//returns //FNA row for //FNA//1.xml, which is ok but also
		//returns //FNA for //FNA123 which is not ok
		
		Query query = new Query("SELECT id FROM filesinuse WHERE INSTR(?, CONCAT(file, ?)) = 1 OR file = ?");
		query.setParameter(1, input);
		query.setParameter(2, File.separator);
		query.setParameter(3, input);
		ResultSet resultFileId = query.execute();
		while(resultFileId.next()) {
			int fileInUseId = resultFileId.getInt(1);
			Query tasksFilesQuery = new Query("SELECT task FROM tasksfiles WHERE fileinuse = ?");
			tasksFilesQuery.setParameter(1, fileInUseId);
			ResultSet tasksResult = tasksFilesQuery.execute();
			while(tasksResult.next()) {
				int taskId = tasksResult.getInt(1);
				result.add(TaskDAO.getInstance().getTask(taskId));
			}
			tasksFilesQuery.close();
		}
		query.close();
		return result;
	}

}
