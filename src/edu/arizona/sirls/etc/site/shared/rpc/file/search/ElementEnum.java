package edu.arizona.sirls.etc.site.shared.rpc.file.search;

public enum ElementEnum {

	
	STRUCTURE("structure"),
	CHARACTER("character"),
	RELATION("relation"),
	TEXT("text");
	
    private String displayName;

    private ElementEnum(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() { 
    	return displayName; 
    }
    
    public static ElementEnum getEnum(String value) {
        if(value == null)
            throw new IllegalArgumentException();
        for(ElementEnum v : values())
            if(value.equals(v.displayName())) return v;
        throw new IllegalArgumentException();
    }
	
}
