package com.resource.allocator.resourceallocator.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegionalCost implements Comparable<RegionalCost>{
    private String region;
    @Getter(AccessLevel.NONE)
    @JsonProperty("total_cost")
    private String totalCostInDollars;
    @JsonIgnore
    private Float totalCost;
    private @Singular
    List<Map<String, Integer>> serverTypes;

    @Override
    public int compareTo(RegionalCost regionalCost) {
        return Float.compare(this.totalCost, regionalCost.totalCost);
    }

    public String getTotalCostInDollars(){
        return String.format("$%.02f",this.totalCost);
    }
}
