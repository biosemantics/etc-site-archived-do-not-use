package edu.arizona.sirls.etc.site.shared.rpc;

public enum TaskStageEnum {
	
	//commonly used
    INPUT("Input"),
    OUTPUT("Output"),
    
    //specific to semantic markup
    PREPROCESS_TEXT("Preprocess Text"),
    LEARN_TERMS("Learn Terms"),
    REVIEW_TERMS("Review Terms"),
    PARSE_TEXT("Parse Text"),
    
    //specific to matrix generation
    PROCESS("Process");

    private String displayName;

    private TaskStageEnum(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() { 
    	return displayName; 
    }
}
