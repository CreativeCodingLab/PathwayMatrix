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
   
  void addColor(Color c){
  }
   
  Color getGradient(float value){
   return Color.RED;
  }
}