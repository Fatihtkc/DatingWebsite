package com.datingapp.backend.dto.Stats;

import com.datingapp.backend.enums.RelationshipType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RelationshipDistributionDTO {

    private RelationshipType relationshipType;
    private Long count;
}