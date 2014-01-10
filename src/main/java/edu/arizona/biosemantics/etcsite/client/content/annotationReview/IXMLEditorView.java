package edu.arizona.biosemantics.etcsite.client.content.annotationReview;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public interface IXMLEditorView extends IsWidget {

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
