package main;

import java.awt.Color;
import java.util.ArrayList;
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
	public Map<String,String> mapProteinRDFId;
	public Map<String,String> mapSmallMoleculeRDFId;
	public Map<String,String> mapComplexRDFId;
	public Map<String,Complex> mapComplexRDFId_Complex;
	public Set<SmallMolecule> smallMoleculeSet;
	
	public ArrayList<String> complexList = new ArrayList<String>(); 
	public ArrayList<String> proteinList = new ArrayList<String>();
	
	public ArrayList<BiochemicalReaction> rectList;
	public ArrayList<Integer> rectSizeList;
	public ArrayList<Integer> rectFileList;
	public int[] pathwaySize;
	public ArrayList<Integer> rectOrderList;
	
	
	public int maxSize = 0;
	public Gradient gradient = new Gradient();
	public float colorScale=0;
	public static Integrator[][] iS;
	public float xCircular, yCircular, rCircular; 
	
	
	
	
	public MultipleReactionView(PApplet p){
		parent = p;
		float v=0.5f;
		gradient.addColor(new Color(0,0,v));
		gradient.addColor(new Color(0,v,v));
		gradient.addColor(new Color(0,v,0));
		gradient.addColor(new Color(v,v,0));
		gradient.addColor(new Color(v,0,0));
		gradient.addColor(new Color(v,0,v));
		gradient.addColor(new Color(0,0,v));
	}
	
	public void setItems(){
		// Causality integrator
		iS = new Integrator[rectList.size()][rectList.size()];
		for (int i=0;i<rectList.size();i++){
			for (int j=0;j<rectList.size();j++){
				iS[i][j] = new Integrator(0, 0.2f,SliderSpeed.speed/2);
			}
		}	
		
		
	
		// Compute proteinList and complexList
		complexList = new ArrayList<String>(); 
		proteinList = new ArrayList<String>();
		for (int r=0; r<rectList.size();r++){
			BiochemicalReaction react = rectList.get(r);
			Object[] left = react.getLeft().toArray();
			Object[] right = react.getRight().toArray();
			for (int i=0;i<left.length;i++){
				String ref = left[i].toString();
				  if ( mapProteinRDFId.get(ref)!=null){
					  String proteinName = mapProteinRDFId.get(ref);
					  if (!proteinList.contains(proteinName))
							proteinList.add(proteinName);
				  }	  
				  else if (mapComplexRDFId.get(ref)!=null){
					  String complexName = mapComplexRDFId.get(ref);
					  if (!complexList.contains(complexName))
						  complexList.add(complexName);
				  }
			}
			for (int i=0;i<right.length;i++){
				String ref = right[i].toString();
				  if ( mapProteinRDFId.get(ref)!=null){
					  String proteinName = mapProteinRDFId.get(ref);
					  if (!proteinList.contains(proteinName))
							proteinList.add(proteinName);
				  }	  
				  else if (mapComplexRDFId.get(ref)!=null){
					  String complexName = mapComplexRDFId.get(ref);
					  if (!complexList.contains(complexName))
						  complexList.add(complexName);
				  }
			}
		}
		
		// Compute size of reaction
		maxSize =0;
		for (int r=0; r<rectList.size();r++){
			BiochemicalReaction react = rectList.get(r);
			Object[] left = react.getLeft().toArray();
			Object[] right = react.getRight().toArray();
			
			ArrayList<Integer> proteinsL = getProteinsInOneSideOfReaction(left);
			ArrayList<Integer> proteinsR = getProteinsInOneSideOfReaction(right);
			int size = proteinsL.size()+ proteinsR.size();
			rectSizeList.add(size);   
			if (size>maxSize)
				maxSize = size;
		}
			
		colorScale = (float) gradient.colors.size()/ (nFiles+1) ;
		isAllowedDrawing = true;
	}
	
	
	public ArrayList<Integer> getProteinsInOneSideOfReaction(Object[] s) {
		ArrayList<Integer> a = new ArrayList<Integer>();
		for (int i3=0;i3<s.length;i3++){
			  String ref = s[i3].toString();
			  if (mapProteinRDFId.get(ref)!=null){
				  String proteinName = mapProteinRDFId.get(ref);
				  int index = proteinList.indexOf(proteinName);
				  a.add(index);
			  }
			  else  if (mapComplexRDFId.get(ref)!=null){
				  ArrayList<String> components = getProteinsInComplexRDFId(ref);
				  for (int k=0;k<components.size();k++){
					  String proteinName = mapComplexRDFId.get(components.get(k));
					  int index = proteinList.indexOf(proteinName);
					  a.add(index);
				  }
			  }
			  else{
				  System.out.println("getProteinsInOneSideOfReaction: CAN NOT FIND ="+s[i3]+"-----SOMETHING WRONG");
			 } 
		  }
		return a;
	}
	
	public ArrayList<String> getProteinsInComplexRDFId(String ref){	
		ArrayList<String> components = new ArrayList<String>(); 
		Complex com = mapComplexRDFId_Complex.get(ref);
		Object[] s2 = com.getComponent().toArray();
		for (int i=0;i<s2.length;i++){
			String ref2 = s2[i].toString();
			 if (mapProteinRDFId.get(ref2)!=null)
				  components.add(mapProteinRDFId.get(ref2));
			 else if (mapComplexRDFId.get(ref2)!=null){
				  ArrayList<String> s4 = getProteinsInComplexRDFId(ref2);
				  for (int k=0;k<s4.size();k++){
					  components.add(s4.get(k));
				  }
			  }
		 }
		 return components;
	}
	
	
	
	public void draw(){
		if (!isAllowedDrawing) return;
		yCircular = parent.height/2;
		rCircular = parent.height*3/7;
		xCircular = rCircular+100;
		
		int count = 0;
		// Draw causality
		
		
		//System.out.println("nFiles="+nFiles+"	"+rectList.size());
		for (int r=0; r<rectSizeList.size();r++){
			int f = rectFileList.get(r);
			int index = rectOrderList.get(r);
			Color color = gradient.getGradient(colorScale*(f+(float)index/(pathwaySize[f]*2)));
			BiochemicalReaction react = rectList.get(r);
			float al = computeAlpha(r);
			float xR =  xCircular+rCircular*PApplet.sin(al);
			float yR =  yCircular+rCircular*PApplet.cos(al);
			float radius = PApplet.map(PApplet.sqrt(rectSizeList.get(r)), 0, PApplet.sqrt(maxSize), 3, 15);  // 10 is the max radius
			
			//System.out.println("	al="+al+"	f="+f+"	yR="+yR+"	"+radius);
			
			parent.fill(color.getRGB());
			parent.noStroke();
			parent.ellipse(xR, yR, radius, radius);
			count++;
		}
		
		for (int r1=0; r1<rectList.size();r1++){
			ArrayList<Integer> processedList =  new ArrayList<Integer>();
			drawDownStreamReaction(r1, processedList, 255);
		}
	}
	
	public float computeAlpha(int r){
		return PApplet.PI -((float)r)/(rectList.size())*2*PApplet.PI;
	}
		
	
	public void drawDownStreamReaction(int r, ArrayList<Integer> processedList, float sat){
		BiochemicalReaction rectSelected = rectList.get(r);
		Object[] sRight1 = rectSelected.getRight().toArray();
		for (int g=0;g<rectList.size();g++) {
			if(g==r) continue;
			BiochemicalReaction rect2 = rectList.get(g);
			Object[] sLeft2 = rect2.getLeft().toArray();
			ArrayList<String> commonElements = compareInputOutput(sRight1, sLeft2);
			if (commonElements.size()>0){
				if (r<g)
					drawCircularRelationship(r,g,Color.MAGENTA,4);
				else
					drawCircularRelationship(g,r,Color.GREEN,4);
			}
		}
	}
	
	public ArrayList<String> compareInputOutput(Object[] a, Object[] b){
		ArrayList<String> results = new ArrayList<String>();
		for (int i=0; i<a.length;i++){
			String ref1 = a[i].toString();
			if (mapProteinRDFId.get(ref1)!=null){
				String proteinName1 = mapProteinRDFId.get(ref1);
				for (int j=0; j<b.length;j++){
					String ref2 = b[j].toString();
					String proteinName2 = mapProteinRDFId.get(ref2);
					if (proteinName2!=null && proteinName1.equals(proteinName2) 
							&& !mapSmallMoleculeRDFId.containsValue(proteinName1)){
							 results.add(proteinName1);
					}	
				}
			}
			else if (mapComplexRDFId.get(ref1)!=null){
				String complexName1 = mapComplexRDFId.get(ref1);
				for (int j=0; j<b.length;j++){
					String ref2 = b[j].toString();
					String complexName2 = mapComplexRDFId.get(ref2);
					if (complexName2!=null && complexName1.equals(complexName2)){
						 results.add(complexName1);
					}	
				}
			}
			
		}
		return results;
	}
		
	public void drawCircularRelationship(int r1, int r2, Color color, float maxWeight){
		float a1 = computeAlpha(r1);
		float x1 =  xCircular+rCircular*PApplet.sin(a1);
		float y1 =  yCircular+rCircular*PApplet.cos(a1);
		
		float a2 = computeAlpha(r2);
		float x2 =  xCircular+rCircular*PApplet.sin(a2);
		float y2 =  yCircular+rCircular*PApplet.cos(a2);
		
		
		float alpha = (y2-y1)/(x2-x1);
		alpha = PApplet.atan(alpha);
		float dis = (y2-y1)*(y2-y1)+(x2-x1)*(x2-x1);
		float dd = PApplet.sqrt(dis);
		float wei = PApplet.map(maxWeight, 0, maxWeight, 0, 191);
		if (wei>191){
			return;
		}
		float strokeWeight = wei/100; 
		
		float alCircular =0;
		float d3, x3, y3, newR;
		if (r2-r1<=rectList.size()/2){
			 alCircular = PApplet.PI-((float) (r2-r1)*2/rectList.size())*PApplet.PI;
			 if (alCircular==0)       // Straight line
				 alCircular = 0.01f; 
			 newR = (dd/2)/PApplet.sin(alCircular/2);
	    	 d3 = PApplet.dist(x1,y1,x2,y2);
			 x3 = (x1+x2)/2 - ((y1-y2)/2)*PApplet.sqrt(PApplet.pow(newR*2/d3,2)-1);
			 y3 = (y1+y2)/2 + ((x1-x2)/2)*PApplet.sqrt(PApplet.pow(newR*2/d3,2)-1);
		}
		else{ // relationship of 2 wordcloud away
			 alCircular = ((float) (r2-r1)*2/rectList.size())*PApplet.PI-PApplet.PI;
			 newR = (dd/2)/PApplet.sin(alCircular/2);
			 d3 = PApplet.dist(x1,y1,x2,y2);
			 x3 = (x1+x2)/2 + ((y1-y2)/2)*PApplet.sqrt(PApplet.pow(newR*2/d3,2)-1);
			 y3 = (y1+y2)/2 - ((x1-x2)/2)*PApplet.sqrt(PApplet.pow(newR*2/d3,2)-1);
			 
			 //parent.strokeWeight(1);
			 //	parent.stroke(0,50);
			 //	parent.line(x1,y1,x2,y2);
				
		}
		
		float delX1 = (x1-x3);
		float delY1 = (y1-y3);
		float delX2 = (x2-x3);
		float delY2 = (y2-y3);
		float al1 = PApplet.atan2(delY1,delX1);
		float al2 = PApplet.atan2(delY2,delX2);
		if (al1-al2>PApplet.PI)
			al1=al1-2*PApplet.PI;
		if (al2-al1>PApplet.PI)
			al2=al2-2*PApplet.PI;
		
		// Check brushing a relationship
		/*
		float distCenter = PApplet.dist(this.mouseX, this.mouseY, WordCloud.xC, WordCloud.yC);
		float dist = PApplet.dist(this.mouseX, this.mouseY, x3, y3);
		if (distCenter<WordCloud.rC && (newR-1<=dist && dist<=newR+strokeWeight) && bWord1<0){
			bWord1 = i1;
			bWord2 = j1;
			bCloud1 = index1;
			bCloud2 = index2;
			
			bX3 = x3;
			bY3 = y3;
			bR3 = newR;
			if (al1<al2)	{	
				bAl1 = al1;
				bAl2 = al2;
			}	
			else{
				bAl1 = al2;
				bAl2 = al1;
			}
			
			bStrokeWeight=strokeWeight;
			bColor = color;
		}*/
		parent.noFill();
		parent.stroke(color.getRed(),color.getGreen(),color.getBlue(),wei);
		parent.strokeWeight(strokeWeight);
		if (al1<al2)		
			parent.arc(x3, y3, newR*2, newR*2, al1, al2);
		else
			parent.arc(x3, y3, newR*2, newR*2, al2, al1);
		parent.strokeWeight(1);
			
	}
}
	