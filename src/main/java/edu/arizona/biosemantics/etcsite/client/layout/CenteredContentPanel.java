package edu.arizona.biosemantics.etcsite.client.layout;

import java.util.Iterator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class CenteredContentPanel extends Composite implements AcceptsOneWidget, HasWidgets, HasWidgets.ForIsWidget, java.lang.Iterable<Widget> {

	private static CenteredPanelUiBinder uiBinder = GWT
			.create(CenteredPanelUiBinder.class);

	interface CenteredPanelUiBinder extends UiBinder<Widget, CenteredContentPanel> {
	}

	@UiField
	SimplePanel centeredContentPanel;
	
	public CenteredContentPanel() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void add(IsWidget w) {
		centeredContentPanel.add(w);
	}

	@Override
	public boolean remove(IsWidget w) {
		return centeredContentPanel.remove(w);
	}

	@Override
	public void add(Widget w) {
		centeredContentPanel.add(w);
	}

	@Override
	public void clear() {
		centeredContentPanel.clear();
	}

	@Override
	public Iterator<Widget> iterator() {
		return centeredContentPanel.iterator();
	}

	@Override
	public boolean remove(Widget w) {
		return centeredContentPanel.remove(w);
	}

	@Override
	public void setWidget(IsWidget w) {
		centeredContentPanel.setWidget(w);
	}

}