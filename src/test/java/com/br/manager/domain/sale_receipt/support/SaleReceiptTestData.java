package com.br.manager.domain.sale_receipt.support;

import com.br.manager.domain.operational.entity.Customer;
import com.br.manager.domain.operational.entity.CustomerVehicle;
import com.br.manager.domain.operational.entity.Nozzle;
import com.br.manager.domain.operational.entity.PaymentMethod;
import com.br.manager.domain.operational.entity.Product;
import com.br.manager.domain.operational.entity.Pump;
import com.br.manager.domain.operational.entity.Tank;
import com.br.manager.domain.operational.repository.CustomerRepository;
import com.br.manager.domain.operational.repository.CustomerVehicleRepository;
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
import com.br.manager.domain.sale_receipt.entity.Sale;
import com.br.manager.domain.sale_receipt.entity.SaleRefund;
import com.br.manager.domain.sale_receipt.enums.PaymentStatus;
import com.br.manager.domain.sale_receipt.enums.SaleStatus;
import com.br.manager.domain.sale_receipt.fixture.SaleFixture;
import com.br.manager.domain.sale_receipt.fixture.SaleItemFixture;
import com.br.manager.domain.sale_receipt.fixture.SalePaymentFixture;
import com.br.manager.domain.sale_receipt.fixture.SaleReceiptReferenceFixture;
import com.br.manager.domain.sale_receipt.fixture.SaleRefundFixture;
import com.br.manager.domain.sale_receipt.repository.SaleRepository;
import com.br.manager.domain.shift_cachs.entity.Shift;
import com.br.manager.domain.shift_cachs.enums.ShiftStatusEnum;
import com.br.manager.domain.shift_cachs.repository.ShiftRepository;
import org.springframework.context.ApplicationContext;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Cria, via repositorios, os pre-requisitos de outros dominios (empresa, posto, usuarios, turno,
 * cliente, produto, bico, forma de pagamento) e vendas ja persistidas para os testes de integracao de venda.
 */
public class SaleReceiptTestData {

    private static final AtomicLong SEQUENCE = new AtomicLong(10_000_000_000_000L);

    private final CompanyRepository companyRepository;
    private final StationRepository stationRepository;
    private final UserRepository userRepository;
    private final ShiftRepository shiftRepository;
    private final CustomerRepository customerRepository;
    private final CustomerVehicleRepository customerVehicleRepository;
    private final ProductRepository productRepository;
    private final TankRepository tankRepository;
    private final PumpRepository pumpRepository;
    private final NozzleRepository nozzleRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final SaleRepository saleRepository;

    public SaleReceiptTestData(ApplicationContext context) {
        this.companyRepository = context.getBean(CompanyRepository.class);
        this.stationRepository = context.getBean(StationRepository.class);
        this.userRepository = context.getBean(UserRepository.class);
        this.shiftRepository = context.getBean(ShiftRepository.class);
        this.customerRepository = context.getBean(CustomerRepository.class);
        this.customerVehicleRepository = context.getBean(CustomerVehicleRepository.class);
        this.productRepository = context.getBean(ProductRepository.class);
        this.tankRepository = context.getBean(TankRepository.class);
        this.pumpRepository = context.getBean(PumpRepository.class);
        this.nozzleRepository = context.getBean(NozzleRepository.class);
        this.paymentMethodRepository = context.getBean(PaymentMethodRepository.class);
        this.saleRepository = context.getBean(SaleRepository.class);
    }

    public record SaleScenario(UUID stationId, UUID shiftId, UUID cashierUserId, UUID productId) {
    }

    public SaleScenario createScenario() {
        UUID stationId = createStation(true);
        UUID cashierUserId = createUser(true);
        UUID shiftId = createShift(stationId, cashierUserId, ShiftStatusEnum.OPEN);
        return new SaleScenario(stationId, shiftId, cashierUserId, createProduct());
    }

    public UUID createStation(boolean active) {
        Company company = SaleReceiptReferenceFixture.company(String.valueOf(SEQUENCE.incrementAndGet()));
        company.setId(null);
        UUID companyId = companyRepository.saveAndFlush(company).getId();

        Station station = SaleReceiptReferenceFixture.station(companyId, active);
        station.setId(null);
        return stationRepository.saveAndFlush(station).getId();
    }

    public UUID createUser(boolean active) {
        User user = SaleReceiptReferenceFixture.user(active);
        user.setId(null);
        return userRepository.saveAndFlush(user).getId();
    }

    public UUID createShift(UUID stationId, UUID cashierUserId, ShiftStatusEnum status) {
        Shift shift = SaleReceiptReferenceFixture.shift(stationId, cashierUserId, status);
        return shiftRepository.saveAndFlush(shift).getId();
    }

    public UUID createCustomer() {
        Customer customer = SaleReceiptReferenceFixture.customer(true);
        customer.setId(null);
        return customerRepository.saveAndFlush(customer).getId();
    }

    public UUID createVehicle(UUID customerId) {
        CustomerVehicle vehicle = SaleReceiptReferenceFixture.vehicle(customerId, true);
        vehicle.setId(null);
        return customerVehicleRepository.saveAndFlush(vehicle).getId();
    }

    public UUID createProduct() {
        Product product = SaleReceiptReferenceFixture.product(true);
        product.setId(null);
        return productRepository.saveAndFlush(product).getId();
    }

    public UUID createNozzle(UUID productId) {
        Tank tank = SaleReceiptReferenceFixture.tank(productId);
        tank.setId(null);
        UUID tankId = tankRepository.saveAndFlush(tank).getId();

        Pump pump = SaleReceiptReferenceFixture.pump();
        pump.setId(null);
        UUID pumpId = pumpRepository.saveAndFlush(pump).getId();

        Nozzle nozzle = SaleReceiptReferenceFixture.nozzle(pumpId, tankId, productId);
        nozzle.setId(null);
        return nozzleRepository.saveAndFlush(nozzle).getId();
    }

    public UUID createPaymentMethod(PaymentMethod paymentMethod) {
        paymentMethod.setId(null);
        return paymentMethodRepository.saveAndFlush(paymentMethod).getId();
    }

    public Sale createSale(SaleScenario scenario, String saleNumber, SaleStatus status) {
        Sale sale = SaleFixture.entity(scenario.stationId(), scenario.shiftId(), scenario.cashierUserId(), status);
        sale.setSaleNumber(saleNumber);
        sale.addItem(SaleItemFixture.persistedEntity(scenario.productId()));
        return saleRepository.saveAndFlush(sale);
    }

    public Sale createSaleWithPayment(SaleScenario scenario, SaleStatus status, UUID paymentMethodId,
                                      PaymentStatus paymentStatus) {
        Sale sale = SaleFixture.entity(scenario.stationId(), scenario.shiftId(), scenario.cashierUserId(), status);
        sale.addItem(SaleItemFixture.persistedEntity(scenario.productId()));
        sale.addPayment(SalePaymentFixture.persistedEntity(paymentMethodId, paymentStatus));
        return saleRepository.saveAndFlush(sale);
    }

    public Sale createRefundedSale(SaleScenario scenario, UUID requestedBy, UUID approvedBy) {
        Sale sale = SaleFixture.entity(scenario.stationId(), scenario.shiftId(), scenario.cashierUserId(), SaleStatus.REFUNDED);
        sale.addItem(SaleItemFixture.persistedEntity(scenario.productId()));
        SaleRefund refund = SaleRefundFixture.persistedEntity(requestedBy, approvedBy);
        sale.addRefund(refund);
        return saleRepository.saveAndFlush(sale);
    }
}
