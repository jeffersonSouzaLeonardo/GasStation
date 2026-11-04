package com.br.manager.domain.operacional.usecase;

import com.br.manager.domain.comum.exception.ExcecaoNegocio;
import com.br.manager.domain.comum.exception.ExcecaoNegocioNaoEncontrado;
import com.br.manager.domain.operacional.dto.VeiculoClienteInputDTO;
import com.br.manager.domain.operacional.dto.VeiculoClienteResponseDTO;
import com.br.manager.domain.operacional.entity.VeiculoCliente;
import com.br.manager.domain.operacional.fixture.ClienteVeiculoFixture;
import com.br.manager.domain.operacional.mapper.VeiculoClienteMapper;
import com.br.manager.domain.operacional.repository.VeiculoClienteRepository;
import com.br.manager.domain.operacional.support.OperacionalBViolacoes;
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
class VeiculoClienteServiceTest {

    @Mock
    private VeiculoClienteRepository clienteVeiculoRepository;

    @Spy
    private VeiculoClienteMapper clienteVeiculoMapper = new VeiculoClienteMapper();

    @InjectMocks
    private VeiculoClienteService clienteVeiculoService;

    @Nested
    class Create {

        @Test
        void shouldPersistAndReturnVeiculo() {
            UUID clienteId = UUID.randomUUID();
            when(clienteVeiculoRepository.saveAndFlush(any(VeiculoCliente.class))).thenAnswer(invocation -> {
                VeiculoCliente veiculo = invocation.getArgument(0);
                veiculo.setId(UUID.randomUUID());
                return veiculo;
            });

            VeiculoClienteResponseDTO response = clienteVeiculoService.create(ClienteVeiculoFixture.validoInput(clienteId));

            assertThat(response.getId()).isNotNull();
            assertThat(response.getClienteId()).isEqualTo(clienteId);
            assertThat(response.getModelo()).isEqualTo(ClienteVeiculoFixture.MODELO);
        }

        @Test
        void shouldConvertConstraintViolacoesIntoExcecaoNegocio() {
            when(clienteVeiculoRepository.saveAndFlush(any(VeiculoCliente.class)))
                    .thenThrow(OperacionalBViolacoes.violacoesOf(new VeiculoCliente()));

            assertThatThrownBy(() -> clienteVeiculoService.create(ClienteVeiculoFixture.validoInput(null)))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining("O ID de Cliente é obrigatório.");
        }

        @Test
        void shouldWrapPersistenceErrorInExcecaoNegocio() {
            when(clienteVeiculoRepository.saveAndFlush(any(VeiculoCliente.class)))
                    .thenThrow(new DataIntegrityViolationException("fk_veiculos_cliente"));

            assertThatThrownBy(() -> clienteVeiculoService.create(ClienteVeiculoFixture.validoInput(UUID.randomUUID())))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessage("Erro ao salvar veículo do cliente")
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class Update {

        @Test
        void shouldUpdateExistingVeiculo() {
            VeiculoCliente existing = ClienteVeiculoFixture.persistedEntity();
            VeiculoClienteInputDTO input = ClienteVeiculoFixture.validoInput(existing.getClienteId());
            input.setId(existing.getId());
            input.setMarca("Scania");
            when(clienteVeiculoRepository.findByIdAndAtivoTrue(existing.getId())).thenReturn(existing);
            when(clienteVeiculoRepository.saveAndFlush(existing)).thenReturn(existing);

            VeiculoClienteResponseDTO response = clienteVeiculoService.update(input);

            assertThat(response.getMarca()).isEqualTo("Scania");
        }

        @Test
        void shouldThrowNotFoundWhenVeiculoDoesNotExist() {
            VeiculoClienteInputDTO input = ClienteVeiculoFixture.validoInput(UUID.randomUUID());
            input.setId(UUID.randomUUID());

            assertThatThrownBy(() -> clienteVeiculoService.update(input))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class)
                    .hasMessageContaining(input.getId().toString());
            verify(clienteVeiculoRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldConvertConstraintViolacoesIntoExcecaoNegocio() {
            VeiculoCliente existing = ClienteVeiculoFixture.persistedEntity();
            VeiculoClienteInputDTO input = ClienteVeiculoFixture.validoInput(existing.getClienteId());
            input.setId(existing.getId());
            when(clienteVeiculoRepository.findByIdAndAtivoTrue(existing.getId())).thenReturn(existing);
            when(clienteVeiculoRepository.saveAndFlush(existing))
                    .thenThrow(OperacionalBViolacoes.violacoesOf(new VeiculoCliente()));

            assertThatThrownBy(() -> clienteVeiculoService.update(input))
                    .isExactlyInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining("Veiculo placa é obrigatório.");
        }

        @Test
        void shouldWrapPersistenceErrorInExcecaoNegocio() {
            VeiculoCliente existing = ClienteVeiculoFixture.persistedEntity();
            VeiculoClienteInputDTO input = ClienteVeiculoFixture.validoInput(existing.getClienteId());
            input.setId(existing.getId());
            when(clienteVeiculoRepository.findByIdAndAtivoTrue(existing.getId())).thenReturn(existing);
            when(clienteVeiculoRepository.saveAndFlush(existing)).thenThrow(new DataIntegrityViolationException("fk"));

            assertThatThrownBy(() -> clienteVeiculoService.update(input))
                    .isExactlyInstanceOf(ExcecaoNegocio.class)
                    .hasMessage("Erro ao atualizar veículo do cliente")
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class Queries {

        @Test
        void shouldListAtivoVeiculos() {
            when(clienteVeiculoRepository.findAllByAtivoTrue()).thenReturn(List.of(ClienteVeiculoFixture.persistedEntity()));

            assertThat(clienteVeiculoService.findAll()).hasSize(1);
        }

        @Test
        void shouldBuscaByPlaca() {
            when(clienteVeiculoRepository.findByPlacaContainingIgnoreCaseAndAtivoTrue("abc"))
                    .thenReturn(List.of(ClienteVeiculoFixture.persistedEntity()));

            assertThat(clienteVeiculoService.findByDescricao("abc"))
                    .extracting(VeiculoClienteResponseDTO::getPlaca)
                    .containsExactly(ClienteVeiculoFixture.PLACA);
        }

        @Test
        void shouldFallbackToFindAllWhenDescricaoIsVazio() {
            when(clienteVeiculoRepository.findAllByAtivoTrue()).thenReturn(List.of());

            assertThat(clienteVeiculoService.findByDescricao(null)).isEmpty();
            verify(clienteVeiculoRepository, never()).findByPlacaContainingIgnoreCaseAndAtivoTrue(any());
        }

        @Test
        void shouldFindById() {
            VeiculoCliente existing = ClienteVeiculoFixture.persistedEntity();
            when(clienteVeiculoRepository.findByIdAndAtivoTrue(existing.getId())).thenReturn(existing);

            assertThat(clienteVeiculoService.find(existing.getId()).getId()).isEqualTo(existing.getId());
        }

        @Test
        void shouldThrowNotFoundWhenIdDoesNotExist() {
            UUID id = UUID.randomUUID();

            assertThatThrownBy(() -> clienteVeiculoService.find(id))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class)
                    .hasMessageContaining(id.toString());
        }
    }

    @Nested
    class Delete {

        @Test
        void shouldInactivateVeiculo() {
            VeiculoCliente existing = ClienteVeiculoFixture.persistedEntity();
            when(clienteVeiculoRepository.findById(existing.getId())).thenReturn(Optional.of(existing));

            clienteVeiculoService.delete(existing.getId());

            ArgumentCaptor<VeiculoCliente> captor = ArgumentCaptor.forClass(VeiculoCliente.class);
            verify(clienteVeiculoRepository).saveAndFlush(captor.capture());
            assertThat(captor.getValue().getAtivo()).isFalse();
        }

        @Test
        void shouldThrowNotFoundWhenDeletingUnknownVeiculo() {
            UUID id = UUID.randomUUID();
            when(clienteVeiculoRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> clienteVeiculoService.delete(id)).isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
            verify(clienteVeiculoRepository, never()).saveAndFlush(any());
        }
    }
}
