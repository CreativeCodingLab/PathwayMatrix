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

import org.biopax.paxtools.model.level3.Complex;
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
	
	public static Map<BiochemicalReaction, Integer> itemHash =  new HashMap<BiochemicalReaction, Integer>();
	float itemH2 = 0; // height of items in the reaction
	
	public String[] proteins = null;
	public static  Map<String,Integer> mapComplexRDFId_index;
	
	public ArrayList<Integer> bProteinLeft = new ArrayList<Integer>();
	public ArrayList<Integer> bProteinRight = new ArrayList<Integer>();
	public Integrator[] iP;
	
	public static CheckBox check1;
	public static CheckBox check5;
	//public static CheckBox checkGroup;
	public static TextBox textbox1; 
	
	public float xL = x;
	public float xL2 = xL+200;
	public float xRect = x+400;
	public float xR = x+800;
	public float xR2 = xR-200;
	
	public static Color smallMoleculeColor = new Color(150,150,0);
	public static Color formComplexColor = new Color(0,100,150);
	public static Color complexRectionColor = new Color(0,0,180);
	public static Color proteinRectionColor = new Color(180,0,0);
	
	public static WordCloud wordCloud;
	public static int numTop =30;
	public PopupReaction(PApplet parent_){
		parent = parent_;
		check1 = new CheckBox(parent, "Fade links of small molecules");
		check5 = new CheckBox(parent, "Rearrange reactions");
		textbox1 = new TextBox(parent, "Search");
		wordCloud = new WordCloud(parent, 10,290,200,parent.height-300);
		
		
	}
	
	public void setItems(){
		int i=0;
		maxSize =0;
		Map<BiochemicalReaction, Integer> unsortMap  =  new HashMap<BiochemicalReaction, Integer>();
		s=-400;
		for (BiochemicalReaction current : main.MainMatrix.reactionSet){
			Object[] s = current.getLeft().toArray();
			
			// compute size of reaction
			int size = 0;
			for (int i3=0;i3<s.length;i3++){
				  String name = main.MainMatrix.getProteinName(s[i3].toString());
				  if (name!=null){
					  size++;
				  }	  
				  else if (main.MainMatrix.mapComplexRDFId_index.get(s[i3].toString())!=null){
					  int id = main.MainMatrix.mapComplexRDFId_index.get(s[i3].toString());
					  ArrayList<String> components = main.MainMatrix.proteinsInComplex[id];
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
		itemHash = sortByComparator(unsortMap);
		
		// Word cloud
		WordCount wc1 = new WordCount(numTop);
		ArrayList<String> a = new ArrayList<String>();
		for (Map.Entry<BiochemicalReaction, Integer> entry : itemHash.entrySet()) {
			String rectName = entry.getKey().getDisplayName();
			String[] pieces = rectName.split(" ");
			for (int k=0;k<pieces.length;k++){
				a.add(pieces[k].trim());
			}
		}
			
		wc1.countNames(a); 
		wordCloud.updateTags(wc1.wordArray, wc1.counts);
		
		// positions of items
		iX = new Integrator[itemHash.size()];
		iY = new Integrator[itemHash.size()];
		iH = new Integrator[itemHash.size()];
		for (i=0;i<itemHash.size();i++){
			iX[i] = new Integrator(x, 0.5f,0.1f);
			iY[i] = new Integrator(20, 0.5f,0.1f);
			iH[i] = new Integrator(10, 0.5f,0.1f);
		}
		
		hightlightList =  new int[itemHash.size()];
		for (i=0;i<itemHash.size();i++){
			hightlightList[i] = -1;
		}
			
		proteins =  new String[main.MainMatrix.ggg.size()];
		mapComplexRDFId_index = new HashMap<String,Integer>();
		iP =  new Integrator[main.MainMatrix.ggg.size()];
		for (int p=0; p<main.MainMatrix.ggg.size();p++){
			proteins[p] =  main.MainMatrix.ggg.get(p).name;
			mapComplexRDFId_index.put( main.MainMatrix.ggg.get(p).name, p);
			iP[p] =   new Integrator(20, 0.5f,0.1f);
		}
		updateProteinPositions();
	}
	
	
	
	public void updateProteinPositions(){
		hProtein = (parent.height-yBeginList)/(proteins.length);
		if (hProtein>maxH)
			hProtein =maxH;
		for (int p=0; p<proteins.length;p++){
			int order = main.MainMatrix.ggg.get(p).order;
			iP[p].target(yBeginList+hProtein*order);
		}
		updateReactionPositions();  /// **********Update reactions when updating proteins **********
	}
	
	public void countProteinParticipation(){
		
	}

	public void updateReactionPositions(){
		itemH2 = (parent.height-yBeginList)/(itemHash.size());
		// Compute positions
		if (itemH2>maxH)
			itemH2 =maxH;
		for (int i=0;i<itemHash.size();i++){
			iH[i].target(itemH2);
		}	
		
		if (check5.s){
			int indexOfItemHash=0;
			Map<Integer, Float> unsortMap  =  new HashMap<Integer, Float>();
			for (Map.Entry<BiochemicalReaction, Integer> entry : itemHash.entrySet()) {
				BiochemicalReaction rect = entry.getKey();
				Object[] aLeft = rect.getLeft().toArray();
				Object[] aRight = rect.getRight().toArray();
				ArrayList<Integer> proteinLeft = getProteinsInOneSideOfReaction(aLeft);
				ArrayList<Integer> proteinRight = getProteinsInOneSideOfReaction(aRight);
				
				float score = 0;
				float size = 0;
				for (int i=0; i<proteinLeft.size();i++){
					int pOrder = proteinLeft.get(i);
					if (!main.MainMatrix.isSmallMolecule(proteins[pOrder])) {// DO NOT order by small molecules
						score -= iP[pOrder].target;
						size++;
					}	
				}
				for (int i=0; i<proteinRight.size();i++){
					int pOrder = proteinRight.get(i);
					if (!main.MainMatrix.isSmallMolecule(proteins[pOrder])) {// DO NOT order by small molecules
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
			for (int i=0;i<itemHash.size();i++){
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
		x = x_-800;
		xL = x;
		xL2 = xL+150;
		xRect = x+400;
		xR = x+800;
		xR2 = xR-150;
		
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
		
		// Draw seach box
		textbox1.draw(xRect);
		
	}
	
	
	public void drawReactions(){
		if (proteins==null) return;
			for (int i=0;i<itemHash.size();i++){
				iY[i].update();
				iH[i].update();
			}
			
			// Draw another button
			parent.fill(0);
			parent.textSize(13);
			parent.textAlign(PApplet.CENTER);
			parent.text(itemHash.size()+" Reactions",xRect,45);
			
			// Draw proteins *****************************
			for (int p=0; p<proteins.length;p++){
				iP[p].update();
			}
			
			parent.fill(0);
			parent.textSize(13);
			parent.textAlign(PApplet.CENTER);
			parent.text("Input Proteins", xL, 45);
			parent.text("Input Complexes", xL2, 45);
			parent.text("Output Complexes", xR2, 45);
			parent.text("Output Proteins", xR, 45);
			
			
			int i4=0;
			bProteinLeft =  new ArrayList<Integer>();
			bProteinRight =  new ArrayList<Integer>();
			for (Map.Entry<BiochemicalReaction, Integer> entry : itemHash.entrySet()) {
				if (i4==bRect){
					BiochemicalReaction rect = entry.getKey();
					Object[] aLeft = rect.getLeft().toArray();
					Object[] aRight = rect.getRight().toArray();
					bProteinLeft = getProteinsInOneSideOfReaction(aLeft);
					bProteinRight = getProteinsInOneSideOfReaction(aRight);
				}
				else if (sRectList.indexOf(i4)>=0){
					BiochemicalReaction rect = entry.getKey();
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
					
				i4++;
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
			int i2=0;
			for (Map.Entry<BiochemicalReaction, Integer> entry : itemHash.entrySet()) {
				BiochemicalReaction rect = entry.getKey();
				if (bRect>=0 || !textbox1.searchText.equals(""))
					drawReactionLink(rect, i2, xL, xL2, xRect, xR, xR2, 25);
				else 
					drawReactionLink(rect, i2, xL, xL2, xRect, xR, xR2, 200);
				i2++;
			}
			
			System.out.println(sRectList);
			// Draw brushing reactions ***************
			if (bRect>=0 || sRectList.size()>0){
				int i3=0;
				for (Map.Entry<BiochemicalReaction, Integer> entry : itemHash.entrySet()) {
					if (i3==bRect || sRectList.indexOf(i3)>=0){
						BiochemicalReaction rect = entry.getKey();
						drawReactionLink(rect, i3, xL, xL2, xRect, xR, xR2, 255);
					}
					i3++;
				}
			}
			
			
			// Draw reaction Nodes **************************
			int i=0;
			for (Map.Entry<BiochemicalReaction, Integer> entry : itemHash.entrySet()) {
				if (bRect>=0)
					drawReactionNode(entry, i, 25);
				else
					drawReactionNode(entry, i, 200);
				i++;
			}	
			float x7 = (xR+150);
			float y7 = 70;
			float gap7 = 40;
			float step7 = 16;
			
			
			check5.draw((int) x7, (int) y7-20);
			check1.draw((int) x7, (int) y7);
			//draw color legend
			parent.textSize(12);
			parent.textAlign(PApplet.LEFT);
			parent.fill(smallMoleculeColor.getRGB());
			parent.stroke(smallMoleculeColor.getRGB());
			float x8 = x7+30;
			float y8 = y7+gap7;
			parent.text("Small Molecules",x8, y8);
			parent.line(x7, y8-5, x7+25, y8-5);
			
			parent.fill(formComplexColor.getRGB());
			parent.stroke(formComplexColor.getRGB());
			y8 += step7;
			parent.text("Complex formation",x8, y8);
			parent.line(x7, y8-5, x7+25, y8-5);
			
			parent.fill(complexRectionColor.getRGB());
			parent.stroke(complexRectionColor.getRGB());
			y8 += step7;
			parent.text("Complex reaction",x8, y8);
			parent.line(x7, y8-5, x7+25, y8-5);
			
			parent.fill(proteinRectionColor.getRGB());
			parent.stroke(proteinRectionColor.getRGB());
			y8 += step7;
			parent.text("Protein reaction",x8, y8);
			parent.line(x7, y8-5, x7+25, y8-5);
			
			// Draw word cloud
			wordCloud.x1=parent.width-200; 
			wordCloud.x2=parent.width; 
			wordCloud.draw(parent);
			
			int[][] rel =  new int[numTop][numTop];
			for (Map.Entry<BiochemicalReaction, Integer> entry : itemHash.entrySet()) {
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
		
		 for (int i=0;i<numTop;i++){
			float y1 = wc.words[i].y-wc.words[i].font_size/3.5f;
			for (int j=i+1;j<numTop;j++){
				float y2 = wc.words[j].y-wc.words[j].font_size/3.5f;
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
		 int brushing = wc.b;
		 if (brushing>=0){
		 	float y1 = wc.words[brushing].y-wc.words[brushing].font_size/3.5f;
			for (int j=0;j<numTop;j++){
				if (j==brushing) continue;
				float y2 = wc.words[j].y-wc.words[j].font_size/3.5f;
				float xx = wc.x1;
				float yy = (y1+y2)/2;
				parent.noFill();
				
				float maxWeight = max;
				if (max<=5){
					maxWeight = 6;
				}
				if (j>brushing){
					float wei = PApplet.map(rel[brushing][j], 0, maxWeight, 0, 191);
					parent.stroke(255,0,0,wei+64);
					parent.strokeWeight(wei/20);
					parent.arc(xx, yy, y2-y1,y2-y1, PApplet.PI/2, 3*PApplet.PI/2);
				}
				else{
					float wei = PApplet.map(rel[j][brushing], 0, maxWeight, 0, 191);
					parent.stroke(255,0,0,wei+64);
					parent.strokeWeight(wei/20);
					parent.arc(xx, yy, y1-y2,y1-y2, PApplet.PI/2, 3*PApplet.PI/2);
				}
			}
		 } 
	}
	
	public  void drawGradientLine(float x1, float y1, float x2, float y2, float sat) {
		int gap = 100;
		if (sat==255)
			gap=400;
		parent.noStroke();
		for (float x = 0; x <= gap; x=x+1f) {
			  float x3 = x1+x;	
			  float x4 = x2-x;	
			  float y3 = (x3-x1)*(y2-y1)/(float) (x2-x1) +y1;
			  float y4 = (x4-x1)*(y2-y1)/(float) (x2-x1) +y1;
			  //float dis1 = PApplet.min(x3-x1, x2-x3);
			  float alpha = PApplet.map(x, 0,gap, sat, 0);
			  if (alpha>1){
				   Color c = new Color(smallMoleculeColor.getRed(), smallMoleculeColor.getGreen(), smallMoleculeColor.getBlue(),(int) alpha);
				   parent.fill(c.getRGB());
				   parent.ellipse(x3,y3,1.25f,1.25f);
				   parent.ellipse(x4,y4,1.25f,1.25f);
			  } 
		}
	} 
	
	public ArrayList<Integer> getProteinsInOneSideOfReaction(Object[] s) {
		ArrayList<Integer> a = new ArrayList<Integer>();
		for (int i3=0;i3<s.length;i3++){
			  String name = main.MainMatrix.getProteinName(s[i3].toString());
			  if (name!=null){
				  if (mapComplexRDFId_index.get(name)!=null){
					  a.add(mapComplexRDFId_index.get(name));
				  }
				  else{
					  System.out.println("CAN NOT find protein = "+name+"	s[i3]="+s[i3]);
				  }
			  }	  
			  else{
				  if (main.MainMatrix.mapComplexRDFId_index.get(s[i3].toString())!=null){
					  int id = main.MainMatrix.mapComplexRDFId_index.get(s[i3].toString());
					  ArrayList<String> components = main.MainMatrix.proteinsInComplex[id];
					  for (int k=0;k<components.size();k++){
						  if (mapComplexRDFId_index.get(components.get(k))!=null){
							  a.add(mapComplexRDFId_index.get(components.get(k)));
						  }	  
					  }
				  }
				  //else
				//	  System.out.println("	Left "+(i3+1)+" = "+s[i3]);
			  }
		  }
		return a;
	}
		
	
	public void drawProteinLeft(int p, float sat) {
		float y3 = iP[p].value;
		float textSixe = PApplet.map(hProtein, 0, maxH, 2, 13);
		parent.textSize(textSixe);
		parent.fill(0,sat);
		if (main.MainMatrix.isSmallMolecule(proteins[p])){
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
		if (main.MainMatrix.isSmallMolecule(proteins[p])){
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
				parent.fill(150,0,0);
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
		Object[] s = rect.getLeft().toArray();
		  for (int i3=0;i3<s.length;i3++){
			  String name = main.MainMatrix.getProteinName(s[i3].toString());
			  if (name!=null){
				  if (mapComplexRDFId_index.get(name)!=null){
					  parent.stroke(proteinRectionColor.getRed(),proteinRectionColor.getGreen(),proteinRectionColor.getBlue(),sat);
					  float y5 = iP[mapComplexRDFId_index.get(name)].value-hProtein/4f;
					  float y6 = iY[i2].value-iH[i2].value/2;
					  if (check1.s && main.MainMatrix.isSmallMolecule(name))
						  drawGradientLine(xL, y5, xRect, y6,sat);
					  else  {
						  if (main.MainMatrix.isSmallMolecule(name))
								parent.stroke(smallMoleculeColor.getRed(),smallMoleculeColor.getGreen(),smallMoleculeColor.getBlue(),sat);
						  parent.line(xL, y5, xRect, y6);
					  }
						
				  }
				  else{
					  System.out.println("CAN NOT find protein = "+name+"	s[i3]="+s[i3]);
				  }
			  }	  
			 // getRDFId = http://www.reactome.org/biopax/50/453279#Complex59
			  else{
				  if (main.MainMatrix.mapComplexRDFId_index.get(s[i3].toString())!=null){
					  int id = main.MainMatrix.mapComplexRDFId_index.get(s[i3].toString());
					  //if (id<0)
						//  System.out.println("	Left "+(i3+1)+" = "+s[i3]);
						 
					  ArrayList<String> components = main.MainMatrix.proteinsInComplex[id];
					  
					  float yL2 = 0;
					  int numAvailableComponents = 0;
					  for (int k=0;k<components.size();k++){
						  if (mapComplexRDFId_index.get(components.get(k))!=null){
							  yL2+= iP[mapComplexRDFId_index.get(components.get(k))].value-hProtein/4f;
							  numAvailableComponents++;
						  }	  
					  }
					  if (numAvailableComponents==0)
						  yL2 =iY[i2].value-iH[i2].value/2;
					  else 	  
						  yL2 /= numAvailableComponents;
					  for (int k=0;k<components.size();k++){
						//	 System.out.println("		 contains "+components.get(k));
						  parent.stroke(formComplexColor.getRed(), formComplexColor.getGreen(), formComplexColor.getBlue(),sat);
						  if (mapComplexRDFId_index.get(components.get(k))!=null){
							  parent.line(xL, iP[mapComplexRDFId_index.get(components.get(k))].value-hProtein/4f, xL2, yL2);
						  }
					  }
					  parent.stroke(complexRectionColor.getRed(),complexRectionColor.getGreen(),complexRectionColor.getBlue(),sat);
					  parent.line(xL2, yL2, xRect, iY[i2].value-iH[i2].value/2);
				  }
				  //else
				//	  System.out.println("	Left "+(i3+1)+" = "+s[i3]);
			  }
		  }

		  Object[] s2 = rect.getRight().toArray();
		  for (int i3=0;i3<s2.length;i3++){
			  String name = main.MainMatrix.getProteinName(s2[i3].toString());
			  if (name!=null){
				  //System.out.println("	Right "+(i3+1)+" = "+name);
				  if (mapComplexRDFId_index.get(name)!=null){
					  parent.stroke(proteinRectionColor.getRed(),proteinRectionColor.getGreen(),proteinRectionColor.getBlue(),sat);
					  float y5 = iY[i2].value-iH[i2].value/2;
					  float y6 = iP[mapComplexRDFId_index.get(name)].value-hProtein/4f;
					  if (check1.s && main.MainMatrix.isSmallMolecule(name))
						  drawGradientLine(xRect, y5, xR, y6,sat);
					  else{
						  if (main.MainMatrix.isSmallMolecule(name))
								parent.stroke(smallMoleculeColor.getRed(),smallMoleculeColor.getGreen(),smallMoleculeColor.getBlue(),sat);
						  parent.line(xRect, y5,xR, y6);
					  }	  
				  }
			  }
			  else{
				  if (main.MainMatrix.mapComplexRDFId_index.get(s2[i3].toString())!=null){
					  int id = main.MainMatrix.mapComplexRDFId_index.get(s2[i3].toString());
					  ArrayList<String> components = main.MainMatrix.proteinsInComplex[id];
					  float yR2 = 0;
					  int numAvailableComponents = 0;
					  for (int k=0;k<components.size();k++){
						  if (mapComplexRDFId_index.get(components.get(k))!=null){
							  yR2+= iP[mapComplexRDFId_index.get(components.get(k))].value-hProtein/4f;
							  numAvailableComponents++;
						  }	  
					  }
					  if (numAvailableComponents==0)
						  yR2 =iY[i2].value-iH[i2].value/2;
					  else 	  
						  yR2 /= numAvailableComponents;
					  
					  parent.stroke(complexRectionColor.getRed(),complexRectionColor.getGreen(),complexRectionColor.getBlue(),sat);
					  parent.line(xRect, iY[i2].value-iH[i2].value/2, xR2, yR2);
				
					  for (int k=0;k<components.size();k++){
						  parent.stroke(formComplexColor.getRed(), formComplexColor.getGreen(), formComplexColor.getBlue(),sat);
						  if (mapComplexRDFId_index.get(components.get(k))!=null){
							  parent.line(xR2, yR2, xR, iP[mapComplexRDFId_index.get(components.get(k))].value-hProtein/4f);
						  }
					  }
						
				  }
				 // else		
				//	  System.out.println("	Right "+(i3+1)+" = "+s2[i3]);
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
		if (itemHash==null || iH==null || iH.length==0) return;
		int mX = parent.mouseX;
		int mY = parent.mouseY;
		if (xButton<mX && mX<xButton+w1 && 0<=mY && mY<=yBegin){
			bPopup=true;
			return;
		}	
		else if (sPopup){
			bPopup=false;		
			for (int i=0; i<itemHash.size(); i++){
				if (xRect-50<=mX && mX<=xRect+50 && iY[i].value-iH[i].value<=mY && mY<=iY[i].value){
					bRect =i;
					hightlightList[i] = 1; 
					return;
				}	
			}
		}
		bPopup=false;		
		bRect =-100;
	}
	
}