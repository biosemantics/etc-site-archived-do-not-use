package edu.arizona.biosemantics.etcsite.client.content.user;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorError;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.inject.Inject;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.Store.StoreSortInfo;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer.HorizontalLayoutData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.DualListField;
import com.sencha.gxt.widget.core.client.form.DualListField.DualListFieldAppearance;
import com.sencha.gxt.widget.core.client.form.DualListField.Mode;
import com.sencha.gxt.widget.core.client.form.Validator;

import edu.arizona.biosemantics.etcsite.client.common.Alerter;
import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.content.user.IUserSelectView.ISelectListener;
import edu.arizona.biosemantics.etcsite.shared.model.ShortUser;
import edu.arizona.biosemantics.etcsite.shared.model.ShortUserProperties;
import edu.arizona.biosemantics.etcsite.shared.rpc.user.IUserServiceAsync;

public class UserSelectForSharePresenter extends Dialog implements IUserSelectView.Presenter {
	
	/*public static class CustomDualListField<D, T> extends DualListField<D, T> {
		public static final int leftButton = 0x1;
		public static final int rightButton = 0x2;
		public static final int allLeftButton = 0x3;
		public static final int allRightButton = 0x4;
		public static final int upButton = 0x5;
		public static final int downButton = 0x6;

		List<Integer> buttonsToRemoveList;

		public CustomDualListField(ListStore<D> fromStore,
				ListStore<D> toStore,
				ValueProvider<? super D, T> valueProvider, Cell<T> cell, List<Integer> buttonsToRemoveList) {
			super(fromStore, toStore, valueProvider, cell);
			this.buttonsToRemoveList = buttonsToRemoveList;
			
		}

		@Override
		protected void onRender(Element target, int index) {
			super.onRend
			super.onRender(target, index);

			if (buttonsToRemoveList != null) {
				for (Integer val : buttonsToRemoveList) {
					switch (val.intValue()) {
					case leftButton: {
						buttonBar.remove(left);
						break;
					}
					case rightButton: {
						buttonBar.remove(right);
						break;
					}
					case allLeftButton: {
						buttonBar.remove(allLeft);
						break;
					}
					case allRightButton: {
						buttonBar.remove(allRight);
						break;
					}
					case upButton: {
						buttonBar.remove(up);
						break;
					}
					case downButton: {
						buttonBar.remove(down);
						break;
					}
					}
				}
			}
		}
	}*/

	private ISelectListener currentListener;
	private final ShortUserProperties shortUserProperties = GWT.create(ShortUserProperties.class);
	private ListStore<ShortUser> unselectedListStore = new ListStore<ShortUser>(shortUserProperties.key());
	private ListStore<ShortUser> selectedListStore = new ListStore<ShortUser>(shortUserProperties.key());
	private IUserServiceAsync userService;
	
	@Inject
	public UserSelectForSharePresenter(IUserServiceAsync userService) {
		this.userService = userService;
		setPredefinedButtons(PredefinedButton.OK);
		setHeading("Select Users");
		setBodyBorder(false);
		setPixelSize(-1, -1);
		setMinWidth(0);
		setMinHeight(0);
	    setResizable(true);
	    setShadow(true);
		setHideOnButtonClick(false);
		setWidth(500);
		setHeight(500);
		this.setPredefinedButtons(PredefinedButton.OK, PredefinedButton.CANCEL);
		getButton(PredefinedButton.OK).setText("Save Changes");
		
		
		//setBodyStyleName("pad-text");
		//getBody().addClassName("pad-text");
		//setHideOnButtonClick(false);
		//;
		
		selectedListStore.addSortInfo(new StoreSortInfo<ShortUser>(shortUserProperties.fullNameEmail(), SortDir.ASC));
		unselectedListStore.addSortInfo(new StoreSortInfo<ShortUser>(shortUserProperties.fullNameEmail(), SortDir.ASC));
		
		VerticalLayoutContainer vlc = new VerticalLayoutContainer();
		HorizontalLayoutContainer hlc = new HorizontalLayoutContainer();
		hlc.add(new Label("Available users"), new HorizontalLayoutData(0.5, 20));
		hlc.add(new Label(""), new HorizontalLayoutData(40, 20));
		hlc.add(new Label("Shared with"), new HorizontalLayoutData(0.5, 20));
		vlc.add(hlc, new VerticalLayoutData(1, 20));
		final DualListField<ShortUser, String> dualListField = new DualListField<ShortUser, String>(
				unselectedListStore, selectedListStore,
				shortUserProperties.fullNameEmail(), new TextCell());
		dualListField.setMode(Mode.APPEND);
		dualListField.addValidator(new Validator<List<ShortUser>>() {
			@Override
			public List<EditorError> validate(Editor<List<ShortUser>> editor, List<ShortUser> value) {
				/*if (value.size() <= 10) // || value.containsAll(ontologies) ||)
					return null;
				else {
					List<EditorError> errors = new ArrayList<EditorError>();
					// errors.add(new DefaultEditorError(editor,
					// "You have to select either all, or <= 10 ontologies.",
					// ""));
					errors.add(new DefaultEditorError(editor,
							"You can't select more than 10 ontologies.", ""));
					return errors;
				}*/
				return null;
			}
		});
		dualListField.setEnableDnd(true);
		vlc.add(dualListField, new VerticalLayoutData(1, 1));
		//add(dualListField);
		add(vlc);

		this.getButton(PredefinedButton.OK).addSelectHandler(
				new SelectHandler() {
					@Override
					public void onSelect(SelectEvent event) {
						if (dualListField.validate()) {
							if(currentListener != null)
								currentListener.onSelect(getSelectedUsers());
						}
					}
				});
		this.getButton(PredefinedButton.CANCEL).addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				hide();
			}
		});
	}
	
	protected Set<ShortUser> getSelectedUsers() {
		return new LinkedHashSet<ShortUser>(selectedListStore.getAll());
	}


	private void refreshUsers(final Set<ShortUser> selected) {
		//already store users, otherwise delay when requested on button press
		userService.getUsers(Authentication.getInstance().getToken(), false, 
				new AsyncCallback<List<ShortUser>>() {
			@Override
			public void onSuccess(List<ShortUser> result) {
				selectedListStore.clear();
				unselectedListStore.clear();
				unselectedListStore.addAll(result);
				
				for(ShortUser select : selected) {
					selectedListStore.add(select);
					unselectedListStore.remove(select);
				}
			}
			@Override
			public void onFailure(Throwable caught) {
				Alerter.failedToGetUsers(caught);
			}
		});
	}

	@Override
	public void show(ISelectListener listener, Set<ShortUser> selected) {
		refreshUsers(selected);
		this.currentListener = listener;
		show();
	}

}