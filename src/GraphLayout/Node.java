package GraphLayout;

import java.awt.Color;
import java.util.ArrayList;

import main.Integrator;
import main.MultipleReactionView;
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
	Graph g;
	public boolean isConnected = false;
	public int wordId = -99;
	public static int bWord = -99;
	public int degree = -1;
	public Integrator iAlpha = new Integrator(0,0.1f,0.4f);
	public Integrator iX = new Integrator(0,0.1f,0.4f);
	public Integrator iY = new Integrator(0,0.1f,0.4f);
	
	public Node(Vector3D v, PApplet p) {
		position = v;
		parent = p;
	}

	public void setGraph(Graph h) {
		g = h;
	}

	public boolean containsWord(float x, float y) {
		if (PApplet.dist(iX.value, iY.value, parent.mouseX, parent.mouseY)<=fontSize/2+1)
			return true;
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
		if (position.getX()<10)
			position.setX(10);
		else if (position.getX()>MultipleReactionView.xRight-10)
			position.setX(MultipleReactionView.xRight-10);
		if (position.getY()<10)
			position.setY(10);
		else if (position.getY()>parent.height-10)
			position.setY(parent.height-10);
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
		if (MultipleReactionView.popupLayout.s==1){ //circular Layout
			float al = MultipleReactionView.computeAlpha(wordId);
			float xR = MultipleReactionView.xCircular + MultipleReactionView.rCircular*PApplet.sin(al);
			float yR = MultipleReactionView.yCircular + MultipleReactionView.rCircular*PApplet.cos(al);
			iAlpha.target(al);
			iX.target(xR);
			iY.target(yR);
		}
		else{
			iAlpha.target(0);
			iX.target(getX());
			iY.target(getY());
		}
		float xx = iX.value;
		float yy = iY.value;
	
		
		if (g.getHoverNode() == this) {
			parent.textAlign(PApplet.CENTER);
			parent.textSize(fontSize);
			
			int sat =( parent.frameCount*22%200);
			parent.noStroke();
			parent.fill(color.getRed(), color.getGreen(), color.getBlue(),55+sat);
			// Draw node names
			parent.text(name, xx, yy-7);
			parent.ellipse(xx, yy, fontSize, fontSize);
		} 
		else if (g.getHoverNode() != null && g.getHoverNode()!=this && !isConnected) {
			parent.fill(color.getRed(), color.getGreen(), color.getBlue(),20);
			parent.ellipse(xx, yy, fontSize, fontSize);
		}
		else{
			parent.textAlign(PApplet.CENTER);
			parent.textSize(fontSize);
			wordWidth = parent.textWidth(name);
			parent.noStroke();
			parent.fill(color.getRGB());
			if (MultipleReactionView.checkName.s){
				// Draw node names
				parent.translate(xx,yy);
				parent.rotate(iAlpha.value);
				parent.fill(color.getRed(), color.getGreen(), color.getBlue());
				parent.text(name, 0, 0);
				parent.rotate(-iAlpha.value);
				parent.translate(-xx,-yy);
			}
			else{
				parent.fill(color.getRed(), color.getGreen(), color.getBlue(),220);
				parent.ellipse(xx, yy, fontSize, fontSize);
			}
		}
		iX.update();
		iY.update();
		iAlpha.update();
	}
}
