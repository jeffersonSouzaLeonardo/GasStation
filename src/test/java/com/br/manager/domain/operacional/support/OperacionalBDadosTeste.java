package com.br.manager.domain.operacional.support;

import com.br.manager.domain.operacional.entity.Produto;
import com.br.manager.domain.operacional.entity.Bomba;
import com.br.manager.domain.operacional.entity.Tanque;
import com.br.manager.domain.operacional.enums.ProdutoTypeEnum;
import com.br.manager.domain.operacional.enums.TipoUnidadeEnum;
import com.br.manager.domain.operacional.repository.ProdutoRepository;
import com.br.manager.domain.operacional.repository.BombaRepository;
import com.br.manager.domain.operacional.repository.TanqueRepository;
import org.springframework.boot.test.context.TestComponent;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Pre-requisitos persistidos diretamente no banco para os ITs de abastecimento
 * (tanque e bomba sao chaves estrangeiras obrigatorias de abastecimentos).
 */
@TestComponent
public class OperacionalBDadosTeste {

    private static final BigDecimal TANQUE_CAPACIDADE = new BigDecimal("15000.0000");

    private final ProdutoRepository produtoRepository;
    private final TanqueRepository tanqueRepository;
    private final BombaRepository bombaRepository;

    public OperacionalBDadosTeste(ProdutoRepository produtoRepository,
                                TanqueRepository tanqueRepository,
                                BombaRepository bombaRepository) {
        this.produtoRepository = produtoRepository;
        this.tanqueRepository = tanqueRepository;
        this.bombaRepository = bombaRepository;
    }

    public UUID createTanque() {
        Tanque tanque = new Tanque();
        tanque.setCodigo("T-01");
        tanque.setProdutoId(createCombustivelProduto());
        tanque.setCapacidadeLitros(TANQUE_CAPACIDADE);
        tanque.setSaldoContabilLitros(BigDecimal.ZERO);
        tanque.setAtivo(true);
        return tanqueRepository.saveAndFlush(tanque).getId();
    }

    public UUID createBomba() {
        Bomba bomba = new Bomba();
        bomba.setCodigo("B-01");
        bomba.setAtivo(true);
        return bombaRepository.saveAndFlush(bomba).getId();
    }

    private UUID createCombustivelProduto() {
        Produto produto = new Produto();
        produto.setSku("GC-001");
        produto.setNome("Gasolina Comum");
        produto.setProdutoType(ProdutoTypeEnum.COMBUSTIVEL);
        produto.setUnidade(TipoUnidadeEnum.L);
        produto.setAtivo(true);
        return produtoRepository.saveAndFlush(produto).getId();
    }
}
