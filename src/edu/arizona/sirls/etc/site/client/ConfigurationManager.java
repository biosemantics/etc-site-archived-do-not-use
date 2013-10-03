package edu.arizona.sirls.etc.site.client;

import edu.arizona.sirls.etc.site.shared.rpc.db.MatrixGenerationConfiguration;

public class ConfigurationManager {

	private static ConfigurationManager instance;

	public static ConfigurationManager getInstance() {
		if(instance == null)
			instance = new ConfigurationManager();
		return instance;
	}
	
	private ConfigurationManager() { }
	
	private MatrixGenerationConfiguration matrixGenerationConfiguration;

	public boolean hasMatrixGenerationConfiguration() {
		return this.matrixGenerationConfiguration != null;
	}
	
	public MatrixGenerationConfiguration getMatrixGenerationConfiguration() {
		return matrixGenerationConfiguration;
	}

	public void setMatrixGenerationConfiguration(MatrixGenerationConfiguration matrixGenerationConfiguration) {
		this.matrixGenerationConfiguration = matrixGenerationConfiguration;
	}

	public void newMatrixGenerationConfiguration() {
		matrixGenerationConfiguration = new MatrixGenerationConfiguration();
	}

	public void removeMatrixGenerationConfiguration() {
		this.matrixGenerationConfiguration = null;
	}
	
}
