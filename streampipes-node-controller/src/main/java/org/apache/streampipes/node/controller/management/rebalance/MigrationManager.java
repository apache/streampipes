package org.apache.streampipes.node.controller.management.rebalance;

import org.apache.http.client.fluent.Request;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.node.controller.management.pe.InvocableElementManager;
import org.apache.streampipes.node.controller.management.pe.storage.RunningInvocableInstances;

import java.util.List;
import java.util.Random;

public class MigrationManager {

    public static boolean migrateRandomPE(){
        List<InvocableStreamPipesEntity> instances = RunningInvocableInstances.INSTANCE.getAll();
        if(instances.size() == 0)
            return false;
        InvocableStreamPipesEntity randomlySelectedInstance = instances.get(new Random().nextInt(instances.size()));
        InvocableElementManager.getInstance().postMigrationRequest(randomlySelectedInstance);
        return true;
    }

    public static void migrateHeaviestPE(){
        //TODO: Migrate the PE with the highest load on CPU (or possibly other resource)
    }

}
