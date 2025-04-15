package com.zam.backend;

import com.fasterxml.jackson.databind.annotation.JsonAppend;
import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;

import org.hibernate.dialect.PostgreSQLEnumJdbcType;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "utente")
public class ZamUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nome;
    private String cognome;

    private String username;
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", columnDefinition = "tipoutente")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private ZamUserType tipo;

    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    @ColumnDefault("nextval('utente_coordinatore_seq')")
    @JoinColumn(name = "coordinatore", nullable = true)
    private ZamUser coordinatore;

    protected ZamUser() {}

    public ZamUser(String username, String password, String nome, String cognome, ZamUserType tipo, ZamUser coordinatore) {
        this.username = username;
        this.password = password;

        this.cognome = cognome;
        this.nome = nome;

        this.tipo = tipo;

        this.coordinatore = coordinatore;

        if(this.tipo != ZamUserType.COORDINATORE && this.coordinatore == null) {
            System.out.println("ERRORE: Creato un utente non coordinatore senza coordinatore!");
        }
    }

    public int getId() { return this.id; }

    public String getUsername() { return this.username; }

    public String getPassword() { return this.password; }

    public String getCognome() { return this.cognome; }

    public String getNome() { return this.nome; }

    public ZamUser getCoordinatore() { return coordinatore; }

    public ZamUserType getTipo() { return this.tipo; }

    public void setUsername(String username) { this.username = username; }

    public void setNome(String nome) { this.nome = nome; }

    public void setCognome(String cognome) { this.cognome = cognome; }

    public void setTipo(ZamUserType tipo) { this.tipo = tipo; }

    public void setCoordinatore(ZamUser coordinatore) { this.coordinatore = coordinatore; }

    public void setPassword(String password) { this.password = password; }

    public WebCoordInfo getCoordInfo() {
        if(this.getTipo() == ZamUserType.DIPENDENTE && this.getCoordinatore() != null) {
            return new WebCoordInfo(this.getCoordinatore().getId(), this.getCoordinatore().getNome(), this.getCoordinatore().getCognome());
        }

        return null;
    }

    public ZamUser clone() {
        return new ZamUser(this.username, this.password, this.nome, this.cognome, this.tipo, this.coordinatore);
    }
}
