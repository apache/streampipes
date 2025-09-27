package org.apache.streampipes.extensions.api.locker;

import java.util.concurrent.TimeUnit;

public interface Lock {

    void tryLock(String pipelineId, TimeUnit timeout);

    void unlock(String pipelineId);

}
