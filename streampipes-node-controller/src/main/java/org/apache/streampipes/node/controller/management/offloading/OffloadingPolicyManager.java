package org.apache.streampipes.node.controller.management.offloading;

import org.apache.streampipes.node.controller.management.offloading.model.OffloadingStrategy;
import org.apache.streampipes.node.controller.management.pe.InvocableElementManager;
import org.apache.streampipes.node.controller.management.resource.model.ResourceMetrics;

import java.util.ArrayList;
import java.util.List;

public class OffloadingPolicyManager {

    private final List<OffloadingStrategy<?>> offloadingStrategies = new ArrayList<>();
    private static OffloadingPolicyManager instance;

    public static OffloadingPolicyManager getInstance(){
        if(instance == null){
            instance = new OffloadingPolicyManager();
        }
        return instance;
    }

    public void checkPolicies(ResourceMetrics rm){
        for(OffloadingStrategy strategy:offloadingStrategies){
            strategy.getOffloadingPolicy().addValue(strategy.getResourceProperty().getProperty(rm));
            if(strategy.getOffloadingPolicy().isViolated()){
                InvocableElementManager.getInstance().postOffloadRequest(strategy.getSelectionStrategy().selectEntity());
            }
        }
    }

    public void addOffloadingStrategy(OffloadingStrategy<?> offloadingStrategy){
        this.offloadingStrategies.add(offloadingStrategy);
    }

}
