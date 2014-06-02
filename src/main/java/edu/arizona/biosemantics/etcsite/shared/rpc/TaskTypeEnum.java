package edu.arizona.biosemantics.etcsite.shared.rpc;

public enum TaskTypeEnum {
	SEMANTIC_MARKUP("Text Capture"),
	MATRIX_GENERATION("Matrix Generation"),
	TREE_GENERATION("Key Generation"),
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