package main;

import java.awt.Color;
import processing.core.PApplet;

public class CheckBox{
	public boolean s;
	public boolean b;
	public PApplet parent;
	public int x = 0;
	public int y = 0;
	public String text = "";
	
	int count =0;
	  
	public CheckBox(PApplet parent_, String text_){
		parent = parent_;
		text = text_;
	}
	
		
	public void draw(int  x_, int y_){
		x = x_;
		y = y_;
		
		checkBrushing();
		parent.textAlign(PApplet.LEFT);
		parent.textSize(12);
		parent.stroke(0);
		if (b)
			parent.fill(180);
		else
			parent.noFill();
		parent.rect(x, y, 15, 15);
		parent.fill(0);
		if (s){
			parent.noStroke();
			parent.fill(0);
			parent.ellipse(x+8, y+8, 12, 12);
		}
		else if (b)
			parent.fill(100);
		if (text.contains("small molecules")){
			parent.text(text.replace("small molecules", ""),x+20,y+13);
			parent.fill(main.PopupReaction.smallMoleculeColor.getRed(),main.PopupReaction.smallMoleculeColor.getGreen(),main.PopupReaction.smallMoleculeColor.getBlue());
			parent.text("small molecules",x+95,y+13);
		}
			
		else	
			parent.text(text,x+20,y+13);
		
		count++;
	    if (count==10000)
	    	count=200;
	}
	
	public void checkBrushing() {
		int mX = parent.mouseX;
		int mY = parent.mouseY;
		if (x-10<mX && mX < x+110 && y<mY && mY<y+20){
			b=true;
		}
		else
			b = false;
	}
	
	public void mouseClicked() {
		if (b){
			s = !s;
		}
	}
	
}