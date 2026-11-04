package com.br.manager.domain.aquisicao_preco.service;

import com.br.manager.domain.comum.exception.ExcecaoNegocio;
import com.br.manager.domain.comum.exception.ExcecaoNegocioNaoEncontrado;
import com.br.manager.domain.organizacao.entity.Usuario;
import com.br.manager.domain.organizacao.repository.UsuarioRepository;
import com.br.manager.domain.aquisicao_preco.dto.ItemPrecoInputDTO;
import com.br.manager.domain.aquisicao_preco.dto.ItemPrecoResponseDTO;
import com.br.manager.domain.aquisicao_preco.dto.TabelaPrecoInputDTO;
import com.br.manager.domain.aquisicao_preco.dto.TabelaPrecoResponseDTO;
import com.br.manager.domain.aquisicao_preco.entity.TabelaPreco;
import com.br.manager.domain.aquisicao_preco.fixture.AquisicaoPrecoReferenciaFixture;
import com.br.manager.domain.aquisicao_preco.fixture.ItemPrecoFixture;
import com.br.manager.domain.aquisicao_preco.fixture.TabelaPrecoFixture;
import com.br.manager.domain.aquisicao_preco.mapper.ItemPrecoMapper;
import com.br.manager.domain.aquisicao_preco.mapper.TabelaPrecoMapper;
import com.br.manager.domain.aquisicao_preco.repository.TabelaPrecoRepository;
import com.br.manager.domain.aquisicao_preco.support.AquisicaoPrecoViolacoes;
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TabelaPrecoServiceTest {

    @Mock
    private TabelaPrecoRepository precoTabelaRepository;

    @Mock
    private ItemPrecoService precoItemService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Spy
    private TabelaPrecoMapper precoTabelaMapper = new TabelaPrecoMapper();

    @Spy
    private ItemPrecoMapper precoItemMapper = new ItemPrecoMapper();

    @InjectMocks
    private TabelaPrecoService precoTabelaService;

    private final Usuario creator = AquisicaoPrecoReferenciaFixture.ativoUsuario();

    private TabelaPrecoInputDTO validoInput() {
        return TabelaPrecoFixture.validoInput(creator.getId(), List.of(ItemPrecoFixture.validoInput(null, UUID.randomUUID())));
    }

    @Nested
    class Create {

        @Test
        void shouldRejectTabelaWithoutItens() {
            TabelaPrecoInputDTO input = TabelaPrecoFixture.validoInput(creator.getId(), List.of());

            assertThatThrownBy(() -> precoTabelaService.create(input))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining("pelo menos um item");
            verify(precoTabelaRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldRejectTabelaWithNullItens() {
            TabelaPrecoInputDTO input = TabelaPrecoFixture.validoInput(creator.getId(), null);

            assertThatThrownBy(() -> precoTabelaService.create(input))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining("pelo menos um item");
        }

        @Test
        void shouldPersistTabelaWithCreatorAndSaveItens() {
            TabelaPrecoInputDTO input = validoInput();
            input.setId(UUID.randomUUID());
            ItemPrecoResponseDTO itemResponse = new ItemPrecoResponseDTO();
            when(usuarioRepository.findById(creator.getId())).thenReturn(Optional.of(creator));
            ArgumentCaptor<TabelaPreco> captor = ArgumentCaptor.forClass(TabelaPreco.class);
            when(precoTabelaRepository.saveAndFlush(captor.capture())).thenAnswer(invocation -> {
                TabelaPreco tabelaPreco = invocation.getArgument(0);
                assertThat(tabelaPreco.getId()).isNull();
                tabelaPreco.setId(UUID.randomUUID());
                return tabelaPreco;
            });
            when(precoItemService.findByTabelaPreco(any())).thenReturn(List.of(itemResponse));

            TabelaPrecoResponseDTO response = precoTabelaService.create(input);

            assertThat(captor.getValue().getCriadoPor()).isSameAs(creator);
            assertThat(response.getId()).isEqualTo(captor.getValue().getId());
            assertThat(response.getNome()).isEqualTo(TabelaPrecoFixture.NAME);
            assertThat(response.getCriadoPor()).isEqualTo(creator.getId());
            assertThat(response.getItensPreco()).containsExactly(itemResponse);
            verify(precoItemService).saveItemPrecos(response.getId(), input.getItensPreco());
        }

        @Test
        void shouldThrowNotFoundWhenCreatorDoesNotExist() {
            TabelaPrecoInputDTO input = validoInput();
            when(usuarioRepository.findById(creator.getId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> precoTabelaService.create(input))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class)
                    .hasMessageContaining(creator.getId().toString());
            verify(precoTabelaRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldThrowNotFoundWhenCreatorIsInativo() {
            Usuario inativo = AquisicaoPrecoReferenciaFixture.inativoUsuario();
            TabelaPrecoInputDTO input = TabelaPrecoFixture.validoInput(inativo.getId(), validoInput().getItensPreco());
            when(usuarioRepository.findById(inativo.getId())).thenReturn(Optional.of(inativo));

            assertThatThrownBy(() -> precoTabelaService.create(input))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
            verify(precoTabelaRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldConvertConstraintViolationIntoExcecaoNegocio() {
            when(usuarioRepository.findById(creator.getId())).thenReturn(Optional.of(creator));
            when(precoTabelaRepository.saveAndFlush(any(TabelaPreco.class)))
                    .thenThrow(AquisicaoPrecoViolacoes.constraintViolationException());

            assertThatThrownBy(() -> precoTabelaService.create(validoInput()))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining(AquisicaoPrecoViolacoes.VIOLATION_MESSAGE);
        }

        @Test
        void shouldWrapPersistenceErrorInExcecaoNegocio() {
            when(usuarioRepository.findById(creator.getId())).thenReturn(Optional.of(creator));
            when(precoTabelaRepository.saveAndFlush(any(TabelaPreco.class)))
                    .thenThrow(new DataIntegrityViolationException("fk"));

            assertThatThrownBy(() -> precoTabelaService.create(validoInput()))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining(TabelaPrecoFixture.NAME)
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class Update {

        @Test
        void shouldUpdateFieldsAndReplaceItensWhenItensAreInformed() {
            TabelaPreco existing = TabelaPrecoFixture.persistedEntity(creator);
            TabelaPrecoInputDTO input = new TabelaPrecoInputDTO();
            input.setId(existing.getId());
            input.setNome(TabelaPrecoFixture.OUTROS_NAME);
            List<ItemPrecoInputDTO> itens = List.of(ItemPrecoFixture.validoInput(existing.getId(), UUID.randomUUID()));
            input.setItensPreco(itens);
            when(precoTabelaRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            when(precoTabelaRepository.saveAndFlush(existing)).thenReturn(existing);

            TabelaPrecoResponseDTO response = precoTabelaService.update(input);

            assertThat(response.getNome()).isEqualTo(TabelaPrecoFixture.OUTROS_NAME);
            assertThat(response.getCriadoPor()).isEqualTo(creator.getId());
            verify(precoItemService).replaceByTabelaPreco(existing, itens);
            verify(precoItemService).findByTabelaPreco(existing.getId());
        }

        @Test
        void shouldKeepItensWhenItensAreNotInformed() {
            TabelaPreco existing = TabelaPrecoFixture.persistedEntity(creator);
            TabelaPrecoInputDTO input = new TabelaPrecoInputDTO();
            input.setId(existing.getId());
            when(precoTabelaRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            when(precoTabelaRepository.saveAndFlush(existing)).thenReturn(existing);

            precoTabelaService.update(input);

            verify(precoItemService, never()).replaceByTabelaPreco(any(), any());
        }

        @Test
        void shouldChangeCreatorWhenInformed() {
            TabelaPreco existing = TabelaPrecoFixture.persistedEntity(creator);
            Usuario newCreator = AquisicaoPrecoReferenciaFixture.ativoUsuario();
            TabelaPrecoInputDTO input = new TabelaPrecoInputDTO();
            input.setId(existing.getId());
            input.setCriadoPor(newCreator.getId());
            when(precoTabelaRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            when(usuarioRepository.findById(newCreator.getId())).thenReturn(Optional.of(newCreator));
            when(precoTabelaRepository.saveAndFlush(existing)).thenReturn(existing);

            TabelaPrecoResponseDTO response = precoTabelaService.update(input);

            assertThat(response.getCriadoPor()).isEqualTo(newCreator.getId());
        }

        @Test
        void shouldThrowNotFoundWhenTabelaDoesNotExist() {
            TabelaPrecoInputDTO input = validoInput();
            input.setId(UUID.randomUUID());
            when(precoTabelaRepository.findById(input.getId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> precoTabelaService.update(input))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class)
                    .hasMessageContaining(input.getId().toString());
            verify(precoTabelaRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldThrowNotFoundWhenNewCreatorDoesNotExist() {
            TabelaPreco existing = TabelaPrecoFixture.persistedEntity(creator);
            TabelaPrecoInputDTO input = new TabelaPrecoInputDTO();
            input.setId(existing.getId());
            input.setCriadoPor(UUID.randomUUID());
            when(precoTabelaRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            when(usuarioRepository.findById(input.getCriadoPor())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> precoTabelaService.update(input))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
        }

        @Test
        void shouldThrowNotFoundWhenNewCreatorIsInativo() {
            TabelaPreco existing = TabelaPrecoFixture.persistedEntity(creator);
            Usuario inativo = AquisicaoPrecoReferenciaFixture.inativoUsuario();
            TabelaPrecoInputDTO input = new TabelaPrecoInputDTO();
            input.setId(existing.getId());
            input.setCriadoPor(inativo.getId());
            when(precoTabelaRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            when(usuarioRepository.findById(inativo.getId())).thenReturn(Optional.of(inativo));

            assertThatThrownBy(() -> precoTabelaService.update(input))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
            verify(precoTabelaRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldConvertConstraintViolationIntoExcecaoNegocio() {
            TabelaPreco existing = TabelaPrecoFixture.persistedEntity(creator);
            TabelaPrecoInputDTO input = new TabelaPrecoInputDTO();
            input.setId(existing.getId());
            when(precoTabelaRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            when(precoTabelaRepository.saveAndFlush(existing)).thenThrow(AquisicaoPrecoViolacoes.constraintViolationException());

            assertThatThrownBy(() -> precoTabelaService.update(input))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining(AquisicaoPrecoViolacoes.VIOLATION_MESSAGE);
        }

        @Test
        void shouldWrapPersistenceErrorInExcecaoNegocio() {
            TabelaPreco existing = TabelaPrecoFixture.persistedEntity(creator);
            TabelaPrecoInputDTO input = new TabelaPrecoInputDTO();
            input.setId(existing.getId());
            input.setNome(TabelaPrecoFixture.OUTROS_NAME);
            when(precoTabelaRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            when(precoTabelaRepository.saveAndFlush(existing)).thenThrow(new DataIntegrityViolationException("db"));

            assertThatThrownBy(() -> precoTabelaService.update(input))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining(TabelaPrecoFixture.OUTROS_NAME)
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class Queries {

        @Test
        void shouldListAllTables() {
            when(precoTabelaRepository.findAll()).thenReturn(List.of(TabelaPrecoFixture.persistedEntity(creator)));

            assertThat(precoTabelaService.findAll()).hasSize(1);
        }

        @Test
        void shouldBuscaByDescricao() {
            when(precoTabelaRepository.findByNameContainingIgnoreCase("janeiro"))
                    .thenReturn(List.of(TabelaPrecoFixture.persistedEntity(creator)));

            assertThat(precoTabelaService.findByDescricao("janeiro")).hasSize(1);
        }

        @Test
        void shouldFallbackToFindAllWhenDescricaoIsVazio() {
            when(precoTabelaRepository.findAll()).thenReturn(List.of());

            assertThat(precoTabelaService.findByDescricao(" ")).isEmpty();
            verify(precoTabelaRepository, never()).findByNameContainingIgnoreCase(any());
        }

        @Test
        void shouldBuscaByNameUsingDescricaoBusca() {
            when(precoTabelaRepository.findByNameContainingIgnoreCase("tabela")).thenReturn(List.of());

            assertThat(precoTabelaService.findByName("tabela")).isEmpty();
            verify(precoTabelaRepository).findByNameContainingIgnoreCase("tabela");
        }

        @Test
        void shouldFindById() {
            TabelaPreco existing = TabelaPrecoFixture.persistedEntity(creator);
            when(precoTabelaRepository.findById(existing.getId())).thenReturn(Optional.of(existing));

            assertThat(precoTabelaService.find(existing.getId()).getId()).isEqualTo(existing.getId());
        }

        @Test
        void shouldReturnEntityById() {
            TabelaPreco existing = TabelaPrecoFixture.persistedEntity(creator);
            when(precoTabelaRepository.findById(existing.getId())).thenReturn(Optional.of(existing));

            assertThat(precoTabelaService.getTabelaPrecoEntityById(existing.getId())).isSameAs(existing);
        }

        @Test
        void shouldThrowNotFoundWhenIdDoesNotExist() {
            UUID id = UUID.randomUUID();
            when(precoTabelaRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> precoTabelaService.find(id)).isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
            assertThatThrownBy(() -> precoTabelaService.getTabelaPrecoEntityById(id)).isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
        }
    }

    @Nested
    class Delete {

        @Test
        void shouldDeleteExistingTabela() {
            TabelaPreco existing = TabelaPrecoFixture.persistedEntity(creator);
            when(precoTabelaRepository.findById(existing.getId())).thenReturn(Optional.of(existing));

            precoTabelaService.delete(existing.getId());

            verify(precoTabelaRepository).delete(existing);
        }

        @Test
        void shouldThrowNotFoundWhenDeletingUnknownTabela() {
            UUID id = UUID.randomUUID();
            when(precoTabelaRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> precoTabelaService.delete(id)).isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
            verify(precoTabelaRepository, never()).delete(any());
        }

        @Test
        void shouldConvertConstraintViolationIntoExcecaoNegocio() {
            TabelaPreco existing = TabelaPrecoFixture.persistedEntity(creator);
            when(precoTabelaRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            doThrow(AquisicaoPrecoViolacoes.constraintViolationException()).when(precoTabelaRepository).delete(existing);

            assertThatThrownBy(() -> precoTabelaService.delete(existing.getId()))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining(AquisicaoPrecoViolacoes.VIOLATION_MESSAGE);
        }

        @Test
        void shouldWrapPersistenceErrorInExcecaoNegocio() {
            TabelaPreco existing = TabelaPrecoFixture.persistedEntity(creator);
            when(precoTabelaRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            doThrow(new DataIntegrityViolationException("fk")).when(precoTabelaRepository).delete(existing);

            assertThatThrownBy(() -> precoTabelaService.delete(existing.getId()))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining("Erro ao excluir tabela de preços")
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }
}
