package main;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import processing.core.PApplet;

import GraphLayout.Node;


public class Pathway2{
  public ArrayList<Pathway2> subPathwayList;
  public ArrayList<String> reactList;
  //public ArrayList<Integer> nodeIdList;
  public int f = -1; 
  public int level = -1; 
  public String displayName = "?";
  
  // for drawing
  public float radius = 0;
  public int numReactions = 0;
   
  // Constructor
  Pathway2(int f_, String dName, int level_){
	  f=f_;
	  level = level_;
	  displayName = dName;
	  
	  subPathwayList =  new ArrayList<Pathway2>();
	  reactList = new ArrayList<String>();
	//  nodeIdList = new ArrayList<Integer>();
  }
  
  public int computeSize(){
	  numReactions = reactList.size();
	  for (int i=0; i<subPathwayList.size();i++){
		  numReactions+=subPathwayList.get(i).computeSize();
	  }
	  return numReactions;
  }
		
  public void draw(PApplet parent, float x_, float y_, float al_){
	  radius = PApplet.sqrt(numReactions)*8;
	  parent.noStroke();
	  /*
	  if (level==0){
		 // parent.fill(0);
		 // parent.textAlign(PApplet.CENTER);
		 // parent.text(displayName,x_, y_);
		  parent.fill(0,150);
		//  parent.arc(x_, y_, radius*2, radius*2,al_-PApplet.PI/2,al_-PApplet.PI/2+PApplet.PI);

	  }	 
	  else if (level==1){
		//  parent.fill(0);
		//  parent.textAlign(PApplet.CENTER);
		//  parent.text(displayName,x_, y_);
		  parent.fill(255,0,0,50);
	  }	  
	  else if (level==2)
		  parent.fill(0,255,0,50);
	  else if (level==3)
		  parent.fill(0,0,255,50);
	  else 
		  parent.fill(0,50);*/
	  
	  
	  float numSect = reactList.size();  // Number of points on the circles including reactions and pathways 
	  for (int i=0; i<subPathwayList.size();i++){
		  Pathway2 pathway = subPathwayList.get(i);
		   numSect += pathway.numReactions/2;
	  }
			
	  int countReactionLeft = 0;
	  int countReactionRight = 0;
	  for (int i=0; i<reactList.size();i++){
		  String nodeName = reactList.get(i);
		  Node node = getNodeByName(nodeName);
		  float al=0;
		  if (i%2==0){
			  al = al_+PApplet.PI*0.6f -(countReactionRight+1f)/(numSect+1f)*PApplet.PI*1.2f;  // Right
			  countReactionRight++;
		  }
		  else{
		  	  al = al_-PApplet.PI*0.6f +(countReactionLeft+1f)/(numSect+1f)*PApplet.PI*1.2f;
			  countReactionLeft++;
		  }
		  float xR2 = x_ + (radius+node.size/2)*PApplet.cos(al);
		  float yR2 = y_ + (radius+node.size/2)*PApplet.sin(al);
		  node.iAlpha.target(al);
		  node.iX.target(xR2);
		  node.iY.target(yR2);
	  }
	  int current = countReactionLeft;
	  for (int i=0; i<subPathwayList.size();i++){
		  Pathway2 pathway = subPathwayList.get(i);
		  
		  float al = al_-PApplet.PI*0.6f +(current+pathway.numReactions/4+1f)/(numSect+1)*PApplet.PI*1.2f;
		  float xR2 = x_ + (radius+pathway.radius/4)*PApplet.cos(al);
		  float yR2 = y_ + (radius+pathway.radius/4)*PApplet.sin(al);
		  pathway.draw(parent, xR2, yR2,al);
		  current+=pathway.numReactions/2;
	  }
	  parent.fill(100+level*20);
	  parent.arc(x_, y_, radius*2, radius*2,al_-PApplet.PI/2,al_-PApplet.PI/2+PApplet.PI*2);
	  
  }
  public Node getNodeByName(String name_){
	  for (int i=0; i<PathwayView.g.nodes.size();i++){
		  Node node = PathwayView.g.nodes.get(i);
		  if (node.name.equals(name_))
			  return node;
	  }
	  return null;
  }
			
  public boolean isContainReaction(String rName){
	  for (int r=0;r<reactList.size();r++){
		  String name = reactList.get(r);
		  if (name==null) continue;
 		  if (name.equals(rName))
			  return true;
	  }
	  for (int p=0;p<subPathwayList.size();p++){
		  Pathway2 path = subPathwayList.get(p);
		  boolean result = path.isContainReaction(rName);
		  if (result)
			  return true;
	  }
	  return false;
  }
  
  public boolean isContainPathway(String pName){
	  //if (pName.equals(displayName))
	  //	  return true;
	  for (int p=0;p<subPathwayList.size();p++){
		  Pathway2 path = subPathwayList.get(p);
		  if (path.displayName.equals(pName) || path.isContainPathway(pName))
			  return true;
	  }
	  return false;
  }
  
  public ArrayList<Pathway2> printRecursively(){
	  ArrayList<Pathway2> a = new ArrayList<Pathway2>();
	  a.add(this);	
	  
	  for (int p=0;p<subPathwayList.size();p++){
		  ArrayList<Pathway2> b = subPathwayList.get(p).printRecursively();
		  for (int i=0;i<b.size();i++){
			  a.add(b.get(i));
		  }
	  }
	  return a;
  }
   
  
  
  Color getGradient(float value){
   return Color.RED;
  }
}