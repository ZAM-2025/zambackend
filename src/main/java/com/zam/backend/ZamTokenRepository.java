package com.zam.backend;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ZamTokenRepository extends CrudRepository<ZamToken, Integer> {
    List<ZamToken> findZamTokenByIdutente(ZamUser idutente);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM zamtoken WHERE created < CURRENT_TIMESTAMP - INTERVAL '30 days'", nativeQuery = true)
    void clearTokens();

    @Transactional
    @Query(value = "SELECT DISTINCT utente.* FROM utente INNER JOIN zamtoken z on utente.id = z.idutente WHERE z.val = :token", nativeQuery = true)
    ZamUser findUser(@Param("token") String token);

    ZamToken findZamTokenByVal(String val);
}
