package edu.arizona.biosemantics.etcsite.shared.file.semanticmarkup;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

import edu.arizona.biosemantics.etcsite.shared.file.semanticmarkup.XmlModel.Description;
import edu.arizona.biosemantics.etcsite.shared.file.semanticmarkup.XmlModel.Meta;
import edu.arizona.biosemantics.etcsite.shared.file.semanticmarkup.XmlModel.OtherInfoOnMeta;
import edu.arizona.biosemantics.etcsite.shared.file.semanticmarkup.XmlModel.ProcessedBy;
import edu.arizona.biosemantics.etcsite.shared.file.semanticmarkup.XmlModel.Processor;
import edu.arizona.biosemantics.etcsite.shared.file.semanticmarkup.XmlModel.Software;
import edu.arizona.biosemantics.etcsite.shared.file.semanticmarkup.XmlModel.Source;
import edu.arizona.biosemantics.etcsite.shared.file.semanticmarkup.XmlModel.TaxonIdentification;
import edu.arizona.biosemantics.etcsite.shared.file.semanticmarkup.XmlModel.Treatment;

public class XmlModelMaker {

	private XmlAutoBeanFactory factory;

	public XmlModelMaker(XmlAutoBeanFactory factory) {
		this.factory = factory;
	}
	
	public interface XmlAutoBeanFactory extends AutoBeanFactory {
		AutoBean<Treatment> treatment();

		AutoBean<Meta> meta();

		AutoBean<Source> source();

		AutoBean<TaxonIdentification> taxonIdentification();

		// List<AutoBean<Description>> descriptions();
		AutoBean<Description> description();

		AutoBean<OtherInfoOnMeta> otherInfoOnMeta();
		
		AutoBean<ProcessedBy> processedBy();

		AutoBean<Processor> processor();

		AutoBean<Software> software();
	}
	
	public Treatment makeTreatment() {
		AutoBean<Treatment> treatment = factory.treatment();
		Treatment t = treatment.as();
		t.setMeta(makeMeta());
		t.setTaxonIdentification(makeTaxonIdentification());
		ArrayList<Description> descriptions = new ArrayList<Description>();
		t.setDescriptions(descriptions);
		return t;
	}

	public Meta makeMeta() {
		AutoBean<Meta> meta = factory.meta();
		Meta m = meta.as();
		m.setSource(makeSource());
		return m;
	}

	public Source makeSource() {
		AutoBean<Source> source = factory.source();
		return source.as();
	}

	public TaxonIdentification makeTaxonIdentification() {
		AutoBean<TaxonIdentification> taxonIdentification = factory.taxonIdentification();
		TaxonIdentification t = taxonIdentification.as();
		t.setStatus("ACCEPTED");
		return t;
	}

	public Description makeDescription() {
		AutoBean<Description> description = factory.description();
		return description.as();
	}

	public OtherInfoOnMeta makeOtherInfoOnMeta() {
		AutoBean<OtherInfoOnMeta> otherInfoOnMeta = factory.otherInfoOnMeta();
		return otherInfoOnMeta.as();
	}
	
	public ProcessedBy makeProcessedBy() {
		AutoBean<ProcessedBy> processedBy = factory.processedBy();
		ProcessedBy pb = processedBy.as();
		List<Processor> processor = new LinkedList<Processor>();
		Processor p = factory.processor().as();
		p.setSoftware(factory.software().as());
		processor.add(p);
		pb.setProcessor(processor);
		return pb;
	}
	
}
