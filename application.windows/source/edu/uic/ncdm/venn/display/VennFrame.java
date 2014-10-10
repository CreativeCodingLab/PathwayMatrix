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

package edu.uic.ncdm.venn.display;

import edu.uic.ncdm.venn.VennDiagram;

import javax.swing.*;
import java.awt.*;

public class VennFrame extends JFrame {
    private int HEIGHT = 700;
    private int WIDTH = 700;

    public VennFrame(VennDiagram vd) {
        Container con = this.getContentPane();
        con.setBackground(Color.white);
        VennCanvas vc = new VennCanvas(vd);
        con.add(vc);
        setTitle("Venn/Euler Diagram");
        setBounds(0, 0, WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setVisible(true);
    }
}
