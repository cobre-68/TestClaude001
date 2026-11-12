# Zentrale – Projektkontext für Claude

## Vision
Persönliche App für Martin (Android), die zum „zentralen Nervensystem“ seiner Kommunikation wird:
Termine, Erinnerungen, Wecken, Sprachsteuerung – und schrittweise immer weiter an seine Bedürfnisse angepasst.
Entwickelt wird überwiegend vom Handy aus über Claude Code.

## Grundprinzipien
- **Privat und Geschäftlich strikt getrennt.** Jeder Eintrag hat genau einen `context`: `privat` | `business`.
  Geschäftlich = 747 Studios (nutzt Google Calendar).
- **Sprache zuerst.** Eingabe per Spracherkennung (de-DE), Bestätigung/Ansagen per Sprachausgabe.
- **Kleine, testbare Schritte.** Jede Änderung muss auf dem Handy sofort ausprobierbar sein.
- UI-Sprache: Deutsch.

## Architektur (v0.1)
- Reine Web-App (PWA), kein Build-Schritt: `index.html` (UI + Logik), `sw.js` (Offline + Benachrichtigungen), `manifest.json`.
- Daten lokal in `localStorage` über das `Store`-Objekt. Der Store ist bewusst gekapselt,
  damit später ein Google-Calendar-Adapter (ein Kalender pro Bereich) eingesetzt werden kann.
- `parseGerman()` versteht freie Sätze wie „Freitag 14:30 Meeting mit Rikke geschäftlich“.
- Hosting: GitHub Pages über `.github/workflows/pages.yml` (Deploy bei jedem Push auf `main`).

## Roadmap
1. ✅ Eigener Kalender, Bereiche, Spracheingabe, Erinnerungen solange App offen
2. KI-Assistent (Claude API) für freie Gespräche und Tagesplanung
3. Capacitor-Wrapper → echte Android-APK für zuverlässige Wecker & Hintergrund-Benachrichtigungen
4. Google-Calendar-Anbindung (privat + 747 getrennt)
5. Weitere Kanäle (Mail, Messenger) → „Nervensystem“

## Bekannte Grenzen
- Als Web-App kann Android Erinnerungen bei geschlossener App nicht garantieren; echtes Wecken erst mit Schritt 3.
