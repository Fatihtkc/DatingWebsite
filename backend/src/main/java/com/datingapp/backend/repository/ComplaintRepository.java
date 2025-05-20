package com.datingapp.backend.repository;

import com.datingapp.backend.model.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    // Şikayet eden kullanıcıya göre sorgulama
    List<Complaint> findByComplainantId(Long complainantId);

    // Şikayet edilen kullanıcıya göre sorgulama
    List<Complaint> findByComplainedId(Long complainedId);
}
