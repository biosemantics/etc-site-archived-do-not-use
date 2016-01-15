package edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.form.ComboBox;

import edu.arizona.biosemantics.etcsite.client.content.treeGeneration.ITreeGenerationCreateView.Presenter;
import gwtupload.client.Uploader;

public class TaxonomyComparisonCreateView extends Composite implements ITaxonomyComparisonCreateView{

	private static TaxonomyComparisonCreateViewUiBinder uiBinder = GWT
			.create(TaxonomyComparisonCreateViewUiBinder.class);

	interface TaxonomyComparisonCreateViewUiBinder extends
			UiBinder<Widget, TaxonomyComparisonCreateView> {
	}

	private ITaxonomyComparisonCreateView.Presenter presenter;
	
	@UiField Anchor fileManagerAnchor;

	@UiField(provided=true)
	IInputCreateView inputCreateView;
	
	@Inject
	public TaxonomyComparisonCreateView(IInputCreateView.Presenter inputCreatePresenter) {		
		this.inputCreateView = inputCreatePresenter.getView();
		initWidget(uiBinder.createAndBindUi(this));		
		fileManagerAnchor.getElement().getStyle().setCursor(Cursor.POINTER);
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public IInputCreateView getInputCreateView() {
		return inputCreateView;
	}
	
}
