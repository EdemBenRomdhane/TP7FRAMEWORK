# TP7: Java Native Interface (JNI) & Foreign Function & Memory API (FFM)

This project demonstrates how to integrate native C code with Java using two different approaches:
1.  **JNI (Java Native Interface)**: The classic approach (compatible with Java 8+).
2.  **FFM (Foreign Function & Memory API)**: The modern approach (Project Panama, requires Java 22+).

## Project Structure

*   `c/`: Native C library source code (Statistics, Filtering, Geometry).
    *   `sig.c`: Implementation.
    *   `sig.h`: Header.
*   `jni/`: JNI Wrapper implementation.
    *   `SigJNI.java`: Java class with `native` methods.
    *   `sig_jni.c`: C JNI wrapper functions.
*   `ffm/`: FFM Implementation.
    *   `SigFFM.java`: Pure Java code using the FFM API to call C functions directly.
*   `bench/`: Benchmarking code.
*   `build.sh`: Build script to compile and run everything.

## Prerequisites

To run this project fully, you need:
*   **GCC**: To compile the shared C library.
*   **Java 8**: For running the JNI implementation (Legacy support).
*   **Java 25 (or 22+)**: For running the FFM implementation (Modern API).

## How to Run

A `build.sh` script is provided to compile the C library and run both Java implementations.

```bash
./build.sh
```

### Manual Steps
See [RUNNING.md](RUNNING.md) for detailed step-by-step commands.

## Features Implemented
The native library (`libsigjni.so`) implements:
*   **Statistics**: Average, Variance, Median.
*   **Filtering**: 1D Convolution, Moving Average.
*   **Geometry**: Euclidean Distance, Angle, Dot Product.

## Performance
We compared JNI vs FFM performance (Convolution 1D).
*   **JNI**: ~46 ms
*   **FFM**: ~41 ms
FFM proved to be slightly faster and safer to use (no C wrapper code required).
