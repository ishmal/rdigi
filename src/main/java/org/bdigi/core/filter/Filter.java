package org.bdigi.core.filter;

import org.bdigi.core.Complex;

/**
 * Methods common to all filters
 */
public interface Filter {
    public double update(double v);
    public Complex update(Complex v);
}
