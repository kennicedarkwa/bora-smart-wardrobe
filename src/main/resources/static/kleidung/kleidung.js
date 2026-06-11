document.getElementById("ladeKleidungBtn").addEventListener("click", ladeKleidung);
document.getElementById("neuBtn").addEventListener("click", openPopupNeu);
document.getElementById("speichernNeu").addEventListener("click", speichernNeu);

document.getElementById("filterKategorie").addEventListener("change", filter);
document.getElementById("filterAnlass").addEventListener("change", filter);
document.getElementById("filterStatus").addEventListener("change", filter);
document.getElementById("filterWarm").addEventListener("change", filter);
document.getElementById("filterVerfuegbar").addEventListener("change", filter);
document.getElementById("filterName").addEventListener("input", filter);

let kleidung = [];

async function ladeKleidung() {
    const response = await fetch("http://localhost:1531/api/kleidung");
    kleidung = await response.json();
    renderListe(kleidung);
}

function renderListe(liste) {
    const container = document.getElementById("kleidungListe");
    container.innerHTML = "";

    if (!liste || liste.length === 0) {
        container.innerHTML = '<p class="placeholder">Keine Kleidungsstücke gefunden…</p>';
        return;
    }

    liste.forEach(k => {
        const verfuegbar = !k.istImWaeschekorb;
        const statusText = k.istImWaeschekorb ? "im Wäschekorb" : "verfügbar";
        const tagsText = k.tags ? k.tags.join(", ") : "";

        container.innerHTML += `
            <div class="card">
                <img src="${k.bildURL || ""}">
                <h3>${k.name}</h3>
                <p>Kategorie: ${k.hauptkategorie}</p>
                <p>Anlass: ${tagsText}</p>
                <p>Wärmeindex: ${k.temperaturIndex}</p>
                <p>Status: ${statusText}</p>
                <p>Verfügbar: ${verfuegbar ? "Ja" : "Nein"}</p>

                <button onclick="details(${k.id})">Details</button>
                <button onclick="alsBenutzt(${k.id})">Als benutzt</button>
                <button onclick="setzeVerfuegbarkeit(${k.id}, true)">Verfügbar</button>
                <button onclick="setzeVerfuegbarkeit(${k.id}, false)">Nicht verfügbar</button>
                <button onclick="loeschen(${k.id})">Löschen</button>
            </div>
        `;
    });
}

function filter() {
    let kategorie = document.getElementById("filterKategorie").value;
    let anlass = document.getElementById("filterAnlass").value;
    let status = document.getElementById("filterStatus").value;
    let warm = document.getElementById("filterWarm").value;
    let verf = document.getElementById("filterVerfuegbar").value;
    let name = document.getElementById("filterName").value.toLowerCase();

    let gefiltert = kleidung.filter(k => {
        const verfuegbar = !k.istImWaeschekorb;
        if (kategorie && k.hauptkategorie !== kategorie) return false;
        if (anlass && (!k.tags || !k.tags.includes(anlass))) return false;
        if (status === "verfuegbar" && k.istImWaeschekorb) return false;
        if (status === "waeschekorb" && !k.istImWaeschekorb) return false;
        if (warm && String(k.temperaturIndex) !== warm) return false;
        if (verf && String(verfuegbar) !== verf) return false;
        if (name && !k.name.toLowerCase().includes(name)) return false;
        return true;
    });

    renderListe(gefiltert);
}

function openPopupNeu() {
    document.getElementById("popupNeu").classList.remove("hidden");
}

function closePopup() {
    document.getElementById("popupNeu").classList.add("hidden");
    document.getElementById("popupDetails").classList.add("hidden");
}

async function speichernNeu() {
    const name = document.getElementById("neuName").value;
    const kategorie = document.getElementById("neuKategorie").value;
    const anlassRoh = document.getElementById("neuAnlass").value;
    const warm = parseInt(document.getElementById("neuWarm").value, 10);
    const regen = document.getElementById("neuRegen").value === "true";
    const bild = document.getElementById("neuBild").value;

    const tags = anlassRoh
        .split(",")
        .map(t => t.trim())
        .filter(t => t.length > 0);

    const neues = {
        name: name,
        temperaturIndex: warm,
        istFuerRegenGeeignet: regen,
        istImWaeschekorb: false,
        hauptkategorie: kategorie,
        tags: tags,
        bildURL: bild
    };

    await fetch("http://localhost:1531/api/kleidung", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(neues)
    });

    closePopup();
    ladeKleidung();
}

function details(id) {
    const k = kleidung.find(x => x.id === id);
    if (!k) return;

    const verfuegbar = !k.istImWaeschekorb;
    const statusText = k.istImWaeschekorb ? "im Wäschekorb" : "verfügbar";
    const tagsText = k.tags ? k.tags.join(", ") : "";

    document.getElementById("detailsContent").innerHTML = `
        <h3>${k.name}</h3>
        <img src="${k.bildURL || ""}" style="width:100%;border:2px solid black;">
        <p>Kategorie: ${k.hauptkategorie}</p>
        <p>Anlass: ${tagsText}</p>
        <p>Wärmeindex: ${k.temperaturIndex}</p>
        <p>Status: ${statusText}</p>
        <p>Verfügbar: ${verfuegbar ? "Ja" : "Nein"}</p>
        <button onclick="closePopup()">Schließen</button>
    `;

    document.getElementById("popupDetails").classList.remove("hidden");
}

async function alsBenutzt(id) {
    const k = kleidung.find(x => x.id === id);
    if (!k) return;

    const aktualisiert = { ...k, istImWaeschekorb: true };

    await fetch(`http://localhost:1531/api/kleidung/${id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(aktualisiert)
    });

    ladeKleidung();
}

async function setzeVerfuegbarkeit(id, wert) {
    const k = kleidung.find(x => x.id === id);
    if (!k) return;

    const aktualisiert = { ...k, istImWaeschekorb: !wert };

    await fetch(`http://localhost:1531/api/kleidung/${id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(aktualisiert)
    });

    ladeKleidung();
}

async function loeschen(id) {
    await fetch(`http://localhost:1531/api/kleidung/${id}`, {
        method: "DELETE"
    });
    ladeKleidung();
}
