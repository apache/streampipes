package org.apache.streampipes.manager.loadbalance;

import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceStatus;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.loadbalancer.ResourceUnit;
import org.apache.streampipes.storage.api.CRUDStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;
import org.apache.streampipes.svcdiscovery.SpServiceDiscovery;
import org.apache.streampipes.svcdiscovery.api.model.SpServiceUrlProvider;

import java.util.*;
import java.util.stream.Collectors;

public class    ResourceUnitGenerator {


    private static CRUDStorage<SpServiceRegistration> serviceStorage;

    static {
        serviceStorage = StorageDispatcher.INSTANCE.getNoSqlStore().getExtensionsServiceStorage();
    }

    public static Map<ResourceUnit<AdapterDescription>, List<SpServiceRegistration>> unitGeneration(AdapterDescription adapterDescription){
        ResourceUnit<AdapterDescription> resourceUnit =new ResourceUnit<>();
        resourceUnit.addElements(adapterDescription);
        resourceUnit.setLabels(new ArrayList<>());
        resourceUnit.setPipelineId(adapterDescription.getElementId());
        List<SpServiceRegistration> serviceRegistrations = SpServiceDiscovery.getServiceDiscovery().findAll();
        List<SpServiceRegistration> list =getService(SpServiceUrlProvider
                .ADAPTER
                .getServiceTag(adapterDescription.getAppId())
                .asString(), serviceRegistrations);

        Map<ResourceUnit<AdapterDescription>,List<SpServiceRegistration>> map =new HashMap<>();
        map.put(resourceUnit,list);
        return map;
    }


    public static Map<ResourceUnit<InvocableStreamPipesEntity>, List<SpServiceRegistration>> unitGeneration (
            List<DataSinkInvocation> sinks, List<DataProcessorInvocation> processor) {
        List<SpServiceRegistration> serviceRegistrations = SpServiceDiscovery.getServiceDiscovery().findAll();
        Map<String, List<SpServiceRegistration>> entityRunnableService = new HashMap<>();
        Map<String, String> resourceUnitMap = new HashMap<>();
        Map<String, InvocableStreamPipesEntity> entityMap = new HashMap<>();

        init(sinks, processor, entityRunnableService, resourceUnitMap, entityMap, serviceRegistrations);

        for (InvocableStreamPipesEntity sink : sinks) {
            for (String d : sink.getConnectedTo()) {
                update(resourceUnitMap, entityRunnableService, entityMap.get(d), sink, entityMap);
            }
        }

        Map<String,ResourceUnit<InvocableStreamPipesEntity>> resourceUnitMap1=get(resourceUnitMap,entityMap);

        return resourceUnitMap1.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> resourceUnitMap1.get(entry.getKey()),
                        entry -> entityRunnableService.get(entry.getKey())
                ));
    }


    public static List<SpServiceRegistration> getService(String tag, List<SpServiceRegistration> activeServices) {
        return activeServices
                .stream()
                .filter(service -> filtersSupported(service, tag))
                .filter(service -> service.getStatus() != SpServiceStatus.UNHEALTHY)
                .collect(Collectors.toList());
    }

    private static boolean filtersSupported(SpServiceRegistration service,
                                            String tag) {
        return new HashSet<>(service.getTags())
                .stream()
                .anyMatch(t -> t.asString().equals(tag));
    }

    public static boolean areListsEqual(List<SpServiceRegistration> list1, List<SpServiceRegistration> list2) {
        if (list1.size() != list2.size()) {
            return false;
        }

        for (SpServiceRegistration registration : list1) {
            if (!contains(list1,registration)) {
                return false;
            }
        }

        return true;
    }

    public static boolean contains(List<SpServiceRegistration> list, SpServiceRegistration item) {
        for (SpServiceRegistration element : list) {
            if (item.getSvcId().equals(element.getSvcId())) {
                return true;
            }
        }
        return false;
    }

    private static void update(Map<String, String> map, Map<String, List<SpServiceRegistration>> se
            , InvocableStreamPipesEntity dom, InvocableStreamPipesEntity needUpdateDom
            , Map<String, InvocableStreamPipesEntity> entityMap) {
        if (dom==null||!se.containsKey(dom.getDom())) {
            return;
        }
        if (areListsEqual(se.get(dom.getDom()), se.get(needUpdateDom.getDom()))) {
            union(map, dom.getDom(), needUpdateDom.getDom());
        }else {
            map.put(dom.getDom(),dom.getDom());
        }

        for (String d : dom.getConnectedTo()) {
            update(map, se, entityMap.get(d), dom, entityMap);
        }

    }

    private static void init(List<DataSinkInvocation> sinks
            , List<DataProcessorInvocation> processor
            , Map<String, List<SpServiceRegistration>> entityRunnableService
            , Map<String, String> resourceUnitMap
            , Map<String, InvocableStreamPipesEntity> entityMap
            , List<SpServiceRegistration> serviceRegistrations) {

        for (InvocableStreamPipesEntity sink : sinks) {
            entityMap.put(sink.getDom(), sink);
            resourceUnitMap.put(sink.getDom(), sink.getDom());
            entityRunnableService.put(sink.getDom(),
                    getService(SpServiceUrlProvider
                            .DATA_SINK
                            .getServiceTag(sink.getAppId())
                            .asString(), serviceRegistrations));
        }


        for (InvocableStreamPipesEntity p : processor) {
            entityMap.put(p.getDom(), p);
            entityRunnableService.put(p.getDom(),
                    getService(SpServiceUrlProvider
                            .DATA_PROCESSOR
                            .getServiceTag(p.getAppId())
                            .asString(), serviceRegistrations));
        }

        for (InvocableStreamPipesEntity sink : sinks) {
            for (String con : sink.getConnectedTo()) {
                if (entityMap.containsKey(con)) {
                    resourceUnitMap.put(con, sink.getDom());
                }
            }
        }

        for (InvocableStreamPipesEntity p : processor) {
            for (String con : p.getConnectedTo()) {
                if (entityMap.containsKey(con)) {
                    resourceUnitMap.put(con, p.getDom());
                }
            }
        }
    }

    private static  Map<String, ResourceUnit<InvocableStreamPipesEntity>> get(Map<String, String> map, Map<String, InvocableStreamPipesEntity> entityMap) {
        Map<String, ResourceUnit<InvocableStreamPipesEntity>> resourceUnitMap = new HashMap<>();
        for (Map.Entry<String, String> e : map.entrySet()) {
            String s = find(map, e.getKey());
            if (resourceUnitMap.containsKey(s)) {
                resourceUnitMap.get(s).addElements(entityMap.get(e.getKey()));
            } else {
                ResourceUnit<InvocableStreamPipesEntity> resourceUnit = new ResourceUnit<>();
                resourceUnit.addElements(entityMap.get(e.getKey()));
                resourceUnitMap.put(s, resourceUnit);
            }
        }
        return resourceUnitMap;
    }

    private static String find(Map<String, String> map, String s) {
        if (map.get(s).equals(s)) {
            return s;
        }
        return find(map, map.get(s));
    }


    private static void union(Map<String, String> map, String d1, String d2) {
        String s1 = find(map, d1);
        String s2 = find(map, d2);
        map.put(s1, s2);
    }
}
