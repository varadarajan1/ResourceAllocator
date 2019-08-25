package com.resource.allocator.resourceallocator.service;

import com.resource.allocator.resourceallocator.models.RegionalCost;
import com.resource.allocator.resourceallocator.models.Server;
import com.resource.allocator.resourceallocator.models.ServerType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AllocationService {

    public RegionalCost getCosts(String region, Map<String, Float> serverCostsMap, Integer minCpus, Integer hours, Float maxCost) {

        List<Server> servers = getServers(serverCostsMap, hours);

//        maxCost = (maxCost == null) ? Integer.MIN_VALUE : maxCost;
        minCpus = (minCpus == null) ? Integer.MIN_VALUE : minCpus;

        Float cost = 0.0f;
        int allocatedCpus = 0;

        List<Map<String, Integer>> instances = new ArrayList<>();

        for (Server server :
                servers) {

            int serverCpus = server.getServerType().getNumberOfCpus();
            int serverInstances = (minCpus - allocatedCpus) / serverCpus;

            float serverCost = server.getCost();
//            int costOfInstances = (int)((maxCost - cost)/server.getCost());

            if (serverInstances > 0) {
                cost += serverInstances * serverCost;
                allocatedCpus += serverInstances * serverCpus;
                server.setAllocatedInstances(serverInstances);
                Map<String, Integer> serverInstanceMap = new HashMap<>();
                serverInstanceMap.put(server.getServerType().getName(), serverInstances);
                instances.add(serverInstanceMap);
            }

//            if(costOfInstances>0){
//                cost += costOfInstances * serverCost;
//                server.setAllocatedInstances(costOfInstances);
//                Map<String, Integer> serverInstanceMap = new HashMap<>();
//                serverInstanceMap.put(server.getServerType().getName(), costOfInstances);
//                instances.add(serverInstanceMap);
//            }
        }

        return RegionalCost.builder().region(region).totalCost(cost).serverTypes(instances).build();
    }

    /* Returns a list of Servers sorted in ascending order by their cost to core ration */
    private List<Server> getServers(Map<String, Float> serverCostsMap, int hours) {

        return serverCostsMap.keySet().stream()
                .filter(x -> ServerType.getByName(x) != null)
                .map(x -> Server.builder().costToCoreRatio((serverCostsMap.get(x) * hours) / ServerType.getByName(x).getNumberOfCpus())
                        .serverType(ServerType.getByName(x))
                        .cost(serverCostsMap.get(x) * hours)
                        .build())
                .sorted().collect(Collectors.toList());
    }
}
