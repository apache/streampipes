package org.apache.streampipes.rest.extensions.monitoring;

import org.apache.streampipes.extensions.management.monitoring.ServiceLoadDataReportGenerator;
import org.apache.streampipes.model.loadbalancer.ServiceLoadDataReport;
import org.apache.streampipes.rest.extensions.AbstractExtensionsResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("serviceMonitor")
public class ServiceMonitorResource extends AbstractExtensionsResource {
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ServiceLoadDataReport> getServiceMonitor() {
        return ok(ServiceLoadDataReportGenerator.generateReport());
    }
}
