package edu.arizona.sirls.etc.site.server.rpc;

import java.io.ByteArrayInputStream;
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

import edu.arizona.sirls.etc.site.client.AuthenticationToken;
import edu.arizona.sirls.etc.site.server.Configuration;
import edu.arizona.sirls.etc.site.shared.rpc.IAuthenticationService;
import edu.arizona.sirls.etc.site.shared.rpc.IFileAccessService;
import edu.arizona.sirls.etc.site.shared.rpc.IFileSearchService;
import edu.arizona.sirls.etc.site.shared.rpc.IFileService;
import edu.arizona.sirls.etc.site.shared.rpc.RPCResult;
import edu.arizona.sirls.etc.site.shared.rpc.file.XMLFileFormatter;
import edu.arizona.sirls.etc.site.shared.rpc.file.search.ElementAttributeValuesSearch;
import edu.arizona.sirls.etc.site.shared.rpc.file.search.ElementValuesSearch;
import edu.arizona.sirls.etc.site.shared.rpc.file.search.ElementsSearch;
import edu.arizona.sirls.etc.site.shared.rpc.file.search.NumericalsSearch;
import edu.arizona.sirls.etc.site.shared.rpc.file.search.Search;
import edu.arizona.sirls.etc.site.shared.rpc.file.search.SearchResult;
import edu.arizona.sirls.etc.site.shared.rpc.file.search.XPathSearch;

public class FileSearchService extends RemoteServiceServlet implements IFileSearchService {

	private static final long serialVersionUID = -6550101274590336948L;

	private IFileAccessService fileAccessService = new FileAccessService();
	private IFileService fileService = new FileService();
	private IAuthenticationService authenticationService = new AuthenticationService();
	private XMLFileFormatter xmlFileFormatter = new XMLFileFormatter();
	
	@Override
	public RPCResult<List<SearchResult>> search(AuthenticationToken authenticationToken, String inputDirectory, Search search) {
		RPCResult<List<SearchResult>> result = new RPCResult<List<SearchResult>>(false, "Authentication failed");
		if(authenticationService.isValidSession(authenticationToken).getData().getResult()) { 
			Map<String, Set<String>> capturedMatchTargets = new HashMap<String, Set<String>>();
			RPCResult<List<String>> filesResult = fileService.getDirectoriesFiles(authenticationToken, inputDirectory);
			if(filesResult.isSucceeded()) {
				List<String> files = filesResult.getData();
				for(String file : files) {
					NodeList nodeList = this.searchNodeList(authenticationToken, inputDirectory + "//" + file, search);
				    if(nodeList != null) {
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
					    	if(!capturedMatchTargets.containsKey(capturedMatch))
					    		capturedMatchTargets.put(capturedMatch, new HashSet<String>());
					    	capturedMatchTargets.get(capturedMatch).add(inputDirectory + "//" + file);
					    }
						/*int occurrences = nodeList.getLength();
						if(occurrences > 0) {
							result.add(new SearchResult(inputDirectory + "//" + file, occurrences));
						}*/
				    }
				}
				
				List<SearchResult> searchResult = new LinkedList<SearchResult>();
				for(String capturedMatch : capturedMatchTargets.keySet()) {
					searchResult.add(new SearchResult(capturedMatch, capturedMatchTargets.get(capturedMatch)));
				}
				Collections.sort(searchResult);
				return new RPCResult<List<SearchResult>>(true, searchResult);
			}
			return new RPCResult<List<SearchResult>>(false, filesResult.getMessage());
		}
		return result;
	}
	
	private NodeList searchNodeList(AuthenticationToken authenticationToken, String target, Search search) {
		if(authenticationService.isValidSession(authenticationToken).getData().getResult()) { 
			if(search != null) {
				RPCResult<String> contentResult = fileAccessService.getFileContent(authenticationToken, target);
				if(contentResult.isSucceeded()) {
					String fileContent = contentResult.getData();
					DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
					try {
						DocumentBuilder builder = builderFactory.newDocumentBuilder();
						Document document = builder.parse(new ByteArrayInputStream(fileContent.getBytes("UTF-8")));
					    XPath xPath =  XPathFactory.newInstance(Configuration.xPathObjectModel).newXPath();
					    try {
						    XPathExpression xpathExpression = xPath.compile(search.getXPath());
						    NodeList nodeList = (NodeList) xpathExpression.evaluate(document, XPathConstants.NODESET);
						    return nodeList;
					    } catch(Exception e) {
					    	e.printStackTrace();
					    }
					} catch(Exception e) { 
						e.printStackTrace();
					}
				}
			}
		}
		return null;
	}
	
	@Override
	public RPCResult<String> getFileContentSearched(AuthenticationToken authenticationToken, String target, Search search) {
		RPCResult<String> result = new RPCResult<String>(false, "Authentication failed", "");
		if(authenticationService.isValidSession(authenticationToken).getData().getResult()) { 
			RPCResult<String> contentResult = fileAccessService.getFileContent(authenticationToken, target);
			if(contentResult.isSucceeded()) {
				String content = contentResult.getData();
				NodeList nodeList = this.searchNodeList(authenticationToken, target, search);
				if(nodeList != null) {
					String highlighted = xmlFileFormatter.formatAndHiglight(content, nodeList);
					return new RPCResult<String>(true, "", highlighted);
				}
				return new RPCResult<String>(false, "", "Search failed");
			} 
			return contentResult;
		}
		return result;
	}
}
