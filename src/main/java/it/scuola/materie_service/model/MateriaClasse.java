package it.scuola.materie_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.UUID;

/**
 * Entity che mappa la tabella "materie_classe": associazione tra una materia
 * e una classe. "materia" è una FK reale (stesso DB), mentre "idClasse" è solo
 * un riferimento all'UUID della classe in composizione-service (DB diverso,
 * nessuna FK fisica).
 */
@Entity
@Table(
    name = "materie_classe",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_materie_classe_composita",
        columnNames = {"id_materia", "id_classe"}
    )
)
public class MateriaClasse {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "id_classe", nullable = false)
    private UUID idClasse;

    @ManyToOne
    @JoinColumn(name = "id_materia", nullable = false)
    private Materia materia;

    // se null, si usano le ore standard della materia
    @Column(name = "ore_settimanali_personalizzate")
    private Integer oreSettimanaliPersonalizzate;

    public MateriaClasse() { }

    public UUID getId() { return id; }

    public UUID getIdClasse() { return idClasse; }
    public void setIdClasse(UUID idClasse) { this.idClasse = idClasse; }

    public Materia getMateria() { return materia; }
    public void setMateria(Materia materia) { this.materia = materia; }

    public Integer getOreSettimanaliPersonalizzate() { return oreSettimanaliPersonalizzate; }
    public void setOreSettimanaliPersonalizzate(Integer ore) { this.oreSettimanaliPersonalizzate = ore; }
}
