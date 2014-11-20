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
import org.biopax.paxtools.model.level2.biochemicalReaction;
import org.biopax.paxtools.model.level3.BiochemicalReaction;
import org.biopax.paxtools.model.level3.Complex;
import org.biopax.paxtools.model.level3.EntityReference;
import org.biopax.paxtools.model.level3.PhysicalEntity;
import org.biopax.paxtools.model.level3.Protein;
import org.biopax.paxtools.model.level3.SmallMolecule;
import org.biopax.paxtools.model.level3.SmallMoleculeReference;
import org.biopax.paxtools.model.level3.XReferrable;
import org.biopax.paxtools.model.level3.Xref;
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
import edu.uic.ncdm.venn.Venn_Detail;

import processing.core.*;

public class MainMatrix extends PApplet {
	private static final long serialVersionUID = 1L;
	public int count = 0;
	/**
	 * The name of the file for IDs of ubiquitous molecules.
	 */
	private static final String UBIQUE_FILE = "blacklist.txt";

	/**
	 * The url of the file for IDs of ubiquitous molecules.
	 */
	private static final String UBIQUE_URL = "http://www.pathwaycommons.org/pc2/downloads/blacklist.txt";


	/**
	 * Blacklist for detecting ubiquitous small molecules.
	 */
	
	
	private static Blacklist blacklist;
	public static List<Miner> minerList = new ArrayList<Miner>();
	public static int currentRelation = -1;
	public static int processingMiner = 0;
	//public String currentFile = "./level3/Pathway Commons.4.Reactome.BIOPAX.owl";
	//public String currentFile = "./level3/Regulation of DNA Replication.owl";
	public String currentFile = "../level3RAS/Rb-E2FpathwayReactome.owl";
	
	public static Button button;
	
	// Store the genes results
	public static ArrayList<String>[] pairs;
//	public static ArrayList<String>[] genes;
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
	
	public ThreadLoader2 loader2=new ThreadLoader2();
	public Thread thread2=new Thread(loader2);
	
	public ThreadLoader3 loader3=new ThreadLoader3();
	public Thread thread3=new Thread(loader3);
	
	public ThreadLoader4 loader4=new ThreadLoader4(this);
	public Thread thread4=new Thread(loader4);
	
	// Venn
	public Venn_Overview vennOverview; 
	public Venn_Detail vennDetail; 
	
	public int bX,bY;
	
	// Order genes
	public static PopupComplex popupComplex;
	public static PopupReaction popupReaction;
	public static PopupRelation popupRelation;
	public static PopupOrder popupOrder;
	public static PopupGroup popupGroup;
	public static CheckBox check1;
	public static CheckBox check2;
	
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
	public static  Map<String,String> mapElementRef;
	public static  Map<String,String> mapElementGenericRef;
	public static  Map<String,String> mapElementRDFId;
	public static  Map<String,String> mapSmallMoleculeRDFId;
	public static  Map<String,String> mapPhysicalEntity;
	public static Set<Complex> complexSet; 
	public static  Map<String,Integer> mapComplexRDFId_index;
	public static Set<BiochemicalReaction> reactionSet; 
	
	public static ArrayList<String>[] proteinsInComplex; 
	
	
	public static void main(String args[]){
	  PApplet.main(new String[] { MainMatrix.class.getName() });
    }

	static{
		File f = new File(UBIQUE_FILE);

		if (!f.exists()) downloadUbiques();
		else if (f.exists()) blacklist = new Blacklist(f.getAbsolutePath());
		else System.out.println("Warning: Cannot load blacklist.");
	}
	/**
	 * Downloads the PC data.
	 * @return true if download successful
	 */
	private static boolean downloadUbiques(){
		return downloadPlain(UBIQUE_URL, UBIQUE_FILE);
	}
	private static boolean downloadPlain(String urlString, String file){
		try{
			URL url = new URL(urlString);
			URLConnection con = url.openConnection();
			InputStream in = con.getInputStream();

			// Open the output file
			OutputStream out = new FileOutputStream(file);
			// Transfer bytes from the compressed file to the output file
			byte[] buf = new byte[1024];

			int lines = 0;
			int len;
			while ((len = in.read(buf)) > 0){
				out.write(buf, 0, len);
				lines++;
			}
			// Close the file and stream
			in.close();
			out.close();

			return lines > 0;
		}
		catch (IOException e){return false;}
	}
	
	

	public void setup() {
		textFont(metaBold,14);
		button = new Button(this);
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
		
		
		button = new Button(this);
		popupRelation = new PopupRelation(this);
		popupComplex = new PopupComplex(this);
		popupReaction = new PopupReaction(this);
		popupOrder  = new PopupOrder(this);
		popupGroup  = new PopupGroup(this);
		check1 = new CheckBox(this, "Lensing");
		check2 = new CheckBox(this, "Highlight groups");
		//VEN DIAGRAM
		vennOverview = new Venn_Overview(this);
		vennDetail = new Venn_Detail(this);
		thread1=new Thread(loader1);
		thread1.start();
		
		// enable the mouse wheel, for zooming
		addMouseWheelListener(new java.awt.event.MouseWheelListener() {
			public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
				mouseWheel(evt.getWheelRotation());
			}
		});
	}	
	
	
	
		
	
	public void draw() {
		background(255);
		
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
			if (ggg==null || ggg.size()==0)
				return;
			else{
				size = (this.height-marginY)/ggg.size();
				size = size*0.75f;
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
			try{
				if (PopupGroup.items[PopupGroup.s].equals("Similarity") && stateAnimation==1)
					drawGroups();
				else{
					drawGenes();
				}
			}
			catch (Exception e){
				System.out.println();
				System.out.println("*******************Catch ERROR*******************");
				e.printStackTrace();
				return;
			}
		}
		
		
		float x2 = this.width-600;
		float y2 = 140;
		this.fill(0);
		this.textAlign(PApplet.LEFT);
		this.textSize(13);
		this.text("File: "+currentFile, x2, y2);
		// find minerID index
		if (Venn_Overview.minerGlobalIDof!=null){
			if (currentRelation>=0){
				this.fill(colorRelations[currentRelation]);
				this.text("Realationship "+currentRelation+": "+minerList.get(currentRelation), x2+250, y2+20);
				this.text("Total genes: "+ggg.size(), x2+250, y2+40);
				this.text("Total relations: "+pairs[currentRelation].size(), x2+250, y2+60);
			}
			this.fill(0);
			this.text("Pathway summary", x2, y2+20);
			this.text("Total genes: "+ggg.size(), x2, y2+40);
			int totalRelations = 0;
			for (int i=0;i<pairs.length;i++){
				totalRelations+=pairs[i].size();
			} 
			this.text("Total relations: "+totalRelations, x2, y2+60);
		}
		
		// Draw button
		try{
			vennOverview.draw(x2+50,300,10);
			//vennDetail.draw(x2+100,500,10);
			
			this.textSize(13);
			check1.draw(this.width-600, 50);
			check2.draw(this.width-600, 70);
			button.draw();
			popupGroup.draw(this.width-100);
			popupRelation.draw(this.width-304);
			popupComplex.draw(this.width-406);
			popupReaction.draw(this.width-508);
			popupOrder.draw(this.width-202);
			
		}
		catch (Exception e){
			e.printStackTrace();
			return;
		}
	}	
	
	public void drawGroups() {
		if (leaderSortedMap==null) return;
		
		size = (this.height-marginY)/leaderSortedMap.size();
		size = size*0.75f;
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
			float fontSize = PApplet.map(numE, 1, maxElement, 10, 18);
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
				if (indexI!=indexJ && check2.s) {
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
			float ww2 = 136;
			if (numE>1 && marginX-wid<=mouseX && mouseX<=marginX && yy<mouseY && mouseY<yy+hh){
				this.textAlign(PApplet.LEFT);
				float hh2 = (numE+3)*13+12;
				float xx2 = marginX;
				float yy2 = yy+hh/2-hh2/2;
				
				// Draw background of element text
				float step = 50;
				for (int i=0; i<step;i++){
					this.stroke(190,10+i*4.5f);
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
				float hh2 = (numE+3)*13+12;
				float yy2 = marginY;
				//float yy2 = yy+hh/2-hh2/2;
				float xx2 = xx+ww/2;
				
				
				// Draw background of element text
				float step = 30;
				for (int i=0; i<step;i++){
					this.stroke(190,10+i*7.5f);
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
				this.text(minerNames[localRalationIndex], xx3, yy2+13*numE+15*count+3);
				count++;
			}
		}
		this.fill(0);
		if (count>1){
			this.textSize(14);
			this.text("These proteins are:",xx3, yy2+13*numE+3);
		}	
		else{
			this.textSize(13);
			if (isX_Axis)
				this.text("No realations between",xx3-10, yy2+13*numE+5);
			else	
				this.text("No realations between",xx3, yy2+13*numE+5);
			this.text("proteins in this group",xx3, yy2+13*numE+18);
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
			if (ww>10){
				float xx =  ggg.get(i).iX.value;
				this.fill(50);
				this.textSize(13);
				if (isSmallMolecule(ggg.get(i).name)){
					this.fill(100);
					this.textSize(10);
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
			if (hh>10){
				float yy =  ggg.get(i).iY.value;
				this.fill(50);
				this.textSize(13);
				if (isSmallMolecule(ggg.get(i).name)){
					this.fill(100);
					this.textSize(10);
				}	
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
						float sat = 50+sat2;
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
		
		// brushingReaction &&&&&& selectedReaction
		/*int brushingReaction = popupReaction2.getIndexInSet(PopupComplex.b);
		if (brushingReaction>=0){
			drawComplex(brushingComplex,200,100,0);
		}
		int selectedRection = popupReaction2.getIndexInSet(PopupComplex.s);
		if (selectedRection>=0){
			drawComplex(selectedComplex,255,0,0);
		}*/
		
		
		this.noStroke();
		for (int i=0;i<ggg.size();i++){
			// Check if this is grouping
			float yy =  ggg.get(i).iY.value;
			float hh = ggg.get(i).iH.value;
			for (int j=0;j<ggg.size();j++){
				float xx =  ggg.get(j).iX.value;
				float ww =ggg.get(j).iW.value;
				
				if (geneRelationList!=null && geneRelationList[i][j]!=null) {
				//	System.out.println(geneRelationList+" " +geneRelationList[i][j]);
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
				System.out.println("drawComplex()	CAN NOT FIND protein = "+a.get(i));
				continue;
			}	
			float yy =  ggg.get(indexI).iY.value;
			float hh = ggg.get(indexI).iH.value;
			for (int j=0;j<a.size();j++){
				int indexJ = getProteinOrderByName(a.get(j));
				if (indexJ<0) { // Exception *******************************
					System.out.println("drawComplex()	CAN NOT FIND protein = "+a.get(j));
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
	
	
		
	
	
	
		
	
	/*
	public static boolean isInTheSameComplex(int g1, int g2, int c1) {
		ArrayList<String> a = getAllGenesInComplexById(c1);
		if (a.indexOf(ggg.get(g1).name)>=0 && a.indexOf(ggg.get(g2).name)>=0){
			return true;
		}
		return false;
		
	}*/
		 
	
	public void setValue(Integrator inter, float value) {
		if (ggg.size()<500){
			inter.target(value);
		}
		else{
			inter.set(value);
		}
	}
				
	public void mousePressed() {
		if (popupOrder.b>=0){
			popupOrder.slider.checkSelectedSlider1();
		}
	}
	public void mouseReleased() {
		if (popupOrder.b>=0){
			popupOrder.slider.checkSelectedSlider2();
		}
	}
	public void mouseDragged() {
		if (popupOrder.b>=0){
			popupOrder.slider.checkSelectedSlider3();
		}
	}
		
	public void mouseClicked() {
		if (button.b>=0){
			thread4=new Thread(loader4);
			thread4.start();
			
		}
		else if (check1.b){
			check1.mouseClicked();
		}
		else if (check2.b){
			check2.mouseClicked();
		}
		else if (popupRelation.b>=0){
			popupRelation.mouseClicked();
		}
		else if (PopupComplex.b>=-1){
			popupComplex.mouseClicked();
		}
		else if (PopupReaction.bPopup && PopupReaction.checkGroup.b){
			PopupReaction.checkGroup.mouseClicked();
			if (PopupReaction.checkGroup.s){
				
			}
		}
		else if (PopupReaction.bPopup){
			popupReaction.mouseClicked();
		}
		else if (popupOrder.b>=0){
			popupOrder.mouseClicked();
		}
		else if (popupGroup.b>=0){
			popupGroup.mouseClicked();
		}
		else if (vennOverview!=null){
			vennOverview.mouseClicked();
			if (vennOverview.brushing>=0)
				vennDetail.compute(currentRelation);
			//update();
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
			PopupGroup.s = 2;
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
			
			File modFile = new File(currentFile);
			File outFile = new File("output.txt");
			SimpleIOHandler io = new SimpleIOHandler();
			Model model;
			try{
				System.out.println("***************** Load data: "+modFile+" ***************************");
				model = io.convertFromOWL(new FileInputStream(modFile));
				mapElementRef = new HashMap<String,String>();
				mapElementGenericRef = new HashMap<String,String>();
				mapElementRDFId = new HashMap<String,String>();
				mapSmallMoleculeRDFId =  new HashMap<String,String>();
				mapComplexRDFId_index =  new HashMap<String,Integer>();
					
				
				 Set<Protein> proteinSet = model.getObjects(Protein.class);
				 int i2=0;
				 for (Protein currentProtein : proteinSet){
					 if (currentProtein.getEntityReference()==null) continue;
					 Object[] s =   currentProtein.getGenericEntityReferences().toArray();
					 for (int i=0;i<s.length;i++){
						 mapElementGenericRef.put(s[i].toString(), currentProtein.getDisplayName());
					 }
					 mapElementRef.put(currentProtein.getEntityReference().toString(), currentProtein.getDisplayName());
					 mapElementRDFId.put(currentProtein.getRDFId().toString(), currentProtein.getDisplayName());
					 System.out.println(" Proteins "+currentProtein.getDisplayName()+"		"+currentProtein.getRDFId());
					 
					 // Gloabal data 
					 String displayName = currentProtein.getDisplayName();
					 if (getProteinOrderByName(displayName)<0)
						 ggg.add(new Gene(displayName,ggg.size()));
					 
					 i2++;
				 }
					
				 Set<SmallMolecule> smallMoleculeSet = model.getObjects(SmallMolecule.class);
				 i2=0;
				 for (SmallMolecule currentMolecule : smallMoleculeSet){
					 if (currentMolecule.getEntityReference()==null) continue;
					 Object[] s =   currentMolecule.getGenericEntityReferences().toArray();
					 for (int i=0;i<s.length;i++){
						 mapElementGenericRef.put(s[i].toString(), currentMolecule.getDisplayName());
					 }
					 mapElementRef.put(currentMolecule.getEntityReference().toString(), currentMolecule.getDisplayName());
					 mapElementRDFId.put(currentMolecule.getRDFId().toString(), currentMolecule.getDisplayName());
					 mapSmallMoleculeRDFId.put(currentMolecule.getRDFId().toString(), currentMolecule.getDisplayName());
					 // System.out.println(i2+"	"+currentMolecule.getEntityReference().toString()+"	getStandardName ="+ currentMolecule.getStandardName());
					 
					 // Gloabal data 
						ggg.add(new Gene(currentMolecule.getDisplayName(),ggg.size()));
					
					 
					 i2++;
				 }
				 
				 
				 /*
				 Set<PhysicalEntity> physicalEntitySet = model.getObjects(PhysicalEntity.class);
				 i2=0;
				 for (PhysicalEntity current : physicalEntitySet){
					// Object[] s =   current.getRDFId().toArray();
					 
					 System.out.println(i2+" PhysicalEntity() = "+current.getDisplayName());
					  
					 System.out.println("	PhysicalEntity() = "+current.getRDFId()+"	PhysicalEntity ="+ current.getComment());
					 i2++;
				 }*/
				 
				 complexSet = model.getObjects(Complex.class);
				 i2=0;
				 for (Complex current : complexSet){
					 System.out.println("Complex getDisplayName() = "+current.getDisplayName()+"	getRDFId = "+current.getRDFId());
					 mapComplexRDFId_index.put(current.getRDFId().toString(), i2);
					 
					 ArrayList<String> components = getComplexById(i2);
					 for (int i=0;i<components.size();i++){
						 System.out.println("	"+components.get(i));
						 }
					 i2++;
				 }
				 i2=0;
				 
				 proteinsInComplex = new ArrayList[complexSet.size()];
				 computeProteinsInComplex();
				 
				 reactionSet = model.getObjects(BiochemicalReaction.class);
				 i2=0;
				 for (BiochemicalReaction current : reactionSet){
					  System.out.println("BiochemicalReaction "+(i2+1)+": "+current.getDisplayName());
					  Object[] s = current.getLeft().toArray();
					  for (int i=0;i<s.length;i++){
						  String name = getProteinName(s[i].toString());
						  if (name!=null)
							  System.out.println("	Left "+(i+1)+" = "+name);
						  else{
							  if (mapComplexRDFId_index.get(s[i].toString())!=null){
								  System.out.println("	Left "+(i+1)+" = "+s[i]);
								  int id = mapComplexRDFId_index.get(s[i].toString());
								  ArrayList<String> components = proteinsInComplex[id];
									 for (int k=0;k<components.size();k++){
										 System.out.println("		 contains "+components.get(k));
								  }
							  }
							  else
								  System.out.println("	Left "+(i+1)+" = "+s[i]);
						  }
					  }

					  Object[] s2 = current.getRight().toArray();
					  for (int i=0;i<s2.length;i++){
						  String name = getProteinName(s2[i].toString());
						  if (name!=null)
							  System.out.println("	Right "+(i+1)+" = "+name);
						  else{
							 if (mapComplexRDFId_index.get(s2[i].toString())!=null){
								  System.out.println("	Right "+(i+1)+" = "+s2[i]);
								  int id = mapComplexRDFId_index.get(s2[i].toString());
								  ArrayList<String> components = proteinsInComplex[id];
									 for (int k=0;k<components.size();k++){
										 System.out.println("		 contains "+components.get(k));
								  }
							  }
							  else		
								  System.out.println("	Right "+(i+1)+" = "+s2[i]);
						  }
					  }
					// System.out.println("  		getLeft() = "+current.getLeft());
					// System.out.println("  		getRight() ="+ current.getRight());
					 i2++;
				 }
				 
				 
				
				/* SIF
				// Iterate through all BioPAX Elements and print basic info
				 SimpleInteractionConverter converter =
					 new SimpleInteractionConverter(new ControlRule());
					 try {
						converter.writeInteractionsInSIF(model, new FileOutputStream("A.txt"));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				String[] lines = p.loadStrings("A.txt");
				for (int i=0;i<lines.length;i++){
					String[] p  = lines[i].split("\t");
 					BioPAXElement element1 = model.getByID(p[0]);
 					BioPAXElement element2 = model.getByID(p[2]);
 					if (lines[i].contains("http://www.pantherdb.or")){
 						System.out.println();
 						System.out.println(i+"	"+lines[i]);
 					String id1 = fetchID(element1);
 					String id2 = fetchID(element2);
 					
 					}
				*/
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
						
						if (s1!=null && s2!=null)
							storeData(s1+"\t"+s2, s1, s2);
						else{
							System.out.println();
							System.out.println("	NULLLLLLLLL");
							System.out.println(match);
							System.out.println();
							System.out.println(match);
							System.out.println(minerList.get(processingMiner)+"	First="+ match.getFirst()+"	"+s1);
							System.out.println(minerList.get(processingMiner)+"	Last ="+ match.getLast()+"	"+s2);
						}
					}	
				}
				 
				try{
					FileOutputStream os = new FileOutputStream(outFile);
					min.writeResult(matches, os);
					
					os.close();
 				}
				catch (IOException e){
					e.printStackTrace();
					return;
				}
				
			}
			System.out.println();
		
			popupComplex.setItems();
			popupReaction.setItems();
			vennOverview.initialize();
			
			
			stateAnimation=0;
			isAllowedDrawing =  true;  //******************* Start drawing **************
			
			// Compute the summary for each Gene
			Gene.compute();
			
			Gene.computeGeneRelationList();
			Gene.computeGeneGeneInComplex();
			//write();
			
			
			//vennOverview.compute();
			//Gene.orderByRandom(p);
			PopupOrder.s =0;
			PopupGroup.s = 0;
		}
	}
	
	public static void storeData(String rel, String gene1, String gene2){
		// Store results for visualization
		if (!pairs[processingMiner].contains(rel)){
			pairs[processingMiner].add(rel);
		}	
	}
	
	public int getProteinOrderByName(String name) {
		for (int i=0;i<ggg.size();i++){
			if (ggg.get(i).name.equals(name))
				return i;
		}
		return -1;
	}
	
	public static String getProteinName(String ref){	
		String s1 = mapElementGenericRef.get(ref);
		if (s1==null)
			s1 = mapElementRef.get(ref);
		if (s1==null)
			s1 = mapElementRDFId.get(ref);
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
		int i2=0;
		for (Complex current : complexSet){
			 if (i2==id){
				  Object[] s2 = current.getComponent().toArray();
				  for (int i=0;i<s2.length;i++){
					  if (getProteinName(s2[i].toString())!=null)
						  components.add(getProteinName(s2[i].toString()));
					  else
						  components.add(s2[i].toString());
				 }
			 }
			 i2++;
		 }
		 return components;
	}
	
	public static void computeProteinsInComplex(){	
		int i2=0;
		for (Complex current : complexSet){
			proteinsInComplex[i2] = getProteinsInComplexById(i2);
			i2++;
		}
			
		
	}
		
	public static ArrayList<String> getProteinsInComplexById(int id){	
		ArrayList<String> components = new ArrayList<String>(); 
		int i2=0;
		
		 boolean found = false;
		 for (Complex current : complexSet){
			 if (i2==id){
				  Object[] s2 = current.getComponent().toArray();
				  for (int i=0;i<s2.length;i++){
					  if (getProteinName(s2[i].toString())!=null)
						  components.add(getProteinName(s2[i].toString()));
					  else {
						  if (mapComplexRDFId_index.get(s2[i].toString())==null){
							  components.add(s2[i].toString());
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
				  found = true;
			 }
			 i2++;
		 }
		 if (!found ){
			 System.err.println("********** CAN NOT find complex id = "+id);
		 }
		 return components;
	}
	
	
	
		
	
	
	//Update genes for drawing
	public void write(){	
		String outFile =  currentFile.replace(".owl", ".txt");
		int total =0;
		for (int m=0;m<Venn_Overview.minerGlobalIDof.length;m++){
			int globalMinerId = Venn_Overview.minerGlobalIDof[m];
			total += pairs[globalMinerId].size();
		}	
	
		String[] outStrings = new String[total];
		int count=0;
		for (int m=0;m<Venn_Overview.minerGlobalIDof.length;m++){
			int globalMinerId = Venn_Overview.minerGlobalIDof[m];
			for (int p=0;p<pairs[globalMinerId].size();p++){
				outStrings[count] = pairs[globalMinerId].get(p)+"\t"+minerList.get(globalMinerId).getName();
				count++;
			}
		}	
		this.saveStrings(outFile, outStrings);
	}
	
	
	// Thread for ordering
	class ThreadLoader2 implements Runnable {
		public ThreadLoader2() {}
		public void run() {
			Gene.orderBySimilarity();
		}
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
				vennDetail = new Venn_Detail(parent);
				thread1=new Thread(loader1);
				thread1.start();
			}
		}
	}	
	
	void mouseWheel(int delta) {
		if (PopupComplex.b>=0){
		//	PopupComplex.y2 -= delta/2;
		//	if (PopupComplex.y2>20)
		//		PopupComplex.y2 = 20;
		}
	}

	
	
}
