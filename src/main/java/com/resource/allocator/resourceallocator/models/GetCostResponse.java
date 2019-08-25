package com.resource.allocator.resourceallocator.models;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("costs")
    private List<RegionalCost> regionalCosts;
}
