package main;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.biopax.paxtools.model.level3.BiochemicalReaction;
import org.biopax.paxtools.model.level3.Complex;
import org.biopax.paxtools.model.level3.SmallMolecule;

import processing.core.PApplet;
import GraphLayout.*;


public class MultipleReactionView{
	public PApplet parent;
	public ArrayList<String> files;
	public int nFiles;
	public boolean isAllowedDrawing = false;
	
	// Read data 
	public Map<String,String> mapProteinRDFId;
	public Map<String,String> mapSmallMoleculeRDFId;
	public Map<String,String> mapComplexRDFId;
	public Map<String,Complex> mapComplexRDFId_Complex;
	public Set<SmallMolecule> smallMoleculeSet;
	
	public ArrayList<String> complexList = new ArrayList<String>(); 
	public ArrayList<String> proteinList = new ArrayList<String>();
	
	public static ArrayList<BiochemicalReaction> rectList;
	public ArrayList<Integer> rectSizeList;
	public ArrayList<Integer> rectFileList;
	public ArrayList<Integer> rectOrderList;
	public int[] pathwaySize;
	
	
	public int maxSize = 0;
	public Gradient gradient = new Gradient();
	public float colorScale=0;
	public static Integrator[][] iS;
	public static float xCircular, yCircular, rCircular; 
	
	
	public static Graph g;
	public static float xRight =0;
	public Slider2 slider2;
	public static PopupLayout popupLayout;
	public static CheckBox checkName;
	
	
	// Line Up
	public static float[] yLineUp;
	public static Integrator iTransition = new Integrator(0,0.1f,0.4f);
	
	public MultipleReactionView(PApplet p){
		parent = p;
		slider2 = new Slider2(parent);
		popupLayout = new PopupLayout(parent);
		checkName = new CheckBox(parent,"Reactions names");
		
		xRight = parent.width*7.5f/10;
		float v=0.5f;
		gradient.addColor(new Color(0,0,v));
		gradient.addColor(new Color(0,v,v));
		gradient.addColor(new Color(0,v,0));
		gradient.addColor(new Color(v,v,0));
		gradient.addColor(new Color(v,0,0));
		gradient.addColor(new Color(v,0,v));
		gradient.addColor(new Color(0,0,v));
	}
	
	public void setItems(){
		// Causality integrator
		iS = new Integrator[rectList.size()][rectList.size()];
		for (int i=0;i<rectList.size();i++){
			for (int j=0;j<rectList.size();j++){
				iS[i][j] = new Integrator(0, 0.2f,SliderSpeed.speed/2);
			}
		}	
		
		
	
		// Compute proteinList and complexList
		complexList = new ArrayList<String>(); 
		proteinList = new ArrayList<String>();
		for (int r=0; r<rectList.size();r++){
			BiochemicalReaction react = rectList.get(r);
			Object[] left = react.getLeft().toArray();
			Object[] right = react.getRight().toArray();
			for (int i=0;i<left.length;i++){
				String ref = left[i].toString();
				  if ( mapProteinRDFId.get(ref)!=null){
					  String proteinName = mapProteinRDFId.get(ref);
					  if (!proteinList.contains(proteinName))
							proteinList.add(proteinName);
				  }	  
				  else if (mapComplexRDFId.get(ref)!=null){
					  String complexName = mapComplexRDFId.get(ref);
					  if (!complexList.contains(complexName))
						  complexList.add(complexName);
				  }
			}
			for (int i=0;i<right.length;i++){
				String ref = right[i].toString();
				  if ( mapProteinRDFId.get(ref)!=null){
					  String proteinName = mapProteinRDFId.get(ref);
					  if (!proteinList.contains(proteinName))
							proteinList.add(proteinName);
				  }	  
				  else if (mapComplexRDFId.get(ref)!=null){
					  String complexName = mapComplexRDFId.get(ref);
					  if (!complexList.contains(complexName))
						  complexList.add(complexName);
				  }
			}
		}
		
		// Compute size of reaction
		maxSize =0;
		for (int r=0; r<rectList.size();r++){
			BiochemicalReaction react = rectList.get(r);
			Object[] left = react.getLeft().toArray();
			Object[] right = react.getRight().toArray();
			
			ArrayList<Integer> proteinsL = getProteinsInOneSideOfReaction(left);
			ArrayList<Integer> proteinsR = getProteinsInOneSideOfReaction(right);
			int size = proteinsL.size()+ proteinsR.size();
			rectSizeList.add(size);   
			if (size>maxSize)
				maxSize = size;
		}
			
		colorScale = (float) gradient.colors.size()/ (nFiles+1) ;
		isAllowedDrawing = true;
	}
	
	public void updateNodes() {
		g = new Graph();
		for (int i = 0; i < rectList.size(); i++) {
			int pathwayId = rectFileList.get(i);
			int reactId = rectOrderList.get(i);
			Node node = new Node(new Vector3D( 20+parent.random(xRight-40), 20 + parent.random(parent.height-40), 0), parent) ;
			node.setMass(6+PApplet.pow(rectSizeList.get(i),0.7f));
			node.nodeId = i;
			node.name = rectList.get(i).getDisplayName();
			if (node.name==null)
				node.name = "NULL";
			node.color = gradient.getGradient(colorScale*(transferID(pathwayId)+(float)reactId/(pathwaySize[pathwayId]*2)));
			g.addNode(node);
		}	
		
		// Initialize topological ordering
		orderTopological();
	}
	// Make sure pathways next to each other receive different colora
	public float transferID(int id) {
		float newId = id;
		if (id%2==1){
			newId =  ((newId+(float) nFiles/2)%nFiles);
		}
		return newId;
	}
		
	
	public void updateEdges() {
		g.edges = new ArrayList<Edge>();
		g.edgesFrom = new HashMap<Node, ArrayList<Edge>>();
		g.edgesTo = new HashMap<Node, ArrayList<Edge>>();
		
		// Update slider value to synchronize the processes
		System.out.println();
		for (int r = 0; r < rectList.size(); r++) {
			Node node1 = g.nodes.get(r);
			int degree = 0;
			ArrayList<Integer> a = getDirectDownstream(r);
			for (int j = 0; j < a.size(); j++) {
				int r2 = a.get(j);
				Node node2 = g.nodes.get(r2);
				Edge e = new Edge(node1, node2, parent);
				e.setStrokeWeight(2);
				g.addEdge(e);
				degree++;
			}
			node1.degree = degree;
		}	
	}
	
	public ArrayList<Integer> getProteinsInOneSideOfReaction(Object[] s) {
		ArrayList<Integer> a = new ArrayList<Integer>();
		for (int i3=0;i3<s.length;i3++){
			  String ref = s[i3].toString();
			  if (mapProteinRDFId.get(ref)!=null){
				  String proteinName = mapProteinRDFId.get(ref);
				  int index = proteinList.indexOf(proteinName);
				  a.add(index);
			  }
			  else  if (mapComplexRDFId.get(ref)!=null){
				  ArrayList<String> components = getProteinsInComplexRDFId(ref);
				  for (int k=0;k<components.size();k++){
					  String proteinName = mapComplexRDFId.get(components.get(k));
					  int index = proteinList.indexOf(proteinName);
					  a.add(index);
				  }
			  }
			  else{
				  System.out.println("getProteinsInOneSideOfReaction: CAN NOT FIND ="+s[i3]+"-----SOMETHING WRONG");
			 } 
		  }
		return a;
	}
	
	public ArrayList<String> getProteinsInComplexRDFId(String ref){	
		ArrayList<String> components = new ArrayList<String>(); 
		Complex com = mapComplexRDFId_Complex.get(ref);
		Object[] s2 = com.getComponent().toArray();
		for (int i=0;i<s2.length;i++){
			String ref2 = s2[i].toString();
			 if (mapProteinRDFId.get(ref2)!=null)
				  components.add(mapProteinRDFId.get(ref2));
			 else if (mapComplexRDFId.get(ref2)!=null){
				  ArrayList<String> s4 = getProteinsInComplexRDFId(ref2);
				  for (int k=0;k<s4.size();k++){
					  components.add(s4.get(k));
				  }
			  }
		 }
		 return components;
	}
	
	public void draw(){
		if (!isAllowedDrawing) return;
		xRight = parent.width*7.5f/10;
		xCircular = xRight/2;
		yCircular = parent.height/2;
		rCircular = parent.height*3/7;
		
		for (int i=0;i<g.nodes.size();i++){
			Node node = g.nodes.get(i);
			node.iX.update();
			node.iY.update();
			node.iAlpha.update();
			
		}
		
		if (popupLayout.s==1){
			iTransition.target(PApplet.PI);
			iTransition.update();
			g.drawNodes();
			g.drawEdges();
		}
		if (popupLayout.s==2){
			iTransition.target(1);
			iTransition.update();
			g.drawNodes();
			g.drawEdges();
			/*for (int r1=0; r1<rectList.size();r1++){
				ArrayList<Integer> processedList =  new ArrayList<Integer>();
				drawDownStreamReaction(r1, processedList, 255);
			}*/
		}
		else if (popupLayout.s==3){
			iTransition.target(0);
			iTransition.update();
			if (g==null) return;
			doLayout();
			g.drawEdges();
			g.drawNodes();
		}
		// Right PANEL
		float wRight = parent.width-xRight;
		parent.fill(200,200);
		parent.noStroke();
		parent.rect(xRight, 25, wRight, parent.height-25);
		slider2.draw("Edge length",xRight+100, 50);
		checkName.draw(xRight+30, 80);
		popupLayout.draw(parent.width-198);
	}
	
	public ArrayList<Integer> orderTopological() {
		yLineUp =  new float[rectList.size()];
		ArrayList<Integer> b = new ArrayList<Integer>();
		
		ArrayList<Integer> doneList = new ArrayList<Integer>();
		ArrayList<Integer> circleList = new ArrayList<Integer>();
		
		int count = 0;
		int r = getNoneUpstream(doneList);
		while (count<rectList.size()){
		//	System.out.println(count+"	doneList="+doneList+"	r="+r);
			if (r>=0){
				doneList.add(r);
				r = getNoneUpstream(doneList);
			}
			else{
				int randomReaction = getReactionMaxDownstream(doneList);
				doneList.add(randomReaction);
				circleList.add(randomReaction);
				r = getNoneUpstream(doneList);
			}	
			count++;
		}
		
		
		// Compute nonCausality reaction
		ArrayList<Integer> nonCausalityList = new ArrayList<Integer>();
		for (int i=0;i<doneList.size();i++){
			int index = doneList.get(i);
			if (getDirectUpstream(index).size()==0 && getDirectDownstream(index).size()==0)
				nonCausalityList.add(index);
		}
		
		float totalH = parent.height-15;
		float itemH2 = totalH/(rectList.size()+circleList.size()-nonCausalityList.size()*0.8f+1);
		float circleGap = itemH2;
		float circleGapSum = 0;
		
		int count2 = 0;
		int count3 = 0;
		float yStartCausality = 15 +(rectList.size()-nonCausalityList.size()+circleList.size()+1)*itemH2;
		for (int i=0;i<doneList.size();i++){
			int index = doneList.get(i);
			// Compute nonCausality reaction
			if (getDirectUpstream(index).size()==0 && getDirectDownstream(index).size()==0){
				yLineUp[index] =  yStartCausality +count3*itemH2*0.2f;
				count3++;
			}	
			else{
				if(circleList.contains(index)){
					yLineUp[index] = circleGapSum+ 10+count2*itemH2+circleGap;
					circleGapSum +=circleGap;
				}
				else{
					yLineUp[index] = circleGapSum+10+count2*itemH2;
				}
				count2++;
			}
		}
		
		return b;
	}
	
		
	public int getNoneUpstream(ArrayList<Integer> doneList){
		ArrayList<Integer> a = new ArrayList<Integer>();
		for (int i=0;i<rectList.size();i++){
			if (doneList.contains(i)) continue;
			ArrayList<Integer> up = this.getDirectUpstream(i);
			if (up.size()==0)  {//No upstream
				a.add(i);
			}	
		}
		if (a.size()>0){
			return getReactionMinDownstreamIn(a);
		}
		else{
			ArrayList<Integer> b = new ArrayList<Integer>();
			for (int i=0;i<rectList.size();i++){
				if (doneList.contains(i)) continue;
				ArrayList<Integer> up = this.getDirectUpstream(i);
				if (isContainedAllUpInDoneList(up,doneList)){  // Upstream are all in the doneList;
				//	return i;
					b.add(i);
				}	
			}
			if (b.size()>0){
				return getReactionMaxUpstreamIn(b);
			}
			return -1;
		}
	}
	public boolean isContainedAllUpInDoneList(ArrayList<Integer> up, ArrayList<Integer> doneList){
		for (int i=0;i<up.size();i++){
			int r = up.get(i);
			if (!doneList.contains(r))
				return false;
		}
		return true;
	}
	
	public int getReactionMaxUpstreamIn(ArrayList<Integer> list){
		int numUpstream = 0;
		int react = -1;
		for (int i=0;i<list.size();i++){
			int index = list.get(i);
			ArrayList<Integer> up = getDirectUpstream(index);
			if (up.size()>=numUpstream){
				numUpstream = up.size();
				react =index;
			}	
		}
		return react;
	}
	
	public int getReactionMaxDownstream(ArrayList<Integer> doneList){
		ArrayList<Integer> a = new ArrayList<Integer>();
		for (int i=0;i<rectList.size();i++){
			if (doneList.contains(i)) continue;
			a.add(i);
		}
		return getReactionMaxDownstreamIn(a);
	}
	
	public int getReactionMaxDownstreamIn(ArrayList<Integer> list){
		int numDownstream = 0;
		int react = -1;
		for (int i=0;i<list.size();i++){
			int index = list.get(i);
			ArrayList<Integer> down = getDirectDownstream(index);
			if (down.size()>=numDownstream){
				numDownstream = down.size();
				react =index;
			}	
		}
		return react;
	}
	
	public int getReactionMinDownstreamIn(ArrayList<Integer> list){
		int numDownstream = Integer.MAX_VALUE;
		int react = -1;
		for (int i=0;i<list.size();i++){
			int index = list.get(i);
			ArrayList<Integer> down = getDirectDownstream(index);
			if (down.size()<numDownstream){
				numDownstream = down.size();
				react =index;
			}	
		}
		return react;
	}
	
	public ArrayList<Integer> getDirectDownstream(int r){
		ArrayList<Integer> a = new ArrayList<Integer>();
		BiochemicalReaction rectSelected = rectList.get(r);
		Object[] sRight1 = rectSelected.getRight().toArray();
		for (int g=0;g<rectList.size();g++) {
			if(g==r) continue;
			BiochemicalReaction rect2 = rectList.get(g);
			Object[] sLeft2 = rect2.getLeft().toArray();
			ArrayList<String> commonElements = compareInputOutput(sRight1, sLeft2);
			if (commonElements.size()>0){
				a.add(g);
			}
		}
		return a;
	}
	
	
	public ArrayList<Integer> getDirectUpstream(int r){
		ArrayList<Integer> a = new ArrayList<Integer>();
		BiochemicalReaction rectSelected = rectList.get(r);
		Object[] sLeft = rectSelected.getLeft().toArray();
		
		// List current reaction
		for (int g=0;g<rectList.size();g++) {
			if(g==r) continue;
			BiochemicalReaction rect2 = rectList.get(g);
			Object[] sRight2 = rect2.getRight().toArray();
			ArrayList<String> commonElements = compareInputOutput(sRight2, sLeft);
			if (commonElements.size()>0){
				a.add(g);
			}
		}
		return a;
	}
	
	
	
	public void doLayout() {
		// calculate forces on each node
		// calculate spring forces on each node
		for (int i = 0; i < g.getNodes().size(); i++) {
			Node n = (Node) g.getNodes().get(i);
			ArrayList edges = (ArrayList) g.getEdgesFrom(n);
			n.setForce(new Vector3D(0, 0, 0));
			for (int j = 0; edges != null && j < edges.size(); j++) {
				Edge e = (Edge) edges.get(j);
				Vector3D f = e.getForceFrom();
				n.applyForce(f);
			}

			edges = (ArrayList) g.getEdgesTo(n);
			for (int j = 0; edges != null && j < edges.size(); j++) {
				Edge e = (Edge) edges.get(j);
				Vector3D f = e.getForceTo();
				n.applyForce(f);
			}
		}

		// calculate the anti-gravitational forces on each node
		// this is the N^2 shittiness that needs to be optimized
		// TODO: at least make it N^2/2 since forces are symmetrical
		for (int i = 0; i < g.getNodes().size(); i++) {
			Node a = (Node) g.getNodes().get(i);
			for (int j = 0; j < g.getNodes().size(); j++) {
				Node b = (Node) g.getNodes().get(j);
				if (b != a) {
					float dx = b.getX() - a.getX();
					float dy = b.getY() - a.getY();
					float r = PApplet.sqrt(dx * dx + dy * dy);
					// F = G*m1*m2/r^2

					float f = 5*(a.getMass() * b.getMass() / (r * r));
					if (a.degree>0){
						f = PApplet.sqrt(a.degree)*f;
					}
					if (r > 0) { // don't divide by zero.
						Vector3D vf = new Vector3D(-dx * f, -dy * f, 0);
						a.applyForce(vf);
					}
				}
			}
		}
		
		for (int i = 0; i < g.getNodes().size(); i++) {
			Node a = (Node) g.getNodes().get(i);
			float dx = xCircular - a.getX();
			float dy = yCircular - a.getY();
			float r2 = dx * dx + dy * dy;
			float f =  r2/5000000;
			if (a.degree>0){
				Vector3D vf = new Vector3D(dx * f, dy * f, 0);
				a.applyForce(vf);
			}
		}

		// move nodes according to forces
		for (int i = 0; i < g.getNodes().size(); i++) {
			Node n = (Node) g.getNodes().get(i);
			if (n != g.getDragNode()) {
				n.setPosition(n.getPosition().add(n.getForce()));
			}
		}
	}
	
	
	public static float computeAlpha(int r){
		return PApplet.PI -((float)r)/(rectList.size())*2*PApplet.PI;
	}
		
	
	public ArrayList<String> compareInputOutput(Object[] a, Object[] b){
		ArrayList<String> results = new ArrayList<String>();
		for (int i=0; i<a.length;i++){
			String ref1 = a[i].toString();
			if (mapProteinRDFId.get(ref1)!=null){
				String proteinName1 = mapProteinRDFId.get(ref1);
				for (int j=0; j<b.length;j++){
					String ref2 = b[j].toString();
					String proteinName2 = mapProteinRDFId.get(ref2);
					if (proteinName2!=null && proteinName1.equals(proteinName2) 
							&& !mapSmallMoleculeRDFId.containsValue(proteinName1)){
							 results.add(proteinName1);
					}	
				}
			}
			else if (mapComplexRDFId.get(ref1)!=null){
				String complexName1 = mapComplexRDFId.get(ref1);
				for (int j=0; j<b.length;j++){
					String ref2 = b[j].toString();
					String complexName2 = mapComplexRDFId.get(ref2);
					if (complexName2!=null && complexName1.equals(complexName2)){
						 results.add(complexName1);
					}	
				}
			}
			
		}
		return results;
	}
	
	
	
	

	
	
	public void keyPressed() {
		if (g!=null){
			ArrayList<Node> nodes= g.getNodes();
			/*if (parent.key == '+') {
				//g.removeNode(g.getNodes().get(1));
				return;
			} else if (parent.key == '-') {
				g.removeNode(nodes.get(4));
				return;
			}*/
		}
	}

	public void mousePressed() {
		if (g==null) return;
		g.setDragNode(null);
		slider2.checkSelectedSlider1();
		for (int i = 0; i < g.getNodes().size(); i++) {
			Node n = (Node) g.getNodes().get(i);
			if (n.containsNode(parent.mouseX, parent.mouseY)) {
				g.setDragNode(n);
			}
		}
	}
	
	public void mouseReleased() {
		if (g==null) return;
		g.setDragNode(null);
		slider2.checkSelectedSlider2();
	}

	public void mouseMoved() {
		if (g!=null && g.getDragNode() == null) {
			g.setHoverNode(null);
			for (int i = 0; i < g.getNodes().size(); i++) {
				Node n = (Node) g.getNodes().get(i);
				if (n.containsNode(parent.mouseX, parent.mouseY)) {
					g.setHoverNode(n);
				}
			}
		}
		popupLayout.mouseMoved();
	}
	
	public void mouseClicked() {
		if (g==null) return;
		if (popupLayout.b>=0){
			popupLayout.mouseClicked();
		}
		else if (checkName.b)
			checkName.mouseClicked();
		else{
			g.setSelectedNode(null);
			for (int i = 0; i < g.getNodes().size(); i++) {
				Node n = (Node) g.getNodes().get(i);
				if (n.containsNode(parent.mouseX, parent.mouseY)) {
					g.setSelectedNode(n);
				}
			}
		}
	}

	public void mouseDragged() {
		slider2.checkSelectedSlider3();
		if (g==null) return;
		if (g.getDragNode() != null) {
			g.getDragNode()
					.setPosition(
							new Vector3D(parent.mouseX, parent.mouseY, 0));
		}
	}
	
}
	