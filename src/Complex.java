public class Complex {
    double re;
    double im;

    public Complex (double re, double im) {
        this.re = re;
        this.im = im;
    }

    @Override
    public String toString() {
        return re + " + " + im + " i";
    }

    public double getRe() {
        return re;
    }

    public double getIm() {
        return im;
    }

    public double getAbs2() {
        return Math.pow(re, 2) + Math.pow(im, 2);
    }

    public double getAbs() {
        return Math.sqrt(getAbs2());
    }

    public void add(Complex c) {
        re += c.getRe();
        im += c.getIm();
    }

    public void mul(Complex c) {
        // (a+bi)(c+di) = (ac-bd) + (bc+ad)i
        double newRe = (re*c.getRe() - im*c.getIm());
        double newIm = (im*c.getRe() + re*c.getIm());
        re = newRe;
        im = newIm;
    }
}