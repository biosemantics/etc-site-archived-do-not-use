package edu.arizona.sirls.etc.site.shared.rpc;

public enum TaskType {
	MATRIX_GENERATION("Matrix Generation"),
	TREE_GENERATION("Tree Generation"),
	TAXONOMY_COMPARISON("Taxonomy Comparison"), 
	VISUALIZATION("Visualization");
	
	private String displayName;

	TaskType(String displayName) {
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