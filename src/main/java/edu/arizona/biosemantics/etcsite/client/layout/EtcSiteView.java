package edu.arizona.biosemantics.etcsite.client.layout;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.Composite;

import edu.arizona.biosemantics.etcsite.client.common.ImageLabel;

public class EtcSiteView extends Composite implements IEtcSiteView {

	private static EtcSiteUiBinder uiBinder = GWT.create(EtcSiteUiBinder.class);

	interface EtcSiteUiBinder extends UiBinder<Widget, EtcSiteView> {
	}
	
	@UiField
	VerticalPanel eastPanel;
	
	@UiField
	ScrollPanel helpPanel;
	
	@UiField
	FocusPanel navigationPanel;
	
	@UiField
	SimpleLayoutPanel contentPanel;

	@UiField
	DockLayoutPanel dockLayoutPanel;
	
	@UiField
	ImageLabel loginLogout;
	
	@UiField
	ImageLabel taskManager;
		
	private Presenter presenter;
	
	public EtcSiteView() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	@Override
	public void setContent(IsWidget content) {
		contentPanel.setWidget(content);
	}
	
	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public SimpleLayoutPanel getContentContainer() {
		return this.contentPanel;
	}
	
	public ScrollPanel getHelpContainer() {
		return helpPanel;
	}

	@Override
	public void setNavigationSize(int size, boolean animated) {
		//dockLayoutPanel.forceLayout(); //makes fast mouse movement not to collapse the menu without animation (for some reason)
		dockLayoutPanel.setWidgetSize(navigationPanel, size);
		if(animated)
			dockLayoutPanel.animate(300);
	}
	
	@Override
	public void setHelpSize(int size, boolean animated) {
		//dockLayoutPanel.forceLayout(); //makes fast mouse movement not to collapse the menu without animation (for some reason)
		dockLayoutPanel.setWidgetSize(eastPanel, size);
		if(animated)
			dockLayoutPanel.animate(300);
	}
	
	
	
	
	/*@UiHandler("menu")
	void onMenuClick(ClickEvent e) {
		Double size = dockLayoutPanel.getWidgetSize(navigationPanel);
		if(size == 200) 
			setNavigationSize(0, true);
		else
			setNavigationSize(200, true);
	}*/
	
	@UiHandler("loginLogout")
	public void onLoginLogout(ClickEvent e) {
		presenter.onLoginLogout();
	}
	
	@UiHandler("navigationPanel") 
	public void onMouseOverMenu(MouseOverEvent event) { 
		setNavigationSize(200, true);
	}
	
	@UiHandler("navigationPanel") 
	public void onMouseOutMenu(MouseOutEvent event) {
		setNavigationSize(0, true);
	}
	
	@UiHandler("menu") 
	public void onMouseOverMenuIcon(MouseOverEvent event) { 
		setNavigationSize(200, true);
	}
	
	@UiHandler("menu") 
	public void onMouseOutMenuIcon(MouseOutEvent event) {
		setNavigationSize(0, true);
	}
	
	@UiHandler("help")
	void onHelpClick(ClickEvent e) {
		/* Disabled for now
		Double size = dockLayoutPanel.getWidgetSize(eastPanel);
		if(size == 400) 
			setHelpSize(0, true);
		else
			setHelpSize(400, true);*/
	}
	
	@UiHandler("homeMenu")
	void onHomeMenuClick(ClickEvent e) {
		presenter.onHome();
	}
	
	@UiHandler("about")
	void onAboutClick(ClickEvent e) {
		presenter.onAbout();
	}
	
	@UiHandler("news")
	void onNewsClick(ClickEvent e) {
		presenter.onNews();
	}
	
	@UiHandler("taskManager")
	void onTaskManagerClick(ClickEvent e) {
		presenter.onTaskManager();
	}
	
	@UiHandler("fileManager")
	void onFileManagerClick(ClickEvent e) {
		presenter.onFileManager();
	}
	
	@UiHandler("account")
	void onAccountClick(ClickEvent e) {
		presenter.onAccount();
	}
	
	@UiHandler("textCapture")
	void onTextCaptureClick(ClickEvent e) {
		presenter.onTextCapture();
	}
	
	@UiHandler("matrixGeneration")
	void onMatrixGenerationClick(ClickEvent e) {
		presenter.onMatrixGeneration();
	}
	
	@UiHandler("treeGeneration")
	void onTreeGenerationClick(ClickEvent e) {
		presenter.onTreeGeneration();
	}
	
	@UiHandler("taxonomyComparison")
	void onTaxonomyComparisonClick(ClickEvent e) {
		presenter.onTaxonomyComparison();
	}
	
	@UiHandler("visualization")
	void onVisualizationClick(ClickEvent e) {
		presenter.onVisualization();
	}
	
	@UiHandler("helpButton") 
	void onHelpButtonClick(ClickEvent e) {
		presenter.onOpenHelpInNewWindow();
	}

	@Override
	public void setLogin() {
		loginLogout.setImage("images/login.gif");
	}

	@Override
	public void setLogout() {
		loginLogout.setImage("images/logout.gif");
	}

	@Override
	public boolean isLogin() {
		return loginLogout.getImage().endsWith("images/login.gif");
	}

	@Override
	public boolean isLogout() {
		return loginLogout.getImage().endsWith("images/logout.gif");
	}

	@Override
	public void setResumableTasks(boolean value) {
		if(value)
			taskManager.setImage("images/TaskManager_notification.gif");
		else
			taskManager.setImage("images/TaskManager.gif");
	}
	
}