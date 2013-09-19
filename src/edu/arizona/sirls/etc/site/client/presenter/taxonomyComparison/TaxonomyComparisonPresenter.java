package edu.arizona.sirls.etc.site.client.presenter.taxonomyComparison;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.sirls.etc.site.client.presenter.Presenter;

public class TaxonomyComparisonPresenter implements Presenter {

	public interface Display {
		Widget asWidget();
	}

	private Display display;

	public TaxonomyComparisonPresenter(HandlerManager eventBus,
			Display display) {
		this.display = display;
	}

	@Override
	public void go(HasWidgets content) {
		content.clear();
		content.add(display.asWidget());
	}

}
