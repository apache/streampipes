package org.apache.streampipes.model.extensions.svcdiscovery;

public class DefaultSpServiceTags {
    public static final SpServiceTag CORE = SpServiceTag.create(SpServiceTagPrefix.SYSTEM, "core");
    public static final SpServiceTag PE = SpServiceTag.create(SpServiceTagPrefix.SYSTEM, "pe");
    public static final SpServiceTag CONNECT_WORKER = SpServiceTag
            .create(SpServiceTagPrefix.SYSTEM, "connect-worker");
}
