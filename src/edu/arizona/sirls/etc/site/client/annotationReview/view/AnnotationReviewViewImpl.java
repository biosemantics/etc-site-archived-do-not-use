package edu.arizona.sirls.etc.site.client.annotationReview.view;
 
import java.util.List;

import edu.arizona.sirls.etc.site.client.annotationReview.presenter.XMLEditorPresenter;
import edu.arizona.sirls.etc.site.shared.rpc.IFileAccessServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.IFileFormatServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.IFileSearchService;
import edu.arizona.sirls.etc.site.shared.rpc.IFileSearchServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.file.search.CharacterAttributeEnum;
import edu.arizona.sirls.etc.site.shared.rpc.file.search.ElementAttributeValuesSearch;
import edu.arizona.sirls.etc.site.shared.rpc.file.search.ElementEnum;
import edu.arizona.sirls.etc.site.shared.rpc.file.search.ElementValuesSearch;
import edu.arizona.sirls.etc.site.shared.rpc.file.search.ElementsSearch;
import edu.arizona.sirls.etc.site.shared.rpc.file.search.NumericalsSearch;
import edu.arizona.sirls.etc.site.shared.rpc.file.search.RelationAttributeEnum;
import edu.arizona.sirls.etc.site.shared.rpc.file.search.Search;
import edu.arizona.sirls.etc.site.shared.rpc.file.search.SearchResult;
import edu.arizona.sirls.etc.site.shared.rpc.file.search.SearchTypeEnum;
import edu.arizona.sirls.etc.site.shared.rpc.file.search.StructureAttributeEnum;
import edu.arizona.sirls.etc.site.shared.rpc.file.search.XPathSearch;

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
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLTable.Cell;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
 
public class AnnotationReviewViewImpl extends Composite implements AnnotationReviewView {
 
    private static AnnotationReviewViewUiBinder uiBinder = GWT.create(AnnotationReviewViewUiBinder.class);

	@UiTemplate("AnnotationReviewView.ui.xml")
	interface AnnotationReviewViewUiBinder extends UiBinder<Widget, AnnotationReviewViewImpl> {
	}

    public AnnotationReviewViewImpl(IFileAccessServiceAsync fileAccessService, IFileFormatServiceAsync fileFormatService, IFileSearchServiceAsync fileSearchService) {
        initWidget(uiBinder.createAndBindUi(this));
        this.xmlEditorPresenter = new XMLEditorPresenter(xmlEditor, fileAccessService, fileFormatService, fileSearchService);
        xmlEditor.setPresenter(xmlEditorPresenter);
        
        initTypeListBox(); 
        initElementListBox();
        DomEvent.fireNativeEvent(Document.get().createChangeEvent(), typeListBox);
    }
    
    private void initTypeListBox() {
    	typeListBox.clear();
        for(SearchTypeEnum searchType : SearchTypeEnum.values()) {
        	typeListBox.addItem(searchType.displayName());
        }
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
    TextBox xpathTextBox;
    @UiField
    ListBox elementListBox;
    @UiField
    ListBox attributeListBox;
    @UiField
    Label resultText;
    @UiField
    Grid resultGrid;
    @UiField
    XMLEditorViewImpl xmlEditor;
	private Presenter presenter;
	private XMLEditorPresenter xmlEditorPresenter;
	private List<SearchResult> searchResults;
    
	@UiHandler("resultGrid")
	void onResultClick(ClickEvent event) {
		Cell cell = resultGrid.getCellForEvent(event);
		if (cell != null) {
			presenter.onResultClicked(searchResults.get(cell.getRowIndex() - 1));
		}
	}
	
    @UiHandler("inputButton")
    void onInputClick(ClickEvent event) {
    	presenter.onInputButtonClicked();
    }
   

	@UiHandler("searchButton")
    void onSearchClick(ClickEvent event) {
		Search search = new XPathSearch(xpathTextBox.getValue());
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
    	Search search = getCurrentSearch();
    	this.xpathTextBox.setText(search.getXPath());
	}

	@UiHandler("typeListBox")
    void onTypeChangeEvent(ChangeEvent event) {
    	SearchTypeEnum searchTypeEnum = SearchTypeEnum.getEnum(typeListBox.getValue(typeListBox.getSelectedIndex()));
    	this.typeLabel.setText(searchTypeEnum.displayName());
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
		valueTextBox.setVisible(false);
		elementListBox.setVisible(false);
		attributeListBox.setVisible(false);
	}
	
	private void createElementAttributeValuesSearch() {
		xpathTextBox.setVisible(true);
		valueTextBox.setVisible(true);
		elementListBox.setVisible(true);
		attributeListBox.setVisible(true);
	}
	
	private void createElementsSearch() {
		xpathTextBox.setVisible(true);
		valueTextBox.setVisible(false);
		elementListBox.setVisible(true);
		attributeListBox.setVisible(false);
	}
	
	private void createNumericalsSearch() {
		xpathTextBox.setVisible(true);
		valueTextBox.setVisible(false);
		elementListBox.setVisible(false);
		attributeListBox.setVisible(false);
	}
	
	private void createElementValuesSearch() {
		xpathTextBox.setVisible(true);
		valueTextBox.setVisible(true);
		elementListBox.setVisible(true);
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

	private void initElementListBox() {
		elementListBox.clear();
        for(ElementEnum element : ElementEnum.values()) {
        	elementListBox.addItem(element.displayName());
        }
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void setInput(String input) {
		this.inputLabel.setText(input);
	}

	@Override
	public void setResult(List<SearchResult> searchResults) {
		if(searchResults == null)
			resultText.setText("Invalid XPath expression");
		else {
			resultText.setText(searchResults.size() + " files found");
			this.searchResults = searchResults;
			this.resultGrid.resize(searchResults.size() + 1, 2);
			for(int i=0; i<searchResults.size(); i++) {
				SearchResult searchResult = searchResults.get(i);
				this.resultGrid.setText(i + 1, 0, searchResult.getTarget());
				this.resultGrid.setText(i + 1, 1, String.valueOf(searchResult.getOccurrences()));
			}
		}
		
	}

	@Override
	public XMLEditorPresenter getXMLEditorPresenter() {
		return this.xmlEditorPresenter;
	}

    @Override
    public void setEnabledSearch(boolean value) {
		this.typeListBox.setEnabled(value);
		this.searchButton.setEnabled(value);
		this.attributeListBox.setEnabled(value);
		this.elementListBox.setEnabled(value);
		this.valueTextBox.setEnabled(value);
		this.xpathTextBox.setEnabled(value);
	}
    
    @Override
    public void setEnabledXMLEditor(boolean value) {
    	this.xmlEditorPresenter.setEnabled(value);
    } 
}