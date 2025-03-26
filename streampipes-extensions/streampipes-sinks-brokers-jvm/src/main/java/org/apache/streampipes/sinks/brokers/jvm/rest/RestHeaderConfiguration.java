package org.apache.streampipes.sinks.brokers.jvm.rest;

public record RestHeaderConfiguration (
        String headerKey,
        String headerValue
) {}
