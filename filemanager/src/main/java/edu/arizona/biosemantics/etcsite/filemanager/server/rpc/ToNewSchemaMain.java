package edu.arizona.biosemantics.etcsite.filemanager.server.rpc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.xpath.XPathFactory;

import edu.arizona.biosemantics.etcsite.filemanager.server.Configuration;

public class ToNewSchemaMain {

	public static void main(String[] args) throws JDOMException, IOException {
		//String input = "C:/Users/rodenhausen/Downloads/141020-FoC-Tiliaceae_141020-FoC-Tiliaceae-Text-Capture"
			//	+ "/141020-FoC-Tiliaceae_141020-FoC-Tiliaceae-Text-Capture/";
		//String output = "C:/Users/rodenhausen/Downloads/141020-FoC-Tiliaceae_141020-FoC-Tiliaceae-Text-Capture"
			//	+ "/out/";
		String input = "C:/Users/rodenhausen/Downloads/141020-FoC-Tiliaceae/141020-FoC-Tiliaceae";
		String output = "C:/Users/rodenhausen/Downloads/141020-FoC-Tiliaceae/out";
		
		File in = new File(input);
		SAXBuilder sax = new SAXBuilder();
		XmlNamespaceManager nsmgr = new XmlNamespaceManager();
		XPathFactory xpfac = XPathFactory.instance();
		Namespace bioNamespace = Namespace.getNamespace("bio", Configuration.targetNamespace);
		
		File out = new File(output);
		out.mkdirs();
		
		for(File file : in.listFiles()) {
			Document doc = sax.build(file);
			
			/*Element root = doc.getRootElement();
			root.getChild("meta").getChild("source").getChild("author").setText("unknown");
			root.getChild("meta").getChild("source").getChild("title").setText("unknown");
			root.getChild("meta").getChild("source").getChild("date").setText("unknown");
			for(Element processor : root.getChild("meta").getChild("processed_by").getChildren()) {
				if(processor.getChild("date").getText().isEmpty())
					processor.getChild("date").setText("unknown");
				if(processor.getChild("operator").getText().isEmpty())
					processor.getChild("operator").setText("unknown");
			}*/
			
			/*nsmgr.setXmlSchema(doc, FileTypeEnum.TAXON_DESCRIPTION);
			
			XPathExpression<Element> xp = xpfac.compile("/bio:treatment/taxon_identification", 
					Filters.element(), null, bioNamespace);
			
			Map<Rank, RankData> rankDataMap = new HashMap<Rank, RankData>();
			Set<Element> detachables = new HashSet<Element>();
			for (Element element : xp.evaluate(doc)) {
				for(Element child : element.getChildren()) {
					if(child.getName().endsWith("_name")) {
						Rank rank = Rank.valueOf(child.getName().split("_name")[0].toUpperCase());
						if(!rankDataMap.containsKey(rank))
							rankDataMap.put(rank, new RankData(rank, child.getValue()));
						rankDataMap.get(rank).setName(child.getValue());
						detachables.add(child);
					}
					if(child.getName().endsWith("_authority")) {
						Rank rank = Rank.valueOf(child.getName().split("_authority")[0].toUpperCase());
						if(!rankDataMap.containsKey(rank))
							rankDataMap.put(rank, new RankData(rank, null));
						rankDataMap.get(rank).setAuthor(child.getValue());
						detachables.add(child);
					}
				}
			}
			
			for(Element element : detachables) 
				element.detach();
			
			for (Element element : xp.evaluate(doc)) {
				for(Rank rank : rankDataMap.keySet()) {
					Element taxonName = new Element("taxon_name");
					taxonName.setAttribute("rank", rank.name().toLowerCase());
					taxonName.setText(rankDataMap.get(rank).getName());
					if(rankDataMap.get(rank).getAuthor() != null && 
							!rankDataMap.get(rank).getAuthor().isEmpty()) {
						taxonName.setAttribute("authority", rankDataMap.get(rank).getAuthor());
					}
					element.addContent(0, taxonName);
				}
			}
			
			xp = xpfac.compile("/bio:treatment/description", 
					Filters.element(), null, bioNamespace);
			for(Element element : xp.evaluate(doc)) {
				if(element.getValue().trim().isEmpty()) {
					element.detach();
				}
			}*/
			
			File outFile = new File(out, file.getName());
			XMLOutputter xout = new XMLOutputter(Format.getPrettyFormat());
			FileOutputStream fos = new FileOutputStream(outFile);
			xout.output(doc, fos);
			fos.flush();
			fos.close();
		}
	}

}
