package edu.arizona.sirls.etc.site.shared.rpc;

public enum FileType {	
	TAXON_DESCRIPTION("Taxon Description"),
	GLOSSARY("Glossary"),
	EULER("Euler");

    private String displayName;

    private FileType(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() { 
    	return displayName; 
    }

    @Override 
    public String toString() { 
    	return displayName; 
    }
}
