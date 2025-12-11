#ifndef SIG_H
#define SIG_H

// Statistics
double avg(double *data, int n);
double var(double *data, int n);
double median(double *data, int n);

// Filtering
void convolve(const double *signal, int n, const double *kernel, int k, double *out);
void moving_avg(const double *data, int n, int window, double *out);

// Geometry
double distance(double x1, double y1, double x2, double y2);
double angle(double x1, double y1, double x2, double y2);
double dot_product(const double *v1, const double *v2, int n);

#endif
