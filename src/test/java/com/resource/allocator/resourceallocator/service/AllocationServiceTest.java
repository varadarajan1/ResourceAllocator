package com.resource.allocator.resourceallocator.service;

import com.resource.allocator.resourceallocator.models.Cost;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class AllocationServiceTest {

    private AllocationService service = new AllocationService();

    @Test
    public void whenRequestHasNumberOfCpus_AndHoursWithOneServerType() {
        Map<String, Float> serverCostsMap = new HashMap() {
            {
                put("large", 0.12f);
            }
        };

        int cpus = 1, hours = 1;

        Cost cost = service.getCosts(serverCostsMap, cpus, null, hours);

        assertNotNull(cost);
        assertNotNull(cost.getServerTypeWithCount());
        assertNotNull(cost.getTotalCost());
        assertEquals(serverCostsMap.get("large"), cost.getTotalCost());
        assertTrue(cost.getServerTypeWithCount().containsKey("large"));
        assertTrue(cost.getServerTypeWithCount().get("large").equals(1));
    }


    @Test
    public void whenRequestHasUnknownServerType_shouldIgnoreUnknownServer() {
        Map<String, Float> serverCostsMap = new HashMap() {
            {
                put("large", 0.12f);
                put("dummy",1.2);
            }
        };

        int cpus = 1, hours = 1;

        Cost cost = service.getCosts(serverCostsMap, cpus, null, hours);

        assertNotNull(cost);
        assertNotNull(cost.getServerTypeWithCount());
        assertNotNull(cost.getTotalCost());
        assertEquals(serverCostsMap.get("large"), cost.getTotalCost());
        assertTrue(cost.getServerTypeWithCount().containsKey("large"));
        assertTrue(cost.getServerTypeWithCount().get("large").equals(1));
    }

    @Test
    public void whenServerWithMinimumNumberOfCpusIsNotAvailable_shouldReturnNextServerWithMinimumCpus() {
        Map<String, Float> serverCostsMap = new HashMap() {
            {
                put("8xLarge", 0.12f);
                put("16xLarge",0.44f);
            }
        };

        int cpus = 2, hours = 1;

        Cost cost = service.getCosts(serverCostsMap, cpus, null, hours);

        assertNotNull(cost);
        assertNotNull(cost.getServerTypeWithCount());
        assertNotNull(cost.getTotalCost());
        assertEquals(serverCostsMap.get("8xLarge"), cost.getTotalCost());
        assertTrue(cost.getServerTypeWithCount().containsKey("8xLarge"));
        assertTrue(cost.getServerTypeWithCount().get("8xLarge").equals(1));
    }

    @Test
    public void whenRequestHasNumberOfCpus_AndHoursWithMultipleServerType() {
        Map<String, Float> serverCostsMap = new HashMap() {
            {
                put("large", 0.12f);
                put("xlarge", 0.23f);
                put("2xlarge", 0.45f);
                put("4xlarge", 0.774f);
                put("8xlarge", 1.4f);
            }
        };

        int cpus = 89, hours = 1;
        Float expectedCost = 7.894f; // 5 of 8x, 1 of 4x and 1 of large

        Cost cost = service.getCosts(serverCostsMap, cpus, null, hours);

        assertNotNull(cost);
        assertNotNull(cost.getServerTypeWithCount());
        assertNotNull(cost.getTotalCost());
        assertEquals(expectedCost, cost.getTotalCost());
        assertTrue(cost.getServerTypeWithCount().containsKey("large"));
        assertTrue(cost.getServerTypeWithCount().get("large").equals(1));
        assertTrue(cost.getServerTypeWithCount().containsKey("4xLarge"));
        assertTrue(cost.getServerTypeWithCount().get("4xLarge").equals(1));
        assertTrue(cost.getServerTypeWithCount().containsKey("8xLarge"));
        assertTrue(cost.getServerTypeWithCount().get("8xLarge").equals(5));
    }

    @Test
    public void whenRequestContainsMaxCostAndHoursWithOneServerType() {
        Map<String, Float> serverCostsMap = new HashMap() {
            {
                put("large", 0.12f);
            }
        };

        int hours = 4;
        float maxCost = 15.00f;
        int expectedCount = (int) (15 / (0.12 * 4));

        Cost cost = service.getCosts(serverCostsMap, null, maxCost, hours);

        assertNotNull(cost);
        assertNotNull(cost.getServerTypeWithCount());
        assertNotNull(cost.getTotalCost());
        assertTrue(serverCostsMap.get("large") <= maxCost);
        assertTrue(cost.getServerTypeWithCount().containsKey("large"));
        assertTrue(cost.getServerTypeWithCount().get("large").equals(expectedCount));
    }

    @Test
    public void whenRequestContainsMaxCostAndHours_WithMultipleServerTypes() {
        /*Same data as the test case with Min CPU and hours*/
        Map<String, Float> serverCostsMap = new HashMap() {
            {
                put("large", 0.12f);
                put("xlarge", 0.23f);
                put("2xlarge", 0.45f);
                put("4xlarge", 0.774f);
                put("8xlarge", 1.4f);
            }
        };
        int hours = 1;
        float maxCost = 8f;
        Integer expectedCpu = 89;

        Cost cost = service.getCosts(serverCostsMap, null, maxCost, hours);

        assertNotNull(cost);
        assertTrue(cost.getTotalCost()<=maxCost);
        assertEquals(cost.getTotalNumberOfCpus(), expectedCpu);
        assertTrue(cost.getServerTypeWithCount().containsKey("large"));
        assertTrue(cost.getServerTypeWithCount().get("large").equals(1));
        assertTrue(cost.getServerTypeWithCount().containsKey("4xLarge"));
        assertTrue(cost.getServerTypeWithCount().get("4xLarge").equals(1));
        assertTrue(cost.getServerTypeWithCount().containsKey("8xLarge"));
        assertTrue(cost.getServerTypeWithCount().get("8xLarge").equals(5));
    }

    @Test
    public void whenRequestContainsMaxCostAndHours_AndNoneOfTheServersAreCheapEnough() {
        Map<String, Float> serverCostsMap = new HashMap() {
            {
                put("large", 10f);
            }
        };

        int hours = 4;
        float maxCost = 15.00f;
        Float expectedCost = 0f;
        Integer expectedCpu = 0;

        Cost cost = service.getCosts(serverCostsMap, null, maxCost, hours);

        assertNotNull(cost);
        assertEquals(cost.getTotalCost(), expectedCost);
        assertEquals(cost.getTotalNumberOfCpus(), expectedCpu);
    }

    @Test
    public void whenRequestContainsMaxCost_AndMinCpus_AndHours_WithOneServerType() {
        Map<String, Float> serverCostsMap = new HashMap() {
            {
                put("large", 0.12f);
            }
        };

        int hours = 4;
        float maxCost = 15.00f;
        int min = 16;

        Cost cost = service.getCosts(serverCostsMap, min, maxCost, hours);

        assertNotNull(cost);
        assertNotNull(cost.getServerTypeWithCount());
        assertNotNull(cost.getTotalCost());
        assertTrue(cost.getTotalCost() <= maxCost);
        assertTrue(cost.getServerTypeWithCount().containsKey("large"));
        assertTrue(cost.getServerTypeWithCount().get("large") >= min);
    }

    @Test
    public void whenRequestContainsMaxCost_AndMinCpus_AndHours_WithMultipleServerTypes() {
        Map<String, Float> serverCostsMap = new HashMap() {
            {
                put("large", 0.12f);
                put("xlarge", 0.23f);
                put("2xlarge", 0.45f);
                put("4xlarge", 0.774f);
                put("8xlarge", 1.4f);
            }
        };
        int hours = 1;
        float maxCost = 8f;
        Integer min = 89;

        Cost cost = service.getCosts(serverCostsMap, min, maxCost, hours);

        assertNotNull(cost);
        assertNotNull(cost.getServerTypeWithCount());
        assertNotNull(cost.getTotalCost());
        assertTrue(cost.getTotalCost() <= maxCost);
        assertTrue(cost.getServerTypeWithCount().containsKey("large"));
        assertTrue(cost.getServerTypeWithCount().get("large").equals(1));
        assertTrue(cost.getServerTypeWithCount().containsKey("4xLarge"));
        assertTrue(cost.getServerTypeWithCount().get("4xLarge").equals(1));
        assertTrue(cost.getServerTypeWithCount().containsKey("8xLarge"));
        assertTrue(cost.getServerTypeWithCount().get("8xLarge").equals(5));
    }

    @Test
    public void whenRequestContainsMaxCost_AndMinCpus_AndHours_AndRequiredNumberOfServersIsNotAvailableWithinTheCost() {
        Map<String, Float> serverCostsMap = new HashMap() {
            {
                put("large", 15f);
            }
        };

        int hours = 4;
        float maxCost = 15.00f;
        Float expectedCost = 0f;
        Integer expectedCpu = 0;
        int min = 16;

        Cost cost = service.getCosts(serverCostsMap, min, maxCost, hours);

        assertNotNull(cost);
        assertEquals(cost.getTotalCost(), expectedCost);
        assertEquals(cost.getTotalNumberOfCpus(), expectedCpu);
    }

    @Test
    public void whenRequestContainsOnylHoursWithServerType() {
        Map<String, Float> serverCostsMap = new HashMap() {
            {
                put("large", 15f);
            }
        };

        int hours = 4;
        Float expectedCost = 0f;
        Integer expectedCpu = 0;

        Cost cost = service.getCosts(serverCostsMap, null, null, hours);

        assertNotNull(cost);
        assertEquals(cost.getTotalCost(), expectedCost);
        assertEquals(cost.getTotalNumberOfCpus(), expectedCpu);
    }

    @Test
    public void whenRequestHasNoServers(){
        Map<String, Float> serverCostsMap = new HashMap<>();

        int hours = 4;
        Float expectedCost = 0f;
        Integer expectedCpu = 0;

        Cost cost = service.getCosts(serverCostsMap,2, null, hours);

        assertNotNull(cost);
        assertEquals(cost.getTotalCost(), expectedCost);
        assertEquals(cost.getTotalNumberOfCpus(), expectedCpu);
    }

}