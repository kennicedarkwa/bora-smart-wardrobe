// JSON (Wetter)
const data = {
    cities: [
        {
            city: "Hamburg",
            daily_forecast: [
                {
                    temperature: { min_celsius: 10, max_celsius: 15 },
                    precipitation: { type: "rain" }
                }
            ]
        }
    ]
};

// sofort ausführen
loadOutfit();

async function loadOutfit() {

    const liste = document.getElementById("outfitListe");
    const infoBox = document.getElementById("infoBox");

    const forecast = data.cities[0].daily_forecast[0];

    const temp = forecast.temperature.max_celsius;
    const rain = forecast.precipitation.type !== "none";

    const backendForecast = {
        temperature: {
            min_celsius: temp - 5,
            max_celsius: temp
        },
        precipitation: {
            type: rain ? "rain" : "none",
            probability_percent: rain ? 50 : 0
        }
    };

    try {
        const response = await fetch("http://localhost:8080/api/empfehlung", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(backendForecast)
        });

        const data = await response.json();

        liste.innerHTML = "";

        // WENN KEINE DATEN → MELDUNG
        if (!data.kleidungsstuecke || data.kleidungsstuecke.length === 0) {
            infoBox.style.display = "block";
            return;
        }

        // SONST: ANZEIGEN
        data.kleidungsstuecke.forEach(item => {

            const div = document.createElement("div");
            div.className = "kleidungsstueck";

            div.innerHTML = `
                <p><strong>${item.name}</strong></p>
                ${item.bildURL ? `<img src="${item.bildURL}">` : ""}
            `;

            liste.appendChild(div);
        });

    } catch (e) {
        infoBox.style.display = "block";
    }
}
``