package org.apache.streampipes.service.core.scheduler;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.apache.streampipes.dataexplorer.api.IDataExplorerQueryManagement;
import org.apache.streampipes.dataexplorer.api.IDataExplorerSchemaManagement;
import org.apache.streampipes.dataexplorer.management.DataExplorerDispatcher;
import org.apache.streampipes.model.datalake.DataLakeMeasure;
import org.slf4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DataLakeScheduler {

    private static final Logger LOG = LoggerFactory.getLogger(DataLakeScheduler.class);

    private final IDataExplorerSchemaManagement dataExplorerSchemaManagement = new DataExplorerDispatcher()
        .getDataExplorerManager()
        .getSchemaManagement();

    private final IDataExplorerQueryManagement dataExplorerQueryManagement  = new DataExplorerDispatcher()
        .getDataExplorerManager()
        .getQueryManagement(this.dataExplorerSchemaManagement);
  

    public void exportMeasurements(){

    }

    public void deleteMeasurements(DataLakeMeasure m){
        Instant now = Instant.now();
        Instant DaysAgo = now.minus(m.getRetentionTime().dataRetentionConfig().olderThanDays(), ChronoUnit.DAYS);

        long endDate = DaysAgo.toEpochMilli();
        log.info("Current time in millis: " + now.toEpochMilli());
        log.info("Current time in millis to delete: " + endDate);

        this.dataExplorerQueryManagement.deleteData(m.getMeasureName(), null, endDate);
    }


	@Scheduled(cron="0 1 0 * * 6")//CronJob Scheduled every Saturday (5) 00:01(cron="0 */5 * * * * ")//CronJob Scheduled evey 5 min
	public void cleanupMeasurements() {
        List<DataLakeMeasure> allMeasurements = this.dataExplorerSchemaManagement.getAllMeasurements();
        log.info("GET ALL Measurements");
        for (DataLakeMeasure m : allMeasurements) {
            if(m.getRetentionTime() != null){
                log.info("Start delete Measurement "+ m.getMeasureName());
                deleteMeasurements(m);
                log.info("Measurements "+m.getMeasureName()+ " successfully deleted");


            }
            
        }
		
	}
    
}
