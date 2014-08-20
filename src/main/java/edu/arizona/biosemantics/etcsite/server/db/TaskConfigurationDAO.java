package edu.arizona.biosemantics.etcsite.server.db;

import java.io.IOException;
import java.sql.SQLException;

import edu.arizona.biosemantics.etcsite.shared.model.AbstractTaskConfiguration;
import edu.arizona.biosemantics.etcsite.shared.model.Configuration;

public class TaskConfigurationDAO {

	private static TaskConfigurationDAO instance;

	public static TaskConfigurationDAO getInstance() {
		if(instance == null)
			instance = new TaskConfigurationDAO();
		return instance;
	}
	
	public AbstractTaskConfiguration getTaskConfiguration(Configuration configuration) throws ClassNotFoundException, SQLException, IOException {
		AbstractTaskConfiguration taskConfiguration = 
				SemanticMarkupConfigurationDAO.getInstance().getSemanticMarkupConfiguration(configuration.getId());
		if(taskConfiguration == null)
			taskConfiguration =	TreeGenerationConfigurationDAO.getInstance().getTreeGenerationConfiguration(configuration.getId());
		if(taskConfiguration == null)
			taskConfiguration =	TaxonomyComparisonConfigurationDAO.getInstance().getTaxonomyComparisonConfiguration(configuration.getId());
		if(taskConfiguration == null)
			taskConfiguration =	VisualizationConfigurationDAO.getInstance().getVisualizationConfiguration(configuration.getId());		
		return taskConfiguration;
	}
	
}
