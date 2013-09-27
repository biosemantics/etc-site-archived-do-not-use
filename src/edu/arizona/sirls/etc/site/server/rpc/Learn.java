package edu.arizona.sirls.etc.site.server.rpc;

import java.util.HashSet;
import java.util.Set;

public class Learn {

	private Set<ILearnListener> listeners = new HashSet<ILearnListener>();
	private int otoId;
	
	public void addListener(ILearnListener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(ILearnListener listener) {
		listeners.remove(listener);
	}

	public void start() {
		otoId = 54;
		this.notifyListeners(otoId);
	}

	public int getOtoId() {
		return otoId;
	}
	
	public void notifyListeners(int otoId) {
		for(ILearnListener listener : listeners) {
			listener.finished(otoId);
		}
	}

}
