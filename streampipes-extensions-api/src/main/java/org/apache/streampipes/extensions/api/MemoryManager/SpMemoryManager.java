package org.apache.streampipes.extensions.api.MemoryManager;

import org.slf4j.Logger;

public enum SpMemoryManager {
    INSTANCE;

    private  final Logger log = org.slf4j.LoggerFactory.getLogger(SpMemoryManager.class);
    private long freeMemory;

    SpMemoryManager() {
        this.freeMemory = 10L * 1024 * 1024 * 1024; // Initialize with 10 GB of free memory
        log.info("SpMemoryManager initialized with {} bytes of free memory", freeMemory);
    }

    public void allocate(long bytes) {
        if (bytes <= 0) {
            log.warn("Attempted to allocate non-positive memory: {} bytes", bytes);
            return;
        }
        
        // 循环检查内存，如果不足则阻塞等待
        while (true) {
            long currentFree = freeMemory;
            long newFreeMemory = currentFree - bytes;
            
            if (newFreeMemory >= 0) {
                // 有足够内存，进行分配
                freeMemory = newFreeMemory;
                log.info("Allocated {} bytes. New free memory: {} bytes", bytes, freeMemory);
                return;
            } else {
                // 内存不足，阻塞等待
                log.warn("Not enough free memory to allocate {} bytes. Current free memory: {} bytes. Blocking allocation.",
                        bytes, currentFree);
                
                synchronized (this) {
                    try {
                        // 阻塞等待，直到有内存被释放
                        wait(1000); // 等待1秒后重新检查
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        log.warn("Memory allocation blocking was interrupted");
                        return;
                    }
                }
            }
        }
    }

    public void free(long bytes) {
        if (bytes <= 0) {
            log.warn("Attempted to free non-positive memory: {} bytes", bytes);
            return;
        }
        
        synchronized (this) {
            freeMemory += bytes;
            log.info("Freed {} bytes. New free memory: {} bytes", bytes, freeMemory);
            
            // 通知所有等待的线程
            notifyAll();
        }
    }
}
