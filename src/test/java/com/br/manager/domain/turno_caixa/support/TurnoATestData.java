package com.br.manager.domain.turno_caixa.support;

import com.br.manager.domain.operacional.entity.Bico;
import com.br.manager.domain.operacional.entity.Bomba;
import com.br.manager.domain.organizacao.entity.Empresa;
import com.br.manager.domain.organizacao.entity.Estacao;
import com.br.manager.domain.organizacao.entity.Usuario;
import com.br.manager.domain.organizacao.repository.EmpresaRepository;
import com.br.manager.domain.organizacao.repository.EstacaoRepository;
import com.br.manager.domain.organizacao.repository.UsuarioRepository;
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
public class TurnoATestData {

    public static final String ESTACAO_NAME = "Posto Turno Teste";
    public static final String USUARIO_NAME = "Operador Turno";
    public static final String PASSWORD_HASH = "hash";
    public static final BigDecimal MEDIDOR_BICO = new BigDecimal("1234.5600");

    private final EmpresaRepository empresaRepository;
    private final EstacaoRepository estacaoRepository;
    private final UsuarioRepository usuarioRepository;

    public TurnoATestData(EmpresaRepository empresaRepository,
                          EstacaoRepository estacaoRepository,
                          UsuarioRepository usuarioRepository) {
        this.empresaRepository = empresaRepository;
        this.estacaoRepository = estacaoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public UUID createEstacao() {
        return createEstacao(true);
    }

    public UUID createInativoEstacao() {
        return createEstacao(false);
    }

    public UUID createUsuario() {
        return createUsuario(true);
    }

    public UUID createInativoUsuario() {
        return createUsuario(false);
    }

    private UUID createEstacao(boolean ativo) {
        Empresa empresa = new Empresa();
        empresa.setLegalName("Empresa Turno LTDA");
        empresa.setCnpj(randomDigits(14));
        empresa.setAtivo(true);
        UUID empresaId = empresaRepository.saveAndFlush(empresa).getId();

        Estacao estacao = new Estacao();
        estacao.setEmpresaId(empresaId);
        estacao.setNome(ESTACAO_NAME);
        estacao.setAtivo(ativo);
        return estacaoRepository.saveAndFlush(estacao).getId();
    }

    private UUID createUsuario(boolean ativo) {
        Usuario usuario = new Usuario();
        usuario.setNome(USUARIO_NAME);
        usuario.setEmail("turno-" + UUID.randomUUID() + "@posto.com.br");
        usuario.setPasswordHash(PASSWORD_HASH);
        usuario.setAtivo(ativo);
        return usuarioRepository.saveAndFlush(usuario).getId();
    }

    public static Estacao estacao(UUID id, boolean ativo) {
        Estacao estacao = new Estacao();
        estacao.setId(id);
        estacao.setNome(ESTACAO_NAME);
        estacao.setAtivo(ativo);
        return estacao;
    }

    public static Usuario usuario(UUID id, boolean ativo) {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setNome(USUARIO_NAME);
        usuario.setAtivo(ativo);
        return usuario;
    }

    public static Bomba bomba() {
        Bomba bomba = new Bomba();
        bomba.setId(UUID.randomUUID());
        bomba.setCodigo("B01");
        bomba.setAtivo(true);
        return bomba;
    }

    public static Bico bico(UUID bombaId, BigDecimal numeroMedidor) {
        Bico bico = new Bico();
        bico.setId(UUID.randomUUID());
        bico.setBombaId(bombaId);
        bico.setCodigo("BC01");
        bico.setNumeroMedidor(numeroMedidor);
        bico.setAtivo(true);
        return bico;
    }

    private static String randomDigits(int length) {
        return String.format("%0" + length + "d", ThreadLocalRandom.current().nextLong((long) Math.pow(10, length)));
    }
}
