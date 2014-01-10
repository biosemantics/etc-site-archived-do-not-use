package edu.arizona.biosemantics.etcsite.client.content.annotationReview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.DragEndEvent;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.biosemantics.etcsite.shared.file.search.CharacterAttributeEnum;
import edu.arizona.biosemantics.etcsite.shared.file.search.ElementAttributeValuesSearch;
import edu.arizona.biosemantics.etcsite.shared.file.search.ElementEnum;
import edu.arizona.biosemantics.etcsite.shared.file.search.ElementValuesSearch;
import edu.arizona.biosemantics.etcsite.shared.file.search.ElementsSearch;
import edu.arizona.biosemantics.etcsite.shared.file.search.NumericalsSearch;
import edu.arizona.biosemantics.etcsite.shared.file.search.RelationAttributeEnum;
import edu.arizona.biosemantics.etcsite.shared.file.search.Search;
import edu.arizona.biosemantics.etcsite.shared.file.search.SearchTypeEnum;
import edu.arizona.biosemantics.etcsite.shared.file.search.StructureAttributeEnum;
import edu.arizona.biosemantics.etcsite.shared.file.search.XPathSearch;

public class SearchView extends Composite implements ISearchView {

	private static SearchUiBinder uiBinder = GWT.create(SearchUiBinder.class);

	@UiTemplate("SearchView.ui.xml")
	interface SearchUiBinder extends UiBinder<Widget, SearchView> {
	}

	
	@UiField
    Label inputLabel;
    @UiField
    Button inputButton;
    @UiField
    Button searchButton;
    @UiField
    ListBox typeListBox;
    @UiField
    Label typeLabel;
    @UiField
    TextBox valueTextBox;
    @UiField
    Label valueTextBoxLabel;
    @UiField
    TextBox xpathTextBox;
    @UiField
    Label xpathTextBoxLabel;
    @UiField
    ListBox elementListBox;
    @UiField
    Label elementListBoxLabel;
    @UiField
    ListBox attributeListBox;
    @UiField
    Label attributeListBoxLabel;
    @UiField
    Label xpathLabel;


	private Presenter presenter;
	private boolean enabled;

	public SearchView() {
		initWidget(uiBinder.createAndBindUi(this));
		
        initTypeListBox(); 
        initElementListBox();
        DomEvent.fireNativeEvent(Document.get().createChangeEvent(), typeListBox);
	}
	
	
    @UiHandler("inputButton")
    void onInputClick(ClickEvent event) {
    	presenter.onInputButtonClicked();
    }
   

	@UiHandler("searchButton")
    void onSearchClick(ClickEvent event) {
		Search search = getCurrentSearch(); // new XPathSearch(xpathTextBox.getValue());
    	presenter.onSearchButtonClicked(search);
    }
	
    private Search getCurrentSearch() {
    	SearchTypeEnum searchTypeEnum = SearchTypeEnum.getEnum(typeListBox.getValue(typeListBox.getSelectedIndex()));		
    	String element = elementListBox.getItemText(elementListBox.getSelectedIndex());
    	String value = valueTextBox.getText();
    	String xpath = xpathTextBox.getText();
    	Search search = null;
    	switch(searchTypeEnum) {
		case ELEMENTATTRIBUTEVALUES:
			//text does not have attributes, hence attributelistbox is empty and nothing selected
			if(attributeListBox.getSelectedIndex() == -1)
				break;
			String attribute = attributeListBox.getItemText(attributeListBox.getSelectedIndex());
			search = new ElementAttributeValuesSearch(element, attribute, value);
			break;
		case ELEMENTS:
			search = new ElementsSearch(element);
			break;
		case ELEMENTVALUES:
			search = new ElementValuesSearch(element, value);
			break;
		case NUMERICALS:
			search = new NumericalsSearch();
			break;
		case XPATH:
			search = new XPathSearch(xpath);
			break;
		default:
			return null;
    	}
    	return search;
	}

	@UiHandler("elementListBox")
    void onElementChangeEvent(ChangeEvent event) {
    	initAttributeListBox();
    	updateXPath();
    }
	
	@UiHandler("attributeListBox")
    void onAttributeChangeEvent(ChangeEvent event) {
    	updateXPath();
    }
	
	@UiHandler("valueTextBox")
	void onValueChangeEvent(KeyPressEvent event) {
		updateXPath();
	}
	
	@UiHandler("valueTextBox")
	void onValueChangeEvent(KeyUpEvent event) {
		updateXPath();
	}
	
	@UiHandler("valueTextBox")
	void onValueChangeEvent(KeyDownEvent event) {
		updateXPath();
	}
	
	/*
	@UiHandler("valueTextBox")
	void onValueChangeEvent(MouseUpEvent event) {
		updateXPath();
	}*/
	
	@UiHandler("valueTextBox")
	void onValueChangeEvent(DragEndEvent event) {
		updateXPath();
	}
    
    private void updateXPath() {
    	if(isEnabled()) {
	    	Search search = getCurrentSearch();
	    	this.xpathLabel.setText(search.getXPath());
	    	this.xpathTextBox.setText(search.getXPath());
    	}
	}	

	@UiHandler("typeListBox")
    void onTypeChangeEvent(ChangeEvent event) {
    	SearchTypeEnum searchTypeEnum = SearchTypeEnum.getEnum(typeListBox.getValue(typeListBox.getSelectedIndex()));
    	this.typeLabel.setText(searchTypeEnum.displayName() + ":");
    	switch(searchTypeEnum) {
		case ELEMENTATTRIBUTEVALUES:
			createElementAttributeValuesSearch();
			initAttributeListBox();
			break;
		case ELEMENTVALUES:
			createElementValuesSearch();
			break;
		case XPATH:
			createXPathSearch();
			break;
		case ELEMENTS:
			createElementsSearch();
			break;
		case NUMERICALS:
			createNumericalsSearch();
			break;
		default:
			break;
    	}
    	updateXPath();
    }
    
	private void createXPathSearch() {
		xpathTextBox.setVisible(true);
		xpathTextBox.setEnabled(true);
		valueTextBoxLabel.setVisible(false);
		valueTextBox.setVisible(false);
		elementListBoxLabel.setVisible(false);
		elementListBox.setVisible(false);
		attributeListBoxLabel.setVisible(false);
		attributeListBox.setVisible(false);
	}
	
	private void createElementAttributeValuesSearch() {
		xpathTextBoxLabel.setVisible(false);
		xpathTextBox.setVisible(false);
		xpathTextBox.setEnabled(false);
		valueTextBoxLabel.setVisible(true);
		valueTextBox.setVisible(true);
		elementListBoxLabel.setVisible(true);
		elementListBox.setVisible(true);
		attributeListBoxLabel.setVisible(true);
		attributeListBox.setVisible(true);
	}
	
	private void createElementsSearch() {
		xpathTextBoxLabel.setVisible(false);
		xpathTextBox.setVisible(false);
		xpathTextBox.setEnabled(false);
		valueTextBoxLabel.setVisible(false);
		valueTextBox.setVisible(false);
		elementListBoxLabel.setVisible(true);
		elementListBox.setVisible(true);
		attributeListBoxLabel.setVisible(false);
		attributeListBox.setVisible(false);
	}
	
	private void createNumericalsSearch() {
		xpathTextBoxLabel.setVisible(false);
		xpathTextBox.setVisible(false);
		xpathTextBox.setEnabled(false);
		valueTextBoxLabel.setVisible(false);
		valueTextBox.setVisible(false);
		elementListBoxLabel.setVisible(false);
		elementListBox.setVisible(false);
		attributeListBoxLabel.setVisible(false);
		attributeListBox.setVisible(false);
	}
	
	private void createElementValuesSearch() {
		xpathTextBoxLabel.setVisible(false);
		xpathTextBox.setVisible(false);
		xpathTextBox.setEnabled(false);
		valueTextBoxLabel.setVisible(true);
		valueTextBox.setVisible(true);
		elementListBoxLabel.setVisible(true);
		elementListBox.setVisible(true);
		attributeListBoxLabel.setVisible(false);
		attributeListBox.setVisible(false);
	}

	private void initAttributeListBox() {
		attributeListBox.clear();
		ElementEnum elementEnum = ElementEnum.getEnum(elementListBox.getValue(elementListBox.getSelectedIndex()));
		switch(elementEnum) {
		case CHARACTER:
	        for(CharacterAttributeEnum characterAttribute : CharacterAttributeEnum.values()) {
	        	attributeListBox.addItem(characterAttribute.displayName());
	        }
			break;
		case RELATION:
	        for(RelationAttributeEnum relationAttribute : RelationAttributeEnum.values()) {
	        	attributeListBox.addItem(relationAttribute.displayName());
	        }
			break;
		case STRUCTURE:
	        for(StructureAttributeEnum structureAttribute : StructureAttributeEnum.values()) {
	        	attributeListBox.addItem(structureAttribute.displayName());
	        }
			break;
		default:
			break;
		}
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
	
    @Override
    public void setEnabled(boolean value) {
    	enabled = value;
		this.typeListBox.setEnabled(value);
		this.searchButton.setEnabled(value);
		this.attributeListBox.setEnabled(value);
		this.elementListBox.setEnabled(value);
		this.valueTextBox.setEnabled(value);
		this.xpathTextBox.setEnabled(value);
		if(value)
			updateXPath();
		else
			this.xpathLabel.setText("");
	}
    
    
    
	public boolean isEnabled() {
		return enabled;
	}


	private void initElementListBox() {
		elementListBox.clear();
        for(ElementEnum element : ElementEnum.values()) {
        	elementListBox.addItem(element.displayName());
        }
	}
	
    private void initTypeListBox() {
    	typeListBox.clear();
        for(SearchTypeEnum searchType : SearchTypeEnum.values()) {
        	typeListBox.addItem(searchType.displayName());
        }
	}

	@Override
	public void setInput(String input) {
		this.inputLabel.setText(input);
	}


}
