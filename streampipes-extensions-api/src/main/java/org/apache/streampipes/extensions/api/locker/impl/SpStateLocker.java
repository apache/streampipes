package org.apache.streampipes.extensions.api.locker.impl;

import org.apache.streampipes.commons.exceptions.SpException;
import org.apache.streampipes.commons.prometheus.SpStateLocker.SpStateLockerStats;
import org.apache.streampipes.extensions.api.locker.Lock;
import org.apache.streampipes.model.loadbalancer.PipelineInfo;
import org.apache.streampipes.model.loadbalancer.PipelineStates;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public enum SpStateLocker implements Lock {

    INSTANCE;

    // 每个 pipelineId 使用独立的 FIFO 队列锁，互不影响并行度
    private static final class QueueState {
        final ReentrantLock lock = new ReentrantLock(true);
        final Condition headChanged = lock.newCondition();
        final Deque<String> queue = new LinkedList<>();
    }

    private final Map<String, QueueState> states = new ConcurrentHashMap<>();

    private QueueState stateFor(String pipelineId) {
        return states.computeIfAbsent(pipelineId, id -> new QueueState());
    }

    // 记录每个 pipelineId 获得执行权的起始时间，用于计算持锁时长
    private final Map<String, Long> acquireStartNanos = new ConcurrentHashMap<>();

    // 兼容保留：方法签名只提供 TimeUnit，不提供时长，我们采用固定时长常量
    private static final long DEFAULT_TIMEOUT = 10L;

    @Override
    public void tryLock(String pipelineId, TimeUnit timeout) {
        long nanosTimeout = timeout.toNanos(DEFAULT_TIMEOUT);
        long t0 = System.nanoTime();
        SpStateLockerStats stats = SpStateLockerStats.get(pipelineId);
        if(stats==null){
            stats= new SpStateLockerStats(pipelineId);
        }
        QueueState qs = stateFor(pipelineId);
        qs.lock.lock();
        try {
            // 入队等待（同一 pipeline 的请求按 FIFO 排队）
            qs.queue.addLast(pipelineId);
            stats.lockQueueLength = qs.queue.size();
            SpStateLockerStats.metrics();
            // 只有当自己成为队首时，才获得执行权
            long remaining = nanosTimeout;
            while (!pipelineId.equals(qs.queue.peekFirst())) {
                if (remaining <= 0L) {
                    // 超时，退出队列并报错
                    qs.queue.remove(pipelineId);
                    stats.lockQueueLength = qs.queue.size();
                    stats.lockTimeout++;
                    stats.lockWaitSeconds = (System.nanoTime() - t0) / 1_000_000_000.0;
                    SpStateLockerStats.metrics();
                    throw new SpException("Lock timeout for pipeline: " + pipelineId + " within " + DEFAULT_TIMEOUT + " " + timeout);
                }
                try {
                    remaining = qs.headChanged.awaitNanos(remaining);
                } catch (InterruptedException e) {
                    // 中断时退出队列并恢复中断标志
                    qs.queue.remove(pipelineId);
                    stats.lockQueueLength = qs.queue.size();
                    stats.lockWaitSeconds = (System.nanoTime() - t0) / 1_000_000_000.0;
                    SpStateLockerStats.metrics();
                    Thread.currentThread().interrupt();
                    throw new SpException("Interrupted while waiting for lock on pipeline: " + pipelineId, e);
                }
            }
            // 成为队首：记录等待时间与持锁开始
            stats.lockWaitSeconds = (System.nanoTime() - t0) / 1_000_000_000.0;
            acquireStartNanos.put(pipelineId, System.nanoTime());
            stats.lockAcquiredCount = Math.max(0, stats.lockAcquiredCount + 1);
            SpStateLockerStats.metrics();
        } finally {
            qs.lock.unlock();
        }
    }

    @Override
    public void unlock(String pipelineId) {
        QueueState qs = stateFor(pipelineId);
        qs.lock.lock();
        try {
            // 仅队首可解锁，解锁=出队并唤醒后继
            if (pipelineId != null && pipelineId.equals(qs.queue.peekFirst())) {
                qs.queue.pollFirst();
                qs.headChanged.signalAll();
                SpStateLockerStats stats = SpStateLockerStats.get(pipelineId);
                if(stats==null){
                    stats=new SpStateLockerStats(pipelineId);
                }
                Long start = acquireStartNanos.remove(pipelineId);
                if (start != null) {
                    stats.lockHoldSeconds = (System.nanoTime() - start) / 1_000_000_000.0;
                } else {
                    stats.lockHoldSeconds = 0.0;
                }
                stats.lockAcquiredCount = Math.max(0, stats.lockAcquiredCount - 1);
                stats.lockQueueLength = qs.queue.size();
                SpStateLockerStats.metrics();
                // 若该 pipeline 队列空了，可释放状态以防内存增长
                if (qs.queue.isEmpty()) {
                    states.remove(pipelineId, qs);
                }
            }
        } finally {
            qs.lock.unlock();
        }
    }
    
    /**
     * 获取队列头的 pipelineId（不移除）—按 pipeline 维度
     */
    public String getHeadPipelineId(String pipelineId) {
        QueueState qs = stateFor(pipelineId);
        qs.lock.lock();
        try {
            return qs.queue.peekFirst();
        } finally {
            qs.lock.unlock();
        }
    }
    
    /**
     * 检查指定 pipeline 的队列是否为空
     */
    public boolean isQueueEmpty(String pipelineId) {
        QueueState qs = stateFor(pipelineId);
        qs.lock.lock();
        try {
            return qs.queue.isEmpty();
        } finally {
            qs.lock.unlock();
        }
    }
    
    /**
     * 获取指定 pipeline 的队列大小
     */
    public int getQueueSize(String pipelineId) {
        QueueState qs = stateFor(pipelineId);
        qs.lock.lock();
        try {
            return qs.queue.size();
        } finally {
            qs.lock.unlock();
        }
    }

    public PipelineInfo setPipelineInfo(String pipelineId, PipelineStates pipelineState) {
        Thread currentThread = Thread.currentThread();
        LocalDateTime currentTime = LocalDateTime.now();
        int waitCount;
        QueueState qs = stateFor(pipelineId);
        qs.lock.lock();
        try {
            waitCount = Math.max(0, qs.queue.size() - 1);
        } finally {
            qs.lock.unlock();
        }
        return new PipelineInfo(pipelineId,pipelineState,currentThread,currentTime,waitCount);
    }
}
