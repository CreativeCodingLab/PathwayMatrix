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
  public boolean isExpanded = true;
  
  public float x = 0;
  public float y = 0;
  
  
  // Constructor
  Pathway2(int f_, String dName, int level_){
	  f=f_;
	  level = level_;
	  displayName = dName;
	  
	  subPathwayList =  new ArrayList<Pathway2>();
	  reactList = new ArrayList<String>();
	//  if (level<13)
	//	  isExpanded = false;
	
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
	  x=x_;
	  y=y_;
	  radius = PApplet.pow(numReactions,0.65f)*5;
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
	  
	  	if (isExpanded)
	  		drawExpanded(parent,al_);
	  	else 
	  		drawUnexpanded(parent,al_);
	  	
		 parent.fill(100+level*20);
		 parent.arc(x, y, radius*2, radius*2,al_-PApplet.PI/2,al_-PApplet.PI/2+PApplet.PI*2);
		 if (PathwayView.bPathway==null && PApplet.dist(x, y, parent.mouseX, parent.mouseY)<radius){
			 PathwayView.bPathway = this;
		 }
  	}	
  public void drawWhenBrushing(PApplet parent){
	  parent.fill(200+(parent.frameCount%20));
	  parent.ellipse(x, y, radius*2, radius*2);
	  parent.fill(0);
	  parent.textAlign(PApplet.CENTER);
	  parent.textSize(12);
	  parent.text(displayName,x,y);
  }
		
  
  public void drawUnexpanded(PApplet parent, float al_){
	  ArrayList<String> a = getAllReaction();
	  float numReact = a.size();  // Number of points on the circles including reactions and pathways 
	  
	  // Draw reactions
	  for (int i=0; i<a.size();i++){
		  String nodeName = a.get(i);
		  Node node = getNodeByName(nodeName);
		  float al = al_+PApplet.PI*0.55f -(i+1f)/(numReact+1f)*PApplet.PI*1.1f;  // Right
		  //System.out.println("node="+node);
		  if (node==null) return;
		  float xR2 = x + (radius+node.size/2)*PApplet.cos(al);
		  float yR2 = y + (radius+node.size/2)*PApplet.sin(al);
		  node.iAlpha.target(al);
		  node.iX.target(xR2);
		  node.iY.target(yR2);
	  }
  }
  public ArrayList<String> getAllReaction(){
	  ArrayList<String> a =  new ArrayList<String>();
	  for (int i=0; i<reactList.size();i++){
		  a.add(reactList.get(i));
	  }
	  for (int p=0;p<subPathwayList.size();p++){
		  ArrayList<String> b = subPathwayList.get(p).getAllReaction();
		  for (int i=0;i<b.size();i++){
			  a.add(b.get(i));
		  }
	  }
	  return a;
  }	
  
  public void drawExpanded(PApplet parent, float al_){
		  float numSect = reactList.size();  // Number of points on the circles including reactions and pathways 
		  for (int i=0; i<subPathwayList.size();i++){
			  Pathway2 pathway = subPathwayList.get(i);
			   numSect += pathway.numReactions/2;
		  }
		  // Draw reactions
		  int countReactionLeft = 0;
		  int countReactionRight = 0;
		  float leftAl = al_-PApplet.PI*0.55f;
		  float rightAl = al_+PApplet.PI*0.55f;
		  for (int i=0; i<reactList.size();i++){
			  String nodeName = reactList.get(i);
			  Node node = getNodeByName(nodeName);
			  float al=0;
			  if (i%2==0){
				  al = al_+PApplet.PI*0.55f -(countReactionRight+1f)/(numSect+1f)*PApplet.PI*1.1f;  // Right
				  rightAl = al;
				  countReactionRight++;
			  }
			  else{
			  	  al = al_-PApplet.PI*0.55f +(countReactionLeft+1f)/(numSect+1f)*PApplet.PI*1.1f;
			  	  leftAl = al;
				  countReactionLeft++;
			  }
			  //System.out.println("node="+node);
			  if (node==null) return;
			  float xR2 = x + (radius+node.size/2)*PApplet.cos(al);
			  float yR2 = y + (radius+node.size/2)*PApplet.sin(al);
			  node.iAlpha.target(al);
			  node.iX.target(xR2);
			  node.iY.target(yR2);
		  }
		  
		  // Draw subpathway
  		  float total = 0;
		  float dif =rightAl-leftAl;
		  for (int i=0; i<subPathwayList.size();i++){
			  Pathway2 pathway = subPathwayList.get(i);
			  total+=PApplet.sqrt(pathway.numReactions);
		  }
		  float sum = 0;
		  for (int i=0; i<subPathwayList.size();i++){
			  Pathway2 pathway = subPathwayList.get(i);
			  
			  float percent = (sum+PApplet.sqrt(pathway.numReactions)/2)/total;
			  float al = leftAl +percent*dif;
			  float xR2 = x + (radius+pathway.radius/3)*PApplet.cos(al);
			  float yR2 = y + (radius+pathway.radius/3)*PApplet.sin(al);
			  pathway.draw(parent, xR2, yR2,al);
			  sum+=PApplet.sqrt(pathway.numReactions);
			  //current+=pathway.numReactions/2;
		  }
		 
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