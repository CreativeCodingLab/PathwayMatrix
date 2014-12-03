package main;

import processing.core.PApplet;

public class Button{
	public boolean b = false;
	public boolean s = false;
	public PApplet parent;
	public int y = 0;
	public int w = 98;
	public int h = 25;
	public float x = 0;
	public int w2 = 200;
	public int itemNum = 9;
	public String text = "";
	public Button(PApplet parent_, String str){
		parent = parent_;
		text = str;
	}
	
	
	public void draw(float x_){
		x = x_;
		checkBrushing();
		parent.textSize(12);
		parent.noStroke();
		parent.fill(150);
		if (b)
			parent.stroke(255,0,0);
		parent.rect(x, y, w, h);
		
		if (s){
			if (text.endsWith("Causality"))
				for (int i=0;i<w;i++){
					parent.stroke(255,255-i*2.55f,i*2.55f);
					parent.line(x+i, y, x+i, y+h-1);
				}
			else
				parent.fill(50);
			
		}
			
		
		parent.fill(0);
		parent.textAlign(PApplet.CENTER);
		parent.text(text,x+w/2,y+18);
		
		
	}
	
	public void mouseClicked() {
		s = !s;
		
	}
		
	 
	public void checkBrushing() {
		int mX = parent.mouseX;
		int mY = parent.mouseY;
		if (x<=mX && mX<=x+w && y<=mY && mY<=h){
			b =true;
			return;
		}
		b =false;
	}
	
}