package edu.arizona.sirls.etc.site.client.view.matrixGeneration;

import java.util.List;
import java.util.Set;

import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.sirls.etc.site.shared.rpc.db.ShortUser;

public interface InputMatrixGenerationView {

	public interface Presenter {
		void onNext(String taskName, String input);
		void onInputSelect();
		void onFileManager();
	}
	  
	void setPresenter(Presenter presenter);
	Widget asWidget();
	HasEnabled getNextButton();
	HasText getInputLabel();

}
