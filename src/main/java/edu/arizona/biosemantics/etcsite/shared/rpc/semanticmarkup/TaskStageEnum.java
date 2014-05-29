package edu.arizona.biosemantics.etcsite.shared.rpc.semanticmarkup;

public enum TaskStageEnum {
	
    INPUT("Input"),
    PREPROCESS_TEXT("Preprocess Text"),
    LEARN_TERMS("Learn Terms"),
    REVIEW_TERMS("Review Terms"),
    PARSE_TEXT("Parse Text"),
    //TO_ONTOLOGIES("To Ontologies"), 
    //HIERARCHY("Hierarchy"), 
    //ORDERS("Orders"),
    OUTPUT("Output");

    private String displayName;

    private TaskStageEnum(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() { 
    	return displayName; 
    }
}
