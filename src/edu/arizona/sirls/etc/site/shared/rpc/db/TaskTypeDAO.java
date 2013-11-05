package edu.arizona.sirls.etc.site.shared.rpc.db;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import edu.arizona.sirls.etc.site.shared.rpc.TaskTypeEnum;

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
			InputType inputType = InputTypeDAO.getInstance().getInputType(inputTypeId);
			Date created = result.getTimestamp(4);
			taskType = new TaskType(id, edu.arizona.sirls.etc.site.shared.rpc.TaskTypeEnum.valueOf(name), inputType, created);
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
			InputType inputType = InputTypeDAO.getInstance().getInputType(inputTypeId);
			Date created = result.getTimestamp(4);
			taskType = new TaskType(id, taskTypeEnum, inputType, created);
		}
		query.close();
		return taskType;
	}
}
