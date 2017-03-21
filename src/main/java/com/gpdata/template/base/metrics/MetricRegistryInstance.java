package com.gpdata.template.base.metrics;

import com.codahale.metrics.MetricRegistry;

public final class MetricRegistryInstance {

    private static final MetricRegistry INSTANCE = new MetricRegistry();

    public static final MetricRegistry getInstance() {
        return INSTANCE;
    }
    
    
}
