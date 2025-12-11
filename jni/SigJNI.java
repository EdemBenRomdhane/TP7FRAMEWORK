
public class SigJNI {
    static {
        // Load the native library "sigjni" (libsigjni.so)
        System.loadLibrary("sigjni");
    }

    // Statistics
    public static native double avg(double[] data);
    public static native double var(double[] data);
    public static native double median(double[] data);

    // Filtering
    public static native double[] convolve(double[] signal, double[] kernel);
    public static native double[] movingAvg(double[] data, int window);

    // Geometry
    public static native double distance(double x1, double y1, double x2, double y2);
    public static native double angle(double x1, double y1, double x2, double y2);
    public static native double dotProduct(double[] v1, double[] v2);

    // Main for quick testing
    public static void main(String[] args) {
        double[] data = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10 ,11 ,12};
        System.out.print("data: ");
        for (int i = 0; i < data.length; i++) {System.out.print(data[i] + ", ");}
        System.out.println("\n"+"Avg: " + avg(data));
        System.out.println("Var: " + var(data));
        System.out.println("Median: " + median(data));

        double[] sig = {1, 2, 3, 4, 5};
        double[] kern = {0.5, 0.5};
        double[] conv = convolve(sig, kern);
        System.out.print("Convolve: ");
        for (double d : conv) System.out.print(d + " ");
        System.out.println();

        double[] mAvg = movingAvg(data, 3);
        System.out.print("Moving Avg (w=3): ");
        for (double d : mAvg) System.out.print(d + " ");
        System.out.println();

        System.out.println("Distance (0,0) to (3,4): " + distance(0, 0, 3, 4));
        System.out.println("Dot Product ([1,2], [3,4]): " + dotProduct(new double[]{1, 2}, new double[]{3, 4}));
    }
}
