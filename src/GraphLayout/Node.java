package GraphLayout;

import java.awt.Color;
import java.util.ArrayList;

import main.Integrator;
import main.PathwayView;
import main.Pathway2;
import main.PopupPathway;
import processing.core.PApplet;
import processing.core.PImage;

//Copyright 2005 Sean McCullough
//banksean at yahoo

public class Node {
	Vector3D f = new Vector3D(0, 0, 0);
	float mass = 1;
	public float size = 1;
	//public float wordWidth = 1;
	public String name = "";
	public PApplet parent;
	public Color color = null;

	Vector3D position;
	Graph g;
	public boolean isConnected = false;
	public int nodeId = -99;
	public static int bWord = -99;
	public int degree = 0;
	public Integrator iAlpha = new Integrator(0,0.2f,0.4f);
	public Integrator iX = new Integrator(0,0.2f,0.4f);
	public Integrator iY = new Integrator(0,0.2f,0.4f);
	public float difX = 0;
	public float difY = 0;
	
	public Node(Vector3D v, PApplet p) {
		position = v;
		parent = p;
	}

	public void setGraph(Graph h) {
		g = h;
	}

	public boolean containsNode(float x, float y) {
		if (PApplet.dist(iX.value, iY.value, parent.mouseX, parent.mouseY)<=size/2+1)
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
		else if (position.getX()>PathwayView.xRight-10)
			position.setX(PathwayView.xRight-10);
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
		size = m;
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

	
	
	
	public void draw() {
		if (PathwayView.popupLayout.s==0){ //Tree
			iAlpha.target(PApplet.PI/2);
			iX.target(parent.width/2-50);
			iY.target(PathwayView.yTree[nodeId]);
			difX = 0;
			difY = 0;
		}
		else if (PathwayView.popupLayout.s==1){ //Line up
			iAlpha.target(PApplet.PI/2);
			float xx = parent.width*7.5f/20;
			iX.target(xx);
			iY.target(PathwayView.yTopological[nodeId]);
			difX = 0;
			difY = 0;
		}
		else if (PathwayView.popupLayout.s==2){ //circular Layout
			/*float al = PathwayView.computeAlpha(nodeId);
			float xR = PathwayView.xCircular + (PathwayView.rCircular+size/2)*PApplet.sin(al);
			float yR = PathwayView.yCircular + (PathwayView.rCircular+size/2)*PApplet.cos(al);
			float xR2 = PathwayView.xCircular + (PathwayView.rCircular)*PApplet.sin(al);
			float yR2 = PathwayView.yCircular + (PathwayView.rCircular)*PApplet.cos(al);
			difX = xR-xR2;
			difY = yR-yR2;
			
			iAlpha.target(al);
			iX.target(xR);
			iY.target(yR);*/
		}
		else{
			iAlpha.target(PApplet.PI/2);
			iX.target(getX());
			iY.target(getY());
			difX = 0;
			difY = 0;
		}
		float xx = iX.value;
		float yy = iY.value;
	
		
		if (g.getHoverNode() == this) {
			/*
			parent.textAlign(PApplet.CENTER);
			parent.textSize(size);
			
			int sat =( parent.frameCount*22%200);
			parent.noStroke();
			parent.fill(color.getRed(), color.getGreen(), color.getBlue(),55+sat);
			// Draw node names
			parent.textSize(12);
			parent.text(name, xx, yy-7);
			parent.ellipse(xx, yy, size, size);*/
		} 
		else if (g.getHoverNode() != null && g.getHoverNode()!=this && !isConnected) {
			parent.fill(color.getRed(), color.getGreen(), color.getBlue(),20);
			parent.noStroke();
			parent.ellipse(xx, yy, size, size);
		}
		else{
			
			//wordWidth = parent.textWidth(name);
			parent.noStroke();
			parent.fill(color.getRGB());
			if (PathwayView.checkName.s){
				// Draw node names
				parent.fill(color.getRed(), color.getGreen(), color.getBlue());
				parent.textSize(12);
				if (0<=iAlpha.value && iAlpha.value<= PApplet.PI/2)
					parent.textAlign(PApplet.LEFT);
				else if (-PApplet.PI/2<=iAlpha.value && iAlpha.value<= 0)
					parent.textAlign(PApplet.LEFT);
				else if (PApplet.PI/2<=iAlpha.value )
					parent.textAlign(PApplet.LEFT);
				else
					parent.textAlign(PApplet.LEFT);
				
				parent.translate(xx,yy);
				parent.rotate(PApplet.PI/2-iAlpha.value);
				parent.text(name, 0, 0);
				parent.rotate(-(PApplet.PI/2-iAlpha.value));
				parent.translate(-xx,-yy);
				
			}
			else{
				if(PopupPathway.b>=0){
					Pathway2 pathway = PopupPathway.pathwayList.get(PopupPathway.b);
					if (pathway.isContainReaction(name)){
						parent.fill(0,15+parent.frameCount*22%240);
						parent.ellipse(xx, yy, size*2, size*2);
					}	
				}
				
				// check redundant reactions
				ArrayList<Integer> fileList =  new ArrayList<Integer>();
				for (int i=0;i<PopupPathway.redundantPathway.size();i++){
					Pathway2 pathway = PopupPathway.redundantPathway.get(i);
					if (pathway.isContainReaction(name)){
						//parent.fill(255,0,255);
						if (!fileList.contains(pathway.f))
							fileList.add(pathway.f);
					}	
				}
				if (fileList.size()>0){ // This reaction is in multiple file
					float increase = 2*PApplet.PI/fileList.size();
					float al1 = -PApplet.PI;
					for (int i=0;i<fileList.size();i++){
						float al2 =al1+increase; 
						Color color2 = PathwayView.getColor(fileList.get(i));
						parent.fill(color2.getRed(), color2.getGreen(), color2.getBlue(),200);
						parent.noStroke();
						parent.arc(xx, yy, size, size, al1, al2);
						al1=al2;
					}
				}
				else{
					parent.fill(color.getRed(), color.getGreen(), color.getBlue(),200);
					parent.ellipse(xx, yy, size, size);
				}
			}
		}
	}
}
