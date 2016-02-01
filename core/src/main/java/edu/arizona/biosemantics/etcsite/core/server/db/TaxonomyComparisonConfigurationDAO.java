package edu.arizona.biosemantics.etcsite.core.server.db;

import java.sql.ResultSet;
import java.sql.SQLException;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.etcsite.core.shared.model.Configuration;
import edu.arizona.biosemantics.etcsite.core.shared.model.taxonomycomparison.TaxonomyComparisonConfiguration;

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
		String cleanTaxInput = result.getString(2);
		String modelInput1 = result.getString(3);
		String modelInput2 = result.getString(4);
		String output = result.getString(3);
		Configuration configuration = configurationDAO.getConfiguration(configurationId);
		return new TaxonomyComparisonConfiguration(configuration, cleanTaxInput, modelInput1, modelInput2, output);
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
						"(`configuration`, `cleantax_input`, `model_input1`, `model_input2`, `output`) VALUES (?, ?, ?, ?, ?)")) {
					taxonomyComparisonQuery.setParameter(1, configuration.getId());
					taxonomyComparisonQuery.setParameter(2, taxonomyComparisonConfiguration.getCleanTaxInput());
					taxonomyComparisonQuery.setParameter(3, taxonomyComparisonConfiguration.getModelInput1());
					taxonomyComparisonQuery.setParameter(4, taxonomyComparisonConfiguration.getModelInput2());
					taxonomyComparisonQuery.setParameter(5, taxonomyComparisonConfiguration.getOutput());
					taxonomyComparisonQuery.execute();
				}
				result = this.getTaxonomyComparisonConfiguration(generatedKeys.getInt(1));
			}
		} catch(Exception e) {
			log(LogLevel.ERROR, "Couldn't insert taxonomy comparison configuration", e);
		}
		return result;
	}
	
	public void updateTaxonomyComparisonQueryConfiguration(TaxonomyComparisonConfiguration taxonomyComparisonConfiguration) {
		Configuration configuration = taxonomyComparisonConfiguration.getConfiguration();
		String cleanTaxInput = taxonomyComparisonConfiguration.getCleanTaxInput();
		String modelInput1 = taxonomyComparisonConfiguration.getModelInput1();
		String modelInput2 = taxonomyComparisonConfiguration.getModelInput2();
		String output = taxonomyComparisonConfiguration.getOutput();
		try (Query query = new Query("UPDATE etcsite_taxonomycomparisonconfigurations SET cleantax_input = ?, model_input1 = ?, model_input2 = ?, output = ?, "
				+ "WHERE configuration = ?")) {
			query.setParameter(1, cleanTaxInput);
			query.setParameter(2, modelInput1);
			query.setParameter(3, modelInput2);
			query.setParameter(4, output);
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
