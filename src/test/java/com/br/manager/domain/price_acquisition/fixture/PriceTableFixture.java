package com.br.manager.domain.price_acquisition.fixture;

import com.br.manager.domain.organization.entity.User;
import com.br.manager.domain.price_acquisition.dto.PriceItemInputDTO;
import com.br.manager.domain.price_acquisition.dto.PriceTableInputDTO;
import com.br.manager.domain.price_acquisition.entity.PriceTable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Massa de dados padrao para cenarios de Tabela de Precos.
 */
public final class PriceTableFixture {

    public static final String NAME = "Tabela Combustiveis Janeiro";
    public static final String OTHER_NAME = "Tabela Lubrificantes";
    public static final String STATUS = "ACTIVE";
    public static final String INACTIVE_STATUS = "INACTIVE";
    public static final LocalDateTime VALID_FROM = LocalDateTime.of(2026, 1, 1, 0, 0);
    public static final LocalDateTime VALID_TO = LocalDateTime.of(2026, 1, 31, 23, 59);

    private PriceTableFixture() {
    }

    public static PriceTableInputDTO validInput(UUID createdBy, List<PriceItemInputDTO> priceItems) {
        PriceTableInputDTO input = new PriceTableInputDTO();
        input.setName(NAME);
        input.setValidFrom(VALID_FROM);
        input.setValidTo(VALID_TO);
        input.setStatus(STATUS);
        input.setCreatedBy(createdBy);
        input.setPriceItems(priceItems);
        return input;
    }

    public static PriceTable persistedEntity(User createdBy) {
        PriceTable priceTable = new PriceTable();
        priceTable.setId(UUID.randomUUID());
        priceTable.setName(NAME);
        priceTable.setValidFrom(VALID_FROM);
        priceTable.setValidTo(VALID_TO);
        priceTable.setStatus(STATUS);
        priceTable.setCreatedBy(createdBy);
        return priceTable;
    }
}
