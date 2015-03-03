package edu.arizona.biosemantics.etcsite.client.content.user;

import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.shared.model.ShortUser;

public class UserSelectView extends Composite implements IUserSelectView {

	private static UserSelectViewUiBinder uiBinder = GWT.create(UserSelectViewUiBinder.class);

	@UiTemplate("UserSelectView.ui.xml")
	interface UserSelectViewUiBinder extends UiBinder<Widget, UserSelectView> {
	}
	
	@UiField(provided=true)
	IUsersView usersView;
	
	private Presenter presenter;
	
	@Inject
	public UserSelectView(IUsersView.Presenter usersPresenter) {
		this.usersView = usersPresenter.getView();
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public Set<ShortUser> getSelectedUsers() {
		return usersView.getSelectedUsers();	
	}

}
