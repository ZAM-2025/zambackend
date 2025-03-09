package com.zam.backend;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "prenotazione")
public class ZamBooking {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prenotazione_id_gen")
    @SequenceGenerator(name = "prenotazione_id_gen", sequenceName = "prenotare_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "inizio")
    @JsonFormat(timezone = "Europe/Rome")
    private LocalDateTime inizio;

    @Column(name = "fine")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "Europe/Rome")
    private LocalDateTime fine;

    @Column(name = "nmod")
    private Integer nmod;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @ColumnDefault("nextval('prenotare_id_utente_seq')")
    @JoinColumn(name = "id_utente", nullable = false)
    @JsonIgnore
    private ZamUser idUtente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @ColumnDefault("nextval('prenotare_id_asset_seq')")
    @JoinColumn(name = "id_asset", nullable = false)
    @JsonIgnore
    private ZamAsset idAsset;

    protected ZamBooking() {}

    public ZamBooking(ZamUser idUtente, ZamAsset idAsset, LocalDateTime inizio, LocalDateTime fine) {
        this.idUtente = idUtente;
        this.idAsset = idAsset;

        this.inizio = inizio;
        this.fine = fine;

        this.nmod = 0;
    }

    public Integer getId() {
        return id;
    }

    public LocalDateTime getInizio() {
        return inizio;
    }

    public LocalDateTime getFine() {
        return fine;
    }

    public Integer getNmod() {
        return nmod;
    }

    public ZamUser getIdUtente() {
        return idUtente;
    }

    public ZamAsset getIdAsset() {
        return idAsset;
    }

    public void setNmod(Integer nmod) { this.nmod = nmod; }

    public void setIdUtente(ZamUser idUtente) { this.idUtente = idUtente; }

    public void setIdAsset(ZamAsset idAsset) { this.idAsset = idAsset; }

    public void setInizio(LocalDateTime inizio) { this.inizio = inizio; }

    public void setFine(LocalDateTime fine) { this.fine = fine; }

    public boolean isBooked() {
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(inizio) && now.isBefore(fine);
    }
}