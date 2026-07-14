package it.scuola.materie_service;

import it.scuola.materie_service.exception.ResourceNotFoundException;
import it.scuola.materie_service.model.Materia;
import it.scuola.materie_service.model.MateriaDTO;
import it.scuola.materie_service.model.MateriaResponseDTO;
import it.scuola.materie_service.model.MateriaUpdateDTO;
import it.scuola.materie_service.model.TipoMateria;
import it.scuola.materie_service.service.MateriaClasseRepository;
import it.scuola.materie_service.service.MateriaRepository;
import it.scuola.materie_service.service.MateriaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test unitari del MateriaService.
 * Puro Mockito, nessun contesto Spring, nessun database.
 */
@ExtendWith(MockitoExtension.class)
class MateriaServiceTest {

    @Mock
    private MateriaRepository materiaRepository;

    @Mock
    private MateriaClasseRepository materiaClasseRepository;

    @InjectMocks
    private MateriaService materiaService;

    private Materia materiaEsistente;
    private UUID idEsistente;

    @BeforeEach
    void setUp() {
        idEsistente = UUID.randomUUID();
        materiaEsistente = new Materia();
        materiaEsistente.setNome("Matematica");
        materiaEsistente.setCodice("MAT");
        materiaEsistente.setDescrizione("Matematica e calcolo");
        materiaEsistente.setOreSettimanali(5);
        materiaEsistente.setTipoMateria(TipoMateria.TEORICA);
        materiaEsistente.setCreatoIl(LocalDateTime.now());
    }

    @Test
    @DisplayName("creaMateria - dovrebbe creare e restituire la materia")
    void creaMateria_dovrebbeCreareLaMateria() {
        MateriaDTO dto = new MateriaDTO("Matematica", "MAT", "Calcolo", 5, TipoMateria.TEORICA);
        when(materiaRepository.existsByCodice("MAT")).thenReturn(false);
        when(materiaRepository.existsByNome("Matematica")).thenReturn(false);
        when(materiaRepository.save(any(Materia.class))).thenReturn(materiaEsistente);

        MateriaResponseDTO risultato = materiaService.creaMateria(dto);

        assertThat(risultato).isNotNull();
        assertThat(risultato.nome()).isEqualTo("Matematica");
        assertThat(risultato.codice()).isEqualTo("MAT");
        assertThat(risultato.tipoMateria()).isEqualTo(TipoMateria.TEORICA);
        verify(materiaRepository, times(1)).save(any(Materia.class));
    }

    @Test
    @DisplayName("creaMateria - dovrebbe lanciare eccezione se codice già esiste")
    void creaMateria_dovrebbeLanciareEccezioneSeCodiceDuplicato() {
        MateriaDTO dto = new MateriaDTO("Matematica", "MAT", null, 5, TipoMateria.TEORICA);
        when(materiaRepository.existsByCodice("MAT")).thenReturn(true);

        assertThatThrownBy(() -> materiaService.creaMateria(dto))
                .isInstanceOf(RuntimeException.class);
        verify(materiaRepository, never()).save(any());
    }

    @Test
    @DisplayName("creaMateria - dovrebbe lanciare eccezione se nome già esiste")
    void creaMateria_dovrebbeLanciareEccezioneSeNomeDuplicato() {
        MateriaDTO dto = new MateriaDTO("Matematica", "MAT", null, 5, TipoMateria.TEORICA);
        when(materiaRepository.existsByCodice("MAT")).thenReturn(false);
        when(materiaRepository.existsByNome("Matematica")).thenReturn(true);

        assertThatThrownBy(() -> materiaService.creaMateria(dto))
                .isInstanceOf(RuntimeException.class);
        verify(materiaRepository, never()).save(any());
    }

    @Test
    @DisplayName("trovaTutte - dovrebbe restituire lista di materie attive")
    void trovaTutte_dovrebbeRestituireLista() {
        when(materiaRepository.findAllByActiveTrue()).thenReturn(List.of(materiaEsistente));

        List<MateriaResponseDTO> risultato = materiaService.trovaTutte();

        assertThat(risultato).hasSize(1);
        assertThat(risultato.get(0).nome()).isEqualTo("Matematica");
    }

    @Test
    @DisplayName("trovaTutte - dovrebbe restituire lista vuota se non ci sono materie attive")
    void trovaTutte_dovrebbeRestituireListaVuota() {
        when(materiaRepository.findAllByActiveTrue()).thenReturn(List.of());

        List<MateriaResponseDTO> risultato = materiaService.trovaTutte();

        assertThat(risultato).isEmpty();
    }

    @Test
    @DisplayName("trovaPerID - dovrebbe restituire la materia se l'ID esiste")
    void trovaPerID_dovrebbeRestituireLaMateria() {
        when(materiaRepository.findById(idEsistente)).thenReturn(Optional.of(materiaEsistente));

        MateriaResponseDTO risultato = materiaService.trovaPerID(idEsistente);

        assertThat(risultato).isNotNull();
        assertThat(risultato.nome()).isEqualTo("Matematica");
    }

    @Test
    @DisplayName("trovaPerID - dovrebbe lanciare ResourceNotFoundException se ID non esiste")
    void trovaPerID_dovrebbeLanciareEccezioneSeIDNonEsiste() {
        UUID idInesistente = UUID.randomUUID();
        when(materiaRepository.findById(idInesistente)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> materiaService.trovaPerID(idInesistente))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(idInesistente.toString());
    }

    @Test
    @DisplayName("aggiornaMateria - dovrebbe lanciare eccezione se ID non esiste")
    void aggiornaMateria_dovrebbeLanciareEccezioneSeIDNonEsiste() {
        UUID idInesistente = UUID.randomUUID();
        when(materiaRepository.findById(idInesistente)).thenReturn(Optional.empty());
        MateriaUpdateDTO dto = new MateriaUpdateDTO("Fisica", null, null, null, null);

        assertThatThrownBy(() -> materiaService.aggiornaMateria(idInesistente, dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("aggiornaMateria - dovrebbe aggiornare tutti i campi quando sono valorizzati")
    void aggiornaMateria_dovrebbeAggiornareTuttiICampi() {
        when(materiaRepository.findById(idEsistente)).thenReturn(Optional.of(materiaEsistente));
        when(materiaRepository.save(any(Materia.class))).thenReturn(materiaEsistente);
        MateriaUpdateDTO dto = new MateriaUpdateDTO("Fisica", "Fisica applicata", 4, TipoMateria.PRATICA, false);

        MateriaResponseDTO risultato = materiaService.aggiornaMateria(idEsistente, dto);

        assertThat(risultato).isNotNull();
        verify(materiaRepository, times(1)).save(materiaEsistente);
    }

    @Test
    @DisplayName("aggiornaMateria - non dovrebbe modificare i campi nulli")
    void aggiornaMateria_nonDovrebbeAggiornareCampiNull() {
        when(materiaRepository.findById(idEsistente)).thenReturn(Optional.of(materiaEsistente));
        when(materiaRepository.save(any(Materia.class))).thenReturn(materiaEsistente);
        MateriaUpdateDTO dto = new MateriaUpdateDTO(null, null, null, null, null);

        MateriaResponseDTO risultato = materiaService.aggiornaMateria(idEsistente, dto);

        assertThat(risultato).isNotNull();
        assertThat(materiaEsistente.getNome()).isEqualTo("Matematica");
        verify(materiaRepository, times(1)).save(materiaEsistente);
    }

    @Test
    @DisplayName("eliminaMateria - dovrebbe lanciare eccezione se ID non esiste")
    void eliminaMateria_dovrebbeLanciareEccezioneSeIDNonEsiste() {
        UUID idInesistente = UUID.randomUUID();
        when(materiaRepository.findById(idInesistente)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> materiaService.eliminaMateria(idInesistente))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("eliminaMateria - soft delete se la materia ha riferimenti in materie_classe")
    void eliminaMateria_dovrebbeFareSoftDeleteSeHaRiferimenti() {
        when(materiaRepository.findById(idEsistente)).thenReturn(Optional.of(materiaEsistente));
        when(materiaClasseRepository.existsByMateriaId(idEsistente)).thenReturn(true);

        materiaService.eliminaMateria(idEsistente);

        assertThat(materiaEsistente.isActive()).isFalse();
        verify(materiaRepository, times(1)).save(materiaEsistente);
        verify(materiaRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("eliminaMateria - hard delete se la materia non ha riferimenti")
    void eliminaMateria_dovrebbeFareHardDeleteSeNonHaRiferimenti() {
        when(materiaRepository.findById(idEsistente)).thenReturn(Optional.of(materiaEsistente));
        when(materiaClasseRepository.existsByMateriaId(idEsistente)).thenReturn(false);

        materiaService.eliminaMateria(idEsistente);

        verify(materiaRepository, never()).save(any());
        verify(materiaRepository, times(1)).deleteById(idEsistente);
    }
}
