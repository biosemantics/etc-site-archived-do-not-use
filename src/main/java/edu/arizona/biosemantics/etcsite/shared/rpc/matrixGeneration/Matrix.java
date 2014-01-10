package edu.arizona.biosemantics.etcsite.shared.rpc.matrixGeneration;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;


public class Matrix implements Serializable {

	private List<String> characterNames = new LinkedList<String>();
	private List<Taxon> taxons = new LinkedList<Taxon>();
	
	public Matrix() { }
	
	public Matrix(List<String> characterNames, List<Taxon> taxons) {
		super();
		this.characterNames = characterNames;
		this.taxons = taxons;
	}

	public List<String> getCharacterNames() {
		return characterNames;
	}

	public void setCharacterNames(List<String> characterNames) {
		this.characterNames = characterNames;
	}

	public List<Taxon> getTaxons() {
		return taxons;
	}

	public void setTaxons(List<Taxon> taxons) {
		this.taxons = taxons;
	}

	public boolean addCharacterName(String e) {
		return characterNames.add(e);
	}

	public boolean removeCharacterName(String o) {
		return characterNames.remove(o);
	}

	public boolean addTaxon(Taxon e) {
		return taxons.add(e);
	}
	

	public boolean removeTaxon(Taxon e) {
		return taxons.remove(e);
	}
	
	
}
