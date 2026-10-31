package com.br.manager.domain.operational.support;

import com.br.manager.domain.operational.entity.Product;
import com.br.manager.domain.operational.entity.Pump;
import com.br.manager.domain.operational.entity.Tank;
import com.br.manager.domain.operational.enums.ProductTypeEnum;
import com.br.manager.domain.operational.enums.UnitTypeEnum;
import com.br.manager.domain.operational.repository.ProductRepository;
import com.br.manager.domain.operational.repository.PumpRepository;
import com.br.manager.domain.operational.repository.TankRepository;
import org.springframework.boot.test.context.TestComponent;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Pre-requisitos persistidos diretamente no banco para os ITs de abastecimento
 * (tanque e bomba sao chaves estrangeiras obrigatorias de supplies).
 */
@TestComponent
public class OperationalBTestData {

    private static final BigDecimal TANK_CAPACITY = new BigDecimal("15000.0000");

    private final ProductRepository productRepository;
    private final TankRepository tankRepository;
    private final PumpRepository pumpRepository;

    public OperationalBTestData(ProductRepository productRepository,
                                TankRepository tankRepository,
                                PumpRepository pumpRepository) {
        this.productRepository = productRepository;
        this.tankRepository = tankRepository;
        this.pumpRepository = pumpRepository;
    }

    public UUID createTank() {
        Tank tank = new Tank();
        tank.setCode("T-01");
        tank.setProductId(createFuelProduct());
        tank.setCapacityLiters(TANK_CAPACITY);
        tank.setCurrentBookLiters(BigDecimal.ZERO);
        tank.setActive(true);
        return tankRepository.saveAndFlush(tank).getId();
    }

    public UUID createPump() {
        Pump pump = new Pump();
        pump.setCode("B-01");
        pump.setActive(true);
        return pumpRepository.saveAndFlush(pump).getId();
    }

    private UUID createFuelProduct() {
        Product product = new Product();
        product.setSku("GC-001");
        product.setName("Gasolina Comum");
        product.setProductType(ProductTypeEnum.FUEL);
        product.setUnit(UnitTypeEnum.L);
        product.setActive(true);
        return productRepository.saveAndFlush(product).getId();
    }
}
