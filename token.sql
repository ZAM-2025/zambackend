CREATE TABLE zamtoken (
    id SERIAL PRIMARY KEY,
    val varchar(48),
    idutente SERIAL,
    created TIMESTAMP,
    FOREIGN KEY (idutente) REFERENCES utente(id) 
);