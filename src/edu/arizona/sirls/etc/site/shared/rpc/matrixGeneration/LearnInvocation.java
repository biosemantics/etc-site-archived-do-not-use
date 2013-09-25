package edu.arizona.sirls.etc.site.shared.rpc.matrixGeneration;

import java.io.Serializable;

public class LearnInvocation implements Serializable {

	private static final long serialVersionUID = -897176969621825980L;
	private int sentences;
	private int words;
	
	public LearnInvocation() {	}
	
	public LearnInvocation(int sentences, int words) {
		super();
		this.sentences = sentences;
		this.words = words;
	}
	public int getSentences() {
		return sentences;
	}
	public void setSentences(int sentences) {
		this.sentences = sentences;
	}
	public int getWords() {
		return words;
	}
	public void setWords(int words) {
		this.words = words;
	}
	
	
	
}
