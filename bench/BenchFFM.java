package bench;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.util.Random;

public class BenchFFM {
    // Handle setup (copied from SigFFM logic for standalone bench)
    private static final SymbolLookup lookup;
    private static final Linker linker = Linker.nativeLinker();
    private static final MethodHandle convolveHandle;

    static {
        System.loadLibrary("sigjni");
        lookup = SymbolLookup.loaderLookup();

        convolveHandle = linker.downcallHandle(
                lookup.find("convolve").orElseThrow(),
                FunctionDescriptor.ofVoid(
                        ValueLayout.ADDRESS, ValueLayout.JAVA_INT,
                        ValueLayout.ADDRESS, ValueLayout.JAVA_INT,
                        ValueLayout.ADDRESS));
    }

    public static void main(String[] args) throws Throwable {
        int n = 1_000_000;
        int k = 100;
        double[] signal = new double[n];
        double[] kernel = new double[k];
        Random rand = new Random(42);

        for (int i = 0; i < n; i++)
            signal[i] = rand.nextDouble();
        for (int i = 0; i < k; i++)
            kernel[i] = rand.nextDouble() / k;

        System.out.println("Processing FFM Benchmark...");
        System.out.println("Signal size: " + n + ", Kernel size: " + k);

        try (Arena arena = Arena.ofConfined()) {
            // Pre-allocate memory to avoid allocation noise in loop (fair comparison to JNI
            // which copies every time?
            // Actually JNI copies every time. So FFM *could* copy every time to be
            // identical, OR we use FFM's strength (reuse).
            // Let's mimic JNI behavior: allocate fresh segments or at least copy data for
            // the call to be comparable
            // in terms of "calling a function".
            // However, normally with FFM you'd keep data in 'Arena' if possible.
            // Let's allocate ONCE (best case FFM) and measure computation call overhead vs
            // JNI copy overhead.

            MemorySegment s = arena.allocateFrom(ValueLayout.JAVA_DOUBLE, signal);
            MemorySegment kSeg = arena.allocateFrom(ValueLayout.JAVA_DOUBLE, kernel);
            MemorySegment out = arena.allocate(ValueLayout.JAVA_DOUBLE, n);

            // Warmup
            for (int i = 0; i < 5; i++) {
                convolveHandle.invoke(s, n, kSeg, k, out);
            }

            // Benchmark
            long start = System.nanoTime();
            int iterations = 10;
            for (int i = 0; i < iterations; i++) {
                convolveHandle.invoke(s, n, kSeg, k, out);
            }
            long end = System.nanoTime();

            double avgTimeMs = (end - start) / 1_000_000.0 / iterations;
            System.out.println("FFM Average Time: " + avgTimeMs + " ms");
        }
    }
}
