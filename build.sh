#!/bin/bash
set -e

# --- Configuration ---
# Java 8 (JRE)
JAVA8_HOME="/usr/lib64/jvm/java-1.8.0-openjdk-1.8.0"
JAVA8_Bin="$JAVA8_HOME/jre/bin/java"

# Java 25 (Oracle JDK)
JAVA25_HOME="/usr/lib/jvm/jdk-25-oracle-x64"
JAVA25_JAVAC="$JAVA25_HOME/bin/javac"
JAVA25_JAVA="$JAVA25_HOME/bin/java"

# System Javac (Java 17) used to compile for Java 8 target
SYSTEM_JAVAC="javac"

echo "=== Config ==="
echo "Java 8 Runtime: $JAVA8_Bin"
echo "Java 25 Home:   $JAVA25_HOME"
echo "System Javac:   $(which javac)"
echo "=============="

# --- 1. Compile Native Library (Shared) ---
echo ">> Compiling Native Library..."
gcc -shared -fPIC \
    -O3 \
    -I"$JAVA25_HOME/include" \
    -I"$JAVA25_HOME/include/linux" \
    -I"c" \
    -I"jni" \
    -o libsigjni.so \
    c/sig.c jni/sig_jni.c \
    -lm

echo "   [OK] libsigjni.so created."

# --- 2. JNI Implementation ---
echo ">> Compiling JNI..."
"$SYSTEM_JAVAC" --release 8 -h jni jni/SigJNI.java

echo ">> Running JNI with Java 8..."
"$JAVA8_Bin" -Djava.library.path=. -cp jni SigJNI

# --- 3. FFM Implementation ---
echo ">> Compiling FFM..."
"$JAVA25_JAVAC" --enable-preview --source 25 ffm/SigFFM.java

echo ">> Running FFM with Java 25..."
"$JAVA25_JAVA" --enable-preview -Djava.library.path=. -cp . ffm.SigFFM

# --- 4. Benchmarks ---
echo ""
echo "=== BENCHMARKS ==="

# JNI Bench
echo ">> Compiling JNI Bench..."
"$SYSTEM_JAVAC" --release 8 -cp jni bench/BenchJNI.java
echo ">> Running JNI Bench..."
# BenchJNI is in default package, SigJNI is in default package (in jni dir source, class in jni dir)
"$JAVA8_Bin" -Djava.library.path=. -cp jni:bench BenchJNI

# FFM Bench
echo ">> Compiling FFM Bench..."
# BenchFFM is in package bench
"$JAVA25_JAVAC" --enable-preview --source 25 -cp . bench/BenchFFM.java
echo ">> Running FFM Bench..."
# We run bench.BenchFFM, classpath root is . (since bench folder is inside)
"$JAVA25_JAVA" --enable-preview --enable-native-access=ALL-UNNAMED -Djava.library.path=. -cp . bench.BenchFFM
