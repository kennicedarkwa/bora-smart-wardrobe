// --- FUNKTION 1: Lädt das Wetter auf der aktuellen Seite im Kasten ---
async function loadWeather() {
    const city = document.getElementById("city").value;
    const date = document.getElementById("date").value;
    const resultDiv = document.getElementById("result");

    try {
        const response = await fetch(`/api/weather/${city}/${date}`);

        if (!response.ok) {
            resultDiv.innerHTML = "❌ Keine Daten gefunden";
            return;
        }

        const data = await response.json();
        const temp = data.temperature.averageCelsius;

        // Hole beide Werte sauber aus dem JSON-Objekt
        const precipProb = data.precipitation?.probability_percent ?? 0;
        const precipMm = data.precipitation?.amount_mm ?? 0;

        let rainDisplay = "";
        let emoji = "🌤️";

        // --- SAUBERE DISPLAY-STEUERUNG ANHAND DER FELDER ---
        if (precipMm > 0) {
            // Wenn reale Millimeter vorhanden sind -> Nutze historische Anzeige
            rainDisplay = `${precipMm.toFixed(1)} mm`;
            if (precipMm > 1.0) emoji = "🌧️";
        } else {
            // Wenn keine Millimeter gemessen wurden -> Nutze normale Prozent-Vorhersage
            rainDisplay = `${precipProb}%`;
            if (precipProb > 60) emoji = "🌧️";
        }

        if (temp > 25 && precipMm === 0 && precipProb <= 30) emoji = "☀️";
        else if (temp < 10 && (precipMm > 0 || precipProb > 0)) emoji = "❄️";

        resultDiv.innerHTML = `
            <div style="font-size:40px">${emoji}</div>
            <b>${city}</b><br><br>
            🌡️ Temp: ${temp}°C<br>
            🌧️ Niederschlag: ${rainDisplay}<br>
            🌬️ UV Index: ${data.uv_index ?? 0}
        `;
    } catch (err) {
        resultDiv.innerHTML = "⚠️ Fehler beim Laden";
    }
}

// --- FUNKTION 2: Bringt den Nutzer zur Empfehlungsseite (Für den "Customize !"-Button) ---
function goToRecommendation() {
    const city = document.getElementById("city").value;
    const date = document.getElementById("date").value;
    const occasion = document.getElementById('occasion').value;

    // Leitet sauber weiter und übergibt Stadt, Datum und Anlass in der URL
    window.location.href = `empfehlung.html?city=${encodeURIComponent(city)}&date=${encodeURIComponent(date)}&occasion=${encodeURIComponent(occasion)}`;
}

// --- AUTOMATISCHES LADEN BEIM SEITENSTART ---
document.addEventListener("DOMContentLoaded", () => {
    const dateInput = document.getElementById("date");

    // 1. Heutiges Datum im Format YYYY-MM-DD berechnen
    const heute = new Date();
    const jahr = heute.getFullYear();
    // Monate und Tage brauchen eine führende Null, wenn sie einstellig sind (z.B. "06" statt "6")
    const monat = String(heute.getMonth() + 1).padStart(2, '0');
    const tag = String(heute.getDate()).padStart(2, '0');

    const heutigesDatumFormatiert = `${jahr}-${monat}-${tag}`;

    // 2. Das Datum im Input-Feld setzen
    dateInput.value = heutigesDatumFormatiert;

    // 3. Wetter direkt automatisch für heute abfragen
    loadWeather();
});

function openSettings() {
    alert("Einstellungen werden geöffnet...");
    // Hier kannst du später z.B. zu einersettings.html weiterleiten:
    // window.location.href = "settings.html";
}

async function logout() {
    const bestaetigung = confirm("Möchtest du dich wirklich ausloggen?");
    if (bestaetigung) {
        await fetch("/api/benutzer/logout", { method: "POST" });
        window.location.href = "benutzer/login.html";
    }
}