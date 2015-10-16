package edu.arizona.biosemantics.etcsitehelpcenter.client.layout;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsitehelp.shared.help.Help;
import edu.arizona.biosemantics.etcsitehelp.shared.rpc.help.IHelpServiceAsync;

public class EtcSiteHelpPresenter implements IEtcSiteHelpView.Presenter {

	private IEtcSiteHelpView view;
	private IHelpServiceAsync helpService;
	
	//private PlaceController placeController;

	@Inject
	public EtcSiteHelpPresenter(IEtcSiteHelpView view, IHelpServiceAsync helpService) {
		this.view = view;
		this.helpService = helpService;
		view.setPresenter(this);
		//this.placeController = placeController;
		
		//use help service to retrieve help contents
		helpService.getHelp(Help.WELCOME, new AsyncCallback<SafeHtml>() {

			@Override
			public void onFailure(Throwable caught) {
				
			}

			@Override
			public void onSuccess(SafeHtml result) {
				System.out.println(result.asString());
			}
			
		});
	}

	
	@Override
	public IEtcSiteHelpView getView() {
		return view;
	}

}
