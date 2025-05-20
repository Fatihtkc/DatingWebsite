package com.datingapp.backend.repository;

import com.datingapp.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // İsim veya soyisim bazında arama
    List<User> findByFullNameContainingIgnoreCase(String searchTerm);

    // Belirli bir yaş aralığında kullanıcıları getirmek (birthDate hesaplaması yapılabilir)
    // Örneğin: 1990 ile 2000 arasında doğan kullanıcılar (basit örnek, doğrudan tarih karşılaştırması)
    List<User> findByBirthDateBetween(java.time.LocalDate startDate, java.time.LocalDate endDate);

    // Kullanıcı lokasyonuna göre arama (basit string eşleştirme)
    List<User> findByLocationContainingIgnoreCase(String location);
    
    // İlişki türüne göre filtreleme
    List<User> findByRelationshipType(String relationshipType);

    // E-posta üzerinden kullanıcıyı getirmek için eklenen metod
    Optional<User> findByEmail(String email);

    // Username üzerinden kullanıcıyı getirmek için eklenen metod
    Optional<User> findByUsername(String username);

    // Onay bekleyen kullanıcıları getir
    List<User> findByApprovedFalse();

    // Onaylanmış ve banlı olmayan kullanıcıları getir
    List<User> findByApprovedTrueAndBannedFalse();

    @Query("SELECT COUNT(u) FROM User u")
    Long countAllUsers();

    @Query("SELECT u.gender, COUNT(u) FROM User u GROUP BY u.gender")
    List<Object[]> genderDistribution();

    @Query("SELECT AVG(YEAR(CURRENT_DATE) - YEAR(u.birthDate)) FROM User u WHERE u.birthDate IS NOT NULL")
    Double averageAge();

    @Query("SELECT u.relationshipType, COUNT(u) FROM User u GROUP BY u.relationshipType")
    List<Object[]> relationshipTypes();

    @Query("SELECT AVG(u.height), AVG(u.weight) FROM User u WHERE u.height IS NOT NULL AND u.weight IS NOT NULL")
    List<Object[]> averageHeightWeight();

    @Query(value = """
        SELECT *, (
            6371 * acos(
                cos(radians(:lat)) * cos(radians(u.latitude)) *
                cos(radians(u.longitude) - radians(:lon)) +
                sin(radians(:lat)) * sin(radians(u.latitude))
            )
        ) AS distance
        FROM users u
        HAVING distance <= :distance
        ORDER BY distance
        """, nativeQuery = true)
    List<User> findUsersWithinDistance(@Param("lat") double lat,
                                       @Param("lon") double lon,
                                       @Param("distance") double distance);
}