package edu.arizona.biosemantics.etcsite.client.content.treeGeneration;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import edu.ucdavis.cs.cfgproject.client.KeyView;

public class TreeGenerationViewView extends Composite implements ITreeGenerationViewView, RequiresResize {

	private static TreeGenerationViewUiBinder uiBinder = GWT.create(TreeGenerationViewUiBinder.class);

	interface TreeGenerationViewUiBinder extends UiBinder<Widget, TreeGenerationViewView> {
	}

	private Presenter presenter;

	@UiField
	SimpleLayoutPanel cfgPanel;
	
	private KeyView keyView = new KeyView();
	
	@Inject
	public TreeGenerationViewView() {
		super();
		initWidget(uiBinder.createAndBindUi(this));
		cfgPanel.setWidget(keyView.asWidget());
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void onResize() {
		((RequiresResize)keyView).onResize();
	}

	@Override
	public KeyView getKeyView() {
		return keyView;
	}
}