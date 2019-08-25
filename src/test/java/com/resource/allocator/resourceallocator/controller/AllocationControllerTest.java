package com.resource.allocator.resourceallocator.controller;

import com.resource.allocator.resourceallocator.models.Cost;
import com.resource.allocator.resourceallocator.models.GetCostRequest;
import com.resource.allocator.resourceallocator.models.GetCostResponse;
import com.resource.allocator.resourceallocator.service.AllocationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AllocationControllerTest {

    @Mock
    AllocationService mockAllocationService;

    @InjectMocks
    AllocationController controller;


    @Test
    public void shouldReturnResponseWhenRequestIsValid() {
        GetCostRequest request = getGetCostRequest();
        Map<String, Integer> serverInstanceMap = new HashMap() {
            {
                put("xLarge", 1);
                put("2xLarge", 2);
            }
        };
        Float usEastCost = 2.5f, usWestCost = 1.5f;

        when(mockAllocationService.getCosts(any(), any(), any(), any())).then(new Answer<Cost>() {
            int count = 0;

            @Override
            public Cost answer(InvocationOnMock invocation) throws Throwable {
                if (count == 0) {
                    count++;
                    return getCost(usEastCost, serverInstanceMap);
                }
                return getCost(usWestCost, serverInstanceMap);
            }
        });
        ResponseEntity<GetCostResponse> response = controller.getCosts(getGetCostRequest());
        GetCostResponse costResponse = response.getBody();
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertNotNull(costResponse);
        assertEquals(costResponse.getRegionalCosts().size(), 2);
        assertTrue(costResponse.getRegionalCosts().get(0).getRegion().equalsIgnoreCase("us-west"));
        assertEquals(costResponse.getRegionalCosts().get(0).getTotalCost(),usWestCost);

        assertTrue(costResponse.getRegionalCosts().get(1).getRegion().equalsIgnoreCase("us-east"));
        assertEquals(costResponse.getRegionalCosts().get(1).getTotalCost(),usEastCost);
    }

    @Test
    public void shouldReturnBadRequestExceptionWhenRequestIsMadeWithoutCostAndHours(){
        GetCostRequest request = getGetCostRequest();
        request.setCpus(null);
        request.setPrice(null);

        ResponseEntity<GetCostResponse> response = controller.getCosts(request);
        assertNotNull(response);
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    private GetCostRequest getGetCostRequest() {
        HashMap<String, Map<String, Float>> instances = new HashMap<>();
        instances.put("us-east", getUSEastMap());
        instances.put("us-west", getUSWestMap());
        return GetCostRequest.builder().cpus(89).hours(1).instances(instances).price(8f).build();
    }

    private Map<String, Float> getUSEastMap() {
        return new HashMap() {
            {
                put("large", 0.12f);
                put("xlarge", 0.23f);
                put("2xlarge", 0.45f);
                put("4xlarge", 0.774f);
                put("8xlarge", 1.4f);
            }
        };
    }

    private Map<String, Float> getUSWestMap() {
        return new HashMap() {
            {
                put("large", 0.24f);
                put("xlarge", 0.46f);
                put("2xlarge", 0.90f);
                put("4xlarge", 1.45f);
                put("8xlarge", 2.8f);
            }
        };
    }

    private Cost getCost(Float totalCost, Map<String, Integer> serverInstanceMap) {
        return Cost.builder().serverTypeWithCount(serverInstanceMap).totalCost(totalCost).build();
    }
}