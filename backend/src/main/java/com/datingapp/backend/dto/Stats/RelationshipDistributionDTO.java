package com.datingapp.backend.dto.Stats;

import com.datingapp.backend.enums.RelationshipType;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RelationshipDistributionDTO {

    private RelationshipType relationshipType;
    private Long count;
}