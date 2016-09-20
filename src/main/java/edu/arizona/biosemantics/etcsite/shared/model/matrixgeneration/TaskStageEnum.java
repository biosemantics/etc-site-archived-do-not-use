package edu.arizona.biosemantics.etcsite.shared.model.matrixgeneration;

public enum TaskStageEnum {
	CREATE_INPUT("Create/Select Input"),
    INPUT("Input"),
    PROCESS("Process"),
    REVIEW("Review"),
    OUTPUT("Output");

    private String displayName;

    private TaskStageEnum(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() { 
    	return displayName; 
    }
}
