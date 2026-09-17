package com.datingapp.backend.dto.Stats;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AverageHeightWeightDTO {

    private Double averageHeight;
    private Double averageWeight;
}