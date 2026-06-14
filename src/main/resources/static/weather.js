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

// --- SAUBERE DISPLAY-STEUERUNG ANHAND DER NEUEN FELDER ---
        if (precipMm > 0) {
            // Wenn reale Millimeter vorhanden sind -> Nutze historische Anzeige
            // .toFixed(1) sorgt dafür, dass z.B. "2.4 mm" statt "2.4000001 mm" angezeigt wird
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