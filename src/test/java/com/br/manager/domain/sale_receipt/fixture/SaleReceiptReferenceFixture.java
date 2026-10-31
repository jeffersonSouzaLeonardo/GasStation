package com.br.manager.domain.sale_receipt.fixture;

import com.br.manager.domain.operational.entity.Customer;
import com.br.manager.domain.operational.entity.CustomerVehicle;
import com.br.manager.domain.operational.entity.Nozzle;
import com.br.manager.domain.operational.entity.PaymentMethod;
import com.br.manager.domain.operational.entity.Product;
import com.br.manager.domain.operational.entity.Pump;
import com.br.manager.domain.operational.entity.Tank;
import com.br.manager.domain.operational.enums.PaymentMethodKindEnum;
import com.br.manager.domain.operational.enums.PersonTypeEnum;
import com.br.manager.domain.operational.enums.ProductTypeEnum;
import com.br.manager.domain.operational.enums.UnitTypeEnum;
import com.br.manager.domain.organization.entity.Company;
import com.br.manager.domain.organization.entity.Station;
import com.br.manager.domain.organization.entity.User;
import com.br.manager.domain.shift_cachs.entity.Shift;
import com.br.manager.domain.shift_cachs.enums.ShiftStatusEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidades de outros dominios usadas como pre-requisito pelos cenarios de venda.
 * Todas sao criadas com ID aleatorio (para testes unitarios); quem persiste deve limpar o ID quando gerado pelo banco.
 */
public final class SaleReceiptReferenceFixture {

    public static final LocalDateTime CREATED_AT = LocalDateTime.of(2026, 10, 1, 8, 0);
    public static final String TIMEZONE = "America/Sao_Paulo";
    public static final String PASSWORD_HASH = "$2a$10$hash";
    public static final BigDecimal OPENING_CASH = new BigDecimal("100.00");
    public static final BigDecimal TANK_CAPACITY = new BigDecimal("15000.0000");

    private SaleReceiptReferenceFixture() {
    }

    public static Company company(String cnpj) {
        Company company = new Company();
        company.setId(UUID.randomUUID());
        company.setLegalName("Rede Posto Venda LTDA");
        company.setTradeName("Posto Venda");
        company.setCnpj(cnpj);
        company.setActive(true);
        return company;
    }

    public static Station station(UUID companyId, boolean active) {
        Station station = new Station();
        station.setId(UUID.randomUUID());
        station.setCompanyId(companyId);
        station.setName("Posto Centro");
        station.setTimezone(TIMEZONE);
        station.setActive(active);
        return station;
    }

    public static Station station(boolean active) {
        return station(UUID.randomUUID(), active);
    }

    public static User user(String email, boolean active) {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setName("Operador " + email);
        user.setEmail(email);
        user.setPasswordHash(PASSWORD_HASH);
        user.setActive(active);
        user.setCreatedAt(CREATED_AT);
        return user;
    }

    public static User user(boolean active) {
        return user(UUID.randomUUID() + "@posto.com.br", active);
    }

    public static Shift shift(UUID stationId, UUID cashierUserId, ShiftStatusEnum status) {
        Shift shift = new Shift();
        shift.setId(UUID.randomUUID());
        shift.setStationId(stationId);
        shift.setCashierUserId(cashierUserId);
        shift.setOpenedBy(cashierUserId);
        shift.setOpenedAt(CREATED_AT);
        shift.setOpeningCash(OPENING_CASH);
        shift.setStatus(status);
        return shift;
    }

    public static Shift shift(ShiftStatusEnum status) {
        return shift(UUID.randomUUID(), UUID.randomUUID(), status);
    }

    public static Customer customer(boolean active) {
        Customer customer = new Customer();
        customer.setId(UUID.randomUUID());
        customer.setName("Transportadora Cliente");
        customer.setPersonType(PersonTypeEnum.JURIDICAL);
        customer.setActive(active);
        return customer;
    }

    public static CustomerVehicle vehicle(UUID customerId, boolean active) {
        CustomerVehicle vehicle = new CustomerVehicle();
        vehicle.setId(UUID.randomUUID());
        vehicle.setCustomerId(customerId);
        vehicle.setPlate("ABC1D23");
        vehicle.setActive(active);
        return vehicle;
    }

    public static Product product(String sku, boolean active) {
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setSku(sku);
        product.setName("Gasolina Comum");
        product.setProductType(ProductTypeEnum.FUEL);
        product.setUnit(UnitTypeEnum.L);
        product.setActive(active);
        product.setCreatedAt(CREATED_AT);
        return product;
    }

    public static Product product(boolean active) {
        return product("SKU-" + UUID.randomUUID(), active);
    }

    public static Tank tank(UUID productId) {
        Tank tank = new Tank();
        tank.setId(UUID.randomUUID());
        tank.setCode("TQ-01");
        tank.setProductId(productId);
        tank.setCapacityLiters(TANK_CAPACITY);
        tank.setCurrentBookLiters(BigDecimal.ZERO);
        tank.setActive(true);
        tank.setCreatedAt(CREATED_AT);
        return tank;
    }

    public static Pump pump() {
        Pump pump = new Pump();
        pump.setId(UUID.randomUUID());
        pump.setCode("BB-01");
        pump.setActive(true);
        pump.setCreatedAt(CREATED_AT);
        return pump;
    }

    public static Nozzle nozzle(UUID pumpId, UUID tankId, UUID productId) {
        Nozzle nozzle = new Nozzle();
        nozzle.setId(UUID.randomUUID());
        nozzle.setPumpId(pumpId);
        nozzle.setTankId(tankId);
        nozzle.setProductId(productId);
        nozzle.setCode("BC-01");
        nozzle.setActive(true);
        nozzle.setCreatedAt(CREATED_AT);
        return nozzle;
    }

    public static PaymentMethod paymentMethod(PaymentMethodKindEnum kind, boolean requiresReference,
                                              int settlementDays, boolean active) {
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setId(UUID.randomUUID());
        paymentMethod.setName(kind.getDescricao());
        paymentMethod.setKind(kind);
        paymentMethod.setRequiresReference(requiresReference);
        paymentMethod.setSettlementDays(settlementDays);
        paymentMethod.setActive(active);
        return paymentMethod;
    }

    public static PaymentMethod cashPaymentMethod() {
        return paymentMethod(PaymentMethodKindEnum.MONEY, false, 0, true);
    }

    public static PaymentMethod agreementPaymentMethod() {
        return paymentMethod(PaymentMethodKindEnum.CONVENIO, false, 0, true);
    }
}
