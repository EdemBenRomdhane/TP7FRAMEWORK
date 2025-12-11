#include <jni.h>
#include <stdlib.h>
#include "../c/sig.h"
#include "SigJNI.h"

// --- Statistics ---

JNIEXPORT jdouble JNICALL Java_SigJNI_avg
  (JNIEnv *env, jclass cls, jdoubleArray data) {
    jsize n = (*env)->GetArrayLength(env, data);
    jdouble *ptr = (*env)->GetDoubleArrayElements(env, data, 0);
    double result = avg(ptr, n);
    (*env)->ReleaseDoubleArrayElements(env, data, ptr, 0);
    return result;
}

JNIEXPORT jdouble JNICALL Java_SigJNI_var
  (JNIEnv *env, jclass cls, jdoubleArray data) {
    jsize n = (*env)->GetArrayLength(env, data);
    jdouble *ptr = (*env)->GetDoubleArrayElements(env, data, 0);
    double result = var(ptr, n);
    (*env)->ReleaseDoubleArrayElements(env, data, ptr, 0);
    return result;
}

JNIEXPORT jdouble JNICALL Java_SigJNI_median
  (JNIEnv *env, jclass cls, jdoubleArray data) {
    jsize n = (*env)->GetArrayLength(env, data);
    jdouble *ptr = (*env)->GetDoubleArrayElements(env, data, 0);
    double result = median(ptr, n);
    (*env)->ReleaseDoubleArrayElements(env, data, ptr, 0);
    return result;
}

// --- Filtering ---

JNIEXPORT jdoubleArray JNICALL Java_SigJNI_convolve
  (JNIEnv *env, jclass cls, jdoubleArray signal, jdoubleArray kernel) {
    jsize n = (*env)->GetArrayLength(env, signal);
    jsize k = (*env)->GetArrayLength(env, kernel);
    
    jdouble *s_ptr = (*env)->GetDoubleArrayElements(env, signal, 0);
    jdouble *k_ptr = (*env)->GetDoubleArrayElements(env, kernel, 0);
    
    jdoubleArray out = (*env)->NewDoubleArray(env, n);
    jdouble *out_native = malloc(n * sizeof(double));
    
    convolve(s_ptr, n, k_ptr, k, out_native);
    
    (*env)->SetDoubleArrayRegion(env, out, 0, n, out_native);
    
    free(out_native);
    (*env)->ReleaseDoubleArrayElements(env, signal, s_ptr, 0);
    (*env)->ReleaseDoubleArrayElements(env, kernel, k_ptr, 0);
    
    return out;
}

JNIEXPORT jdoubleArray JNICALL Java_SigJNI_movingAvg
  (JNIEnv *env, jclass cls, jdoubleArray data, jint window) {
    jsize n = (*env)->GetArrayLength(env, data);
    
    jdouble *d_ptr = (*env)->GetDoubleArrayElements(env, data, 0);
    
    jdoubleArray out = (*env)->NewDoubleArray(env, n);
    jdouble *out_native = malloc(n * sizeof(double));
    
    moving_avg(d_ptr, n, window, out_native);
    
    (*env)->SetDoubleArrayRegion(env, out, 0, n, out_native);
    
    free(out_native);
    (*env)->ReleaseDoubleArrayElements(env, data, d_ptr, 0);
    
    return out;
}

// --- Geometry ---

JNIEXPORT jdouble JNICALL Java_SigJNI_distance
  (JNIEnv *env, jclass cls, jdouble x1, jdouble y1, jdouble x2, jdouble y2) {
    return distance(x1, y1, x2, y2);
}

JNIEXPORT jdouble JNICALL Java_SigJNI_angle
  (JNIEnv *env, jclass cls, jdouble x1, jdouble y1, jdouble x2, jdouble y2) {
    return angle(x1, y1, x2, y2);
}

JNIEXPORT jdouble JNICALL Java_SigJNI_dotProduct
  (JNIEnv *env, jclass cls, jdoubleArray v1, jdoubleArray v2) {
    jsize n1 = (*env)->GetArrayLength(env, v1);
    // Assumes n1 == n2 based on typical dot product usage, but should check or take min
    jdouble *p1 = (*env)->GetDoubleArrayElements(env, v1, 0);
    jdouble *p2 = (*env)->GetDoubleArrayElements(env, v2, 0);
    
    double result = dot_product(p1, p2, n1);
    
    (*env)->ReleaseDoubleArrayElements(env, v1, p1, 0);
    (*env)->ReleaseDoubleArrayElements(env, v2, p2, 0);
    
    return result;
}
