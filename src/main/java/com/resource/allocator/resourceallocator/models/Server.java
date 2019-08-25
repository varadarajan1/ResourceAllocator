package com.resource.allocator.resourceallocator.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Server implements Comparable<Server> {
    private ServerType serverType;
    private Float costToCoreRatio;
    private Float cost;
    private int allocatedInstances;

    @Override
    public int compareTo(Server server) {
        return Float.compare(this.costToCoreRatio, server.costToCoreRatio);
    }
}
