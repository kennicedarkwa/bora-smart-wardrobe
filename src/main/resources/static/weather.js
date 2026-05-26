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
        const rain = data.precipitation?.probability_percent;

        let emoji = "🌤️";

        if (rain > 60) emoji = "🌧️";
        else if (temp > 25) emoji = "☀️";
        else if (temp < 10) emoji = "❄️";

        resultDiv.innerHTML = `
            <div style="font-size:40px">${emoji}</div>
            <b>${city}</b><br><br>
            🌡️ Temp: ${temp}°C<br>
            🌧️ Rain: ${rain}%<br>
            🌬️ UV Index: ${data.uv_index}
        `;
    } catch (err) {
        resultDiv.innerHTML = "⚠️ Fehler beim Laden";
    }
}