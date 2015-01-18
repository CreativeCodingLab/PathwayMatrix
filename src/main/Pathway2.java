package main;

import java.awt.Color;
import java.util.ArrayList;


public class Pathway2{
  public ArrayList<Pathway2> subPathwayList;
  public ArrayList<String> reactList;
  public int f = -1; 
  public int level = -1; 
  public String displayName = "?";
  
  // Constructor
  Pathway2(int f_, String dName, int level_){
	  f=f_;
	  level = level_;
	  displayName = dName;
	  
	  subPathwayList =  new ArrayList<Pathway2>();
	  reactList = new ArrayList<String>();
	  
  }
   
  public boolean isContainReaction(String rName){
	  for (int r=0;r<reactList.size();r++){
		  String name = reactList.get(r);
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
   
  
  
  Color getGradient(float value){
   return Color.RED;
  }
}