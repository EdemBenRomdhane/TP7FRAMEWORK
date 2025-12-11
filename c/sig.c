#include <math.h>
#include <stdlib.h>
#include "sig.h"

// --- Statistics ---

double avg(double *data, int n) {
    if (n <= 0) return 0.0;
    double s = 0;
    for(int i = 0; i < n; i++) {
        s += data[i];
    }
    return s / n;
}

double var(double *data, int n) {
    if (n <= 0) return 0.0;
    double m = avg(data, n);
    double s = 0;
    for(int i = 0; i < n; i++) {
        double d = data[i] - m;
        s += d * d;
    }
    return s / n;
}

// Compare function for qsort
int compare_doubles(const void *a, const void *b) {
    double arg1 = *(const double *)a;
    double arg2 = *(const double *)b;
    if (arg1 < arg2) return -1;
    if (arg1 > arg2) return 1;
    return 0;
}

double median(double *data, int n) {
    if (n <= 0) return 0.0;
    
    // Copy data to avoid modifying original array
    double *sorted = (double*)malloc(n * sizeof(double));
    for(int i=0; i<n; i++) sorted[i] = data[i];
    
    qsort(sorted, n, sizeof(double), compare_doubles);
    
    double med;
    if (n % 2 == 0) {
        med = (sorted[n/2 - 1] + sorted[n/2]) / 2.0;
    } else {
        med = sorted[n/2];
    }
    
    free(sorted);
    return med;
}

// --- Filtering ---

void convolve(const double *signal, int n, const double *kernel, int k, double *out) {
    for (int i = 0; i < n; i++) {
        double s = 0;
        for (int j = 0; j < k; j++) {
            int idx = i - j;
            if (idx >= 0) {
                s += signal[idx] * kernel[j];
            }
        }
        out[i] = s;
    }
}

void moving_avg(const double *data, int n, int window, double *out) {
    if (window <= 0) return;
    
    for (int i = 0; i < n; i++) {
        double sum = 0;
        int count = 0;
        // Simple moving average: includes current element and previous window-1 elements
        // Strategy: take average of available elements up to window size ending at i
        // or Centered? TP doesn't specify. Let's assume standard causal moving average (last 'window' points)
        
        for (int j = 0; j < window; j++) {
            int idx = i - j;
            if (idx >= 0) {
                sum += data[idx];
                count++;
            }
        }
        out[i] = (count > 0) ? sum / count : 0.0;
    }
}

// --- Geometry ---

double distance(double x1, double y1, double x2, double y2) {
    return sqrt(pow(x2 - x1, 2) + pow(y2 - y1, 2));
}

double angle(double x1, double y1, double x2, double y2) {
    return atan2(y2 - y1, x2 - x1);
}

double dot_product(const double *v1, const double *v2, int n) {
    double sum = 0;
    for (int i = 0; i < n; i++) {
        sum += v1[i] * v2[i];
    }
    return sum;
}
