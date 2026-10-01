package com.br.manager.domain.shift_cachs.repository;

import com.br.manager.domain.shift_cachs.entity.NozzleReading;
import com.br.manager.domain.shift_cachs.enums.NozzleReadingTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NozzleReadingRepository extends JpaRepository<NozzleReading, UUID> {

    List<NozzleReading> findByShiftId(UUID shiftId);

    List<NozzleReading> findByShiftIdAndNozzleId(UUID shiftId, UUID nozzleId);

    boolean existsByShiftIdAndNozzleIdAndReadingType(UUID shiftId, UUID nozzleId, NozzleReadingTypeEnum readingType);

    List<NozzleReading> findByShiftIdAndReadingType(UUID shiftId, NozzleReadingTypeEnum readingType);
}
