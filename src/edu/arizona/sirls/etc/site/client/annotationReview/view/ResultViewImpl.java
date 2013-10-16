package edu.arizona.sirls.etc.site.client.annotationReview.view;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.sirls.etc.site.shared.rpc.file.search.SearchResult;

public class ResultViewImpl extends Composite implements ResultView {

	private static ResultViewUiBinder uiBinder = GWT.create(ResultViewUiBinder.class);

	@UiTemplate("ResultView.ui.xml")
	interface ResultViewUiBinder extends UiBinder<Widget, ResultViewImpl> {
	}
	
	@UiField
	StackLayoutPanel stackLayoutPanel;
	@UiField
    Label resultText;
	private Presenter presenter;
	private List<SearchResult> results;

	public ResultViewImpl() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	//@UiHandler("stackLayoutPanel")
	//void onClick(ClickEvent e) {
		/*Object source = e.getSource();
		stackLayoutPanel.getWidgetIndex(source)
		 ??? */ // or init below on setresult the handlers
		//presenter.onTargetClicked("");
	//}
	
	@Override
	public void setResult(List<SearchResult> results) {
		stackLayoutPanel.clear();
		if(results == null) {
			resultText.setText("Invalid XPath expression");
		} else {
			int headerSize = 20;
			int contentViewSize = 200;
			stackLayoutPanel.setHeight((headerSize*results.size() + contentViewSize)+"px");
			this.results = results;
			int matches = 0;
			Set<String> files = new HashSet<String>();
			for(SearchResult result : results) {
				matches += result.getTargets().size();
				files.addAll(result.getTargets());
				
				ScrollPanel scrollPanel = new ScrollPanel();
				VerticalPanel filesPanel = new VerticalPanel();
				for(final String target : result.getTargets()) {
					Anchor anchor = new Anchor(target);
					filesPanel.add(anchor);
					anchor.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							ResultViewImpl.this.presenter.onTargetClicked(target);
						}
					});
				}
				scrollPanel.add(filesPanel);
				stackLayoutPanel.add(scrollPanel, new Label(result.getCapturedMatch()), 20);
			}
			resultText.setText(matches + " matches found in " + files.size() + " files");
		}
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
	

}