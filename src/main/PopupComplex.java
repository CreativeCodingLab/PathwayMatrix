package main;
import java.awt.Color;

import org.biopax.paxtools.model.level3.Complex;

import processing.core.PApplet;

public class PopupComplex{
	public static int b = -1;
	public PApplet parent;
	public float x = 800;
	public static int y = 0;
	public static int y2 = 0;
	public int w1 = 100;
	public int w = 500;
	public int h = 28;
	public int itemH = 18;
	public Color cGray  = new Color(240,240,240);
	public static int s=-1;
	public static int orderByRelation = -1;
	public static float maxSize = 0;
	public static int[] sizes;
	
	public PopupComplex(PApplet parent_){
		parent = parent_;
	}
	
	public void setItems(){
		int i=0;
		y2=0;
		maxSize =0;
		sizes = new int[main.MainMatrix.complexSet.size()];
		for (Complex current : main.MainMatrix.complexSet){
			int size = main.MainMatrix.getAllGenesInComplexById(i).size();
			sizes[i] = size;
			if (size>maxSize)
				maxSize = size;
			i++;
		}
		
	}
		
	public void draw(float x_){
		x = x_;
		checkBrushing();
		parent.textSize(13);
		parent.fill(125,125,125);
		parent.rect(x,y,w1,25);
		parent.fill(0);
		parent.textAlign(PApplet.CENTER);
		parent.text("Complex",x+w1/2,y+18);
	
		
		if (b>=0){
			parent.fill(100);
			parent.stroke(0);
			h=main.MainMatrix.complexSet.size()*itemH+20;
			parent.rect(x, y2-2, w,h);
			
			int i=0;
			parent.textSize(12);
			for (Complex current : main.MainMatrix.complexSet){
				//int index = edu.uic.ncdm.venn.Venn_Overview.globalToLocal(i);
				if (i==s){
					parent.noStroke();
					parent.fill(0);
					parent.rect(x+10,y2+itemH*(i)+4,w-25,itemH+1);
				
					parent.fill(255,0,0);
				}
				else if (i==b){
					parent.fill(255);
				}
				else{
					parent.fill(0);
				}
				parent.textAlign(PApplet.LEFT);
				parent.text(current.getDisplayName(),x+50,y2+itemH*(i+1));
				float r = PApplet.map(PApplet.sqrt(sizes[i]), 0, PApplet.sqrt(maxSize), 0, 18);
				
				parent.noStroke();
				if (i==s){
					parent.fill(255,0,0);
				}
				else if (i==b){
					parent.fill(255);
				}
				else{
					parent.fill(0);
				}
				parent.ellipse(x+30,y2+itemH*(i)+13, r, r);
			
				// Order By drawing
				if (i==orderByRelation){
					parent.fill(255);
					parent.rect(x+330,y2+itemH*(i)+14, 9, 8);
					parent.rect(x+340,y2+itemH*(i)+6, 9, 16);
					parent.rect(x+350,y2+itemH*(i)-2, 9, 24);
				}
				i++;
			}	
		}
			
	}
	
	 public void mouseClicked() {
		if (parent.key=='r' || parent.key=='R'){
			orderByRelation=b;
			Gene.orderByRelation(orderByRelation);
		}
		s = b;
	 	
	}
	 
	public void checkBrushing() {
		int mX = parent.mouseX;
		int mY = parent.mouseY;
		if (b==-1){
			if (x<mX && mX<x+w1 && y<=mY && mY<=itemH+5){
				b =100;
				return;
			}	
		}
		else{
			for (int i=0; i<main.MainMatrix.complexSet.size(); i++){
				if (x<=mX && mX<=x+w && y2+itemH*i<=mY && mY<=y2+itemH*(i+1)+6){
					b =i;
					return;
				}	
			}
		}
		b =-1;
	}
	
}