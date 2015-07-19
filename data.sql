--
-- PostgreSQL database dump
--

-- Dumped from database version 9.4.4
-- Dumped by pg_dump version 9.4.4
-- Started on 2015-07-19 19:20:43 CEST

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

SET search_path = public, pg_catalog;

--
-- TOC entry 2071 (class 0 OID 16455)
-- Dependencies: 175
-- Data for Name: projects; Type: TABLE DATA; Schema: public; Owner: kanban
--

INSERT INTO projects (id, title, description) VALUES (1, 'Premier projet', 'Ceci est le premier projet');
INSERT INTO projects (id, title, description) VALUES (2, 'Second projet', 'Voilà un projet très intéressant');


--
-- TOC entry 2083 (class 0 OID 0)
-- Dependencies: 174
-- Name: projects_id_seq; Type: SEQUENCE SET; Schema: public; Owner: kanban
--

SELECT pg_catalog.setval('projects_id_seq', 2, true);


--
-- TOC entry 2073 (class 0 OID 16466)
-- Dependencies: 177
-- Data for Name: steps; Type: TABLE DATA; Schema: public; Owner: kanban
--

INSERT INTO steps (id, title, step_order, project_id) VALUES (1, 'DONE', 3, 1);
INSERT INTO steps (id, title, step_order, project_id) VALUES (2, 'DEVELOP', 2, 1);
INSERT INTO steps (id, title, step_order, project_id) VALUES (3, 'TODO', 1, 1);
INSERT INTO steps (id, title, step_order, project_id) VALUES (4, 'DONE', 3, 2);
INSERT INTO steps (id, title, step_order, project_id) VALUES (5, 'DEVELOP', 2, 2);
INSERT INTO steps (id, title, step_order, project_id) VALUES (6, 'TODO', 1, 2);


--
-- TOC entry 2084 (class 0 OID 0)
-- Dependencies: 176
-- Name: steps_id_seq; Type: SEQUENCE SET; Schema: public; Owner: kanban
--

SELECT pg_catalog.setval('steps_id_seq', 6, true);


--
-- TOC entry 2075 (class 0 OID 16479)
-- Dependencies: 179
-- Data for Name: stories; Type: TABLE DATA; Schema: public; Owner: kanban
--

INSERT INTO stories (id, title, description, project_id, step_id, number, priority, bg_color, fg_color) VALUES (6, 'Une story rose', '', 1, NULL, 6, 2, '#ED839D', '#333333');
INSERT INTO stories (id, title, description, project_id, step_id, number, priority, bg_color, fg_color) VALUES (7, 'Une story bleue', '', 1, NULL, 7, 2, '#36A9CE', '#405362');
INSERT INTO stories (id, title, description, project_id, step_id, number, priority, bg_color, fg_color) VALUES (1, 'Une belle story', '', 1, 2, 1, 3, '#e9e74a', '#333333');
INSERT INTO stories (id, title, description, project_id, step_id, number, priority, bg_color, fg_color) VALUES (3, 'Une story qui déchire', '', 1, NULL, 3, 1, '#e9e74a', '#333333');
INSERT INTO stories (id, title, description, project_id, step_id, number, priority, bg_color, fg_color) VALUES (5, 'Une story super importante', '', 1, NULL, 5, 1, '#e9e74a', '#333333');
INSERT INTO stories (id, title, description, project_id, step_id, number, priority, bg_color, fg_color) VALUES (2, 'Une story pas mal', '', 1, 3, 2, 2, '#ED839D', '#333333');
INSERT INTO stories (id, title, description, project_id, step_id, number, priority, bg_color, fg_color) VALUES (4, 'Une chouette story', '', 1, NULL, 4, 2, '#36A9CE', '#333333');
INSERT INTO stories (id, title, description, project_id, step_id, number, priority, bg_color, fg_color) VALUES (8, 'Une story assez importante', '', 1, NULL, 8, 1, '#AE86BC', '#333333');
INSERT INTO stories (id, title, description, project_id, step_id, number, priority, bg_color, fg_color) VALUES (9, 'jmdfjqd', 'fqdlmj qd jmj qmm qdfmlj qdf mlj qdf qs', 1, NULL, 9, 2, '#E9E74A', '#333333');


--
-- TOC entry 2085 (class 0 OID 0)
-- Dependencies: 178
-- Name: stories_id_seq; Type: SEQUENCE SET; Schema: public; Owner: kanban
--

SELECT pg_catalog.setval('stories_id_seq', 9, true);


-- Completed on 2015-07-19 19:20:43 CEST

--
-- PostgreSQL database dump complete
--

