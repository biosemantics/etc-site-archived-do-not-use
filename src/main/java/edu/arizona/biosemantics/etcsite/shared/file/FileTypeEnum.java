package edu.arizona.biosemantics.etcsite.shared.file;


public enum FileTypeEnum {	
	/*TAXON_DESCRIPTION("Taxon Description"),
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
    }*/
	
	TAXON_DESCRIPTION("Taxon Description", true),
	MARKED_UP_TAXON_DESCRIPTION("Marked Up Taxon Description", true),
	MATRIX("Matrix", true), 
	PLAIN_TEXT("Plain Text", true),
	DIRECTORY("Directory", false);
	
    private String displayName;
    private boolean viewable;

    private FileTypeEnum(String displayName, boolean viewable) {
        this.displayName = displayName;
        this.viewable = viewable;
    }

    public String displayName() { 
    	return displayName; 
    }
    
    public boolean isViewable() {
    	return viewable;
    }
    
    public static FileTypeEnum getEnum(String value) {
        if(value == null)
            throw new IllegalArgumentException();
        for(FileTypeEnum v : values())
            if(value.equals(v.displayName())) return v;
        throw new IllegalArgumentException();
    }	


}
