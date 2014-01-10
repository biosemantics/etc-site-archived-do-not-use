package edu.arizona.biosemantics.etcsite.client.content.user;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class UserSelectView extends Composite implements IUserSelectView {

	private static UserSelectViewUiBinder uiBinder = GWT.create(UserSelectViewUiBinder.class);

	@UiTemplate("UserSelectView.ui.xml")
	interface UserSelectViewUiBinder extends UiBinder<Widget, UserSelectView> {
	}
	
	@UiField(provided=true)
	IUsersView usersView;
	
	@UiField
	Button selectButton;
	private Presenter presenter;
	
	@Inject
	public UserSelectView(IUsersView usersView) {
		this.usersView = usersView;
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiHandler("selectButton")
	void onClick(ClickEvent e) {
		presenter.onSelect(usersView.getSelectedUsers());
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

}
