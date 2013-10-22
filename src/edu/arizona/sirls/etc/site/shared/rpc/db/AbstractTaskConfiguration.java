package edu.arizona.sirls.etc.site.shared.rpc.db;

import java.io.Serializable;

public abstract class AbstractTaskConfiguration implements Serializable {

	private static final long serialVersionUID = -5525334891246592457L;
	private Configuration configuration;

	public AbstractTaskConfiguration() { }
	
	public AbstractTaskConfiguration(Configuration configuration) {
		super();
		this.configuration = configuration;
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}
	
	
	
}
