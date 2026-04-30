package com.br.manager.domain.stock.repository;

import com.br.manager.domain.stock.entity.Fuel;
import com.br.manager.domain.stock.entity.Tank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TankRepository extends JpaRepository<Tank, Long> {
	List<Tank> findAllByDeletedIsNull();

	Tank findByIdAndDeletedIsNull(Long id);

    List<Tank> findByIdentityContainingIgnoreCaseAndDeletedIsNull(String name);

}
