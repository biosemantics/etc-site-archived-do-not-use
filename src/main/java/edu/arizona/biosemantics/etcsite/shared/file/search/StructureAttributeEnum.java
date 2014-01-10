package edu.arizona.biosemantics.etcsite.shared.file.search;

public enum StructureAttributeEnum {

	ALTER_NAME("alter_name"),
	CONSTRAINT("constraint"),
	CONSTRAINT_ID("constraint_id"),
	GEOGRAPHICAL_CONSTRAINT("geographical_constraint"),
	ID("id"),
	IN_BRACKET("in_bracket"),
	IN_BRACKETS("in_brackets"),
	NAME("name"),
	PARALLELISM_CONSTRAINT("parallelism_constraint"),
	TAXON_CONSTRAINT("taxon_constraint"),
	ONTOLOGY_ID("ontology_id"),
	PROVENANCE("provenance"),
	NOTES("notes");
	
    private String displayName;

    private StructureAttributeEnum(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() { 
    	return displayName; 
    }
    
    public static StructureAttributeEnum getEnum(String value) {
        if(value == null)
            throw new IllegalArgumentException();
        for(StructureAttributeEnum v : values())
            if(value.equals(v.displayName())) return v;
        throw new IllegalArgumentException();
    }
    
}
