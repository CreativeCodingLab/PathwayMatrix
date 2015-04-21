package main;


import java.awt.Color;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Random;

import processing.core.PApplet;
import processing.core.PFont;

@SuppressWarnings("serial")
public class MainTest41 extends PApplet {
	public static String[] scagNames = {"Outlying","Skewed","Clumpy","Sparse",
			"Striated","Convex","Skinny","Stringy","Monotonic"}; 
	
	public static int numSamp;
	public static int n;
	public static int numP;
	
	public static double[][][] scag;
	public static double[][][][] data;
	public PFont metaBold = loadFont("Arial-BoldMT-18.vlw");
	
	public static Color[] scagColors = new Color[9];	
	public static float yMargin =10;
	public final static String[] dataset = {"None", "Half","Square", "Half","Square", "Half", "Glucozo", "Glucozo"};
	public final static int numDataset = dataset.length;
	public static int menu = 0;  
	public static void main(String args[]){
	  PApplet.main(new String[] { MainTest41.class.getName() });
    }
	
	public void setup() {
		size(1440, 900);
		background(Color.WHITE.getRGB());
		stroke(255);
		frameRate(12);
		curveTightness(1.f); 
		
		if (menu==0){
			numSamp = 50;
			numP = 9;
			n = 200;
			data = new double[numSamp][numP][2][n];
			scag = new double[numSamp][numP][9];
		}	
		
		// Color brewer Set3
		scagColors[0] = new Color(141, 211, 199);
		scagColors[1] = new Color(255, 255, 179);
		scagColors[2] = new Color(190, 186, 218); 
		scagColors[3] = new Color(251, 128, 114); 
		scagColors[4] = new Color(128, 177, 211); 
		scagColors[5] = new Color(252, 205, 229); 
		scagColors[6] = new Color(253, 180, 98);
		scagColors[7] = new Color(179, 222, 105);
		scagColors[8] = new Color(188, 128, 189);
	
		addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent evt) {
				mouseWheel(evt);
			}
		});
	}
	
	
	
	public void draw() {
		this.background(255,255,255);
		this.smooth();
		this.textFont(metaBold);
		
		String[] dataN = {"176",	"355",		"3,599",		"1,993"};
		String[] dataP = {"11,337",	"29,614",	"8,128",	"8,128"};
		
		double[] time0 = 	{450,1494,43,1264}; // Disease Glucozo 		222		2589
		double[] time1 = 	{335,1087,14,1009}; // Regulation of DNA	86		5,968
		double[] time2 = 	{775,1819,72,1603}; // Myoclonic 			280		5,281
		double[] time3 = 	{274,1019,41,1434}; // NGF 					222		7,455
		double[] time4 = 	{209,2049,39,1661}; // ERBB2 				197		7,480
		double[] time5 = 	{331,454,13,1000}; // Inteferon				109		7,824
		double[] time6 = 	{400,7960,28,1082}; // HIV					176		11,337
		double[] time7 = 	{889,6991,73,1204}; // Influenza 			150		18,955
		//double[] time4 = 	{276,2026,19,1395}; // RB-E2F 				156		9,007
		//double[] time2 = 	{717,25751,101,1401}; // RNAP II			355		29,614
		
		double[][] time = 	new double[numDataset][];
		time[0] =  time0;
		time[1] =  time1;
		time[2] =  time2;
		time[3] =  time3;
		time[4] =  time4;
		time[5] =  time5;
		time[6] =  time6;
		time[7] =  time7;
		
		// Draw frames
		float xMargin =200;
		float w =767;
		float h =700;
		float x = xMargin;
			float y5 = yMargin;
			for (int t=0;t<10;t=t+1){
				this.noStroke();
				float gapY = h/10;
				float yT = y5+gapY*t;
				this.fill(220,220,220);
				this.rect(x,yT+1,w,gapY-2);
			}
		
		
		
		
			float y4 = yMargin;
			this.stroke(0,0,0);
			this.strokeWeight(2f);
			this.noFill();
			this.rect(x,y4,w,h);
		
		// Draw X axis
		float y = yMargin+h;
		float gapX = 90f;
		
		
		// Draw function names
		this.fill(0,0,0);
		this.textSize(20);
		this.textAlign(PApplet.CENTER);
		float w2 =60;
		for (int i=0;i<numDataset; i++){
			float x2 = x+ i*gapX+40+w2/2;
			float y2 = y+20;
			this.text(dataset[i],x2,y2);
		}
		this.textSize(26);
		this.textAlign(PApplet.CENTER);
		this.text("Transformation Functions",xMargin+w/2,yMargin+h+50);
		
		
		// Draw chart 1
		float maxTime = 10*1000; //2 seconds
		this.noStroke();
		this.textSize(20);
			float frameY = yMargin+ h-1;
			for (int d=0;d<numDataset; d++){
				float sum=0;
				for (int i=0;i<time[d].length; i++){
					float x2 = x+ (d)*gapX+40;
					//Read and transform
					float hh = (float) (time[d][i]*h/maxTime);
					float y1 = frameY-sum-hh;
					if (i==0)
						this.fill(0,0,150);
					else if (i==1)
						this.fill(0,150,0);
					else if (i==2)
						this.fill(150,0,0);
					else
						this.fill(140,0,160);
					
					this.rect(x2,y1,w2,hh);
					sum+=hh;
					
				}	
			}
			this.fill(0,0,0);
			this.textAlign(PApplet.LEFT);
			this.textSize(26);
			this.text(dataset[0], xMargin+40, yMargin+ h+ 35);
			this.textAlign(PApplet.RIGHT);
			this.textSize(20);
			this.text("n="+dataN[0], xMargin+350, yMargin+ h+ 35);
			this.text("p="+dataP[0], xMargin+500, yMargin+ h+ 35);
			
			// Draw Y axis
			this.textSize(20);
			this.textAlign(PApplet.RIGHT);
			for (int t=0;t<=2;t++){
				float gapY = h/2.5f;
				float yT = frameY-gapY*t+2*t;
				this.fill(0,0,0);
						this.text(t+"", xMargin -5,yT);
				if (t>0)
					this.text(t+"", xMargin -5,yT);
			}	
			
		
		// Draw Y axis TEXT
		this.textSize(26);
		this.textAlign(PApplet.CENTER);
		float al = -PApplet.PI/2; 
		this.translate(xMargin-35,yMargin+2*h);
		this.rotate(al);
		this.fill(0,0,0);
		this.text("Computation times (in minutes)",0,0);
		this.rotate(-al);
		this.translate(-(xMargin-35), -(yMargin+2*h));
		
		
		// Draw color legend
		this.textAlign(PApplet.LEFT);
		this.textSize(20);
		float x7 = xMargin+ 400;
		float y7 = yMargin;
		float size7 =15; 
		this.fill(40,40,130);
		this.rect(x7-w2,y7-75,w2-2,size7);
		this.text("Computing Scagnostics", x7+7, y7-60);
		
		this.fill(0,140,0);
		this.rect(x7-w2,y7-53,w2-2,size7);
		this.text("Binning", x7+7, y7-38);
		
		this.fill(190,0,0);
		this.rect(x7-w2,y7-31,w2-2,size7);
		this.text("Reading and transforming", x7+7, y7-16);
	}	
	public String formatIntegerMillinon(int num) {
		String nStr = ""+num;
		if (num<1000)
			return ""+num;
		int mi = num/1000000;
		return mi+","+"000"+","+nStr.substring(nStr.length()-3,nStr.length());	
	}
	
	public String formatIntegerThousand(int num) {
		String nStr = ""+num;
		if (num<1000)
			return ""+num;
		int th = num/1000;
		return th+","+nStr.substring(nStr.length()-3,nStr.length());	
	}
	
	public void keyPressed() {
	}
	
	
	public void mouseMoved() {
			
	}
	
	public void mousePressed() {
	}
	public void mouseReleased() {
	}
	
	public void mouseDragged() {
	}
	public void mouseClicked() {
	}
	
	public void mouseWheel(MouseWheelEvent e) {
		int delta = e.getWheelRotation();
			yMargin -=delta;
	}
}