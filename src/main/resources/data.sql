-- Event Management System - Demo Data
-- This file is automatically executed by Spring Boot when the database is empty
-- Only runs when spring.sql.init.mode=always (configured per profile)

-- Insert Ressorts
INSERT INTO ressort (id, name, beschreibung, zustaendigkeiten, kontaktperson) VALUES
(1, 'Küche', 'Verantwortlich für Essensversorgung', 'Kochen, Ausgabe, Reinigung', 'Maria Schmidt'),
(2, 'Bar', 'Getränkeversorgung und -ausgabe', 'Getränkezubereitung, Verkauf, Bestandsführung', 'Thomas Weber'),
(3, 'Sicherheit', 'Sicherheit und Ordnung', 'Einlasskontrolle, Überwachung, Erste Hilfe', 'Andreas Müller'),
(4, 'Technik', 'Technische Infrastruktur', 'Aufbau, Wartung, Abbau von Technik', 'Sarah Fischer'),
(5, 'Dekoration', 'Gestaltung und Atmosphäre', 'Aufbau, Pflege und Abbau der Dekoration', 'Laura Klein')
ON CONFLICT (id) DO NOTHING;

-- Insert Helfer
INSERT INTO helfer (id, vorname, nachname, email, telefon, stammressort_id) VALUES
(1, 'Max', 'Mustermann', 'max.mustermann@example.com', '0123-456789', 1),
(2, 'Anna', 'Schmidt', 'anna.schmidt@example.com', '0123-456790', 1),
(3, 'Peter', 'Meyer', 'peter.meyer@example.com', '0123-456791', 2),
(4, 'Julia', 'Wagner', 'julia.wagner@example.com', '0123-456792', 2),
(5, 'Michael', 'Becker', 'michael.becker@example.com', '0123-456793', 3),
(6, 'Sabine', 'Hoffmann', 'sabine.hoffmann@example.com', '0123-456794', 3),
(7, 'Christian', 'Koch', 'christian.koch@example.com', '0123-456795', 4),
(8, 'Nina', 'Wolf', 'nina.wolf@example.com', '0123-456796', 4),
(9, 'Stefan', 'Schröder', 'stefan.schroeder@example.com', '0123-456797', 5),
(10, 'Lisa', 'Neumann', 'lisa.neumann@example.com', '0123-456798', 5),
(11, 'Frank', 'Zimmermann', 'frank.zimmermann@example.com', '0123-456799', 1),
(12, 'Petra', 'Braun', 'petra.braun@example.com', '0123-456800', 2),
(13, 'Markus', 'Lange', 'markus.lange@example.com', '0123-456801', 3),
(14, 'Sandra', 'Krause', 'sandra.krause@example.com', '0123-456802', 4),
(15, 'Daniel', 'Richter', 'daniel.richter@example.com', '0123-456803', 5)
ON CONFLICT (email) DO NOTHING;

-- Insert Schichten (30 days from now)
INSERT INTO schicht (id, name, startzeit, endzeit, beschreibung) VALUES
(1, 'Morgen-Schicht', CURRENT_TIMESTAMP + INTERVAL '30 days' + TIME '08:00:00', CURRENT_TIMESTAMP + INTERVAL '30 days' + TIME '14:00:00', 'Aufbau und Vorbereitung'),
(2, 'Mittags-Schicht', CURRENT_TIMESTAMP + INTERVAL '30 days' + TIME '14:00:00', CURRENT_TIMESTAMP + INTERVAL '30 days' + TIME '20:00:00', 'Hauptveranstaltung'),
(3, 'Abend-Schicht', CURRENT_TIMESTAMP + INTERVAL '30 days' + TIME '20:00:00', CURRENT_TIMESTAMP + INTERVAL '31 days' + TIME '02:00:00', 'Nachveranstaltung und Abbau')
ON CONFLICT (id) DO NOTHING;

-- Insert Einsätze (Morgen-Schicht)
INSERT INTO einsatz (id, beschreibung, startzeit, endzeit, ort, mittel, benoetigte_helfer, ressort_id, schicht_id, status) VALUES
(1, 'Küche vorbereiten', CURRENT_TIMESTAMP + INTERVAL '30 days' + TIME '08:00:00', CURRENT_TIMESTAMP + INTERVAL '30 days' + TIME '14:00:00', 'Hauptküche', 'Kochutensilien, Zutaten', 3, 1, 1, 'IN_PLANUNG'),
(2, 'Bar aufbauen', CURRENT_TIMESTAMP + INTERVAL '30 days' + TIME '08:00:00', CURRENT_TIMESTAMP + INTERVAL '30 days' + TIME '14:00:00', 'Hauptbar', 'Kühlung, Gläser, Getränke', 2, 2, 1, 'VOLLSTAENDIG'),
(3, 'Technik-Setup', CURRENT_TIMESTAMP + INTERVAL '30 days' + TIME '08:00:00', CURRENT_TIMESTAMP + INTERVAL '30 days' + TIME '14:00:00', 'Hauptbühne', 'Mikrofone, Lautsprecher, Beleuchtung', 2, 4, 1, 'VOLLSTAENDIG'),
(4, 'Dekoration aufbauen', CURRENT_TIMESTAMP + INTERVAL '30 days' + TIME '08:00:00', CURRENT_TIMESTAMP + INTERVAL '30 days' + TIME '14:00:00', 'Gesamtgelände', 'Banner, Ballons, Tische', 2, 5, 1, 'VOLLSTAENDIG'),
-- Mittags-Schicht
(5, 'Essensausgabe', CURRENT_TIMESTAMP + INTERVAL '30 days' + TIME '14:00:00', CURRENT_TIMESTAMP + INTERVAL '30 days' + TIME '20:00:00', 'Hauptküche', 'Besteck, Teller, Servietten', 4, 1, 2, 'IN_PLANUNG'),
(6, 'Getränkeausschank', CURRENT_TIMESTAMP + INTERVAL '30 days' + TIME '14:00:00', CURRENT_TIMESTAMP + INTERVAL '30 days' + TIME '20:00:00', 'Hauptbar', 'Zapfanlage, Eiswürfel', 3, 2, 2, 'IN_PLANUNG'),
(7, 'Einlasskontrolle', CURRENT_TIMESTAMP + INTERVAL '30 days' + TIME '14:00:00', CURRENT_TIMESTAMP + INTERVAL '30 days' + TIME '20:00:00', 'Haupteingang', 'Scanner, Armbänder', 2, 3, 2, 'VOLLSTAENDIG'),
(8, 'Technikbetreuung', CURRENT_TIMESTAMP + INTERVAL '30 days' + TIME '14:00:00', CURRENT_TIMESTAMP + INTERVAL '30 days' + TIME '20:00:00', 'Hauptbühne', 'Mischpult, Kabel', 2, 4, 2, 'VOLLSTAENDIG'),
-- Abend-Schicht
(9, 'Küche aufräumen', CURRENT_TIMESTAMP + INTERVAL '30 days' + TIME '20:00:00', CURRENT_TIMESTAMP + INTERVAL '31 days' + TIME '02:00:00', 'Hauptküche', 'Reinigungsmittel', 3, 1, 3, 'OFFEN'),
(10, 'Bar abbauen', CURRENT_TIMESTAMP + INTERVAL '30 days' + TIME '20:00:00', CURRENT_TIMESTAMP + INTERVAL '31 days' + TIME '02:00:00', 'Hauptbar', 'Transportboxen', 2, 2, 3, 'OFFEN'),
(11, 'Gelände sichern', CURRENT_TIMESTAMP + INTERVAL '30 days' + TIME '20:00:00', CURRENT_TIMESTAMP + INTERVAL '31 days' + TIME '02:00:00', 'Gesamtgelände', 'Taschenlampen, Funkgeräte', 3, 3, 3, 'IN_PLANUNG'),
(12, 'Technik abbauen', CURRENT_TIMESTAMP + INTERVAL '30 days' + TIME '20:00:00', CURRENT_TIMESTAMP + INTERVAL '31 days' + TIME '02:00:00', 'Hauptbühne', 'Transportwagen, Kabel', 2, 4, 3, 'VOLLSTAENDIG'),
(13, 'Dekoration abbauen', CURRENT_TIMESTAMP + INTERVAL '30 days' + TIME '20:00:00', CURRENT_TIMESTAMP + INTERVAL '31 days' + TIME '02:00:00', 'Gesamtgelände', 'Müllsäcke, Transportboxen', 2, 5, 3, 'VOLLSTAENDIG')
ON CONFLICT (id) DO NOTHING;

-- Assign Helfer to Einsätze (junction table)
INSERT INTO einsatz_helfer (einsatz_id, helfer_id) VALUES
-- Einsatz 1: Küche vorbereiten (2 of 3 needed)
(1, 1), (1, 2),
-- Einsatz 2: Bar aufbauen (2 of 2 needed)
(2, 3), (2, 4),
-- Einsatz 3: Technik-Setup (2 of 2 needed)
(3, 7), (3, 8),
-- Einsatz 4: Dekoration aufbauen (2 of 2 needed)
(4, 9), (4, 10),
-- Einsatz 5: Essensausgabe (3 of 4 needed)
(5, 1), (5, 2), (5, 11),
-- Einsatz 6: Getränkeausschank (3 of 3 needed)
(6, 3), (6, 4), (6, 12),
-- Einsatz 7: Einlasskontrolle (2 of 2 needed)
(7, 5), (7, 6),
-- Einsatz 8: Technikbetreuung (2 of 2 needed)
(8, 7), (8, 8),
-- Einsatz 11: Gelände sichern (2 of 3 needed)
(11, 13), (11, 5),
-- Einsatz 12: Technik abbauen (2 of 2 needed)
(12, 14), (12, 7),
-- Einsatz 13: Dekoration abbauen (2 of 2 needed)
(13, 9), (13, 10)
ON CONFLICT (einsatz_id, helfer_id) DO NOTHING;

-- Reset sequences to avoid conflicts with future inserts
SELECT setval('ressort_id_seq', (SELECT MAX(id) FROM ressort));
SELECT setval('helfer_id_seq', (SELECT MAX(id) FROM helfer));
SELECT setval('schicht_id_seq', (SELECT MAX(id) FROM schicht));
SELECT setval('einsatz_id_seq', (SELECT MAX(id) FROM einsatz));
