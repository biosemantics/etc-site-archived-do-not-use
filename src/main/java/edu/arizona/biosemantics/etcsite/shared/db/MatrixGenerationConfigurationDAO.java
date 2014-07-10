package edu.arizona.biosemantics.etcsite.shared.db;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.eclipse.persistence.exceptions.QueryException;

public class MatrixGenerationConfigurationDAO {

	private static MatrixGenerationConfigurationDAO instance;

	public static MatrixGenerationConfigurationDAO getInstance() {
		if(instance == null)
			instance = new MatrixGenerationConfigurationDAO();
		return instance;
	}
	
	public MatrixGenerationConfiguration getMatrixGenerationConfiguration(int configurationId) {
		MatrixGenerationConfiguration matrixGenerationConfiguration = null;
		try(Query query = new Query("SELECT * FROM matrixgenerationconfigurations WHERE configuration = ?")) {
			query.setParameter(1, configurationId);
			ResultSet result = query.execute();
			while(result.next()) {
				matrixGenerationConfiguration = createMatrixGenerationConfiguration(result);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return matrixGenerationConfiguration;
	}

	private MatrixGenerationConfiguration createMatrixGenerationConfiguration(ResultSet result) throws SQLException {
		int configurationId = result.getInt(1);
		String input = result.getString(2);
		String output = result.getString(3);
		Configuration configuration = ConfigurationDAO.getInstance().getConfiguration(configurationId);
		return  new MatrixGenerationConfiguration(configuration, input, output);
	}

	public MatrixGenerationConfiguration addMatrixGenerationConfiguration(MatrixGenerationConfiguration matrixGenerationConfiguration) {
		MatrixGenerationConfiguration result = null;
		try (Query query = new Query("INSERT INTO `configurations` (`id`) VALUES(NULL)")) {
			query.execute();
			ResultSet generatedKeys = query.getGeneratedKeys();
			while(generatedKeys.next()) {
				Configuration configuration = ConfigurationDAO.getInstance().getConfiguration(generatedKeys.getInt(1));
				try (Query matrixGenerationQuery = new Query("INSERT INTO `matrixgenerationconfigurations` " +
						"(`configuration`, `input`, `output`) VALUES (?, ?, ?)")) {
					matrixGenerationQuery.setParameter(1, configuration.getId());
					matrixGenerationQuery.setParameter(2, matrixGenerationConfiguration.getInput());
					matrixGenerationQuery.setParameter(3, matrixGenerationConfiguration.getOutput());
					matrixGenerationQuery.execute();
				}
				result = this.getMatrixGenerationConfiguration(generatedKeys.getInt(1));
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public void updateMatrixGenerationConfiguration(MatrixGenerationConfiguration matrixGenerationConfiguration) {
		Configuration configuration = matrixGenerationConfiguration.getConfiguration();
		String input = matrixGenerationConfiguration.getInput();
		String output = matrixGenerationConfiguration.getOutput();
		try (Query query = new Query("UPDATE matrixgenerationconfigurations SET input = ?, output = ? WHERE configuration = ?")) {
			query.setParameter(1, input);
			query.setParameter(2, output);
			query.setParameter(3, configuration.getId());
			query.execute();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void remove(MatrixGenerationConfiguration matrixGenerationConfiguration) {
		Configuration configuration = matrixGenerationConfiguration.getConfiguration();
		try(Query query = new Query("DELETE FROM matrixgenerationconfigurations WHERE configuration = ?")) {
			query.setParameter(1, configuration.getId());
			query.execute();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}


	
}
