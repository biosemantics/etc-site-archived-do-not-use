package edu.arizona.biosemantics.etcsite.shared.model.taxonomycomparison;

public enum TaskStageEnum {
	
    INPUT("Input"),
    ALIGN("Align"), 
    ANALYZE("Analyze"), 
    ANALYZE_COMPLETE("Analyze Complete");

    private String displayName;

    private TaskStageEnum(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() { 
    	return displayName; 
    }
}

