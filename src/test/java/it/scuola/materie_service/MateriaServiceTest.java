package it.scuola.materie_service;

import it.scuola.materie_service.exception.ResourceNotFoundException;
import it.scuola.materie_service.model.Materia;
import it.scuola.materie_service.model.MateriaDTO;
import it.scuola.materie_service.model.MateriaResponseDTO;
import it.scuola.materie_service.model.TipoMateria;
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
import static org.mockito.Mockito.*;

/**
 * Test unitari del MateriaService: usano Mockito per simulare il repository,
 * così il test non tocca il database.
 */
@ExtendWith(MockitoExtension.class)                 // Estende il test con il supporto di Mockito per l'iniezione di dipendenze e la creazione di mock
class MateriaServiceTest {

    @Mock
    private MateriaRepository materiaRepository;
                                                    // Crea un mock del repository MateriaRepository, che simula il comportamento del repository reale senza accedere al database
    @InjectMocks
    private MateriaService materiaService;

    private Materia materiaEsistente;
    private UUID idEsistente;
                                                    //Viene eseguito prima di ogni singolo test. Prepara i dati comuni, in questo caso un UUID e un oggetto Materia già popolato
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
                                                                                                // Configura il mock del repository per restituire false quando si verifica l'esistenza di codice e nome, e per restituire l'oggetto materiaEsistente quando si salva una nuova materia
        when(materiaRepository.existsByCodice("MAT")).thenReturn(false);
        when(materiaRepository.existsByNome("Matematica")).thenReturn(false);
        when(materiaRepository.save(any(Materia.class))).thenReturn(materiaEsistente);

        MateriaResponseDTO risultato = materiaService.creaMateria(dto);                         // Chiama il metodo del servizio per creare una nuova materia e cattura il risultato in un DTO di risposta
                                                                                                // Verifica che il risultato non sia nullo e che i campi del DTO di risposta corrispondano ai valori attesi, e che il metodo save del repository sia stato chiamato una volta
        assertThat(risultato).isNotNull();
        assertThat(risultato.nome()).isEqualTo("Matematica");
        assertThat(risultato.codice()).isEqualTo("MAT");
        assertThat(risultato.tipoMateria()).isEqualTo(TipoMateria.TEORICA);
                                                                                                //Verifica che il risultato sia quello atteso. Viene da AssertJ, una libreria di asserzioni più leggibile rispetto al classico assertEquals di JUnit
        verify(materiaRepository, times(1)).save(any(Materia.class));                       // Verifica che il mock sia stato chiamato nel modo giusto per controllare che il service non chiami save quando non dovrebbe (caso del codice duplicato).


    }

    @Test
    @DisplayName("creaMateria - dovrebbe lanciare eccezione se codice già esiste")
    void creaMateria_dovrebbeLanciareEccezioneSeCodiceDuplicato() {
        MateriaDTO dto = new MateriaDTO("Matematica", "MAT", null, 5, TipoMateria.TEORICA);
        when(materiaRepository.existsByCodice("MAT")).thenReturn(true);

        assertThatThrownBy(() -> materiaService.creaMateria(dto))                               // Verifica che lancia un'eccezione
                .isInstanceOf(RuntimeException.class);

        verify(materiaRepository, never()).save(any());                          // Verifica che il mock sia stato chiamato nel modo giusto per controllare che il service non chiami save quando non dovrebbe (caso del codice duplicato).


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
        when(materiaRepository.findById(idEsistente))
                .thenReturn(Optional.of(materiaEsistente));

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
}
