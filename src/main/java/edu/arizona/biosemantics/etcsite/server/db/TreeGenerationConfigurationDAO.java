package edu.arizona.biosemantics.etcsite.server.db;

import java.sql.ResultSet;
import java.sql.SQLException;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.etcsite.shared.model.Configuration;
import edu.arizona.biosemantics.etcsite.shared.model.TreeGenerationConfiguration;

public class TreeGenerationConfigurationDAO {

	private ConfigurationDAO configurationDAO;
	
	public void setConfigurationDAO(ConfigurationDAO configurationDAO) {
		this.configurationDAO = configurationDAO;
	}
	
	public TreeGenerationConfiguration getTreeGenerationConfiguration(int configurationId) {
		TreeGenerationConfiguration treeGenerationConfiguration = null;
		try(Query query = new Query("SELECT * FROM etcsite_treegenerationconfigurations WHERE configuration = ?")) {
			query.setParameter(1, configurationId);
			ResultSet result = query.execute();
			while(result.next()) {
				treeGenerationConfiguration = createTreeGenerationConfiguration(result);
			}
		} catch (Exception e) {
			log(LogLevel.ERROR, "Couldn't get tree generation configuration", e);
		}
		return treeGenerationConfiguration;
	}

	private TreeGenerationConfiguration createTreeGenerationConfiguration(ResultSet result) throws SQLException {
		int configurationId = result.getInt(1);
		String input = result.getString(2);
		Configuration configuration = configurationDAO.getConfiguration(configurationId);
		return  new TreeGenerationConfiguration(configuration, input);
	}

	public TreeGenerationConfiguration addTreeGenerationConfiguration(TreeGenerationConfiguration treeGenerationConfiguration) {
		TreeGenerationConfiguration result = null;
		try (Query query = new Query("INSERT INTO `etcsite_configurations` (`id`) VALUES(NULL)")) {
			query.execute();
			ResultSet generatedKeys = query.getGeneratedKeys();
			while(generatedKeys.next()) {
				Configuration configuration = configurationDAO.getConfiguration(generatedKeys.getInt(1));
				try (Query treeGenerationQuery = new Query("INSERT INTO `etcsite_treegenerationconfigurations` " +
						"(`configuration`, `input`) VALUES (?, ?)")) {
					treeGenerationQuery.setParameter(1, configuration.getId());
					treeGenerationQuery.setParameter(2, treeGenerationConfiguration.getInput());
					treeGenerationQuery.execute();
				}
				result = this.getTreeGenerationConfiguration(generatedKeys.getInt(1));
			}
		} catch(Exception e) {
			log(LogLevel.ERROR, "Couldn't insert matrix generation configuration", e);
		}
		return result;
	}

	public void remove(TreeGenerationConfiguration treeGenerationConfiguration) {
		Configuration configuration = treeGenerationConfiguration.getConfiguration();
		try(Query query = new Query("DELETE FROM etcsite_treegenerationconfigurations WHERE configuration = ?")) {
			query.setParameter(1, configuration.getId());
			query.execute();
		} catch(Exception e) {
			log(LogLevel.ERROR, "Couldn't remove tree generation configuration", e);
		}
	}

}
