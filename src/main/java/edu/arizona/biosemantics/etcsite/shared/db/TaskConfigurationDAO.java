package edu.arizona.biosemantics.etcsite.shared.db;

import java.io.IOException;
import java.sql.SQLException;

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
