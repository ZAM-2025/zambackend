package com.zam.backend;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ZamUserRepository extends CrudRepository<ZamUser, Integer> {
    @Query(value = "SELECT * FROM utente WHERE tipo::name = :tipoLabel", nativeQuery = true)
    List<ZamUser> findByTipo(@Param("tipoLabel") String tipoLabel);

    default List<ZamUser> findByTipo(ZamUserType tipo) {
        return findByTipo(tipo.name());
    }
}
