package edu.arizona.biosemantics.etcsite.client.common.files;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.biosemantics.etcsite.shared.file.semanticmarkup.TaxonIdentificationEntry;


public class CreateSemanticMarkupFilesView extends Composite implements ICreateSemanticMarkupFilesView {

	private static CreateSemanticMarkupFilesViewUiBinder uiBinder = GWT.create(CreateSemanticMarkupFilesViewUiBinder.class);
	
	interface CreateSemanticMarkupFilesViewUiBinder extends UiBinder<Widget, CreateSemanticMarkupFilesView> { }
	
	@UiField
	TextBox author;
	
	@UiField
	TextBox date;
	
	@UiField
	TextBox title;
	
	@UiField
	TextBox strain;
	
	@UiField
	TextBox strainSource;
	
	@UiField
	TextArea morphologicalDescription;
	
	@UiField
	TextArea phenologicalDescription;
	
	@UiField
	TextArea habitatDescription;
	
	@UiField
	TextArea distributionDescription;
	
	@UiField
	Button createButton;
	
	@UiField
	Grid ranksGrid;
	
	@UiField
	ListBox ranksList;
	
	@UiField
	Button addRankButton;
	
	private ICreateSemanticMarkupFilesView.Presenter presenter;

	public CreateSemanticMarkupFilesView() {
		initWidget(uiBinder.createAndBindUi(this));
		initRanksListBox(ranksList);
	}
	
	private ListBox initRanksListBox(ListBox ranksList) {
		ranksList.addItem("order");
		ranksList.addItem("suborder");
		ranksList.addItem("superfamily");
		ranksList.addItem("family");
		ranksList.addItem("subfamily");
		ranksList.addItem("tribe");
		ranksList.addItem("subtribe");
		ranksList.addItem("genus");
		ranksList.addItem("subgenus");
		ranksList.addItem("section");
		ranksList.addItem("subsection");
		ranksList.addItem("series");
		ranksList.addItem("species");
		ranksList.addItem("subspecies");
		ranksList.addItem("variety");
		ranksList.addItem("forma");
		ranksList.addItem("unranked");
		return ranksList;
	}

	@UiHandler("addRankButton")
	public void onAddRank(ClickEvent event) {
		int newrow = ranksGrid.insertRow(ranksGrid.getRowCount() - 1);
		ListBox ranksBox = new ListBox();
		initRanksListBox(ranksBox);
		if(newrow > 0)  {
			Widget widget = ranksGrid.getWidget(newrow -1, 0);
			if(widget instanceof ListBox) {
				ListBox previousRanksBox = (ListBox)widget;
				if(previousRanksBox.getSelectedIndex() < ranksBox.getItemCount() - 1)
					ranksBox.setItemSelected(previousRanksBox.getSelectedIndex() + 1, true);
			}
		}
		ranksGrid.setWidget(newrow, 0, ranksBox);
		ranksGrid.setWidget(newrow, 1, new TextBox());
	}
	
	@UiHandler("createButton") 
	public void onCreate(ClickEvent event) {
		presenter.onSend();
	}

	public String getAuthor() {
		return author.getText().trim();
	}

	public String getDate() {
		return date.getText().trim();
	}

	public String getTitleText() {
		return title.getText().trim();
	}
	
	public String getStrain() {
		return strain.getText().trim();
	}
	
	public String getStrainsSource() {
		return strainSource.getText().trim();
	}

	public List<TaxonIdentificationEntry> getTaxonIdentificationEntries() {
		List<TaxonIdentificationEntry> result = new LinkedList<TaxonIdentificationEntry>();
		for(int i = 1; i < ranksGrid.getRowCount() - 1; i++){ //row 0 is the header row, also there is a button at the end of table
			Widget rankWidget = ranksGrid.getWidget(i, 0);
			Widget valueWidget = ranksGrid.getWidget(i, 1);
			if(rankWidget instanceof ListBox && valueWidget instanceof TextBox) { 
				ListBox rankBox = (ListBox)rankWidget;
				String rank = rankBox.getItemText(rankBox.getSelectedIndex());
				
				TextBox valueBox = (TextBox)valueWidget;
				String value = valueBox.getText().trim();
				result.add(new TaxonIdentificationEntry(rank, value));
			}
		}
		return result;
	}

	public String getMorphologicalDescription() {
		return morphologicalDescription.getText().trim();
	}

	public String getPhenologzDescriptionField() {
		return phenologicalDescription.getText().trim();
	}

	public String getHabitatDescriptionField() {
		return habitatDescription.getText().trim();
	}

	public String getDistributionDescriptionField() {
		return distributionDescription.getText().trim();
	}

	@Override
	public void setPresenter(ICreateSemanticMarkupFilesView.Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void removeAddtionalTaxonRanks() {
		while(ranksGrid.getRowCount() > 3) {
			ranksGrid.removeRow(ranksGrid.getRowCount() - 2);
		}
	}
}
