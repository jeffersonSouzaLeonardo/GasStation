package com.br.manager.domain.organizacao.usecase;

import com.br.manager.domain.comum.exception.ExcecaoNegocio;
import com.br.manager.domain.comum.exception.ExcecaoNegocioNaoEncontrado;
import com.br.manager.domain.organizacao.dto.VinculoUsuarioInputDTO;
import com.br.manager.domain.organizacao.dto.VinculoUsuarioResponseDTO;
import com.br.manager.domain.organizacao.entity.VinculoUsuario;
import com.br.manager.domain.organizacao.fixture.ConstraintViolationFixture;
import com.br.manager.domain.organizacao.fixture.VinculoUsuarioFixture;
import com.br.manager.domain.organizacao.mapper.VinculoUsuarioMapper;
import com.br.manager.domain.organizacao.repository.VinculoUsuarioRepository;
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
class VinculoUsuarioServiceTest {

    private static final String CARGO_OBRIGATORIO = "O papel é obrigatório.";

    @Mock
    private VinculoUsuarioRepository usuarioVinculoRepository;

    @Spy
    private VinculoUsuarioMapper usuarioVinculoMapper = new VinculoUsuarioMapper();

    @InjectMocks
    private VinculoUsuarioService usuarioVinculoService;

    private static ConstraintViolationException cargoViolation() {
        VinculoUsuario invalido = VinculoUsuarioFixture.persistedEntity();
        invalido.setCargo(null);
        return ConstraintViolationFixture.violacoesOf(invalido);
    }

    @Nested
    class Create {

        @Test
        void shouldPersistAndReturnVinculoUsuario() {
            when(usuarioVinculoRepository.saveAndFlush(any(VinculoUsuario.class))).thenAnswer(invocation -> {
                VinculoUsuario usuarioVinculo = invocation.getArgument(0);
                usuarioVinculo.setId(UUID.randomUUID());
                return usuarioVinculo;
            });
            VinculoUsuarioInputDTO input = VinculoUsuarioFixture.validoInput();

            VinculoUsuarioResponseDTO response = usuarioVinculoService.create(input);

            assertThat(response.getId()).isNotNull();
            assertThat(response.getUsuarioId()).isEqualTo(input.getUsuarioId());
            assertThat(response.getCargo()).isEqualTo(VinculoUsuarioFixture.CARGO);
        }

        @Test
        void shouldTranslateConstraintViolationIntoExcecaoNegocio() {
            when(usuarioVinculoRepository.saveAndFlush(any(VinculoUsuario.class))).thenThrow(cargoViolation());

            assertThatThrownBy(() -> usuarioVinculoService.create(VinculoUsuarioFixture.validoInput()))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining(CARGO_OBRIGATORIO);
        }

        @Test
        void shouldWrapPersistenceErrorInExcecaoNegocioWithCargo() {
            when(usuarioVinculoRepository.saveAndFlush(any(VinculoUsuario.class)))
                    .thenThrow(new DataIntegrityViolationException("fk usuario"));

            assertThatThrownBy(() -> usuarioVinculoService.create(VinculoUsuarioFixture.validoInput()))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining(VinculoUsuarioFixture.CARGO.name())
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }

        @Test
        void shouldWrapErrorWithoutCargoWhenCargoIsNull() {
            VinculoUsuarioInputDTO input = VinculoUsuarioFixture.validoInput();
            input.setCargo(null);
            when(usuarioVinculoRepository.saveAndFlush(any(VinculoUsuario.class)))
                    .thenThrow(new DataIntegrityViolationException("cargo null"));

            assertThatThrownBy(() -> usuarioVinculoService.create(input))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessage("Erro ao salvar vínculo de usuário ");
        }
    }

    @Nested
    class Update {

        @Test
        void shouldUpdateExistingVinculoUsuario() {
            VinculoUsuario existing = VinculoUsuarioFixture.persistedEntity();
            VinculoUsuarioInputDTO input = VinculoUsuarioFixture.validoInput();
            input.setId(existing.getId());
            input.setCargo(VinculoUsuarioFixture.OUTROS_CARGO);
            when(usuarioVinculoRepository.findByIdAndAtivoTrue(existing.getId())).thenReturn(existing);
            when(usuarioVinculoRepository.saveAndFlush(existing)).thenReturn(existing);

            VinculoUsuarioResponseDTO response = usuarioVinculoService.update(input);

            assertThat(response.getId()).isEqualTo(existing.getId());
            assertThat(response.getCargo()).isEqualTo(VinculoUsuarioFixture.OUTROS_CARGO);
        }

        @Test
        void shouldThrowNotFoundWhenVinculoUsuarioDoesNotExist() {
            VinculoUsuarioInputDTO input = VinculoUsuarioFixture.validoInput();
            input.setId(UUID.randomUUID());

            assertThatThrownBy(() -> usuarioVinculoService.update(input))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class)
                    .hasMessageContaining(input.getId().toString());
            verify(usuarioVinculoRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldTranslateConstraintViolationIntoExcecaoNegocio() {
            VinculoUsuario existing = VinculoUsuarioFixture.persistedEntity();
            VinculoUsuarioInputDTO input = VinculoUsuarioFixture.validoInput();
            input.setId(existing.getId());
            when(usuarioVinculoRepository.findByIdAndAtivoTrue(existing.getId())).thenReturn(existing);
            when(usuarioVinculoRepository.saveAndFlush(existing)).thenThrow(cargoViolation());

            assertThatThrownBy(() -> usuarioVinculoService.update(input))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining(CARGO_OBRIGATORIO);
        }

        @Test
        void shouldWrapPersistenceErrorInExcecaoNegocio() {
            VinculoUsuario existing = VinculoUsuarioFixture.persistedEntity();
            VinculoUsuarioInputDTO input = VinculoUsuarioFixture.validoInput();
            input.setId(existing.getId());
            when(usuarioVinculoRepository.findByIdAndAtivoTrue(existing.getId())).thenReturn(existing);
            when(usuarioVinculoRepository.saveAndFlush(existing)).thenThrow(new DataIntegrityViolationException("fk"));

            assertThatThrownBy(() -> usuarioVinculoService.update(input))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .isNotInstanceOf(ExcecaoNegocioNaoEncontrado.class)
                    .hasMessageContaining(VinculoUsuarioFixture.CARGO.name())
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class Queries {

        @Test
        void shouldListAtivoVinculoUsuarios() {
            when(usuarioVinculoRepository.findAllByAtivoTrue()).thenReturn(List.of(VinculoUsuarioFixture.persistedEntity()));

            assertThat(usuarioVinculoService.findAll()).hasSize(1);
        }

        @Test
        void shouldBuscaByCargoText() {
            when(usuarioVinculoRepository.findByCargoTextAndAtivoTrue("mana"))
                    .thenReturn(List.of(VinculoUsuarioFixture.persistedEntity()));

            assertThat(usuarioVinculoService.findByName("mana")).hasSize(1);
        }

        @Test
        void shouldFallbackToFindAllWhenDescricaoIsVazio() {
            when(usuarioVinculoRepository.findAllByAtivoTrue()).thenReturn(List.of());

            assertThat(usuarioVinculoService.findByDescricao(null)).isEmpty();
            verify(usuarioVinculoRepository, never()).findByCargoTextAndAtivoTrue(any());
        }

        @Test
        void shouldFindByCargo() {
            when(usuarioVinculoRepository.findByCargoAndAtivoTrue(VinculoUsuarioFixture.CARGO))
                    .thenReturn(List.of(VinculoUsuarioFixture.persistedEntity()));

            assertThat(usuarioVinculoService.findByCargo(VinculoUsuarioFixture.CARGO))
                    .extracting(VinculoUsuarioResponseDTO::getCargo)
                    .containsExactly(VinculoUsuarioFixture.CARGO);
        }

        @Test
        void shouldFindById() {
            VinculoUsuario existing = VinculoUsuarioFixture.persistedEntity();
            when(usuarioVinculoRepository.findByIdAndAtivoTrue(existing.getId())).thenReturn(existing);

            assertThat(usuarioVinculoService.find(existing.getId()).getId()).isEqualTo(existing.getId());
        }

        @Test
        void shouldReturnEntityById() {
            VinculoUsuario existing = VinculoUsuarioFixture.persistedEntity();
            when(usuarioVinculoRepository.findByIdAndAtivoTrue(existing.getId())).thenReturn(existing);

            assertThat(usuarioVinculoService.getVinculoUsuarioEntityById(existing.getId())).isSameAs(existing);
        }

        @Test
        void shouldThrowNotFoundWhenIdDoesNotExist() {
            UUID id = UUID.randomUUID();

            assertThatThrownBy(() -> usuarioVinculoService.find(id)).isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
            assertThatThrownBy(() -> usuarioVinculoService.getVinculoUsuarioEntityById(id)).isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
        }
    }

    @Nested
    class Delete {

        @Test
        void shouldInactivateVinculoUsuario() {
            VinculoUsuario existing = VinculoUsuarioFixture.persistedEntity();
            when(usuarioVinculoRepository.findById(existing.getId())).thenReturn(Optional.of(existing));

            usuarioVinculoService.delete(existing.getId());

            ArgumentCaptor<VinculoUsuario> captor = ArgumentCaptor.forClass(VinculoUsuario.class);
            verify(usuarioVinculoRepository).saveAndFlush(captor.capture());
            assertThat(captor.getValue().getAtivo()).isFalse();
        }

        @Test
        void shouldThrowNotFoundWhenDeletingUnknownVinculoUsuario() {
            UUID id = UUID.randomUUID();
            when(usuarioVinculoRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> usuarioVinculoService.delete(id)).isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
        }

        @Test
        void shouldTranslateConstraintViolationIntoExcecaoNegocio() {
            VinculoUsuario existing = VinculoUsuarioFixture.persistedEntity();
            when(usuarioVinculoRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            when(usuarioVinculoRepository.saveAndFlush(existing)).thenThrow(cargoViolation());

            assertThatThrownBy(() -> usuarioVinculoService.delete(existing.getId()))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining(CARGO_OBRIGATORIO);
        }

        @Test
        void shouldWrapPersistenceErrorInExcecaoNegocio() {
            VinculoUsuario existing = VinculoUsuarioFixture.persistedEntity();
            when(usuarioVinculoRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            when(usuarioVinculoRepository.saveAndFlush(existing)).thenThrow(new DataIntegrityViolationException("fk"));

            assertThatThrownBy(() -> usuarioVinculoService.delete(existing.getId()))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessage("Erro ao excluir vínculo de usuário")
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }
}
