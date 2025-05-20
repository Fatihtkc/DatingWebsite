package com.datingapp.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "complaint_images")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Resmin URL'si veya dosya yolu
    private String imageUrl;

    // İlişkili kullanıcı
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "complaint_id")
    @JsonIgnore
     @ToString.Exclude            // Lombok’a bu alanı toString’a dahil etme
    @EqualsAndHashCode.Exclude   // equals/hashCode’da da dahil etme
    private Complaint complaint;
}
