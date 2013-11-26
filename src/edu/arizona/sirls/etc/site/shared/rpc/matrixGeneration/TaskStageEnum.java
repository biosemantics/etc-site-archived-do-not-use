package edu.arizona.sirls.etc.site.shared.rpc.matrixGeneration;

public enum TaskStageEnum {
	
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
