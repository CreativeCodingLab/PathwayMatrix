package GraphLayout;

import processing.core.PApplet;

//Copyright 2005 Sean McCullough
//banksean at yahoo

public class Edge {
	float k=0.01f; //stiffness
	float strokeWeight=0; 
	float naturalLength=1; //natural length.  ehmm uh, huh huh stiffness. natural length ;-)
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
	    naturalLength = 100;
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
	    
	    if (f>50)
	    	f=50;
	    return new Vector3D(-f*dx/l, -f*dy/l, 0);
	  }
	    
	  public Vector3D getForceFrom() {
	    float dx = dX();
	    float dy = dY();
	    float l = PApplet.sqrt(dx*dx + dy*dy);
	    float f = k*(l-naturalLength);
	    
	    if (f>50)
	    	f=50;
	    return new Vector3D(f*dx/l, f*dy/l, 0);
	  }

	  public void draw() {
	    if (parent!=null && g!=null){
	    	parent.strokeWeight(strokeWeight);
    	    if (g.getHoverNode() ==null){
    	    	drawLine(40);
	    	}
	    	else if (g.getHoverNode().equals(from) ||
	    		g.getHoverNode().equals(to)){ 
	    		drawLine(150);
			    from.isConnected =true;
			    to.isConnected = true;
	    	}
	    	else{
	    		drawLine(12);
	     	}
	 	}
	  }
	  public void drawLine(float sat) {
		parent.stroke(200,sat);
  		/*int cloudId1 = this.from.wordId/main.MainBlog_Version_7_2.numWords;
  		int cloudId2 = this.to.wordId/main.MainBlog_Version_7_2.numWords;
  		if (cloudId1==cloudId2){
  			if (cloudId1==0){
  				parent.stroke(0,255,0,sat);
  		  	}
  			else if (cloudId1==1){
  				parent.stroke(255,0,0,sat);
  		  	}
  			else if (cloudId1==2){
  				parent.stroke(0,125,255,sat);
  		  	}
  			else if (cloudId1==3){
  				parent.stroke(255,255,0,sat);
  		  	}
  		}*/	
  	//	if (strokeWeight>11) return;
		parent.strokeWeight(strokeWeight);
  		parent.line(from.getX(), from.getY(), to.getX(), to.getY());
 	  }
		  
}
