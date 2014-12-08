package main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import processing.core.PApplet;

import edu.uic.ncdm.venn.Venn_Overview;

import static main.MainMatrixVersion_1_6.pairs;
import static main.MainMatrixVersion_1_6.ggg;
import static main.MainMatrixVersion_1_6.geneRelationList;
import static main.MainMatrixVersion_1_6.gene_gene_InComplex;
import static main.MainMatrixVersion_1_6.maxGeneInComplex;
import static main.MainMatrixVersion_1_6.leaderSortedMap;
import static main.MainMatrixVersion_1_6.locals;
import static edu.uic.ncdm.venn.Venn_Overview.numMinerContainData;
import static edu.uic.ncdm.venn.Venn_Overview.minerGlobalIDof;;


public class Gene {
	// For a gene, we have the count number of each type of relations
	public static Hashtable<String, int[]> hGenes = new Hashtable<String,int[]>();
	public static int maxRelationOfGenes = -1;
	public String name = "????";
	public Integrator iX, iY, iH,iW;
	public int order;
	
	public Gene(String name_, int order_){
		name = name_;
		iX = new Integrator(main.MainMatrixVersion_1_6.marginX,.5f,.1f);
		iY = new Integrator(main.MainMatrixVersion_1_6.marginY,.5f,.1f);
		iW = new Integrator(0,.5f,.1f);
		iH = new Integrator(0,.5f,.1f);
		order = order_;
	}
	
	public static void compute(){
		 hGenes = new Hashtable<String,int[]>();
		 for (int i=0; i<main.MainMatrixVersion_1_6.ggg.size();i++){
			 hGenes.put(main.MainMatrixVersion_1_6.ggg.get(i).name, new int[numMinerContainData]);
		 }
		 maxRelationOfGenes = -1;
		 for (int j=0; j<numMinerContainData;j++){
			 int m =  minerGlobalIDof[j];
			 for (int p=0; p<pairs[m].size();p++){
				 String g = pairs[m].get(p).split("\t")[0];
				 int[] list =  hGenes.get(g);
				 if (list==null) continue;
				 list[j]++;
				 // compute max number of relationships
				 if (list[j]>maxRelationOfGenes)
					 maxRelationOfGenes = list[j];
				 hGenes.put(g, list);
			 }
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void computeGeneRelationList(){
		geneRelationList = new ArrayList[ggg.size()][ggg.size()];
		for (int localMinerID=0;localMinerID<Venn_Overview.minerGlobalIDof.length;localMinerID++){
			int globalMinerId = Venn_Overview.minerGlobalIDof[localMinerID];
		    for (int i=0;i<ggg.size();i++){
				for (int j=0;j<ggg.size();j++){
					if (pairs[globalMinerId].contains(ggg.get(i).name+"\t"+ggg.get(j).name)){
						if (geneRelationList[i][j]==null)
							geneRelationList[i][j] = new ArrayList<Integer>();
						geneRelationList[i][j].add(localMinerID);
					}
				}
			}
		}	
		
		ArrayList<Integer> removeList = new ArrayList<Integer>();
	    for (int i=0;i<ggg.size();i++){
	    	int count = 0;
			for (int j=0;j<ggg.size();j++){
				if (geneRelationList[i][j]!=null || geneRelationList[j][i]!=null){
					count++;
				}
			}
			if (count==0)
				removeList.add(i);
		}
		System.out.println(" non-intereact proteins="+removeList);
		for (int i=removeList.size()-1;i>=0;i--){
			int index = removeList.get(i);
			System.out.println("	"+i+":"+ggg.get(index).name);
			ggg.remove(index);
		}
		    
		geneRelationList = new ArrayList[ggg.size()][ggg.size()];
		for (int localMinerID=0;localMinerID<Venn_Overview.minerGlobalIDof.length;localMinerID++){
			int globalMinerId = Venn_Overview.minerGlobalIDof[localMinerID];
		    for (int i=0;i<ggg.size();i++){
				for (int j=0;j<ggg.size();j++){
					if (pairs[globalMinerId].contains(ggg.get(i).name+"\t"+ggg.get(j).name)){
						if (geneRelationList[i][j]==null)
							geneRelationList[i][j] = new ArrayList<Integer>();
						geneRelationList[i][j].add(localMinerID);
					}
				}
			}
		}	
	 }	 
	
	public static void computeGeneGeneInComplex(){
		maxGeneInComplex = 0;
		gene_gene_InComplex = new int[ggg.size()][ggg.size()];
		for (int c=0;c<main.MainMatrixVersion_1_6.complexSet.size();c++){
			ArrayList<String> a = main.MainMatrixVersion_1_6.proteinsInComplex[c];
			for (int i=0;i<ggg.size();i++){
				for (int j=0;j<ggg.size();j++){
					if (a.indexOf(ggg.get(i).name)>=0 && a.indexOf(ggg.get(j).name)>=0){
						gene_gene_InComplex[i][j]++;
						if (gene_gene_InComplex[i][j]>maxGeneInComplex)
							maxGeneInComplex = gene_gene_InComplex[i][j];
					}
				}
			}
		 }	
	 }	 
	
	
	//Reverse genes for drawing
	public static void reveseGenesForDrawing(){	
		for (int i=0;i<ggg.size();i++){
			ggg.get(i).order = ggg.size()-ggg.get(i).order-1;
		}
	}
	//Swap genes for drawing
	public static void swapGenesForDrawing(){	
		for (int i=0;i<ggg.size();i++){
			ggg.get(i).order =(ggg.get(i).order+ ggg.size()/2)% ggg.size();
		}
	}
	
	// Order genes by name
	public static void orderByName(){	
		Map<String, Integer> unsortMap = new HashMap<String, Integer>();
		for (int i=0;i<ggg.size();i++){
			unsortMap.put(ggg.get(i).name, i);
		}
		Map<String, Integer> treeMap = new TreeMap<String, Integer>(unsortMap);
		int count=0;
		for (Map.Entry<String, Integer> entry : treeMap.entrySet()) {
			int inputOrder = entry.getValue();
			ggg.get(inputOrder).order = count;
			count++;
		}
	}
	
	// Order genes by reading order
	public static void orderByReadingOrder(){	
		for (int i=0;i<ggg.size();i++){
			ggg.get(i).order = i;
		}
	}
	// Order genes by random
	public static void orderByRandom(PApplet p){	
		ArrayList<Integer> a = new ArrayList<Integer>();
		for (int i=0;i<ggg.size();i++){
			a.add(i);
		}
		while (a.size()>0){
			int num = (int) p.random(a.size());
			ggg.get(a.size()-1).order = a.get(num);
			a.remove(num);
		}
	}
	
	
	// Order genes by a relation
	public static void orderByRelation(int orederedRelation){	
		Map<String, Integer> mapGene = new HashMap<String, Integer>();
		for (int i=0;i<ggg.size();i++){
			mapGene.put(ggg.get(i).name, i);
		}
		
		
		Map<String, Integer> unsortMap = new HashMap<String, Integer>();
		for (int i=0;i<ggg.size();i++){
			unsortMap.put(ggg.get(i).name, 0);
		}
		
		for (int i=0;i<pairs[orederedRelation].size();i++){
			String pair = pairs[orederedRelation].get(i);
			String gene1 = pair.split("\t")[0];
			int c = unsortMap.get(gene1);
			c++;
			unsortMap.put(gene1, c);
		}
		
		Map<String, Integer> sortedMap = sortByComparator(unsortMap);
		
		int count=0;
		for (Map.Entry<String, Integer> entry : sortedMap.entrySet()) {
			String gene1 = entry.getKey();
			int inputOrder = mapGene.get(gene1);
			ggg.get(inputOrder).order = count;
			count++;
		}
	}
	
	
	public static Map<String, Integer> sortByComparator(Map<String, Integer> unsortMap) {
		// Convert Map to List
		List<Map.Entry<String, Integer>> list = 
			new LinkedList<Map.Entry<String, Integer>>(unsortMap.entrySet());
 
		// Sort list with comparator, to compare the Map values
		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> o1,
                                           Map.Entry<String, Integer> o2) {
				return (o1.getValue()).compareTo(o2.getValue());
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
	
	private static Map<Integer, Integer> sortByComparator2(Map<Integer, Integer> unsortMap) {
		// Convert Map to List
		List<Map.Entry<Integer, Integer>> list = 
			new LinkedList<Map.Entry<Integer, Integer>>(unsortMap.entrySet());
 
		// Sort list with comparator, to compare the Map values
		Collections.sort(list, new Comparator<Map.Entry<Integer, Integer>>() {
			public int compare(Map.Entry<Integer, Integer> o1,
                                           Map.Entry<Integer, Integer> o2) {
				return (o1.getValue()).compareTo(o2.getValue());
			}
		});
 
		// Convert sorted map back to a Map
		Map<Integer, Integer> sortedMap = new LinkedHashMap<Integer, Integer>();
		for (Iterator<Map.Entry<Integer, Integer>> it = list.iterator(); it.hasNext();) {
			Map.Entry<Integer, Integer> entry = it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}
	
	
	
	// Order by similarity *********** SMALL MOLECULES are first
	public static void orderBySimilarity(){	
		int maxminIndex = -1;
		double maxRelation = -100;
		
		// Find the smallest molecule
		for (int p=0;p<ggg.size();p++){
			if (main.MainMatrixVersion_1_6.isSmallMolecule(ggg.get(p).name)){
				int count = getNumberRelationOfProtein(p);
				if (count>maxRelation){
					maxRelation = count;
					maxminIndex = p;
				}	
			}
		}
		
		// Find the smallest protein
		double minRelation = Double.POSITIVE_INFINITY;
		if (maxminIndex<0){
			for (int p=0;p<ggg.size();p++){
				if (main.MainMatrixVersion_1_6.isSmallMolecule(ggg.get(p).name)){
					continue;
				}
				int count = getNumberRelationOfProtein(p);
				if (count<minRelation){
					minRelation = count;
					maxminIndex = p;
				}	
			}
		}
		
		// Process small molecules
		ArrayList<Integer> processedProteins =  new ArrayList<Integer>();
		int beginIndex = maxminIndex;
		int order = 0;
		ggg.get(beginIndex).order=order;
		order++;
		processedProteins.add(beginIndex);
		
		// Eliminate proteins
		for (int i=0;i<ggg.size();i++){
			if (!main.MainMatrixVersion_1_6.isSmallMolecule(ggg.get(i).name)){
				processedProteins.add(i);
			}
		}	
		for (int i=0;i<ggg.size();i++){
			int similarIndex =  getSimilarGene(beginIndex, processedProteins);
			if (similarIndex<0) break;  // can not find more small molecules
			
			ggg.get(similarIndex).order=order;
			order++;
			beginIndex = similarIndex;
			processedProteins.add(similarIndex);
		}
		
		ArrayList<Integer> processedGenes =  new ArrayList<Integer>();
		
		// Eliminate small molecules
		for (int i=0;i<ggg.size();i++){
			if (main.MainMatrixVersion_1_6.isSmallMolecule(ggg.get(i).name)){
				processedGenes.add(i);
			}
		}
		
		for (int i=0;i<ggg.size();i++){
			int similarIndex =  getSimilarGene(beginIndex, processedGenes);
			if (similarIndex<0) break;  // can not find more protein
			
			ggg.get(similarIndex).order=order;
			//System.out.println("	"+i+"  beginIndex="+beginIndex+"	similarIndex="+similarIndex+ " = order "+order);
			
			order++;
			beginIndex = similarIndex;
			processedGenes.add(similarIndex);
		}
		
		
		
	}
	
	// Order by similarity *********** SMALL MOLECULES are last
	/*
	public static void orderBySimilarity(){	
		int minIndex = -1;
		double minRelation = Double.POSITIVE_INFINITY;
		// Find the smallest protein
		for (int p=0;p<ggg.size();p++){
			if (main.MainMatrix.isSmallMolecule(ggg.get(p).name)){
				continue;
			}
			int count = getNumberRelationOfProtein(p);
			if (count<minRelation){
				minRelation = count;
				minIndex = p;
			}	
		}
		
		System.out.println("	minIndex = "+minIndex);
		ArrayList<Integer> processedGenes =  new ArrayList<Integer>();
		int beginIndex = minIndex;
		int order = 0;
			
		ggg.get(beginIndex).order=order;
		order++;
		processedGenes.add(beginIndex);
		// Eliminate small molecules
		for (int i=0;i<ggg.size();i++){
			if (main.MainMatrix.isSmallMolecule(ggg.get(i).name)){
				processedGenes.add(i);
				System.out.println("   isSmallMolecule ="+ggg.get(i).name);
			}
		}
		
		for (int i=0;i<ggg.size();i++){
			int similarIndex =  getSimilarGene(beginIndex, processedGenes);
			if (similarIndex<0) break;  // can not find more protein
			
			ggg.get(similarIndex).order=order;
			System.out.println("	"+i+"  beginIndex="+beginIndex+"	similarIndex="+similarIndex+ " = order "+order);
			
			order++;
			beginIndex = similarIndex;
			processedGenes.add(similarIndex);
		}
		
		// Process small molecules
		ArrayList<Integer> processedProteins =  new ArrayList<Integer>();
		for (int i=0;i<ggg.size();i++){
			if (!main.MainMatrix.isSmallMolecule(ggg.get(i).name)){
				processedProteins.add(i);
			}
		}	
		for (int i=0;i<ggg.size();i++){
			int similarIndex =  getSimilarGene(beginIndex, processedProteins);
			if (similarIndex<0) break;  // can not find more small molecules
			
			ggg.get(similarIndex).order=order;
			System.out.println("	"+i+"  beginMolecule="+beginIndex+"	similarIndex="+similarIndex+ " = order "+order);
			
			order++;
			beginIndex = similarIndex;
			processedProteins.add(similarIndex);
		}
		
	}
	*/
	
	/*
	
	// Order by Complex
	public static void orderByComplex(){	
		Map<String, Integer> unsortMap = new HashMap<String, Integer>();
		for (int i=0;i<ggg.size();i++){
			unsortMap.put(ggg.get(i).name, ggg.get(i).order);
		}
		Map<String, Integer> sortedMap = sortByComparator(unsortMap);
		
		
		Map.Entry<String, Integer> firstEntry = null;
		for (Map.Entry<String, Integer> entry : sortedMap.entrySet()) {
			firstEntry = entry;
			break;
		}
		int index1 = sortedMap.get(firstEntry.getKey());
		int orderReading1 = index1;
		
		ArrayList<Integer> processedGenes =  new ArrayList<Integer>();
		for (int i=0;i<ggg.size();i++){
			ggg.get(index1).order=i;
			processedGenes.add(orderReading1);
			if (i==ggg.size()-1) break;
			int similarIndex =  getSimilarGeneComplex(orderReading1,processedGenes);
			index1 = similarIndex;
			orderReading1 = index1;
		}
	}
	
	
	public static int getSimilarGeneComplex(int orderReading1, ArrayList<Integer> a){
		float minDis = Float.POSITIVE_INFINITY;
		int minIndex = -1;
		for (int i=0;i<ggg.size();i++){
			int orderReading2 = i;
			if (orderReading1==orderReading2) continue;
			if (a.contains(orderReading2)) continue;
			float dis = -gene_gene_InComplex[orderReading1][orderReading2];
			if (dis<minDis){
				minDis = dis;
				minIndex = i;
			}
		}
		return minIndex;
	}
	
		*/
	
		
	public static int getNumberRelationOfProtein(int p){
		int count=0;
		for (int i=0;i<ggg.size();i++){
			if (geneRelationList[p][i]!=null)
				count+=geneRelationList[p][i].size();
			if (geneRelationList[i][p]!=null)
				count+=geneRelationList[i][p].size();
		}
		return count;
	}
	
	public static int getSimilarGene(int orderReading1, ArrayList<Integer> a){
		float minDis = Float.POSITIVE_INFINITY;
		int minIndex = -1;
		for (int i=0;i<ggg.size();i++){
			int orderReading2 = i;
			if (orderReading1==orderReading2) continue;
			if (a.contains(orderReading2)) continue;
			float dis = computeDis(orderReading1,orderReading2, main.MainMatrixVersion_1_6.popupOrder.slider.val);
			if (dis<minDis){
				minDis = dis;
				minIndex = i;
			}
		}
		return minIndex;
	}
	
	public static float computeDis(int orderReading1, int orderReading2, float val){
		float dis = 0;
		if (geneRelationList==null) return 0;
		for (int i=0;i<ggg.size();i++){
			int orderReading3 = i;
			if (orderReading3==orderReading1 || orderReading3 ==orderReading2){
				dis += computeDisOfArrayList(geneRelationList[orderReading1][orderReading2],geneRelationList[orderReading2][orderReading1],val);
			}
			else{
				dis += computeDisOfArrayList(geneRelationList[orderReading1][orderReading3],geneRelationList[orderReading2][orderReading3],val);
				dis += computeDisOfArrayList(geneRelationList[orderReading3][orderReading1],geneRelationList[orderReading3][orderReading2],val);
			}
		}
		return dis;
	}
	

	public static float computeDisOfArrayList(ArrayList<Integer> a1, ArrayList<Integer> a2, float val){
		if (a1==null && a2==null) return 0;
		else if (a1==null) return a2.size();
		else if (a2==null) return a1.size();
		
		int numCommonElements = 0;
		for (int i=0;i<a1.size();i++){
			int num1 = a1.get(i);
			if (a2.contains(num1))
				numCommonElements++;
		}
		return (a1.size()+a2.size()-val*numCommonElements);
		// main.MainViewer.popupOrder.slider.val=0    We consider total number of element;
		// main.MainViewer.popupOrder.slider.val=2    We consider only the different;
	}
	
	
	
// ************************ grouping
	// Group by similarity
	@SuppressWarnings("unchecked")
	public static void groupBySimilarity(){
		locals = new ArrayList[ggg.size()];
		for (int i=0;i<ggg.size();i++){
			locals[i] = new ArrayList<Integer>();
		}
			
		ArrayList<Integer> leaderList = new ArrayList<Integer>();
		for (int i=0;i<ggg.size();i++){
			//System.out.println(indexLeader+" "+Gene.computeDis(ggg.get(indexLeader).orderReading, ggg.get(i).orderReading,2));
			int foundLeader = 0;
			int indexLeader =-1;
			for (int l=0;l<leaderList.size();l++){
				int leader = leaderList.get(l);
				if (Gene.computeDis(leader, i,2)==0){
					ggg.get(i).order = ggg.get(leader).order;
					indexLeader = leader;
					foundLeader++;
				}
			}
			//System.out.println(i+" "+ggg.get(i).name+;
			if (foundLeader==0){
				indexLeader=i;
				leaderList.add(indexLeader);
				locals[indexLeader].add(indexLeader);
			}
			else if (foundLeader==1){
				locals[indexLeader].add(i);
			}
			else if (foundLeader>1){
				System.out.println("********************* ERROR= Found "+foundLeader+" leaders *******************************");
			}
		}
		
		// order leader list by leader order
		Map<Integer, Integer> unsortMap = new HashMap<Integer, Integer>();
		for (int i=0;i<leaderList.size();i++){
			int leader = leaderList.get(i);
			unsortMap.put(leader, ggg.get(leader).order);
		}
	
		leaderSortedMap = sortByComparator2(unsortMap);
		
		/*
		Map.Entry<String, Integer> firstEntry = null;
		for (Map.Entry<String, Integer> entry : sortedMap.entrySet()) {
			firstEntry = entry;
			break;
		}
		*/
	}
}
