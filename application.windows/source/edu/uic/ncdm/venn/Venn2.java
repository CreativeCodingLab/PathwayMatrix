/*
 * VennEuler -- A Venn and Euler Diagram program.
 *
 * Copyright 2009 by Leland Wilkinson.
 *
 * The contents of this file are subject to the Mozilla Public License Version 1.1 (the "License")
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the License.
 */

package edu.uic.ncdm.venn;
import edu.uic.ncdm.venn.data.VennData;
import edu.uic.ncdm.venn.display.VennFrame;



public class Venn2 {

    private Venn2() {
    }

    public static void main(String[] argv) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                read();
            }
        });
    }

    private static void read() {
        VennData dv = getData();
        VennAnalytic va = new VennAnalytic();
        VennDiagram vd = va.compute(dv);
        new VennFrame(vd);
    }

    public static VennData getData() {
    	int nLines =16;
    	String[][] data = new String[nLines][1];
        double[] areas =  new double[nLines];
        data[0][0] = "F";
        data[1][0] = "E";
        data[2][0] = "E&F";
        data[3][0] = "D";
        data[4][0] = "D&E";
        data[5][0] = "C";
        data[6][0] = "C&D";
        data[7][0] = "B";
        data[8][0] = "B&F";
        data[9][0] = "B&D";
        data[10][0] = "B&C";
        data[11][0] = "B&C&D";
        data[12][0] = "A";
        data[13][0] = "A&F";
        data[14][0] = "A&B";
        data[15][0] = "A&B&F";
       
       areas[0] = 3;
       areas[1] = 7;
       areas[2] = 1;
       areas[3] = 2;
       areas[4] = 1;
       areas[5] = 3;
       areas[6] = 1;
       areas[7] = 6;
       areas[8] = 2;
       areas[9] = 1;
       areas[10] = 2;
       areas[11] = 1;
       areas[12] = 4;
       areas[13] = 2;
       areas[14] = 2;
       areas[15] = 1;
         return new VennData(data, areas);
    }

   
}
