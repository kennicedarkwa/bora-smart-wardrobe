// Wir tun für diese Seite so, als hätte der Nutzer "CASUAL" gewählt
const gewaehlterAnlass = "CASUAL";

document.addEventListener("DOMContentLoaded", async () => {
    document.getElementById("anlassTitel").textContent = gewaehlterAnlass.toLowerCase();
    await ladeUndFiltereKleidung();
});

async function ladeUndFiltereKleidung() {
    const container = document.getElementById("kategorienContainer");

    try {
        // 1. NEU: Wetter direkt von DEINEM Java-Backend laden (WeatherController)
        // Wir nehmen hier "Hamburg" und ein Datum aus deinen Mock-Daten
        const stadt = "Hamburg";
        const datum = "2026-05-03";

        const weatherResponse = await fetch(`/api/weather/${stadt}/${datum}`);
        if (!weatherResponse.ok) throw new Error("Wetter-API nicht erreichbar");

        const aktuellesWetter = await weatherResponse.json();
        const tempCelsius = aktuellesWetter.temperature.max_celsius;

        // 2. Temperatur in den Index (1-5) umwandeln
        let tempIndex = 3;
        if (tempCelsius < 10) tempIndex = 1;
        else if (tempCelsius <= 14) tempIndex = 2;
        else if (tempCelsius <= 19) tempIndex = 3;
        else if (tempCelsius <= 24) tempIndex = 4;
        else tempIndex = 5;

        // "none" bedeutet kein Regen. Alles andere (rain, drizzle, etc.) wird als Regen gewertet
        const isRaining = aktuellesWetter.precipitation.type !== "none" && aktuellesWetter.precipitation.probability_percent > 20;

        // 3. Payload für dein Backend bauen (DailyForecast Klasse)
        const backendForecast = {
            temperature: { temperatureIndex: tempIndex },
            precipitation: { likely: isRaining }
        };

        // 4. Empfehlung vom Backend abrufen (EmpfehlungController)
        const response = await fetch("/api/empfehlung", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(backendForecast)
        });

        const empfehlungData = await response.json();
        container.innerHTML = "";

        if (!empfehlungData.kleidungsstuecke || empfehlungData.kleidungsstuecke.length === 0) {
            container.innerHTML = "<p style='text-align: center; color: #1e0033;'>Keine passenden Stücke im Schrank gefunden.</p>";
            erstelleTestDesign(container); // Für das Wireframe-Design beim Testen
            return;
        }

        // 5. NEU: VERKNÜPFUNG MIT DER KLASSE "Kleidungsstueck"
        const gruppierteKleidung = {};

        empfehlungData.kleidungsstuecke.forEach(item => {
            // HIER nutzen wir EXAKT die Variable 'hauptkategorie' aus deinem Java-Code!
            const kategorie = item.hauptkategorie ? item.hauptkategorie : "SONSTIGES";

            if (!gruppierteKleidung[kategorie]) {
                gruppierteKleidung[kategorie] = [];
            }
            gruppierteKleidung[kategorie].push(item);
        });

        // 6. Kategorien im HTML zeichnen
        for (const [kategorieName, items] of Object.entries(gruppierteKleidung)) {
            zeichneKategorieReihe(kategorieName, items, container);
        }

    } catch (error) {
        console.error("Fehler bei der Verbindung zum Backend:", error);
        container.innerHTML = "<p style='text-align: center; color: red;'>Fehler: Backend antwortet nicht.</p>";
        erstelleTestDesign(container);
    }
}

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

        // HIER: Nutzen wir exakt 'bildURL' aus deiner Java-Klasse
        if(item.bildURL) {
            // Bild anzeigen, wenn eines in der Datenbank liegt
            box.innerHTML = `<img src="${item.bildURL}" alt="${item.name}">`;
        } else {
            // Platzhalter-Emoji, wenn das Bildfeld leer ist
            let icon = "👕";
            if(kategorieName.toLowerCase().includes("hose")) icon = "👖";
            if(kategorieName.toLowerCase().includes("schuhe")) icon = "👞";
            if(kategorieName.toLowerCase().includes("accessoire")) icon = "👜";
            box.textContent = icon;
        }
        itemsContainer.appendChild(box);
    });

    reiheDiv.appendChild(itemsContainer);
    parentContainer.appendChild(reiheDiv);
}

function erstelleTestDesign(container) {
    zeichneKategorieReihe("OBERTEIL", [{hauptkategorie: "OBERTEIL"}, {hauptkategorie: "OBERTEIL"}], container);
    zeichneKategorieReihe("ACCESSOIRE", [{hauptkategorie: "ACCESSOIRE"}, {hauptkategorie: "ACCESSOIRE"}], container);
}