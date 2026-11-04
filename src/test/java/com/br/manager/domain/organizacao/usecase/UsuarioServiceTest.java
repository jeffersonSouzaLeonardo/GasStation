package com.br.manager.domain.organizacao.usecase;

import com.br.manager.domain.comum.entity.Sincronizacao;
import com.br.manager.domain.comum.exception.ExcecaoNegocio;
import com.br.manager.domain.comum.exception.ExcecaoNegocioNaoEncontrado;
import com.br.manager.domain.comum.service.ServicoSincronizacao;
import com.br.manager.domain.organizacao.dto.UsuarioInputDTO;
import com.br.manager.domain.organizacao.dto.UsuarioResponseDTO;
import com.br.manager.domain.organizacao.entity.Usuario;
import com.br.manager.domain.organizacao.fixture.ConstraintViolationFixture;
import com.br.manager.domain.organizacao.fixture.UsuarioFixture;
import com.br.manager.domain.organizacao.mapper.UsuarioMapper;
import com.br.manager.domain.organizacao.repository.UsuarioRepository;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    private static final String USUARIO_TABELA = "USUARIO";
    private static final String EMAIL_OBRIGATORIO = "O e-mail do usuário é obrigatório.";
    private static final LocalDateTime DEFAULT_SINCE = LocalDateTime.of(2026, 1, 1, 0, 0, 0);
    private static final LocalDateTime ULTIMA_SINCRONIZACAO = LocalDateTime.of(2026, 6, 1, 12, 0);

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ServicoSincronizacao sincronizacaoService;

    @Spy
    private UsuarioMapper usuarioMapper = new UsuarioMapper();

    @InjectMocks
    private UsuarioService usuarioService;

    private static ConstraintViolationException emailViolation() {
        Usuario invalido = UsuarioFixture.persistedEntity();
        invalido.setEmail(null);
        return ConstraintViolationFixture.violacoesOf(invalido);
    }

    private static Usuario inativoUsuario() {
        Usuario usuario = UsuarioFixture.persistedEntity();
        usuario.setAtivo(false);
        return usuario;
    }

    private static Sincronizacao sincronizacaoAt(LocalDateTime ultimaSincronizacao) {
        Sincronizacao sincronizacao = new Sincronizacao();
        sincronizacao.setUltimaSincronizacao(ultimaSincronizacao);
        return sincronizacao;
    }

    @Nested
    class Create {

        @Test
        void shouldPersistAndReturnUsuario() {
            when(usuarioRepository.saveAndFlush(any(Usuario.class))).thenAnswer(invocation -> {
                Usuario usuario = invocation.getArgument(0);
                usuario.setId(UUID.randomUUID());
                return usuario;
            });

            UsuarioResponseDTO response = usuarioService.create(UsuarioFixture.validoInput());

            assertThat(response.getId()).isNotNull();
            assertThat(response.getEmail()).isEqualTo(UsuarioFixture.EMAIL);
            assertThat(response.getAtivo()).isTrue();
        }

        @Test
        void shouldTranslateConstraintViolationIntoExcecaoNegocio() {
            when(usuarioRepository.saveAndFlush(any(Usuario.class))).thenThrow(emailViolation());

            assertThatThrownBy(() -> usuarioService.create(UsuarioFixture.validoInput()))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining(EMAIL_OBRIGATORIO);
        }

        @Test
        void shouldWrapPersistenceErrorInExcecaoNegocio() {
            when(usuarioRepository.saveAndFlush(any(Usuario.class)))
                    .thenThrow(new DataIntegrityViolationException("duplicate email"));

            assertThatThrownBy(() -> usuarioService.create(UsuarioFixture.validoInput()))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining(UsuarioFixture.NAME)
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class Update {

        @Test
        void shouldUpdateExistingUsuario() {
            Usuario existing = UsuarioFixture.persistedEntity();
            UsuarioInputDTO input = UsuarioFixture.validoInput(UsuarioFixture.OUTROS_EMAIL);
            input.setId(existing.getId());
            when(usuarioRepository.findByIdAndAtivoTrue(existing.getId())).thenReturn(existing);
            when(usuarioRepository.saveAndFlush(existing)).thenReturn(existing);

            UsuarioResponseDTO response = usuarioService.update(input);

            assertThat(response.getId()).isEqualTo(existing.getId());
            assertThat(response.getEmail()).isEqualTo(UsuarioFixture.OUTROS_EMAIL);
        }

        @Test
        void shouldThrowNotFoundWhenUsuarioDoesNotExist() {
            UsuarioInputDTO input = UsuarioFixture.validoInput();
            input.setId(UUID.randomUUID());

            assertThatThrownBy(() -> usuarioService.update(input))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class)
                    .hasMessageContaining(input.getId().toString());
            verify(usuarioRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldTranslateConstraintViolationIntoExcecaoNegocio() {
            Usuario existing = UsuarioFixture.persistedEntity();
            UsuarioInputDTO input = UsuarioFixture.validoInput();
            input.setId(existing.getId());
            when(usuarioRepository.findByIdAndAtivoTrue(existing.getId())).thenReturn(existing);
            when(usuarioRepository.saveAndFlush(existing)).thenThrow(emailViolation());

            assertThatThrownBy(() -> usuarioService.update(input))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining(EMAIL_OBRIGATORIO);
        }

        @Test
        void shouldWrapPersistenceErrorInExcecaoNegocio() {
            Usuario existing = UsuarioFixture.persistedEntity();
            UsuarioInputDTO input = UsuarioFixture.validoInput();
            input.setId(existing.getId());
            when(usuarioRepository.findByIdAndAtivoTrue(existing.getId())).thenReturn(existing);
            when(usuarioRepository.saveAndFlush(existing)).thenThrow(new DataIntegrityViolationException("duplicate"));

            assertThatThrownBy(() -> usuarioService.update(input))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .isNotInstanceOf(ExcecaoNegocioNaoEncontrado.class)
                    .hasMessageContaining(UsuarioFixture.NAME)
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class Queries {

        @Test
        void shouldListAtivoUsuarios() {
            when(usuarioRepository.findAllByAtivoTrue()).thenReturn(List.of(UsuarioFixture.persistedEntity()));

            assertThat(usuarioService.findAll()).hasSize(1);
        }

        @Test
        void shouldBuscaByName() {
            when(usuarioRepository.findByNameContainingIgnoreCaseAndAtivoTrue("joao"))
                    .thenReturn(List.of(UsuarioFixture.persistedEntity()));

            assertThat(usuarioService.findByName("joao")).hasSize(1);
        }

        @Test
        void shouldFallbackToFindAllWhenDescricaoIsVazio() {
            when(usuarioRepository.findAllByAtivoTrue()).thenReturn(List.of());

            assertThat(usuarioService.findByDescricao("")).isEmpty();
            verify(usuarioRepository, never()).findByNameContainingIgnoreCaseAndAtivoTrue(any());
        }

        @Test
        void shouldFindAtivoUsuarioById() {
            Usuario existing = UsuarioFixture.persistedEntity();
            when(usuarioRepository.findById(existing.getId())).thenReturn(Optional.of(existing));

            assertThat(usuarioService.find(existing.getId()).getId()).isEqualTo(existing.getId());
        }

        @Test
        void shouldReturnAtivoEntityById() {
            Usuario existing = UsuarioFixture.persistedEntity();
            when(usuarioRepository.findById(existing.getId())).thenReturn(Optional.of(existing));

            assertThat(usuarioService.getUsuarioEntityById(existing.getId())).isSameAs(existing);
        }

        @Test
        void shouldThrowNotFoundWhenIdDoesNotExist() {
            UUID id = UUID.randomUUID();
            when(usuarioRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> usuarioService.find(id)).isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
            assertThatThrownBy(() -> usuarioService.getUsuarioEntityById(id)).isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
        }

        @Test
        void shouldThrowNotFoundWhenUsuarioIsInativo() {
            Usuario inativo = inativoUsuario();
            when(usuarioRepository.findById(inativo.getId())).thenReturn(Optional.of(inativo));

            assertThatThrownBy(() -> usuarioService.find(inativo.getId())).isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
            assertThatThrownBy(() -> usuarioService.getUsuarioEntityById(inativo.getId())).isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
        }
    }

    @Nested
    class Delete {

        @Test
        void shouldInactivateUsuarioAndFillExcluidoEm() {
            Usuario existing = UsuarioFixture.persistedEntity();
            when(usuarioRepository.findById(existing.getId())).thenReturn(Optional.of(existing));

            usuarioService.delete(existing.getId());

            ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
            verify(usuarioRepository).saveAndFlush(captor.capture());
            assertThat(captor.getValue().getAtivo()).isFalse();
            assertThat(captor.getValue().getExcluidoEm()).isNotNull();
        }

        @Test
        void shouldThrowNotFoundWhenDeletingUnknownUsuario() {
            UUID id = UUID.randomUUID();
            when(usuarioRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> usuarioService.delete(id)).isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
        }

        @Test
        void shouldTranslateConstraintViolationIntoExcecaoNegocio() {
            Usuario existing = UsuarioFixture.persistedEntity();
            when(usuarioRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            when(usuarioRepository.saveAndFlush(existing)).thenThrow(emailViolation());

            assertThatThrownBy(() -> usuarioService.delete(existing.getId()))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining(EMAIL_OBRIGATORIO);
        }

        @Test
        void shouldWrapPersistenceErrorInExcecaoNegocio() {
            Usuario existing = UsuarioFixture.persistedEntity();
            when(usuarioRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            when(usuarioRepository.saveAndFlush(existing)).thenThrow(new DataIntegrityViolationException("fk"));

            assertThatThrownBy(() -> usuarioService.delete(existing.getId()))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessage("Erro ao excluir usuário")
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class ChangedSinceSincronizacao {

        @Test
        void shouldBuscaUsuariosChangedSinceUltimaSincronizacao() {
            when(sincronizacaoService.findByTabelaName(USUARIO_TABELA)).thenReturn(sincronizacaoAt(ULTIMA_SINCRONIZACAO));
            when(usuarioRepository.findAllChangedSince(ULTIMA_SINCRONIZACAO)).thenReturn(List.of(UsuarioFixture.persistedEntity()));

            List<UsuarioResponseDTO> changed = usuarioService.buscarAlteradosDesdeSincronizacao(USUARIO_TABELA);

            assertThat(changed).hasSize(1);
        }

        @Test
        void shouldUseDefaultDateWhenNeverSincronizacaod() {
            when(sincronizacaoService.findByTabelaName(USUARIO_TABELA)).thenReturn(sincronizacaoAt(null));
            when(usuarioRepository.findAllChangedSince(any())).thenReturn(List.of());

            usuarioService.buscarAlteradosDesdeSincronizacao(USUARIO_TABELA);

            ArgumentCaptor<LocalDateTime> captor = ArgumentCaptor.forClass(LocalDateTime.class);
            verify(usuarioRepository).findAllChangedSince(captor.capture());
            assertThat(captor.getValue()).isEqualTo(DEFAULT_SINCE);
        }

        @Test
        void shouldPropagateNotFoundWhenSincronizacaoIsMissing() {
            when(sincronizacaoService.findByTabelaName(USUARIO_TABELA)).thenThrow(new ExcecaoNegocioNaoEncontrado("missing"));

            assertThatThrownBy(() -> usuarioService.buscarAlteradosDesdeSincronizacao(USUARIO_TABELA))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
            verify(usuarioRepository, never()).findAllChangedSince(any());
        }
    }
}
