// --- NEU: Dark Mode Check  ---
document.addEventListener("DOMContentLoaded", () => {
    if (localStorage.getItem("bora_darkmode") === "true") {
        document.body.classList.add("dark-mode");
    }
});

document.addEventListener("DOMContentLoaded", loadOutfit);

function loadOutfit() {
    const liste = document.getElementById("outfitListe");
    const infoBox = document.getElementById("infoBox");

    liste.innerHTML = "";

    // Daten aus localStorage holen
    const gespeichertesOutfit = localStorage.getItem("outfit");

    if (!gespeichertesOutfit) {
        infoBox.style.display = "block";
        infoBox.innerText = "Kein Outfit ausgewählt";
        return;
    }

    const kleidung = JSON.parse(gespeichertesOutfit);

    if (!kleidung || kleidung.length === 0) {
        infoBox.style.display = "block";
        infoBox.innerText = "Outfit ist leer";
        return;
    }

    infoBox.style.display = "none";

    // 🌟 AUTOMATISCHE SORTIERUNG VON KOPF BIS FUSS
    // Wir definieren die feste Reihenfolge für die Anzeige
    const reihenfolge = ["KOPFBEDECKUNG", "OBERTEIL", "GANZKOERPER", "UNTERTEIL", "SCHUHE", "ACCESSOIRES"];

    kleidung.sort((a, b) => {
        const katA = (a.hauptkategorie || "").toUpperCase();
        const katB = (b.hauptkategorie || "").toUpperCase();

        // Findet heraus, an welcher Stelle (0-5) die Kategorie in unserer Liste steht
        const indexA = reihenfolge.findIndex(k => katA.includes(k));
        const indexB = reihenfolge.findIndex(k => katB.includes(k));

        // Falls eine Kategorie unbekannt ist, kommt sie ganz nach unten (99)
        const posA = indexA === -1 ? 99 : indexA;
        const posB = indexB === -1 ? 99 : indexB;

        return posA - posB;
    });

    // ITEMS in der richtigen Reihenfolge anzeigen
    kleidung.forEach(item => {
        const div = document.createElement("div");
        div.className = "outfit-item";

        // Um den coolen Retro-Brutalismus Look beizubehalten, verpassen wir den Boxen Style direkt im JS:
        div.style.border = "4px solid #000";
        div.style.backgroundColor = "#fff";
        div.style.padding = "15px";
        div.style.marginBottom = "20px";
        div.style.width = "100%";
        div.style.maxWidth = "250px";
        div.style.textAlign = "center";
        div.style.boxShadow = "6px 6px 0px #000";
        div.style.display = "inline-block";

        // Bild-Pfad ermitteln (fängt beide Schreibweisen ab)
        const bildPfad = item.bildURL || item.bildurl;

        // Bild vorhanden
        if (bildPfad && bildPfad.trim() !== "") {
            div.innerHTML = `
                <img src="${bildPfad}" style="max-width:120px; max-height:120px; object-fit:contain; margin-bottom: 10px;">
                <p style="font-family: 'Courier New', monospace; font-weight: bold; margin: 0;">${item.name}</p>
            `;
        } else {
            // Emoji fallback
            let icon = "👕";
            const katLower = (item.hauptkategorie || "").toLowerCase();
            if (katLower.includes("schuhe")) icon = "👞";
            if (katLower.includes("hose") || katLower.includes("unterteil")) icon = "👖";
            if (katLower.includes("access")) icon = "👜";
            if (katLower.includes("kopf")) icon = "🧢";

            div.innerHTML = `
                <div style="font-size:50px; margin-bottom: 10px;">${icon}</div>
                <p style="font-family: 'Courier New', monospace; font-weight: bold; margin: 0;">${item.name}</p>
            `;
        }

        liste.appendChild(div);
    });
}

// ==========================================================================
// NEU: Outfit an das Backend senden, speichern und weiterleiten (Mit Namens-Prüfung)
// ==========================================================================
async function speichereAlsFavorit() {
    const nameInput = document.getElementById("favoritName");
    const outfitName = nameInput.value.trim();

    if (!outfitName) {
        alert("Bitte gib zuerst einen Namen für das Outfit ein!");
        return;
    }

    const gespeichertesOutfit = localStorage.getItem("outfit");
    if (!gespeichertesOutfit) {
        alert("Kein Outfit zum Speichern gefunden.");
        return;
    }

    try {
        // 🌟 SCHRITT 1: Bereits existierende Outfits abfragen, um Duplikate zu verhindern
        const checkResponse = await fetch("/api/outfits");
        if (checkResponse.ok) {
            const existierendeOutfits = await checkResponse.json();

            // Prüfen, ob der eingegebene Name (Groß-/Kleinschreibung ignoriert) schon vergeben ist
            const nameExistiert = existierendeOutfits.some(outfit =>
                outfit.name && outfit.name.trim().toLowerCase() === outfitName.toLowerCase()
            );

            if (nameExistiert) {
                alert(`Ein Outfit mit dem Namen "${outfitName}" existiert bereits. Bitte wähle einen anderen Namen!`);
                return; // Bricht das Speichern sofort ab!
            }
        }
    } catch (checkError) {
        console.warn("Vorab-Prüfung auf doppelte Namen übersprungen (Server antwortet nicht sauber):", checkError);
        // Falls die Liste leer ist oder der Endpunkt im Prototyp anders reagiert, erlauben wir den Versuch trotzdem
    }

    const kleidungArray = JSON.parse(gespeichertesOutfit);

    // Wir mappen die Kleidungsstücke so, wie es die "bestandteile"-Liste im Java-Backend erwartet
    const bestandteile = kleidungArray.map(item => {
        const id = item.kleidungsstueckID || item.id;
        return { id: id };
    });

    // Request-Body für den OutfitController zusammenstellen
    const neuesOutfit = {
        name: outfitName,
        anlass: null,
        bestandteile: bestandteile,
        tags: []
    };

    try {
        const response = await fetch("/api/outfits", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(neuesOutfit)
        });

        if (response.ok) {
            alert(`Das Outfit "${outfitName}" wurde erfolgreich gespeichert!`);
            nameInput.value = ""; // Feld wieder leeren

            // Weiterleitung zur Favoriten-Seite
            window.location.href = "/favoriten.html";

        } else {
            alert("Fehler beim Speichern des Outfits im System.");
        }
    } catch (error) {
        console.error("Fehler beim API-Call:", error);
        alert("Verbindung zum Server fehlgeschlagen.");
    }
}