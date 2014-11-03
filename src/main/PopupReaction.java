package main;
import java.awt.Color;

import org.biopax.paxtools.model.level3.BiochemicalReaction;

import processing.core.PApplet;

public class PopupReaction{
	public static boolean sAll = false;
	public static int b = -1000;
	public PApplet parent;
	public float x = 800;
	public static int y = 0;
	public static int y2 = 0;
	public int w1 = 100;
	public int w = 500;
	public int h = 28;
	public int itemH = 18;
	public Color cGray  = new Color(240,240,240);
	public static int s=-100;
	public static int orderByRelation = -100;
	public static float maxSize = 0;
	public static int[] sizeL;
	public static int[] sizeR;
	
	public PopupReaction(PApplet parent_){
		parent = parent_;
	}
	
	public void setItems(){
		int i=0;
		y2=20;
		maxSize =0;
		sizeL = new int[main.MainMatrix.reactionSet.size()];
		sizeR = new int[main.MainMatrix.reactionSet.size()];
		s=-400;
		for (BiochemicalReaction current : main.MainMatrix.reactionSet){
			sizeL[i] = current.getLeft().size();
			sizeR[i] = current.getRight().size();
			if (sizeL[i]>maxSize)
				maxSize = sizeL[i];
			if (sizeR[i]>maxSize)
				maxSize = sizeR[i];
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
		parent.text("Reaction",x+w1/2,y+18);
	
		
		if (b>=-1){
			parent.fill(100);
			parent.stroke(0);
			h=main.MainMatrix.reactionSet.size()*itemH+20;
			parent.rect(x, y2-itemH-4, w,h+itemH);
			
			int i=0;
			parent.textSize(13);
			
			// Draw another button
			if (sAll){
				parent.noStroke();
				parent.fill(0);
				parent.rect(x+10,y2-itemH+3,w-25,itemH);
				parent.fill(180);
			}
			else if (b==-1){
				parent.fill(255);
			}
			else{
				parent.fill(0);
			}
			parent.textAlign(PApplet.LEFT);
			parent.text("All Biochemical Reaction",x+50,y2-1);
			
			parent.textSize(12);
			for (BiochemicalReaction current : main.MainMatrix.reactionSet){
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
				float rL = PApplet.map(PApplet.sqrt(sizeL[i]), 0, PApplet.sqrt(maxSize), 0, 16);
				float rR = PApplet.map(PApplet.sqrt(sizeR[i]), 0, PApplet.sqrt(maxSize), 0, 16);
				
				parent.noStroke();
				parent.fill(200,0,0);
				parent.ellipse(x+22,y2+itemH*(i)+13, rL, rL);
				parent.fill(0,200,0);
				parent.ellipse(x+36,y2+itemH*(i)+13, rR, rR);
				
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
		
		if (b==-1){
			sAll = !sAll;
		}
		else{
			if (b!=s)
				s = b;
			else
				s =-200;
		}
		
	}
	 
	public void checkBrushing() {
		if (main.MainMatrix.reactionSet==null) return;
		int mX = parent.mouseX;
		int mY = parent.mouseY;
		if (b==-100){
			if (x<mX && mX<x+w1 && y<=mY && mY<=itemH+5){
				b =100;
				return;
			}	
		}
		else{
			for (int i=0; i<main.MainMatrix.reactionSet.size(); i++){
				if (x<=mX && mX<=x+w && y2-itemH-5<=mY && mY<=y2+4){
					b =-1;
					return;
				}
				if (x<=mX && mX<=x+w && y2+itemH*i<=mY && mY<=y2+itemH*(i+1)+6){
					b =i;
					return;
				}	
			}
		}
		b =-100;
	}
	
}