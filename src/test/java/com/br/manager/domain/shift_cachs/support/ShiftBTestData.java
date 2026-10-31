package com.br.manager.domain.shift_cachs.support;

import com.br.manager.domain.operational.entity.Nozzle;
import com.br.manager.domain.operational.entity.PaymentMethod;
import com.br.manager.domain.operational.entity.Product;
import com.br.manager.domain.operational.entity.Pump;
import com.br.manager.domain.operational.entity.Tank;
import com.br.manager.domain.operational.enums.PaymentMethodKindEnum;
import com.br.manager.domain.operational.enums.ProductTypeEnum;
import com.br.manager.domain.operational.enums.UnitTypeEnum;
import com.br.manager.domain.operational.repository.NozzleRepository;
import com.br.manager.domain.operational.repository.PaymentMethodRepository;
import com.br.manager.domain.operational.repository.ProductRepository;
import com.br.manager.domain.operational.repository.PumpRepository;
import com.br.manager.domain.operational.repository.TankRepository;
import com.br.manager.domain.organization.entity.Company;
import com.br.manager.domain.organization.entity.Station;
import com.br.manager.domain.organization.entity.User;
import com.br.manager.domain.organization.repository.CompanyRepository;
import com.br.manager.domain.organization.repository.StationRepository;
import com.br.manager.domain.organization.repository.UserRepository;
import com.br.manager.domain.shift_cachs.entity.Shift;
import com.br.manager.domain.shift_cachs.enums.ShiftStatusEnum;
import com.br.manager.domain.shift_cachs.repository.ShiftRepository;
import io.restassured.config.JsonConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.path.json.config.JsonPathConfig;
import org.springframework.boot.test.context.TestComponent;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Pre-requisitos de outros dominios (empresa, posto, usuario, bico, forma de pagamento, turno)
 * usados pelos testes de sessao de caixa, movimentacoes, leituras de bico e fechamentos de pagamento.
 * Os metodos estaticos montam entidades em memoria (testes unitarios); os de instancia persistem (ITs).
 */
@TestComponent
public class ShiftBTestData {

    public static final BigDecimal SHIFT_OPENING_CASH = new BigDecimal("200.00");
    public static final LocalDateTime SHIFT_OPENED_AT = LocalDateTime.of(2026, 10, 31, 6, 0);
    public static final BigDecimal NOZZLE_METER = new BigDecimal("1000.0000");

    /**
     * Le valores numericos do JSON como BigDecimal, permitindo comparar valores monetarios sem perda de precisao.
     */
    public static final RestAssuredConfig DECIMAL_JSON = RestAssuredConfig.config()
            .jsonConfig(JsonConfig.jsonConfig().numberReturnType(JsonPathConfig.NumberReturnType.BIG_DECIMAL));

    private static final AtomicLong SEQUENCE = new AtomicLong();

    private final CompanyRepository companyRepository;
    private final StationRepository stationRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final TankRepository tankRepository;
    private final PumpRepository pumpRepository;
    private final NozzleRepository nozzleRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final ShiftRepository shiftRepository;

    public ShiftBTestData(CompanyRepository companyRepository,
                          StationRepository stationRepository,
                          UserRepository userRepository,
                          ProductRepository productRepository,
                          TankRepository tankRepository,
                          PumpRepository pumpRepository,
                          NozzleRepository nozzleRepository,
                          PaymentMethodRepository paymentMethodRepository,
                          ShiftRepository shiftRepository) {
        this.companyRepository = companyRepository;
        this.stationRepository = stationRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.tankRepository = tankRepository;
        this.pumpRepository = pumpRepository;
        this.nozzleRepository = nozzleRepository;
        this.paymentMethodRepository = paymentMethodRepository;
        this.shiftRepository = shiftRepository;
    }

    public static Shift shiftEntity(ShiftStatusEnum status) {
        Shift shift = new Shift();
        shift.setId(UUID.randomUUID());
        shift.setStationId(UUID.randomUUID());
        shift.setCashierUserId(UUID.randomUUID());
        shift.setOpenedBy(shift.getCashierUserId());
        shift.setOpeningCash(SHIFT_OPENING_CASH);
        shift.setOpenedAt(SHIFT_OPENED_AT);
        shift.setStatus(status);
        return shift;
    }

    public UUID createUser() {
        return saveUser(true);
    }

    public UUID createInactiveUser() {
        return saveUser(false);
    }

    public UUID createStation() {
        long sequence = SEQUENCE.incrementAndGet();
        Company company = new Company();
        company.setLegalName("Empresa ShiftB " + sequence);
        company.setCnpj(String.format("%014d", sequence));
        company.setActive(true);
        UUID companyId = companyRepository.saveAndFlush(company).getId();

        Station station = new Station();
        station.setCompanyId(companyId);
        station.setName("Posto ShiftB " + sequence);
        station.setActive(true);
        return stationRepository.saveAndFlush(station).getId();
    }

    public UUID createNozzle() {
        long sequence = SEQUENCE.incrementAndGet();
        Product product = new Product();
        product.setSku("GAS-" + sequence);
        product.setName("Gasolina Comum");
        product.setProductType(ProductTypeEnum.FUEL);
        product.setUnit(UnitTypeEnum.L);
        product.setActive(true);
        UUID productId = productRepository.saveAndFlush(product).getId();

        Tank tank = new Tank();
        tank.setCode("TQ-" + sequence);
        tank.setProductId(productId);
        tank.setCapacityLiters(new BigDecimal("15000.0000"));
        tank.setActive(true);
        UUID tankId = tankRepository.saveAndFlush(tank).getId();

        Pump pump = new Pump();
        pump.setCode("BB-" + sequence);
        pump.setActive(true);
        UUID pumpId = pumpRepository.saveAndFlush(pump).getId();

        Nozzle nozzle = new Nozzle();
        nozzle.setPumpId(pumpId);
        nozzle.setTankId(tankId);
        nozzle.setProductId(productId);
        nozzle.setCode("BC-" + sequence);
        nozzle.setMeterNumber(NOZZLE_METER);
        nozzle.setActive(true);
        return nozzleRepository.saveAndFlush(nozzle).getId();
    }

    public UUID createPaymentMethod() {
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setName("Dinheiro " + SEQUENCE.incrementAndGet());
        paymentMethod.setKind(PaymentMethodKindEnum.MONEY);
        paymentMethod.setRequiresReference(false);
        paymentMethod.setActive(true);
        return paymentMethodRepository.saveAndFlush(paymentMethod).getId();
    }

    public Shift createOpenShift() {
        return saveShift(ShiftStatusEnum.OPEN);
    }

    public Shift createClosedShift() {
        return saveShift(ShiftStatusEnum.CLOSED);
    }

    private Shift saveShift(ShiftStatusEnum status) {
        Shift shift = shiftEntity(status);
        shift.setStationId(createStation());
        shift.setCashierUserId(createUser());
        shift.setOpenedBy(shift.getCashierUserId());
        return shiftRepository.saveAndFlush(shift);
    }

    private UUID saveUser(boolean active) {
        long sequence = SEQUENCE.incrementAndGet();
        User user = new User();
        user.setName("Operador ShiftB " + sequence);
        user.setEmail("operador.shiftb." + sequence + "@posto.com.br");
        user.setPasswordHash("hash-" + sequence);
        user.setActive(active);
        return userRepository.saveAndFlush(user).getId();
    }
}
