package edu.arizona.sirls.etc.site.shared.rpc;

public enum TaskTypeEnum {
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