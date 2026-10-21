package com.datingapp.backend.records;

public record BoundingBox(
        double minLatitude,
        double maxLatitude,
        double minLongitude,
        double maxLongitude,
        boolean crossesAntimeridian,
        boolean allLongitudes
){}