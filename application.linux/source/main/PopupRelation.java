package main;
import java.awt.Color;
import java.text.DecimalFormat;

import processing.core.PApplet;
import processing.core.PFont;

public class PopupRelation{
	public int b = -1;
	public PApplet parent;
	public float x = 800;
	public int y = 0;
	public int w1 = 100;
	public int w = 400;
	public int h = 28;
	public int itemH = 20;
	public Color cGray  = new Color(240,240,240);
	public static String[] items={}; 
	public static boolean[] s;
	public static int orderByRelation = -1;
	
	public PopupRelation(PApplet parent_){
		parent = parent_;
		items = new String [PathwayViewer_2_9.minerList.size()];
		for (int i = 0; i < PathwayViewer_2_9.minerList.size(); i++) {
			items[i] = PathwayViewer_2_9.minerList.get(i).toString();
		}	
		s = new boolean[PathwayViewer_2_9.minerList.size()];
	}
	
	public void draw(float x_){
		x = x_;
		checkBrushing();
		parent.textSize(13);
		if (b>=0){
			parent.fill(100);
			parent.stroke(0);
			h=items.length*itemH+20;
			parent.rect(x, y-2, w,h);
			// Max number of relations
			float max =-1;
			for (int j=0;j<main.PathwayViewer_2_9.pairs.length;j++){
				float sqrt = PApplet.sqrt(main.PathwayViewer_2_9.pairs[j].size());
				if (sqrt>max)
					max = sqrt;
			}
			for (int i=0;i<items.length;i++){
				if (s[i]){
					parent.noStroke();
					parent.fill(0);
					parent.rect(x+10,y+itemH*(i)+5,w-25,itemH+1);
					parent.fill(main.PathwayViewer_2_9.colorRelations[i]);
				}
				else if (i==b){
					parent.fill(main.PathwayViewer_2_9.colorRelations[i]);
				}
				else{
					parent.fill(0);
				}
				parent.textAlign(PApplet.LEFT);
				parent.text(items[i],x+50,y+itemH*(i+1));
				float r = PApplet.map(PApplet.sqrt(main.PathwayViewer_2_9.pairs[i].size()), 0, max, 0, 25);
				
				parent.noStroke();
				parent.fill(main.PathwayViewer_2_9.colorRelations[i]);
				parent.ellipse(x+30,y+itemH*(i)+15, r, r);
			
				// Order By drawing
				if (i==orderByRelation){
					parent.fill(255);
					parent.rect(x+330,y+itemH*(i)+14, 9, 8);
					parent.rect(x+340,y+itemH*(i)+6, 9, 16);
					parent.rect(x+350,y+itemH*(i)-2, 9, 24);
				}
			}	
		}
		else{
			parent.fill(150);
			parent.rect(x,y,w1,25);
			parent.fill(0);
			parent.textAlign(PApplet.CENTER);
			parent.text("Relations",x+w1/2,y+18);
		}	
	}
	
	 public void mouseClicked() {
		if (parent.key=='r' || parent.key=='R'){
			orderByRelation=b;
			Gene.orderByRelation(orderByRelation);
		}
		else if (b<s.length)
				s[b] = !s[b];
	 	
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
			for (int i=0; i<items.length; i++){
				if (x<=mX && mX<=x+w && y+itemH*i<=mY && mY<=y+itemH*(i+1)+6){
					b =i;
					return;
				}	
			}
		}
		b =-1;
	}
	
}