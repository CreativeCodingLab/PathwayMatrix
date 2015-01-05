package main;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.biopax.paxtools.model.level3.BiochemicalReaction;
import org.biopax.paxtools.model.level3.Complex;
import org.biopax.paxtools.model.level3.SmallMolecule;

import processing.core.PApplet;

public class MultipleReactionView{
	public PApplet parent;
	public ArrayList<String> files;
	public int nFiles;
	public boolean isAllowedDrawing = false;
	
	// Read data 
	public Map<String,String> mapElementRDFId;
	public Map<String,String> mapElementRef;
	public Map<String,String> mapSmallMoleculeRDFId;
	public ArrayList<Complex> complexList; 
	public Map<String,Integer> mapComplexRDFId_index;
	public Set<BiochemicalReaction> reactionSet; 
	public Set<SmallMolecule> smallMoleculeSet;
	//public ArrayList<String>[] proteinsInComplex; 
	
	
	public MultipleReactionView(PApplet p){
		parent = p;
	}
	
	public void setItems(){
		
	}
	public void draw(){
		
	}
	
	
	
	
}
	