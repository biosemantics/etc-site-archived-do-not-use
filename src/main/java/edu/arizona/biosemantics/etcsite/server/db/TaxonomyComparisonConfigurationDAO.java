package edu.arizona.biosemantics.etcsite.server.db;

import java.sql.ResultSet;
import java.sql.SQLException;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.etcsite.shared.model.Configuration;
import edu.arizona.biosemantics.etcsite.shared.model.TaxonomyComparisonConfiguration;

public class TaxonomyComparisonConfigurationDAO {
	
	private ConfigurationDAO configurationDAO;
	
	public void setConfigurationDAO(ConfigurationDAO configurationDAO) {
		this.configurationDAO = configurationDAO;
	}
	
	public TaxonomyComparisonConfiguration getTaxonomyComparisonConfiguration(int id) {
		TaxonomyComparisonConfiguration taxonomyComparisonConfiguration = null;
		try(Query query = new Query("SELECT * FROM etcsite_taxonomycomparisonconfigurations WHERE configuration = ?")) {
			query.setParameter(1, id);
			ResultSet result = query.execute();
			while(result.next()) {
				taxonomyComparisonConfiguration = createTaxonomyComparisonConfiguration(result);
			}
		} catch (Exception e) {
			log(LogLevel.ERROR, "Couldn't get taxonomy comparison configuration", e);
		}
		return taxonomyComparisonConfiguration;
	}

	private TaxonomyComparisonConfiguration createTaxonomyComparisonConfiguration(ResultSet result) throws SQLException {
		int configurationId = result.getInt(1);
		String input = result.getString(2);
		String output = result.getString(3);
		Configuration configuration = configurationDAO.getConfiguration(configurationId);
		return  new TaxonomyComparisonConfiguration(configuration, input, output);
	}

	
	public TaxonomyComparisonConfiguration addTaxonomyComparisonConfiguration(
			TaxonomyComparisonConfiguration taxonomyComparisonConfiguration) {
		TaxonomyComparisonConfiguration result = null;
		try (Query query = new Query("INSERT INTO `etcsite_configurations` (`id`) VALUES(NULL)")) {
			query.execute();
			ResultSet generatedKeys = query.getGeneratedKeys();
			while(generatedKeys.next()) {
				Configuration configuration = configurationDAO.getConfiguration(generatedKeys.getInt(1));
				try (Query taxonomyComparisonQuery = new Query("INSERT INTO `etcsite_taxonomycomparisonconfigurations` " +
						"(`configuration`, `input`, `output`) VALUES (?, ?, ?)")) {
					taxonomyComparisonQuery.setParameter(1, configuration.getId());
					taxonomyComparisonQuery.setParameter(2, taxonomyComparisonConfiguration.getInput());
					taxonomyComparisonQuery.setParameter(3, taxonomyComparisonConfiguration.getOutput());
					taxonomyComparisonQuery.execute();
				}
				result = this.getTaxonomyComparisonConfiguration(generatedKeys.getInt(1));
			}
		} catch(Exception e) {
			log(LogLevel.ERROR, "Couldn't insert taxonomy comparison configuration", e);
		}
		return result;
	}
	
	public void updateTaxonomyComparisonQueryConfiguration(TaxonomyComparisonConfiguration txonomyComparisonConfiguration) {
		Configuration configuration = txonomyComparisonConfiguration.getConfiguration();
		String input = txonomyComparisonConfiguration.getInput();
		String output = txonomyComparisonConfiguration.getOutput();
		try (Query query = new Query("UPDATE etcsite_taxonomycomparisonconfigurations SET input = ?, output = ?, "
				+ "WHERE configuration = ?")) {
			query.setParameter(1, input);
			query.setParameter(2, output);
			query.setParameter(5, configuration.getId());
			query.execute();
		} catch(Exception e) {
			log(LogLevel.ERROR, "Couldn't update taxonomy comparison configuration", e);
		}
	}

	public void remove(TaxonomyComparisonConfiguration taxonomyComparisonConfiguration) {
		Configuration configuration = taxonomyComparisonConfiguration.getConfiguration();
		try(Query query = new Query("DELETE FROM etcsite_taxonomycomparisonconfigurations WHERE configuration = ?")) {
			query.setParameter(1, configuration.getId());
			query.execute();
		} catch(Exception e) {
			log(LogLevel.ERROR, "Couldn't remove taxonomy comparison configuration", e);
		}
	}


}
