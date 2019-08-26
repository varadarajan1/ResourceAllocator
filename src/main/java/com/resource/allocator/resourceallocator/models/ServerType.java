package com.resource.allocator.resourceallocator.models;

import java.util.Arrays;

public enum ServerType {
    LARGE("large", 1),
    xLARGE("xlarge", 2),
    x2_LARGE("2xLarge", 4),
    x4_LARGE("4xLarge", 8),
    x8_LARGE("8xLarge", 16),
    x10_LARGE("10xLarge", 32);


    private String name;
    private Integer cpu;

    ServerType(String name, Integer cpu) {
        this.name = name;
        this.cpu = cpu;
    }

    public Integer getNumberOfCpus() {
        return this.cpu;
    }

    public String getName() {
        return this.name;
    }

    public static ServerType getByName(String name) {
        return Arrays.stream(ServerType.values()).filter(x -> x.name.equalsIgnoreCase(name)).findFirst().orElse(null);
    }
}
