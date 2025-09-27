package org.apache.streampipes.model.loadbalancer;

import java.time.LocalDateTime;

public class PipelineInfo {
    String lockKey;
    PipelineStates pipelineState;
    Thread currentThread;
    LocalDateTime lockTime;
    Integer waitCount;

    public PipelineInfo(String lockKey, PipelineStates pipelineState, Thread currentThread, LocalDateTime lockTime, Integer waitCount) {
        this.lockKey = lockKey;
        this.pipelineState = pipelineState;
        this.currentThread = currentThread;
        this.lockTime = lockTime;
        this.waitCount = waitCount;
    }

    public String getLockKey() {
        return lockKey;
    }

    public void setLockKey(String lockKey) {
        this.lockKey = lockKey;
    }

    public LocalDateTime getLockTime() {
        return lockTime;
    }

    public void setLockTime(LocalDateTime lockTime) {
        this.lockTime = lockTime;
    }

    public PipelineStates getPipelineState() {
        return pipelineState;
    }

    public void setPipelineState(PipelineStates pipelineState) {
        this.pipelineState = pipelineState;
    }

    public Thread getCurrentThread() {
        return currentThread;
    }

    public void setCurrentThread(Thread currentThread) {
        this.currentThread = currentThread;
    }

    public Integer getWaitCount() {
        return waitCount;
    }

    public void setWaitCount(Integer waitCount) {
        this.waitCount = waitCount;
    }
}
