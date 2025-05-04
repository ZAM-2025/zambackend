package com.zam.backend;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ZamAssetRepository extends CrudRepository<ZamAsset, Integer> {
    public List<ZamAsset> findAll();
    public Iterable<ZamAsset> findByPiano(Integer piano);

    Iterable<ZamAsset> findByAttivo(Boolean attivo);
    Iterable<ZamAsset> findByPianoAndAttivo(Integer piano, Boolean attivo);
}
