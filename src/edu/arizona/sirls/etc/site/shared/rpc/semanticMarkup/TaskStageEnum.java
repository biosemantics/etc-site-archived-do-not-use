package edu.arizona.sirls.etc.site.shared.rpc.semanticMarkup;

public enum TaskStageEnum {
	
    INPUT("Input"),
    PREPROCESS_TEXT("Preprocess Text"),
    LEARN_TERMS("Learn Terms"),
    REVIEW_TERMS("Review Terms"),
    PARSE_TEXT("Parse Text"),
    OUTPUT("Output");

    private String displayName;

    private TaskStageEnum(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() { 
    	return displayName; 
    }
}
