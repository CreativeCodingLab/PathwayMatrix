package main;

import java.awt.Color;
import java.text.DecimalFormat;

import processing.core.PApplet;

public class SliderSimulation{
	public int pair =-1;
	public PApplet parent;
	public float x,y;
	public int w; 
	
	
	public int bSlider = -1;
	public int sSlider = -1;
	public float ggg =10;
	public float u = 10;
	public int value =0;
		
	
	public SliderSimulation(PApplet parent_){
		parent = parent_;
		w= 300;
		u=(int) (w/5);
	}
		
	
		
	public void draw(float x_, float y_, int maxLevel_){
		int maxLevel2 = maxLevel_+1;
		if (maxLevel2<=0) return;
		x = x_;
		y = y_;
		
		float reactionSize = w/maxLevel2;
		checkBrushingSlider();
		
		
		for (int k=0; k<maxLevel2; k++ ){
			float x1 = x+k*reactionSize;
			float x2 = x1+reactionSize/5;
			parent.stroke(PopupReaction.formComplexColor.getRGB());
			parent.line(x1, y, x2, y);
			
			x1=x2;
			x2+=reactionSize/5;
			parent.stroke(PopupReaction.complexRectionColor.getRGB());
			parent.line(x1, y, x2, y);
			
			x1=x2;
			parent.noStroke();
			parent.fill(0);
			parent.ellipse(x1, y, 8, 8);
			
			x2+=reactionSize/5;
			parent.stroke(PopupReaction.complexRectionColor.getRGB());
			parent.line(x1, y, x2, y);
			
			x1=x2;
			x2+=reactionSize/5;
			parent.stroke(PopupReaction.formComplexColor.getRGB());
			parent.line(x1, y, x2, y);
			
			if (k<maxLevel2-1){
				x1=x2;
				x2+=reactionSize/10;
				parent.stroke(200,160,0);
				parent.line(x1, y, x2, y);
				x1=x2;
				x2+=reactionSize/10;
				parent.stroke(255,0,255);
				parent.line(x1, y, x2, y);
			}
		}
		
		Color color = new Color(0,0,0);
		if (sSlider==1){
			color= new Color(200,100,0);
		}	
		else{
			color = new Color(0,100,255);
		}
		
		int lastIndex = PopupReaction.simulationRectList.size()-1;
		if (lastIndex<0) return;
		
		int currentLevel = PopupReaction.simulationRectListLevel.get(lastIndex);
		int currentRect = PopupReaction.simulationRectList.get(lastIndex);
		
		float sumStep = PopupReaction.iS1[currentRect].value+PopupReaction.iS2[currentRect].value+
						PopupReaction.iS3[currentRect].value+PopupReaction.iS4[currentRect].value;
		float xx2 = x+currentLevel*reactionSize +(sumStep/1000)*reactionSize/5;
		DecimalFormat df = new DecimalFormat("#.##");
		parent.stroke(200);
		parent.strokeWeight(1.0f);
		parent.line(x, y+9, x, y+16);
		parent.line(x+w, y+9, x+w, y+16);
		
		parent.textSize(14);
		parent.stroke(0,100);
		parent.fill(color.getRGB());
		parent.triangle(xx2-12, y-10, xx2-12, y+10, xx2, y);
		parent.textAlign(PApplet.RIGHT);
		
		// Decide the text on slider
		parent.textAlign(PApplet.CENTER);
		parent.textSize(13);
		value = (int) (u/w);
		//parent.text(df.format(value), xx2,y+8);
		parent.textAlign(PApplet.LEFT);
	}
	
	
	
	public void checkBrushingSlider() {
		float xx2 = x+u;
		int mX = parent.mouseX;
		int mY = parent.mouseY;
		
		if (xx2-20<mX && mX < xx2+20 && y<mY && mY<y+25){
			bSlider =1; 
			return;
		}
		bSlider =-1;
	}
	
	public void mousePressed() {
		sSlider = bSlider;
	}
	public void mouseRelease() {
		sSlider = -1;
	}	
	public int checkSelectedSlider() {
		if (sSlider==1){
			u += (parent.mouseX - parent.pmouseX);
			if (u<0) u=0;
			if (u>w)  u=w;
		}
		return sSlider;
	}
		
}