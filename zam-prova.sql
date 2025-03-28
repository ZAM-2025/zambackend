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
-- Name: zam-prova; Type: DATABASE; Schema: -; Owner: postgres
--

CREATE DATABASE "zam-prova" WITH TEMPLATE = template0 ENCODING = 'UTF8';


ALTER DATABASE "zam-prova" OWNER TO postgres;

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
-- Name: _statoasset; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE public._statoasset AS ENUM (
    'occ',
    'lib',
    'np'
);


ALTER TYPE public._statoasset OWNER TO postgres;

--
-- Name: _tipoasset; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE public._tipoasset AS ENUM (
    'a',
    'a2',
    'b',
    'c'
);


ALTER TYPE public._tipoasset OWNER TO postgres;

--
-- Name: _tipoutente; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE public._tipoutente AS ENUM (
    'dip',
    'cor',
    'ges'
);


ALTER TYPE public._tipoutente OWNER TO postgres;

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
    tipo public.tipoasset,
    coords text,
    nome text,
    piano integer
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
-- Name: zamtoken; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.zamtoken (
    id integer NOT NULL,
    val character varying(48),
    idutente integer NOT NULL,
    created timestamp without time zone
);


ALTER TABLE public.zamtoken OWNER TO postgres;

--
-- Name: zamtoken_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.zamtoken_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.zamtoken_id_seq OWNER TO postgres;

--
-- Name: zamtoken_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.zamtoken_id_seq OWNED BY public.zamtoken.id;


--
-- Name: zamtoken_idutente_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.zamtoken_idutente_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.zamtoken_idutente_seq OWNER TO postgres;

--
-- Name: zamtoken_idutente_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
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
-- Name: zamtoken id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.zamtoken ALTER COLUMN id SET DEFAULT nextval('public.zamtoken_id_seq'::regclass);


--
-- Name: zamtoken idutente; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.zamtoken ALTER COLUMN idutente SET DEFAULT nextval('public.zamtoken_idutente_seq'::regclass);


--
-- Data for Name: asset; Type: TABLE DATA; Schema: public; Owner: zam
--

COPY public.asset (id, stato, tipo, coords, nome, piano) FROM stdin;
10	LIBERO	B	[{"lat":-12.897489183755892,"lng":-87.71484375000001},{"lat":21.453068633086783,"lng":-87.80273437500001},{"lat":21.453068633086783,"lng":-29.794921875000004},{"lat":-12.811801316582619,"lng":-29.794921875000004}]	Sala Riunioni 1	1
11	LIBERO	B	[{"lat":-51.6180165487737,"lng":-87.89062500000001},{"lat":-24.206889622398023,"lng":-87.71484375000001},{"lat":-24.686952411999144,"lng":-30.058593750000004},{"lat":-51.39920565355378,"lng":-29.707031250000004}]	Sala Riunioni 2	1
12	LIBERO	B	[{"lat":21.616579336740603,"lng":-29.882812500000004},{"lat":-12.897489183755892,"lng":-29.707031250000004},{"lat":-12.554563528593656,"lng":28.4765625},{"lat":21.616579336740603,"lng":28.4765625}]	Sala Riunioni 3	1
13	LIBERO	B	[{"lat":-24.5271348225978,"lng":-29.882812500000004},{"lat":-51.72702815704775,"lng":-29.794921875000004},{"lat":-51.39920565355378,"lng":28.125000000000004},{"lat":-24.44714958973082,"lng":28.300781250000004}]	Sala Riunioni 4	1
14	LIBERO	A2	[{"lat":21.616579336740603,"lng":28.388671875000004},{"lat":-12.983147716796566,"lng":28.388671875000004},{"lat":-12.811801316582619,"lng":57.39257812500001},{"lat":21.37124437061832,"lng":57.30468750000001}]	Ufficio 1	1
15	LIBERO	A2	[{"lat":-24.607069137709694,"lng":28.388671875000004},{"lat":-51.39920565355378,"lng":28.388671875000004},{"lat":-51.344338660599234,"lng":57.30468750000001},{"lat":-24.766784522874442,"lng":57.48046875000001}]	Ufficio 2	1
16	LIBERO	A2	[{"lat":-12.983147716796566,"lng":86.48437500000001},{"lat":21.453068633086783,"lng":86.396484375},{"lat":21.453068633086783,"lng":57.568359375},{"lat":-12.811801316582619,"lng":57.48046875000001}]	Ufficio 3	1
17	LIBERO	A2	[{"lat":-24.607069137709694,"lng":57.39257812500001},{"lat":-24.607069137709694,"lng":86.57226562500001},{"lat":-51.56341232867589,"lng":86.48437500000001},{"lat":-51.45400691005982,"lng":57.39257812500001}]	Ufficio 4	1
18	LIBERO	A	[{"lat":51.39920565355378,"lng":100.72265625000001},{"lat":51.50874245880335,"lng":179.47265625000003},{"lat":-40.979898069620134,"lng":179.47265625000003},{"lat":-41.11246878918086,"lng":132.18750000000003},{"lat":5.528510525692801,"lng":131.92382812500003},{"lat":5.266007882805498,"lng":108.80859375000001},{"lat":4.915832801313164,"lng":100.63476562500001}]	Uffici Cubicles	1
\.


--
-- Data for Name: prenotazione; Type: TABLE DATA; Schema: public; Owner: zam
--

COPY public.prenotazione (id, inizio, fine, nmod, id_utente, id_asset) FROM stdin;
41	2025-03-11 11:52:00	2025-03-11 13:52:00	0	1	10
42	2025-03-26 11:16:00	2025-03-26 14:17:00	0	0	10
43	2025-03-14 09:37:00	2025-03-14 14:37:00	0	0	10
\.


--
-- Data for Name: utente; Type: TABLE DATA; Schema: public; Owner: zam
--

COPY public.utente (id, nome, cognome, username, password, tipo, coordinatore) FROM stdin;
2	Provo	McProva	prova	Ylil4Ot3KRHU+SvltdsOFFEe2+AdHQ3dHVosuduaVro=	DIPENDENTE	0
0	Matteo	Forlani	m.forlani	Ylil4Ot3KRHU+SvltdsOFFEe2+AdHQ3dHVosuduaVro=	COORDINATORE	\N
3	Francesco	Giaquinto	f.giaquinto	Ylil4Ot3KRHU+SvltdsOFFEe2+AdHQ3dHVosuduaVro=	GESTORE	\N
1	Giacomo	Agatan	g.agatan	Ylil4Ot3KRHU+SvltdsOFFEe2+AdHQ3dHVosuduaVro=	DIPENDENTE	0
\.


--
-- Data for Name: zamtoken; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.zamtoken (id, val, idutente, created) FROM stdin;
68	b82976ad-7588-40dc-aa13-417011dd4462	3	2025-03-14 08:45:31.034754
\.


--
-- Name: asset_id_seq; Type: SEQUENCE SET; Schema: public; Owner: zam
--

SELECT pg_catalog.setval('public.asset_id_seq', 18, true);


--
-- Name: prenotare_id_asset_seq; Type: SEQUENCE SET; Schema: public; Owner: zam
--

SELECT pg_catalog.setval('public.prenotare_id_asset_seq', 1, false);


--
-- Name: prenotare_id_seq; Type: SEQUENCE SET; Schema: public; Owner: zam
--

SELECT pg_catalog.setval('public.prenotare_id_seq', 43, true);


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

SELECT pg_catalog.setval('public.utente_id_seq', 3, true);


--
-- Name: zamtoken_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.zamtoken_id_seq', 68, true);


--
-- Name: zamtoken_idutente_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
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
-- Name: zamtoken zamtoken_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
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
-- Name: zamtoken zamtoken_idutente_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.zamtoken
    ADD CONSTRAINT zamtoken_idutente_fkey FOREIGN KEY (idutente) REFERENCES public.utente(id);


--
-- Name: SCHEMA public; Type: ACL; Schema: -; Owner: postgres
--

GRANT USAGE ON SCHEMA public TO zam;


--
-- PostgreSQL database dump complete
--

