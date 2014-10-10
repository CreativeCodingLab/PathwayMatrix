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

public class Eigen {

    private Eigen() {}

    public static void eigenSymmetric(double[][] a, double[][] eigenvectors, double[] eigenvalues) {
        // eigenvalues and eigenvectors for dense system (A-lambda*I)x = 0
        // returns eigenvectors in columns
        double[] e = new double[eigenvalues.length];
        Eigen.tred2(a, eigenvectors, eigenvalues, e);
        Eigen.imtql2(eigenvectors, eigenvalues, e);
        Eigen.sort(eigenvalues, eigenvectors);
    }

    private static void tred2(double[][] a, double[][] z, double[] d, double[] e) {

        /*     Tridiagonalization of symmetric matrix.
         *     This method is a translation of the Algol procedure tred2(),
         *     Wilkinson and Reinsch, Handbook for Auto. Comp., Vol II-Linear Algebra, (1971).
         *     Converted to Java by Leland Wilkinson.
        */

        int i, j, jp1, k, l, n;
        double f, g, h, hh, scale;

        n = d.length;
        for (i = 0; i < n; i++) {
            for (j = 0; j <= i; j++)
                z[i][j] = a[i][j];
        }
        if (n > 1) {
            for (i = n - 1; i > 0; i--) {
                l = i - 1;
                h = 0.0;
                scale = 0.0;
                if (l > 0) {
                    for (k = 0; k <= l; k++)
                        scale += Math.abs(z[i][k]);
                }
                if (scale == 0.0)
                    e[i] = z[i][l];
                else {
                    for (k = 0; k <= l; k++) {
                        z[i][k] /= scale;
                        h += z[i][k] * z[i][k];
                    }
                    f = z[i][l];
                    g = f < 0.0 ? Math.sqrt(h) : -Math.sqrt(h);
                    e[i] = scale * g;
                    h -= f * g;
                    z[i][l] = f - g;
                    f = 0.0;
                    for (j = 0; j <= l; j++) {
                        z[j][i] = z[i][j] / (scale * h);
                        g = 0.0;
                        for (k = 0; k <= j; k++)
                            g += z[j][k] * z[i][k];
                        jp1 = j + 1;
                        if (l >= jp1) {
                            for (k = jp1; k <= l; k++)
                                g += z[k][j] * z[i][k];
                        }
                        e[j] = g / h;
                        f += e[j] * z[i][j];
                    }
                    hh = f / (h + h);
                    for (j = 0; j <= l; j++) {
                        f = z[i][j];
                        g = e[j] - hh * f;
                        e[j] = g;
                        for (k = 0; k <= j; k++)
                            z[j][k] = z[j][k] - f * e[k] - g * z[i][k];
                    }
                    for (k = 0; k <= l; k++)
                        z[i][k] *= scale;
                }
                d[i] = h;
            }
        }
        d[0] = 0.0;
        e[0] = 0.0;
        for (i = 0; i < n; i++) {
            l = i - 1;
            if (d[i] != 0.0) {
                for (j = 0; j <= l; j++) {
                    g = 0.0;
                    for (k = 0; k <= l; k++)
                        g += z[i][k] * z[k][j];
                    for (k = 0; k <= l; k++)
                        z[k][j] -= g * z[k][i];
                }
            }
            d[i] = z[i][i];
            z[i][i] = 1.0;
            if (l >= 0) {
                for (j = 0; j <= l; j++) {
                    z[i][j] = 0.0;
                    z[j][i] = 0.0;
                }
            }
        }
    }

    private static int imtql2(double[][] z, double[] d, double[] e) {

        /*     Implicit QL iterations on tridiagonalized matrix.
             *     This method is a translation of the Algol procedure imtql2(),
             *     Wilkinson and Reinsch, Handbook for Auto. Comp., Vol II-Linear Algebra, (1971).
             *     Converted to Java by Leland Wilkinson.
        */

        int i, ii, ip1, j, k, l, lp1, m, mm, mml, n;
        double b, c, f, g, p, r, s;
        double EPSILON = 1.0e-15;

        n = d.length;
        if (n == 1)
            return 0;
        for (i = 1; i < n; i++)
            e[i - 1] = e[i];
        e[n - 1] = 0.0;
        mm = 0;

        for (l = 0; l < n; l++) {
            lp1 = l + 1;
            j = 0;
            for (; ;) {
                for (m = l; m < n; m++) {
                    mm = m;
                    if (m == n - 1)
                        break;
                    if (Math.abs(e[m]) <= EPSILON * (Math.abs(d[m]) + Math.abs(d[m + 1])))
                        break;
                }
                m = mm;
                p = d[l];
                if (m == l)
                    break;
                if (j == 30)
                    return l;
                j++;
                g = (d[lp1] - p) / (2.0 * e[l]);
                r = Math.sqrt(g * g + 1.0);
                g = d[m] - p + e[l] / (g + (g < 0.0 ? -r : r));
                s = 1.0;
                c = 1.0;
                p = 0.0;
                mml = m - l;
                for (ii = 1; ii <= mml; ii++) {
                    i = m - ii;
                    ip1 = i + 1;
                    f = s * e[i];
                    b = c * e[i];
                    if (Math.abs(f) >= Math.abs(g)) {
                        c = g / f;
                        r = Math.sqrt(c * c + 1.0);
                        e[ip1] = f * r;
                        s = 1.0 / r;
                        c *= s;
                    } else {
                        s = f / g;
                        r = Math.sqrt(s * s + 1.0);
                        e[ip1] = g * r;
                        c = 1.0 / r;
                        s *= c;
                    }
                    g = d[ip1] - p;
                    r = (d[i] - g) * s + 2.0 * c * b;
                    p = s * r;
                    d[ip1] = g + p;
                    g = c * r - b;
                    for (k = 0; k < n; k++) {
                        f = z[k][ip1];
                        z[k][ip1] = s * z[k][i] + c * f;
                        z[k][i] = c * z[k][i] - s * f;
                    }
                }
                d[l] -= p;
                e[l] = g;
                e[m] = 0.0;
            }
        }
        return 0;
    }

    private static void sort(double[] d, double[][] z) {
        int l;
        int k;
        int j;
        int i;
        int ip1;
        double p;
        int ii;
        int n = d.length;
/* Sort by eigenvalues (descending) */
        for (l = 1; l <= n; l = 3 * l + 1) ;
        while (l > 2) {
            l = l / 3;
            k = n - l;
            for (j = 0; j < k; j++) {
                i = j;
                while (i >= 0) {
                    ip1 = i + l;
                    if (d[i] < d[ip1] || Double.isNaN(d[i])) {
                        p = d[i];
                        d[i] = d[ip1];
                        d[ip1] = p;
                        for (ii = 0; ii < n; ii++) {
                            p = z[ii][i];
                            z[ii][i] = z[ii][ip1];
                            z[ii][ip1] = p;
                        }
                        i = i - l;
                    } else
                        break;
                }
            }
        }
    }
}
