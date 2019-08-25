package com.resource.allocator.resourceallocator.controller;

import com.resource.allocator.resourceallocator.models.Cost;
import com.resource.allocator.resourceallocator.models.GetCostRequest;
import com.resource.allocator.resourceallocator.models.GetCostResponse;
import com.resource.allocator.resourceallocator.models.RegionalCost;
import com.resource.allocator.resourceallocator.service.AllocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/allocations")
public class AllocationController {

    private AllocationService allocationService;

    @Autowired
    public AllocationController(AllocationService allocationService){
        this.allocationService = allocationService;
    }

    @PostMapping("/costs")
    public ResponseEntity<GetCostResponse> getCosts(@RequestBody GetCostRequest getCostRequest){
        if(getCostRequest.getPrice() == null && getCostRequest.getCpus() == null){
            return ResponseEntity.badRequest().build();
        }
        List<RegionalCost> regionalCosts = getServerCosts(getCostRequest);
        return ResponseEntity.ok(GetCostResponse.builder().regionalCosts(regionalCosts).build());
    }

    private List<RegionalCost> getServerCosts(GetCostRequest getCostRequest) {
        return getCostRequest.getInstances().entrySet().stream()
                .map(region ->
                        this.mapCostToRegionalCost(
                                this.allocationService.getCosts(region.getValue(), getCostRequest.getCpus(), getCostRequest.getPrice(), getCostRequest.getHours()),
                        region.getKey()))
                .sorted().collect(Collectors.toList());
    }

    private RegionalCost mapCostToRegionalCost(Cost cost, String region){
        return RegionalCost.builder()
                .region(region)
                .totalCost(cost.getTotalCost())
                .serverTypes(cost.getServerTypeWithCount().entrySet())
                .build();
    }
}