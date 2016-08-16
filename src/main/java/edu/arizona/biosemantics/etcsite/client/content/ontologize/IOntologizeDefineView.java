package edu.arizona.biosemantics.etcsite.client.content.ontologize;

import com.google.gwt.user.client.ui.IsWidget;

import edu.arizona.biosemantics.common.biology.TaxonGroup;

public interface IOntologizeDefineView extends IsWidget {

	public interface Presenter {
		void onNext();
		IOntologizeDefineView getView();
		void setSelectedFolder(String fullPath, String shortendPath);
		void onInputSelect();
		//void onOntologySelect();
	}
	  
	void setPresenter(Presenter presenter);
	String getTaskName();
	void setFilePath(String shortendPath);
	void setEnabledNext(boolean b);
	void resetFields();
	//boolean isSelectOntology();
	String getTaxonGroup();
	//boolean isCreateOntology();
	//void setOntologyFilePath(String shortendPath);
}
