package project.practospoinformatike.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import project.practospoinformatike.entity.Buyer;


public interface BuyerRepository extends JpaRepository<Buyer, Long> {

}
