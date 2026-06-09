// 1. ANLASS HOLEN: Wir lesen den Anlass aus dem Speicher (von der vorherigen Seite)
const gewaehlterAnlass = localStorage.getItem("bora_anlass") || "CASUAL";

// Hier merken wir uns die IDs für den "wear" Button
let aktuelleEmpfehlungsIds = [];

document.addEventListener("DOMContentLoaded", async () => {
    document.getElementById("anlassTitel").textContent = gewaehlterAnlass.toLowerCase();
    await ladeUndFiltereKleidung();
});

async function ladeUndFiltereKleidung() {
    const container = document.getElementById("kategorienContainer");

    try {
        // 2. WETTER HOLEN: Verknüpfung mit dem WeatherController
        const stadt = "Hamburg";
        const datum = "2026-05-05"; // Dein aktuelles Datum aus der JSON

        const weatherResponse = await fetch(`/api/weather/${stadt}/${datum}`);
        if (!weatherResponse.ok) throw new Error("Wetter-API nicht erreichbar");
        const aktuellesWetter = await weatherResponse.json();

        // 3. BACKEND ANFRAGEN: Das JSON muss EXAKT wie eure Java-Klasse "DailyForecast" aussehen
        const backendForecast = {
            date: datum,
            temperature: {
                min_celsius: aktuellesWetter.temperature.min_celsius,
                max_celsius: aktuellesWetter.temperature.max_celsius
            },
            precipitation: {
                type: aktuellesWetter.precipitation.type,
                probability_percent: aktuellesWetter.precipitation.probability_percent
            }
        };

        // ✅ HIER IST DIE ÄNDERUNG: Wir hängen den Anlass an die URL an!
        const url = `/api/empfehlung?anlass=${gewaehlterAnlass}`;

        // Ruft deinen EmpfehlungController in Java auf
        const response = await fetch(url, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(backendForecast)
        });

        const empfehlungData = await response.json();
        container.innerHTML = "";

        // Prüfen, ob das Backend Klamotten gefunden hat
        if (!empfehlungData.kleidungsstuecke || empfehlungData.kleidungsstuecke.length === 0) {
            container.innerHTML = `<p style='text-align: center; font-weight:bold;'>Keine passenden Stücke für <b>${gewaehlterAnlass}</b> im Schrank.</p>`;
            return;
        }

        // IDs für den "wear" Button merken
        aktuelleEmpfehlungsIds = empfehlungData.kleidungsstuecke.map(item => item.id);

        // 4. ANZEIGEN: Nach "hauptkategorie" gruppieren (Oberteil, Hose, etc.)
        const gruppierteKleidung = {};

        empfehlungData.kleidungsstuecke.forEach(item => {
            const kategorie = item.hauptkategorie ? item.hauptkategorie : "SONSTIGES";

            if (!gruppierteKleidung[kategorie]) {
                gruppierteKleidung[kategorie] = [];
            }
            gruppierteKleidung[kategorie].push(item);
        });

        // HTML-Boxen zeichnen
        for (const [kategorieName, items] of Object.entries(gruppierteKleidung)) {
            zeichneKategorieReihe(kategorieName, items, container);
        }

    } catch (error) {
        console.error("Fehler bei der Empfehlung:", error);
        container.innerHTML = "<p style='text-align: center; color: red;'>Fehler: Backend antwortet nicht.</p>";
    }
}

// Hilfsfunktion: Zeichnet die hellblauen Reihen
function zeichneKategorieReihe(kategorieName, items, parentContainer) {
    const reiheDiv = document.createElement("div");
    reiheDiv.className = "kategorie-reihe";

    const titel = document.createElement("div");
    titel.className = "kategorie-titel";
    titel.textContent = kategorieName.toLowerCase();
    reiheDiv.appendChild(titel);

    const itemsContainer = document.createElement("div");
    itemsContainer.className = "items-container";

    items.forEach(item => {
        const box = document.createElement("div");
        box.className = "cloth-box";

        if(item.bildURL) {
            box.innerHTML = `<img src="${item.bildURL}" alt="${item.name}">`;
        } else {
            let icon = "👕";
            if(kategorieName.toLowerCase().includes("unterteil") || kategorieName.toLowerCase().includes("hose")) icon = "👖";
            if(kategorieName.toLowerCase().includes("schuhe")) icon = "👞";
            if(kategorieName.toLowerCase().includes("accessoire")) icon = "👜";
            box.textContent = icon;
        }
        itemsContainer.appendChild(box);
    });

    reiheDiv.appendChild(itemsContainer);
    parentContainer.appendChild(reiheDiv);
}

// WIRD GETRAGEN VERKNÜPFUNG (wear ! Button)
function wearOutfit() {
    if (aktuelleEmpfehlungsIds.length === 0) {
        alert("Kein Outfit zum Anziehen da!");
        return;
    }
    alert("Outfit getragen! Du siehst super aus heute!");
}