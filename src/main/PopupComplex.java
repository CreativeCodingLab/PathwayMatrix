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

import processing.core.PApplet;

public class PopupComplex{
	public static boolean bPopup = false;
	public static boolean sAll = false;
	public static int b = -1000;
	public PApplet parent;
	public float x = 800;
	public static float yBegin = 25;
	public static float yBeginList = 70;
	public int w1 = 100;
	public int w = 600;
	public int h = 28;
	public static int s=-100;
	public static float maxSize = 0;
	public Integrator[] iX, iY, iH;
	public int[] hightlightList;
	public float maxH = 20;
	
	
	public static Map<String, Integer> itemHash =  new HashMap<String, Integer>();
	
	public PopupComplex(PApplet parent_){
		parent = parent_;
	}
	
	public void setItems(){
		int i=0;
		maxSize =0;
		Map<String, Integer> unsortMap  =  new HashMap<String, Integer>();
		s=-400;
		for (Complex current : main.MainMatrix.complexSet){
			int size = main.MainMatrix.getAllGenesInComplexById(i).size();
			String name = current.getDisplayName();
			
			unsortMap.put(name, size);
			if (size>maxSize)
				maxSize = size;
			i++;
		}
		itemHash = sortByComparator(unsortMap);
		
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
			
	}
		
	// Sort decreasing order
	public static Map<String, Integer> sortByComparator(Map<String, Integer> unsortMap) {
		// Convert Map to List
		List<Map.Entry<String, Integer>> list = 
			new LinkedList<Map.Entry<String, Integer>>(unsortMap.entrySet());
 
		// Sort list with comparator, to compare the Map values
		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> o1,
                                           Map.Entry<String, Integer> o2) {
				return -(o1.getValue()).compareTo(o2.getValue());
			}
		});
 
		// Convert sorted map back to a Map
		Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
		for (Iterator<Map.Entry<String, Integer>> it = list.iterator(); it.hasNext();) {
			Map.Entry<String, Integer> entry = it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}
	
	public void draw(float x_){
		x = x_;
		checkBrushing();
		parent.textSize(13);
		parent.fill(125,125,125);
		parent.rect(x,0,w1,25);
		parent.fill(0);
		parent.textAlign(PApplet.CENTER);
		parent.text("Complex",x+w1/2,18);
	
		//
		if (hightlightList==null) return;
			
		int countLitems = 0;
		for (int i=0;i<hightlightList.length;i++){
			if (hightlightList[i]>=1){
				countLitems++;
			}
		}
			
		// Compute positions
		float itemH2 = (parent.height-yBeginList)/(itemHash.size());
		if (itemH2>maxH)
			itemH2 =maxH;
		for (int i=0;i<itemHash.size();i++){
			iY[i].target(yBeginList+i*itemH2);
			iH[i].target(itemH2);
		}
		
		for (int i=0;i<itemHash.size();i++){
			iY[i].update();
			iH[i].update();
		}
		
		
		if (bPopup == true || b>=-1){
			parent.fill(100);
			parent.stroke(0);
			parent.rect(x-200, yBegin, w+200,iY[itemHash.size()-1].value-10);
			
			int i=0;
			
			// Draw another button
			if (sAll){
				parent.noStroke();
				parent.fill(0);
				parent.rect(x+10,30,200,19);
				parent.fill(180);
			}
			else if (b==-1){
				parent.fill(255);
			}
			else{
				parent.fill(0);
			}
			parent.textSize(13);
			parent.textAlign(PApplet.LEFT);
			parent.text("All complexes",x+50,45);
			
			for (Map.Entry<String, Integer> entry : itemHash.entrySet()) {
				float textSixe = PApplet.map(iH[i].value, 0, 20, 5, 13);
				parent.textSize(textSixe);
				
				if (i==s){
					parent.noStroke();
					parent.fill(0);
					parent.rect(x+10,iY[i].value-iH[i].value,w-25,iH[i].value);
				
					parent.fill(255,0,0);
				}
				else if (i==b){
					parent.fill(200,100,0);
				}
				else{
					parent.fill(0);
				}
				parent.textAlign(PApplet.LEFT);
				parent.text(entry.getKey(),x+50,iY[i].value-iH[i].value/4);
				float r = PApplet.map(PApplet.sqrt(entry.getValue()), 0, PApplet.sqrt(maxSize), 0, maxH/2);
				
				parent.noStroke();
				if (i==s){
					parent.fill(255,0,0);
				}
				else if (i==b){
					parent.fill(255);
				}
				else{
					parent.fill(0);
				}
				parent.ellipse(x+30,iY[i].value-iH[i].value/2, r, r);
			
				i++;
			}	
		}
		// Draw structures
		if (b>=0){
			int indexSet = getIndexInSet(b);
			
			drawRelationship(indexSet,b, 200,100,0,150, true,0);
		}
		else if (b==-1){
			int i=0;
			for (Map.Entry<String, Integer> entry : itemHash.entrySet()) {
				int indexSet = getIndexInSet(i);
				drawRelationship(indexSet,i, 0,0,0,100, false, 0);
				i++;
			}
				
		}
			
	}
	 public int getIndexSetByName(String name) {
		 if (name.contains("Complex")){
			 int i=0;
			 for (Complex current : main.MainMatrix.complexSet){
				 if (current.getDisplayName().equals(name)){
					 return i;
				 }
			 }
			i++;		
		 }
		 return -33;
	 }
	 
	 public int getIndexHashByName(String name) {
		 if (name.contains("Complex")){
			 int i=0;
			 for (Map.Entry<String, Integer> entry : itemHash.entrySet()) {
				System.out.println(entry.getKey()+" name="+name); 
				 if (entry.getKey().equals(name)){
				  return i;
				 }
			 }
			i++;		
		 }
		 return -11;
	 }
	
	 public void drawRelationship(int indexSet, int indexHash, int r, int g, int b, int alpha, boolean recursive, int level) {
		 ArrayList<String> components = main.MainMatrix.getComplexById(indexSet);
		 for (int i=0;i<components.size();i++){
			 int indexSet2 = main.MainMatrix.getComplex_RDFId_to_id(components.get(i));
			 if (indexSet2>=0){
				int indexHash2 = getIndexInHash(indexSet2);
				float yy1 =  iY[indexHash].value-iH[indexHash].value/2;
				float yy2 =  iY[indexHash2].value-iH[indexHash2].value/2;
		
				float xx = x+30;
				float yy = (yy1+yy2)/2;
				parent.noFill();
				
				float size = PApplet.sqrt(main.MainMatrix.getAllGenesInComplexById(indexSet2).size());
				float thickness = PApplet.map(size, 0, PApplet.sqrt(maxSize), 0, maxH/2);
				
				int g2 = g+30*level;
				if (g2>255)
					g2=255;
				int b2 = b+30*level;
				if (b2>255)
					b2=255;
				
				parent.strokeWeight(thickness);
				parent.stroke(r,g2,b2,alpha);
				
				parent.arc(xx, yy, yy2-yy1,yy2-yy1, PApplet.PI/2, 3*PApplet.PI/2);
				
				if (recursive){
					drawRelationship(indexSet2,indexHash2, r,g,b,150, true, level+1);
				}
			}
		 }
		 
		 parent.strokeWeight(1);
	 	
		/* int max = 0;
			for (int i=0;i<numTop;i++){
				for (int j=i+1;j<numTop;j++){
					if (rel[i][j]>max)
						max = rel[i][j];
				}
			}*/	
			/*
			 for (int i=0;i<numTop;i++){
				float y1 = wc.words[i].y-wc.words[i].font_size/3.5f;
				for (int j=i+1;j<numTop;j++){
					float y2 = wc.words[j].y-wc.words[j].font_size/3.5f;
					float xx = wc.x1;
					float yy = (y1+y2)/2;
					this.noFill();
					
					float maxWeight = max;
					if (max<=5){
						maxWeight = 6;
					}
					
					float wei = PApplet.map(rel[i][j], 0, maxWeight, 0, 191);
					this.stroke(color.getRed(),color.getGreen(),color.getBlue(),wei);
					this.strokeWeight(wei/20);
					this.arc(xx, yy, y2-y1,y2-y1, PI/2, 3*PI/2);
				}
			}*/
			// Draw relationship of brushing term
		 /*
			 int brushing = wc.b;
			 if (brushing>=0){
			 	float y1 = wc.words[brushing].y-wc.words[brushing].font_size/3.5f;
				for (int j=0;j<numTop;j++){
					if (j==brushing) continue;
					float y2 = wc.words[j].y-wc.words[j].font_size/3.5f;
					float xx = wc.x1;
					float yy = (y1+y2)/2;
					this.noFill();
					
					float maxWeight = max;
					if (max<=5){
						maxWeight = 6;
					}
					if (j>brushing){
						float wei = PApplet.map(rel[brushing][j], 0, maxWeight, 0, 191);
						this.stroke(255,wei+64);
						this.strokeWeight(wei/20);
						this.arc(xx, yy, y2-y1,y2-y1, PI/2, 3*PI/2);
					}
					else{
						float wei = PApplet.map(rel[j][brushing], 0, maxWeight, 0, 191);
						this.stroke(255,wei+64);
						this.strokeWeight(wei/20);
						this.arc(xx, yy, y1-y2,y1-y2, PI/2, 3*PI/2);
					}
				}
			 } 
		*/ 
	 }
		
	
	public int getIndexInSet(int brushing) {
		String name = "";
		int i=0;
		for (Map.Entry<String, Integer> entry : itemHash.entrySet()) {
			if (i==brushing){
				name = entry.getKey();
			}
			i++;
		}	
		
		i=0;
		for (Complex current : main.MainMatrix.complexSet){
			if (current.getDisplayName().equals(name))
				return i;
			i++;
		}
		return -5;	
	}
	
	public int getIndexInHash(int indexSet) {
		int i=0;
		String name = "";
		for (Complex current : main.MainMatrix.complexSet){
			if (indexSet==i)
				name = current.getDisplayName();
			i++;
		}
		
		i=0;
		for (Map.Entry<String, Integer> entry : itemHash.entrySet()) {
			if (entry.getKey().equals(name)){
				return i;
			}
			i++;
		}	
		return -5;	
	}
		
	 public void mouseClicked() {
		if (b==-1){
			sAll = !sAll;
		}
		else{
			if (b!=s)
				s = b;
			else
				s =-200;
		}
		
	}
	 
	public void checkBrushing() {
		if (itemHash==null || iH==null || iH.length==0) return;
		int mX = parent.mouseX;
		int mY = parent.mouseY;
		if (x<mX && mX<x+w1 && 0<=mY && mY<=yBegin){
			bPopup=true;
			return;
		}	
		else{
			if (x<mX && mX<x+w1 && yBegin<=mY && mY<=iY[0].value-iH[0].value){
				b=-1;
				return;
			}	
			for (int i=0; i<itemHash.size(); i++){
				if (x<=mX && mX<=x+w && iY[i].value-iH[i].value<=mY && mY<=iY[i].value){
					b =i;
					hightlightList[i] = 1; 
					return;
				}	
			}
		}
		bPopup=false;		
		b =-100;
	}
	
}