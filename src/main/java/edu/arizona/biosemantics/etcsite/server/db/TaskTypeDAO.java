package edu.arizona.biosemantics.etcsite.server.db;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import edu.arizona.biosemantics.etcsite.shared.model.TaskType;
import edu.arizona.biosemantics.etcsite.shared.model.TaskTypeEnum;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileType;

public class TaskTypeDAO {

	private static TaskTypeDAO instance;
	
	public static TaskTypeDAO getInstance() {
		if(instance == null)
			instance = new TaskTypeDAO();
		return instance;
	}

	public TaskType getTaskType(int id) {
		TaskType taskType = null;
		try(Query query = new Query("SELECT * FROM tasktypes WHERE id = ?")) {
			query.setParameter(1, id);
			ResultSet result = query.execute();
			while(result.next()) {
				id = result.getInt(1);
				String name = result.getString(2);
				int inputTypeId = result.getInt(3);
				boolean inputDirectory = result.getString(4).equals("directory");
				int outputTypeId = result.getInt(5);
				boolean outputDirectory = result.getString(6).equals("directory");
				FileType inputFileType = FileTypeDAO.getInstance().getFileType(inputTypeId);
				FileType outputFileType = FileTypeDAO.getInstance().getFileType(outputTypeId);
				Date created = result.getTimestamp(7);
				taskType = new TaskType(id, edu.arizona.biosemantics.etcsite.shared.model.TaskTypeEnum.valueOf(name), inputFileType, inputDirectory, outputFileType, outputDirectory, created);
			}	
		}catch(Exception e) {
			e.printStackTrace();
		}
		return taskType;
	}
	
	public TaskType getTaskType(TaskTypeEnum taskTypeEnum) {		
		TaskType taskType = null;
		try(Query query = new Query("SELECT * FROM tasktypes WHERE name = ?")) {
			query.setParameter(1, taskTypeEnum.toString());
			ResultSet result = query.execute();
			while(result.next()) {
				int id = result.getInt(1);
				taskTypeEnum = TaskTypeEnum.valueOf(result.getString(2));
				int inputTypeId = result.getInt(3);
				boolean inputDirectory = result.getString(4).equals("directory");
				int outputTypeId = result.getInt(5);
				boolean outputDirectory = result.getString(6).equals("directory");
				FileType inputFileType = FileTypeDAO.getInstance().getFileType(inputTypeId);
				FileType outputFileType = FileTypeDAO.getInstance().getFileType(outputTypeId);
				Date created = result.getTimestamp(7);
				taskType = new TaskType(id, taskTypeEnum, inputFileType, inputDirectory, outputFileType, outputDirectory, created);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return taskType;
	}
}
