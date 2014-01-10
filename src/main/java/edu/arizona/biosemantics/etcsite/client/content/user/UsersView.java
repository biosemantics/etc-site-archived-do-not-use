package edu.arizona.biosemantics.etcsite.client.content.user;

import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.ProvidesKey;

import edu.arizona.biosemantics.etcsite.shared.db.ShortUser;

public class UsersView extends Composite implements IUsersView {

	private static UsersViewUiBinder uiBinder = GWT.create(UsersViewUiBinder.class);

	@UiTemplate("UsersView.ui.xml")
	interface UsersViewUiBinder extends UiBinder<Widget, UsersView> {
	}
		
	@UiField(provided = true)
	CellList<ShortUser> usersList;
	//@UiField
	//SuggestBox suggestBox;
	
	private Presenter presenter;
	private MultiSelectionModel<ShortUser> selectionModel;
	private ListDataProvider<ShortUser> dataProvider;
	private ProvidesKey<ShortUser> userKeyProvider = new ProvidesKey<ShortUser>() {
		@Override
		public Object getKey(ShortUser item) {
			return item == null ? null : item.getId();
		}
	};
	
	public UsersView() {
		dataProvider = new ListDataProvider<ShortUser>();
		usersList = createUsersList();
	    dataProvider.addDataDisplay(usersList);	
		initWidget(uiBinder.createAndBindUi(this));
	}

	private CellList<ShortUser> createUsersList() {
		usersList = new CellList<ShortUser>(new ShortUserCell(), userKeyProvider);
		selectionModel = new MultiSelectionModel<ShortUser>(userKeyProvider);
		usersList.setSelectionModel(selectionModel);
	    return usersList;
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void setUsers(List<ShortUser> users) {
		List<ShortUser> usersList = dataProvider.getList();
		usersList.clear();
		usersList.addAll(users);
	}

	@Override
	public Set<ShortUser> getSelectedUsers() {
		return selectionModel.getSelectedSet();
	}

	@Override
	public void setSelectedUsers(Set<ShortUser> selectedUsers) {
		selectionModel.clear();
		for(ShortUser shortUser : selectedUsers) {
			selectionModel.setSelected(shortUser, true);
		}
	}

}
