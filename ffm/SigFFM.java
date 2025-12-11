package ffm;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.util.Arrays;

/**
 * Implementation of Foreign Function & Memory API (Java 25 Preview).
 * This code is for reference and requires JDK 25 with --enable-preview.
 */
public class SigFFM {

    // Symbol lookup for the native library
    private static final SymbolLookup lookup;
    private static final Linker linker = Linker.nativeLinker();

    static {
        // Load the shared library
        System.loadLibrary("sigjni"); // Reusing the same lib containing the C logic (and JNI wrappers we ignore here)
        lookup = SymbolLookup.loaderLookup();
    }

    // Method Handles
    private static final MethodHandle avgHandle;
    private static final MethodHandle varHandle;
    private static final MethodHandle medianHandle;
    private static final MethodHandle convolveHandle;
    private static final MethodHandle movingAvgHandle;
    private static final MethodHandle distanceHandle;
    private static final MethodHandle angleHandle;
    private static final MethodHandle dotProductHandle;

    static {
        // Function Descriptors and Lookups

        // double avg(double *data, int n);
        avgHandle = linker.downcallHandle(
                lookup.find("avg").orElseThrow(),
                FunctionDescriptor.of(ValueLayout.JAVA_DOUBLE, ValueLayout.ADDRESS, ValueLayout.JAVA_INT));

        // double var(double *data, int n);
        varHandle = linker.downcallHandle(
                lookup.find("var").orElseThrow(),
                FunctionDescriptor.of(ValueLayout.JAVA_DOUBLE, ValueLayout.ADDRESS, ValueLayout.JAVA_INT));

        // double median(double *data, int n);
        medianHandle = linker.downcallHandle(
                lookup.find("median").orElseThrow(),
                FunctionDescriptor.of(ValueLayout.JAVA_DOUBLE, ValueLayout.ADDRESS, ValueLayout.JAVA_INT));

        // void convolve(const double *signal, int n, const double *kernel, int k,
        // double *out);
        convolveHandle = linker.downcallHandle(
                lookup.find("convolve").orElseThrow(),
                FunctionDescriptor.ofVoid(
                        ValueLayout.ADDRESS, ValueLayout.JAVA_INT,
                        ValueLayout.ADDRESS, ValueLayout.JAVA_INT,
                        ValueLayout.ADDRESS));

        // void moving_avg(const double *data, int n, int window, double *out);
        movingAvgHandle = linker.downcallHandle(
                lookup.find("moving_avg").orElseThrow(),
                FunctionDescriptor.ofVoid(
                        ValueLayout.ADDRESS, ValueLayout.JAVA_INT,
                        ValueLayout.JAVA_INT,
                        ValueLayout.ADDRESS));

        // double distance(double x1, double y1, double x2, double y2);
        distanceHandle = linker.downcallHandle(
                lookup.find("distance").orElseThrow(),
                FunctionDescriptor.of(ValueLayout.JAVA_DOUBLE,
                        ValueLayout.JAVA_DOUBLE, ValueLayout.JAVA_DOUBLE,
                        ValueLayout.JAVA_DOUBLE, ValueLayout.JAVA_DOUBLE));

        // double angle(double x1, double y1, double x2, double y2);
        angleHandle = linker.downcallHandle(
                lookup.find("angle").orElseThrow(),
                FunctionDescriptor.of(ValueLayout.JAVA_DOUBLE,
                        ValueLayout.JAVA_DOUBLE, ValueLayout.JAVA_DOUBLE,
                        ValueLayout.JAVA_DOUBLE, ValueLayout.JAVA_DOUBLE));

        // double dot_product(const double *v1, const double *v2, int n);
        dotProductHandle = linker.downcallHandle(
                lookup.find("dot_product").orElseThrow(),
                FunctionDescriptor.of(ValueLayout.JAVA_DOUBLE, ValueLayout.ADDRESS, ValueLayout.ADDRESS,
                        ValueLayout.JAVA_INT));
    }

    public static void main(String[] args) throws Throwable {
        try (Arena arena = Arena.ofConfined()) {
            // --- Statistics ---
            double[] dataJava = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
            // Java 22+ / 25 Preview: Use MemorySegment.ofArray or Arena.allocateFrom
            MemorySegment dataSeg = arena.allocateFrom(ValueLayout.JAVA_DOUBLE, dataJava);

            double avg = (double) avgHandle.invoke(dataSeg, dataJava.length);
            System.out.println("Avg: " + avg);

            double var = (double) varHandle.invoke(dataSeg, dataJava.length);
            System.out.println("Var: " + var);

            double median = (double) medianHandle.invoke(dataSeg, dataJava.length);
            System.out.println("Median: " + median);

            // --- Filtering ---
            double[] sigJava = { 1, 2, 3, 4, 5 };
            double[] kernJava = { 0.5, 0.5 };
            MemorySegment sigSeg = arena.allocateFrom(ValueLayout.JAVA_DOUBLE, sigJava);
            MemorySegment kernSeg = arena.allocateFrom(ValueLayout.JAVA_DOUBLE, kernJava);
            // Allocating empty array for output
            MemorySegment outSeg = arena.allocate(ValueLayout.JAVA_DOUBLE, sigJava.length);

            convolveHandle.invoke(sigSeg, sigJava.length, kernSeg, kernJava.length, outSeg);
            double[] convRes = outSeg.toArray(ValueLayout.JAVA_DOUBLE);
            System.out.println("Convolve: " + Arrays.toString(convRes));

            MemorySegment mAvgOutSeg = arena.allocate(ValueLayout.JAVA_DOUBLE, dataJava.length);
            movingAvgHandle.invoke(dataSeg, dataJava.length, 3, mAvgOutSeg);
            double[] mAvgRes = mAvgOutSeg.toArray(ValueLayout.JAVA_DOUBLE);
            System.out.println("Moving Avg (w=3): " + Arrays.toString(mAvgRes));

            // --- Geometry ---
            double dist = (double) distanceHandle.invoke(0.0, 0.0, 3.0, 4.0);
            System.out.println("Distance: " + dist);

            double[] v1 = { 1, 2 };
            double[] v2 = { 3, 4 };
            MemorySegment v1Seg = arena.allocateFrom(ValueLayout.JAVA_DOUBLE, v1);
            MemorySegment v2Seg = arena.allocateFrom(ValueLayout.JAVA_DOUBLE, v2);
            double dot = (double) dotProductHandle.invoke(v1Seg, v2Seg, v1.length);
            System.out.println("Dot Product: " + dot);
        }
    }
}
