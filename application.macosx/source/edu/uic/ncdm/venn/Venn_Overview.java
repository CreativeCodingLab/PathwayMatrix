package edu.uic.ncdm.venn;
/*
 * EVL temperature visualization.
 *
 * Copyright 2011 by Tuan Dang.
 *
 * The contents of this file are subject to the Mozilla Public License Version 2.0 (the "License")
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the License.
 */

import processing.core.*;

import java.awt.Color;
import java.util.ArrayList;

import edu.uic.ncdm.venn.data.VennData;
import static main.MainMatrixVersion_1_2.minerList;
import static main.MainMatrixVersion_1_2.minerNames;

public class Venn_Overview{
	int count = 0;
	public String[][] data;
    public double[] areas;
    
    public double[][] centers;
    public double[] diameters;
    public String[] labels;
    private int size;
    private double mins, maxs;
    public PApplet parent;
    public int brushing=-1;
    public float bx=0;
    public float by=0;
    public float br=0;
    
    public ArrayList<String>[] pair2;
	public boolean[] deactive;
   
	public static int numMinerContainData=-1;
	public static int[] minerGlobalIDof;
	public static float currentSize;
	
	
	public Venn_Overview(PApplet pa) {
		parent = pa;
		centers = new double[0][0];	
	}  
    
	public void initialize() {
		// Select the list of miner
		numMinerContainData = 0;
		for (int i=0;i<minerList.size();i++){
			if (main.MainMatrixVersion_1_2.pairs[i].size()>0)
				numMinerContainData++;
		}	
		if (numMinerContainData==0) return;
			
		//System.out.println(minerList.size()+" numMiner="+numMiner);
		
		pair2 = new ArrayList[numMinerContainData];
		minerGlobalIDof = new int[numMinerContainData];
		minerNames = new String[numMinerContainData];
		int count =0;
		for (int i=0;i<minerList.size();i++){
			if (main.MainMatrixVersion_1_2.pairs[i].size()>0){
				minerGlobalIDof[count] = i;
				pair2[count] = main.MainMatrixVersion_1_2.pairs[i];
				minerNames[count] = ""+minerList.get(i);
				count++;
			}	           
		}
		deactive = new boolean[numMinerContainData];
		
	}
		
	public void compute() {
		
		// Obtain relation of intersections
		ArrayList<String> aData = new ArrayList<String>();
		ArrayList<Integer> aAreas = new ArrayList<Integer>();
		for (int i =0; i< numMinerContainData; i++){
			for (int j =i+1; j< numMinerContainData; j++){
				int countRelations=0;
				// Pair 2 is a new array
				for (int i2 =0; i2< pair2[i].size(); i2++){
					for (int j2 =0; j2< pair2[j].size(); j2++){
						if (pair2[i].get(i2).equals(pair2[j].get(j2))){
							countRelations++;		
						}
					}
				}	
				if (countRelations>0){
					aData.add(minerNames[i]+"&"+minerNames[j]);
					aAreas.add(countRelations);
				}
			}
		}
		
		data = new String[numMinerContainData+aData.size()][1];
		areas =  new double[numMinerContainData+aData.size()];		
		for (int i =0; i< numMinerContainData; i++){
			data[i][0] = minerNames[i];
			areas[i]=pair2[i].size();
		}
		for (int i =0; i< aData.size(); i++){
			data[i+numMinerContainData][0] = aData.get(i);
			areas[i+numMinerContainData] = aAreas.get(i);
		}
			
		
		
		/*
		System.out.println();
		System.out.println("****** PRINT Venn Overview   numMiner="+numMiner);
		for (int i =0; i< areas.length; i++){
			System.out.println("\t"+data[i][0]+"	"+areas[i]);
		}
		System.out.println();
		*/
			
       VennData dv = new VennData(data, areas);
	   VennAnalytic va = new VennAnalytic();
       VennDiagram venn = va.compute(dv);
     	
       centers = venn.centers;
       diameters = venn.diameters;
       labels = venn.circleLabels;
       mins = Double.POSITIVE_INFINITY;
       maxs = Double.NEGATIVE_INFINITY;
       for (int i = 0; i < centers.length; i++) {
    	   double margin = diameters[i] / 2;
           mins = Math.min(centers[i][0] - margin, mins);
           mins = Math.min(centers[i][1] - margin, mins);
           maxs = Math.max(centers[i][0] + margin, maxs);
           maxs = Math.max(centers[i][1] + margin, maxs);
       }
	}
	
	public static int globalToLocal(int id) {
		if (minerGlobalIDof==null) return -99;
		for (int i=0;i<minerGlobalIDof.length;i++){
			if (Venn_Overview.minerGlobalIDof[i]==id){
				return i;
			}	
		}
		return -100;
	}
	
	/*
	public static Color getMinerColor(int miner) {
		Color color = Color.WHITE;
		if (minerNames==null || miner>=minerNames.length || miner<0)
			return Color.GRAY;
		String name = minerNames[miner];
		if (name==null)
			return Color.WHITE;
		if (name.equals("in-complex-with"))
			color = Color.CYAN;
		else if (name.equals("neighbor-of"))
			color = Color.BLUE;
		else if (name.equals("controls-state-change-of"))
			color = Color.RED;
		else if (name.equals("directed-relations"))
			color = Color.GREEN;
		else if (name.equals("chemical-affects-through-binding"))
			color = Color.YELLOW;	
		else if (name.equals("consumption-controlled-by"))
			color = Color.MAGENTA;	
		else if (name.equals("controls-transport-of"))
			color = Color.PINK;	
		return color;
	}*/
	
	/*
	public static Color getMinerColor(int miner) {
		Color color = Color.ORANGE;
		if (minerNames==null || miner>=minerNames.length || miner<0)
			return Color.GRAY;
		String name = minerNames[miner];
		if (name==null)
			return Color.BLACK;
		if (name.equals("in-complex-with"))
			color = new Color(0,200,200);
		else if (name.equals("neighbor-of"))
			color = Color.BLUE;
		else if (name.equals("controls-state-change-of"))
			color = new Color(220,0,0);
		else if (name.equals("directed-relations"))
			color = new Color(50,180,0); //color = Color.GREEN;
		else if (name.equals("chemical-affects-through-binding"))
			color = new Color(200,200,0); //color = Color.YELLOW;	
		else if (name.equals("consumption-controlled-by"))
			color = Color.MAGENTA.darker();	
		else if (name.equals("controls-transport-of"))
			color = Color.PINK.darker();	
		else if (name.equals("use-to-produce"))
			color = Color.PINK.darker();	
		return color;
	}
	*/
	
	public void draw(float xPanelRight, float yy4, int numSongs) {
		parent.noStroke();
		parent.textAlign(PApplet.CENTER);
		parent.textSize(11);
		
		brushing=-1;
		size = 400;
        for (int i = 0; i < centers.length; i++) {
            double xi = (centers[i][0] - mins) / (maxs - mins);
            double yi = (centers[i][1] - mins) / (maxs - mins);
            double pi = diameters[i] / (maxs - mins);
            int radius = (int) (pi * size);
            float x = xPanelRight+10+(int) (xi * size);
            float y = yy4 + (int) (yi * size);
            Color color = new Color(main.MainMatrixVersion_1_2.colorRelations[minerGlobalIDof[i]]);  
            
             // if (deactive[i])
           // 	color = new Color(255,255,255,10);
            
            if (minerGlobalIDof[i]==main.MainMatrixVersion_1_2.currentRelation){
            	currentSize = radius;
            }
            	
           if (radius>0){
        	   	parent.fill(color.getRed(), color.getGreen(), color.getBlue(),180);
        	   	if (PApplet.dist(x, y, parent.mouseX, parent.mouseY)<radius/2 && brushing<0){
        	   		parent.fill(color.getRed(), color.getGreen(), color.getBlue(),255);
            	   	brushing=i;
                }
              
        	   	parent.ellipse(x , y , radius, radius);
        	   	parent.fill(0);
        	   	//if (deactive[i])
	            //	parent.fill(50,50,50,50);
	            parent.text(labels[i], x , y+4);
           }
        }
        if (minerGlobalIDof!=null && brushing>=0)
        	main.MainMatrixVersion_1_2.currentRelation = minerGlobalIDof[brushing];
        else
        	main.MainMatrixVersion_1_2.currentRelation = -1;
        
        parent.fill(Color.GRAY.getRGB());
		parent.textSize(14);
		parent.textAlign(PApplet.LEFT);
	}

	
	 public static Color rainbow(double value, float transparency) {
	        /* blue to red, approximately by wavelength */
	        float v = (float) value * 255.f;
	        float vmin = 0;
	        float vmax = 255;
	        float range = vmax - vmin;

	        if (v < vmin + 0.25f * range)
	            return new Color(0.f, 4.f * (v - vmin) / range, 1.f, transparency);
	        else if (v < vmin + 0.5 * range)
	            return new Color(0.f, 1.f, 1.f + 4.f * (vmin + 0.25f * range - v) / range, transparency);
	        else if (v < vmin + 0.75 * range)
	            return new Color(4.f * (v - vmin - 0.5f * range) / range, 1.f, 0, transparency);
	        else
	            return new Color(1.f, 1.f + 4.f * (vmin + 0.75f * range - v) / range, 0, transparency);
	    }
	 
	public boolean mouseClicked() {
		if (brushing>=0){
			main.MainMatrixVersion_1_2.currentRelation = minerGlobalIDof[brushing];
			deactive[brushing] =! deactive[brushing];
			return true;
		}
		else{
			main.MainMatrixVersion_1_2.currentRelation = -2;
			return false;
		}	
	}

	
	
	// zoom in or out:
	void mouseWheel(int delta) {
		if (delta > 0) {
		} else if (delta < 0) {
		}
	}

}
