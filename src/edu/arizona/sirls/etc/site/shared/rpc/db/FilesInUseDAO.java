package edu.arizona.sirls.etc.site.shared.rpc.db;

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
		Query query = new Query("SELECT id FROM filesinuse WHERE file = '" + input + "'");
		ResultSet resultFileId = query.execute();
		if(!value) {
			if(resultFileId.next()) {
				int fileInUseId = resultFileId.getInt(1);
				query.close();				
				Query deleteTasksFiles = new Query("DELETE FROM tasksfiles WHERE fileinuse = " + fileInUseId);
				deleteTasksFiles.executeAndClose();
				Query delteFileInUse = new Query("DELETE FROM filesinuse WHERE id = " + fileInUseId);
				delteFileInUse.executeAndClose();

			}
		} else {
			int fileInUseId = -1;
			if(resultFileId.next()) {
				fileInUseId = resultFileId.getInt(1);
			} else {
				Query insertFileInUse = new Query("INSERT INTO filesinuse (file) VALUES ('" + input + "')");
				insertFileInUse.execute();
				ResultSet generatedKeys = insertFileInUse.getGeneratedKeys();
				if(generatedKeys.next()) {
					fileInUseId = generatedKeys.getInt(1);
				}
				insertFileInUse.close();
			}
			
			if(fileInUseId != -1) {
				Query insertFileInUse = new Query("INSERT INTO tasksfiles (fileinuse, task) VALUES (" + fileInUseId + ", " + task.getId() + ")");
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
		Query query = new Query("SELECT id FROM filesinuse WHERE INSTR('" + input + "', file) = 1");
		ResultSet resultFileId = query.execute();
		while(resultFileId.next()) {
			int fileInUseId = resultFileId.getInt(1);
			Query tasksFilesQuery = new Query("SELECT task FROM tasksfiles WHERE fileinuse = " + fileInUseId);
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
