package edu.arizona.sirls.etc.site.client.view.users;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class UserSelectViewImpl extends Composite implements UserSelectView {

	private static UserSelectViewUiBinder uiBinder = GWT.create(UserSelectViewUiBinder.class);

	@UiTemplate("UserSelectView.ui.xml")
	interface UserSelectViewUiBinder extends UiBinder<Widget, UserSelectViewImpl> {
	}
	
	@UiField(provided=true)
	UsersViewImpl usersView;
	
	@UiField
	Button selectButton;
	private Presenter presenter;
	
	public UserSelectViewImpl(UsersViewImpl usersView) {
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
