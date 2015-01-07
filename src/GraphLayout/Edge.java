package GraphLayout;

import java.awt.Color;

import main.MultipleReactionView;
import main.Slider2;
import processing.core.PApplet;

//Copyright 2005 Sean McCullough
//banksean at yahoo

public class Edge {
	float k=0.12f; //stiffness
	float strokeWeight=0; 
	public float naturalLength=1; //natural length.  ehmm uh, huh huh stiffness. natural length ;-)
	Node to;
	Node from;
	Graph g;
	PApplet parent;

	public Edge(Node t, Node f, PApplet papa) {
		parent = papa;
		to = t;
		from = f;
	}
	public void setStrokeWeight(float wei) {
		strokeWeight = wei;
	    naturalLength = Slider2.val;
	 }
	public float getNaturalLength() {
	    return naturalLength;
	  }
	  
	public void setGraph(Graph h) {
		g = h;
	}

	public Node getTo() {
		return to;
	}

	public Node getFrom() {
		return from;
	}

	public void setTo(Node n) {
		to = n;
	}

	public void setFrom(Node n) {
		from = n;
	}

	public float dX() {
		return to.getX() - from.getX();
	}

	public float dY() {
		return to.getY() - from.getY();
	}

	public Vector3D getForceTo() {
	    float dx = dX();
	    float dy = dY();
	    float l = PApplet.sqrt(dx*dx + dy*dy);
	    float f = k*(l-naturalLength);
	    return new Vector3D(-f*dx/l, -f*dy/l, 0);
	  }
	    
	  public Vector3D getForceFrom() {
	    float dx = dX();
	    float dy = dY();
	    float l = PApplet.sqrt(dx*dx + dy*dy);
	    float f = k*(l-naturalLength);
	    
	    return new Vector3D(f*dx/l, f*dy/l, 0);
	  }

	  public void draw() {
	    if (parent!=null && g!=null){
	    	parent.strokeWeight(strokeWeight);
    	    if (g.getHoverNode() ==null){
    	    	drawLine(200);
	    	}
	    	else if (g.getHoverNode().equals(from) ||
	    		g.getHoverNode().equals(to)){ 
	    		drawLine(255);
			    from.isConnected =true;
			    to.isConnected = true;
	    	}
	    	else{
	    		drawLine(20);
	     	}
	 	}
	  }
	  public void drawLine(float sat) {
		 parent.strokeWeight(1);
		 if (MultipleReactionView.popupLayout.s==1){  // LineUp
			drawCircularRelationship(sat);
		 }
		 else if (MultipleReactionView.popupLayout.s==2){   // Forced-directed Layout
			drawCircularRelationship(sat);
		}
		else if (MultipleReactionView.popupLayout.s==3) { // Circular layout
			// parent.line(from.iX.value, from.iY.value, to.iX.value, to.iY.value);
			
			int numSec =6;
			float x1 = from.iX.value;
			float y1 = from.iY.value;
			for (int i=1;i<=numSec;i++){
				float sss = (float) i/numSec;
				float x2 = from.iX.value+(to.iX.value-from.iX.value)*sss;
				float y2 = from.iY.value+(to.iY.value-from.iY.value)*sss;
				float sat2 = sat*sss;
				float r = (255-sat*sss)*0.7f;
				
				parent.stroke(r,r,0,sat2);
				parent.line(x1,y1,x2,y2);
				x1=x2;
				y1=y2;
			}
		}	
 	  }
	  
	  public void drawCircularRelationship(float sat){
		 // float a1 = from.iAlpha.value;
		  	int r1 = from.nodeId;
			float x1 = from.iX.value;
			float y1 = from.iY.value;
			
		//	float a2 = to.iAlpha.value;
			int r2 = to.nodeId;
			float x2 = to.iX.value;
			float y2 = to.iY.value;
			
			
			float alpha = (y2-y1)/(x2-x1);
			alpha = PApplet.atan(alpha);
			float dis = (y2-y1)*(y2-y1)+(x2-x1)*(x2-x1);
			float dd = PApplet.sqrt(dis);
			
			float alCircular =0;
			float d3, x3, y3, newR;
			
			if (0<r2-r1 && r2-r1<=MultipleReactionView.rectList.size()/2){
				 alCircular = PApplet.PI-((float) (r2-r1)*2/MultipleReactionView.rectList.size())*PApplet.PI;
				 if (alCircular==0)       // Straight line
					 alCircular = 0.01f; 
				 newR = (dd/2)/PApplet.sin(alCircular/2);
		    	 d3 = PApplet.dist(x1,y1,x2,y2);
				 x3 = (x1+x2)/2 - ((y1-y2)/2)*PApplet.sqrt(PApplet.pow(newR*2/d3,2)-1);
				 y3 = (y1+y2)/2 + ((x1-x2)/2)*PApplet.sqrt(PApplet.pow(newR*2/d3,2)-1);
			}
			else if (r2-r1>MultipleReactionView.rectList.size()/2){ // relationship of 2 wordcloud away
				 alCircular = ((float) (r2-r1)*2/MultipleReactionView.rectList.size())*PApplet.PI-PApplet.PI;
				 newR = (dd/2)/PApplet.sin(alCircular/2);
				 d3 = PApplet.dist(x1,y1,x2,y2);
				 x3 = (x1+x2)/2 + ((y1-y2)/2)*PApplet.sqrt(PApplet.pow(newR*2/d3,2)-1);
				 y3 = (y1+y2)/2 - ((x1-x2)/2)*PApplet.sqrt(PApplet.pow(newR*2/d3,2)-1);
				 
				 //parent.strokeWeight(1);
				 //	parent.stroke(0,50);
				 //	parent.line(x1,y1,x2,y2);
			}
			else if (0<r1-r2 && r1-r2<=MultipleReactionView.rectList.size()/2){
				 alCircular = PApplet.PI-((float) (r1-r2)*2/MultipleReactionView.rectList.size())*PApplet.PI;
				 if (alCircular==0)       // Straight line
					 alCircular = 0.01f; 
				 newR = (dd/2)/PApplet.sin(alCircular/2);
		    	 d3 = PApplet.dist(x1,y1,x2,y2);
				 x3 = (x1+x2)/2 + ((y1-y2)/2)*PApplet.sqrt(PApplet.pow(newR*2/d3,2)-1);
				 y3 = (y1+y2)/2 - ((x1-x2)/2)*PApplet.sqrt(PApplet.pow(newR*2/d3,2)-1);
			}
			else if (r1-r2>MultipleReactionView.rectList.size()/2){
				 alCircular = ((float) (r1-r2)*2/MultipleReactionView.rectList.size())*PApplet.PI-PApplet.PI;
				 if (alCircular==0)       // Straight line
					 alCircular = 0.01f; 
				 newR = (dd/2)/PApplet.sin(alCircular/2);
		    	 d3 = PApplet.dist(x1,y1,x2,y2);
				 x3 = (x1+x2)/2 - ((y1-y2)/2)*PApplet.sqrt(PApplet.pow(newR*2/d3,2)-1);
				 y3 = (y1+y2)/2 + ((x1-x2)/2)*PApplet.sqrt(PApplet.pow(newR*2/d3,2)-1);
			}
				
			else {
				x3=0;
				y3=100;
				newR=100;
			}
			
			float delX1 = (x1-x3);
			float delY1 = (y1-y3);
			float delX2 = (x2-x3);
			float delY2 = (y2-y3);
			float al1 = PApplet.atan2(delY1,delX1);
			float al2 = PApplet.atan2(delY2,delX2);
			if (al1-al2>PApplet.PI)
				al1=al1-2*PApplet.PI;
			if (al2-al1>PApplet.PI)
				al2=al2-2*PApplet.PI;
			
			parent.noFill();
			parent.strokeWeight(1);
			
			boolean down = true;
			if (r2<r1)
				down = false;
			
			drawArc(x3, y3, newR*2,  al1, al2, down, sat);
			parent.strokeWeight(1);
			
	  }

	  
	  public void drawArc(float x3, float y3, float d3, float al1, float al2, boolean down, float sat){
			int numSec = 15;
			float beginAngle = al1;
			if (al1>al2)
				beginAngle = al1;
			for (int k=0;k<=numSec;k++){
				float endAngle = al1+k*(al2-al1)/numSec;
				if (al1>al2)
					endAngle = al2+k*(al1-al2)/numSec;
				parent.noFill();
				float sss = (float) k/numSec;
				float sat2 = sat-sat*sss;
				float r = sat*sss;
				if (!down){
					sat2 = sat*sss;
					r=sat-sat*sss;
				}
				if (sat>100){
					if (sat2<100)
						sat2=100;
				}
				parent.stroke(r,r,0,sat2);
				parent.arc(x3, y3, d3,d3, beginAngle, endAngle);
				beginAngle = endAngle;
			}
	}	  
}
