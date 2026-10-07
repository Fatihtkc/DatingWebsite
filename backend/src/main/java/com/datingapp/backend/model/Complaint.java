package com.datingapp.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(
    name = "complaints",
    indexes = {
        @Index(name = "idx_complaint_date_id", columnList = "complaint_date, id"),
        @Index(name = "idx_complaint_complainant", columnList = "complainant_id"),
        @Index(name = "idx_complaint_complained", columnList = "complained_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Complaint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "complainant_id")
    private User complainant;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "complained_id")
    private User complained;
    
    private String reason;

    private LocalDateTime complaintDate;

    @OneToMany(mappedBy = "complaint", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ComplaintImage> images;

    public void addImage(ComplaintImage image) {
        images.add(image);
        image.setComplaint(this);
    }
}
