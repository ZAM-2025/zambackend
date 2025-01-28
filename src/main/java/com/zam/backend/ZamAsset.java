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

    protected ZamAsset() {}

    public ZamAsset(ZamAssetStatus stato, ZamAssetType tipo) {
        this.stato = stato;
        this.tipo = tipo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ZamAssetStatus getStato() { return this.stato; }

    public ZamAssetType getTipo() { return this.tipo; }
}