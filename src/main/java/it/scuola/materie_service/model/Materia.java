package it.scuola.materie_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity che mappa la tabella "materie" nel database db_materie.
 */
@Entity
@Table(name = "materie")
public class Materia {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "nome", nullable = false, unique = true, length = 100)
    private String nome;

    @Column(name = "codice", nullable = false, unique = true, length = 10)
    private String codice;

    @Column(name = "descrizione")
    private String descrizione;

    @Column(name = "ore_settimanali")
    private Integer oreSettimanali;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_materia", nullable = false)
    private TipoMateria tipoMateria;

    @Column(name = "creato_il", updatable = false)
    private LocalDateTime creatoIl;

    // true = materia attiva, false = disattivata (soft delete)
    @Column(name = "active", nullable = false, columnDefinition = "boolean default true")
    private boolean active = true;

    /** Required by JPA/Hibernate. */
    @SuppressWarnings("PMD.UncommentedEmptyConstructor")
    public Materia() { }

    public Materia(MateriaDTO dto) {
        this.nome = dto.nome();
        this.codice = dto.codice();
        this.descrizione = dto.descrizione();
        this.oreSettimanali = dto.oreSettimanali();
        this.tipoMateria = dto.tipoMateria();
        this.creatoIl = LocalDateTime.now();
    }

    public UUID getId() { return id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCodice() { return codice; }
    public void setCodice(String codice) { this.codice = codice; }

    public String getDescrizione() { return descrizione; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }

    public Integer getOreSettimanali() { return oreSettimanali; }
    public void setOreSettimanali(Integer oreSettimanali) { this.oreSettimanali = oreSettimanali; }

    public TipoMateria getTipoMateria() { return tipoMateria; }
    public void setTipoMateria(TipoMateria tipoMateria) { this.tipoMateria = tipoMateria; }

    public LocalDateTime getCreatoIl() { return creatoIl; }
    public void setCreatoIl(LocalDateTime creatoIl) { this.creatoIl = creatoIl; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
