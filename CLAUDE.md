# Zentrale – Projektkontext für Claude

## Vision
Persönliche App für Martin (Android), die zum „zentralen Nervensystem“ seiner Kommunikation wird:
Termine, Erinnerungen, Wecken, Sprachsteuerung – und schrittweise immer weiter an seine Bedürfnisse angepasst.
Entwickelt wird überwiegend vom Handy aus über Claude Code.

## Grundprinzipien
- **Privat und Geschäftlich strikt getrennt.** Jeder Eintrag hat genau einen `context`: `privat` | `business`.
  Geschäftlich = 747 Studios: Exchange/Kopano-Kalender (kupfer@747studios.de, ActiveSync), auf dem Android
  in der Google-Kalender-App eingebunden, aber NICHT über die Google-API erreichbar → Anbindung über CalDAV/iCal klären.
  Privat = Google-Konto.
- **Sprache zuerst.** Eingabe per Spracherkennung (de-DE), Bestätigung/Ansagen per Sprachausgabe.
- **Kleine, testbare Schritte.** Jede Änderung muss auf dem Handy sofort ausprobierbar sein.
- UI-Sprache: Deutsch.

## Ansichten
Oben (und per Wischen): Alle · Privat · Geschäftlich · 💭 Gedanken · ⏰ Wecker. ⚙️ Einstellungen im Header.

## Architektur (v0.3)
- Reine Web-App (PWA), kein Build-Schritt: `index.html` (UI + Logik), `sw.js` (Offline + Benachrichtigungen), `manifest.json`.
- Daten lokal in `localStorage` über das `Store`-Objekt. Der Store ist bewusst gekapselt,
  damit später ein Google-Calendar-Adapter (ein Kalender pro Bereich) eingesetzt werden kann.
- Wecker separat in `Alarms` (einmalig oder an Wochentagen), Nachtmodus mit Wake Lock, Klingeln mit Schlummern;
  nach „Aus“ folgt ein gesprochenes Morgenbriefing aus dem Kalender. „Weck mich …“ per Sprache legt einen Wecker an.
- `Notes`: geparkte Gedanken (Sprachnotiz, Bereich, erledigt, → Termin umwandeln).
- `Settings`: Feierabend-Zeiten (geschäftliche Erinnerungen stumm), Wochenende frei, Aufsteh-Vorlauf, Sprachbestätigung.
- Verknüpfungen: Weckvorschlag aus erstem Termin morgen (Vorlauf + Wegzeit), Abendrückblick (🔊 ab 18 Uhr, beim
  Nachtmodus), Losfahr-Erinnerung (Ort + Wegzeit, Maps-Link), „Sag X, dass …“ → WhatsApp/SMS (Nummern in Prefs.contacts).
- Sprach-Routing in `#voiceOk`: Gedanke → Nachricht → Wecker → Termin.
- `parseGerman()` versteht freie Sätze wie „Freitag 14:30 Meeting mit Rikke geschäftlich“.
- Hosting: GitHub Pages über `.github/workflows/pages.yml` (Deploy bei jedem Push auf `main`).

## Roadmap
1. ✅ Eigener Kalender, Bereiche, Spracheingabe, Erinnerungen solange App offen
1b. ✅ Wecker mit Nachtmodus, Wischen zwischen Ansichten
1c. ✅ Gedanken, Weckvorschlag, Abendrückblick, Feierabend, Losfahren, Nachrichten
2. KI-Assistent (Claude API) für freie Gespräche und Tagesplanung
3. Capacitor-Wrapper → echte Android-APK für zuverlässige Wecker & Hintergrund-Benachrichtigungen
4. Kalender-Anbindung: privat über Google, 747 über Exchange/CalDAV
5. Weitere Kanäle (Mail, Messenger) → „Nervensystem“

## Bekannte Grenzen
- Als Web-App kann Android Erinnerungen bei geschlossener App nicht garantieren; echtes Wecken erst mit Schritt 3.
