package com.resource.allocator.resourceallocator.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Cost {
    private Integer totalNumberOfCpus;
    private Map<String,Integer> serverTypeWithCount;
    private Float totalCost;
}
