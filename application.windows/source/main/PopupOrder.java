package main;
import java.awt.Color;
import java.text.DecimalFormat;

import processing.core.PApplet;
import processing.core.PFont;

public class PopupOrder{
	public int b = -1;
	public PApplet parent;
	public float x = 800;
	public int y = 0;
	public int w1 = 120;
	public int w = 300;
	public int h;
	public int itemH = 20;
	public Color cGray  = new Color(240,240,240);
	public static String[] items={"Random","Reading order", "Name", "Similarity"}; 
	public static int s=1;
	public Slider slider;

	public PopupOrder(PApplet parent_){
		parent = parent_;
		slider =  new Slider(parent_,5);
	}
	
	public void draw(float x_){
		x = x_;
		if (main.MainMatrix.popupRelation.b<0)
			checkBrushing();
		if (b>=0){
			parent.fill(100);
			parent.stroke(0);
			h=items.length*itemH+40;
			parent.rect(x, y-2, w,h);
			// Max number of relations
			float max =-1;
			for (int j=0;j<main.MainMatrix.pairs.length;j++){
				float sqrt = PApplet.sqrt(main.MainMatrix.pairs[j].size());
				if (sqrt>max)
					max = sqrt;
			}
			for (int i=0;i<items.length;i++){
				if (i==s){
					parent.noStroke();
					parent.fill(0);
					parent.rect(x+10,y+itemH*(i)+5,w-25,itemH+1);
					parent.fill(255,255,0);
				}
				else if (i==b){
					parent.fill(255);
				}
				else{
					parent.fill(0);
				}
				parent.textAlign(PApplet.LEFT);
				parent.text(items[i],x+30,y+itemH*(i+1));
			}	
			
			if (items[s].equals("Similarity")) 
				slider.draw(x+130, y+itemH*items.length-14);
			
		}
		else{
			parent.fill(125,125,125);
			parent.rect(x,y,w1,25);
			parent.fill(0);
			parent.textAlign(PApplet.CENTER);
			parent.text("Order by",x+w1/2,y+18);
		}	
	}
	
	 public void mouseClicked() {
		if (b<items.length){
			s = b;
			if (items[s].equals("Random")) { 
				main.MainMatrix.stateAnimation=0;
				main.PopupGroup.s=0;
				Gene.orderByRandom(parent);
			}	
			else if (items[s].equals("Reading order"))  {
				main.MainMatrix.stateAnimation=0;
				main.PopupGroup.s=0;
				Gene.orderByReadingOrder();
			}	
			else if (items[s].equals("Name"))  {
				main.MainMatrix.stateAnimation=0;
				main.PopupGroup.s=0;
				Gene.orderByName();
			}	
			else if (items[s].equals("Similarity"))  {
				main.MainMatrix.stateAnimation=0;
				main.PopupGroup.s=0;
				Gene.orderBySimilarity();
			}	
		}
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
			if (x<=mX && mX<=x+w && y<=mY && mY<=y+h)
				return;
		}
		b =-1;
	}
	
	
	
}