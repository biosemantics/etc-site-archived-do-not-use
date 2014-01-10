package edu.arizona.biosemantics.etcsite.shared.file.search;

public enum RelationAttributeEnum {

	ALTER_NAME("alter_name"),
	FROM("from"),
	GEOGRAPHICAL_CONSTRAINT("geographical_constraint"),
	ID("id"),
	IN_BRACKETS("in_brackets"),
	MODIFIER("modifier"),
	NAME("name"),
	NEGATION("negation"),
	ORGAN_CONSTRAINT("organ_constraint"),
	PARALLELISM_CONSTRAINT("parallelism_constraint"),
	TAXON_CONSTRAINT("taxon_constraint"),
	TO("to"),
	ONTOLOGY_ID("ontology_id"),
	PROVENANCE("provenance"),
	NOTES("notes");
	
    private String displayName;

    private RelationAttributeEnum(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() { 
    	return displayName; 
    }
    
    public static RelationAttributeEnum getEnum(String value) {
        if(value == null)
            throw new IllegalArgumentException();
        for(RelationAttributeEnum v : values())
            if(value.equals(v.displayName())) return v;
        throw new IllegalArgumentException();
    }
}
