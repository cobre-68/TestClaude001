// Kopiert die Web-App nach www/ (für Capacitor) bzw. in ein Zielverzeichnis (für GitHub Pages)
const fs = require('fs');
const path = require('path');
const out = path.resolve(process.argv[2] || 'www');
const files = ['index.html', 'sw.js', 'manifest.json', 'icon.svg', 'icons'];
fs.rmSync(out, { recursive: true, force: true });
fs.mkdirSync(out, { recursive: true });
for (const f of files) fs.cpSync(f, path.join(out, f), { recursive: true });
const core = 'node_modules/@capacitor/core/dist/capacitor.js';
if (fs.existsSync(core)) fs.copyFileSync(core, path.join(out, 'capacitor.js'));
console.log('Web-App nach', out, 'kopiert');
