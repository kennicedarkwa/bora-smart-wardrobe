document.getElementById("ladeKleidungBtn").addEventListener("click", ladeKleidung);

function ladeKleidung() {
    const liste = document.getElementById("kleidungListe");

    // Platzhalter-Daten (Backend kommt später)
    const dummy = [
        {
            kleidungsID: 1,
            name: "Schwarzer Hoodie",
            kategorie: "Oberteil",
            tagsAnlass: ["casual"],
            warmIndex: 4,
            status: "verfügbar",
            einkaufsdatum: "2024-01-10",
            istVerfügbar: true,
            bildOhneHintergrund: "https://via.placeholder.com/200"
        },
        {
            kleidungsID: 2,
            name: "Blaue Jeans",
            kategorie: "Hose",
            tagsAnlass: ["casual"],
            warmIndex: 2,
            status: "inWäsche",
            einkaufsdatum: "2023-11-05",
            istVerfügbar: false,
            bildOhneHintergrund: "https://via.placeholder.com/200"
        }
    ];

    liste.innerHTML = "";

    dummy.forEach(k => {
        liste.innerHTML += `
            <div class="card">
                <img src="${k.bildOhneHintergrund}">
                <h3>${k.name}</h3>
                <p>Kategorie: ${k.kategorie}</p>
                <p>Anlass: ${k.tagsAnlass.join(", ")}</p>
                <p>Wärmeindex: ${k.warmIndex}</p>
                <p>Status: ${k.status}</p>
                <p>Verfügbar: ${k.istVerfügbar ? "Ja" : "Nein"}</p>
                <p>Einkaufsdatum: ${k.einkaufsdatum}</p>
                <button>Details</button>
                <button>Als benutzt markieren</button>
            </div>
        `;
    });
}
