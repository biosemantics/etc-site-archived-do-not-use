package edu.arizona.sirls.etc.site.server.rpc;

import java.util.HashSet;
import java.util.Set;

public class Parse {

	private Set<IParseListener> listeners = new HashSet<IParseListener>();
	
	public void addListener(IParseListener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(IParseListener listener) {
		listeners.remove(listener);
	}

	public void start() {
		this.notifyListeners();
	}

	
	public void notifyListeners() {
		for(IParseListener listener : listeners) {
			listener.finished();
		}
	}

}
