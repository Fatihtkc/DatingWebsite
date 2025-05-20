package com.datingapp.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "complaints")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Complaint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Şikayeti yapan kullanıcı
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "complainant_id")
    private User complainant;
    
    // Şikayet konusu: genellikle başka bir kullanıcı
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "complained_id")
    private User complained;
    
    // Şikayet nedeni ve detayları
    private String reason;
    private LocalDateTime complaintDate;

    @OneToMany(mappedBy = "complaint", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ComplaintImage> images;
}
