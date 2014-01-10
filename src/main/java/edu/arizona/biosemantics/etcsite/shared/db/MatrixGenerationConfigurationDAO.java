package edu.arizona.biosemantics.etcsite.shared.db;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MatrixGenerationConfigurationDAO {

	private static MatrixGenerationConfigurationDAO instance;

	public static MatrixGenerationConfigurationDAO getInstance() {
		if(instance == null)
			instance = new MatrixGenerationConfigurationDAO();
		return instance;
	}
	
	public MatrixGenerationConfiguration getMatrixGenerationConfiguration(int configurationId) throws SQLException, ClassNotFoundException, IOException {
		MatrixGenerationConfiguration matrixGenerationConfiguration = null;
		Query query = new Query("SELECT * FROM matrixgenerationconfigurations WHERE configuration = ?");
		query.setParameter(1, configurationId);
		ResultSet result = query.execute();
		while(result.next()) {
			matrixGenerationConfiguration = createMatrixGenerationConfiguration(result);
		}
		query.close();
		return matrixGenerationConfiguration;
	}

	private MatrixGenerationConfiguration createMatrixGenerationConfiguration(ResultSet result) throws SQLException, ClassNotFoundException, IOException {
		int configurationId = result.getInt(1);
		String input = result.getString(2);
		String output = result.getString(3);
		Configuration configuration = ConfigurationDAO.getInstance().getConfiguration(configurationId);
		return  new MatrixGenerationConfiguration(configuration, input, output);
	}

	public MatrixGenerationConfiguration addMatrixGenerationConfiguration(MatrixGenerationConfiguration matrixGenerationConfiguration) throws SQLException, ClassNotFoundException, IOException {
		MatrixGenerationConfiguration result = null;
		Query query = new Query("INSERT INTO `configurations` (`id`) VALUES(NULL)");
		query.execute();
		ResultSet generatedKeys = query.getGeneratedKeys();
		while(generatedKeys.next()) {
			Configuration configuration = ConfigurationDAO.getInstance().getConfiguration(generatedKeys.getInt(1));
			Query matrixGenerationQuery = new Query("INSERT INTO `matrixgenerationconfigurations` " +
					"(`configuration`, `input`, `output`) VALUES (?, ?, ?)");
			matrixGenerationQuery.setParameter(1, configuration.getId());
			matrixGenerationQuery.setParameter(2, matrixGenerationConfiguration.getInput());
			matrixGenerationQuery.setParameter(3, matrixGenerationConfiguration.getOutput());
			matrixGenerationQuery.execute();
			matrixGenerationQuery.close();
			result = this.getMatrixGenerationConfiguration(generatedKeys.getInt(1));
		}
		return result;
	}

	public void updateMatrixGenerationConfiguration(MatrixGenerationConfiguration matrixGenerationConfiguration) throws SQLException, ClassNotFoundException, IOException {
		Configuration configuration = matrixGenerationConfiguration.getConfiguration();
		String input = matrixGenerationConfiguration.getInput();
		String output = matrixGenerationConfiguration.getOutput();
		Query query = new Query("UPDATE matrixgenerationconfigurations SET input = ?, output = ? WHERE configuration = ?");
		query.setParameter(1, input);
		query.setParameter(2, output);
		query.setParameter(3, configuration.getId());
		query.executeAndClose();
	}

	public void remove(MatrixGenerationConfiguration matrixGenerationConfiguration) throws SQLException, ClassNotFoundException, IOException {
		Configuration configuration = matrixGenerationConfiguration.getConfiguration();
		Query query = new Query("DELETE FROM matrixgenerationconfigurations WHERE configuration = ?");
		query.setParameter(1, configuration.getId());
		query.executeAndClose();
	}


	
}
