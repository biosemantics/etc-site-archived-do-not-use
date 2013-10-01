package edu.arizona.sirls.etc.site.shared.rpc.db;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;


public class MatrixGenerationConfigurationDAO extends AbstractDAO {

	private static MatrixGenerationConfigurationDAO instance;

	public MatrixGenerationConfigurationDAO() throws IOException, ClassNotFoundException, SQLException {
		super();
	}

	public static MatrixGenerationConfigurationDAO getInstance() throws ClassNotFoundException, IOException, SQLException {
		if(instance == null)
			instance = new MatrixGenerationConfigurationDAO();
		return instance;
	}
	
	public MatrixGenerationConfiguration getMatrixGenerationConfiguration(int id) throws SQLException, ClassNotFoundException, IOException {
		MatrixGenerationConfiguration matrixGenerationConfiguration = null;
		Query query = new Query("SELECT * FROM matrixgenerationconfiguration WHERE id = " + id);
		query.execute();
		ResultSet result = query.getResultSet();
		while(result.next()) {
			id = result.getInt(1);
			String input = result.getString(2);
			int glossaryId = result.getInt(3);
			int oto = result.getInt(4);
			String output = result.getString(5);
			int taskId = result.getInt(6);
			long created = result.getLong(7);
			Glossary glossary = GlossaryDAO.getInstance().getGlossary(glossaryId);
			Task task = TaskDAO.getInstance().getTask(taskId);
			matrixGenerationConfiguration = new MatrixGenerationConfiguration(id, input, glossary, oto, output, task, created);
		}
		query.close();
		return matrixGenerationConfiguration;
	}

	public MatrixGenerationConfiguration addMatrixGenerationConfiguration(MatrixGenerationConfiguration matrixGenerationConfiguration) throws SQLException, ClassNotFoundException, IOException {
		MatrixGenerationConfiguration result = null;
		Query query = new Query("INSERT INTO `matrixgenerationconfiguration` " +
				"(`input`, `glossary`, `oto`, `output`, `task`) VALUES ('" + matrixGenerationConfiguration.getInput() + 
				"', " + matrixGenerationConfiguration.getGlossary().getId() + 
				", " + matrixGenerationConfiguration.getOtoId() + 
				", '" + matrixGenerationConfiguration.getOutput() + "'" +
				", " + matrixGenerationConfiguration.getTask().getId() + ")");
		query.execute();
		ResultSet generatedKeys = query.getGeneratedKeys();
		if (generatedKeys.next()) {
            result = this.getMatrixGenerationConfiguration(generatedKeys.getInt(1));
        }
		query.close();
		return result;
	}

	public MatrixGenerationConfiguration getMatrixGenerationConfigurationFromTask(int taskId) throws SQLException, ClassNotFoundException, IOException {
		MatrixGenerationConfiguration matrixGenerationConfiguration = null;
		Query query = new Query("SELECT * FROM matrixgenerationconfiguration WHERE task = " + taskId);
		query.execute();
		ResultSet result = query.getResultSet();
		while(result.next()) {
			int id = result.getInt(1);
			String input = result.getString(2);
			int glossaryId = result.getInt(3);
			int oto = result.getInt(4);
			String output = result.getString(5);
			taskId = result.getInt(6);
			long created = result.getLong(7);
			Glossary glossary = GlossaryDAO.getInstance().getGlossary(glossaryId);
			Task task = TaskDAO.getInstance().getTask(taskId);
			matrixGenerationConfiguration = new MatrixGenerationConfiguration(id, input, glossary, oto, output, task, created);
		}
		query.close();
		return matrixGenerationConfiguration;
	}

	public void updateMatrixGenerationConfiguration(MatrixGenerationConfiguration matrixGenerationConfiguration) throws SQLException, ClassNotFoundException, IOException {
		int id = matrixGenerationConfiguration.getId();
		int glossaryId = matrixGenerationConfiguration.getGlossary().getId();
		String input = matrixGenerationConfiguration.getInput();
		String output = matrixGenerationConfiguration.getOutput();
		int taskId = matrixGenerationConfiguration.getTask().getId();
		int otoId = matrixGenerationConfiguration.getOtoId();
		Query query = new Query("UPDATE matrixgenerationconfiguration SET glossary = " + glossaryId + ", input = '" + input + "', " +
				"output = '" + output + "', task = " + taskId + ", oto = " + otoId +" WHERE id = " + id);
		query.executeAndClose();
	}

	public void remove(MatrixGenerationConfiguration matrixGenerationConfiguration) throws SQLException, ClassNotFoundException, IOException {
		int id = matrixGenerationConfiguration.getId();
		Query query = new Query("DELETE FROM matrixgenerationconfiguration WHERE id = " + id);
		query.executeAndClose();
	}
}
