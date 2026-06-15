const benutzerId = localStorage.getItem("aktueller_benutzer_id") || 1;
const apiBaseUrl = `/api/einstellungen/${benutzerId}`;

document.addEventListener("DOMContentLoaded", () => {
    ladeEinstellungen();

    // Event Listener
    document.getElementById("settingsForm").addEventListener("submit", speichereEinstellungen);
    document.getElementById("btnReset").addEventListener("click", setzeStandardwerte);

    // --- NEU: Dark Mode Live-Wechsel ---
    const darkModeToggle = document.getElementById("darkModeToggle");

    // Beim Laden prüfen, ob Dark Mode an war
    if (localStorage.getItem("bora_darkmode") === "true") {
        document.body.classList.add("dark-mode");
        darkModeToggle.checked = true;
    }

    // Wenn der Schalter geklickt wird
    darkModeToggle.addEventListener("change", (e) => {
        if (e.target.checked) {
            document.body.classList.add("dark-mode");
            localStorage.setItem("bora_darkmode", "true");
        } else {
            document.body.classList.remove("dark-mode");
            localStorage.setItem("bora_darkmode", "false");
        }
    });

    // --- NEU: Konto löschen ---
    document.getElementById("btnDeleteData").addEventListener("click", () => {
        const sicher = confirm("🚨 Bist du sicher? Alle deine Outfits, Einstellungen und gespeicherten Daten werden unwiderruflich gelöscht!");

        if (sicher) {
            // Löscht alle lokalen Daten
            localStorage.clear();
            alert("Dein Konto wurde erfolgreich gelöscht. Du wirst ausgeloggt.");
            // Leitet zurück zur Startseite (Pfad ggf. anpassen!)
            window.location.href = "/homepage/index.html";
        }
    });
});

async function ladeEinstellungen() {
    try {
        const response = await fetch(apiBaseUrl);
        if (!response.ok) throw new Error("Fehler beim Laden");

        const daten = await response.json();
        if (daten) {
            document.getElementById("standardStandort").value = daten.standardStandort || "";
            document.getElementById("waescheErinnerungAktiv").checked = daten.waescheErinnerungAktiv;
        }

        // --- NEU: Wäsche-Limit laden ---
        document.getElementById("waescheLimit").value = localStorage.getItem("bora_waeschelimit") || 5;

    } catch (error) {
        showStatus("⚠️ Einstellungen konnten nicht geladen werden.", "error");
    }
}

async function speichereEinstellungen(event) {
    event.preventDefault();

    // --- NEU: Wäsche-Limit speichern ---
    const limit = document.getElementById("waescheLimit").value;
    localStorage.setItem("bora_waeschelimit", limit);

    // Daten für das Java-Backend
    const geaenderteEinstellungen = {
        standardStandort: document.getElementById("standardStandort").value,
        waescheErinnerungAktiv: document.getElementById("waescheErinnerungAktiv").checked
    };

    try {
        const response = await fetch(apiBaseUrl, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(geaenderteEinstellungen)
        });

        if (response.ok) {
            showStatus("✅ Einstellungen erfolgreich gespeichert!", "success");

            if (geaenderteEinstellungen.waescheErinnerungAktiv) {
                await fetch(`${apiBaseUrl}/erinnerung/aktivieren`, { method: "POST" });
            } else {
                await fetch(`${apiBaseUrl}/erinnerung/deaktivieren`, { method: "POST" });
            }
        } else {
            showStatus("❌ Fehler beim Speichern.", "error");
        }
    } catch (error) {
        showStatus("⚠️ Server-Fehler.", "error");
    }
}

async function setzeStandardwerte() {
    if (!confirm("Alle Einstellungen auf Standard zurücksetzen?")) return;

    try {
        const response = await fetch(`${apiBaseUrl}/standardwerte`, { method: "POST" });
        if (response.ok) {
            // Auch Frontend-Sachen zurücksetzen
            localStorage.setItem("bora_waeschelimit", 5);
            localStorage.setItem("bora_darkmode", "false");
            document.body.classList.remove("dark-mode");

            showStatus("🔄 Standardwerte wiederhergestellt!", "success");
            ladeEinstellungen();
        }
    } catch (error) {
        showStatus("⚠️ Server-Fehler.", "error");
    }
}

function showStatus(text, type) {
    const msgBox = document.getElementById("statusMessage");
    msgBox.textContent = text;
    msgBox.className = `status-message status-${type}`;
    msgBox.style.display = "block";
    setTimeout(() => { msgBox.style.display = "none"; }, 4000);
}