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
-- Name: zam-prova; Type: DATABASE; Schema: -; Owner: zam
--

CREATE DATABASE "zam-prova" WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE = 'it_IT.UTF-8';


ALTER DATABASE "zam-prova" OWNER TO zam;

\connect -reuse-previous=on "dbname='zam-prova'"

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
-- Name: _statoasset; Type: TYPE; Schema: public; Owner: zam
--

CREATE TYPE public._statoasset AS ENUM (
    'occ',
    'lib',
    'np'
);


ALTER TYPE public._statoasset OWNER TO zam;

--
-- Name: _tipoasset; Type: TYPE; Schema: public; Owner: zam
--

CREATE TYPE public._tipoasset AS ENUM (
    'a',
    'a2',
    'b',
    'c'
);


ALTER TYPE public._tipoasset OWNER TO zam;

--
-- Name: _tipoutente; Type: TYPE; Schema: public; Owner: zam
--

CREATE TYPE public._tipoutente AS ENUM (
    'dip',
    'cor',
    'ges'
);


ALTER TYPE public._tipoutente OWNER TO zam;

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
    password character varying(64),
    tipo public.tipoutente,
    coordinatore integer
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
-- Name: zamtoken; Type: TABLE; Schema: public; Owner: zam
--

CREATE TABLE public.zamtoken (
    id integer NOT NULL,
    val character varying(48),
    idutente integer NOT NULL,
    created timestamp without time zone
);


ALTER TABLE public.zamtoken OWNER TO zam;

--
-- Name: zamtoken_id_seq; Type: SEQUENCE; Schema: public; Owner: zam
--

CREATE SEQUENCE public.zamtoken_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.zamtoken_id_seq OWNER TO zam;

--
-- Name: zamtoken_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: zam
--

ALTER SEQUENCE public.zamtoken_id_seq OWNED BY public.zamtoken.id;


--
-- Name: zamtoken_idutente_seq; Type: SEQUENCE; Schema: public; Owner: zam
--

CREATE SEQUENCE public.zamtoken_idutente_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.zamtoken_idutente_seq OWNER TO zam;

--
-- Name: zamtoken_idutente_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: zam
--

ALTER SEQUENCE public.zamtoken_idutente_seq OWNED BY public.zamtoken.idutente;


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
-- Name: zamtoken id; Type: DEFAULT; Schema: public; Owner: zam
--

ALTER TABLE ONLY public.zamtoken ALTER COLUMN id SET DEFAULT nextval('public.zamtoken_id_seq'::regclass);


--
-- Name: zamtoken idutente; Type: DEFAULT; Schema: public; Owner: zam
--

ALTER TABLE ONLY public.zamtoken ALTER COLUMN idutente SET DEFAULT nextval('public.zamtoken_idutente_seq'::regclass);


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
1	Giacomo	Agatan	gagatan	0	DIPENDENTE	0
2	Provo	McProva	prova	Ylil4Ot3KRHU+SvltdsOFFEe2+AdHQ3dHVosuduaVro=	DIPENDENTE	0
0	Matteo	Forlani	zam	GRJ2bWug5Q6LG6z7USB+g7lbesDNjOFTB830ll5+P2w=	COORDINATORE	\N
\.


--
-- Data for Name: zamtoken; Type: TABLE DATA; Schema: public; Owner: zam
--

COPY public.zamtoken (id, val, idutente, created) FROM stdin;
1	d1dd5394-c349-4037-bfff-d73f4e714468	0	2025-02-22 08:47:41.714694
2	d85f2e2d-9a26-417d-9a19-8413723f5e85	2	2025-02-22 08:49:23.716314
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
-- Name: zamtoken_id_seq; Type: SEQUENCE SET; Schema: public; Owner: zam
--

SELECT pg_catalog.setval('public.zamtoken_id_seq', 2, true);


--
-- Name: zamtoken_idutente_seq; Type: SEQUENCE SET; Schema: public; Owner: zam
--

SELECT pg_catalog.setval('public.zamtoken_idutente_seq', 1, false);


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
-- Name: zamtoken zamtoken_pkey; Type: CONSTRAINT; Schema: public; Owner: zam
--

ALTER TABLE ONLY public.zamtoken
    ADD CONSTRAINT zamtoken_pkey PRIMARY KEY (id);


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
-- Name: zamtoken zamtoken_idutente_fkey; Type: FK CONSTRAINT; Schema: public; Owner: zam
--

ALTER TABLE ONLY public.zamtoken
    ADD CONSTRAINT zamtoken_idutente_fkey FOREIGN KEY (idutente) REFERENCES public.utente(id);


--
-- Name: SCHEMA public; Type: ACL; Schema: -; Owner: zam
--

GRANT USAGE ON SCHEMA public TO zam;


--
-- PostgreSQL database dump complete
--

