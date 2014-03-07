package edu.arizona.biosemantics.etcsite.client.common.files;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.common.IMessageConfirmView;
import edu.arizona.biosemantics.etcsite.client.common.IMessageConfirmView.IConfirmListener;
import edu.arizona.biosemantics.etcsite.client.common.IMessageView;
import edu.arizona.biosemantics.etcsite.client.common.ServerSetup;
import edu.arizona.biosemantics.etcsite.shared.file.FilePathShortener;
import edu.arizona.biosemantics.etcsite.shared.file.MyXmlWriter;
import edu.arizona.biosemantics.etcsite.shared.file.semanticmarkup.TaxonIdentificationEntry;
import edu.arizona.biosemantics.etcsite.shared.file.semanticmarkup.XmlModel.*;
import edu.arizona.biosemantics.etcsite.shared.rpc.IFileAccessServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.IFileServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.RPCCallback;
import edu.arizona.biosemantics.etcsite.shared.rpc.RPCResult;

public class CreateSemanticMarkupFilesPresenter implements ICreateSemanticMarkupFilesView.Presenter {

	interface XmlAutoBeanFactory extends AutoBeanFactory {
		AutoBean<Treatment> treatment();

		AutoBean<Meta> meta();

		AutoBean<Source> source();

		AutoBean<TaxonIdentification> taxonIdentification();

		// List<AutoBean<Description>> descriptions();
		AutoBean<Description> description();
	}

	private ICreateSemanticMarkupFilesView view;
	private XmlAutoBeanFactory factory = GWT.create(XmlAutoBeanFactory.class);
	private MyXmlWriter<Treatment> writer = new MyXmlWriter<Treatment>(factory,
			Treatment.class, "treatment");
	private IFileServiceAsync fileService;
	private IFileAccessServiceAsync fileAccessService;
	private String destinationFilePath;
	private IMessageView.Presenter messagePresenter;
	private IMessageConfirmView.Presenter messageConfirmPresenter;
	private FilePathShortener filePathShortener = new FilePathShortener();
	private int filesCreated;
	
	@Inject
	public CreateSemanticMarkupFilesPresenter(ICreateSemanticMarkupFilesView view, IFileServiceAsync fileService, 
			IFileAccessServiceAsync fileAccessService, IMessageView.Presenter messagePresenter, IMessageConfirmView.Presenter messageConfirmPresenter) {
		this.view = view;
		view.setPresenter(this);
		this.fileService = fileService;
		this.fileAccessService = fileAccessService;
		this.messagePresenter = messagePresenter;
		this.messageConfirmPresenter = messageConfirmPresenter;
	}

	public void onSend() {
		Treatment treatment = makeTreatment();

		String error = "";
		// source document information
		String text = view.getAuthor();
		if (text.length() == 0) {
			error += "Author can not be empty; ";
		} else {
			treatment.getMeta().getSource().setAuthor(text);
		}

		text = view.getDate();
		if (text.length() == 0) {
			error += "Date can not be empty; ";
		} else {
			treatment.getMeta().getSource().setDate(text);
		}

		text = view.getTitleText();
		treatment.getMeta().getSource().setTitle(text);

		// taxon name information
		String filename = "";
		List<TaxonIdentificationEntry> taxonIdentificationEntries = view
				.getTaxonIdentificationEntries();
		for (TaxonIdentificationEntry taxonIdentificationEntry : taxonIdentificationEntries) {
			if (filename.matches(".*(_|^)" + taxonIdentificationEntry.getRank()
					+ "(_|$).*"))
				error += "Redundant rank '"
						+ taxonIdentificationEntry.getRank() + "'. ";
			addToTreatment(treatment, taxonIdentificationEntry);
			filename += taxonIdentificationEntry.getRank() + "_"
					+ taxonIdentificationEntry.getValue() + "_";
		}
		filename = filename.replaceAll("_+", "_").replaceFirst("_$", "");

		// strain info
		String ttext = view.getStrain();
		if (ttext.length() > 0)
			treatment.getTaxonIdentification().setStrainName(ttext);

		if ((filename + ttext).length() == 0) {
			error += "Taxon Name and Strain Name fields can not all be empty; ";
		}

		String strainsrctext = view.getStrainsSource();
		if (strainsrctext.length() > 0)
			treatment.getTaxonIdentification().setStrainSource(strainsrctext);

		String mtext = view.getMorphologicalDescription();
		String ptext = view.getPhenologzDescriptionField();
		String htext = view.getHabitatDescriptionField();
		String dtext = view.getDistributionDescriptionField();

		if ((mtext + ptext + htext + dtext).length() == 0) {
			error += "Descriptions can not all be empty; ";
		} else {
			error += errorChecking(mtext, "Morphological");
			error += errorChecking(ptext, "Phenoloy");
			error += errorChecking(htext, "Habitat");
			error += errorChecking(dtext, "Distribution");

			mtext = mtext.replaceAll("&", "&amp;").replaceAll("<", "&lt;")
					.replaceAll(">", "&gt;");
			ptext = ptext.replaceAll("&", "&amp;").replaceAll("<", "&lt;")
					.replaceAll(">", "&gt;");
			htext = htext.replaceAll("&", "&amp;").replaceAll("<", "&lt;")
					.replaceAll(">", "&gt;");
			dtext = dtext.replaceAll("&", "&amp;").replaceAll("<", "&lt;")
					.replaceAll(">", "&gt;");

			if (mtext.length() > 0) {
				Description md = makeDescription();
				md.setText(mtext);
				md.setDescriptionType("morphology");
				treatment.getDescriptions().add(md);
			}
			if (ptext.length() > 0) {
				Description pd = makeDescription();
				pd.setText(ptext);
				pd.setDescriptionType("phenology");
				treatment.getDescriptions().add(pd);
			}
			if (htext.length() > 0) {
				Description hd = makeDescription();
				hd.setText(htext);
				hd.setDescriptionType("habitat");
				treatment.getDescriptions().add(hd);
			}
			if (dtext.length() > 0) {
				Description dd = makeDescription();
				dd.setText(dtext);
				dd.setDescriptionType("distribution");
				treatment.getDescriptions().add(dd);
			}
			// treatment.setDescription(text);
		}
		if (error.length() > 0) {
			messagePresenter.showMessage("Input Error", error.trim());
			return;
		}

		final String xml = writer.write(treatment);
	
		// TODO validate
		// XMLValidator validator = new XMLValidator(new File("")); //TODO
		// client can not use File, how to access schema file?
		// validator.validate(xml);

		
		final String theFileName = filename;
		fileService.createFile(Authentication.getInstance().getToken(), destinationFilePath, theFileName, false, new AsyncCallback<RPCResult<String>>() {
			@Override
			public void onFailure(Throwable caught) { }

			@Override
			public void onSuccess(RPCResult<String> result) {
				if(result.isSucceeded()) {
					setFileContent(xml, destinationFilePath + ServerSetup.getInstance().getSetup().getSeperator() + theFileName);
				} else {
					messageConfirmPresenter.show("File creation failed", "File with name \"" + theFileName + "\" could not be created. " +
							"Do you want to create with a unique filename suffix?", new IConfirmListener() {
								@Override
								public void onConfirm() {
									fileService.createFile(Authentication.getInstance().getToken(), destinationFilePath, theFileName, 
											true, new RPCCallback<String>() {
										@Override
										public void onResult(String result) {
											setFileContent(xml, result);
										}
									});
								}
								@Override
								public void onCancel() { }
					});
				}
			}
		});
	}
	
	private void setFileContent(String content, final String fileDestination) {
		fileAccessService.setFileContent(Authentication.getInstance().getToken(), fileDestination, content, new RPCCallback<Void>() {
			@Override
			public void onResult(Void result) {
				messagePresenter.showMessage("File created", "File " + filePathShortener.shortenOwnedPath(fileDestination) + " " +
						"successfully created in " + filePathShortener.shortenOwnedPath(destinationFilePath));
				filesCreated++;
			}
		});
	}

	private String errorChecking(String text, String type) {
		String error = "";
		int left = text.replaceAll("[^(]", "").length();
		int right = text.replaceAll("[^)]", "").length();
		if (left > right)
			error = type + " description contains un-closed left brackets (. ";
		if (left < right)
			error = type + " description contains un-closed right brackets ). ";

		left = text.replaceAll("[^\\[]", "").length();
		right = text.replaceAll("[^\\]]", "").length();
		if (left > right)
			error = type + " description contains un-closed left brackets [. ";
		if (left < right)
			error = type + " description contains un-closed right brackets ]. ";

		left = text.replaceAll("[^{]", "").length();
		right = text.replaceAll("[^}]", "").length();
		if (left > right)
			error = type + " description contains un-closed left brackets {. ";
		if (left < right)
			error = type + " description contains un-closed right brackets }. ";
		return error;
	}

	private void addToTreatment(Treatment treatment, TaxonIdentificationEntry entry) {
		String rank = entry.getRank();
		String value = entry.getValue();
		if (rank.equals("order") )
			treatment.getTaxonIdentification().setOrderName(value);
		else if (rank.equals("suborder"))
			treatment.getTaxonIdentification().setSuborderName(value);
		else if (rank.equals("superfamily"))
			treatment.getTaxonIdentification().setSuperfamilyName(value);
		else if (rank.equals("family") )
			treatment.getTaxonIdentification().setFamilyName(value);
		else if (rank.equals("subfamily") )
			treatment.getTaxonIdentification().setSubfamilyName(value);
		else if (rank.equals("tribe") )
			treatment.getTaxonIdentification().setTribeName(value);
		else if (rank.equals("subtribe") )
			treatment.getTaxonIdentification().setSubtribeName(value);
		else if (rank.equals("genus") )
			treatment.getTaxonIdentification().setGenusName(value);
		else if (rank.equals("subgenus") )
			treatment.getTaxonIdentification().setSubgenusName(value);
		else if (rank.equals("section") )
			treatment.getTaxonIdentification().setSectionName(value);
		else if (rank.equals("subsection") )
			treatment.getTaxonIdentification().setSubsectionName(value);
		else if (rank.equals("series") )
			treatment.getTaxonIdentification().setSeriesName(value);
		else if (rank.equals("species") )
			treatment.getTaxonIdentification().setSpeciesName(value);
		else if (rank.equals("subspecies") )
			treatment.getTaxonIdentification().setSubspeciesName(value);
		else if (rank.equals("variety") )
			treatment.getTaxonIdentification().setVarietyName(value);
		else if (rank.equals("forma") )
			treatment.getTaxonIdentification().setFormaName(value);
		else if (rank.equals("unranked") )
			treatment.getTaxonIdentification().setUnrankedName(value);
	}

	private Treatment makeTreatment() {
		AutoBean<Treatment> treatment = factory.treatment();
		Treatment t = treatment.as();
		t.setMeta(makeMeta());
		t.setTaxonIdentification(makeTaxonIdentification());
		ArrayList<Description> descriptions = new ArrayList<Description>();
		t.setDescriptions(descriptions);
		return t;
	}

	private Meta makeMeta() {
		AutoBean<Meta> meta = factory.meta();
		Meta m = meta.as();
		m.setSource(makeSource());
		return m;
	}

	private Source makeSource() {
		AutoBean<Source> source = factory.source();
		return source.as();
	}

	private TaxonIdentification makeTaxonIdentification() {
		AutoBean<TaxonIdentification> taxonIdentification = factory.taxonIdentification();
		TaxonIdentification t = taxonIdentification.as();
		t.setStatus("ACCEPTED");
		return t;
	}

	private Description makeDescription() {
		AutoBean<Description> description = factory.description();
		return description.as();
	}

	@Override
	public ICreateSemanticMarkupFilesView getView() {
		return view;
	}
	
	@Override
	public void setDestinationFilePath(String destinationFilePath) {
		this.filesCreated = 0;
		this.destinationFilePath = destinationFilePath;
	}

	@Override
	public int getFilesCreated() {
		return this.filesCreated;
	}

	@Override
	public void init() {
		view.removeAddtionalTaxonRanks();
	}
}
