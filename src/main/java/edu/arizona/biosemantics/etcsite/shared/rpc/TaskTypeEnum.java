package edu.arizona.biosemantics.etcsite.shared.rpc;

public enum TaskTypeEnum {
	SEMANTIC_MARKUP("Semantic Markup"),
	MATRIX_GENERATION("Matrix Generation"),
	TREE_GENERATION("ID Key Generation"),
	TAXONOMY_COMPARISON("Taxonomy Comparison"), 
	VISUALIZATION("Visualization");
	
	private String displayName;

	TaskTypeEnum(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() { 
    	return displayName; 
    }
}