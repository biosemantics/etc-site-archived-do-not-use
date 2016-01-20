package edu.arizona.biosemantics.etcsite.filemanager.shared.model.search;

public enum SearchTypeEnum {
	
	ELEMENTATTRIBUTEVALUES("Element-Attribute-Values"),
	ELEMENTVALUES("Element-Values"),
	ELEMENTS("Elements"),
	NUMERICALS("Numerical Values"),
	XPATH("XPath");

    private String displayName;

    private SearchTypeEnum(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() { 
    	return displayName; 
    }
    
    public static SearchTypeEnum getEnum(String value) {
        if(value == null)
            throw new IllegalArgumentException();
        for(SearchTypeEnum v : values())
            if(value.equals(v.displayName())) return v;
        throw new IllegalArgumentException();
    }
}
