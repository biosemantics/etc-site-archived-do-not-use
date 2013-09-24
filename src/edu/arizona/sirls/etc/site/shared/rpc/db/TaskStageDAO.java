package edu.arizona.sirls.etc.site.shared.rpc.db;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import edu.arizona.sirls.etc.site.shared.rpc.TaskType;

public class TaskStageDAO extends AbstractDAO {

	private static TaskStageDAO instance;

	public TaskStageDAO() throws IOException, ClassNotFoundException {
		super();
		// TODO Auto-generated constructor stub
	}

	public static TaskStageDAO getInstance() throws ClassNotFoundException, IOException {
		if(instance == null)
			instance = new TaskStageDAO();
		return instance;
	}

	public int getTaskStage(TaskType taskType, String stage) throws ClassNotFoundException, SQLException, IOException {
		this.openConnection();
		int  taskStageId = -1;
		int taskTypeId = TaskTypeDAO.getInstance().getTaskType(taskType);
		
		PreparedStatement statement = this.executeSQL("SELECT id FROM taskStages WHERE taskStages.taskType = taskTypes.id ANd " +
				"taskTypes.id = " + taskTypeId + " AND taskStages.name = '" + stage + "'");
		ResultSet result = statement.getResultSet();
		while(result.next()) {
			taskTypeId = result.getInt(0);
		}
		
		this.closeConnection();
		return taskStageId;
	}

}
