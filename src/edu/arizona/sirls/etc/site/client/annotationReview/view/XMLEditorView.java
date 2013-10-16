package edu.arizona.sirls.etc.site.client.annotationReview.view;

import com.google.gwt.user.client.ui.Widget;

import edu.arizona.sirls.etc.site.shared.rpc.file.search.Search;

public interface XMLEditorView {

  public interface Presenter {
    void onSaveButtonClicked();
    void onValidateButtonClicked();
	//void setTarget(Search search, String target);
	void setTarget(String target);
  }
  
  void setPresenter(Presenter presenter);
  void setText(String text);
  Widget asWidget();
  String getText();
  void setEnabled(boolean value);
}
