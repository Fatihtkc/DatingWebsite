package com.datingapp.backend.repository;

import com.datingapp.backend.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    // (m.senderId = u1 AND m.receiverId = u2) OR (m.senderId = u2 AND m.receiverId = u1)
    List<Message> findBySenderIdAndReceiverIdOrSenderIdAndReceiverIdOrderBySentAtAsc(
         Long senderId1, Long receiverId1, Long senderId2, Long receiverId2);
}

