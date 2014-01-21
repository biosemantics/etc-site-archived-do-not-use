package edu.arizona.biosemantics.otolite.client.rpc;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.biosemantics.otolite.shared.beans.terminfo.TermContext;
import edu.arizona.biosemantics.otolite.shared.beans.terminfo.TermDictionary;
import edu.arizona.biosemantics.otolite.shared.beans.terminfo.TermGlossary;

public interface TermInfoServiceAsync {

	void getTermContext(String term, String uploadID,
			AsyncCallback<ArrayList<TermContext>> callback);

	void getTermGlossary(String term, String glossaryType,
			AsyncCallback<ArrayList<TermGlossary>> callback);

	void getTermDictionary(String term, AsyncCallback<TermDictionary> callback);

	void getFileContent(String uploadID, String sourceName,
			AsyncCallback<String> callback);

}
