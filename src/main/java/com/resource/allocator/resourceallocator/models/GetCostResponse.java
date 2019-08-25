package com.resource.allocator.resourceallocator.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetCostResponse {
    private List<RegionalCost> regionalCosts;
}
