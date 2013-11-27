public class Complex {
    double re;
    double im;

    public Complex (double re, double im) {
        this.re = re;
        this.im = im;
    }

    /**
     *
     * @return String-representation av objektets innehåll
     */
    @Override
    public String toString() {
        return re + " + " + im + " i";
    }

    /**
     *
     * @return Reella delen av det komplexa talet
     */
    public double getRe() {
        return re;
    }

    /**
     *
     * @return Imaginära delen av det komplexa talet
     */
    public double getIm() {
        return im;
    }

    /**
     *
     * @return Returnerar komplexa talets absolutbelopp i kvadrat
     */
    public double getAbs2() {
        return Math.pow(re, 2) + Math.pow(im, 2);
    }

    /**
     *
     * @return Returnerar komplexa talets absolutbelopp
     */
    public double getAbs() {
        return Math.sqrt(getAbs2());
    }

    /**
     * Adderar med det komplexa talet c
     * @param c Talet att addera med
     */
    public void add(Complex c) {
        re += c.getRe();
        im += c.getIm();
    }

    /**
     * Multiplicerar med det komplexa talet c
     * @param c Talet att multiplicera med
     */
    public void mul(Complex c) {
        // (a+bi)(c+di) = (ac-bd) + (bc+ad)i
        double newRe = (re*c.getRe() - im*c.getIm());
        double newIm = (im*c.getRe() + re*c.getIm());
        re = newRe;
        im = newIm;
    }
}