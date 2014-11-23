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

import org.biopax.paxtools.model.level3.BiochemicalReaction;

import processing.core.PApplet;

public class PopupReaction{
	public static boolean sPopup = true;
	public static boolean bPopup = false;
	public static int bRect = -1000;
	public static ArrayList<Integer> sRectList = new ArrayList<Integer>();
	public PApplet parent;
	public float x = 0;
	public float xButton = 0;
	public static float yBegin = 25;
	public static float yBeginList = 70;
	public int w1 = 100;
	public int w = 600;
	public int h = 28;
	public static int s=-100;
	public static float maxSize = 0;
	public Integrator[] iX, iY, iH;
	public int[] hightlightList;
	public float maxH = 22;
	float hProtein = 0;
	
	public static Map<BiochemicalReaction, Integer> rectHash =  new HashMap<BiochemicalReaction, Integer>();
	public static ArrayList<BiochemicalReaction> rectList =  new ArrayList<BiochemicalReaction>();
	float itemH2 = 0; // height of items in the reaction
	
	public String[] proteins = null;
	public static  Map<String,Integer> mapProteinRDFId_index;
	
	public ArrayList<Integer> bProteinLeft = new ArrayList<Integer>();
	public ArrayList<Integer> bProteinRight = new ArrayList<Integer>();
	public Integrator[] iP;
	
	public static CheckBox check1;
	public static CheckBox check21;
	public static CheckBox check22;
	public static CheckBox check23;
	public static CheckBox check24;
	public static CheckBox check25;
	public static TextBox textbox1; 
	
	public float xL = x;
	public float xL2 = xL+200;
	public float xRect = x+400;
	public float xR = x+800;
	public float xR2 = xR-200;
	
	public static Color smallMoleculeColor = new Color(150,150,0);
	public static Color unidentifiedElementColor = new Color(150,70,150);
	public static Color formComplexColor = new Color(0,150,100);
	public static Color complexRectionColor = new Color(0,0,180);
	public static Color proteinRectionColor = new Color(180,0,0);
	
	public static WordCloud wordCloud;
	public static int numTop =30;
	public ArrayList<String> unidentifiedList = new ArrayList<String>();
	
	// Unidentified Elements
	public float yUFO = 0;
	
	public PopupReaction(PApplet parent_){
		parent = parent_;
		check1 = new CheckBox(parent, "Rearrange reactions");
		check21 = new CheckBox(parent, "Fade links of Small molecules");
		check22 = new CheckBox(parent, "Fade links of Unidentified elements");
		check23 = new CheckBox(parent, "Fade links of Complex formation");
		check24 = new CheckBox(parent, "Fade links of Complex reaction");
		check25 = new CheckBox(parent, "Fade links of Protein reaction");
		textbox1 = new TextBox(parent, "Search");
		wordCloud = new WordCloud(parent, 10,290,200,parent.height-300);
	}
	
	public void setItems(){
		int i=0;
		maxSize =0;
		Map<BiochemicalReaction, Integer> unsortMap  =  new HashMap<BiochemicalReaction, Integer>();
		s=-400;
		for (BiochemicalReaction current : main.MainMatrixVersion_1_2.reactionSet){
			Object[] s = current.getLeft().toArray();
			
			// Compute size of reaction
			int size = 0;
			for (int i3=0;i3<s.length;i3++){
				  String name = main.MainMatrixVersion_1_2.getProteinName(s[i3].toString());
				  if (name!=null){
					  size++;
				  }	  
				  else if (main.MainMatrixVersion_1_2.mapComplexRDFId_index.get(s[i3].toString())!=null){
					  int id = main.MainMatrixVersion_1_2.mapComplexRDFId_index.get(s[i3].toString());
					  ArrayList<String> components = main.MainMatrixVersion_1_2.proteinsInComplex[id];
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
		rectHash = sortByComparator(unsortMap);
		rectList =  new ArrayList<BiochemicalReaction>();
		for (Map.Entry<BiochemicalReaction, Integer> entry : rectHash.entrySet()) {
			rectList.add(entry.getKey());
		}
			
		// Word cloud
		WordCount wc1 = new WordCount(numTop);
		ArrayList<String> a = new ArrayList<String>();
		for (Map.Entry<BiochemicalReaction, Integer> entry : rectHash.entrySet()) {
			String rectName = entry.getKey().getDisplayName();
			String[] pieces = rectName.split(" ");
			for (int k=0;k<pieces.length;k++){
				a.add(pieces[k].trim());
			}
		}
			
		wc1.countNames(a); 
		wordCloud.updateTags(wc1.wordArray, wc1.counts);
		
		// positions of items
		iX = new Integrator[rectHash.size()];
		iY = new Integrator[rectHash.size()];
		iH = new Integrator[rectHash.size()];
		for (i=0;i<rectHash.size();i++){
			iX[i] = new Integrator(x, 0.5f,0.1f);
			iY[i] = new Integrator(20, 0.5f,0.1f);
			iH[i] = new Integrator(10, 0.5f,0.1f);
		}
		
		hightlightList =  new int[rectHash.size()];
		for (i=0;i<rectHash.size();i++){
			hightlightList[i] = -1;
		}
			
		proteins =  new String[main.MainMatrixVersion_1_2.ggg.size()];
		mapProteinRDFId_index = new HashMap<String,Integer>();
		iP =  new Integrator[main.MainMatrixVersion_1_2.ggg.size()];
		for (int p=0; p<main.MainMatrixVersion_1_2.ggg.size();p++){
			proteins[p] =  main.MainMatrixVersion_1_2.ggg.get(p).name;
			mapProteinRDFId_index.put( main.MainMatrixVersion_1_2.ggg.get(p).name, p);
			iP[p] =   new Integrator(20, 0.5f,0.1f);
		}
		updateProteinPositions();
		updateUnidentifiedElements();
	}
	
	public void updateUnidentifiedElements(){
		unidentifiedList = new ArrayList<String>();
		for (int r=0;r<rectList.size();r++) {
			BiochemicalReaction rect = rectList.get(r);
			Object[] aLeft = rect.getLeft().toArray();
			Object[] aRight = rect.getRight().toArray();
			
			ArrayList<String> a1 = getUnidentifiedElements2(aLeft);
			for (int i=0;i<a1.size();i++){
				String ufo = a1.get(i);
				if (!unidentifiedList.contains(ufo))
					unidentifiedList.add(ufo);
			}
			ArrayList<String> a2 = getUnidentifiedElements2(aRight);
			for (int i=0;i<a2.size();i++){
				String ufo = a2.get(i);
				if (!unidentifiedList.contains(ufo))
					unidentifiedList.add(ufo);
			}
		}
	}
	
	public ArrayList<String> getUnidentifiedElements2(Object[] s) {
		ArrayList<String> a = new ArrayList<String>();
		for (int i3=0;i3<s.length;i3++){
			  String name = main.MainMatrixVersion_1_2.getProteinName(s[i3].toString());
			  if (mapProteinRDFId_index.get(name)!=null){
			  }
			  else  if (main.MainMatrixVersion_1_2.mapComplexRDFId_index.get(s[i3].toString())!=null){
				  int id = main.MainMatrixVersion_1_2.mapComplexRDFId_index.get(s[i3].toString());
				  ArrayList<String> components = main.MainMatrixVersion_1_2.proteinsInComplex[id];
				  for (int k=0;k<components.size();k++){
					 if (mapProteinRDFId_index.get(components.get(k))==null){
						 a.add(components.get(k));
						 System.out.println(components.get(k));
					 }	  
				  }
			  }
			  else{
				 a.add(s[i3].toString());
			 } 
		}
		return a;
	}
	
	public void updateProteinPositions(){
		hProtein = (parent.height-yBeginList-20)/(proteins.length);  // Save 20 pixels for Unidentified elements
		if (hProtein>maxH)
			hProtein =maxH;
		for (int p=0; p<proteins.length;p++){
			int order = main.MainMatrixVersion_1_2.ggg.get(p).order;
			iP[p].target(yBeginList+hProtein*order);
		}
		updateReactionPositions();  /// **********Update reactions when updating proteins **********
	}
	
	public void countProteinParticipation(){
		
	}

	public void updateReactionPositions(){
		itemH2 = (parent.height-yBeginList)/(rectHash.size());
		// Compute positions
		if (itemH2>maxH)
			itemH2 =maxH;
		for (int i=0;i<rectHash.size();i++){
			iH[i].target(itemH2);
		}	
		
		if (check1.s){
			int indexOfItemHash=0;
			Map<Integer, Float> unsortMap  =  new HashMap<Integer, Float>();
			for (Map.Entry<BiochemicalReaction, Integer> entry : rectHash.entrySet()) {
				BiochemicalReaction rect = entry.getKey();
				Object[] aLeft = rect.getLeft().toArray();
				Object[] aRight = rect.getRight().toArray();
				ArrayList<Integer> proteinLeft = getProteinsInOneSideOfReaction(aLeft);
				ArrayList<Integer> proteinRight = getProteinsInOneSideOfReaction(aRight);
				
				float score = 0;
				float size = 0;
				for (int i=0; i<proteinLeft.size();i++){
					int pOrder = proteinLeft.get(i);
					if (!main.MainMatrixVersion_1_2.isSmallMolecule(proteins[pOrder])) {// DO NOT order by small molecules
						score -= iP[pOrder].target;
						size++;
					}	
				}
				for (int i=0; i<proteinRight.size();i++){
					int pOrder = proteinRight.get(i);
					if (!main.MainMatrixVersion_1_2.isSmallMolecule(proteins[pOrder])) {// DO NOT order by small molecules
						score -= iP[pOrder].target;
						size++;
					}	
				}
				
				if (size>0)
					score = score/size;
				
				unsortMap.put(indexOfItemHash, score);	
				indexOfItemHash++;
			}
			
			Map<Integer, Float> sortedMap = sortByComparator2(unsortMap);
			int i5 = 0;
			for (Map.Entry<Integer, Float> entry : sortedMap.entrySet()) {
				int rectOrder = entry.getKey();
				iY[rectOrder].target(yBeginList+i5*itemH2);
				i5++;
			}
		}
		else{
			for (int i=0;i<rectHash.size();i++){
				iY[i].target(yBeginList+i*itemH2);
			}
		}
		
	}
		
		
	// Sort decreasing order
	public static Map<BiochemicalReaction, Integer> sortByComparator(Map<BiochemicalReaction, Integer> unsortMap) {
		// Convert Map to List
		List<Map.Entry<BiochemicalReaction, Integer>> list = 
			new LinkedList<Map.Entry<BiochemicalReaction, Integer>>(unsortMap.entrySet());
 
		// Sort list with comparator, to compare the Map values
		Collections.sort(list, new Comparator<Map.Entry<BiochemicalReaction, Integer>>() {
			public int compare(Map.Entry<BiochemicalReaction, Integer> o1,
                                           Map.Entry<BiochemicalReaction, Integer> o2) {
				return -(o1.getValue()).compareTo(o2.getValue());
			}
		});
 
		// Convert sorted map back to a Map
		Map<BiochemicalReaction, Integer> sortedMap = new LinkedHashMap<BiochemicalReaction, Integer>();
		for (Iterator<Map.Entry<BiochemicalReaction, Integer>> it = list.iterator(); it.hasNext();) {
			Map.Entry<BiochemicalReaction, Integer> entry = it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}
	
	// Sort Reactions by score (average positions of proteins)
	public static Map<Integer, Float> sortByComparator2(Map<Integer, Float> unsortMap) {
		// Convert Map to List
		List<Map.Entry<Integer, Float>> list = 
			new LinkedList<Map.Entry<Integer, Float>>(unsortMap.entrySet());
 
		// Sort list with comparator, to compare the Map values
		Collections.sort(list, new Comparator<Map.Entry<Integer, Float>>() {
			public int compare(Map.Entry<Integer, Float> o1,
                                           Map.Entry<Integer, Float> o2) {
				return -(o1.getValue()).compareTo(o2.getValue());
			}
		});
 
		// Convert sorted map back to a Map
		Map<Integer, Float> sortedMap = new LinkedHashMap<Integer, Float>();
		for (Iterator<Map.Entry<Integer, Float>> it = list.iterator(); it.hasNext();) {
			Map.Entry<Integer, Float> entry = it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}
	
	
	
	public void drawButton(float x_){
		xButton = x_;
		
		checkBrushing();
		parent.textSize(13);
		parent.fill(150);
		if (bPopup)
			parent.stroke(255,0,0);
		if (sPopup)
			parent.fill(0);
		parent.rect(xButton,0,w1,25);
		parent.fill(0);
		if (sPopup)
			parent.fill(255);
		parent.textAlign(PApplet.CENTER);
		parent.text("Reaction",xButton+w1/2,18);
	
		if (hightlightList==null) return;
	
		int countLitems = 0;
		for (int i=0;i<hightlightList.length;i++){
			if (hightlightList[i]>=1){
				countLitems++;
			}
		}
		
		
	}
	
	
	public void drawReactions(float x_){
		xL = x_;
		float www = parent.width/1.8f;
		xL2 = xL+www/6;
		xRect = x_+www/2;
		xR = x_+www;
		xR2 = xR-www/6;
		
		// Draw seach box
		textbox1.draw(xRect);
		
		if (proteins==null) return;
			for (int i=0;i<rectHash.size();i++){
				iY[i].update();
				iH[i].update();
			}
			
			// Draw another button
			parent.fill(0);
			parent.textSize(13);
			parent.textAlign(PApplet.CENTER);
			parent.text(rectHash.size()+" Reactions",xRect,45);
			
			// Draw proteins *****************************
			float maxY = 0;
			for (int p=0; p<proteins.length;p++){
				iP[p].update();
				if (iP[p].value>maxY)
					maxY = iP[p].value;
			}
			yUFO = maxY+20;
			  		
			
			parent.fill(0);
			parent.textSize(13);
			parent.textAlign(PApplet.CENTER);
			parent.text("Input Proteins", xL, 45);
			parent.text("Input Complexes", xL2, 45);
			parent.text("Output Complexes", xR2, 45);
			parent.text("Output Proteins", xR, 45);
			
			
			bProteinLeft =  new ArrayList<Integer>();
			bProteinRight =  new ArrayList<Integer>();
			if (bRect>=0){
				BiochemicalReaction rect = rectList.get(bRect);
				Object[] aLeft = rect.getLeft().toArray();
				Object[] aRight = rect.getRight().toArray();
				bProteinLeft = getProteinsInOneSideOfReaction(aLeft);
				bProteinRight = getProteinsInOneSideOfReaction(aRight);
			}	
			else if (!textbox1.searchText.equals("")){
				for (int r=0;r<sRectList.size();r++) {
					BiochemicalReaction rect = rectList.get(sRectList.get(r));
					Object[] aLeft = rect.getLeft().toArray();
					Object[] aRight = rect.getRight().toArray();
					
					ArrayList<Integer> a1 = getProteinsInOneSideOfReaction(aLeft);
					for (int i=0;i<a1.size();i++){
						int ind = a1.get(i);
						if (bProteinLeft.indexOf(ind)<0)
							bProteinLeft.add(ind);
					}
					ArrayList<Integer> a2 = getProteinsInOneSideOfReaction(aRight);
					for (int i=0;i<a2.size();i++){
						int ind = a2.get(i);
						if (bProteinRight.indexOf(ind)<0)
							bProteinRight.add(ind);
					}
				}
			}
			
			for (int p=0; p<proteins.length;p++){
				if (bRect>=0 || !textbox1.searchText.equals("") ){
					// Get protein in the brushing reactions
					if (bProteinLeft.indexOf(p)>=0)
						drawProteinLeft(p,255);
					else
						drawProteinLeft(p,25);
					
					if (bProteinRight.indexOf(p)>=0)
						drawProteinRight(p,255);
					else
						drawProteinRight(p,25);
				}
				else{
					drawProteinLeft(p,200);
					drawProteinRight(p,200);
				}
			}
			
			
			// Reaction Links ******************
			if (bRect>=0){
				for (int r=0;r<rectList.size();r++) {
					BiochemicalReaction rect = rectList.get(r);
					if (r==bRect) // Draw brushing reactions ***************
						drawReactionLink(rect, r, xL, xL2, xRect, xR, xR2, 255);
					else 
						drawReactionLink(rect, r, xL, xL2, xRect, xR, xR2, 25);
				}
			}
			else if (!textbox1.searchText.equals("") ){
				for (int r=0;r<rectList.size();r++) {
					BiochemicalReaction rect = rectList.get(r);
					if (sRectList.indexOf(r)>=0)
						drawReactionLink(rect, r, xL, xL2, xRect, xR, xR2, 255);
					else
						drawReactionLink(rect, r, xL, xL2, xRect, xR, xR2, 25);
				}
			}
			else{
				for (int r=0;r<rectList.size();r++) {
					BiochemicalReaction rect = rectList.get(r);
					drawReactionLink(rect, r, xL, xL2, xRect, xR, xR2, 200);
				}
			}
			
			
			// Draw reaction Nodes **************************
			int i=0;
			for (Map.Entry<BiochemicalReaction, Integer> entry : rectHash.entrySet()) {
				if (bRect>=0)
					drawReactionNode(entry, i, 25);
				else
					drawReactionNode(entry, i, 200);
				i++;
			}	
			float x7 = (xR+200);
			float y7 = 70;
			float gap7 = 40;
			float step7 = 16;
			
			
			check1.draw((int) x7, (int) y7-20);
			check21.draw((int) x7, (int) y7);
			check22.draw((int) x7, (int) y7+20);
			check23.draw((int) x7, (int) y7+40);
			check24.draw((int) x7, (int) y7+60);
			check25.draw((int) x7, (int) y7+80);
			
			// Draw word cloud
			wordCloud.x1=parent.width-200; 
			wordCloud.x2=parent.width; 
			wordCloud.draw(parent);
			
			int[][] rel =  new int[numTop][numTop];
			for (Map.Entry<BiochemicalReaction, Integer> entry : rectHash.entrySet()) {
				String rectName = entry.getKey().getDisplayName();
				for (int m=0;m<numTop;m++){
					for (int n=0;n<numTop;n++){
						if (wordCloud.words[m].equals("") || wordCloud.words[n].equals("")) 
							continue;
						if (rectName.contains(wordCloud.words[m].word) && rectName.contains(wordCloud.words[n].word))
							rel[m][n]++;
					}
				}		
			}
			drawRelationship(wordCloud, rel, Color.BLACK);
	}

	
	public void drawRelationship(WordCloud wc, int[][] rel, Color color){
		int max = 0;
		for (int i=0;i<numTop;i++){
			for (int j=i+1;j<numTop;j++){
				if (rel[i][j]>max)
					max = rel[i][j];
			}
		}	
		int brushing = wc.b;
		 for (int i=0;i<numTop;i++){
			if (i==brushing) continue;  // skip drawing the brushing relations
			float y1 = wc.words[i].y-wc.words[i].font_size/3f;
			for (int j=i+1;j<numTop;j++){
				float y2 = wc.words[j].y-wc.words[j].font_size/3f;
				float xx = wc.x1;
				float yy = (y1+y2)/2;
				parent.noFill();
				
				float maxWeight = max;
				if (max<=5){
					maxWeight = 6;
				}
				float wei = PApplet.map(rel[i][j], 0, maxWeight, 0, 100);
				parent.stroke(color.getRed(),color.getGreen(),color.getBlue(),wei);
				parent.strokeWeight(wei/20);
				parent.arc(xx, yy, y2-y1,y2-y1, PApplet.PI/2, 3*PApplet.PI/2);
			}
		}
		// Draw relationship of brushing term
		 if (brushing>=0){
		 	float y1 = wc.words[brushing].y-wc.words[brushing].font_size/3f;
			for (int j=0;j<numTop;j++){
				if (j==brushing) continue;
				float y2 = wc.words[j].y-wc.words[j].font_size/3f;
				float xx = wc.x1;
				float yy = (y1+y2)/2;
				parent.noFill();
				
				float maxWeight = max;
				if (max<=5){
					maxWeight = 6;
				}
				if (j>brushing && rel[brushing][j]>0){
					float wei = PApplet.map(rel[brushing][j], 0, maxWeight, 0, 150);
					parent.stroke(255,0,0,wei+155);
					parent.strokeWeight(wei/20);
					parent.arc(xx, yy, y2-y1,y2-y1, PApplet.PI/2, 3*PApplet.PI/2);
				}
				else if (j<brushing && rel[j][brushing]>0){
					float wei = PApplet.map(rel[j][brushing], 0, maxWeight, 0, 150);
					parent.stroke(255,0,0,wei+155);
					parent.strokeWeight(wei/20);
					parent.arc(xx, yy, y1-y2,y1-y2, PApplet.PI/2, 3*PApplet.PI/2);
				}
			}
		 } 
	}
	
	public  void drawGradientLine(float x1, float y1, float x2, float y2, Color color, float sat) {
		float gap = PApplet.abs(x2-x1)/4;
		
		parent.noStroke();
		
		for (float x = 0; x <= gap; x=x+gap/100) {
			  float x3 = x1+x;	
			  float x4 = x2-x;	
			  float y3 = (x3-x1)*(y2-y1)/(float) (x2-x1) +y1;
			  float y4 = (x4-x1)*(y2-y1)/(float) (x2-x1) +y1;
			  //float dis1 = PApplet.min(x3-x1, x2-x3);
			  float alpha = PApplet.map(x, 0,gap, sat, 0);
			  if (alpha>1){
				   Color c = new Color(color.getRed(), color.getGreen(), color.getBlue(),(int) alpha);
				   parent.fill(c.getRGB());
				   parent.ellipse(x3,y3,1.25f,1.25f);
				   parent.ellipse(x4,y4,1.25f,1.25f);
			  } 
		}
	} 
	
	public ArrayList<Integer> getProteinsInOneSideOfReaction(Object[] s) {
		ArrayList<Integer> a = new ArrayList<Integer>();
		for (int i3=0;i3<s.length;i3++){
			  String name = main.MainMatrixVersion_1_2.getProteinName(s[i3].toString());
			  if (mapProteinRDFId_index.get(name)!=null){
				  a.add(mapProteinRDFId_index.get(name));
			  }
			  else  if (main.MainMatrixVersion_1_2.mapComplexRDFId_index.get(s[i3].toString())!=null){
				  int id = main.MainMatrixVersion_1_2.mapComplexRDFId_index.get(s[i3].toString());
				  ArrayList<String> components = main.MainMatrixVersion_1_2.proteinsInComplex[id];
				  for (int k=0;k<components.size();k++){
					  if (mapProteinRDFId_index.get(components.get(k))!=null){
						  a.add(mapProteinRDFId_index.get(components.get(k)));
					  }	  
				  }
			  }
			 else{
			//	 a.add(s[i3].toString());
				//		System.out.println("***getProteinsInOneSideOfReaction: CAN NOT FIND ="+s[i3]);
			 } 
		  }
		return a;
	}
		
	
	public void drawProteinLeft(int p, float sat) {
		float y3 = iP[p].value;
		float textSixe = PApplet.map(hProtein, 0, maxH, 2, 13);
		parent.textSize(textSixe);
		parent.fill(0,sat);
		if (main.MainMatrixVersion_1_2.isSmallMolecule(proteins[p])){
			parent.fill(smallMoleculeColor.getRed(),smallMoleculeColor.getGreen(),smallMoleculeColor.getBlue(),sat);
			parent.textSize(textSixe);
		}
		if (sat>=255 && textSixe<10)
			parent.textSize(10);
		parent.textAlign(PApplet.RIGHT);
		parent.text(proteins[p], xL,y3);
	}
	
	public void drawProteinRight(int p, float sat) {
		float y3 = iP[p].value;
		float textSixe = PApplet.map(hProtein, 0, maxH, 2, 13);
		parent.textSize(textSixe);
		parent.fill(0,sat);
		if (main.MainMatrixVersion_1_2.isSmallMolecule(proteins[p])){
			parent.fill(smallMoleculeColor.getRed(),smallMoleculeColor.getGreen(),smallMoleculeColor.getBlue(),sat);
			parent.textSize(textSixe);
		}
		if (sat>=255 && textSixe<10)
			parent.textSize(10);
		
		parent.textAlign(PApplet.LEFT);
		parent.text(proteins[p], xR,y3);
		
	}
		
	public void drawReactionNode(Map.Entry<BiochemicalReaction, Integer> entry, int i, float sat) {
		float r = PApplet.map(PApplet.sqrt(entry.getValue()), 0, PApplet.sqrt(maxSize), 0, maxH/2);
		parent.noStroke();
		parent.fill(0,sat);
		
		String rectName = entry.getKey().getDisplayName();
		if (!textbox1.searchText.equals("")){
			if (sRectList.indexOf(i)>=0){
				parent.fill(100,0,0);
				parent.ellipse(xRect,iY[i].value-iH[i].value/2, r, r);
			}
		}	
		else 
			parent.ellipse(xRect,iY[i].value-iH[i].value/2, r, r);
		
		// Draw brushing reaction name
		if (i==bRect){
			parent.fill(0);
			parent.ellipse(xRect,iY[i].value-iH[i].value/2, r, r);
			
			parent.fill(0);
			parent.textSize(13);
			parent.textAlign(PApplet.CENTER);
			float y3 = iY[i].value-iH[i].value;
			if (y3<55)
				y3=55;
			
			parent.text(rectName,xRect,y3);
		}
	}
		 
	// draw Reactions links
	public void drawReactionLink(BiochemicalReaction rect, int i2, float xL, float xL2, float xRect, float xR, float xR2, float sat) {
		Object[] sLeft = rect.getLeft().toArray();
		  for (int i3=0;i3<sLeft.length;i3++){
			  String name = main.MainMatrixVersion_1_2.getProteinName(sLeft[i3].toString());
			  if (mapProteinRDFId_index.get(name)!=null){
				  parent.stroke(proteinRectionColor.getRed(),proteinRectionColor.getGreen(),proteinRectionColor.getBlue(),sat);
				  float y5 = iP[mapProteinRDFId_index.get(name)].value-hProtein/4f;
				  float y6 = iY[i2].value-iH[i2].value/2;
				  if (check21.s && main.MainMatrixVersion_1_2.isSmallMolecule(name) && sat==200)
					  drawGradientLine(xL, y5, xRect, y6, smallMoleculeColor, sat);
				  else if (check25.s && !main.MainMatrixVersion_1_2.isSmallMolecule(name) && sat==200){
					  drawGradientLine(xL, y5, xRect, y6, proteinRectionColor, sat);
				  }
				  else  {
					  if (main.MainMatrixVersion_1_2.isSmallMolecule(name)){
							parent.stroke(smallMoleculeColor.getRed(),smallMoleculeColor.getGreen(),smallMoleculeColor.getBlue(),sat);
					  }
					  parent.line(xL, y5, xRect, y6);
				  }
			  }	  
			  else if (main.MainMatrixVersion_1_2.mapComplexRDFId_index.get(sLeft[i3].toString())!=null){
				  int id = main.MainMatrixVersion_1_2.mapComplexRDFId_index.get(sLeft[i3].toString());
				  
				  ArrayList<String> components = main.MainMatrixVersion_1_2.proteinsInComplex[id];
				  
				  float yL2 = 0;
				  int numAvailableComponents = 0;
				  for (int k=0;k<components.size();k++){
					  if (mapProteinRDFId_index.get(components.get(k))!=null){
						  yL2+= iP[mapProteinRDFId_index.get(components.get(k))].value-hProtein/4f;
						  numAvailableComponents++;
					  }	  
				  }
				  if (numAvailableComponents==0)
					  yL2 =iY[i2].value-iH[i2].value/2;
				  else 	  
					  yL2 /= numAvailableComponents;
				  for (int k=0;k<components.size();k++){
					  parent.stroke(formComplexColor.getRed(), formComplexColor.getGreen(), formComplexColor.getBlue(),sat);
					  if (mapProteinRDFId_index.get(components.get(k))!=null){
						  float y4 = iP[mapProteinRDFId_index.get(components.get(k))].value-hProtein/4f;
						  if (check23.s && sat==200)
							  drawGradientLine(xL, y4, xL2, yL2, formComplexColor, sat);
						  else
							  parent.line(xL, y4, xL2, yL2);
					  }	
					  else{
						  if (check22.s && sat==200)
							  drawGradientLine(xL, yUFO, xL2, yL2, unidentifiedElementColor, sat);
						  else{
							  parent.stroke(unidentifiedElementColor.getRed(),unidentifiedElementColor.getGreen(),unidentifiedElementColor.getBlue(),sat);
							  parent.line(xL, yUFO, xL2, yL2);
						  }
					}
				  }
				  float yRect2 = iY[i2].value-iH[i2].value/2;
				  if (check24.s && sat==200)
					  drawGradientLine(xL2, yL2, xRect, yRect2, complexRectionColor, sat);
				  else{	
					  parent.stroke(complexRectionColor.getRed(),complexRectionColor.getGreen(),complexRectionColor.getBlue(),sat);
					  parent.line(xL2, yL2, xRect, yRect2);
				  }
			  }
			  else if (unidentifiedList.contains(sLeft[i3].toString())){
				  float y5 = iY[i2].value-iH[i2].value/2;
				  if (check22.s && sat==200)
					  drawGradientLine(xL, yUFO, xRect, y5, unidentifiedElementColor, sat);
				  else{
					  parent.stroke(unidentifiedElementColor.getRed(),unidentifiedElementColor.getGreen(),unidentifiedElementColor.getBlue(),sat);
					  parent.line(xL, yUFO, xRect, y5);
				  }
			  }
			  else{
				System.out.println("drawReactionLink Left: CAN NOT FIND ="+sLeft[i3]);
			  }
		  }

		   
		  Object[] sRight = rect.getRight().toArray();
		  for (int i3=0;i3<sRight.length;i3++){
			  String name = main.MainMatrixVersion_1_2.getProteinName(sRight[i3].toString());
			  if (mapProteinRDFId_index.get(name)!=null){
				  parent.stroke(proteinRectionColor.getRed(),proteinRectionColor.getGreen(),proteinRectionColor.getBlue(),sat);
				  float y5 = iY[i2].value-iH[i2].value/2;
				  float y6 = iP[mapProteinRDFId_index.get(name)].value-hProtein/4f;
				  if (check21.s && main.MainMatrixVersion_1_2.isSmallMolecule(name))
					  drawGradientLine(xRect, y5, xR, y6, smallMoleculeColor, sat);
				  else if (check25.s && !main.MainMatrixVersion_1_2.isSmallMolecule(name) && sat==200){
					  drawGradientLine(xRect, y5, xR, y6, proteinRectionColor, sat);
				  }
						
				  else{
					  if (main.MainMatrixVersion_1_2.isSmallMolecule(name))
							parent.stroke(smallMoleculeColor.getRed(),smallMoleculeColor.getGreen(),smallMoleculeColor.getBlue(),sat);
					  parent.line(xRect, y5,xR, y6);
				  }	  
			  }
			  else if (main.MainMatrixVersion_1_2.mapComplexRDFId_index.get(sRight[i3].toString())!=null){
				  int id = main.MainMatrixVersion_1_2.mapComplexRDFId_index.get(sRight[i3].toString());
				  ArrayList<String> components = main.MainMatrixVersion_1_2.proteinsInComplex[id];
				  float yR2 = 0;
				  int numAvailableComponents = 0;
				  for (int k=0;k<components.size();k++){
					  if (mapProteinRDFId_index.get(components.get(k))!=null){
						  yR2+= iP[mapProteinRDFId_index.get(components.get(k))].value-hProtein/4f;
						  numAvailableComponents++;
					  }	  
				  }
				  if (numAvailableComponents==0)
					  yR2 =iY[i2].value-iH[i2].value/2;
				  else 	  
					  yR2 /= numAvailableComponents;
				  
				 
				  float yRect2 = iY[i2].value-iH[i2].value/2;
				  if (check24.s && sat==200)
					  drawGradientLine(xRect, yRect2, xR2, yR2, complexRectionColor, sat);
				  else{	  
					  parent.stroke(complexRectionColor.getRed(),complexRectionColor.getGreen(),complexRectionColor.getBlue(),sat);
					  parent.line(xRect, yRect2, xR2, yR2);
				  }
				  
				  for (int k=0;k<components.size();k++){
					  parent.stroke(formComplexColor.getRed(), formComplexColor.getGreen(), formComplexColor.getBlue(),sat);
					  if (mapProteinRDFId_index.get(components.get(k))!=null){
						  float y4=iP[mapProteinRDFId_index.get(components.get(k))].value-hProtein/4f;
						  if (check23.s && sat==200)
							  drawGradientLine(xR2, yR2, xR, y4, formComplexColor, sat);
						  else
							  parent.line(xR2, yR2, xR, y4);
					  }
					  else{
						  if (check22.s && sat==200)
							  drawGradientLine(xR2, yR2, xR, yUFO, unidentifiedElementColor, sat);
						  else{
							  parent.stroke(unidentifiedElementColor.getRed(),unidentifiedElementColor.getGreen(),unidentifiedElementColor.getBlue(),sat);
							  parent.line(xR2, yR2, xR, yUFO);
						  }
					  }
					  
				  }
			  }
			  else if (unidentifiedList.contains(sRight[i3].toString())){
				  float y5 = iY[i2].value-iH[i2].value/2;
				  if (check22.s && sat==200)
					  drawGradientLine(xRect, y5, xR, yUFO, unidentifiedElementColor, sat);
				  else{
					  parent.stroke(unidentifiedElementColor.getRed(),unidentifiedElementColor.getGreen(),unidentifiedElementColor.getBlue(),sat);
					  parent.line(xRect, y5, xR, yUFO);
				  }
				  
			  }
			  else{
					 System.out.println("drawReactionLink Right: CAN NOT FIND ="+sRight[i3]);
			  }
			  
		  }
	 }
		
	public void mouseClicked() {
		 if (bPopup)
			 sPopup = !sPopup;
		if (bRect!=s)
			s = bRect;
		else
			s =-200;
		
		
	}
	 
	public void checkBrushing() {
		if (rectHash==null || iH==null || iH.length==0) return;
		
		int mX = parent.mouseX;
		int mY = parent.mouseY;
		if (xButton<mX && mX<xButton+w1 && 0<=mY && mY<=yBegin){
			bPopup=true;
			return;
		}	
		else if (sPopup){
			bPopup=false;		
			for (int i=0; i<rectHash.size(); i++){
				if (xRect-50<=mX && mX<=xRect+50 && iY[i].value-iH[i].value<=mY && mY<=iY[i].value){
					if (textbox1.searchText.equals("") || (!textbox1.searchText.equals("") && sRectList.indexOf(i)>=0)) {
						bRect =i;
						hightlightList[i] = 1; 
						return;
					}
				}	
			}
		}
		bPopup=false;		
		bRect =-100;
	}
	
}