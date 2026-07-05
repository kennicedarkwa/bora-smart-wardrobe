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
document.getElementById("bildEntfernenBtn").addEventListener("click", () => clearSelectedImage());

document.getElementById("neuBildFile").addEventListener("change", (event) => {
    const file = event.target.files[0];
    const label = document.getElementById("bildNameLabel");
    const btn = document.getElementById("wähleBildBtn");
    if (file) {
        if (!validiereBildDatei(file)) {
            clearSelectedImage();
            return;
        }

        label.innerText = file.name;
        btn.classList.add("selected");
        zeigeBildVorschau(file);
    } else {
        clearSelectedImage();
    }
});

let kleidung = [];
const uploadStates = new Map();
const uploadRequests = new Map();
let neuBildPreviewUrl = null;
const erlaubteBildEndungen = [".png", ".jpg", ".jpeg", ".webp", ".gif", ".bmp", ".tif", ".tiff", ".heic", ".heif"];
const bildAcceptWert = `image/*,${erlaubteBildEndungen.join(",")}`;
const maxBildGroesseBytes = 15 * 1024 * 1024;

document.getElementById("neuBildFile").setAttribute("accept", bildAcceptWert);

function istBildDateiErlaubt(file) {
    const name = file.name.toLowerCase();
    return erlaubteBildEndungen.some(endung => name.endsWith(endung));
}

function validiereBildDatei(file) {
    if (!istBildDateiErlaubt(file)) {
        alert("Dieses Bildformat wird nicht unterstützt. Erlaubt sind PNG, JPG, JPEG, WEBP, GIF, BMP, TIFF, HEIC und HEIF.");
        return false;
    }

    if (file.size > maxBildGroesseBytes) {
        alert("Das Bild ist zu groß. Bitte wähle ein Bild mit maximal 15MB.");
        return false;
    }

    return true;
}

function zeigeBildVorschau(file) {
    if (neuBildPreviewUrl) {
        URL.revokeObjectURL(neuBildPreviewUrl);
    }

    neuBildPreviewUrl = URL.createObjectURL(file);
    document.getElementById("bildPreview").src = neuBildPreviewUrl;
    document.getElementById("bildPreviewBox").classList.remove("hidden");
}

function clearSelectedImage(options = {}) {
    const shouldRevoke = options.revokePreview !== false;

    if (shouldRevoke && neuBildPreviewUrl) {
        URL.revokeObjectURL(neuBildPreviewUrl);
    }

    neuBildPreviewUrl = null;
    document.getElementById("neuBildFile").value = "";
    document.getElementById("bildNameLabel").innerText = "Kein Foto ausgewählt";
    document.getElementById("wähleBildBtn").classList.remove("selected");
    document.getElementById("bildPreview").removeAttribute("src");
    document.getElementById("bildPreviewBox").classList.add("hidden");
}

function aktualisiereAnsicht() {
    filter();
}

function aktualisiereDetailsWennOffen(id) {
    const detailsPopup = document.getElementById("popupDetails");
    if (!detailsPopup || detailsPopup.classList.contains("hidden")) return;

    const currentId = Number(detailsPopup.dataset.currentId);
    if (currentId === Number(id)) {
        details(id);
    }
}

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
        const uploadState = uploadStates.get(k.id);
        const statusText = k.istImWaeschekorb ? "im Wäschekorb" : "verfügbar";
        const waescheBtnText = k.istImWaeschekorb ? "Aus dem Korb" : "In den Wäschekorb";
        const bildPfad = uploadState?.previewUrl || k.bildURL || k.bildurl || "";
        const progressHtml = uploadState ? `
            <div class="upload-progress">
                <div class="upload-progress-bar">
                    <div class="upload-progress-fill ${uploadState.error ? 'error' : ''}" style="width: ${uploadState.progress}%"></div>
                </div>
                <div class="upload-progress-text">${uploadState.message} ${uploadState.progress}%</div>
            </div>
        ` : "";
        const imageHtml = bildPfad ? `
            <div class="image-stage">
                <img src="${bildPfad}" alt="${k.name}">
                ${progressHtml}
            </div>
        ` : "";
        container.innerHTML += `
            <div class="cloth-card ${k.istImWaeschekorb ? 'in-laundry' : ''}">
                ${imageHtml}
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

    const uploadState = uploadStates.get(k.id);
    const bildPfad = uploadState?.previewUrl || k.bildURL || k.bildurl;
    const detailsProgressHtml = uploadState ? `
        <div class="upload-progress details-progress">
            <div class="upload-progress-bar">
                <div class="upload-progress-fill ${uploadState.error ? 'error' : ''}" style="width: ${uploadState.progress}%"></div>
            </div>
            <div class="upload-progress-text">${uploadState.message} ${uploadState.progress}%</div>
        </div>
    ` : "";
    const bildHtml = bildPfad
        ? `
            <div class="details-image-stage">
                <img src="${bildPfad}" alt="${k.name}">
                ${detailsProgressHtml}
            </div>
        `
        : `<p style="font-style: italic; margin-bottom: 15px;">Kein Bild vorhanden</p>`;
    const bildLoeschenButton = bildPfad
        ? `<button type="button" class="details-delete-image-btn" onclick="bildInDetailsLoeschen(${k.id})">Bild löschen</button>`
        : "";

    document.getElementById("detailsContent").innerHTML = `
        <h3>${k.name}</h3>
        ${bildHtml}
        <div class="details-image-actions">
            <input type="file" id="detailsBildFile-${k.id}" accept="${bildAcceptWert}" style="display: none;" onchange="bildInDetailsAendern(event, ${k.id})">
            <button type="button" onclick="document.getElementById('detailsBildFile-${k.id}').click()">Bild ändern</button>
            ${bildLoeschenButton}
        </div>
        
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

    const popupDetails = document.getElementById("popupDetails");
    popupDetails.dataset.currentId = k.id;
    popupDetails.classList.remove("hidden");
}

function openPopupNeu() {
    document.getElementById("popupNeu").classList.remove("hidden");
}

function closePopup() {
    document.getElementById("popupNeu").classList.add("hidden");
    document.getElementById("popupDetails").classList.add("hidden");

    document.getElementById("neuName").value = "";
    clearSelectedImage();
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
    const previewUrl = file ? neuBildPreviewUrl : null;

    try {
        const response = await fetch("/api/kleidung", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(neuesKleidungsstueck)
        });

        if (!response.ok) throw new Error("Fehler beim Anlegen des Textes");
        const gespeichert = await response.json();

        kleidung = [gespeichert, ...kleidung.filter(item => item.id !== gespeichert.id)];

        if (file && gespeichert.id && previewUrl) {
            neuBildPreviewUrl = null;
            uploadStates.set(gespeichert.id, {
                previewUrl,
                progress: 1,
                message: "Bild wird vorbereitet"
            });
        }

        closePopup();
        aktualisiereAnsicht();

        if (file && gespeichert.id && previewUrl) {
            uploadImageMitFortschritt(gespeichert, file, previewUrl);
        }
    } catch (error) {
        console.error("Gescheitert:", error);
    }
}

function setUploadState(id, patch) {
    const current = uploadStates.get(id);
    if (!current) return;

    uploadStates.set(id, { ...current, ...patch });
    aktualisiereAnsicht();
    aktualisiereDetailsWennOffen(id);
}

function cleanupUploadState(id) {
    const state = uploadStates.get(id);
    if (state?.previewUrl) {
        URL.revokeObjectURL(state.previewUrl);
    }
    uploadStates.delete(id);
    uploadRequests.delete(id);
    aktualisiereAnsicht();
    aktualisiereDetailsWennOffen(id);
}

function brecheBildUploadAb(id) {
    const activeRequest = uploadRequests.get(id);
    if (activeRequest) {
        activeRequest.abort();
    }

    const state = uploadStates.get(id);
    if (state?.previewUrl) {
        URL.revokeObjectURL(state.previewUrl);
    }

    uploadStates.delete(id);
    uploadRequests.delete(id);
}

function uploadImageMitFortschritt(gespeichert, file, previewUrl) {
    const formData = new FormData();
    formData.append("file", file);

    const xhr = new XMLHttpRequest();
    let processingTimer = null;

    uploadRequests.set(gespeichert.id, xhr);

    xhr.upload.onprogress = (event) => {
        if (!event.lengthComputable) return;
        const uploadProgress = Math.round((event.loaded / event.total) * 80);
        setUploadState(gespeichert.id, {
            progress: Math.max(1, Math.min(uploadProgress, 80)),
            message: "Bild wird hochgeladen"
        });
    };

    xhr.upload.onload = () => {
        setUploadState(gespeichert.id, {
            progress: 82,
            message: "Python verarbeitet das Bild"
        });

        processingTimer = setInterval(() => {
            const state = uploadStates.get(gespeichert.id);
            if (!state || state.progress >= 95) return;
            setUploadState(gespeichert.id, {
                progress: state.progress + 1,
                message: "Python verarbeitet das Bild"
            });
        }, 700);
    };

    xhr.onload = async () => {
        if (processingTimer) clearInterval(processingTimer);
        uploadRequests.delete(gespeichert.id);

        try {
            if (xhr.status < 200 || xhr.status >= 300) {
                throw new Error("Bild konnte nicht verarbeitet werden");
            }

            const imgData = JSON.parse(xhr.responseText);
            gespeichert.bildURL = `/image/${imgData.id}`;

            setUploadState(gespeichert.id, {
                progress: 98,
                message: "Bild wird gespeichert"
            });

            const updateResponse = await fetch(`/api/kleidung/${gespeichert.id}`, {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(gespeichert)
            });

            if (!updateResponse.ok) {
                throw new Error("Bild-URL konnte nicht gespeichert werden");
            }

            kleidung = kleidung.map(item => item.id === gespeichert.id ? { ...item, bildURL: gespeichert.bildURL } : item);
            setUploadState(gespeichert.id, {
                progress: 100,
                message: "Bild ist fertig"
            });

            setTimeout(() => cleanupUploadState(gespeichert.id), 900);
        } catch (error) {
            console.error("Bildverarbeitung fehlgeschlagen:", error);
            setUploadState(gespeichert.id, {
                progress: 100,
                message: "Bild fehlgeschlagen",
                error: true
            });
        }
    };

    xhr.onerror = () => {
        if (processingTimer) clearInterval(processingTimer);
        uploadRequests.delete(gespeichert.id);
        setUploadState(gespeichert.id, {
            progress: 100,
            message: "Upload fehlgeschlagen",
            error: true
        });
    };

    xhr.onabort = () => {
        if (processingTimer) clearInterval(processingTimer);
        uploadRequests.delete(gespeichert.id);
    };

    xhr.open("POST", `/image/kleidungsstuecke/${gespeichert.id}/image/remove-background`);
    xhr.send(formData);
}

function bildInDetailsAendern(event, id) {
    const file = event.target.files[0];
    if (!file) return;

    if (!validiereBildDatei(file)) {
        event.target.value = "";
        return;
    }

    const item = kleidung.find(k => k.id === id);
    if (!item) return;

    brecheBildUploadAb(id);

    const previewUrl = URL.createObjectURL(file);
    uploadStates.set(id, {
        previewUrl,
        progress: 1,
        message: "Bild wird vorbereitet"
    });

    aktualisiereAnsicht();
    aktualisiereDetailsWennOffen(id);
    uploadImageMitFortschritt({ ...item }, file, previewUrl);
}

async function bildInDetailsLoeschen(id) {
    brecheBildUploadAb(id);

    const item = kleidung.find(k => k.id === id);
    if (!item) {
        aktualisiereAnsicht();
        return;
    }

    item.bildURL = "";
    item.bildurl = "";

    try {
        const response = await fetch(`/api/kleidung/${id}`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(item)
        });

        if (!response.ok) {
            throw new Error("Bild konnte nicht gelöscht werden");
        }

        kleidung = kleidung.map(k => k.id === id ? { ...k, bildURL: "", bildurl: "" } : k);
    } catch (error) {
        console.error("Bild konnte nicht gelöscht werden:", error);
    }

    aktualisiereAnsicht();
    aktualisiereDetailsWennOffen(id);
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
    brecheBildUploadAb(id);

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
