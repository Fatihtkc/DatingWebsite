package com.datingapp.backend.repository;

import com.datingapp.backend.model.Complaint;
import com.datingapp.backend.projection.ComplaintProjection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    @Query(value = """
        SELECT
            c.id AS id,
            c.reason AS reason,
            c.complaint_date AS complaintDate,

            u1.id AS complainantId,
            u1.username AS complainantUsername,
            u1.first_name AS complainantFirstName,
            u1.last_name AS complainantLastName,
            u1.email AS complainantEmail,

            (   SELECT ui1.image_url
                FROM user_images ui1
                WHERE ui1.user_id = u1.id
                ORDER BY ui1.id ASC
                LIMIT 1
            ) AS complainantImageUrl,

            u2.id AS complainedId,
            u2.username AS complainedUsername,
            u2.first_name AS complainedFirstName,
            u2.last_name AS complainedLastName,
            u2.email AS complainedEmail,

            (   SELECT ui2.image_url
                FROM user_images ui2
                WHERE ui2.user_id = u2.id
                ORDER BY ui2.id ASC
                LIMIT 1
            ) AS complainedImageUrl,

            (   SELECT ci.image_url
                FROM complaint_images ci
                WHERE ci.complaint_id = c.id
                ORDER BY ci.id ASC
                LIMIT 1
            ) AS complaintImageUrl

        FROM complaints c
        JOIN users u1 ON u1.id = c.complainant_id
        JOIN users u2 ON u2.id = c.complained_id

        ORDER BY c.complaint_date DESC
        """,
        countQuery = """
        SELECT COUNT(*)
        FROM complaints
        """,
        nativeQuery = true)
    Page<ComplaintProjection> findAllComplaintProjections(Pageable pageable);


    @Query(value = """
        SELECT
            c.id AS id,
            c.reason AS reason,
            c.complaint_date AS complaintDate,

            u1.id AS complainantId,
            u1.username AS complainantUsername,
            u1.first_name AS complainantFirstName,
            u1.last_name AS complainantLastName,
            u1.email AS complainantEmail,

            (   SELECT ui1.image_url
                FROM user_images ui1
                WHERE ui1.user_id = u1.id
                ORDER BY ui1.id ASC
                LIMIT 1
            ) AS complainantImageUrl,

            u2.id AS complainedId,
            u2.username AS complainedUsername,
            u2.first_name AS complainedFirstName,
            u2.last_name AS complainedLastName,
            u2.email AS complainedEmail,

            (   SELECT ui2.image_url
                FROM user_images ui2
                WHERE ui2.user_id = u2.id
                ORDER BY ui2.id ASC
                LIMIT 1
            ) AS complainedImageUrl,

            (   SELECT ci.image_url
                FROM complaint_images ci
                WHERE ci.complaint_id = c.id
                ORDER BY ci.id ASC
                LIMIT 1
            ) AS complaintImageUrl

        FROM complaints c
        JOIN users u1 ON u1.id = c.complainant_id
        JOIN users u2 ON u2.id = c.complained_id

        WHERE c.id = :id
        """,
        nativeQuery = true)
    ComplaintProjection findComplaintProjectionById(@Param("id") Long id);
}