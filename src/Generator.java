import se.lth.cs.ptdc.fractal.MandelbrotGUI;

import java.awt.*;

public class Generator {
    Color[] greyScale;
    Color[] colorScale;
    Color voidColor = new Color(0,0,0);
    int maxIterations = 200;
    int lowK;
    int highK;

    public Generator() {
        this.reset();

        this.greyScale = new Color[256];
        for (int i=0; i<255; i++) {
            greyScale[i] = new Color(i,i,i);
        }

        this.colorScale = new Color[256];
        int r = 0;
        int g = 0;
        int b = 255;
        int i = 0;
        while (r < 255) {
            this.colorScale[i] = new Color(r, g, b);
            r += 3;
            i++;
        }
        while (b > 0) {
            this.colorScale[i] = new Color(r, g, b);
            b -= 3;
            i++;
        }
        while (g < 81) {
            this.colorScale[i] = new Color(r, g, b);
            g += 1;
            i++;
        }
    }

    public void reset() {
        lowK = maxIterations;
        highK = 0;
    }

    public void render(MandelbrotGUI w) {
        w.disableInput();

        String extra = w.getExtraText();
        if (!extra.isEmpty())
            maxIterations = Integer.parseInt(w.getExtraText());

        lowK = maxIterations;
        highK = 0;

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

        boolean color = false;
        if (w.getMode() == MandelbrotGUI.MODE_COLOR) color = true;

        // Experimental
        //Color[][] colors = apply(mandelK);
        Color[][] colors = getImage(mandelK, color);
        w.putData(colors, pix, pix);

        maxIterations += lowK;

        w.enableInput();
    }

    private int[][] getMandelK(Complex[][] complex, int res) {
        int[][] mandelK = new int[complex.length/res][complex[0].length/res];
        for (int icol = 0; icol < complex[0].length / res; icol++)
            for (int irow = 0; irow < complex.length / res; irow++)
                mandelK[irow][icol] = getK(complex[irow * res + res / 2][icol * res + res / 2]);
        return mandelK;
    }

    private Color[][] getImage(int[][] mandelK, boolean color) {
        Color[][] image = new Color[mandelK.length][mandelK[0].length];
        for (int icol = 0; icol < mandelK[0].length; icol++)
            for (int irow = 0; irow < mandelK.length; irow++)
                if (color) {
                    image[irow][icol] = getColor(mandelK[irow][icol]);
                } else {
                    image[irow][icol] = getGreyscale(mandelK[irow][icol]);
                }
        return image;
    }

    private Color getColor(double k) {
        if (k == maxIterations) {
            return voidColor;
        } else {
            int colorIndex = (int)( Math.pow((k-lowK)/(highK-lowK), 0.5) * 255);
            return colorScale[colorIndex];
        }
    }

    private Color getGreyscale(double k) {
        if (k == maxIterations) {
            return voidColor;
        } else {
            int colorIndex = (int)( Math.pow((k-lowK)/(highK-lowK), 0.5) * 255);
            return greyScale[colorIndex];
        }
    }

    private int getK(Complex c) {
        Complex z = new Complex(0,0);
        z.add(c);
        for (int k=0; k<maxIterations; k++) {
            z.mul(z);
            z.add(c);
            if (z.getAbs() > 2) {
                if (k < lowK) {
                    System.out.println("New lowK: " + k);
                    lowK = k;
                } else if (k > highK) {
                    System.out.println("New highK: " + k);
                    highK = k;
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


    /**
     * Experimental!
     *
     * @param k
     * @return
     */
    public Color[][] apply(int[][] k) {
        //double cutOff = paramValue/100;
        double cutOff = 0.01;

        int height = k.length;
        int width = k[0].length;
        int pixels = height*width;

        int[] histogram = new int[256];

        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                histogram[k[i][j]] += 1;
            }
        }

        int[] cuts = computeCuts(histogram, cutOff, pixels);

        Color[][] outPixels = new Color[height][width];
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                int newk = 255 * (k[i][j] - cuts[0]) / (cuts[1] - cuts[0]);
                if (newk >= 0 && newk <= 255) {
                    outPixels[i][j] = greyScale[newk];
                } else if (newk > 255) {
                    outPixels[i][j] = voidColor;
                } else if (newk < 0) {
                    outPixels[i][j] = greyScale[0];
                }
            }
        }

        return outPixels;
    }

    private int[] computeCuts(int[] histogram, double cutOff, int pixels) {
        int[] histAccum = new int[maxIterations+1];
        histAccum[0] = histogram[0];
        histAccum[maxIterations] = pixels;

        int lowCut = 0;
        int highCut = 0;
        for (int i=1; i<maxIterations+1; i++) {
            histAccum[i] = histAccum[i-1] + histogram[i];
            if (histAccum[i] >= 1) {
                lowCut = i;
                break;
            }
        }
        for (int i=maxIterations-1; i>0; i--) {
            histAccum[i] = histAccum[i+1] - histogram[i];
            if (histAccum[i] <= pixels*(1-cutOff)) {
                highCut = i;
                break;
            }
        }

        return new int[] {lowCut, highCut};
    }
}
