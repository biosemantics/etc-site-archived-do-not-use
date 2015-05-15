package edu.arizona.biosemantics.etcsite.shared.model.treegeneration;

public enum TaskStageEnum {
	CREATE_INPUT("Create Input"),
    INPUT("Input"),
    VIEW("View");

    private String displayName;

    private TaskStageEnum(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() { 
    	return displayName; 
    }
}

