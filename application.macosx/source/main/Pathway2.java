package main;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.biopax.paxtools.model.level3.BiochemicalReaction;

import processing.core.PApplet;

import GraphLayout.Graph;
import GraphLayout.Node;


public class Pathway2{
  public Pathway2 parentPathway;
  public ArrayList<Pathway2> subPathwayList;
  public ArrayList<BiochemicalReaction> reactList;
  public ArrayList<Integer> nodeIdList;
  public int f = -1; 
  public int level = -1; 
  public String displayName = "?";
  
  // for drawing
  public float radius = 0;
  public float radiusCenter = 0;
  public int numReactions = 0;
  public boolean isExpanded = true;
  
  public float xPathway = 100;
  public float yPathway = 100;
  public float al = 0;
  private PApplet parent = null;
  public static float beginDarknessOfPathways = 150;
  
  // Constructor
  Pathway2(PApplet parent_, Pathway2 parentPathway_, int f_, String dName, int level_){
	  parent = parent_;
	  parentPathway = parentPathway_;
	  f=f_;
	  level = level_;
	  displayName = dName;
	  
	  subPathwayList =  new ArrayList<Pathway2>();
	  reactList = new ArrayList<BiochemicalReaction>();
	  nodeIdList = new ArrayList<Integer>();
  }
  
  public int computeSize(){
	  numReactions = reactList.size();
	  for (int i=0; i<subPathwayList.size();i++){
		  numReactions+=subPathwayList.get(i).computeSize();
	  }
	   return numReactions;
  }
		
  public void draw(float x_, float y_, float al_){
	  xPathway=x_;
	  yPathway=y_;
	  al = al_;
	  radius = PApplet.pow(numReactions,0.65f)*PathwayView.scale*5;
	  radiusCenter = radius/4;
	  parent.noStroke();
	  	if (isExpanded)
	  		drawExpanded();
	  	else 
	  		drawUnexpanded();
	  	
	  	
	  	
		if (PathwayView.bPathway==null && PApplet.dist(xPathway, yPathway, parent.mouseX, parent.mouseY)<radius){
			PathwayView.bPathway = this;
		}
		else{
			float v = beginDarknessOfPathways+(level+1)*14;  
		  	if (v>240)
		  		v=240;
		  
			parent.fill(v);
		  	parent.noStroke();
			parent.arc(xPathway, yPathway, radius*2, radius*2,al-PApplet.PI/2,al-PApplet.PI/2+PApplet.PI*2);
			drawCenter();
		}
	}	
  public void drawWhenBrushing(){
	  parent.fill(255,220,255,230);
	  parent.noStroke();
	  parent.ellipse(xPathway, yPathway, radius*2, radius*2);
	  parent.fill(0);
	  parent.textAlign(PApplet.CENTER);
	  parent.textSize(12);
	  parent.text(displayName,xPathway,yPathway-10);
	  
	  drawCenter();
  }
  
  //Draw center
	public void drawCenter(){
		Color color2 = PathwayView.getColor(f).darker().darker();
		float sat = 200+level*10;
	  	if (sat>255)
	  		sat=255;
	  	parent.fill(color2.getRed(),color2.getGreen(),color2.getBlue(),sat);
	  	float xCenter = xPathway-radiusCenter/2*PApplet.cos(al);
	  	float yCenter = yPathway-radiusCenter/2*PApplet.sin(al);
	  	parent.ellipse(xCenter, yCenter, radiusCenter, radiusCenter);
		
	  	parent.strokeWeight(radiusCenter/20);
		parent.stroke(150);
		parent.line(xCenter-(radiusCenter*0.8f)/2,yCenter, xCenter+(radiusCenter*0.8f)/2,yCenter);
		if (!isExpanded)
			parent.line(xCenter,yCenter-(radiusCenter*0.8f)/2, xCenter,yCenter+(radiusCenter*0.8f)/2);
		
  }
		
		
  
  public void drawUnexpanded(){
	  ArrayList<Integer> a = getAllNodeId();
	  float numNode = a.size();  // Number of points on the circles including reactions and pathways 
	  
	  // Draw reactions
	  for (int i=0; i<a.size();i++){
		  Node node = Graph.nodes.get(a.get(i));
		  float al2 = al+PApplet.PI*0.55f -(i+1f)/(numNode+1f)*PApplet.PI*1.1f;  // Right
		  //System.out.println("node="+node);
		  if (node==null) return;
		  float xR2 = xPathway + (radius+node.size/2)*PApplet.cos(al2);
		  float yR2 = yPathway + (radius+node.size/2)*PApplet.sin(al2);
		  setNodePosistion(node, xR2,yR2,al2);
	  }
  }
  
  // Does not check redundant reactions
  public ArrayList<BiochemicalReaction> getAllReaction(){
	  ArrayList<BiochemicalReaction> a =  new ArrayList<BiochemicalReaction>();
	  for (int i=0; i<reactList.size();i++){
		  a.add(reactList.get(i));
	  }
	  for (int p=0;p<subPathwayList.size();p++){
		  ArrayList<BiochemicalReaction> b = subPathwayList.get(p).getAllReaction();
		  for (int i=0;i<b.size();i++){
			  a.add(b.get(i));
		  }
	  }
	  return a;
  }	
  
  // Does not check redundant reactions
  public ArrayList<Integer> getAllNodeId(){
	  ArrayList<Integer> a =  new ArrayList<Integer>();
	  for (int i=0; i<nodeIdList.size();i++){
		  a.add(nodeIdList.get(i));
	  }
	  for (int p=0;p<subPathwayList.size();p++){
		  ArrayList<Integer> b = subPathwayList.get(p).getAllNodeId();
		  for (int i=0;i<b.size();i++){
			  a.add(b.get(i));
		  }
	  }
	  return a;
  }	
  
  public void drawExpanded(){
		  float numSect = nodeIdList.size();  // Number of points on the circles including reactions and pathways 
		  for (int i=0; i<subPathwayList.size();i++){
			  Pathway2 pathway = subPathwayList.get(i);
			   numSect += pathway.numReactions/2;
		  }
		  // Draw reactions
		  int countReactionLeft = 0;
		  int countReactionRight = 0;
		  float leftAl = al-PApplet.PI*0.55f;
		  float rightAl = al+PApplet.PI*0.55f;
		  for (int i=0; i<nodeIdList.size();i++){
			  Node node = Graph.nodes.get(nodeIdList.get(i));
			  float al2=0;
			  if (i%2==0){
				  al2 = al+PApplet.PI*0.55f -(countReactionRight+1f)/(numSect+1f)*PApplet.PI*1.1f;  // Right
				  rightAl = al2;
				  countReactionRight++;
			  }
			  else{
			  	  al2 = al-PApplet.PI*0.55f +(countReactionLeft+1f)/(numSect+1f)*PApplet.PI*1.1f;
			  	  leftAl = al2;
				  countReactionLeft++;
			  }
			  //System.out.println("node="+node);
			  if (node==null) return;
			  float xR2 = xPathway + (radius+node.size/2)*PApplet.cos(al2);
			  float yR2 = yPathway + (radius+node.size/2)*PApplet.sin(al2);
			  setNodePosistion(node, xR2,yR2,al2);
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
			  float xR2 = xPathway + (radius+pathway.radiusCenter)*PApplet.cos(al);
			  float yR2 = yPathway + (radius+pathway.radiusCenter)*PApplet.sin(al);
			  pathway.draw(xR2, yR2,al);
			  sum+=PApplet.sqrt(pathway.numReactions);
			  //current+=pathway.numReactions/2;
		  }
  }
  
  public static void setNodePosistion(Node node, float xR2, float yR2, float al2){
	  node.iAlpha.target(al2);
	  node.iX.target(xR2);
	  node.iY.target(yR2);
	  if (PathwayView.setIntegrator>0){
		  node.iAlpha.set(al2);
		  node.iX.set(xR2);
		  node.iY.set(yR2);
	  }
  }
  

	// Every reaction associated to a node id in the nodes of Graph class
	public int setNodeId(int nodeId) {
		int newId = nodeId;
		for (int i=0;i<reactList.size();i++){
			nodeIdList.add(newId);
			newId++;
		}
		for (int i=0;i<subPathwayList.size();i++){
			Pathway2 subpathway = subPathwayList.get(i);
			newId = subpathway.setNodeId(newId);
		}
		return newId;
	}
	
	
	// Every reaction associated to this pathway should contain this pathway as parent pathway
	public void setNodePathway() {
		for (int i=0;i<nodeIdList.size();i++){
			Graph.nodes.get(nodeIdList.get(i)).parentPathway= this;
		}
		for (int i=0;i<subPathwayList.size();i++){
			Pathway2 subpathway = subPathwayList.get(i);
			subpathway.setNodePathway();
		}
	}
	
			
  public boolean isContainReaction(String rName){
	  for (int r=0;r<reactList.size();r++){
		  String name = reactList.get(r).getDisplayName();
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