--
-- PostgreSQL database dump
--

-- Dumped from database version 14.15 (Homebrew)
-- Dumped by pg_dump version 14.15 (Homebrew)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: statoasset; Type: TYPE; Schema: public; Owner: zam
--

CREATE TYPE public.statoasset AS ENUM (
    'OCCUPATO',
    'LIBERO',
    'NON_PRENOTABILE'
);


ALTER TYPE public.statoasset OWNER TO zam;

--
-- Name: tipoasset; Type: TYPE; Schema: public; Owner: zam
--

CREATE TYPE public.tipoasset AS ENUM (
    'A',
    'A2',
    'B',
    'C'
);


ALTER TYPE public.tipoasset OWNER TO zam;

--
-- Name: tipoutente; Type: TYPE; Schema: public; Owner: zam
--

CREATE TYPE public.tipoutente AS ENUM (
    'DIPENDENTE',
    'COORDINATORE',
    'GESTORE'
);


ALTER TYPE public.tipoutente OWNER TO zam;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: asset; Type: TABLE; Schema: public; Owner: zam
--

CREATE TABLE public.asset (
    id integer NOT NULL,
    stato public.statoasset,
    tipo public.tipoasset
);


ALTER TABLE public.asset OWNER TO zam;

--
-- Name: asset_id_seq; Type: SEQUENCE; Schema: public; Owner: zam
--

CREATE SEQUENCE public.asset_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.asset_id_seq OWNER TO zam;

--
-- Name: asset_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: zam
--

ALTER SEQUENCE public.asset_id_seq OWNED BY public.asset.id;


--
-- Name: prenotazione; Type: TABLE; Schema: public; Owner: zam
--

CREATE TABLE public.prenotazione (
    id integer NOT NULL,
    inizio timestamp without time zone,
    fine timestamp without time zone,
    nmod integer,
    id_utente integer NOT NULL,
    id_asset integer NOT NULL
);


ALTER TABLE public.prenotazione OWNER TO zam;

--
-- Name: prenotare_id_asset_seq; Type: SEQUENCE; Schema: public; Owner: zam
--

CREATE SEQUENCE public.prenotare_id_asset_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.prenotare_id_asset_seq OWNER TO zam;

--
-- Name: prenotare_id_asset_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: zam
--

ALTER SEQUENCE public.prenotare_id_asset_seq OWNED BY public.prenotazione.id_asset;


--
-- Name: prenotare_id_seq; Type: SEQUENCE; Schema: public; Owner: zam
--

CREATE SEQUENCE public.prenotare_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.prenotare_id_seq OWNER TO zam;

--
-- Name: prenotare_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: zam
--

ALTER SEQUENCE public.prenotare_id_seq OWNED BY public.prenotazione.id;


--
-- Name: prenotare_id_utente_seq; Type: SEQUENCE; Schema: public; Owner: zam
--

CREATE SEQUENCE public.prenotare_id_utente_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.prenotare_id_utente_seq OWNER TO zam;

--
-- Name: prenotare_id_utente_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: zam
--

ALTER SEQUENCE public.prenotare_id_utente_seq OWNED BY public.prenotazione.id_utente;


--
-- Name: utente; Type: TABLE; Schema: public; Owner: zam
--

CREATE TABLE public.utente (
    id integer NOT NULL,
    nome character varying(30),
    cognome character varying(30),
    username character varying(20),
    password character varying(20),
    tipo public.tipoutente,
    coordinatore integer NOT NULL
);


ALTER TABLE public.utente OWNER TO zam;

--
-- Name: utente_coordinatore_seq; Type: SEQUENCE; Schema: public; Owner: zam
--

CREATE SEQUENCE public.utente_coordinatore_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.utente_coordinatore_seq OWNER TO zam;

--
-- Name: utente_coordinatore_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: zam
--

ALTER SEQUENCE public.utente_coordinatore_seq OWNED BY public.utente.coordinatore;


--
-- Name: utente_id_seq; Type: SEQUENCE; Schema: public; Owner: zam
--

CREATE SEQUENCE public.utente_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.utente_id_seq OWNER TO zam;

--
-- Name: utente_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: zam
--

ALTER SEQUENCE public.utente_id_seq OWNED BY public.utente.id;


--
-- Name: asset id; Type: DEFAULT; Schema: public; Owner: zam
--

ALTER TABLE ONLY public.asset ALTER COLUMN id SET DEFAULT nextval('public.asset_id_seq'::regclass);


--
-- Name: prenotazione id; Type: DEFAULT; Schema: public; Owner: zam
--

ALTER TABLE ONLY public.prenotazione ALTER COLUMN id SET DEFAULT nextval('public.prenotare_id_seq'::regclass);


--
-- Name: prenotazione id_utente; Type: DEFAULT; Schema: public; Owner: zam
--

ALTER TABLE ONLY public.prenotazione ALTER COLUMN id_utente SET DEFAULT nextval('public.prenotare_id_utente_seq'::regclass);


--
-- Name: prenotazione id_asset; Type: DEFAULT; Schema: public; Owner: zam
--

ALTER TABLE ONLY public.prenotazione ALTER COLUMN id_asset SET DEFAULT nextval('public.prenotare_id_asset_seq'::regclass);


--
-- Name: utente id; Type: DEFAULT; Schema: public; Owner: zam
--

ALTER TABLE ONLY public.utente ALTER COLUMN id SET DEFAULT nextval('public.utente_id_seq'::regclass);


--
-- Name: utente coordinatore; Type: DEFAULT; Schema: public; Owner: zam
--

ALTER TABLE ONLY public.utente ALTER COLUMN coordinatore SET DEFAULT nextval('public.utente_coordinatore_seq'::regclass);


--
-- Data for Name: asset; Type: TABLE DATA; Schema: public; Owner: zam
--

COPY public.asset (id, stato, tipo) FROM stdin;
\.


--
-- Data for Name: prenotazione; Type: TABLE DATA; Schema: public; Owner: zam
--

COPY public.prenotazione (id, inizio, fine, nmod, id_utente, id_asset) FROM stdin;
\.


--
-- Data for Name: utente; Type: TABLE DATA; Schema: public; Owner: zam
--

COPY public.utente (id, nome, cognome, username, password, tipo, coordinatore) FROM stdin;
\.


--
-- Name: asset_id_seq; Type: SEQUENCE SET; Schema: public; Owner: zam
--

SELECT pg_catalog.setval('public.asset_id_seq', 1, false);


--
-- Name: prenotare_id_asset_seq; Type: SEQUENCE SET; Schema: public; Owner: zam
--

SELECT pg_catalog.setval('public.prenotare_id_asset_seq', 1, false);


--
-- Name: prenotare_id_seq; Type: SEQUENCE SET; Schema: public; Owner: zam
--

SELECT pg_catalog.setval('public.prenotare_id_seq', 1, false);


--
-- Name: prenotare_id_utente_seq; Type: SEQUENCE SET; Schema: public; Owner: zam
--

SELECT pg_catalog.setval('public.prenotare_id_utente_seq', 1, false);


--
-- Name: utente_coordinatore_seq; Type: SEQUENCE SET; Schema: public; Owner: zam
--

SELECT pg_catalog.setval('public.utente_coordinatore_seq', 1, false);


--
-- Name: utente_id_seq; Type: SEQUENCE SET; Schema: public; Owner: zam
--

SELECT pg_catalog.setval('public.utente_id_seq', 1, false);


--
-- Name: asset asset_pkey; Type: CONSTRAINT; Schema: public; Owner: zam
--

ALTER TABLE ONLY public.asset
    ADD CONSTRAINT asset_pkey PRIMARY KEY (id);


--
-- Name: prenotazione prenotare_pkey; Type: CONSTRAINT; Schema: public; Owner: zam
--

ALTER TABLE ONLY public.prenotazione
    ADD CONSTRAINT prenotare_pkey PRIMARY KEY (id);


--
-- Name: utente utente_pkey; Type: CONSTRAINT; Schema: public; Owner: zam
--

ALTER TABLE ONLY public.utente
    ADD CONSTRAINT utente_pkey PRIMARY KEY (id);


--
-- Name: prenotazione prenotare_id_asset_fkey; Type: FK CONSTRAINT; Schema: public; Owner: zam
--

ALTER TABLE ONLY public.prenotazione
    ADD CONSTRAINT prenotare_id_asset_fkey FOREIGN KEY (id_asset) REFERENCES public.asset(id);


--
-- Name: prenotazione prenotare_id_utente_fkey; Type: FK CONSTRAINT; Schema: public; Owner: zam
--

ALTER TABLE ONLY public.prenotazione
    ADD CONSTRAINT prenotare_id_utente_fkey FOREIGN KEY (id_utente) REFERENCES public.utente(id);


--
-- Name: utente utente_coordinatore_fkey; Type: FK CONSTRAINT; Schema: public; Owner: zam
--

ALTER TABLE ONLY public.utente
    ADD CONSTRAINT utente_coordinatore_fkey FOREIGN KEY (coordinatore) REFERENCES public.utente(id);


--
-- Name: SCHEMA public; Type: ACL; Schema: -; Owner: zam
--

GRANT USAGE ON SCHEMA public TO zam;


--
-- PostgreSQL database dump complete
--

