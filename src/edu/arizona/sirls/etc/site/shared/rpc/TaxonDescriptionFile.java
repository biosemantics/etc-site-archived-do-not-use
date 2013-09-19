package edu.arizona.sirls.etc.site.shared.rpc;

import java.io.Serializable;

public class TaxonDescriptionFile implements Serializable, IHasPath {

	private static final long serialVersionUID = 6700880527740029700L;
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
