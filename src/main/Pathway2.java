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
  public ArrayList<Integer> nodeIdList;
  public int f = -1; 
  public int level = -1; 
  public String displayName = "?";
  
  // for drawing
  public float radius = 0;
  public int num = 0;
   
  // Constructor
  Pathway2(int f_, String dName, int level_){
	  f=f_;
	  level = level_;
	  displayName = dName;
	  
	  subPathwayList =  new ArrayList<Pathway2>();
	  reactList = new ArrayList<String>();
	  nodeIdList = new ArrayList<Integer>();
  }
  
  public void draw(PApplet parent, float x_, float y_, float al_){
	  num = subPathwayList.size() + nodeIdList.size();
	  radius = num*10;
	  parent.noStroke();
	  if (level==0){
		  parent.fill(0);
		  parent.textAlign(PApplet.CENTER);
		  parent.text(displayName,x_, y_);
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
		  parent.fill(0,50);
		
	  parent.arc(x_, y_, radius*2, radius*2,al_-PApplet.PI/2,al_-PApplet.PI/2+PApplet.PI);
	  

		  
	 
	  for (int i=0; i<nodeIdList.size();i++){
		  int nodeId = nodeIdList.get(i);
		  Node node = PathwayView.g.nodes.get(nodeId);
		  float al = al_-PApplet.PI/2 +(i+1f)/(num+1f)*PApplet.PI;
		  float xR2 = x_ + radius*PApplet.cos(al);
		  float yR2 = y_ + radius*PApplet.sin(al);
		  node.iAlpha.target(al);
		  node.iX.target(xR2);
		  node.iY.target(yR2);
	  }
	  for (int i=0; i<subPathwayList.size();i++){
		  Pathway2 pathway = subPathwayList.get(i);
		  float al = al_-PApplet.PI/2 +(i+nodeIdList.size()+1f)/(num+1f)*PApplet.PI;
		  float xR2 = x_ + (radius)*PApplet.cos(al);
		  float yR2 = y_ + (radius)*PApplet.sin(al);
		  pathway.draw(parent, xR2, yR2,al);
	  }
	  
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