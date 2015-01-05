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
	public Set<BiochemicalReaction>[] reactionSet; 
	public Set<SmallMolecule> smallMoleculeSet;
	public ArrayList<String>[] proteinsInComplex; 
	public static Map<BiochemicalReaction, Integer>[] rectHash;
	public static ArrayList<BiochemicalReaction>[] rectList;
	
	
	public int maxSize = 0;
	
	public MultipleReactionView(PApplet p){
		parent = p;
	}
	
	@SuppressWarnings("unchecked")
	public void setItems(){
		int i=0;
		maxSize =0;
		rectHash =  new HashMap[nFiles];
		rectList =  new ArrayList[nFiles];
		for (int f=0; f<nFiles;f++){
			Map<BiochemicalReaction, Integer> unsortMap  =  new HashMap<BiochemicalReaction, Integer>();
			for (BiochemicalReaction current : reactionSet[f]){
				Object[] s = current.getLeft().toArray();
				
				// Compute size of reaction
				int size = 0;
				for (int i3=0;i3<s.length;i3++){
					  String name = getProteinName(s[i3].toString());
					  if (name!=null){
						  size++;
					  }	  
					  else if (mapComplexRDFId_index.get(s[i3].toString())!=null){
						  int id = mapComplexRDFId_index.get(s[i3].toString());
						  ArrayList<String> components = proteinsInComplex[id];
						  size += components.size();
					  }
					  else 
						  size++;
				}
				 
				unsortMap.put(current, size);
				if (size>maxSize)
					maxSize = size;
				i++;
			}
		}
		
	}
	public String getProteinName(String ref){	
		String s1 = mapElementRDFId.get(ref);
		if (s1==null){
			s1 = mapElementRef.get(ref);
		}
		return s1;
	}
	
	public ArrayList<String> getProteinsInComplexById(int id){	
		ArrayList<String> components = new ArrayList<String>(); 
		Complex com = complexList.get(id);
		Object[] s2 = com.getComponent().toArray();
		for (int i=0;i<s2.length;i++){
			 if (getProteinName(s2[i].toString())!=null)
				  components.add(getProteinName(s2[i].toString()));
			 else {
				  if (mapComplexRDFId_index.get(s2[i].toString())==null){
					  String name = s2[i].toString();
					  components.add(name);
				  }
				  else{
					  int id4 = mapComplexRDFId_index.get(s2[i].toString());
					  ArrayList<String> s4 = getProteinsInComplexById(id4);
					  for (int k=0;k<s4.size();k++){
						  components.add(s4.get(k));
					  }
				  }
			  }
		 }
		 return components;
	}
	
	
	
	public void draw(){
		
	}
	
	
	
	
}
	