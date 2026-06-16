// Lädt die Daten beim Öffnen der Seite
async function loadProfil() {
    const response = await fetch("/api/benutzer/aktueller");

    if (!response.ok) {
        window.location.href = "login.html";
        return;
    }

    const benutzer = await response.json();

    // Falls ein benutzername da ist (z.B. "Martin Schultz"), teilen wir ihn auf
    if (benutzer.benutzername) {
        const nameTeile = benutzer.benutzername.split(" ");

        // Der erste Teil wird Vorname, der Rest wird als Nachname zusammengefügt
        document.getElementById("vorname").value = nameTeile[0] || "";
        document.getElementById("nachname").value = nameTeile.slice(1).join(" ") || "";
    } else {
        document.getElementById("vorname").value = "";
        document.getElementById("nachname").value = "";
    }

    document.getElementById("email").value = benutzer.email || "";
}

// Profildaten speichern
async function profilSpeichern() {
    const vorname = document.getElementById("vorname").value.trim();
    const nachname = document.getElementById("nachname").value.trim();
    const email = document.getElementById("email").value.trim();

    // Wir bauen Vorname und Nachname zu EINEM Benutzernamen für das Backend zusammen
    const vollerBenutzername = `${vorname} ${nachname}`.trim();

    const response = await fetch("/api/benutzer/update", {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ benutzername: vollerBenutzername, email: email })
    });

    if(response.ok) {
        alert("Profildaten erfolgreich aktualisiert!");
    } else {
        alert("Fehler beim Speichern der Daten.");
    }
}

// Passwort ändern
async function passwortAendern() {
    const neu = document.getElementById("neuPasswort").value;

    if (!neu) {
        alert("Bitte gib ein neues Passwort ein.");
        return;
    }

    const response = await fetch("/api/benutzer/passwort", {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ neuesPasswort: neu })
    });

    if(response.ok) {
        alert("Passwort erfolgreich geändert!");
        document.getElementById("neuPasswort").value = "";
    } else {
        alert("Fehler beim Ändern des Passworts.");
    }
}

// Account löschen
async function accountLoeschen() {
    if (!confirm("Willst du deinen Account wirklich löschen? Dieser Schritt kann nicht rückgängig gemacht werden!")) return;

    const response = await fetch("/api/benutzer/loeschen", {
        method: "DELETE"
    });

    if(response.ok) {
        alert("Account erfolgreich gelöscht.");
        window.location.href = "login.html";
    } else {
        alert("Fehler beim Löschen des Accounts.");
    }
}

// Logout
async function logout() {
    await fetch("/api/benutzer/logout", { method: "POST" });
    window.location.href = "login.html";
}

// Einstellungen öffnen
function openSettings() {
    window.location.href = "../einstellungen.html";
}

// Beim Laden ausführen
loadProfil();