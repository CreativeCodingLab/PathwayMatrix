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

import static javax.swing.JOptionPane.showMessageDialog;
import static main.MainMatrix.geneRelationList;
import static main.MainMatrix.ggg;
import static main.MainMatrix.minerList;
import static main.MainMatrix.minerNames;
import static main.MainMatrix.pairs;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.biopax.paxtools.io.SimpleIOHandler;
import org.biopax.paxtools.model.BioPAXElement;
import org.biopax.paxtools.model.Model;
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
import org.biopax.paxtools.pattern.miner.InComplexWithMiner;
import org.biopax.paxtools.pattern.miner.InteractsWithMiner;
import org.biopax.paxtools.pattern.miner.Miner;
import org.biopax.paxtools.pattern.miner.NeighborOfMiner;
import org.biopax.paxtools.pattern.miner.ReactsWithMiner;
import org.biopax.paxtools.pattern.miner.RelatedGenesOfInteractionsMiner;
import org.biopax.paxtools.pattern.miner.UbiquitousIDMiner;
import org.biopax.paxtools.pattern.miner.UsedToProduceMiner;
import org.biopax.paxtools.pattern.util.Blacklist;

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
	public String currentFile = "./level3/Regulation of DNA Replication.owl";
	//public String currentFile = "./level3/ATM Mediated Phosphorylation of Repair Proteins.owl";
	
	public static Button button;
	
	// Store the genes results
	public static ArrayList<String>[] pairs;
	public static ArrayList<String>[] genes;
	public static ArrayList<Integer>[][] geneRelationList;
	
	// Global data
	public static ArrayList<String> allGenes = new ArrayList<String>();
	public static String[] minerNames;
	//public static ArrayList<String> gen = new ArrayList<String>();
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
		//minerList.add(new ControlsStateChangeDetailedMiner());
		//minerList.add(new ControlsPhosphorylationMiner());
		minerList.add(new ControlsTransportMiner());
		minerList.add(new ControlsExpressionMiner());
		minerList.add(new ControlsExpressionWithConvMiner());
		//minerList.add(new CSCOThroughDegradationMiner());
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
		minerList.add(new RelatedGenesOfInteractionsMiner());
		minerList.add(new UbiquitousIDMiner());
	
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
		popupOrder  = new PopupOrder(this);
		popupGroup  = new PopupGroup(this);
		check1 = new CheckBox(this, "Lensing");
		check2 = new CheckBox(this, "Highlight groups");
		//VEN DIAGRAM
		vennOverview = new Venn_Overview(this);
		vennDetail = new Venn_Detail(this);
		thread1=new Thread(loader1);
		thread1.start();
	}	
	
	
	public void computeRelationship(String fileName, int relID) {
		File modFile = new File(fileName);
		File outFile = new File("output.txt");
		SimpleIOHandler io = new SimpleIOHandler();
		Model model;
		try{
			model = io.convertFromOWL(new FileInputStream(modFile));
		}
		catch (FileNotFoundException e){
			e.printStackTrace();
			showMessageDialog(this, "File not found: " + modFile.getPath());
			return;
		}

		// Search
		Miner min = minerList.get(relID);
		Pattern p = min.getPattern();
		Map<BioPAXElement,List<Match>> matches = Searcher.search(model, p, null);

		try{
			FileOutputStream os = new FileOutputStream(outFile);
			min.writeResult(matches, os);
			
			os.close();
		}
		catch (IOException e){
			e.printStackTrace();
			showMessageDialog(this, "Error occurred while writing the results");
			return;
		}
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
			this.text(message, marginX+20,this.height-14);
		}

		if (isAllowedDrawing){
			if (ggg==null || ggg.size()==0)
				return;
			else{
				size = (this.height-marginY)/allGenes.size();
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
				System.out.println("**************************************************");
				return;
			}
		}
		
		
		float x2 = this.width-600;
		float y2 = 140;
		this.fill(0);
		this.textAlign(PApplet.LEFT);
		this.textSize(14);
		this.text("File: "+currentFile, x2, y2);
		// find minerID index
		if (Venn_Overview.minerGlobalIDof!=null){
			if (currentRelation>=0){
				this.fill(colorRelations[currentRelation]);
				this.text("Realationship "+currentRelation+": "+minerList.get(currentRelation), x2+250, y2+20);
				this.text("Total genes: "+genes[currentRelation].size(), x2+250, y2+40);
				this.text("Total relations: "+pairs[currentRelation].size(), x2+250, y2+60);
			}
			this.fill(0);
			this.text("Pathway summary", x2, y2+20);
			this.text("Total genes: "+allGenes.size(), x2, y2+40);
			int totalRelations = 0;
			for (int i=0;i<pairs.length;i++){
				totalRelations+=pairs[i].size();
			} 
			this.text("Total relations: "+totalRelations, x2, y2+60);
		}
		vennOverview.draw(x2+50,300,10);
		//vennDetail.draw(x2+100,500,10);
		
		// Draw button
		check1.draw(this.width-600, 7);
		check2.draw(this.width-600, 27);
		button.draw();
		popupGroup.draw(this.width-258);
		popupOrder.draw(this.width-379);
		popupRelation.draw(this.width-500);
		
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
			this.fill(70);
			float fontSize = PApplet.map(numE, 1, maxElement, 10, 18);
			this.textSize(fontSize);
			if (locals[index].size()>1){
				name = locals[index].size()+" genes";
				this.fill(0);
			}	
			if (ww>8){
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
				float yy =  ggg.get(index).iY.value;
				this.textAlign(PApplet.RIGHT);
				this.text(name, marginX-6, yy+hh/2+fontSize/3);
			}
		}
		

		this.noStroke();
		for (Map.Entry<Integer, Integer> entryI : leaderSortedMap.entrySet()) {
			int indexI = entryI.getKey();
			// Check if this is grouping
			float xx =  ggg.get(indexI).iX.value;
			float ww = ggg.get(indexI).iW.value;
			
			int numEx = locals[indexI].size();
			
			for (Map.Entry<Integer, Integer> entryJ : leaderSortedMap.entrySet()) {
				int indexJ = entryJ.getKey();
				float yy =  ggg.get(indexJ).iY.value;
				float hh =ggg.get(indexJ).iH.value;
				
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
			this.text("These genes are:",xx3, yy2+13*numE+3);
		}	
		else{
			this.textSize(13);
			if (isX_Axis)
				this.text("No realations between",xx3-10, yy2+13*numE+5);
			else	
				this.text("No realations between",xx3, yy2+13*numE+5);
			this.text("genes in this group",xx3, yy2+13*numE+18);
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
				// Draw rose chart
				/*
				if (Venn_Overview.numMiner>0){
					float[] aValues = new float[Venn_Overview.numMiner];
					for (int r=0;r<Venn_Overview.numMiner && main.Gene.hGenes!=null;r++){
						String g = gen.get(i);
						if (main.Gene.hGenes.get(g) == null) continue;// thread exception
						//int count = main.Gene.hGenes.get(g)[r];
						//float val = PApplet.map(count, 0, main.Gene.maxRelationOfGenes, 0, 1);
						//aValues[r] = val;
					}
					float x4 = xx+ww/2;
					float y4 = marginY-20;
				//	drawRose(x4, y4, ww*2f, aValues);
				}*/
				
				this.fill(50,50,50);
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
				this.fill(50,50,50);
				this.textAlign(PApplet.RIGHT);
				this.text(ggg.get(i).name, marginX-6, yy+hh/2+5);
			}
		}
			
		
		
		this.noStroke();
		for (int i=0;i<ggg.size();i++){
			// Check if this is grouping
			float xx =  ggg.get(i).iX.value;
			float ww = ggg.get(i).iW.value;
			for (int j=0;j<ggg.size();j++){
				float yy =  ggg.get(j).iY.value;
				float hh =ggg.get(j).iH.value;
				// Draw Rosemary chart
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
			main.MainMatrix.stateAnimation=0;
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
			genes = new ArrayList[minerList.size()];
			for (int i=0;i<minerList.size();i++){
				genes[i] = new ArrayList<String>();
			}
			
			allGenes = new ArrayList<String>();
			ggg = new ArrayList<Gene>();
			geneRelationList = null;
			leaderSortedMap = null;
			
			for (processingMiner=0;processingMiner<minerList.size();processingMiner++){
				 message = "Processing realtion ("+processingMiner+"/"+minerList.size()
					+"): "+minerList.get(processingMiner);
				 computeRelationship(currentFile, processingMiner);
				
			}
			System.out.println();
		
		
			
			vennOverview.initialize();
			
			// Compute the summary for each Gene
			Gene.compute();
			
			Gene.computeGeneRalationList();
			//write();
			
			
			stateAnimation=0;
			isAllowedDrawing =  true;
			
			vennOverview.compute();
			
			
			PopupOrder.s =0;
			Gene.orderByRandom(p);
			PopupGroup.s = 0;
			
			/*
			 System.out.println("BRCA1 NBN= "+Gene.computeDis(0,1));
			 System.out.println("ADP ATM= "+Gene.computeDis(6,3));
			int index =  Gene.getSimilarGene(0, new ArrayList<Integer>());
			if (index>=0)
				System.out.println("Most similar to BRA1= "+ggg.get(index).name+"	dis="+Gene.computeDis(0,2));
			*/
			
		}
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
	
	
	
	
}
