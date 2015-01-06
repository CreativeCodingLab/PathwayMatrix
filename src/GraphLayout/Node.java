package GraphLayout;

import java.awt.Color;
import java.util.ArrayList;

import main.Integrator;
import processing.core.PApplet;
import processing.core.PImage;

//Copyright 2005 Sean McCullough
//banksean at yahoo

public class Node {
	Vector3D f = new Vector3D(0, 0, 0);
	float mass = 1;
	float fontSize = 1;
	public float wordWidth = 1;
	public String name = "";
	public PApplet parent;
	public Color color = null;

	Vector3D position;
	float h = 10;
	float w = 10;
	Graph g;
	public boolean isConnected = false;
	public int wordId = -99;
	public static int bWord = -99;
	public int degree = -1;
	public Integrator iAlpha = new Integrator(0,0.2f,0.5f);
	
	public Node(Vector3D v, PApplet p) {
		position = v;
		parent = p;
	}

	public void setGraph(Graph h) {
		g = h;
	}

	public boolean containsWord(float x, float y) {
		if (getX()-wordWidth/2<=x && x<=getX()+wordWidth/2 &&
				getY()-fontSize/2<=y && y<=getY()+fontSize/2){
			return true;
		}
		else
			return false;
	}

	public Node(Vector3D v) {
		position = v;
	}

	public Vector3D getPosition() {
		return position;
	}

	public void setPosition(Vector3D v) {
		position = v;
		if (position.getX()-wordWidth/2<15)
			position.setX(15+wordWidth/2);
		else if (position.getX()+wordWidth/2>parent.width-15)
			position.setX(parent.width-15-wordWidth/2);
		if (position.getY()<15)
			position.setY(15);
		else if (position.getY()>parent.height-18)
			position.setY(parent.height-18);
	}

	public float getX() {
		return position.getX();
	}

	public float getY() {
		return position.getY();
	}

	public float getMass() {
		return mass;
	}

	public void setMass(float m) {
		mass = m/10;
		fontSize = m;
		h = m;
		w = m;
	}

	public void setForce(Vector3D v) {
		f = v;
	}

	public Vector3D getForce() {
		return f;
	}

	public void applyForce(Vector3D v) {
		f = f.add(v);
	}

	
	public void checkBrushing() {
		if (getX()<=parent.mouseX && parent.mouseX<=getX()+wordWidth &&
				getY()-fontSize<=parent.mouseY && parent.mouseY<=getY()+fontSize/2){
			bWord=wordId;
		}	
	}
	
	public void draw() {
		iAlpha.target(0);
		iAlpha.update();
		// Draw node names
		float xx = getX();
		float yy = getY() + h / 2 + 10;
		yy = getY()+fontSize/3;
		
		
		if (g.getHoverNode() == this) {
			parent.textAlign(PApplet.CENTER);
			parent.textSize(fontSize);
			parent.fill(0);
			parent.text(name, xx+1, yy+1);  // background
			
			
			int sat =( parent.frameCount*20%200);
			parent.noStroke();
			parent.fill(color.getRed(), color.getGreen(), color.getBlue(),55+sat);
			parent.text(name, xx, yy);
		} 
		else if (g.getHoverNode() != null && g.getHoverNode()!=this && !isConnected) {
		}
		else {
			parent.textAlign(PApplet.CENTER);
			parent.textSize(fontSize);
			
			parent.translate(xx,yy);
			parent.rotate(iAlpha.value);
			parent.fill(0);
			parent.text(name, 1, 1);  // background
			parent.fill(color.getRGB());
			parent.text(name, 0, 0);
			parent.rotate(-iAlpha.value);
			parent.translate(-xx,-yy);
			
		}
		
		
		
		
	}
}
