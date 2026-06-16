document.getElementById("ladeKleidungBtn").addEventListener("click", ladeKleidung);
document.getElementById("neuBtn").addEventListener("click", openPopupNeu);
document.getElementById("speichernNeu").addEventListener("click", speichernNeu);

document.getElementById("filterKategorie").addEventListener("change", filter);
document.getElementById("filterAnlass").addEventListener("change", filter);
document.getElementById("filterStatus").addEventListener("change", filter);
document.getElementById("filterWarm").addEventListener("change", filter);
document.getElementById("filterVerfuegbar").addEventListener("change", filter);
document.getElementById("filterName").addEventListener("input", filter);

// Trigger für das versteckte Datei-Input-Feld
document.getElementById("wähleBildBtn").addEventListener("click", () => {
    document.getElementById("neuBildFile").click();
});

// Dateinamen anzeigen, wenn ein Foto gewählt wurde
document.getElementById("neuBildFile").addEventListener("change", (event) => {
    const file = event.target.files[0];
    const label = document.getElementById("bildNameLabel");
    const btn = document.getElementById("wähleBildBtn");

    if (file) {
        label.innerText = file.name;
        btn.classList.add("selected");
    } else {
        label.innerText = "Kein Foto ausgewählt";
        btn.classList.remove("selected");
    }
});

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

        // Dynamischer Button-Text je nach Zustand
        const waescheBtnText = k.istImWaeschekorb ? "✨ Aus dem Korb" : "🧺 In den Wäschekorb";

        container.innerHTML += `
            <div class="card ${k.istImWaeschekorb ? 'in-laundry' : ''}">
                <img src="${k.bildURL || ""}" alt="${k.name}" style="max-height: 120px; object-fit: cover;">
                <h3>${k.name}</h3>
                <p>Kategorie: ${k.hauptkategorie}</p>
                <p>Anlass: ${tagsText}</p>
                <p>Wärmeindex: ${k.temperaturIndex}</p>
                <p>Status: ${statusText}</p>
                <p>Verfügbar: ${verfuegbar ? "Ja" : "Nein"}</p>

                <!-- Nur noch 3 aufgeräumte Buttons im Grid -->
                <div class="card-buttons">
                    <button onclick="details(${k.id})">Details</button>
                    <button onclick="toggleWaeschekorb(${k.id})">${waescheBtnText}</button>
                    <button onclick="loeschen(${k.id})" class="delete-btn">Löschen</button>
                </div>
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
    document.getElementById("neuName").value = "";
    document.getElementById("neuAnlass").value = "";
    document.getElementById("neuBildFile").value = "";
    document.getElementById("bildNameLabel").innerText = "Kein Foto ausgewählt";
    document.getElementById("wähleBildBtn").classList.remove("selected");

    document.getElementById("neuKategorie").value = "";
    document.getElementById("neuWarm").value = "";
    document.getElementById("neuRegen").value = "";

    document.querySelectorAll(".retro-btn-group button").forEach(b => b.classList.remove("active"));
    document.getElementById("popupNeu").classList.remove("hidden");
}

function closePopup() {
    document.getElementById("popupNeu").classList.add("hidden");
    document.getElementById("popupDetails").classList.add("hidden");
}

async function speichernNeu() {
    const name = document.getElementById("neuName").value;
    const kategorie = document.getElementById("neuKategorie").value;
    const anlass = document.getElementById("neuAnlass").value;
    const warmRaw = document.getElementById("neuWarm").value;
    const regenRaw = document.getElementById("neuRegen").value;

    const fileInput = document.getElementById("neuBildFile");
    const file = fileInput.files[0];

    if (!name || !kategorie || !anlass || warmRaw === "" || regenRaw === "") {
        alert("Bitte fülle alle Pflichtfelder und Buttons aus!");
        return;
    }

    const warm = parseInt(warmRaw, 10);
    const regen = (regenRaw === "true");

    const neues = {
        name: name,
        temperaturIndex: warm,
        istFuerRegenGeeignet: regen,
        istImWaeschekorb: false,
        hauptkategorie: kategorie,
        tags: [anlass],
        bildURL: ""
    };

    try {

        const response = await fetch("http://localhost:1531/api/kleidung", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(neues)
        });

        if (response.ok) {

            const gespeichert = await response.json();
            const kleidungsstueckId = gespeichert.id;

            if (file && kleidungsstueckId) {
                const formData = new FormData();
                formData.append("file", file);

                const imgResponse = await fetch(`http://localhost:1531/image/kleidungsstuecke/${kleidungsstueckId}/image/remove-background`, {
                    method: "POST",
                    body: formData
                });

                if (imgResponse.ok) {
                    const imgData = await imgResponse.json();

                    const bildPath = imgData.url || `/image/${imgData.id}`;

                    gespeichert.bildURL = "http://localhost:1531" + bildPath;

                    await fetch("http://localhost:1531/api/kleidung", {
                        method: "POST",
                        headers: { "Content-Type": "application/json" },
                        body: JSON.stringify(gespeichert)
                    });
                } else {
                    alert("Foto konnte von Ehsans Controller nicht verarbeitet werden.");
                }
            }
        } else {
            alert("Das Backend hat den Eintrag abgelehnt! Code: " + response.status);
        }
    } catch (error) {
        console.error("Fehler beim Senden:", error);
    }

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

function aktivierePopupButtons(groupId, hiddenInputId) {
    const group = document.getElementById(groupId);
    if(!group) return;
    const hiddenInput = document.getElementById(hiddenInputId);
    const buttons = group.querySelectorAll("button");

    buttons.forEach(button => {
        button.addEventListener("click", () => {
            buttons.forEach(b => b.classList.remove("active"));
            button.classList.add("active");
            hiddenInput.value = button.getAttribute("data-value");
        });
    });
}

// Alle Button-Reihen inklusive dem neuen Anlass scharf schalten
aktivierePopupButtons("groupKategorie", "neuKategorie");
aktivierePopupButtons("groupAnlass", "neuAnlass");
aktivierePopupButtons("groupWarm", "neuWarm");
aktivierePopupButtons("groupRegen", "neuRegen");

async function toggleWaeschekorb(id) {
    const k = kleidung.find(x => x.id === id);
    if (!k) return;

    // Zustand einfach umdrehen
    k.istImWaeschekorb = !k.istImWaeschekorb;

    try {
        // Schickt das aktualisierte Objekt an deine neue Java-Schnittstelle
        await fetch(`http://localhost:1531/api/kleidung/${id}`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(k)
        });
        ladeKleidung(); // Ansicht live neu laden!
    } catch (error) {
        console.error("Fehler beim Ändern des Wäschekorb-Status:", error);
    }
}