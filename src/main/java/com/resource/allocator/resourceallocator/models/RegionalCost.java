package com.resource.allocator.resourceallocator.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({"region","total_cost","servers"})
public class RegionalCost implements Comparable<RegionalCost>{
    private String region;
    @Getter(AccessLevel.NONE)
    @JsonProperty("total_cost")
    private String totalCostInDollars;
    @JsonIgnore
    private Float totalCost;
    @Singular
    @JsonProperty("servers")
    private List<Map.Entry<String, Integer>> serverTypes;

    @Override
    public int compareTo(RegionalCost regionalCost) {
        return Float.compare(this.totalCost, regionalCost.totalCost);
    }

    public String getTotalCostInDollars(){
        return String.format("$%.02f",this.totalCost);
    }
}
