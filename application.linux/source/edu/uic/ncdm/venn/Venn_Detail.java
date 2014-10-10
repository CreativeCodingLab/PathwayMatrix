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
import static main.MainViewer.minerList;
import static edu.uic.ncdm.venn.Venn_Overview.*;

public class Venn_Detail{
	int count = 0;
	public String[][] data;
    public double[] areas;
    
    public double[][] centers;
    public double[] diameters;
    public String[] labels;
    private float size;
    private double mins, maxs;
    public PApplet parent;
    public int brushing=-1;
    public float bx=0;
    public float by=0;
    public float br=0;
    
     ArrayList<String>[] pair3;
	
	public int numMiner=-1;
	
	public ArrayList<Integer> minerID;
	public ArrayList<String> minerNames;
	public float currentSize2=0;
	
	
	public Venn_Detail(PApplet pa) {
		parent = pa;
		centers = new double[0][0];	
	}  
    
	public void compute(int current) {
		// Select the list of miner
		numMiner = 0;
		minerID = new ArrayList<Integer>();
		minerNames = new ArrayList<String>();
		int currentIndex =-1;
		for (int j=0;j<minerList.size();j++){
			int countPair=0;
			for (int i2 =0; i2< main.MainViewer.pairs[current].size(); i2++){
				for (int j2 =0; j2< main.MainViewer.pairs[j].size(); j2++){
					if (main.MainViewer.pairs[current].get(i2).equals(main.MainViewer.pairs[j].get(j2)))
						countPair++;
				}
			}
			if (countPair>0){
				if (j==current) currentIndex=numMiner;
				minerID.add(j);
				minerNames.add(minerList.get(j).toString());
				numMiner++;
			}	
		}	
		//System.out.println(minerList.size()+" numMiner="+numMiner);
		
		data = new String[numMiner+numMiner*(numMiner-1)/2][1];
		areas =  new double[numMiner+numMiner*(numMiner-1)/2];		
		
		for (int i =0; i< numMiner; i++){
			data[i][0] = minerNames.get(i);
			areas[i]= main.MainViewer.pairs[minerID.get(i)].size();
		}
		
		int k=numMiner;
		for (int i =0; i< numMiner; i++){
			for (int j =i+1; j< numMiner; j++){
				for (int i2 =0; i2< main.MainViewer.pairs[minerID.get(i)].size(); i2++){
					for (int j2 =0; j2< main.MainViewer.pairs[minerID.get(j)].size(); j2++){
						if (main.MainViewer.pairs[minerID.get(i)].get(i2).equals
								(main.MainViewer.pairs[minerID.get(j)].get(j2))){
							areas[k]++;
						}
					}
				}	
				data[k][0] = minerNames.get(i)+"&"+minerNames.get(j);
				k++; 
			}
		}
		
		/*
 		System.out.println();
		System.out.println("----------- PRINT Venn Details    numMiner="+numMiner);
		for (int i =0; i< areas.length; i++){
			System.out.println(data[i][0]+" "+areas[i]);
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
	
	public void draw(float x4, float y4, int numSongs) {
		parent.noStroke();
		parent.textAlign(PApplet.CENTER);
		parent.textSize(11);
		
		brushing=-1;
		size = 400f;
		//System.out.println("size="+size+"	currentSize2="+currentSize2);
        for (int i = 0; i < centers.length; i++) {
        	float xi = (float) ((centers[i][0] - mins) / (maxs - mins));
            float yi = (float) ((centers[i][1] - mins) / (maxs - mins));
            float pi = (float) (diameters[i] / (maxs - mins));
        
            // Recompute scale to make sure Circles has same size as in the overview Venn
            float radius2 = (pi * size);
            
            if (minerID.get(i)==main.MainViewer.currentRelation){
            	currentSize2 = radius2;
            }
            float scale = (Venn_Overview.currentSize/currentSize2);
            radius2 *=scale;
            float x = x4 + scale*(xi * size);
            float y = y4 + scale*(yi * size);
           
            
            Color color = new Color(main.MainViewer.colorRelations[minerGlobalIDof[i]]);  
            
            if (PApplet.dist(x, y, parent.mouseX, parent.mouseY)<radius2/2 && brushing<0){
            	color = new Color(255,255,255,100);
            	brushing=i;
            }
            if (radius2>0){
        	   	parent.fill(color.getRed(), color.getGreen(), color.getBlue(),100);
        	   	parent.ellipse(x , y , radius2, radius2);
        	   	parent.fill(150,150,150);
        	  // 	if (deactive[i])
	          //  	parent.fill(50,50,50,50);
	            parent.text(labels[i], x , y+4);
           }
        }
        parent.fill(Color.GRAY.getRGB());
		parent.textSize(14);
		parent.textAlign(PApplet.LEFT);
	}

	
	 
	 
	public boolean mouseClicked() {
		return false;
	}

}
