package com.zam.backend;

import jakarta.persistence.*;

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
    private ZamAssetStatus stato;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", columnDefinition = "tipoasset")
    private ZamAssetType tipo;

    @Column(name = "coords")
    private String coords;

    @Column(name = "nome")
    private String nome;

    @Column(name = "piano")
    private Integer piano;

    protected ZamAsset() {}

    public ZamAsset(ZamAssetStatus stato, ZamAssetType tipo, String coords, String nome, Integer piano) {
        this.stato = stato;
        this.tipo = tipo;
        this.coords = coords;
        this.nome = nome;
        this.piano = piano;
    }

    public Integer getId() {
        return id;
    }

    public String getCoords() { return coords; }

    public String getNome() { return nome; }

    public Integer getPiano() { return piano; }

    public void setId(Integer id) {
        this.id = id;
    }

    public ZamAssetStatus getStato() { return this.stato; }

    public ZamAssetType getTipo() { return this.tipo; }
}