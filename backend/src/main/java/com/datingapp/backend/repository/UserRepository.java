package com.datingapp.backend.repository;

import com.datingapp.backend.dto.Stats.AverageHeightWeightDTO;
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

    @Query("SELECT new com.datingapp.backend.dto.GenderDistributionDTO( u.gender, COUNT(u) ) FROM User u GROUP BY u.gender")
    List<GenderDistributionDTO> genderDistribution();

    @Query("SELECT AVG(YEAR(CURRENT_DATE) - YEAR(u.birthDate)) FROM User u WHERE u.birthDate IS NOT NULL")
    Double averageAge();

    @Query("SELECT new com.datingapp.backend.dto.RelationshipDistributionDTO( u.relationshipType, COUNT(u) ) FROM User u GROUP BY u.relationshipType")
    List<RelationshipDistributionDTO> relationshipTypes();

    @Query("SELECT new com.datingapp.backend.dto.AverageHeightWeightDTO( AVG(u.height), AVG(u.weight)) FROM User u WHERE u.height IS NOT NULL AND u.weight IS NOT NULL")
    AverageHeightWeightDTO averageHeightWeight();

    @Query(value = """
        SELECT
            u.id AS id,
            u.username AS username,
            u.full_name AS fullName,
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
            ) / 1000 AS distance

        FROM users u

        WHERE u.approved = true
        AND u.banned = false
        AND u.location_point IS NOT NULL
        AND u.id != :requesterId

        -- Bounding Box
        AND ST_Y(u.location_point)
            BETWEEN :latitude - (:distance / 111.0)
            AND :latitude + (:distance / 111.0)

        AND ST_X(u.location_point)
            BETWEEN
                :longitude -
                (:distance / (111.0 * COS(RADIANS(:latitude))))
            AND
                :longitude +
                (:distance / (111.0 * COS(RADIANS(:latitude))))

        -- Block control
        AND u.id NOT IN (
            SELECT ub.blocked_id
            FROM user_blocks ub
            WHERE ub.blocker_id = :requesterId

            UNION

            SELECT ub.blocker_id
            FROM user_blocks ub
            WHERE ub.blocked_id = :requesterId
        )

        -- Real Distance
        HAVING distance <= :distance

        ORDER BY distance
        """,
        countQuery = """
        SELECT COUNT(*)
        FROM users u

        WHERE u.approved = true
        AND u.banned = false
        AND u.location_point IS NOT NULL
        AND u.id != :requesterId

        AND ST_Y(u.location_point)
            BETWEEN :latitude - (:distance / 111.0)
            AND :latitude + (:distance / 111.0)

        AND ST_X(u.location_point)
            BETWEEN
                :longitude -
                (:distance / (111.0 * COS(RADIANS(:latitude))))
            AND
                :longitude +
                (:distance / (111.0 * COS(RADIANS(:latitude))))

        AND u.id NOT IN (
            SELECT ub.blocked_id
            FROM user_blocks ub
            WHERE ub.blocker_id = :requesterId

            UNION

            SELECT ub.blocker_id
            FROM user_blocks ub
            WHERE ub.blocked_id = :requesterId
        )

        AND ST_Distance_Sphere(
            u.location_point,
            ST_SRID(
                POINT(:longitude, :latitude),
                4326
            )
        ) / 1000 <= :distance
        """,
        nativeQuery = true)
    Page<NearbyUserProjection> findUsersWithinDistance(
            @Param("requesterId") Long requesterId,
            @Param("latitude") double latitude,
            @Param("longitude") double longitude,
            @Param("distance") double distance,
            Pageable pageable
    );
}