package edu.arizona.biosemantics.etcsite.filemanager.server.rpc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Random;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.biosemantics.etcsite.filemanager.server.Configuration;
import edu.arizona.biosemantics.etcsite.filemanager.shared.model.FolderModel;
import edu.arizona.biosemantics.etcsite.filemanager.shared.model.Model;
import edu.arizona.biosemantics.etcsite.filemanager.shared.rpc.IFileService;

public class FileService extends RemoteServiceServlet implements IFileService {
	
	private FolderModel root;
	private Model a1;
	private Model a2;
	private Model a3;
	private FolderModel root2;
	
	public FileService() {

		this.root = new FolderModel("root");
		this.a1 = new Model("1");
		this.a2 = new Model("2");
		this.a3 = new Model("3");
		this.root2 = new FolderModel("root2");
	}

	@Override
	public List<Model> getFolders(Model loadConfig) {
		List<Model> result = new LinkedList<Model>();
		if(loadConfig == null) {
			result.add(root);
			result.add(root2);
		} else {
			if(loadConfig.equals(root)) {
				result.add(a1);
				result.add(a2);
				result.add(a3);
			}
		}	
		return result;
	}
	
}
