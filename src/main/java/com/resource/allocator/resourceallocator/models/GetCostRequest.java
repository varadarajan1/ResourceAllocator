package com.resource.allocator.resourceallocator.models;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Map;

@Data
@Builder
public class GetCostRequest {
    @NotEmpty
    @NotNull
    private Map<String, Map<String, Float>> instances;
    @NotNull
    private Integer hours;
    private Integer cpus;
    private Float price;
}
