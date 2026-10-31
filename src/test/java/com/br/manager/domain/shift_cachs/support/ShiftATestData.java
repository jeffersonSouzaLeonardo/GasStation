package com.br.manager.domain.shift_cachs.support;

import com.br.manager.domain.operational.entity.Nozzle;
import com.br.manager.domain.operational.entity.Pump;
import com.br.manager.domain.organization.entity.Company;
import com.br.manager.domain.organization.entity.Station;
import com.br.manager.domain.organization.entity.User;
import com.br.manager.domain.organization.repository.CompanyRepository;
import com.br.manager.domain.organization.repository.StationRepository;
import com.br.manager.domain.organization.repository.UserRepository;
import org.springframework.boot.test.context.TestComponent;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Pre-requisitos de outros dominios (empresa, posto, usuario, bomba, bico)
 * usados pelos testes do ciclo de vida do turno.
 * Fabricas estaticas atendem os testes unitarios; os metodos de instancia
 * persistem os registros nos testes de integracao (importar com @Import).
 */
@TestComponent
public class ShiftATestData {

    public static final String STATION_NAME = "Posto Turno Teste";
    public static final String USER_NAME = "Operador Turno";
    public static final String PASSWORD_HASH = "hash";
    public static final BigDecimal NOZZLE_METER = new BigDecimal("1234.5600");

    private final CompanyRepository companyRepository;
    private final StationRepository stationRepository;
    private final UserRepository userRepository;

    public ShiftATestData(CompanyRepository companyRepository,
                          StationRepository stationRepository,
                          UserRepository userRepository) {
        this.companyRepository = companyRepository;
        this.stationRepository = stationRepository;
        this.userRepository = userRepository;
    }

    public UUID createStation() {
        return createStation(true);
    }

    public UUID createInactiveStation() {
        return createStation(false);
    }

    public UUID createUser() {
        return createUser(true);
    }

    public UUID createInactiveUser() {
        return createUser(false);
    }

    private UUID createStation(boolean active) {
        Company company = new Company();
        company.setLegalName("Empresa Turno LTDA");
        company.setCnpj(randomDigits(14));
        company.setActive(true);
        UUID companyId = companyRepository.saveAndFlush(company).getId();

        Station station = new Station();
        station.setCompanyId(companyId);
        station.setName(STATION_NAME);
        station.setActive(active);
        return stationRepository.saveAndFlush(station).getId();
    }

    private UUID createUser(boolean active) {
        User user = new User();
        user.setName(USER_NAME);
        user.setEmail("turno-" + UUID.randomUUID() + "@posto.com.br");
        user.setPasswordHash(PASSWORD_HASH);
        user.setActive(active);
        return userRepository.saveAndFlush(user).getId();
    }

    public static Station station(UUID id, boolean active) {
        Station station = new Station();
        station.setId(id);
        station.setName(STATION_NAME);
        station.setActive(active);
        return station;
    }

    public static User user(UUID id, boolean active) {
        User user = new User();
        user.setId(id);
        user.setName(USER_NAME);
        user.setActive(active);
        return user;
    }

    public static Pump pump() {
        Pump pump = new Pump();
        pump.setId(UUID.randomUUID());
        pump.setCode("B01");
        pump.setActive(true);
        return pump;
    }

    public static Nozzle nozzle(UUID pumpId, BigDecimal meterNumber) {
        Nozzle nozzle = new Nozzle();
        nozzle.setId(UUID.randomUUID());
        nozzle.setPumpId(pumpId);
        nozzle.setCode("BC01");
        nozzle.setMeterNumber(meterNumber);
        nozzle.setActive(true);
        return nozzle;
    }

    private static String randomDigits(int length) {
        return String.format("%0" + length + "d", ThreadLocalRandom.current().nextLong((long) Math.pow(10, length)));
    }
}
