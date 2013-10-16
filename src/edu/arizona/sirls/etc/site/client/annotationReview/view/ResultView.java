package edu.arizona.sirls.etc.site.client.annotationReview.view;

import java.util.List;

import com.google.gwt.user.client.ui.Widget;

import edu.arizona.sirls.etc.site.shared.rpc.file.search.SearchResult;

public interface ResultView {
	
	  public interface Presenter {
		void onTargetClicked(String target);
	  }
	  
	  void setPresenter(Presenter presenter);
	  void setResult(List<SearchResult> result);
	  Widget asWidget();

}
