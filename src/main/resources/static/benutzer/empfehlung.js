async function holeEmpfehlung() {
    const tempIndex = document.getElementById("tempIndex").value;
    const isRaining = document.getElementById("regen").value === "true";
    const errorElement = document.getElementById("error");
    const ergebnisContainer = document.getElementById("ergebnisContainer");
    const outfitListe = document.getElementById("outfitListe");

    errorElement.textContent = "";
    ergebnisContainer.style.display = "none";
    outfitListe.innerHTML = "";

    if (!tempIndex || tempIndex < 1 || tempIndex > 5) {
        errorElement.textContent = "Bitte einen gültigen Temperatur-Index (1-5) eingeben.";
        return;
    }

    const forecastData = {
        temperature: {
            temperatureIndex: parseInt(tempIndex)
        },
        precipitation: {
            likely: isRaining
        }
    };

    try {
        const response = await fetch("/api/empfehlung", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(forecastData)
        });

        if (response.ok) {
            const empfehlung = await response.json();
            zeigeErgebnis(empfehlung);
        } else {
            errorElement.textContent = "Empfehlung konnte nicht geladen werden.";
        }
    } catch (e) {
        errorElement.textContent = "Serverfehler.";
    }
}

function zeigeErgebnis(empfehlung) {
    const outfitListe = document.getElementById("outfitListe");
    const ergebnisContainer = document.getElementById("ergebnisContainer");

    if (empfehlung.kleidungsstuecke && empfehlung.kleidungsstuecke.length > 0) {
        empfehlung.kleidungsstuecke.forEach(klamotte => {
            const div = document.createElement("div");
            div.className = "kleidungsstueck";

            const name = klamotte.name ? klamotte.name : "Kleidungsstück";
            const kategorie = klamotte.kategorie ? klamotte.kategorie : "Unbekannt";

            div.innerHTML = `<strong>${name}</strong><br>
                             <small style="color: #666;">Kategorie: ${kategorie}</small>`;
            outfitListe.appendChild(div);
        });
    } else {
        outfitListe.innerHTML = "<p style='text-align: center; font-size: 14px; color: #666;'>Keine passenden Teile im Schrank gefunden.</p>";
    }

    ergebnisContainer.style.display = "block";
}