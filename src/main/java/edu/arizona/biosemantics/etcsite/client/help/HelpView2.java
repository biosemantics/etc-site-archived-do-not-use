package edu.arizona.biosemantics.etcsite.client.help;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

public class HelpView2 extends HTML implements IHelpView {

	private Presenter presenter;

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void setContent(String html) {
		this.setHTML(html);
	}
}
