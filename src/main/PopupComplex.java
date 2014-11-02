package main;
import java.awt.Color;

import org.biopax.paxtools.model.level3.Complex;

import processing.core.PApplet;

public class PopupComplex{
	public int b = -1;
	public PApplet parent;
	public float x = 800;
	public int y = 0;
	public int w1 = 100;
	public int w = 400;
	public int h = 28;
	public int itemH = 20;
	public Color cGray  = new Color(240,240,240);
	public static int s=-1;
	public static int orderByRelation = -1;
	
	public PopupComplex(PApplet parent_){
		parent = parent_;
	}
	
	public void draw(float x_){
		x = x_;
		checkBrushing();
		parent.textSize(13);
		if (b>=0){
			parent.fill(100);
			parent.stroke(0);
			h=main.MainMatrix.complexSet.size()*itemH+20;
			parent.rect(x, y-2, w,h);
			// Max number of relations
			float max =-1;
			for (Complex current : main.MainMatrix.complexSet){
				float sqrt = PApplet.sqrt(current.getComponent().size());
				if (sqrt>max)
					max = sqrt;
			}
			int i=0;
			parent.textSize(12);
			for (Complex current : main.MainMatrix.complexSet){
				//int index = edu.uic.ncdm.venn.Venn_Overview.globalToLocal(i);
				if (i==s){
					parent.noStroke();
					parent.fill(100);
					parent.rect(x+10,y+itemH*(i)+5,w-25,itemH+1);
				
					parent.fill(255);
				}
				else if (i==b){
					parent.fill(255,255,0);
				}
				else{
					parent.fill(0);
				}
				parent.textAlign(PApplet.LEFT);
				parent.text(current.getDisplayName(),x+50,y+itemH*(i+1));
				float r = PApplet.map(PApplet.sqrt(current.getComponent().size()), 0, max, 0, 20);
				
				parent.noStroke();
				parent.fill(100,200,0);
				parent.ellipse(x+30,y+itemH*(i)+15, r, r);
			
				// Order By drawing
				if (i==orderByRelation){
					parent.fill(255);
					parent.rect(x+330,y+itemH*(i)+14, 9, 8);
					parent.rect(x+340,y+itemH*(i)+6, 9, 16);
					parent.rect(x+350,y+itemH*(i)-2, 9, 24);
				}
				i++;
			}	
		}
		else{
			parent.fill(125,125,125);
			parent.rect(x,y,w1,25);
			parent.fill(0);
			parent.textAlign(PApplet.CENTER);
			parent.text("Complex",x+w1/2,y+18);
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
				if (x<=mX && mX<=x+w && y+itemH*i<=mY && mY<=y+itemH*(i+1)+6){
					b =i;
					return;
				}	
			}
		}
		b =-1;
	}
	
}