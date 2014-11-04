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
	public static boolean sAll = false;
	public static int b = -1000;
	public PApplet parent;
	public float x = 800;
	public static int y = 0;
	public static int y2 = 0;
	public int w1 = 100;
	public int w = 500;
	public int h = 28;
	public int itemH = 18;
	public Color cGray  = new Color(240,240,240);
	public static int s=-100;
	public static int orderByRelation = -100;
	public static float maxSize = 0;
	
	public static Map<String, Integer> itemHash =  new HashMap<String, Integer>();
	
	public PopupComplex(PApplet parent_){
		parent = parent_;
	}
	
	public void setItems(){
		int i=0;
		y2=20;
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
		parent.rect(x,y,w1,25);
		parent.fill(0);
		parent.textAlign(PApplet.CENTER);
		parent.text("Complex",x+w1/2,y+18);
	
		
		if (b>=-1){
			parent.fill(100);
			parent.stroke(0);
			h=itemHash.size()*itemH+20;
			parent.rect(x, y2-itemH-4, w,h+itemH);
			
			int i=0;
			parent.textSize(13);
			
			// Draw another button
			if (sAll){
				parent.noStroke();
				parent.fill(0);
				parent.rect(x+10,y2-itemH+3,w-25,itemH);
				parent.fill(180);
			}
			else if (b==-1){
				parent.fill(255);
			}
			else{
				parent.fill(0);
			}
			parent.textAlign(PApplet.LEFT);
			parent.text("All complexes",x+50,y2-1);
			
			parent.textSize(12);
			for (Map.Entry<String, Integer> entry : itemHash.entrySet()) {
				if (i==s){
					parent.noStroke();
					parent.fill(0);
					parent.rect(x+10,y2+itemH*(i)+4,w-25,itemH+1);
				
					parent.fill(255,0,0);
				}
				else if (i==b){
					parent.fill(200,100,0);
				}
				else{
					parent.fill(0);
				}
				parent.textAlign(PApplet.LEFT);
				parent.text(entry.getKey(),x+50,y2+itemH*(i+1));
				float r = PApplet.map(PApplet.sqrt(entry.getValue()), 0, PApplet.sqrt(maxSize), 0, 18);
				
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
				parent.ellipse(x+30,y2+itemH*(i)+13, r, r);
			
				// Order By drawing
				if (i==orderByRelation){
					parent.fill(255);
					parent.rect(x+330,y2+itemH*(i)+14, 9, 8);
					parent.rect(x+340,y2+itemH*(i)+6, 9, 16);
					parent.rect(x+350,y2+itemH*(i)-2, 9, 24);
				}
				i++;
			}	
		}
		// Draw structures
		if (b>=0){
			int indexSet = getIndexInSet(b);
			
			drawRelationship(indexSet,b);
		}
		else if (b==-1){
			int i=0;
			for (Map.Entry<String, Integer> entry : itemHash.entrySet()) {
				drawRelationship2(i,b);
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
			
	 public void drawRelationship2(int indexSet, int indexHash) {
		 ArrayList<String> components = main.MainMatrix.getComplexById(indexSet);
		 for (int i=0;i<components.size();i++){
			 int indexSet2 = main.MainMatrix.getComplex_RDFId_to_id(components.get(i));
			 if (indexSet2>=0){
					 int indexHash2 = getIndexInHash(indexSet2);
					 float yy1 =  y2+itemH*indexHash+12;
					 float yy2 =  y2+itemH*indexHash2+12;
			
					 float xx = x+30;
					 float yy = (yy1+yy2)/2;
					 parent.noFill();
				
				parent.stroke(0,80);
				parent.strokeWeight(8);
				parent.arc(xx, yy, yy2-yy1,yy2-yy1, PApplet.PI/2, 3*PApplet.PI/2);
				drawRelationship2(indexSet2, indexHash2);
			}
		 }
		 
		 parent.strokeWeight(1);
	 }	
	
	 public void drawRelationship(int indexSet, int indexHash) {
		 ArrayList<String> components = main.MainMatrix.getComplexById(indexSet);
		 for (int i=0;i<components.size();i++){
			 int indexSet2 = main.MainMatrix.getComplex_RDFId_to_id(components.get(i));
			 if (indexSet2>=0){
					 int indexHash2 = getIndexInHash(indexSet2);
					 float yy1 =  y2+itemH*indexHash+12;
					 float yy2 =  y2+itemH*indexHash2+12;
			
					 float xx = x+30;
					 float yy = (yy1+yy2)/2;
					 parent.noFill();
				
				parent.stroke(200,100,0,150);
				parent.strokeWeight(8);
				parent.arc(xx, yy, yy2-yy1,yy2-yy1, PApplet.PI/2, 3*PApplet.PI/2);
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
		if (parent.key=='r' || parent.key=='R'){
			orderByRelation=b;
			Gene.orderByRelation(orderByRelation);
		}
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
		if (itemHash==null) return;
		int mX = parent.mouseX;
		int mY = parent.mouseY;
		if (b==-100){
			if (x<mX && mX<x+w1 && y<=mY && mY<=itemH+5){
				b =100;
				return;
			}	
		}
		else{
			for (int i=0; i<itemHash.size(); i++){
				if (x<=mX && mX<=x+w && y2-itemH-5<=mY && mY<=y2+4){
					b =-1;
					return;
				}
				if (x<=mX && mX<=x+w && y2+itemH*i<=mY && mY<=y2+itemH*(i+1)+6){
					b =i;
					return;
				}	
			}
		}
		b =-100;
	}
	
}