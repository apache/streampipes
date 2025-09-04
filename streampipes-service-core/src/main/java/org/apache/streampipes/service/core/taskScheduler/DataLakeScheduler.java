package org.apache.streampipes.service.core.taskScheduler;

import org.apache.streampipes.dataexplorer.api.IDataExplorerQueryManagement;
import org.apache.streampipes.model.datalake.DataLakeMeasure;

import org.springframework.beans.factory.annotation.Autowired;

public class DataLakeScheduler extends TaskScheduler{

    private final DataLakeMeasure dataLakeMeasure;
    private final IDataExplorerQueryManagement dataExplorerQueryManagement;

    @Autowired
    public DataLakeScheduler(DataLakeMeasure dataLakeMeasure, IDataExplorerQueryManagement dataExplorerQueryManagement){
        this.dataLakeMeasure= dataLakeMeasure;
        //TODO How can I get this ? 
        this.dataExplorerQueryManagement=dataExplorerQueryManagement;
    } 

    @Override
    void scheduleCronTask(String cronExpression) {
        //TODO TEst this with a print of the object ? 
        
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'scheduleCronTask'");
    }

    public void cleanupDataLake(){

    }
    
}
