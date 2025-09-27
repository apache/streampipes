package org.apache.streampipes.manager.loadbalance;

import org.apache.streampipes.extensions.api.locker.impl.SpStateLocker;
import org.apache.streampipes.manager.execution.endpoint.ExtensionsServiceEndpointUtils;
import org.apache.streampipes.manager.health.ServiceRegistrationManager;
import org.apache.streampipes.manager.pipeline.PipelineManager;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;
import org.apache.streampipes.model.loadbalancer.ResourceUnit;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.storage.management.StorageDispatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class PipelineRuntimeData {

    static Map<String, List<ResourceUnit<InvocableStreamPipesEntity>>> sinksAndProcess;

    static Map<String, List<ResourceUnit<AdapterDescription>>> adapter;

    private static final ServiceRegistrationManager serviceManager = new ServiceRegistrationManager(
            StorageDispatcher.INSTANCE.getNoSqlStore().getExtensionsServiceStorage());

    static  {
        sinksAndProcess = new ConcurrentHashMap<>();
        adapter = new ConcurrentHashMap<>();
    }

    public static void loadAll(){
        loadSinkAndProcess();
        loadAdapter();
    }

    public static void loadSinkAndProcess() {
        List<SpServiceRegistration> services = serviceManager.getAllServices();
        if (services == null || services.isEmpty()) {
            // 没有服务，直接返回
            return;
        }
        List<Pipeline> pipelines = PipelineManager.getAllPipelines();
        for (SpServiceRegistration service : services) {
            String serviceId = service.getSvcId();
            String serviceUrl = service.getServiceUrl();

            // 找出使用该服务的 pipelineId
            List<String> pipelineIds = pipelines.stream()
                    .filter(pipeline -> {
                        boolean sepaUsesService = pipeline.getSepas() != null && pipeline.getSepas().stream()
                                .anyMatch(sepa -> matchesSelectedEndpoint(sepa, serviceUrl));
                        boolean actionUsesService = pipeline.getActions() != null && pipeline.getActions().stream()
                                .anyMatch(action -> matchesSelectedEndpoint(action, serviceUrl));
                        return sepaUsesService || actionUsesService;
                    })
                    .map(Pipeline::getPipelineId)
                    .toList();

            for (String pipelineId : pipelineIds) {
                SpStateLocker.INSTANCE.tryLock(pipelineId, TimeUnit.SECONDS);
                try{
                    Pipeline pipeline = PipelineManager.getPipeline(pipelineId);
                    if (pipeline == null) continue;

                    List<InvocableStreamPipesEntity> entities = new ArrayList<>();
                    if (pipeline.getSepas() != null) {
                        pipeline.getSepas().stream()
                                .filter(sepa -> matchesSelectedEndpoint(sepa, serviceUrl))
                                .forEach(entities::add);
                    }
                    if (pipeline.getActions() != null) {
                        pipeline.getActions().stream()
                                .filter(action -> matchesSelectedEndpoint(action, serviceUrl))
                                .forEach(entities::add);
                    }

                    if (!entities.isEmpty()) {
                        ResourceUnit<InvocableStreamPipesEntity> unit = new ResourceUnit<>();
                        unit.setPipelineId(pipelineId);
                        unit.setServiceId(serviceId);
                        entities.forEach(unit::addElements);
                        sinksAndProcess.computeIfAbsent(serviceId, k -> new ArrayList<>()).add(unit);
                    }
                } finally {
                    System.out.println(sinksAndProcess);
                    SpStateLocker.INSTANCE.unlock(pipelineId);
                }
            }
        }
    }

    public static void loadAdapter() {
        List<SpServiceRegistration> services = serviceManager.getAllServices();
        if (services == null || services.isEmpty()) {
            return;
        }
        for (SpServiceRegistration service : services) {
            String serviceId = service.getSvcId();
            var adapterStorage = StorageDispatcher.INSTANCE.getNoSqlStore().getAdapterDescriptionStorage();
            List<AdapterDescription> allAdapters = adapterStorage.findAll();
            String serviceUrl = service.getServiceUrl();
            List<AdapterDescription> matched = allAdapters.stream()
                    .filter(ad -> serviceUrl != null && serviceUrl.equals(ad.getSelectedEndpointUrl()))
                    .toList();
            for (AdapterDescription ad : matched) {
                String adapterId = ad.getElementId();
                SpStateLocker.INSTANCE.tryLock(adapterId, TimeUnit.SECONDS);
                try{
                    ResourceUnit<AdapterDescription> unit = new ResourceUnit<>();
                    unit.setPipelineId(adapterId);
                    unit.setServiceId(serviceId);
                    unit.addElements(ad);
                    adapter.computeIfAbsent(serviceId, k -> new ArrayList<>()).add(unit);
                } finally {
                    SpStateLocker.INSTANCE.unlock(adapterId);
                }
            }

        }
    }

        /*
        public static void addSinkAndProcess(ResourceUnit<InvocableStreamPipesEntity> unit, SpServiceRegistration registration) {
            if (sinksAndProcess.containsKey(unit.getPipelineId())) {
                List<ResourceUnit<InvocableStreamPipesEntity>> entities = sinksAndProcess.get(unit.getPipelineId());
                for (int i=0;i<sinksAndProcess.get(unit.getPipelineId()).size();){
                    if(entities.get(i).getId().equals(unit.getId())){
                        entities.remove(i);
                        break;
                    }else {
                        i++;
                    }
                }
                sinksAndProcess.get(unit.getPipelineId()).add(unit);
            } else {
                sinksAndProcess.put(unit.getPipelineId(), new ArrayList<>());
                sinksAndProcess.get(unit.getPipelineId()).add(unit);
            }
        }
         */

    public static void deleteSinkAndProcess(String serviceId) {
        sinksAndProcess.remove(serviceId);
    }

    public static void deleteAdapter(String serviceId) {
        adapter.remove(serviceId);
    }

    public static void clearAll(){
        sinksAndProcess.clear();
        adapter.clear();
    }

//    public  static void addAdapter(ResourceUnit<AdapterDescription> unit, SpServiceRegistration registration) {
//        if (adapter.containsKey(unit.getPipelineId())) {
//            List<ResourceUnit<AdapterDescription>> entities = adapter.get(registration.getSvcId());
//            for (int i=0;i<adapter.get(unit.getPipelineId()).size();){
//                if(entities.get(i).getPipelineId().equals(unit.getPipelineId())){
//                    entities.remove(i);
//                    break;
//                }else {
//                    i++;
//                }
//            }
//            adapter.get(unit.getPipelineId()).add(unit);
//        } else {
//            adapter.put(unit.getPipelineId(), new ArrayList<>());
//            adapter.get(unit.getPipelineId()).add(unit);
//        }
//    }

    public static Map<String, List<ResourceUnit<InvocableStreamPipesEntity>>> getSinksAndProcess() {
        return sinksAndProcess;
    }

    public static Map<String, List<ResourceUnit<AdapterDescription>>> getAdapter() {
        return adapter;
    }

    public static void removeServiceResourceUnit(String serviceId) {
        sinksAndProcess.remove(serviceId);
        adapter.remove(serviceId);
    }

//    public static List<ResourceUnit<InvocableStreamPipesEntity>> getServiceResourceUnit(String serviceId){
//        List<ResourceUnit<InvocableStreamPipesEntity>> resourceUnits = new ArrayList<>();
//        for(Map.Entry<String,List<ResourceUnit<InvocableStreamPipesEntity>>> entry : sinksAndProcess.entrySet()){
//            for(ResourceUnit<InvocableStreamPipesEntity> resourceUnit : entry.getValue()){
//                if(resourceUnit.getServiceId().equals(serviceId)) {
//                    resourceUnits.add(resourceUnit);
//                }
//            }
//        }
//        return resourceUnits;
//    }
//
//    public static void removeServiceResourceUnit(String serviceId){
//        for(Map.Entry<String,List<ResourceUnit<InvocableStreamPipesEntity>>> entry : sinksAndProcess.entrySet()){
//            List<ResourceUnit<InvocableStreamPipesEntity>> list = entry.getValue();
//            for(int i=0;i<list.size();){
//                if(list.get(i).getServiceId().equals(serviceId)){
//                    list.remove(i);
//                }else {
//                    i++;
//                }
//            }
//        }
//
//        for(Map.Entry<String,List<ResourceUnit<AdapterDescription>>> entry : adapter.entrySet()){
//            List<ResourceUnit<AdapterDescription>> list = entry.getValue();
//            for(int i=0;i<list.size();){
//                if(list.get(i).getServiceId().equals(serviceId)){
//                    list.remove(i);
//                }else {
//                    i++;
//                }
//            }
//        }
//    }

    public static List<ResourceUnit<AdapterDescription>> getServiceAdapter(String serviceId){
        List<ResourceUnit<AdapterDescription>> resourceUnits = new ArrayList<>();
        for(Map.Entry<String,List<ResourceUnit<AdapterDescription>>> entry : adapter.entrySet()){
            for(ResourceUnit<AdapterDescription> resourceUnit : entry.getValue()){
                if(resourceUnit.getServiceId().equals(serviceId)) {
                    resourceUnits.add(resourceUnit);
                }
            }
        }
        return resourceUnits;
    }


    private static boolean matchesSelectedEndpoint(InvocableStreamPipesEntity element, String serviceUrl) {
        if (serviceUrl == null || element == null) return false;
        String selected = element.getSelectedEndpointUrl();
        if (selected == null) return false;
        try {
            String expected = ExtensionsServiceEndpointUtils
                    .getPipelineElementType(element)
                    .getInvocationUrl(serviceUrl, element.getAppId());
            if (expected != null && expected.equals(selected)) {
                return true;
            }
        } catch (Exception ignored) {
        }
        return selected.startsWith(serviceUrl);
    }

}
