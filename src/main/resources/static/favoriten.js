document.addEventListener("DOMContentLoaded", () => {
    // Dark Mode Check
    if (localStorage.getItem("bora_darkmode") === "true") {
        document.body.classList.add("dark-mode");
    }

    ladeFavoriten();
});

async function ladeFavoriten() {
    const liste = document.getElementById("favoritenListe");

    try {
        // Holt alle Outfits aus deinem Spring Boot Backend
        const response = await fetch("/api/outfits");

        if (!response.ok) {
            liste.innerHTML = "<p class='error'>Fehler beim Laden der Favoriten.</p>";
            return;
        }

        const outfits = await response.json();

        if (outfits.length === 0) {
            liste.innerHTML = "<div class='infoBox'>Du hast noch keine Outfits gespeichert.</div>";
            return;
        }

        // Liste leeren und neu aufbauen
        liste.innerHTML = "";

        // Jedes Outfit durchgehen und eine Karte erstellen
        outfits.forEach(outfit => {
            const outfitCard = document.createElement("div");
            outfitCard.className = "outfit-card";

            // Titel des Outfits (den der User vergeben hat)
            let htmlInhalt = `<h2>★ ${outfit.name || "Ohne Namen"}</h2>`;
            htmlInhalt += `<div class="outfit-items-grid">`;

            // Alle Kleidungsstücke in diesem Outfit durchgehen
            if (outfit.bestandteile && outfit.bestandteile.length > 0) {
                outfit.bestandteile.forEach(item => {
                    const bildPfad = item.bildURL || item.bildurl;

                    htmlInhalt += `<div class="mini-item">`;
                    if (bildPfad) {
                        htmlInhalt += `<img src="${bildPfad}" style="max-width:100%; max-height:80px; object-fit:contain;">`;
                    } else {
                        htmlInhalt += `<div style="font-size:30px;">👕</div>`;
                    }
                    htmlInhalt += `<p style="font-size: 10px; margin: 5px 0 0 0; font-weight: bold;">${item.name || "Kleidung"}</p>`;
                    htmlInhalt += `</div>`;
                });
            } else {
                htmlInhalt += `<p>Dieses Outfit ist leer.</p>`;
            }

            htmlInhalt += `</div>`; // Ende Grid

            // Löschen-Button für später (optional)
            htmlInhalt += `
                <button onclick="loescheOutfit(${outfit.outfitID})" style="margin-top: 15px; font-family: 'Courier New', monospace; font-weight: bold; padding: 5px 10px; background-color: #e1406b; color: white; border: 2px solid #000; box-shadow: 2px 2px 0px #000; cursor: pointer;">
                    🗑️ Outfit löschen
                </button>
            `;

            outfitCard.innerHTML = htmlInhalt;
            liste.appendChild(outfitCard);
        });

    } catch (error) {
        console.error("Netzwerkfehler:", error);
        liste.innerHTML = "<p class='error'>Verbindung zum Server fehlgeschlagen.</p>";
    }
}

// Funktion zum Löschen (greift auf deine bestehende Delete-Route im Controller zu)
async function loescheOutfit(id) {
    if(!confirm("Willst du dieses Outfit wirklich löschen?")) return;

    try {
        const response = await fetch(`/api/outfits/${id}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            ladeFavoriten(); // Liste nach dem Löschen neu laden
        } else {
            alert("Fehler beim Löschen.");
        }
    } catch (error) {
        console.error("Fehler:", error);
    }
}