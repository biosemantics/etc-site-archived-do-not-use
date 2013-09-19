package edu.arizona.sirls.etc.site.shared.rpc;

import java.io.Serializable;

public class TaxonGlossaryFile implements Serializable, IHasPath {

	private static final long serialVersionUID = 8695707028513213243L;
	private String path = null;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public boolean hasPath() {
		return path != null;
	}
	
	
	
}
