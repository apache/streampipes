package org.apache.streampipes.extensions.api.Limiter;

import com.google.common.util.concurrent.RateLimiter;
import org.slf4j.Logger;
import java.util.concurrent.TimeUnit;

public enum SpRateLimiter {
    
    INSTANCE;
    
    private RateLimiter rateLimiter;
    private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(SpRateLimiter.class);
    
    // 默认参数
    private static final double DEFAULT_PERMITS_PER_SECOND = 100.0;
    private static final long DEFAULT_WARMUP_PERIOD = 1000L;
    private static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.MILLISECONDS;

    public void createRateLimiter() {
        createRateLimiter(DEFAULT_PERMITS_PER_SECOND, DEFAULT_WARMUP_PERIOD, DEFAULT_TIME_UNIT);
    }

    public void createRateLimiter(double permitsPerSecond) {
        createRateLimiter(permitsPerSecond, DEFAULT_WARMUP_PERIOD, DEFAULT_TIME_UNIT);
    }

    public void createRateLimiter(double permitsPerSecond, long warmupPeriod, TimeUnit unit) {
        if (this.rateLimiter == null) {
            if (permitsPerSecond <= 0) {
                throw new IllegalArgumentException("permitsPerSecond must be positive, got: " + permitsPerSecond);
            }
            if (warmupPeriod < 0) {
                throw new IllegalArgumentException("warmupPeriod must be non-negative, got: " + warmupPeriod);
            }
            if (unit == null) {
                throw new IllegalArgumentException("TimeUnit cannot be null");
            }

            this.rateLimiter = RateLimiter.create(permitsPerSecond, warmupPeriod, unit);
            LOG.info("RateLimiter created with {} permits per second, warmup period: {} {}",
                    permitsPerSecond, warmupPeriod, unit);
        } else {
            LOG.warn("RateLimiter already exists. Use setRate() to modify the rate instead.");
        }
    }

    public void limit() throws InterruptedException {
        if (this.rateLimiter == null) {
            LOG.warn("RateLimiter has not been initialized. Please call createRateLimiter() first.");
            return;
        }
        
        if (rateLimiter.getRate() <= 0) {
            LOG.warn("RateLimiter is set to zero or negative rate. No permits will be acquired.");
            Thread.sleep(1000);
        } else {
            this.rateLimiter.acquire();
        }
    }

    public void setRate(double permitsPerSecond) {
        if (this.rateLimiter != null) {
            this.rateLimiter.setRate(permitsPerSecond);
            LOG.info("RateLimiter rate updated to {} permits per second", permitsPerSecond);
        } else {
            throw new IllegalStateException("RateLimiter has not been initialized.");
        }
    }

    public double getRate() {
        if (this.rateLimiter != null) {
            return this.rateLimiter.getRate();
        } else {
            throw new IllegalStateException("RateLimiter has not been initialized.");
        }
    }

    public boolean isInitialized() {
        return this.rateLimiter != null;
    }
    
    // 重置限流器
    public void reset() {
        if (this.rateLimiter != null) {
            this.rateLimiter = null;
            LOG.info("RateLimiter has been reset");
        }
    }
}
