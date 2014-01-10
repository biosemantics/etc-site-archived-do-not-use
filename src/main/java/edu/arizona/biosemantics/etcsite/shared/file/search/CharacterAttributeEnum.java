package edu.arizona.biosemantics.etcsite.shared.file.search;

public enum CharacterAttributeEnum {

	CHAR_TYPE("char_type"),
	CONSTRAINT("constraint"),
	CONSTRAINTID("constraintid"),
	FROM("from"),
	FROM_INCLUSIVE("from_inclusive"),
	FROM_UNIT("from_unit"),
	GEOGRAPHICAL_CONSTRAINT("geographical_constraint"),
	IN_BRACKETS("in_brackets"),
	MODIFIER("modifier"),
	NAME("name"),
	ORGAN_CONSTRAINT("organ_constraint"),
	OTHER_CONSTRAINT("other_constraint"),
	PARALLELISM_CONSTRAINT("parallelism_constraint"),
	TAXON_CONSTRAINT("taxon_constraint"),
	TO("to"),
	TO_INCLUSIVE("to_inclusive"),
	TO_UNIT("to_unit"),
	TYPE("type"),
	UNIT("unit"),
	UPPER_RESTRICTED("upper_restricted"),
	VALUE("value"),
	ONTOLOGY_ID("ontologyid"),
	PROVENANCE("provenance"),
	NOTES("notes");
	
    private String displayName;

    private CharacterAttributeEnum(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() { 
    	return displayName; 
    }
	
    public static CharacterAttributeEnum getEnum(String value) {
        if(value == null)
            throw new IllegalArgumentException();
        for(CharacterAttributeEnum v : values())
            if(value.equals(v.displayName())) return v;
        throw new IllegalArgumentException();
    }	
}
