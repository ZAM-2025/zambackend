package com.zam.backend;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "zamtoken")
public class ZamToken {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "zamtoken_id_gen")
    @SequenceGenerator(name = "zamtoken_id_gen", sequenceName = "zamtoken_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "val", length = 48)
    private String val;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @ColumnDefault("nextval('zamtoken_idutente_seq')")
    @JoinColumn(name = "idutente", nullable = false)
    private ZamUser idutente;

    @Column(name = "created")
    private Instant created;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public ZamUser getIdutente() {
        return idutente;
    }

    public void setIdutente(ZamUser idutente) {
        this.idutente = idutente;
    }

    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    @Override
    public String toString() {
        return this.getVal();
    }

    protected ZamToken() {}

    public ZamToken(ZamUser idutente) {
        this.setCreated(Instant.now());
        this.setVal(UUID.randomUUID().toString());
        this.setIdutente(idutente);
    }
}