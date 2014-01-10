package edu.arizona.biosemantics.etcsite.client.content.annotationReview;

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

import edu.arizona.biosemantics.etcsite.shared.file.search.SearchResult;

public class ResultView extends Composite implements IResultView {

	private static ResultViewUiBinder uiBinder = GWT.create(ResultViewUiBinder.class);

	@UiTemplate("ResultView.ui.xml")
	interface ResultViewUiBinder extends UiBinder<Widget, ResultView> {
	}
	
	@UiField
	StackLayoutPanel stackLayoutPanel;
	@UiField
    Label resultText;
	private Presenter presenter;
	private List<SearchResult> results;

	public ResultView() {
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
				matches += result.getFilePaths().size();
				files.addAll(result.getFilePaths());
				
				ScrollPanel scrollPanel = new ScrollPanel();
				VerticalPanel filesPanel = new VerticalPanel();
				for(final String target : result.getFilePaths()) {
					Anchor anchor = new Anchor(target);
					filesPanel.add(anchor);
					anchor.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							ResultView.this.presenter.onTargetClicked(target);
						}
					});
				}
				scrollPanel.add(filesPanel);
				Anchor headerAnchor = new Anchor(result.getCapturedMatch());
				headerAnchor.setTitle(result.getCapturedMatch());
				stackLayoutPanel.add(scrollPanel, headerAnchor, headerSize);
			}
			resultText.setText(matches + " matches found in " + files.size() + " files");
		}
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
	

}
