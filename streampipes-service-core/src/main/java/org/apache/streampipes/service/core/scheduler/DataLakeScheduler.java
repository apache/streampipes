package org.apache.streampipes.service.core.scheduler;

import java.text.SimpleDateFormat;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.apache.streampipes.dataexplorer.api.IDataExplorerQueryManagement;
import org.apache.streampipes.dataexplorer.api.IDataExplorerSchemaManagement;
import org.apache.streampipes.dataexplorer.management.DataExplorerDispatcher;
import org.apache.streampipes.model.datalake.DataLakeMeasure;
import org.slf4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;

public class DataLakeScheduler {

    private static final Logger log = LoggerFactory.getLogger(DataLakeScheduler.class);

    private final IDataExplorerSchemaManagement dataExplorerSchemaManagement = new DataExplorerDispatcher()
        .getDataExplorerManager()
        .getSchemaManagement();

    private final IDataExplorerQueryManagement dataExplorerQueryManagement  = new DataExplorerDispatcher()
        .getDataExplorerManager()
        .getQueryManagement(this.dataExplorerSchemaManagement);
  }

    public void exportMeasurements(){

    }

    public void deleteMeasurements(DataLakeMeasure m){

        long startDate = System.currentTimeMillis();
        // TODO check CALC 
        long endDate = System.currentTimeMillis() - (1000L*60*60*24*m.getRetentionTime().dataRetentionConfig().olderThanDays());

        this.dataExplorerQueryManagement.deleteData(m.getElementId(), startDate, endDate);

    }


	@Scheduled(cron = "1 * * * * *")
	public void cleanupMeasurements() {
        // Get All Measurements 
        List<DataLakeMeasure> allMeasurements = this.dataExplorerSchemaManagement.getAllMeasurements();
        log.info("GET ALL Measurements");
        // Iterate through all measurements
        for (DataLakeMeasure m : allMeasurements) {

            //var measure = this.dataExplorerSchemaManagement.getById(m.getElementId());
            if(m.getRetentionTime() != null){

                //log.info("Start export Measurement");
                //exportMeasurements();
                log.info("Start delete Measurement");
                deleteMeasurements(m);
                log.info("Measurements successfully deleted");


            }
            
        }
		
	}
    
}
