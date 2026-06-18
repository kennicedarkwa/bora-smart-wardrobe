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

    // ITEMS anzeigen
    kleidung.forEach(item => {
        const div = document.createElement("div");
        div.className = "outfit-item";

        // Bild vorhanden
        if (item.bildURL) {
            div.innerHTML = `
                <img src="${item.bildURL}" style="max-width:80px;">
                <p>${item.name}</p>
            `;
        } else {
            // Emoji fallback
            let icon = "👕";
            if (item.hauptkategorie?.includes("SCHUHE")) icon = "👞";
            if (item.hauptkategorie?.includes("HOSE") || item.hauptkategorie?.includes("UNTERTEIL")) icon = "👖";
            if (item.hauptkategorie?.includes("ACCESS")) icon = "👜";

            div.innerHTML = `
                <div style="font-size:40px">${icon}</div>
                <p>${item.name}</p>
            `;
        }

        liste.appendChild(div);
    });
}