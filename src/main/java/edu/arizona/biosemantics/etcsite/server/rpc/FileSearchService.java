package edu.arizona.biosemantics.etcsite.server.rpc;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.biosemantics.etcsite.server.Configuration;
import edu.arizona.biosemantics.etcsite.shared.file.XMLFileFormatter;
import edu.arizona.biosemantics.etcsite.shared.file.search.ElementAttributeValuesSearch;
import edu.arizona.biosemantics.etcsite.shared.file.search.ElementValuesSearch;
import edu.arizona.biosemantics.etcsite.shared.file.search.ElementsSearch;
import edu.arizona.biosemantics.etcsite.shared.file.search.NumericalsSearch;
import edu.arizona.biosemantics.etcsite.shared.file.search.Search;
import edu.arizona.biosemantics.etcsite.shared.file.search.SearchResult;
import edu.arizona.biosemantics.etcsite.shared.file.search.XPathSearch;
import edu.arizona.biosemantics.etcsite.shared.rpc.AuthenticationToken;
import edu.arizona.biosemantics.etcsite.shared.rpc.IFileAccessService;
import edu.arizona.biosemantics.etcsite.shared.rpc.IFileSearchService;
import edu.arizona.biosemantics.etcsite.shared.rpc.IFileService;
import edu.arizona.biosemantics.etcsite.shared.rpc.RPCResult;

public class FileSearchService extends RemoteServiceServlet implements IFileSearchService {

	private static final long serialVersionUID = -6550101274590336948L;

	private IFileAccessService fileAccessService = new FileAccessService();
	private IFileService fileService = new FileService();
	private XMLFileFormatter xmlFileFormatter = new XMLFileFormatter();
	
	@Override
	public RPCResult<List<SearchResult>> search(AuthenticationToken authenticationToken, String filePath, Search search) {
		if(search == null)
			return new RPCResult<List<SearchResult>>(false, "Invalid Search");
		
		Map<String, Set<String>> capturedMatchFiles = new HashMap<String, Set<String>>();
		RPCResult<List<String>> filesResult = fileService.getDirectoriesFiles(authenticationToken, filePath);
		if(!filesResult.isSucceeded()) 
			return new RPCResult<List<SearchResult>>(false, filesResult.getMessage());
		
		List<String> files = filesResult.getData();
		for(String file : files) {
			RPCResult<String> contentResult = fileAccessService.getFileContent(authenticationToken, filePath + File.separator + file);
			if(!contentResult.isSucceeded()) 
				return new RPCResult<List<SearchResult>>(false, contentResult.getMessage());
			try {
				NodeList nodeList = this.searchNodeList(authenticationToken, contentResult.getData(), search);
				for(int i=0; i<nodeList.getLength(); i++) {
		    		Node node = nodeList.item(i);
		    		
		    		// node can't be given to search as its used client-side. gwt's node implementation cannot be used with LSSerializer, 
		    		// no other way known to pretty print xml with gwt client
		    		String capturedMatch = null;
		    		if(search instanceof ElementsSearch) {
		    			capturedMatch = node.getNodeName();
		    		} 
	    			if(search instanceof ElementAttributeValuesSearch) {
	    				ElementAttributeValuesSearch elementAttributeValuesSearch = (ElementAttributeValuesSearch)search;
	    				NamedNodeMap attributes = node.getAttributes();
	    				for(int j=0; j<attributes.getLength(); j++) {
							Node attribute = attributes.item(j);
							if(attribute.getNodeName().equals(elementAttributeValuesSearch.getAttribute())) {
								capturedMatch = attribute.getNodeValue();
							}
	    				}
		    		} 
	    			if(search instanceof ElementValuesSearch) {
	    				capturedMatch = node.getNodeValue();
		    		} 
					if(search instanceof NumericalsSearch) {
						StringBuilder resultBuilder = new StringBuilder();
						NamedNodeMap attributes = node.getAttributes();
						for(int j=0; j<attributes.getLength(); j++) {
							Node attribute = attributes.item(j);
							String attributeValue = attribute.getNodeValue();
							if(attributeValue.matches(((NumericalsSearch)search).getNumericalExpression())) {
								resultBuilder.append(attributeValue + "; ");
							}
						}
						capturedMatch = resultBuilder.toString();
						if(!capturedMatch.isEmpty())
							capturedMatch = capturedMatch.substring(0, capturedMatch.length() - 2);
					} 
					if(search instanceof XPathSearch) {
						capturedMatch = xmlFileFormatter.format(node, false);
					} 
			    	if(capturedMatch == null)
			    		capturedMatch = xmlFileFormatter.format(node, false);
			    	if(!capturedMatchFiles.containsKey(capturedMatch))
			    		capturedMatchFiles.put(capturedMatch, new HashSet<String>());
			    	capturedMatchFiles.get(capturedMatch).add(filePath + File.separator + file);
			    }
				/*int occurrences = nodeList.getLength();
				if(occurrences > 0) {
					result.add(new SearchResult(inputDirectory + File.separator + file, occurrences));
				}*/
			} catch(Exception e) {
				e.printStackTrace();
				return new RPCResult<List<SearchResult>>(false, "Search failed");
			}
		}
			
		List<SearchResult> searchResult = new LinkedList<SearchResult>();
		for(String capturedMatch : capturedMatchFiles.keySet()) {
			searchResult.add(new SearchResult(capturedMatch, capturedMatchFiles.get(capturedMatch)));
		}
		Collections.sort(searchResult);
		return new RPCResult<List<SearchResult>>(true, searchResult);
	}
	
	private NodeList searchNodeList(AuthenticationToken authenticationToken, String content, Search search) throws Exception {
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();

		DocumentBuilder builder = builderFactory.newDocumentBuilder();
		Document document = builder.parse(new ByteArrayInputStream(content.getBytes("UTF-8")));
	    XPath xPath =  XPathFactory.newInstance(Configuration.xPathObjectModel).newXPath();

	    XPathExpression xpathExpression = xPath.compile(search.getXPath());
	    NodeList nodeList = (NodeList) xpathExpression.evaluate(document, XPathConstants.NODESET);
	    return nodeList;
	}
	
	@Override
	public RPCResult<String> getFileContentSearched(AuthenticationToken authenticationToken, String filePath, Search search) {
		if(search == null)
			return new RPCResult<String>(false, "Invalid Search", "");
		
		RPCResult<String> contentResult = fileAccessService.getFileContent(authenticationToken, filePath);
		if(contentResult.isSucceeded()) {
			String content = contentResult.getData();
			try {
				NodeList nodeList = this.searchNodeList(authenticationToken, content, search);
				String highlighted = xmlFileFormatter.formatAndHiglight(content, nodeList);
				return new RPCResult<String>(true, "", highlighted);
			} catch(Exception e) {
				e.printStackTrace();
				return new RPCResult<String>(false, "Search failed", "");
			}
		} 
		return contentResult;
	}
}
