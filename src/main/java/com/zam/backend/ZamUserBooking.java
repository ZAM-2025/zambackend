package com.zam.backend;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Entity
@Table(name = "prenotazione")
public class ZamUserBooking {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prenotazione_id_gen")
    @SequenceGenerator(name = "prenotazione_id_gen", sequenceName = "prenotare_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "inizio")
    private Instant inizio;

    @Column(name = "fine")
    private Instant fine;

    @Column(name = "nmod")
    private Integer nmod;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @ColumnDefault("nextval('prenotare_id_utente_seq')")
    @JoinColumn(name = "id_utente", nullable = false)
    private ZamUser idUtente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @ColumnDefault("nextval('prenotare_id_asset_seq')")
    @JoinColumn(name = "id_asset", nullable = false)
    private ZamAsset idAsset;

    protected ZamUserBooking() {}

    public ZamUserBooking(ZamUser idUtente, ZamAsset idAsset, Instant inizio, Instant fine) {
        this.idUtente = idUtente;
        this.idAsset = idAsset;

        this.inizio = inizio;
        this.fine = fine;

        this.nmod = 0;
    }

    public Integer getId() {
        return id;
    }

    public Instant getInizio() {
        return inizio;
    }

    public Instant getFine() {
        return fine;
    }

    public Integer getNmod() {
        return nmod;
    }

    public void setNmod(Integer nmod) { this.nmod = nmod; }

    public ZamUser getIdUtente() {
        return idUtente;
    }

    public ZamAsset getIdAsset() {
        return idAsset;
    }
}