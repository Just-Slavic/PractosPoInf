package project.practospoinformatike.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import project.practospoinformatike.entity.Buyer;

@Repository
public interface BuyerRepository extends JpaRepository<Buyer, Long> {
    @Modifying
    @Transactional
    @Query("DELETE FROM Buyer b WHERE b.email = ?1")
    void deleteByEmail(String email);
}
