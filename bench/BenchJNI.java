
import java.util.Random;

public class BenchJNI {
    public static void main(String[] args) {
        // Setup data
        int n = 1_000_000;
        int k = 100;
        double[] signal = new double[n];
        double[] kernel = new double[k];
        Random rand = new Random(42);

        for (int i = 0; i < n; i++)
            signal[i] = rand.nextDouble();
        for (int i = 0; i < k; i++)
            kernel[i] = rand.nextDouble() / k;

        System.out.println("Processing JNI Benchmark...");
        System.out.println("Signal size: " + n + ", Kernel size: " + k);

        // Warmup
        for (int i = 0; i < 5; i++) {
            SigJNI.convolve(signal, kernel);
        }

        // Benchmark
        long start = System.nanoTime();
        int iterations = 10;
        for (int i = 0; i < iterations; i++) {
            SigJNI.convolve(signal, kernel);
        }
        long end = System.nanoTime();

        double avgTimeMs = (end - start) / 1_000_000.0 / iterations;
        System.out.println("JNI Average Time: " + avgTimeMs + " ms");
    }
}
