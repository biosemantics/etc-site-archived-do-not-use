package edu.arizona.sirls.etc.site.client.view.users;

import java.util.List;
import java.util.Set;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;

import edu.arizona.sirls.etc.site.shared.rpc.db.ShortUser;

public class UsersViewImpl extends Composite implements UsersView {

	private static UsersViewUiBinder uiBinder = GWT.create(UsersViewUiBinder.class);

	@UiTemplate("UsersView.ui.xml")
	interface UsersViewUiBinder extends UiBinder<Widget, UsersViewImpl> {
	}
		
	@UiField(provided = true)
	CellList<ShortUser> cellList = new CellList<ShortUser>(new ShortUserCell());
	//@UiField
	//SuggestBox suggestBox;
	
	private Presenter presenter;
	private MultiSelectionModel<ShortUser> selectionModel;
	
	public UsersViewImpl() {
		initWidget(uiBinder.createAndBindUi(this));
		selectionModel = new MultiSelectionModel<ShortUser>();
		cellList.setSelectionModel(selectionModel);
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void setUsers(List<ShortUser> users) {
		cellList.setRowData(users);
	}

	@Override
	public Set<ShortUser> getSelectedUsers() {
		return selectionModel.getSelectedSet();
	}

}
