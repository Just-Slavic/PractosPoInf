package project.practospoinformatike.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import project.practospoinformatike.entity.Seller;

@Repository
public interface SellerRepository extends JpaRepository<Seller, Long> {
    @Modifying
    @Transactional
    @Query("DELETE FROM Seller s WHERE s.email = ?1")
    void deleteByEmail(String email);
}
