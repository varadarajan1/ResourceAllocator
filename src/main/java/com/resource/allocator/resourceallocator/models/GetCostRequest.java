package com.resource.allocator.resourceallocator.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetCostRequest {
    @NotEmpty
    @NotNull
    private Map<String, Map<String, Float>> instances;
    @NotNull
    private Integer hours;
    private Integer cpus;
    private Float price;
}
