package com.datingapp.backend.repository;

import com.datingapp.backend.dto.Stats.GenderDistributionDTO;
import com.datingapp.backend.dto.Stats.RelationshipDistributionDTO;
import com.datingapp.backend.enums.RelationshipType;
import com.datingapp.backend.model.User;
import com.datingapp.backend.projection.NearbyUserProjection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE LOWER(CONCAT(u.firstName, ' ', u.lastName)) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<User> searchByFullName(@Param("name") String name, Pageable page);

    List<User> findByBirthDateBetween(java.time.LocalDate startDate, java.time.LocalDate endDate);

    List<User> findByLocationContainingIgnoreCase(String location);
    
    List<User> findByRelationshipType(RelationshipType relationshipType);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    Page<User> findByApprovedFalse(Pageable pageable);

    Page<User> findByApprovedTrueAndBannedFalse(Pageable pageable);

    @Query("SELECT COUNT(u) FROM User u")
    Long countAllUsers();

    @Query("SELECT new com.datingapp.backend.dto.Stats.GenderDistributionDTO( u.gender, COUNT(u) ) FROM User u GROUP BY u.gender")
    List<GenderDistributionDTO> genderDistribution();

    @Query("SELECT AVG(YEAR(CURRENT_DATE) - YEAR(u.birthDate)) FROM User u WHERE u.birthDate IS NOT NULL")
    Double averageAge();

    @Query("SELECT new com.datingapp.backend.dto.Stats.RelationshipDistributionDTO( u.relationshipType, COUNT(u) ) FROM User u GROUP BY u.relationshipType")
    List<RelationshipDistributionDTO> relationshipTypes();

    @Query(
        value = """
            SELECT
                u.id AS id,
                u.username AS username,
                u.first_name AS firstName,
                u.last_name AS lastName,
                u.birth_date AS birthDate,
                u.gender AS gender,
                u.body_type AS bodyType,
                u.relationship_type AS relationshipType,
                u.smoke AS smoke,
                u.alcohol AS alcohol,
                u.diet AS diet,
                u.shorter_bio AS shorterBio,

                ST_Distance_Sphere(
                    u.location_point,
                    ST_SRID(
                        POINT(:longitude, :latitude),
                        4326
                    )
                ) / 1000.0 AS distance

            FROM users u

            WHERE u.approved = TRUE
            AND u.banned = FALSE
            AND u.location_point IS NOT NULL
            AND u.id <> :requesterId

            AND (
                :allLongitudes = TRUE
                OR (
                    :crossesAntimeridian = FALSE
                    AND MBRContains(
                        ST_GeomFromText(
                            CONCAT(
                                'POLYGON((',
                                :minLongitude, ' ', :minLatitude, ',',
                                :maxLongitude, ' ', :minLatitude, ',',
                                :maxLongitude, ' ', :maxLatitude, ',',
                                :minLongitude, ' ', :maxLatitude, ',',
                                :minLongitude, ' ', :minLatitude,
                                '))'
                            ),
                            4326,
                            'axis-order=long-lat'
                        ),
                        u.location_point
                    )
                )
                OR (
                    :crossesAntimeridian = TRUE
                    AND (
                        MBRContains(
                            ST_GeomFromText(
                                CONCAT(
                                    'POLYGON((',
                                    :minLongitude, ' ', :minLatitude, ',',
                                    '180 ', :minLatitude, ',',
                                    '180 ', :maxLatitude, ',',
                                    :minLongitude, ' ', :maxLatitude, ',',
                                    :minLongitude, ' ', :minLatitude,
                                    '))'
                                ),
                                4326,
                                'axis-order=long-lat'
                            ),
                            u.location_point
                        )
                        OR
                        MBRContains(
                            ST_GeomFromText(
                                CONCAT(
                                    'POLYGON((',
                                    '-180 ', :minLatitude, ',',
                                    :maxLongitude, ' ', :minLatitude, ',',
                                    :maxLongitude, ' ', :maxLatitude, ',',
                                    '-180 ', :maxLatitude, ',',
                                    '-180 ', :minLatitude,
                                    '))'
                                ),
                                4326,
                                'axis-order=long-lat'
                            ),
                            u.location_point
                        )
                    )
                )
            )

            AND NOT EXISTS (
                SELECT 1
                FROM user_blocks ub
                WHERE
                    (ub.blocker_id = :requesterId
                    AND ub.blocked_id = u.id)
                    OR
                    (ub.blocker_id = u.id
                    AND ub.blocked_id = :requesterId)
            )

            AND ST_Distance_Sphere(
                u.location_point,
                ST_SRID(
                    POINT(:longitude, :latitude),
                    4326
                )
            ) / 1000.0 <= :distance

            ORDER BY distance ASC
            """,

        countQuery = """
            SELECT COUNT(*)
            FROM users u

            WHERE u.approved = TRUE
            AND u.banned = FALSE
            AND u.location_point IS NOT NULL
            AND u.id <> :requesterId

            AND (
                :allLongitudes = TRUE
                OR (
                    :crossesAntimeridian = FALSE
                    AND MBRContains(
                        ST_GeomFromText(
                            CONCAT(
                                'POLYGON((',
                                :minLongitude, ' ', :minLatitude, ',',
                                :maxLongitude, ' ', :minLatitude, ',',
                                :maxLongitude, ' ', :maxLatitude, ',',
                                :minLongitude, ' ', :maxLatitude, ',',
                                :minLongitude, ' ', :minLatitude,
                                '))'
                            ),
                            4326,
                            'axis-order=long-lat'
                        ),
                        u.location_point
                    )
                )
                OR (
                    :crossesAntimeridian = TRUE
                    AND (
                        MBRContains(
                            ST_GeomFromText(
                                CONCAT(
                                    'POLYGON((',
                                    :minLongitude, ' ', :minLatitude, ',',
                                    '180 ', :minLatitude, ',',
                                    '180 ', :maxLatitude, ',',
                                    :minLongitude, ' ', :maxLatitude, ',',
                                    :minLongitude, ' ', :minLatitude,
                                    '))'
                                ),
                                4326,
                                'axis-order=long-lat'
                            ),
                            u.location_point
                        )
                        OR
                        MBRContains(
                            ST_GeomFromText(
                                CONCAT(
                                    'POLYGON((',
                                    '-180 ', :minLatitude, ',',
                                    :maxLongitude, ' ', :minLatitude, ',',
                                    :maxLongitude, ' ', :maxLatitude, ',',
                                    '-180 ', :maxLatitude, ',',
                                    '-180 ', :minLatitude,
                                    '))'
                                ),
                                4326,
                                'axis-order=long-lat'
                            ),
                            u.location_point
                        )
                    )
                )
            )

            AND NOT EXISTS (
                SELECT 1
                FROM user_blocks ub
                WHERE
                    (ub.blocker_id = :requesterId
                    AND ub.blocked_id = u.id)
                    OR
                    (ub.blocker_id = u.id
                    AND ub.blocked_id = :requesterId)
            )

            AND ST_Distance_Sphere(
                u.location_point,
                ST_SRID(
                    POINT(:longitude, :latitude),
                    4326
                )
            ) / 1000.0 <= :distance
            """,

        nativeQuery = true
    )
    Page<NearbyUserProjection> findUsersWithinDistance(
        @Param("requesterId") Long requesterId,
        @Param("latitude") double latitude,
        @Param("longitude") double longitude,
        @Param("distance") double distance,
        @Param("minLatitude") double minLatitude,
        @Param("maxLatitude") double maxLatitude,
        @Param("minLongitude") double minLongitude,
        @Param("maxLongitude") double maxLongitude,
        @Param("crossesAntimeridian") boolean crossesAntimeridian,
        @Param("allLongitudes") boolean allLongitudes,
        Pageable pageable
    );
}