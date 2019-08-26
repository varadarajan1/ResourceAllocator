package com.resource.allocator.resourceallocator.service;

import com.resource.allocator.resourceallocator.models.Cost;
import com.resource.allocator.resourceallocator.models.Server;
import com.resource.allocator.resourceallocator.models.ServerType;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AllocationService {

    public Cost getCosts(Map<String, Float> serverCostsMap, Integer minCpus, Float maxCost, Integer hours) {

        List<Server> servers = getServers(serverCostsMap, hours);
        if (maxCost != null && minCpus != null) {
            return getCosts(servers, minCpus, maxCost);
        } else if (minCpus != null)
            return getCosts(servers, minCpus);
        else if (maxCost != null)
            return getCosts(servers, maxCost);

        return new Cost(0, new HashMap<String, Integer>(), 0f);
    }

    private Cost getCosts(List<Server> servers, Integer minCpus) {
        Float totalCost = 0.0f;
        int allocatedCpus = 0;
        int minServerCpus = servers.stream().mapToInt(x -> x.getServerType().getNumberOfCpus()).min().orElse(0);
        Map<String, Integer> serverInstanceMap = new HashMap<>();
        for (Server server :
                servers) {
            float serverCost = server.getCost();
            int serverCpus = server.getServerType().getNumberOfCpus();

            if (allocatedCpus >= minCpus)
                break;

            /* Get Server instances required By Number Of Cpus required*/
            int cpusToBeAllocated = minCpus - allocatedCpus;

            int serverInstancesByCpu = getServerInstancesByCpu(cpusToBeAllocated, serverCpus);

            if (cpusToBeAllocated < minServerCpus && server.getServerType().getNumberOfCpus() == minServerCpus)
                serverInstancesByCpu = 1;

            if (serverInstancesByCpu > 0) {
                totalCost += serverInstancesByCpu * serverCost;
                allocatedCpus += serverInstancesByCpu * serverCpus;
                server.setAllocatedInstances(serverInstancesByCpu);
                serverInstanceMap.put(server.getServerType().getName(), serverInstancesByCpu);
            }
        }

        return Cost.builder().totalCost(totalCost).totalNumberOfCpus(allocatedCpus).serverTypeWithCount(serverInstanceMap).build();
    }

    private Cost getCosts(List<Server> servers, Float maxCost) {
        Float totalCost = 0.0f;
        int allocatedCpus = 0;
        Map<String, Integer> serverInstanceMap = new HashMap<>();
        for (Server server :
                servers) {
            float serverCost = server.getCost();
            int serverCpus = server.getServerType().getNumberOfCpus();

            if (maxCost <= totalCost)
                break;

            /* Get Server instances required By max cost required*/
            int serverInstancesByCost = getInstancesByCost(maxCost, totalCost, server);

            if (serverInstancesByCost > 0) {
                totalCost += serverInstancesByCost * serverCost;
                allocatedCpus += serverInstancesByCost * serverCpus;
                server.setAllocatedInstances(serverInstancesByCost);
                serverInstanceMap.put(server.getServerType().getName(), serverInstancesByCost);
            }
        }
        return Cost.builder().totalCost(totalCost).totalNumberOfCpus(allocatedCpus).serverTypeWithCount(serverInstanceMap).build();
    }

    private Cost getCosts(List<Server> servers, Integer minCpus, Float maxCost) {
        Cost cost = getCosts(servers, maxCost);
        if (cost.getTotalNumberOfCpus() < minCpus)
            return new Cost(0, new HashMap<String, Integer>(), 0f);
        return cost;
    }

    private int getInstancesByCost(Float maxCost, Float cost, Server server) {
        return (int) ((maxCost - cost) / server.getCost());
    }

    private int getServerInstancesByCpu(Integer cpusToBeAllocated, int serverCpus) {
        return (cpusToBeAllocated) / serverCpus;
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
