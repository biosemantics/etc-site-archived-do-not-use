package edu.arizona.biosemantics.etcsite.shared.db;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import edu.arizona.biosemantics.etcsite.shared.rpc.TaskTypeEnum;

public class TaskTypeDAO {

	private static TaskTypeDAO instance;
	
	public static TaskTypeDAO getInstance() {
		if(instance == null)
			instance = new TaskTypeDAO();
		return instance;
	}

	public TaskType getTaskType(int id) throws SQLException, ClassNotFoundException, IOException {
		TaskType taskType = null;
		Query query = new Query("SELECT * FROM tasktypes WHERE id = ?");
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
			taskType = new TaskType(id, edu.arizona.biosemantics.etcsite.shared.rpc.TaskTypeEnum.valueOf(name), inputFileType, inputDirectory, outputFileType, outputDirectory, created);
		}
		query.close();
		return taskType;
	}
	
	public TaskType getTaskType(TaskTypeEnum taskTypeEnum) throws SQLException, ClassNotFoundException, IOException {		
		TaskType taskType = null;
		Query query = new Query("SELECT * FROM tasktypes WHERE name = ?");
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
		query.close();
		return taskType;
	}
}
