package edu.arizona.biosemantics.etcsite.client.common.files;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.biosemantics.etcsite.shared.file.semanticmarkup.TaxonIdentificationEntry;


public class CreateSemanticMarkupFilesView extends Composite implements ICreateSemanticMarkupFilesView {

	private ICreateSemanticMarkupFilesView.Presenter presenter;
	private Label errorLabel;
	private TextBox authorField;
	private TextBox dateField;
	private TextBox titleField;
	private TextBox strainField;
	private TextArea mdescriptionField;
	private TextArea pdescriptionField;
	private TextArea hdescriptionField;
	private TextArea ddescriptionField;
	private TextBox strainsrcField;
	private Button sendButton;
	//private Label textToServerLabel;
	//private HTML serverResponseLabel;
	private Grid nametable;
	
	public CreateSemanticMarkupFilesView() {
		ScrollPanel scrollPanel = new ScrollPanel();
		VerticalPanel body = new VerticalPanel();
		scrollPanel.add(body);
		
		// introduction text
		final Label pagetitle = new Label("Create Input XML File for Semantic Markup Task");
		// pagetitle.setAutoHorizontalAlignment(ALIGN_CENTER);
		body.add(pagetitle);
		body.add(new Label("")); // padding
		final Label instruction = new Label(
				"Fill out the form for each taxon. Save the form to create one input file as required by the semantic markup tool. "
						+ "The file name will be the taxon name you provided. All applicable fields are required");
		body.add(instruction);
		errorLabel = new Label();
		body.add(errorLabel);

		// source
		final DisclosurePanel sourcedocinfo = new DisclosurePanel(
				"Source Document Information");
		final FlowPanel authorline = new FlowPanel();
		final Label author = new Label("Author:");
		authorField = new TextBox();
		authorline.add(author);
		authorline.add(authorField);

		final Label date = new Label("Date:");
		dateField = new TextBox();
		authorline.add(date);
		authorline.add(dateField);

		final Label title = new Label("Title:");
		titleField = new TextBox();
		authorline.add(title);
		authorline.add(titleField);
		sourcedocinfo.add(authorline);
		body.add(sourcedocinfo);
		
		// taxon name id
		final DisclosurePanel taxonnameinfo = new DisclosurePanel(
				"Taxon Name Information");

		ListBox ranks = getRanks();

		nametable = new Grid(3, 2);
		nametable.setText(0, 0, "rank");
		nametable.setText(0, 1, "name");
		nametable.setWidget(1, 0, ranks);
		nametable.setWidget(1, 1, new TextBox());
		Button addrank = new Button("add a rank");
		nametable.setWidget(2, 0, addrank);
		addrank.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				int newrow = nametable.insertRow(nametable.getRowCount() - 1);
				// nametable.insertRow(nametable.getRowCount()-1);
				// int newrow = nametable.getRowCount()-2;
				nametable.setWidget(newrow, 0, getRanks());
				nametable.setWidget(newrow, 1, new TextBox());
			}
		});
		taxonnameinfo.add(nametable);
		body.add(taxonnameinfo);

		// strain info
		final DisclosurePanel straininfo = new DisclosurePanel(
				"Strain Information (for microbes only)");
		final FlowPanel strainline = new FlowPanel();
		final Label strain = new Label("Strain:");
		strainField = new TextBox();
		strainline.add(strain);
		strainline.add(strainField);

		final Label strainsrc = new Label("Strain Source:");
		strainsrcField = new TextBox();
		strainline.add(strainsrc);
		strainline.add(strainsrcField);
		straininfo.add(strainline);
		body.add(straininfo);

		// descriptions
		final DisclosurePanel descriptioninfo = new DisclosurePanel(
				"Descriptions");
		final FlowPanel descriptionline = new FlowPanel();

		final Label warning = new Label(
				"  If copy/paste from another source, check word by word (especially special symbols) that the content is copied correctly.");
		final Label warning2 = new Label(
				"  Do not include any HTML or XML tag in the text.");
		descriptionline.add(warning);
		descriptionline.add(warning2);

		final Label mdescription = new Label(
				"Morphological Description or Microbial Description:");
		mdescriptionField = new TextArea();
		// TODO: enforce UTF-8
		mdescriptionField.setSize("600px", "100px");
		descriptionline.add(mdescription);
		descriptionline.add(mdescriptionField);

		final Label pdescription = new Label("Phenology Description:");
		pdescriptionField = new TextArea();
		// TODO: enforce UTF-8
		pdescriptionField.setSize("600px", "100px");
		descriptionline.add(pdescription);
		descriptionline.add(pdescriptionField);

		final Label hdescription = new Label("Habitat Description:");
		hdescriptionField = new TextArea();
		// TODO: enforce UTF-8
		hdescriptionField.setSize("600px", "100px");
		descriptionline.add(hdescription);
		descriptionline.add(hdescriptionField);

		final Label ddescription = new Label("Taxon Distribution:");
		ddescriptionField = new TextArea();
		// TODO: enforce UTF-8
		ddescriptionField.setSize("600px", "100px");
		descriptionline.add(ddescription);
		descriptionline.add(ddescriptionField);
		descriptioninfo.add(descriptionline);
		body.add(descriptioninfo);

		sendButton = new Button("Save to Folder");
		sendButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				presenter.onSend();
			}
		});
		body.add(sendButton);

		// Create the popup dialog box
		/*final DialogBox dialogBox = new DialogBox();
		dialogBox.setText("Remote Procedure Call");
		dialogBox.setAnimationEnabled(true);
		final Button closeButton = new Button("Close");
		// We can set the id of a widget by accessing its Element
		closeButton.getElement().setId("closeButton");
		textToServerLabel = new Label();
		serverResponseLabel = new HTML();
		VerticalPanel dialogVPanel = new VerticalPanel();
		dialogVPanel.addStyleName("dialogVPanel");
		dialogVPanel.add(new HTML("<b>Sending name to the server:</b>"));
		dialogVPanel.add(textToServerLabel);
		dialogVPanel.add(new HTML("<br><b>Server replies:</b>"));
		dialogVPanel.add(serverResponseLabel);
		dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
		dialogVPanel.add(closeButton);
		dialogBox.setWidget(dialogVPanel);

		// Add a handler to close the DialogBox
		closeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				dialogBox.hide();
				sendButton.setEnabled(true);
				sendButton.setFocus(true);
			}
		});
		*/
		this.initWidget(scrollPanel);
		this.setSize("800px", "600px");
	}

	private ListBox getRanks() {
		final ListBox ranks = new ListBox();
		ranks.addItem("order");
		ranks.addItem("suborder");
		ranks.addItem("superfamily");
		ranks.addItem("family");
		ranks.addItem("subfamily");
		ranks.addItem("tribe");
		ranks.addItem("subtribe");
		ranks.addItem("genus");
		ranks.addItem("subgenus");
		ranks.addItem("section");
		ranks.addItem("subsection");
		ranks.addItem("series");
		ranks.addItem("species");
		ranks.addItem("subspecies");
		ranks.addItem("variety");
		ranks.addItem("forma");
		ranks.addItem("unranked");
		return ranks;
	}
	
	public void setErrorText(String string) {
		errorLabel.setText(string);
	}

	public String getAuthor() {
		return authorField.getText().trim();
	}

	public String getDate() {
		return dateField.getText().trim();
	}

	public String getTitleText() {
		return titleField.getText().trim();
	}

	public List<TaxonIdentificationEntry> getTaxonIdentificationEntries() {
		List<TaxonIdentificationEntry> result = new LinkedList<TaxonIdentificationEntry>();
		for(int i = 1; i < nametable.getRowCount() - 1; i++){ //row 0 is the header row, also there is a button at the end of table
			Widget rankWidget = nametable.getWidget(i, 0);
			Widget valueWidget = nametable.getWidget(i, 1);
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

	public String getStrain() {
		return strainField.getText().trim();
	}

	public String getMorphologicalDescription() {
		return mdescriptionField.getText().trim();
	}

	public String getPhenologzDescriptionField() {
		return pdescriptionField.getText().trim();
	}

	public String getHabitatDescriptionField() {
		return hdescriptionField.getText().trim();
	}

	public String getDistributionDescriptionField() {
		return ddescriptionField.getText().trim();
	}

	public String getStrainsSource() {
		return strainsrcField.getText().trim();
	}

	public void setResult(String xml) {
		//sendButton.setEnabled(false);
		//textToServerLabel.setText(xml);
		//serverResponseLabel.setText("");
	}

	@Override
	public void setPresenter(ICreateSemanticMarkupFilesView.Presenter presenter) {
		this.presenter = presenter;
	}
}
