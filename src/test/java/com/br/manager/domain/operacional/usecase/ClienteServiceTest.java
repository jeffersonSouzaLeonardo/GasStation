package com.br.manager.domain.operacional.usecase;

import com.br.manager.domain.comum.exception.ExcecaoNegocio;
import com.br.manager.domain.comum.exception.ExcecaoNegocioNaoEncontrado;
import com.br.manager.domain.operacional.dto.ClienteInputDTO;
import com.br.manager.domain.operacional.dto.ClienteResponseDTO;
import com.br.manager.domain.operacional.entity.Cliente;
import com.br.manager.domain.operacional.fixture.ClienteFixture;
import com.br.manager.domain.operacional.mapper.ClienteMapper;
import com.br.manager.domain.operacional.repository.ClienteRepository;
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
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Spy
    private ClienteMapper clienteMapper = new ClienteMapper();

    @InjectMocks
    private ClienteService clienteService;

    @Nested
    class Create {

        @Test
        void shouldPersistAndReturnCliente() {
            when(clienteRepository.saveAndFlush(any(Cliente.class))).thenAnswer(invocation -> {
                Cliente cliente = invocation.getArgument(0);
                cliente.setId(UUID.randomUUID());
                return cliente;
            });

            ClienteResponseDTO response = clienteService.create(ClienteFixture.validoInput());

            assertThat(response.getId()).isNotNull();
            assertThat(response.getNome()).isEqualTo(ClienteFixture.NAME);
            assertThat(response.getLimiteCredito()).isEqualByComparingTo(ClienteFixture.CREDITO_LIMITE);
        }

        @Test
        void shouldConvertConstraintViolacoesIntoExcecaoNegocio() {
            when(clienteRepository.saveAndFlush(any(Cliente.class)))
                    .thenThrow(OperacionalBViolacoes.violacoesOf(new Cliente()));

            assertThatThrownBy(() -> clienteService.create(ClienteFixture.validoInput()))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining("Cliente nome é obrigatório.");
        }

        @Test
        void shouldWrapPersistenceErrorInExcecaoNegocio() {
            when(clienteRepository.saveAndFlush(any(Cliente.class)))
                    .thenThrow(new DataIntegrityViolationException("too long"));

            assertThatThrownBy(() -> clienteService.create(ClienteFixture.validoInput()))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessage("Erro ao salvar cliente")
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class Update {

        @Test
        void shouldUpdateExistingCliente() {
            Cliente existing = ClienteFixture.persistedEntity();
            ClienteInputDTO input = ClienteFixture.outrosInput();
            input.setId(existing.getId());
            when(clienteRepository.findByIdAndAtivoTrue(existing.getId())).thenReturn(existing);
            when(clienteRepository.saveAndFlush(existing)).thenReturn(existing);

            ClienteResponseDTO response = clienteService.update(input);

            assertThat(response.getNome()).isEqualTo(ClienteFixture.OUTROS_NAME);
            assertThat(response.getCnpjCpf()).isEqualTo(ClienteFixture.CPF);
        }

        @Test
        void shouldThrowNotFoundWhenClienteDoesNotExist() {
            ClienteInputDTO input = ClienteFixture.validoInput();
            input.setId(UUID.randomUUID());

            assertThatThrownBy(() -> clienteService.update(input))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class)
                    .hasMessageContaining(input.getId().toString());
            verify(clienteRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldConvertConstraintViolacoesIntoExcecaoNegocio() {
            Cliente existing = ClienteFixture.persistedEntity();
            ClienteInputDTO input = ClienteFixture.validoInput();
            input.setId(existing.getId());
            when(clienteRepository.findByIdAndAtivoTrue(existing.getId())).thenReturn(existing);
            when(clienteRepository.saveAndFlush(existing)).thenThrow(OperacionalBViolacoes.violacoesOf(new Cliente()));

            assertThatThrownBy(() -> clienteService.update(input))
                    .isExactlyInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining("Person type é obrigatório.");
        }

        @Test
        void shouldWrapPersistenceErrorInExcecaoNegocio() {
            Cliente existing = ClienteFixture.persistedEntity();
            ClienteInputDTO input = ClienteFixture.validoInput();
            input.setId(existing.getId());
            when(clienteRepository.findByIdAndAtivoTrue(existing.getId())).thenReturn(existing);
            when(clienteRepository.saveAndFlush(existing)).thenThrow(new DataIntegrityViolationException("too long"));

            assertThatThrownBy(() -> clienteService.update(input))
                    .isExactlyInstanceOf(ExcecaoNegocio.class)
                    .hasMessage("Erro ao atualizar cliente")
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class Queries {

        @Test
        void shouldListAtivoClientes() {
            when(clienteRepository.findAllByAtivoTrue()).thenReturn(List.of(ClienteFixture.persistedEntity()));

            assertThat(clienteService.findAll()).hasSize(1);
        }

        @Test
        void shouldBuscaByName() {
            when(clienteRepository.findByNameContainingIgnoreCaseAndAtivoTrue("silva"))
                    .thenReturn(List.of(ClienteFixture.persistedEntity()));

            assertThat(clienteService.findByDescricao("silva"))
                    .extracting(ClienteResponseDTO::getNome)
                    .containsExactly(ClienteFixture.NAME);
        }

        @Test
        void shouldFallbackToFindAllWhenDescricaoIsVazio() {
            when(clienteRepository.findAllByAtivoTrue()).thenReturn(List.of());

            assertThat(clienteService.findByDescricao("")).isEmpty();
            verify(clienteRepository, never()).findByNameContainingIgnoreCaseAndAtivoTrue(any());
        }

        @Test
        void shouldFindById() {
            Cliente existing = ClienteFixture.persistedEntity();
            when(clienteRepository.findByIdAndAtivoTrue(existing.getId())).thenReturn(existing);

            assertThat(clienteService.find(existing.getId()).getId()).isEqualTo(existing.getId());
        }

        @Test
        void shouldThrowNotFoundWhenIdDoesNotExist() {
            UUID id = UUID.randomUUID();

            assertThatThrownBy(() -> clienteService.find(id))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class)
                    .hasMessageContaining(id.toString());
        }
    }

    @Nested
    class Delete {

        @Test
        void shouldInactivateCliente() {
            Cliente existing = ClienteFixture.persistedEntity();
            when(clienteRepository.findById(existing.getId())).thenReturn(Optional.of(existing));

            clienteService.delete(existing.getId());

            ArgumentCaptor<Cliente> captor = ArgumentCaptor.forClass(Cliente.class);
            verify(clienteRepository).saveAndFlush(captor.capture());
            assertThat(captor.getValue().getAtivo()).isFalse();
        }

        @Test
        void shouldThrowNotFoundWhenDeletingUnknownCliente() {
            UUID id = UUID.randomUUID();
            when(clienteRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> clienteService.delete(id)).isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
            verify(clienteRepository, never()).saveAndFlush(any());
        }
    }
}
