// --- Dark Mode Check ---
document.addEventListener("DOMContentLoaded", () => {
    if (localStorage.getItem("bora_darkmode") === "true") {
        document.body.classList.add("dark-mode");
    }
});

// --- Event-Listener ---
document.getElementById("neuBtn").addEventListener("click", openPopupNeu);
document.getElementById("speichernNeu").addEventListener("click", speichernNeu);
document.getElementById("filterKategorie").addEventListener("change", filter);
document.getElementById("filterAnlass").addEventListener("change", filter);
document.getElementById("filterStatus").addEventListener("change", filter);
document.getElementById("filterWarm").addEventListener("change", filter);
document.getElementById("filterName").addEventListener("input", filter);

document.getElementById("wähleBildBtn").addEventListener("click", () => document.getElementById("neuBildFile").click());

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

// --- Daten laden ---
async function ladeKleidung() {
    try {
        const response = await fetch("/api/kleidung");
        kleidung = await response.json();
        renderListe(kleidung);
    } catch (error) {
        console.error("Fehler beim Laden:", error);
    }
}

// --- UI rendern ---
function renderListe(liste) {
    const container = document.getElementById("kleidungListe");
    container.innerHTML = "";

    if (!liste || liste.length === 0) {
        container.innerHTML = '<p class="placeholder">Keine Kleidungsstücke gefunden…</p>';
        return;
    }

    liste.forEach(k => {
        if (!k.id) return;
        const statusText = k.istImWaeschekorb ? "im Wäschekorb" : "verfügbar";
        const waescheBtnText = k.istImWaeschekorb ? "Aus dem Korb" : "In den Wäschekorb";
        const bildPfad = k.bildURL || k.bildurl || "";

        container.innerHTML += `
            <div class="cloth-card ${k.istImWaeschekorb ? 'in-laundry' : ''}">
                ${bildPfad ? `<img src="${bildPfad}" alt="${k.name}">` : ''}
                <div class="item-name">${k.name}</div>
                <div class="item-status">Status: ${statusText}</div>
                <div class="card-buttons">
                    <button onclick="details(${k.id})">Details</button>
                    <button onclick="toggleWaeschekorb(${k.id})">${waescheBtnText}</button>
                    <button onclick="loeschen(${k.id})" class="delete-btn">Löschen</button>
                </div>
            </div>
        `;
    });
}

// --- Filter ---
function filter() {
    let kategorie = document.getElementById("filterKategorie").value;
    let anlass = document.getElementById("filterAnlass").value;
    let status = document.getElementById("filterStatus").value;
    let warm = document.getElementById("filterWarm").value;
    let name = document.getElementById("filterName").value.toLowerCase();

    let gefiltert = kleidung.filter(k => {
        if (kategorie && k.hauptkategorie !== kategorie) return false;
        if (anlass && (!k.tags || !k.tags.includes(anlass))) return false;
        if (status === "verfuegbar" && k.istImWaeschekorb) return false;
        if (status === "waeschekorb" && !k.istImWaeschekorb) return false;
        if (warm && String(k.temperaturIndex) !== warm) return false;
        if (name && !k.name.toLowerCase().includes(name)) return false;
        return true;
    });
    renderListe(gefiltert);
}

// --- Details & Popups ---
function details(id) {
    const k = kleidung.find(x => x.id === id);
    if (!k) return;

    const statusText = k.istImWaeschekorb ? "🧺 Im Wäschekorb" : "✅ Verfügbar";
    const regenText = k.istFuerRegenGeeignet ? "☔ Ja" : "☀️ Nein";
    const anlassText = (k.tags && k.tags.length > 0) ? k.tags.join(', ') : "Kein Anlass";

    const bildPfad = k.bildURL || k.bildurl;
    const bildHtml = bildPfad
        ? `<img src="${bildPfad}" alt="${k.name}" style="width:100%; max-height: 250px; object-fit: contain; margin-bottom: 15px; border: 3px solid black; box-shadow: 4px 4px 0px #000;">`
        : `<p style="font-style: italic; margin-bottom: 15px;">Kein Bild vorhanden</p>`;

    document.getElementById("detailsContent").innerHTML = `
        <h3>${k.name}</h3>
        ${bildHtml}
        
        <div style="text-align: left; margin-bottom: 20px; line-height: 1.6; font-weight: bold;">
            <p><strong>Kategorie:</strong> ${k.hauptkategorie || "-"}</p>
            <p><strong>Anlass:</strong> ${anlassText}</p>
            <p><strong>Wärmeindex:</strong> ${k.temperaturIndex || "-"}</p>
            <p><strong>Regengeeignet:</strong> ${regenText}</p>
            <p><strong>Status:</strong> ${statusText}</p>
        </div>

        <div class="popup-actions">
            <button onclick="closePopup()">Schließen</button>
        </div>
    `;

    document.getElementById("popupDetails").classList.remove("hidden");
}

function openPopupNeu() {
    document.getElementById("popupNeu").classList.remove("hidden");
}

function closePopup() {
    document.getElementById("popupNeu").classList.add("hidden");
    document.getElementById("popupDetails").classList.add("hidden");

    document.getElementById("neuName").value = "";
    document.getElementById("neuBildFile").value = "";
    document.getElementById("bildNameLabel").innerText = "Kein Foto ausgewählt";
    document.getElementById("wähleBildBtn").classList.remove("selected");
    document.querySelectorAll(".active").forEach(btn => btn.classList.remove("active"));
    document.querySelectorAll("input[type='hidden']").forEach(input => input.value = "");
}

// --- Speichern ---
async function speichernNeu(event) {
    if (event) event.preventDefault();

    const neuesKleidungsstueck = {
        name: document.getElementById("neuName").value,
        hauptkategorie: document.getElementById("neuKategorie").value,
        tags: [document.getElementById("neuAnlass").value],
        temperaturIndex: parseInt(document.getElementById("neuWarm").value, 10),
        istFuerRegenGeeignet: (document.getElementById("neuRegen").value === "true"),
        istImWaeschekorb: false
    };

    const fileInput = document.getElementById("neuBildFile");
    const file = fileInput ? fileInput.files[0] : null;

    try {
        const response = await fetch("/api/kleidung", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(neuesKleidungsstueck)
        });

        if (!response.ok) throw new Error("Fehler beim Anlegen des Textes");
        const gespeichert = await response.json();

        if (file && gespeichert.id) {
            const imgFormData = new FormData();
            imgFormData.append("file", file);

            const imgResponse = await fetch(`/image/kleidungsstuecke/${gespeichert.id}/image/remove-background`, {
                method: "POST",
                body: imgFormData
            });

            if (imgResponse.ok) {
                const imgData = await imgResponse.json();
                gespeichert.bildURL = `/image/${imgData.id}`;

                await fetch(`/api/kleidung/${gespeichert.id}`, {
                    method: "PUT",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(gespeichert)
                });
            }
        }

        closePopup();
        ladeKleidung();
    } catch (error) {
        console.error("Gescheitert:", error);
    }
}

// --- Wäschekorb & Löschen ---
async function toggleWaeschekorb(id) {
    const k = kleidung.find(x => x.id === id);
    if (!k) return;
    k.istImWaeschekorb = !k.istImWaeschekorb;
    await fetch(`/api/kleidung/${id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(k)
    });
    ladeKleidung();
}

async function loeschen(id) {
    await fetch(`/api/kleidung/${id}`, { method: "DELETE" });
    ladeKleidung();
}

// --- Initialisierung ---
function aktivierePopupButtons(groupId, hiddenInputId) {
    const group = document.getElementById(groupId);
    if(!group) return;
    const hiddenInput = document.getElementById(hiddenInputId);
    group.querySelectorAll("button").forEach(button => {
        button.addEventListener("click", () => {
            group.querySelectorAll("button").forEach(b => b.classList.remove("active"));
            button.classList.add("active");
            hiddenInput.value = button.getAttribute("data-value");
        });
    });
}

async function logout() {
    const bestaetigung = confirm("Möchtest du dich wirklich ausloggen?");
    if (bestaetigung) {
        await fetch("/api/benutzer/logout", { method: "POST" });
        window.location.href = "/benutzer/login.html";
    }
}

aktivierePopupButtons("groupKategorie", "neuKategorie");
aktivierePopupButtons("groupAnlass", "neuAnlass");
aktivierePopupButtons("groupWarm", "neuWarm");
aktivierePopupButtons("groupRegen", "neuRegen");

ladeKleidung();