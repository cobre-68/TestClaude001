# Feature-Sammlung – Zentrale

Hier sammeln wir Wünsche und Ideen, um sie gebündelt in einem Rutsch umzusetzen.
Status: 🟢 gewünscht · 💡 Vorschlag (noch nicht entschieden) · ⏸ zurückgestellt · ✅ umgesetzt

## Umgesetzt in v0.7 (13.11.2026)

### Kalender: vorerst nur privat
- ✅ **747-Kalender außen vor lassen**, bis die App stabil läuft: geschäftliche Kalender weder lesen noch beschreiben.
  Private Termine landen im privaten Google-Kalender. Geschäftliche Termine bleiben vorerst lokal in der App.
  (Sofort-Lösung ohne Update: in ⚙️ die 747-Kalender auf „Aus“ stellen.)

### Stumm-Modus
- ✅ Sprachausgabe komplett abschaltbar (ein Tipp im Header, 🔊/🔇). Alles, was die App sonst sagt
  (Bestätigungen, Morgenbriefing, Abendrückblick, Erinnerungen, Warnungen), erscheint dann nur als Text-Karte.

### Kontakte
- ✅ **Zugriff auf die Handy-Kontakte** (Plugin `@capacitor-community/contacts` 8.x, passt zu Capacitor 8):
  Name, Telefon, E-Mail, **Adressen**.
- ✅ **Eigene Infos zu Personen vorhalten:** Notizen, Bereich (privat/747), Vorbereitung für Treffen,
  „zuletzt gesehen“ (aus Terminen).
- ✅ Verknüpfung mit Terminen: „Treffen mit Bettina“ → Person erkannt, Adresse als Ort, Navigation mit einem Tipp.
- ✅ Nachrichten („Sag Bettina …“) nutzen die Telefonnummer aus den Kontakten statt eigener Liste.

### Wetter & Verkehr – Termin erreichen
- ✅ **Wetter:** im Morgenbriefing und am Termin (Regen/Kälte am Zielort zur Terminzeit). Kostenlos über Open-Meteo,
  kein Schlüssel nötig.
- ✅ **Verkehr / Fahrzeit:** echte Fahrzeit zum Termin-Ort statt geschätzter Wegzeit, Losfahr-Erinnerung passt sich an
  (Stau → früher los). Braucht einen Routing-Dienst mit Verkehrsdaten (z. B. TomTom oder HERE, kostenloses
  Kontingent, Schlüssel nötig) – Auswahl vor der Umsetzung klären.

### Ansichten
- ✅ **Tagesansicht:** Zeitleiste des Tages, Lücken sichtbar, Wegzeiten als Blöcke, Office/Homeoffice.
- ✅ **Wochenansicht:** 7 Tage auf einen Blick, privat/geschäftlich farbig, freie Zeitfenster.
  (Zusätzlich zur bisherigen Liste, umschaltbar.)

### Wecker
- ✅ **Vollbild-Wecker:** Klingeln mit Vollbild auch bei gesperrtem Handy, Dauerton bis „Aus“, Umdrehen und
  Schlummern direkt auf dem Sperrbildschirm (native Android-Erweiterung).

- ✅ **Datensicherung:** Export/Import als Datei (Android: Teilen → z. B. Google Drive).

## Vorschläge (noch offen)
- 💡 **Automatische Sicherung** (z. B. wöchentlich nach Google Drive).
- 💡 **Update-Hinweis in der APK:** „Neue Version verfügbar“ mit Download-Knopf.
- 💡 **Startbildschirm-Widget:** nächster Termin + Mikrofon-Knopf.
- 💡 **App-Kurzbefehle:** lange aufs App-Icon → „Gedanke“, „Termin“, „Wecker“.
- 💡 **Aufgaben mit Fälligkeit:** neben Gedanken echte To-dos, getrennt privat/geschäftlich.

## Zurückgestellt
- ⏸ 747-Kalender (lesen/schreiben) – bis die App stabil läuft.
- ⏸ KI-Assistent (Claude) – siehe Roadmap in CLAUDE.md.
- ⏸ Benachrichtigungen mitlesen (WhatsApp & Co.).
- ⏸ Standort im Hintergrund.
