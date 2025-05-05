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

    ZamUser findByUsername(String username);

    // Extra janky
    @Query(value = "INSERT INTO utente (nome, cognome, username, password, tipo, coordinatore) VALUES (:nome, :cognome, :username, :password, :tipo, :coordinatore)", nativeQuery = true)
    void write(@Param("nome") String nome, @Param("cognome") String cognome,
               @Param("username") String username, @Param("password") String password,
               @Param("tipo") String tipo, @Param("coordinatore") String coordinatore);


    default void write(ZamUser user) {
        String coordinatore = "NULL";
        if(user.getCoordinatore() != null) {
            coordinatore = Integer.toString(user.getCoordinatore().getId());
        }

        String tipo = user.getTipo().toString() + "::tipoutente";

        write(user.getNome(), user.getCognome(), user.getUsername(), user.getPassword(), tipo, coordinatore);
    }

    int countZamUsersByTipo(ZamUserType tipo);

    List<ZamUser> findByCoordinatore(ZamUser coordinatore);
}
