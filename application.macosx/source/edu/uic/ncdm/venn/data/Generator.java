package edu.uic.ncdm.venn.data;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

public class Generator {
    private int totalCount;
    private PrintWriter out;
    private String[] sets = new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m",
    "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
    private Random random;

    public Generator(Random random) {
        this.random = random;
    }

    public void compute(int nCircles) {
        openFile();
        calculateAreas(nCircles);
    }

    private void openFile() {
        try {
            FileWriter rawOut = new FileWriter("circles.txt");
            out = new PrintWriter(rawOut);
        }
        catch (IOException error) {
            error.printStackTrace();
        }

    }

    private void calculateAreas(int nCircles) {
        int nPolygons = (int) Math.pow(2, nCircles);
        double[] diameters = new double[nCircles];
        double[][] centers = new double[nCircles][2];
        double[] polyAreas = new double[nPolygons];
        for (int i = 0; i < nCircles; i++) {
            diameters[i] = .1 + .7 * random.nextDouble();
            centers[i][0] = .15 + .7 * random.nextDouble();
            centers[i][1] = .15 + .7 * random.nextDouble();
        }
        totalCount = 0;
        int size = 200;
        byte[][][] bis = new byte[nCircles][size][size];
        double mins = Double.POSITIVE_INFINITY;
        double maxs = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < nCircles; i++) {
            double radius = diameters[i] / 2;
            mins = Math.min(centers[i][0] - radius, mins);
            mins = Math.min(centers[i][1] - radius, mins);
            maxs = Math.max(centers[i][0] + radius, maxs);
            maxs = Math.max(centers[i][1] + radius, maxs);
        }
        for (int i = 0; i < nCircles; i++) {
            double xi = (centers[i][0] - mins) / (maxs - mins);
            double yi = (centers[i][1] - mins) / (maxs - mins);
            double di = diameters[i] / (maxs - mins);
            int r = (int) (di * size / 2.);
            int r2 = r * r;
            int cx = (int) (xi * size);
            int cy = (int) (size - yi * size);
            for (int x = 0; x < size; x++) {
                for (int y = 0; y < size; y++) {
                    if ((x - cx) * (x - cx) + (y - cy) * (y - cy) < r2)
                        bis[i][x][y] = 1;
                }
            }
        }
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                int[] counts = new int[nCircles];
                int count = 0;
                for (int j = 0; j < nCircles; j++) {
                    if (bis[j][x][y] == 1) {
                        counts[j]++;
                        count++;
                    }
                }
                if (count > 0)
                    updatePixels(counts, polyAreas);
            }
        }
        for (int i = 0; i < nPolygons; i++)
            polyAreas[i] = 100. * polyAreas[i] / totalCount;

        printElementDataset(nCircles, nPolygons, polyAreas);
    }

    private void printAreaDataset(int nCircles, int nPolygons, double[] polyAreas) {
        out.println("set area");
        for (int i = 0; i < nPolygons; i++) {
            double polyArea = polyAreas[i];
            if (polyArea == 0)
                continue;
            char[] c = encode(i, nCircles);
            String label = "";
            for (int j = 0; j < c.length; j++) {
                if (c[j] != '0')
                    label += sets[j] + "&";
            }
            if (label.endsWith("&"))
                label = label.substring(0, label.length() - 1);
            out.println(label + " " + polyArea);
        }
        out.close();
    }

    private void printElementDataset(int nCircles, int nPolygons, double[] polyAreas) {
        out.println("element set");
        for (int i = 0; i < nPolygons; i++) {
            double polyArea = polyAreas[i];
            if (polyArea == 0)
                continue;
            int p = (int) polyArea;
            char[] c = encode(i, nCircles);
            String label = "";
            for (int j = 0; j < c.length; j++) {
                if (c[j] != '0')
                    label += sets[j];
            }
            for (int j = 0; j < c.length; j++) {
                if (c[j] == '0') continue;
                for (int k = 0; k < p; k++)
                    out.println(label +" "+sets[j]);
            }
        }
        out.close();
    }

    private void updatePixels(int[] counts, double[] polyAreas) {
        int index = decode(counts);
        polyAreas[index]++;
        totalCount++;
    }

    private char[] encode(int index, int nCircles) {
        String s = Integer.toBinaryString(index);
        char[] c = s.toCharArray();
        int offset = nCircles - c.length;
        char[] result = new char[nCircles];
        for (int i = 0; i < offset; i++)
            result[i] = '0';
        System.arraycopy(c, 0, result, offset, c.length);
        return result;
    }

    private int decode(int[] subsets) {
        String b = "";
        for (int j = 0; j < subsets.length; j++) {
            if (subsets[j] > 0)
                b += '1';
            else
                b += '0';
        }
        return Integer.parseInt(b, 2);
    }
}
