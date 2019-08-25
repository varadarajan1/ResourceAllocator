package com.resource.allocator.resourceallocator.service;

import com.resource.allocator.resourceallocator.models.RegionalCost;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class AllocationServiceTest {

    private AllocationService service = new AllocationService();

    @Test
    public void whenRequestHasNumberOfCpus_AndHours() {
        Map<String, Float> serverCostsMap = new HashMap() {
            {
                put("large", 0.12f);
            }
        };

        int cpus = 1, hours = 1;

        RegionalCost cost = service.getCosts("region", serverCostsMap, cpus, hours, null);

        assertNotNull(cost);
        assertNotNull(cost.getRegion());
        assertNotNull(cost.getServerTypes());
        assertNotNull(cost.getTotalCostInDollars());
        assertEquals(serverCostsMap.get("large"),cost.getTotalCost());
        assertTrue(cost.getServerTypes().get(0).containsKey("large"));
        assertTrue(cost.getServerTypes().get(0).get("large").equals(1));
    }
}