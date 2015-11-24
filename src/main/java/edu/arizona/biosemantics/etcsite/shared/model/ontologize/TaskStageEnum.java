package edu.arizona.biosemantics.etcsite.shared.model.ontologize;

public enum TaskStageEnum {
	CREATE_INPUT("Create Input"),
    INPUT("Input"),
    BUILD("Build");
    //OUTPUT("Output");

    private String displayName;

    private TaskStageEnum(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() { 
    	return displayName; 
    }
}