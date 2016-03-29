package edu.arizona.biosemantics.etcsite.client.content.matrixGeneration;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.matrixreview.client.MatrixReviewView;
import edu.arizona.biosemantics.matrixreview.shared.model.Model;

public interface IMatrixGenerationReviewView extends IsWidget {

	public interface Presenter {
		void onNext();
		IMatrixGenerationReviewView getView();
		void setTask(Task task);
		Model getTaxonMatrix();
		void onSave();
		boolean hasUnsavedChanges();
		void setUnsavedChanges(boolean value);
		void onExport();
	}
	  
	void setPresenter(Presenter presenter);
	Widget asWidget();
	MatrixReviewView getMatrixReviewView();
	void setFullModel(Model model);
	
}
