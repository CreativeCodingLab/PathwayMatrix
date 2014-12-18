package main;

import java.awt.Color;
import java.text.DecimalFormat;

import processing.core.PApplet;

public class Slider{
	int count =0;
	public int pair =-1;
	public PApplet parent;
	public float x=0,y=0;
	public int w; 
	public int u =-1;
	public float val =-1;
	
	
	public int bSlider = -1;
	public int sSlider = -1;
	public int ggg =10;
	
	
	public Slider(PApplet parent_, float initial){
		parent = parent_;
		w= 10*ggg;
		update();
		u = (int) (initial*ggg);
		update();
	}
		
	public void update(){
		val = (float) u/(ggg);
	}
		
	public void draw(float x_, float y_){
		x = x_;
		y = y_;
		checkBrushingSlider();
		
		float xx2 = x+u;
		DecimalFormat df = new DecimalFormat("#.##");
		parent.stroke(Color.GRAY.getRGB());
		parent.strokeWeight(1.0f);
		for (int j=0; j<=1; j++ ){
			parent.line(x+j*10*ggg, y+9, x+j*10*ggg, y+16);
			if (j==1) break;
			for (int k=1; k<10; k++ ){
				parent.line(x+j*10*ggg+k*ggg, y+9, x+j*10*ggg+k*ggg, y+11);
			}
		}
		
		//Upper range
		Color c;
		if (sSlider==1){
			c= Color.YELLOW;
		}	
		else if (bSlider==1){
			c= Color.WHITE;
		}	
		else{
			c = new Color(255,255,0);
		}
		parent.textSize(13);
		parent.noStroke();
		parent.fill(c.getRGB());
		parent.triangle(xx2-5, y+20, xx2+5, y+20, xx2, y+10);
		
		parent.textAlign(PApplet.CENTER);
		parent.textSize(12);
		parent.text(df.format(val), xx2,y+8);
		parent.textAlign(PApplet.LEFT);
		
		count++;
	    if (count==10000)
	    	count=200;
	    parent.textSize(14);
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
	
	public void checkSelectedSlider1() {
		sSlider = bSlider;
	}
	public void checkSelectedSlider2() {
		sSlider = -1;
		Gene.orderBySimilarity();
	}	
	public int checkSelectedSlider3() {
		if (sSlider==1){
			u += (parent.mouseX - parent.pmouseX);
			if (u<0) u=0;
			if (u>w)  u=w;
			update();
		}
		return sSlider;
	}
		
}