package main;
/*
 * DARPA project
 *
 * Copyright 2014 by Tuan Dang.
 *
 * The contents of this file are subject to the Mozilla Public License Version 2.0 (the "License")
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the License.
 */

import java.awt.Color;
import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.biopax.paxtools.io.SimpleIOHandler;
import org.biopax.paxtools.io.sbgn.L3ToSBGNPDConverter;
import org.biopax.paxtools.io.sif.SimpleInteractionConverter;
import org.biopax.paxtools.io.sif.level3.ControlRule;
import org.biopax.paxtools.model.BioPAXElement;
import org.biopax.paxtools.model.Model;
import org.biopax.paxtools.model.level3.*;
import org.biopax.paxtools.model.level3.Process;
import org.biopax.paxtools.pattern.Match;
import org.biopax.paxtools.pattern.Pattern;
import org.biopax.paxtools.pattern.Searcher;
import org.biopax.paxtools.pattern.miner.CSCOBothControllerAndParticipantMiner;
import org.biopax.paxtools.pattern.miner.CSCOButIsParticipantMiner;
import org.biopax.paxtools.pattern.miner.CSCOThroughBindingSmallMoleculeMiner;
import org.biopax.paxtools.pattern.miner.CSCOThroughControllingSmallMoleculeMiner;
import org.biopax.paxtools.pattern.miner.CSCOThroughDegradationMiner;
import org.biopax.paxtools.pattern.miner.CatalysisPrecedesMiner;
import org.biopax.paxtools.pattern.miner.ChemicalAffectsThroughBindingMiner;
import org.biopax.paxtools.pattern.miner.ChemicalAffectsThroughControlMiner;
import org.biopax.paxtools.pattern.miner.ConsumptionControlledByMiner;
import org.biopax.paxtools.pattern.miner.ControlsDegradationIndirectMiner;
import org.biopax.paxtools.pattern.miner.ControlsExpressionMiner;
import org.biopax.paxtools.pattern.miner.ControlsExpressionWithConvMiner;
import org.biopax.paxtools.pattern.miner.ControlsPhosphorylationMiner;
import org.biopax.paxtools.pattern.miner.ControlsProductionOfMiner;
import org.biopax.paxtools.pattern.miner.ControlsStateChangeDetailedMiner;
import org.biopax.paxtools.pattern.miner.ControlsStateChangeOfMiner;
import org.biopax.paxtools.pattern.miner.ControlsTransportMiner;
import org.biopax.paxtools.pattern.miner.ControlsTransportOfChemicalMiner;
import org.biopax.paxtools.pattern.miner.DirectedRelationMiner;
import org.biopax.paxtools.pattern.miner.IDFetcher;
import org.biopax.paxtools.pattern.miner.InComplexWithMiner;
import org.biopax.paxtools.pattern.miner.InteractsWithMiner;
import org.biopax.paxtools.pattern.miner.Miner;
import org.biopax.paxtools.pattern.miner.NeighborOfMiner;
import org.biopax.paxtools.pattern.miner.ReactsWithMiner;
import org.biopax.paxtools.pattern.miner.RelatedGenesOfInteractionsMiner;
import org.biopax.paxtools.pattern.miner.SIFInteraction;
import org.biopax.paxtools.pattern.miner.UbiquitousIDMiner;
import org.biopax.paxtools.pattern.miner.UsedToProduceMiner;
import org.biopax.paxtools.pattern.util.Blacklist;
import org.biopax.paxtools.pattern.util.HGNC;

import static edu.uic.ncdm.venn.Venn_Overview.*;
import edu.uic.ncdm.venn.Venn_Overview;

import processing.core.*;

public class PathwayViewer_2_8 extends PApplet {
	private static final long serialVersionUID = 1L;
	public int count = 0;
	
	public static List<Miner> minerList = new ArrayList<Miner>();
	public static int currentRelation = -1;
	public static int processingMiner = 0;
	//public String currentFile = "./level3/Pathway Commons.4.Reactome.BIOPAX.owl";
	//public String currentFile = "./level3/Regulation of DNA Replication.owl";
	public String currentFile = "./level3RAS/1_RAF-Cascade.owl";
	//public String currentFile = "";
	
	
	public static ButtonBrowse buttonBrowse;
	
	// Store the genes results
	public static ArrayList<String>[] pairs;
	public static ArrayList<Integer>[][] geneRelationList;
	public static int[][] gene_gene_InComplex; 
	public static int maxGeneInComplex; 
	
	// Global data
	public static String[] minerNames;
	public static ArrayList<Gene> ggg = new ArrayList<Gene>();
	
	public static Map<Integer, Integer> leaderSortedMap;
	public static ArrayList<Integer>[] locals = null;
	
	//public static ArrayList<Integrator> iW;
	// Contains the location and size of each gene to display
	public float size=0;
	public static float marginX = 100;
	public static float marginY = 100;
	public static String message="";
	
	public ThreadLoader1 loader1=new ThreadLoader1(this);
	public Thread thread1=new Thread(loader1);
	
	public ThreadLoader2 loader2=new ThreadLoader2(this);
	public Thread thread2=new Thread(loader2);
	
	public ThreadLoader3 loader3=new ThreadLoader3();
	public Thread thread3=new Thread(loader3);
	
	public ThreadLoader4 loader4=new ThreadLoader4(this);
	public Thread thread4=new Thread(loader4);
	
	// Venn
	public Venn_Overview vennOverview; 
	
	public int bX,bY;
	
	// Order genes
	public static PopupComplex popupComplex;
	public static ReactionView popupReaction;
	public static PopupRelation popupRelation;
	public static PopupOrder popupOrder;
	public static CheckBox check1;
	public static CheckBox check2;
	public static CheckBox check3;
	
	// Grouping animation
	public static int stateAnimation =0;
	public static int bg =0;
	
	
	// Color of miner
	public static int[] colorRelations;
	
	
	// Allow to draw 
	public static boolean isAllowedDrawing = false;
	public static int  ccc = 0; // count to draw progessing bar
	
	public PFont metaBold = loadFont("Arial-BoldMT-18.vlw");
	
	
	// New to read data 
	public static  Map<String,String> mapElementRDFId;
	public static  Map<String,String> mapElementRef;
	public static  Map<String,String> mapSmallMoleculeRDFId;
	public static  Map<String,String> mapPhysicalEntity;
	public static ArrayList<Complex> complexList; 
	public static  Map<String,Integer> mapComplexRDFId_index;
	public static Set<BiochemicalReaction> reactionSet; 
	public static Set<SmallMolecule> smallMoleculeSet;
	public static ArrayList<String>[] proteinsInComplex; 
	
	// Multiple pathways 
	public PopupView popupView = new PopupView(this);
	public PathwayView pathwayView;
	
	public static void main(String args[]){
	  PApplet.main(new String[] { PathwayViewer_2_8.class.getName() });
    }

	public void setup() {
		textFont(metaBold,14);
		size(1440, 900);
		//size(2000, 1200);
		if (frame != null) {
		    frame.setResizable(true);
		  }
		background(0);
		frameRate(12);
		curveTightness(0.7f); 
		smooth();
		
		// Get the output file
		minerList.add(new DirectedRelationMiner());
		minerList.add(new ControlsStateChangeOfMiner());
		//minerList.add(new CSCOButIsParticipantMiner());
		//minerList.add(new CSCOBothControllerAndParticipantMiner());
		//minerList.add(new CSCOThroughControllingSmallMoleculeMiner());
		//minerList.add(new CSCOThroughBindingSmallMoleculeMiner());
		//minerList.add(new CSCOThroughDegradationMiner());
		//minerList.add(new ControlsStateChangeDetailedMiner());
		//minerList.add(new ControlsPhosphorylationMiner());
		
		minerList.add(new ControlsTransportMiner());
		minerList.add(new ControlsExpressionMiner());
		minerList.add(new ControlsExpressionWithConvMiner());
		minerList.add(new ControlsDegradationIndirectMiner());
		minerList.add(new ConsumptionControlledByMiner());
		minerList.add(new ControlsProductionOfMiner());
		minerList.add(new CatalysisPrecedesMiner());
		minerList.add(new ChemicalAffectsThroughBindingMiner());
		minerList.add(new ChemicalAffectsThroughControlMiner());
		minerList.add(new ControlsTransportOfChemicalMiner());
		minerList.add(new InComplexWithMiner());
		minerList.add(new InteractsWithMiner());
		minerList.add(new NeighborOfMiner());
		minerList.add(new ReactsWithMiner());
		minerList.add(new UsedToProduceMiner());
		//minerList.add(new RelatedGenesOfInteractionsMiner()); Genes related to Biochemical reactions which involves multiple proteins/complex input and output
		//minerList.add(new UbiquitousIDMiner());
	
		colorRelations =  new int[minerList.size()];
		for (int i=0; i<minerList.size();i++){
			String name = minerList.get(i).toString();
			if (name.equals("in-complex-with"))
				colorRelations[i] = new Color(0,200,200).getRGB(); //light blue
			else if (name.equals("neighbor-of"))
				colorRelations[i] = Color.BLUE.getRGB();		//BLUE
			else if (name.equals("controls-state-change-of"))
				colorRelations[i] = new Color(220,0,0).getRGB(); //RED
			else if (name.equals("directed-relations"))
				colorRelations[i] = new Color(50,180,0).getRGB(); //color = Color.GREEN;
			else if (name.equals("chemical-affects-through-binding"))
				colorRelations[i] = new Color(200,200,0).getRGB(); //color = Color.YELLOW;	
			else if (name.equals("consumption-controlled-by"))
				colorRelations[i] = Color.MAGENTA.darker().getRGB();	//MAGENTA
			else if (name.equals("controls-transport-of"))
				colorRelations[i] = Color.PINK.darker().getRGB();	//PINK
			else if (name.equals("controls-expression-of"))
				colorRelations[i] = Color.ORANGE.getRGB();	//PINK
			else if (name.equals("controls-production-of"))
				colorRelations[i] = Color.PINK.darker().getRGB();	//PINK
			else if (name.equals("used-to-produce"))
				colorRelations[i] = new Color(100,50,0).getRGB();	// dark Red
			else
				colorRelations[i] = Color.BLACK.getRGB();
		}
		
		buttonBrowse = new ButtonBrowse(this);
		popupRelation = new PopupRelation(this);
		popupComplex = new PopupComplex(this);
		popupReaction = new ReactionView(this);
		popupOrder  = new PopupOrder(this);
		check1 = new CheckBox(this, "Lensing");
		check2 = new CheckBox(this, "Grouping by Similarity");
		check3 = new CheckBox(this, "Highlighting groups");
		
		pathwayView = new PathwayView(this);
		
		//VEN DIAGRAM
		vennOverview = new Venn_Overview(this);
		if (!currentFile.equals("")){
			thread1=new Thread(loader1);
			thread1.start();
		}
		
		// Loading multiple pathways
		thread2=new Thread(loader2);
		thread2.start();
		
		
		// enable the mouse wheel, for zooming
		addMouseWheelListener(new java.awt.event.MouseWheelListener() {
			public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
				mouseWheel(evt.getWheelRotation());
			}
		});
		/*
		    Pathway2 pathway = new Pathway2(this, null, 0, "Tuan", 2);
		    System.out.println( "Main: 1	" +pathway.displayName);
		    foo(pathway);
		    System.out.println( "Main: 2	" +pathway.displayName);*/
		}	
	/*
	public void foo(Pathway2 d) {
		System.out.println( "Foo: 0	" +d.displayName);
		 d.displayName = "Max";
		 System.out.println( "Foo: 1	" +d.displayName);
		 d =  new Pathway2(this, null, 0, "Amy", 2);
		 System.out.println( "Foo: 2	" +d.displayName);
		   
	}*/
	
	
	public void draw() {
		background(255);
		// Draw 
		try{
			// Print message
			if (processingMiner<colorRelations.length){
				ccc+=10;
				if (ccc>10000) ccc=0;
				this.fill(colorRelations[processingMiner],100+ccc%155);
				this.noStroke();
				this.arc(marginX,this.height-20, 30, 30, 0, PApplet.PI*2*(processingMiner+1)/minerList.size());
				
				
				this.fill(colorRelations[processingMiner]);
				this.textSize(14);
				this.textAlign(PApplet.LEFT);
				this.text(message, marginX+20,this.height-14);
			}
			
			if (isAllowedDrawing){
				if (popupView.s==0){
					
					if (currentFile.equals("")){
						int ccc = this.frameCount*6%255;
						this.fill(ccc, 255-ccc,(ccc*3)%255);
						this.textAlign(PApplet.LEFT);
						this.textSize(20);
						this.text("Please select a BioPax input file", 300,250);
						
						float x6 =74;
						float y6 =25;
						this.stroke(ccc, 255-ccc,(ccc*3)%255);
						this.line(74,25,300,233);
						this.noStroke();
						this.triangle(x6, y6, x6+4, y6+13, x6+13, y6+4);
					}
					else{
						check1.draw(this.width-500, 60);
						check2.draw(this.width-500, 82);
						
						drawMatrix();
						this.textSize(13);
						
						popupOrder.draw(this.width-298);
						popupComplex.draw(this.width-198);
					
						if (check2.s)
						check3.draw(this.width-500, 100);
					}
				}	
				else if (popupView.s==1){
					// Draw file name
					this.fill(128);
					this.textAlign(PApplet.LEFT);
					this.textSize(12);
					String[] str = currentFile.split("/");
			        String nameFile = str[str.length-1];
			  	    this.text("File: "+nameFile, 80, 16);
					
					popupReaction.drawReactions(150);
				}
				else if (popupView.s==2){
					if (pathwayView!=null)
						pathwayView.draw();
				}
					
				
			}
			popupView.draw(this.width-98);
			buttonBrowse.draw();
			//popupRelation.draw(this.width-304);
		}
		catch (Exception e){
			System.out.println();
			System.out.println("*******************Catch ERROR*******************");
			e.printStackTrace();
			return;
		}
	}	
	
	public void drawMatrix() {
		if (ggg==null || ggg.size()==0)
			return;
		else{
			size = (this.height-marginY)/ggg.size();
			size = size*0.7f;
			if (size>100)
				size=100;
		}
		
		// Checking state of group transition
		if (leaderSortedMap!=null && stateAnimation==0){
			float maxDis = 0;
			for (Map.Entry<Integer, Integer> entry : leaderSortedMap.entrySet()) {
				int index = entry.getKey();
				for (int i=1;i<locals[index].size();i++){
					int child = locals[index].get(i);
					float dis = PApplet.abs(ggg.get(index).iX.value-ggg.get(child).iX.value);
					if (dis>maxDis)
						maxDis = dis;
				}
			}
			if (maxDis<1){
				stateAnimation=1;
			}
		}

		if (check2.s && stateAnimation==1){
			drawGroups();
		}	
		else{
			drawGenes();
		}

		float x2 = this.width-500;
		float y2 = 180;
		this.fill(0);
		this.textAlign(PApplet.LEFT);
		this.textSize(12);
		String[] str = currentFile.split("/");
        String nameFile = str[str.length-1];
  	    this.text("File: "+nameFile, x2, y2);
		// find minerID index
		if (Venn_Overview.minerGlobalIDof!=null){
			if (currentRelation>=0){
				this.fill(colorRelations[currentRelation]);
				this.text("Realationship "+currentRelation+": "+minerList.get(currentRelation), x2+250, y2+20);
				this.text("Total proteins: "+ggg.size(), x2+250, y2+40);
				this.text("Total relations: "+pairs[currentRelation].size(), x2+250, y2+60);
			}
			this.fill(0);
			this.text("Pathway summary", x2, y2+20);
			this.text("Total proteins: "+ggg.size(), x2, y2+40);
			int totalRelations = 0;
			for (int i=0;i<pairs.length;i++){
				totalRelations+=pairs[i].size();
			} 
			this.text("Total relations: "+totalRelations, x2, y2+60);
		}
		vennOverview.draw(x2+50,300,10);
	}
	
	
	
	public void drawGroups() {
		if (leaderSortedMap==null) return;
		size = (this.height-marginY)/leaderSortedMap.size();
		size = size*0.7f;
		if (size>100)
			size=100;
		
		// Compute lensing
		if (check1.s){
			bX = (int) ((mouseX-marginX)/size);
			bY = (int) ((mouseY-marginY)/size);
		}
		else{
			bX = leaderSortedMap.size()+10;
			bY = leaderSortedMap.size()+10;
		}
		float lensingSize = PApplet.map(size, 0, 100, 25, 120);	
		
		int num = 4; // Number of items in one side of lensing
		
		int order = 0;
		for (Map.Entry<Integer, Integer> entry : leaderSortedMap.entrySet()) {
			int index = entry.getKey();
			if (bX-num<=order && order<=bX+num) {
				ggg.get(index).iW.target(lensingSize);
				int num2 = order-(bX-num);
				if (bX-num>=0)
					setValue(ggg.get(index).iX, marginX +(bX-num)*size+num2*lensingSize);
				else
					setValue(ggg.get(index).iX, marginX +order*lensingSize);
			}	
			else{
				ggg.get(index).iW.target(size);
				if (order<bX-num)
					setValue(ggg.get(index).iX, marginX +order*size);
				else if (order>bX+num){
					if (bX-num>=0)
						setValue(ggg.get(index).iX, marginX +(order-(num*2+1))*size+(num*2+1)*lensingSize);
					else{
						int num3= bX+num+1;
						if (num3>0)
							setValue(ggg.get(index).iX, marginX +(order-num3)*size+num3*lensingSize);
					}	
				}	
			}
			order++;
		}
		
		order = 0;
		for (Map.Entry<Integer, Integer> entry : leaderSortedMap.entrySet()) {
			int index = entry.getKey();
			if (bY-num<=order && order<=bY+num){
				ggg.get(index).iH.target(lensingSize);
				int num2 = order-(bY-num);
				if (bY-num>=0)
					setValue(ggg.get(index).iY, marginY +(bY-num)*size+num2*lensingSize);
				else
					setValue(ggg.get(index).iY, marginY +order*lensingSize);
			}	
			else{
				ggg.get(index).iH.target(size);
				if (order<bY-num)
					setValue(ggg.get(index).iY, marginY +order*size);
				else if (order>bY+num){
					if (bY-num>=0)
						setValue(ggg.get(index).iY, marginY +(order-(num*2+1))*size+(num*2+1)*lensingSize);
					else{
						int num3= bY+num+1;
						if (num3>0)
							setValue(ggg.get(index).iY, marginY +(order-num3)*size+num3*lensingSize);
					}	
				}	
			}	
			order++;
		}
		
		for (Map.Entry<Integer, Integer> entry : leaderSortedMap.entrySet()) {
			int index = entry.getKey();
			ggg.get(index).iH.update();
			ggg.get(index).iW.update();
			ggg.get(index).iX.update();
			ggg.get(index).iY.update();
		}
		
		// Draw gene names on X and Y axes
		int maxElement = 0;
		for (Map.Entry<Integer, Integer> entry : leaderSortedMap.entrySet()) {
			int index = entry.getKey();
			int numE = locals[index].size();
			if (numE>maxElement)
				maxElement = numE;
		}	
		
		for (Map.Entry<Integer, Integer> entry : leaderSortedMap.entrySet()) {
			int index = entry.getKey();
			int numE = locals[index].size();
			float ww = ggg.get(index).iW.value;
			String name = ggg.get(index).name;
			this.fill(50);
			float fontSize = PApplet.map(numE, 1, maxElement, 9, 15);
			this.textSize(fontSize);
			if (locals[index].size()>1){
				name = locals[index].size()+" proteins";
				this.fill(0);
			}	
			if (ww>8){
				if (isSmallMolecule(name))
					this.fill(100);
				float xx =  ggg.get(index).iX.value;
				this.textAlign(PApplet.LEFT);
				float al = -PApplet.PI/2;
				this.translate(xx+ww/2+fontSize/3,marginY-8);
				this.rotate(al);
				this.text(name, 0,0);
				this.rotate(-al);
				this.translate(-(xx+ww/2+fontSize/3), -(marginY-8));
			}
			float hh =ggg.get(index).iH.value;
			if (hh>8){
				if (isSmallMolecule(name))
					this.fill(100);
				float yy =  ggg.get(index).iY.value;
				this.textAlign(PApplet.RIGHT);
				this.text(name, marginX-6, yy+hh/2+fontSize/3);
			}
		}
		

		this.noStroke();
		for (Map.Entry<Integer, Integer> entryI : leaderSortedMap.entrySet()) {
			int indexI = entryI.getKey();
			// Check if this is grouping
			float yy =  ggg.get(indexI).iY.value;
			float hh = ggg.get(indexI).iH.value;
			
			int numEx = locals[indexI].size();
			
			for (Map.Entry<Integer, Integer> entryJ : leaderSortedMap.entrySet()) {
				int indexJ = entryJ.getKey();
				float xx =  ggg.get(indexJ).iX.value;
				float ww =ggg.get(indexJ).iW.value;
				
				// Draw background
				if (indexI!=indexJ && check3.s) {
					int numEy = locals[indexJ].size();
					int maxNumE = PApplet.max(numEx, numEy);
					float dense = PApplet.map(maxNumE, 1, maxElement, 10, 70);
					if (maxNumE==1)
						dense=0;
					this.fill(0,dense);
					this.noStroke();
					this.rect(xx, yy, ww, hh);
				}
				// Draw Rosemary chart
				if (geneRelationList==null || geneRelationList[indexI][indexJ]==null) continue; // no relation of two genes
				for (int i2=0;i2<geneRelationList[indexI][indexJ].size();i2++){
					int localRalationIndex = geneRelationList[indexI][indexJ].get(i2);
					
					this.noStroke();
					this.fill(colorRelations[minerGlobalIDof[localRalationIndex]]);
					float alpha = PApplet.PI*2/minerGlobalIDof.length;
					this.arc(xx+ww/2,yy+hh/2, PApplet.min(ww,hh), PApplet.min(ww,hh), localRalationIndex*alpha, (localRalationIndex+1)*alpha);
				}
			}
		}
		drawGenesInGroup(maxElement);
	}
	
	// Draw group names
	public void drawGenesInGroup(int maxElement) {
		for (Map.Entry<Integer, Integer> entryI : leaderSortedMap.entrySet()) {
			int index = entryI.getKey();
			// Check if this is grouping
			float xx =  ggg.get(index).iX.value;
			float yy =  ggg.get(index).iY.value;
			float ww =  ggg.get(index).iW.value;
			float hh =ggg.get(index).iH.value;
			String name = ggg.get(index).name;
			int numE = locals[index].size();
			
			// Draw genes in compound
			float fontSize = PApplet.map(numE, 1, maxElement, 10, 18);
			this.textSize(fontSize);
			float wid = 20+this.textWidth(name);
			float ww2 = 145;
			if (numE>1 && marginX-wid<=mouseX && mouseX<=marginX && yy<mouseY && mouseY<yy+hh){
				this.textAlign(PApplet.LEFT);
				float hh2 = (numE+3)*13+20;
				float xx2 = marginX;
				float yy2 = yy+hh/2-hh2/2;
				
				// Draw background of element text
				float step = 50;
				for (int i=0; i<step;i++){
					this.stroke(184,10+i*5f);
					float hh3 = PApplet.map(i, 0, step, 10, hh2);
					this.line(marginX-6+i, yy+hh/2-hh3/2, marginX-6+i, yy+hh/2+hh3/2);
				}
				this.noStroke();
				this.fill(190,235);
				this.rect(marginX-6+step, yy2+0.5f, ww2, hh2);
				
				drawElementList(numE, index,xx2+step+10,yy2+13,step, true);
			}
			else if (numE>1 && xx<mouseX && mouseX<xx+ww && marginY-wid<=mouseY && mouseY<=marginY){
				this.textAlign(PApplet.CENTER);
				float hh2 = (numE+3)*13+20;
				float yy2 = marginY;
				//float yy2 = yy+hh/2-hh2/2;
				float xx2 = xx+ww/2;
				
				
				// Draw background of element text
				float step = 30;
				for (int i=0; i<step;i++){
					this.stroke(184,10+i*8f);
					float ww3 = PApplet.map(i, 0, step, 10, ww2);
					this.line(xx2-ww3/2, marginY-8+i, xx2+ww3/2, marginY-8+i);
				}
				this.noStroke();
				this.fill(190,235);
				this.rect(xx2-ww2/2, marginY-8+step, ww2, hh2);
				
				drawElementList(numE, index,xx2,yy2+step,step, false);
			}
		}
	}
	public void drawElementList(int numE, int leaderIndex, float xx2, float yy2, float step, boolean isX_Axis){
		// Order names
		Map<String, Integer> unsortMap = new HashMap<String, Integer>();
		int index1 = -1;
		int index2 = -1;
		for (int i=0;i<numE;i++){
			int e = locals[leaderIndex].get(i);
			unsortMap.put(ggg.get(e).name, i);
			if (i==0)
				index1 = e;
			else if (i==1)
				index2 = e;
		}
		Map<String, Integer> treeMap = new TreeMap<String, Integer>(unsortMap);
		int count=0;
		this.fill(0);
		this.textSize(12);
		for (Map.Entry<String, Integer> entry : treeMap.entrySet()) {
			this.text(entry.getKey(),xx2,yy2+count*13);
			count++;
		}
		
		// Draw relation of genes in the group
		count = 1;
		float xx3 = xx2;
		if (isX_Axis)
			xx3 -=10;
		this.textSize(13);
		if (geneRelationList!=null && geneRelationList[index1][index2]!=null){
			for (int i2=0;i2<geneRelationList[index1][index2].size();i2++){
				int localRalationIndex = geneRelationList[index1][index2].get(i2);
				Color c = new Color(colorRelations[minerGlobalIDof[localRalationIndex]]).darker();
				this.fill(c.getRGB());
				this.text(minerNames[localRalationIndex], xx3, yy2+13*numE+15*count+10);
				count++;
			}
		}
		this.fill(0);
		if (count>1){
			this.textSize(13);
			this.text("These proteins are:",xx3, yy2+13*numE+10);
		}	
		else{
			this.textSize(12);
			if (isX_Axis)
				this.text("No relations between",xx3-10, yy2+13*numE+10);
			else	
				this.text("No relations between",xx3, yy2+13*numE+10);
			this.text("proteins in this group",xx3, yy2+13*numE+24);
		}
	}
	
		
	public void drawGenes() {
		// Compute lensing
		if (check1.s){
			bX = (int) ((mouseX-marginX)/size);
			bY = (int) ((mouseY-marginY)/size);
		}
		else{
			bX = ggg.size()+10;
			bY = ggg.size()+10;
		}
		float lensingSize = PApplet.map(size, 0, 100, 25, 120);	
		
		int num = 4; // Number of items in one side of lensing
		for (int i=0;i<ggg.size();i++){
			int order = ggg.get(i).order;
			if (bX-num<=order && order<=bX+num) {
				ggg.get(i).iW.target(lensingSize);
				int num2 = order-(bX-num);
				if (bX-num>=0)
					setValue(ggg.get(i).iX, marginX +(bX-num)*size+num2*lensingSize);
				else
					setValue(ggg.get(i).iX, marginX +order*lensingSize);
			}	
			else{
				ggg.get(i).iW.target(size);
				if (order<bX-num)
					setValue(ggg.get(i).iX, marginX +order*size);
				else if (order>bX+num){
					if (bX-num>=0)
						setValue(ggg.get(i).iX, marginX +(order-(num*2+1))*size+(num*2+1)*lensingSize);
					else{
						int num3= bX+num+1;
						if (num3>0)
							setValue(ggg.get(i).iX, marginX +(order-num3)*size+num3*lensingSize);
						else
							setValue(ggg.get(i).iX, marginX +order*size);
					}	
				}	
			}	
		}
		for (int j=0;j<ggg.size();j++){
			int order = ggg.get(j).order;
			if (bY-num<=order && order<=bY+num){
				ggg.get(j).iH.target(lensingSize);
				int num2 = order-(bY-num);
				if (bY-num>=0)
					setValue(ggg.get(j).iY, marginY +(bY-num)*size+num2*lensingSize);
				else
					setValue(ggg.get(j).iY, marginY +order*lensingSize);
			}	
			else{
				ggg.get(j).iH.target(size);
				if (order<bY-num)
					setValue(ggg.get(j).iY, marginY +order*size);
				else if (order>bY+num){
					if (bY-num>=0)
						setValue(ggg.get(j).iY, marginY +(order-(num*2+1))*size+(num*2+1)*lensingSize);
					else{
						int num3= bY+num+1;
						if (num3>0)
							setValue(ggg.get(j).iY, marginY +(order-num3)*size+num3*lensingSize);
						else
							setValue(ggg.get(j).iY, marginY +order*size);
					}	
					
				}	
			}	
		}
		
		for (int i=0;i<ggg.size();i++){
			ggg.get(i).iH.update();
			ggg.get(i).iW.update();
			ggg.get(i).iX.update();
			ggg.get(i).iY.update();
		}
		
		// Draw gene names on X and Y axes
		for (int i=0;i<ggg.size();i++){
			float ww = ggg.get(i).iW.value;
			float xx =  ggg.get(i).iX.value;
			this.fill(50);
			
			if (ww>3){
				this.textSize(12);
				if (isSmallMolecule(ggg.get(i).name)){
					this.fill(150,150,0);
				}	
					
				this.textAlign(PApplet.LEFT);
				float al = -PApplet.PI/2;
				this.translate(xx+ww/2+5,marginY-8);
				this.rotate(al);
				this.text(ggg.get(i).name, 0,0);
				this.rotate(-al);
				this.translate(-(xx+ww/2+5), -(marginY-8));
			}
			
			float hh =ggg.get(i).iH.value;
			float yy =  ggg.get(i).iY.value;
			this.fill(50);
			if (isSmallMolecule(ggg.get(i).name)){
				this.fill(150,150,0);
			}	
			if (hh>3){
				this.textSize(12);
				this.textAlign(PApplet.RIGHT);
				this.text(ggg.get(i).name, marginX-6, yy+hh/2+5);
			}
			
		}
		
		
		// All complexes
		if (PopupComplex.sAll || PopupComplex.b==-1){
			for (int i=0;i<ggg.size();i++){
				// Check if this is grouping
				float yy =  ggg.get(i).iY.value;
				float hh = ggg.get(i).iH.value;
				for (int j=0;j<ggg.size();j++){
					float xx =  ggg.get(j).iX.value;
					float ww =ggg.get(j).iW.value;
					if (gene_gene_InComplex[i][j]>0){
						float sat2 = (255-50)*gene_gene_InComplex[i][j]/(float) maxGeneInComplex;
						float sat = 80+sat2;
						this.fill(0,sat);
						this.noStroke();
						this.rect(xx, yy, ww, hh);
					}
				}
			}	
		}
		
		// brushingComplex &&&&&& selectedComplex
		int brushingComplex = popupComplex.getIndexInSet(PopupComplex.b);
		if (brushingComplex>=0){
			drawComplex(brushingComplex,200,100,0);
		}
		int selectedComplex = popupComplex.getIndexInSet(PopupComplex.s);
		if (selectedComplex>=0){
			drawComplex(selectedComplex,255,0,0);
		}
		
		
		this.noStroke();
		for (int i=0;i<ggg.size();i++){
			// Check if this is grouping
			float yy =  ggg.get(i).iY.value;
			float hh = ggg.get(i).iH.value;
			for (int j=0;j<ggg.size();j++){
				float xx =  ggg.get(j).iX.value;
				float ww =ggg.get(j).iW.value;
				
				if (geneRelationList!=null && geneRelationList[i][j]!=null) {
					for (int i2=0;i2<geneRelationList[i][j].size();i2++){
						int localRalationIndex = geneRelationList[i][j].get(i2);
						this.noStroke();
						this.fill(colorRelations[minerGlobalIDof[localRalationIndex]]);
						float alpha = PApplet.PI*2/minerGlobalIDof.length;
						this.arc(xx+ww/2,yy+hh/2, PApplet.min(ww,hh), PApplet.min(ww,hh), localRalationIndex*alpha, (localRalationIndex+1)*alpha);
					}
				}	
			}
		}
	}	
	
	public void drawComplex(int complex, int r, int g, int b) {
		ArrayList<String> a = proteinsInComplex[complex];
		for (int i=0;i<a.size();i++){
			int indexI = getProteinOrderByName(a.get(i));
			if (indexI<0) { // Exception *******************************
			//	System.out.println("drawComplex in Maxtrix View:	CAN NOT FIND protein = "+a.get(i));
				continue;
			}	
			float yy =  ggg.get(indexI).iY.value;
			float hh = ggg.get(indexI).iH.value;
			for (int j=0;j<a.size();j++){
				int indexJ = getProteinOrderByName(a.get(j));
				if (indexJ<0) { // Exception *******************************
				//	System.out.println("drawComplex in Maxtrix View:	CAN NOT FIND protein = "+a.get(j));
					continue;
				}
				
				float xx =  ggg.get(indexJ).iX.value;
				float ww =ggg.get(indexJ).iW.value;
					
				this.fill(r,g,b,200);
				this.noStroke();
				this.rect(xx, yy, ww, hh);
			}
		}
	}
	
	public void setValue(Integrator inter, float value) {
		if (ggg.size()<500){
			inter.target(value);
		}
		else{
			inter.set(value);
		}
	}
				
	public void mousePressed() {
		if (popupView.s==2){
			pathwayView.mousePressed();
		}
		else if (popupView.s==1){
			popupReaction.mousePressed();
		}
		else if (popupOrder.b>=0){
			popupOrder.slider.checkSelectedSlider1();
		}
	}
	public void mouseReleased() {
		if (popupView.s==2){
			pathwayView.mouseReleased();
		}
		else if (popupView.s==1){
				popupReaction.mouseReleased();
		}
		else if (popupOrder.b>=0){
			popupOrder.slider.checkSelectedSlider2();
		}
	}
	public void mouseDragged() {
		if (popupView.s==2){
			pathwayView.mouseDragged();
		}
		else if (popupView.s==1){
			popupReaction.mouseDragged();
		}
		else if (popupOrder.b>=0){
			popupOrder.slider.checkSelectedSlider3();
		}
		
	}
		
	public void mouseMoved() {
		popupView.mouseMoved();
		if (popupView.s==2){
			pathwayView.mouseMoved();
		}
		else if (popupView.s==1){
			ReactionView.popupCausality.mouseMoved();
			ReactionView.popupReactionOrder.mouseMoved();
			popupReaction.checkReactionBrushing();
			if (isAllowedDrawing && ReactionView.simulationRectList.size()==0){
				popupReaction.mouseMoved();
			}
		}
		
	}
		
	public void mouseClicked() {
		popupView.mouseClicked();
		if (buttonBrowse.b>=0){
			thread4=new Thread(loader4);
			thread4.start();
		}
		if (popupView.s==2){
			pathwayView.mouseClicked();
		}
		else if (popupView.s==1){
			popupReaction.mouseClicked();
		}
		else {
			if (check1.b){
				check1.mouseClicked();
			}
			else if (check2.b){
			check2.mouseClicked();
			if (check2.s){  
				main.PathwayViewer_2_8.stateAnimation=0;
				Gene.orderBySimilarity();
				Gene.groupBySimilarity();
			}	
				else {
					main.PathwayViewer_2_8.stateAnimation=0;
					Gene.orderBySimilarity();
				}	
			}
			else if (check3.b){
				check3.mouseClicked();
			}
			else if (popupRelation.b>=0){
				popupRelation.mouseClicked();
			}
			else if (PopupComplex.b>=-1){
				popupComplex.mouseClicked();
			}
			
			else if (popupOrder.b>=0){
				popupOrder.mouseClicked();
			}
			else if (vennOverview!=null){
				vennOverview.mouseClicked();
				//update();
			}
		}
	}
		
	
	public String loadFile (Frame f, String title, String defDir, String fileType) {
		  FileDialog fd = new FileDialog(f, title, FileDialog.LOAD);
		  fd.setFile(fileType);
		  fd.setDirectory(defDir);
		  fd.setLocation(50, 50);
		  fd.show();
		  String path = fd.getDirectory()+fd.getFile();
	      return path;
	}
	
	
	public void keyPressed() {
		if (popupView.s==1 &&  ReactionView.textbox1.b){
			ReactionView.textbox1.keyPressed();
			return;
		}
		
		if (this.key == '+') {
			currentRelation++;
			if (currentRelation>=minerList.size())
				currentRelation = 0;
			//update();
		}
		else if (this.key == '-') {
			currentRelation--;
			if (currentRelation<0)
				currentRelation = minerList.size()-1;
			//update();
		}
		if (this.key == 'q' || this.key == 'Q'){
			Gene.reveseGenesForDrawing();
		}
		if (this.key == 'w' || this.key == 'W'){
			Gene.swapGenesForDrawing();
		}
		if (this.key == 'l' || this.key == 'L'){
			check1.s = !check1.s;
		}
		if (this.key == 'n' || this.key == 'N'){
			Gene.orderByName();
			PopupOrder.s = 2;
		}	
		if (this.key == 's' || this.key == 'S'){
			thread2=new Thread(loader2);
			thread2.start();
			PopupOrder.s = 3;
		}
		if (this.key == 'g' || this.key == 'G'){
			thread3=new Thread(loader3);
			thread3.start();
			stateAnimation=0;
			Gene.orderBySimilarity();
			Gene.groupBySimilarity();
		 	check2.s = true;
		}
	}
	
	
	// Thread for Venn Diagram
	class ThreadLoader1 implements Runnable {
		PApplet p;
		public ThreadLoader1(PApplet parent_) {
			p = parent_;
		}
		
		@SuppressWarnings("unchecked")
		public void run() {
			isAllowedDrawing =  false;
			
			 // Initialize best plots
			pairs = new ArrayList[minerList.size()];
			for (int i=0;i<minerList.size();i++){
				pairs[i] = new ArrayList<String>();
			}
			
			ggg = new ArrayList<Gene>();
			geneRelationList = null;
			leaderSortedMap = null;
			
			ReactionView.textbox1.searchText="";
			File modFile = new File(currentFile);
			//File outFile = new File("output.txt");
			SimpleIOHandler io = new SimpleIOHandler();
			Model model;
			try{
				System.out.println();
				System.out.println("***************** Load data: "+modFile+" ***************************");
				model = io.convertFromOWL(new FileInputStream(modFile));
				mapElementRDFId = new HashMap<String,String>();
				mapElementRef = new HashMap<String,String>();
				mapSmallMoleculeRDFId =  new HashMap<String,String>();
				mapComplexRDFId_index =  new HashMap<String,Integer>();
				
				 Set<Protein> proteinSet = model.getObjects(Protein.class);
				 for (Protein currentProtein : proteinSet){
					 mapElementRDFId.put(currentProtein.getRDFId().toString(), currentProtein.getDisplayName());
					 if (currentProtein.getEntityReference()==null) continue;
					 	mapElementRef.put(currentProtein.getEntityReference().toString(), currentProtein.getDisplayName());
				 }
					
				 smallMoleculeSet = model.getObjects(SmallMolecule.class);
				 for (SmallMolecule currentMolecule : smallMoleculeSet){
					 mapElementRDFId.put(currentMolecule.getRDFId().toString(), currentMolecule.getDisplayName());
					 mapSmallMoleculeRDFId.put(currentMolecule.getRDFId().toString(), currentMolecule.getDisplayName());
				 }
				 
				 
				 Set<PhysicalEntity> physicalEntitySet = model.getObjects(PhysicalEntity.class);
				 for (PhysicalEntity current : physicalEntitySet){
					 if (current.getRDFId().contains("PhysicalEntity")){
						 mapElementRDFId.put(current.getRDFId().toString(), current.getDisplayName());
					 }	 
				 }
				 
				 int j=0;
				 for (Map.Entry<String,String> entry : mapElementRDFId.entrySet()){
					 String displayName = entry.getValue();
					 ggg.add(new Gene(displayName,ggg.size()));
					 j++;
				 }
						
				 
				 Set<Complex> complexSet = model.getObjects(Complex.class);
				 complexList = new ArrayList<Complex>();
				 int i2=0;
				 for (Complex current : complexSet){
					 mapComplexRDFId_index.put(current.getRDFId().toString(), i2);
					 complexList.add(current);
					 i2++;
				 }
				 i2=0;
				 
				 
				 // Compute proteins in complexes
				 proteinsInComplex = new ArrayList[complexSet.size()];
				 for (int i=0; i<complexList.size();i++){
						proteinsInComplex[i] = getProteinsInComplexById(i);
				 }
				 reactionSet = model.getObjects(BiochemicalReaction.class);
				
				
				 /*
				 Set<PathwayStep> set3 = 	 model.getObjects(PathwayStep.class);
				 i2=0;
				 System.out.println("SET2 ******:"+set3);
				 for (PathwayStep current : set3){
				//	 System.out.println("PathwayStep"+i2+"	"+current.getNextStepOf()+"	"+current.getPathwayOrderOf());
				//	 System.out.println("		"+current.getNextStep());
					 i2++;
				 }*/
				 
				 
				
			}
			catch (FileNotFoundException e){
				e.printStackTrace();
				javax.swing.JOptionPane.showMessageDialog(p, "File not found: " + modFile.getPath());
				return;
			}
			
			for (processingMiner=0;processingMiner<minerList.size();processingMiner++){
				 message = "Processing relation ("+processingMiner+"/"+minerList.size()
					+"): "+minerList.get(processingMiner);

				 // Search
				Miner min = minerList.get(processingMiner);
				Pattern p = min.getPattern();
				Map<BioPAXElement,List<Match>> matches = Searcher.search(model, p, null);
				
				for (List<Match> matchList : matches.values()){
					for (Match match : matchList){
						String s1 = getProteinName(match.getFirst().toString());
						String s2 = getProteinName(match.getLast().toString());
						
						if (s1==null) 
							s1 = match.getFirst().toString();
						if (s2==null) 
							s2 = match.getFirst().toString();
						
						if (s1!=null && s2!=null){
							// Store results for visualization
							if (!pairs[processingMiner].contains(s1+"\t"+s2)){
								pairs[processingMiner].add(s1+"\t"+s2);
							}	
						}	
						else{
							System.out.println();
							System.out.println("	NULLLLLLLLL");
						}
					}	
				}
			}
			System.out.println();
			popupComplex.setItems();
			ReactionView.check11.s=true;   // Fade small molecule
			ReactionView.popupReactionOrder.s=2;
			ReactionView.check5.s=false;
			PopupCausality.s =4;
			
			popupReaction.setItems();
			vennOverview.initialize();
			
			stateAnimation=0;
			isAllowedDrawing =  true;  //******************* Start drawing **************
			
			// Compute the summary for each Gene
			Gene.compute();
			
			Gene.computeGeneRelationList();
			Gene.computeGeneGeneInComplex();
			Gene.orderByRandom(p);
			//write();
			
			vennOverview.compute();
			check2.s  = false;
		}
	}
	
	
	
	// Thread for Venn Diagram
	class ThreadLoader2 implements Runnable {
		PApplet parent;
		public ThreadLoader2(PApplet parent_) {
			parent = parent_;
		}
		public void run() {
			pathwayView.isAllowedDrawing =  false;
			
			 String path = "./level3_2015/";
			 String imgType = ".owl";
			 pathwayView.files = listFileNames(path, imgType); 
			 pathwayView.nFiles = pathwayView.files.size();
			 println("Number of Pathway: "+pathwayView.nFiles);
			
			 // Initialize best plots
			 try{
				pathwayView.mapProteinRDFId = new HashMap<String,String>();
				pathwayView.mapSmallMoleculeRDFId =  new HashMap<String,String>();
				pathwayView.mapComplexRDFId = new HashMap<String,String>();
				pathwayView.mapComplexRDFId_Complex = new HashMap<String,Complex>();
				pathwayView.rectSizeList = new ArrayList<Integer>();
				pathwayView.rectFileList = new ArrayList<Integer>();
				pathwayView.maxSize=0;
				pathwayView.filePathway = new Pathway2[pathwayView.nFiles];
				PathwayView.rootPathway = new Pathway2(parent,null,-1,"ROOT",0);
				

				//multipleReaction.popupPathway.pathwayList = new ArrayList<String>();
				//multipleReaction.popupPathway.pathwayListFile = new ArrayList<Integer>();
				for (int f=0;f<pathwayView.files.size();f++){
					 File modFile = new File(pathwayView.files.get(f));
					 SimpleIOHandler io = new SimpleIOHandler();
					 Model model = io.convertFromOWL(new FileInputStream(modFile));
					
					System.out.println();
					System.out.println("***************** Load data: "+modFile+" ***************************");
					
					 Set<Protein> proteinSet = model.getObjects(Protein.class);
					 for (Protein currentProtein : proteinSet){
						 if (!pathwayView.mapProteinRDFId.containsKey(currentProtein.getRDFId().toString()))
							 pathwayView.mapProteinRDFId.put(currentProtein.getRDFId().toString(), currentProtein.getDisplayName());
						 
					 }
						
					 pathwayView.smallMoleculeSet = model.getObjects(SmallMolecule.class);
					 for (SmallMolecule currentMolecule : pathwayView.smallMoleculeSet){
						 if (!pathwayView.mapProteinRDFId.containsKey(currentMolecule.getRDFId().toString()))
							 pathwayView.mapProteinRDFId.put(currentMolecule.getRDFId().toString(), currentMolecule.getDisplayName());
						 if (!pathwayView.mapSmallMoleculeRDFId.containsKey(currentMolecule.getRDFId().toString()))
							 pathwayView.mapSmallMoleculeRDFId.put(currentMolecule.getRDFId().toString(), currentMolecule.getDisplayName());
					 }
					 
					 
					 Set<Complex> complexSet = model.getObjects(Complex.class);
					 
					 int i2=0;
					 for (Complex current : complexSet){
						 if (!pathwayView.mapComplexRDFId.containsKey(current.getRDFId().toString())){
							 pathwayView.mapComplexRDFId.put(current.getRDFId().toString(), current.getDisplayName());
						 	 pathwayView.mapComplexRDFId_Complex.put(current.getRDFId().toString(),current);
						 }	 
						 i2++;
					 }
					 i2=0;
					 
					 
					 
					
					// PATHWAY excited
					 i2=0;
					 String[] str = pathwayView.files.get(f).split("/");
					 String nameFile = str[str.length-1];
					 PathwayView.filePathway[f] = new Pathway2(parent,PathwayView.rootPathway,f,nameFile,1);
					 for (Pathway aPathway : model.getObjects(Pathway.class)){
						 Pathway2 newPathway = new Pathway2(parent,PathwayView.filePathway[f],
								 f,aPathway.getDisplayName(),PathwayView.filePathway[f].level+1);
						 PathwayView.filePathway[f].subPathwayList.add(newPathway);
						 processPathway(aPathway, newPathway, f);
				    	 i2++;
					 }
					 
					 
					 // Remove nested pathway from the top level of a file Because top contains all pathways
					 boolean isRedundentPathway = true;
					 while(isRedundentPathway){
						 isRedundentPathway = false;
						 for (int p1=0;p1< PathwayView.filePathway[f].subPathwayList.size();p1++){
							 Pathway2 path1 = PathwayView.filePathway[f].subPathwayList.get(p1);
							 //System.out.println(p1+"	path1="+path1.displayName);
							 int redundentPathwayIndex = -1;
							 for (int p2=0;p2< PathwayView.filePathway[f].subPathwayList.size();p2++){
								 if (p1==p2) continue;
								 Pathway2 path2 = PathwayView.filePathway[f].subPathwayList.get(p2);
								 if (path2.isContainPathway(path1.displayName)){
									 redundentPathwayIndex =p2;
								 }
							 }
							 
							 if (redundentPathwayIndex>=0){
								 PathwayView.filePathway[f].subPathwayList.remove(p1);
								 isRedundentPathway = true;
							 }
						 }
					 }
				}
				// Compute size for 1 time
				for (int f=0;f<pathwayView.files.size();f++){
					PathwayView.filePathway[f].computeSize();
					PathwayView.rootPathway.subPathwayList.add(PathwayView.filePathway[f]);
				}
				
				// Set reactions to pathway view
				ArrayList<BiochemicalReaction> rectList = new ArrayList<BiochemicalReaction>();
				for (int f=0;f<pathwayView.files.size();f++){
					Pathway2 pathway = PathwayView.filePathway[f];
					ArrayList<BiochemicalReaction> a = pathway.getAllReaction();
					for (int i=0;i<a.size();i++){
						BiochemicalReaction react = a.get(i);
						rectList.add(react);
						pathwayView.rectFileList.add(f);
					}
				}
				
				int newId = 0;
				// Node id to each reaction in pathway hierarchy
				// This is done after pathwayView.updateNodes(rectList);
				for (int f=0;f<pathwayView.files.size();f++){
					Pathway2 pathway = PathwayView.filePathway[f];
					newId = pathway.setNodeId(newId);
				}
				
				pathwayView.setItems(rectList);
				pathwayView.updateNodes(rectList);
				pathwayView.updateEdges();
				pathwayView.popupPathway.setItems();
				
				// Node id to each reaction in pathway hierarchy
				// This is done after pathwayView.updateNodes(rectList);
				for (int f=0;f<pathwayView.files.size();f++){
					Pathway2 pathway = PathwayView.filePathway[f];
					pathway.setNodePathway();
				}
				
			 }
			 
			 catch (FileNotFoundException e){
					e.printStackTrace();
					javax.swing.JOptionPane.showMessageDialog(parent, "Exception in multi pathways reading");
					return;
				}
		}	 
	}
	
	public void processPathway(Pathway aPathway, Pathway2 thisPathway, int f) {
		for (Process aProcess : aPathway.getPathwayComponent()) {
			if (aProcess instanceof Pathway) { // Dig into the nested structure
				Pathway2 newPathway = new Pathway2(this, thisPathway,f,aProcess.getDisplayName(),thisPathway.level+1);
				thisPathway.subPathwayList.add(newPathway);
				processPathway((Pathway) aProcess,newPathway,f);
			} else if (aProcess instanceof BiochemicalReaction) {// It must be an Interaction
				thisPathway.reactList.add((BiochemicalReaction) aProcess);
			} else { 
				 //System.out.println("		??? " + aProcess.getDisplayName());
			}
		}
	}
	
	
	
	
	public  boolean isContainReaction(ArrayList<BiochemicalReaction> a, String s) {
		if (a==null || s==null)
			return false;
		for (int r=0;r<a.size();r++){
			if (a.get(r)==null || a.get(r).getDisplayName()==null) continue;
			
			if (a.get(r).getDisplayName().equals(s))
				return true;
		}
		return false;
	}
		
	
	// This function returns all the files in a directory as an array of Strings
	public  ArrayList<String> listFileNames(String dir, String imgType) {
		File file = new File(dir);
		ArrayList<String> a = new ArrayList<String>();
		if (file.isDirectory()) { // Do
			String names[] = file.list();
			for (int i = 0; i < names.length; i++) {
				ArrayList<String> b = listFileNames(dir + "/" + names[i], imgType);
				for (int j = 0; j < b.size(); j++) {
					a.add(b.get(j));	
				}
				
			}
		} else if (dir.endsWith(imgType)) {
			a.add(dir);
		}
		return a;
	}
	
	
	
	public int getProteinOrderByName(String name) {
		for (int i=0;i<ggg.size();i++){
			if (ggg.get(i).name.equals(name))
				return i;
		}
		return -1;
	}
	
	public static String getProteinName(String ref){	
		String s1 = mapElementRDFId.get(ref);
		if (s1==null){
			s1 = mapElementRef.get(ref);
		}
		return s1;
	}
	
	public static boolean isSmallMolecule(String name){	
		if (mapSmallMoleculeRDFId.containsValue(name))
			return true;
		else
			return false;
	}
	
	
	
	
	public static ArrayList<String> getComplexById(int id){	
		ArrayList<String> components = new ArrayList<String>(); 
		Complex com = complexList.get(id);
		  Object[] s2 = com.getComponent().toArray();
		  for (int i=0;i<s2.length;i++){
			  if (getProteinName(s2[i].toString())!=null)
				  components.add(getProteinName(s2[i].toString()));
			  else
				  components.add(s2[i].toString());
		 }
		return	 components;
	}
	
	
		
	public static ArrayList<String> getProteinsInComplexById(int id){	
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
	
	// Thread for grouping
	class ThreadLoader3 implements Runnable {
		public ThreadLoader3() {}
		public void run() {
			Gene.groupBySimilarity();
		}
	}	
	
	// Thread for grouping
	class ThreadLoader4 implements Runnable {
		PApplet parent;
		public ThreadLoader4(PApplet p) {
			parent = p;
		}
		public void run() {
			String fileName =  loadFile(new Frame(), "Open your file", "..", ".txt");
			if (fileName.equals("..null"))
				return;
			else{
				currentFile = fileName;
				//VEN DIAGRAM
				vennOverview = new Venn_Overview(parent);
				thread1=new Thread(loader1);
				thread1.start();
			}
		}
	}	
	
	void mouseWheel(int delta) {
		if (popupView.s==2){
			PathwayView.scale += delta/20f;
			if (PathwayView.scale<0.1f)
				PathwayView.scale=0.1f;
			pathwayView.updateScale();
			
			PathwayView.setIntegrator = 4;
		}
		if (PopupComplex.b>=0){
		//	PopupComplex.y2 -= delta/2;
		//	if (PopupComplex.y2>20)
		//		PopupComplex.y2 = 20;
		}
	}

	
	
}
