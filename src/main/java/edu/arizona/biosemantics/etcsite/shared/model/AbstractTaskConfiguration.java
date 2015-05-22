package edu.arizona.biosemantics.etcsite.shared.model;

import java.io.Serializable;
import java.util.List;

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
	
	public abstract List<String> getInputs();
	
	public abstract List<String> getOutputs();
	
	@Override
	public int hashCode() {
		if(configuration != null)
			return configuration.getId();
		return super.hashCode();
	}
	
	@Override
	public boolean equals(Object object) {
		if(object == null)
			return false;
		if (getClass() != object.getClass()) {
	        return false;
	    }
		AbstractTaskConfiguration abstractTaskConfiguration = (AbstractTaskConfiguration)object;
		if(abstractTaskConfiguration.getConfiguration().getId()==this.getConfiguration().getId())
			return true;
		return false;
	}
	
}
