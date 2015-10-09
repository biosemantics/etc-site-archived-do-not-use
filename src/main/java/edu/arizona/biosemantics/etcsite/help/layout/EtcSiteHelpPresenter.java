package edu.arizona.biosemantics.etcsite.help.layout;

import com.google.inject.Inject;

public class EtcSiteHelpPresenter implements IEtcSiteHelpView.Presenter {

	private IEtcSiteHelpView view;
	//private PlaceController placeController;

	@Inject
	public EtcSiteHelpPresenter(IEtcSiteHelpView view ) {
		this.view = view;
		view.setPresenter(this);
		//this.placeController = placeController;
	}

	
	@Override
	public IEtcSiteHelpView getView() {
		return view;
	}

}
