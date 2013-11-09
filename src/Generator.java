import se.lth.cs.ptdc.fractal.MandelbrotGUI;

import java.awt.*;

public class Generator {
    Color[] greyScale;
    Color[] colorScale;
    int maxIterations = 200;
    int lowK;

    public Generator() {
        this.greyScale = new Color[256];
        for(int i=0; i<256; i++) {
            this.greyScale[i] = new Color(i, i, i);
        }

        this.colorScale = new Color[256];
        for(int i=0; i<256; i++) {
            this.colorScale[i] = new Color(i, i, i);
        }
    }

    public void render(MandelbrotGUI w) {
        w.disableInput();

        String extra = w.getExtraText();
        if (!extra.isEmpty())
            maxIterations = Integer.parseInt(w.getExtraText());

        lowK = maxIterations;

        double minRe = w.getMinimumReal(), maxRe = w.getMaximumReal();
        double minIm = w.getMinimumImag(), maxIm = w.getMaximumImag();
        int height = w.getHeight(), width = w.getWidth();
        int res = w.getResolution();

        int pix = 1;
        if      (res == MandelbrotGUI.RESOLUTION_VERY_HIGH) pix=1;
        else if (res == MandelbrotGUI.RESOLUTION_HIGH)      pix=3;
        else if (res == MandelbrotGUI.RESOLUTION_MEDIUM)    pix=5;
        else if (res == MandelbrotGUI.RESOLUTION_LOW)       pix=7;
        else if (res == MandelbrotGUI.RESOLUTION_VERY_LOW)  pix=9;

        Complex[][] complex = mesh(minRe, maxRe, minIm, maxIm, width, height);
        int[][] mandelK = getMandelK(complex, pix);
        maxIterations += lowK;
        Color[][] colors = getImage(mandelK);
        w.putData(colors, pix, pix);

        w.enableInput();
    }

    private int[][] getMandelK(Complex[][] complex, int res) {
        int[][] mandelK = new int[complex.length/res][complex[0].length/res];
        for (int icol = 0; icol < complex[0].length / res; icol++)
            for (int irow = 0; irow < complex.length / res; irow++)
                mandelK[irow][icol] = getK(complex[irow * res + res / 2][icol * res + res / 2]);
        return mandelK;
    }

    private Color[][] getImage(int[][] mandelK) {
        Color[][] colors = new Color[mandelK.length][mandelK[0].length];
        for (int icol = 0; icol < mandelK[0].length; icol++)
            for (int irow = 0; irow < mandelK.length; irow++)
                colors[irow][icol] = getColor(mandelK[irow][icol]);
        return colors;
    }

    private Color getColor(int k) {
        return greyScale[255 - 255 * (k-lowK) / maxIterations];
    }

    private int getK(Complex c) {
        Complex z = new Complex(0,0);
        z.add(c);
        for (int k=1; k<maxIterations; k++) {
            z.mul(z);
            z.add(c);
            if (z.getAbs() > 2) {
                if (k < lowK) {
                    System.out.println("New lowK: " + k);
                    lowK = k;
                }
                return k;
            }
        }
        return maxIterations;
    }

    private Complex[][] mesh(double minRe, double maxRe,
                             double minIm, double maxIm,
                             int width, int height) {
        Complex[][] plane = new Complex[height][width];

        double stepRe = (maxRe-minRe)/width;
        double stepIm = (maxIm-minIm)/height;

        for (int icol=0; icol<width; icol++) {
            double real = minRe+stepRe*icol;
            for (int irow=0; irow<height; irow++) {
                double imaginary = maxIm-stepIm*irow;
                plane[irow][icol] = new Complex(real, imaginary);
            }
        }

        return plane;
    }
}
