package edu.arizona.sirls.etc.site.shared.rpc.semanticMarkup;

import java.io.Serializable;

public class LearnInvocation implements Serializable {

	private static final long serialVersionUID = -897176969621825980L;
	private String sentences;
	private String words;
	
	public LearnInvocation() {	}
	
	public LearnInvocation(String sentences, String words) {
		super();
		this.sentences = sentences;
		this.words = words;
	}
	public String getSentences() {
		return sentences;
	}
	public void setSentences(String sentences) {
		this.sentences = sentences;
	}
	public String getWords() {
		return words;
	}
	public void setWords(String words) {
		this.words = words;
	}
	
	
	
}
