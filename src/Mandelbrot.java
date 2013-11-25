import se.lth.cs.ptdc.fractal.MandelbrotGUI;

public class Mandelbrot {

    public static void main(String[] args) {
        MandelbrotGUI w = new MandelbrotGUI();
        Generator gen = new Generator();

        boolean drawn = false;

        while (true) {
            int cmd = w.getCommand();
            switch (cmd) {
                case (MandelbrotGUI.RENDER):
                    gen.render(w);
                    drawn = true;
                    break;
                case (MandelbrotGUI.RESET):
                    w.resetPlane();
                    gen.reset();
                    drawn = false;
                    break;
                case (MandelbrotGUI.QUIT):
                    System.exit(0);
                    break;
                case (MandelbrotGUI.ZOOM):
                    if (drawn) gen.render(w);
                    break;
                default:
                    System.out.println(cmd);
                    break;
            }
        }
    }
}