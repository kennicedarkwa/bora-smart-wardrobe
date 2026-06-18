// --- NEU: Dark Mode Check  ---
document.addEventListener("DOMContentLoaded", () => {
    if (localStorage.getItem("bora_darkmode") === "true") {
        document.body.classList.add("dark-mode");
    }
});

// 1. PARAMETER AUS DER URL AUSLESEN
const urlParams = new URLSearchParams(window.location.search);
const gewaehlterAnlass = urlParams.get("occasion") || "CASUAL";
const gewaehlteStadt = urlParams.get("city") || "Hamburg";
const gewaehltesDatum = urlParams.get("date") || "2026-05-05";

let ausgewaehltesOutfit = {};

document.addEventListener("DOMContentLoaded", () => {
    const titelElement = document.getElementById("anlassTitel");
    if (titelElement) {
        titelElement.textContent = gewaehlterAnlass.toLowerCase();
    }
    ladeUndFiltereKleidung();
});

async function ladeUndFiltereKleidung() {
    const container = document.getElementById("kategorienContainer");

    try {
        const weatherResponse = await fetch(`/api/weather/${encodeURIComponent(gewaehlteStadt)}/${encodeURIComponent(gewaehltesDatum)}`);
        if (!weatherResponse.ok) throw new Error("Wetter-API nicht erreichbar");
        const aktuellesWetter = await weatherResponse.json();

        const minTemp = Math.round(aktuellesWetter.temperature?.min_celsius ?? 15);
        const maxTemp = Math.round(aktuellesWetter.temperature?.max_celsius ?? 22);

        const backendForecast = {
            date: gewaehltesDatum,
            uv_index: Math.round(aktuellesWetter.uv_index ?? 0),
            temperature: {
                min_celsius: minTemp, // 👈 Das braucht Java für die Berechnung!
                max_celsius: maxTemp  // 👈 Das braucht Java für die Berechnung!
            },
            precipitation: {
                type: aktuellesWetter.precipitation?.type || "NONE",
                probability_percent: Math.round(aktuellesWetter.precipitation?.probability_percent || 0)
            }
        };

        // Falls dein Backend "isLikely()" prüft, fügen wir es zur Sicherheit im JS-Forecast hinzu
        backendForecast.precipitation.likely = backendForecast.precipitation.probability_percent > 50;

        const url = `/api/empfehlung?anlass=${encodeURIComponent(gewaehlterAnlass)}`;

        const response = await fetch(url, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(backendForecast)
        });

        if (!response.ok) throw new Error("Backend-Empfehlung fehlgeschlagen");
        const empfehlungData = await response.json();

        container.innerHTML = "";

        // Greift direkt auf die neu bereitgestellte Java-Transient-Map zu
        let optionen = empfehlungData.optionenNachKategorie;

        // Robuster Fallback, falls die Map leer ist, aber flache Stücke existieren
        if ((!optionen || Object.keys(optionen).length === 0) && empfehlungData.kleidungsstuecke) {
            optionen = {};
            empfehlungData.kleidungsstuecke.forEach(item => {
                // Nutzt dein exaktes Java-Feld "hauptkategorie"
                const kat = item.hauptkategorie || "SONSTIGES";
                if (!optionen[kat]) {
                    optionen[kat] = [];
                }
                optionen[kat].push(item);
            });
        }

        if (!optionen || Object.keys(optionen).length === 0) {
            container.innerHTML = `<p style='text-align: center; font-weight:bold; margin-top: 20px;'>Keine passenden Stücke für <b>${gewaehlterAnlass}</b> mit Temperatur-Index im Schrank.</p>`;
            return;
        }

        // --- NEU: Feste Reihenfolge für die Kategorien auf der Seite ---
        const wunschReihenfolge = ["KOPFBEDECKUNG", "OBERTEIL", "GANZKOERPER", "UNTERTEIL", "SCHUHE", "ACCESSOIRES"];

        // Kategorien sortieren
        const sortierteKategorien = Object.keys(optionen).sort((a, b) => {
            const posA = wunschReihenfolge.indexOf(a.toUpperCase());
            const posB = wunschReihenfolge.indexOf(b.toUpperCase());
            return (posA === -1 ? 99 : posA) - (posB === -1 ? 99 : posB);
        });

        // Zeilen in der exakt richtigen Reihenfolge zeichnen
        for (const kategorieName of sortierteKategorien) {
            const items = optionen[kategorieName];
            if (items && items.length > 0) {
                zeichneKategorieReihe(kategorieName, items, container);
            }
        }

    } catch (error) {
        console.error("Fehler in der JS-Verarbeitung:", error);
        container.innerHTML = "<p style='text-align: center; color: red; margin-top: 20px;'>Fehler beim Laden oder Verarbeiten der Empfehlungen.</p>";
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
        const box = document.createElement("button");
        box.type = "button";
        box.className = "cloth-box";

        // Falls dieses Item bereits im Speicher ist, direkt als selektiert markieren
        if (ausgewaehltesOutfit[kategorieName] && ausgewaehltesOutfit[kategorieName].id === item.id) {
            box.classList.add("selected");
        }

        // 1. BILD-CONTAINER (Holt Pfade wie /uploads/... direkt aus deinem KleidungsstueckService)
        const imgContainer = document.createElement("div");
        imgContainer.className = "bild-container";

        // Fängt beide Schreibweisen (Java DTO-Feld 'bildURL' und H2-Spalte 'bildurl') ab
        const bildPfad = item.bildURL || item.bildurl;

        if (bildPfad && bildPfad.trim() !== "") {
            imgContainer.innerHTML = `<img src="${bildPfad}" alt="${item.name}" class="kleidung-bild">`;
        } else {
            let icon = "👕";
            const katLower = kategorieName.toLowerCase();
            if (katLower.includes("unterteil") || katLower.includes("hose")) icon = "👖";
            if (katLower.includes("schuhe")) icon = "👞";
            if (katLower.includes("accessoire")) icon = "👜";
            imgContainer.textContent = icon;
            imgContainer.style.fontSize = "32px";
        }
        box.appendChild(imgContainer);

        // 2. NAME
        const nameSpan = document.createElement("span");
        nameSpan.className = "kleidung-name";
        nameSpan.textContent = item.name || "Unbekanntes Teil";
        box.appendChild(nameSpan);

        // 3. KLICK-LOGIK MIT TOGGLE (UNSELECT)
        box.addEventListener("click", () => {
            const istBereitsSelektiert = box.classList.contains("selected");

            // Erstmal alle in dieser Reihe visuell zurücksetzen
            itemsContainer.querySelectorAll(".cloth-box").forEach(b => b.classList.remove("selected"));

            if (istBereitsSelektiert) {
                // Wenn es schon ausgewählt war -> ABWÄHLEN (Aus dem Speicher löschen)
                delete ausgewaehltesOutfit[kategorieName];
            } else {
                // Wenn es neu angeklickt wurde -> AUSWÄHLEN
                box.classList.add("selected");
                ausgewaehltesOutfit[kategorieName] = item;
            }

            console.log("Aktuelles Outfit:", ausgewaehltesOutfit);
        });

        itemsContainer.appendChild(box);
    });

    reiheDiv.appendChild(itemsContainer);
    parentContainer.appendChild(reiheDiv);
}

// ==========================================================
// OUTFIT SPEICHERN (ALS ARRAY FÜR OUTFIT.JS)
// ==========================================================

// Stelle sicher, dass der Button im HTML der empfehlung.html die id="btnTragen" hat!
const tragenButton = document.getElementById("btnTragen");

if (tragenButton) {
    tragenButton.addEventListener("click", () => {

        if (Object.keys(ausgewaehltesOutfit).length === 0) {
            alert("Bitte wähle zuerst mindestens ein Kleidungsstück aus!");
            return;
        }

        // Wir wandeln das Objekt in ein echtes Array um,
        // da deine outfit.js ein Array erwartet!
        const outfitArray = Object.values(ausgewaehltesOutfit);

        // Wir speichern es unter dem Namen "outfit" ab
        localStorage.setItem("outfit", JSON.stringify(outfitArray));

        // Weiterleitung zur Outfit-Seite
        window.location.href = "../outfit/outfit.html";
    });
}
