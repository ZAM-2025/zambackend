package com.zam.backend;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "asset")
public class ZamAsset {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "asset_id_gen")
    @SequenceGenerator(name = "asset_id_gen", sequenceName = "asset_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "stato", columnDefinition = "statoasset")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private ZamAssetStatus stato;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", columnDefinition = "tipoasset")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private ZamAssetType tipo;

    @Column(name = "ismarker")
    private Boolean isMarker;

    @Column(name = "coords")
    private String coords;

    @Column(name = "nome")
    private String nome;

    @Column(name = "piano")
    private Integer piano;

    @Column(name = "attivo")
    private Boolean attivo;

    protected ZamAsset() {}

    public ZamAsset(ZamAssetStatus stato, ZamAssetType tipo, String coords, String nome, Integer piano, Boolean attivo, Boolean isMarker) {
        this.stato = stato;
        this.tipo = tipo;
        this.coords = coords;
        this.nome = nome;
        this.piano = piano;
        this.attivo = attivo;
        this.isMarker = isMarker;
    }

    public Integer getId() {
        return id;
    }

    public String getCoords() { return coords; }

    public String getNome() { return nome; }

    public Integer getPiano() { return piano; }

    public Boolean getIsMarker() { return isMarker; }

    public void setId(Integer id) {
        this.id = id;
    }

    public ZamAssetStatus getStato() { return this.stato; }

    public void setActive(Boolean active) { this.attivo = active; }

    public boolean isActive() { return this.attivo; }

    public ZamAssetType getTipo() { return this.tipo; }
}