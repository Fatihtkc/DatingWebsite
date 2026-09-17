package com.datingapp.backend.dto.Stats;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor 
public class UserStatsDTO {

    private Long totalUsers;

    private Double averageAge;

    private List<GenderDistributionDTO> genderDistribution;

    private List<RelationshipDistributionDTO> relationshipTypes;

    private AverageHeightWeightDTO averageHeightWeight;
}