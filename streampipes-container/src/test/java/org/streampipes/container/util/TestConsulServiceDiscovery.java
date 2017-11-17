package org.streampipes.container.util;

import com.mashape.unirest.http.exceptions.UnirestException;
import org.junit.Test;

import static org.streampipes.container.util.ConsulServiceDiscovery.getActivePEServicesRdfEndPoints;
import static org.streampipes.container.util.ConsulServiceDiscovery.subcribeHealthService;

public class TestConsulServiceDiscovery {

     public static void main(String[] args) throws UnirestException {
     /*   ConsulServiceDiscovery.registerPeService("t2",
                                                    "t2",
                                                        "http://141.21.14.94",
                                                        8090);
                                                        */
         //getActivePEServicesRdfEndPoints();
         subcribeHealthService();
         while(true) ;

     }
}
