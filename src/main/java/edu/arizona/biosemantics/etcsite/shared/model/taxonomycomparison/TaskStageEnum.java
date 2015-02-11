package edu.arizona.biosemantics.etcsite.shared.model.taxonomycomparison;

public enum TaskStageEnum {
	
    INPUT("Input"),
    PROCESS("Process"),
    VIEW("View");

    private String displayName;

    private TaskStageEnum(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() { 
    	return displayName; 
    }
}

