package com.zam.backend;

import io.hypersistence.utils.hibernate.type.basic.PostgreSQLEnumType;
import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "utente")
@Convert(attributeName = "tipoutente", converter = PostgreSQLEnumType.class)
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
    private ZamUserType tipo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @ColumnDefault("nextval('utente_coordinatore_seq')")
    @JoinColumn(name = "coordinatore", nullable = false)
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
}
