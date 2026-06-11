async function loadProfil() {
    const response = await fetch("/api/benutzer/aktueller");

    if (!response.ok) {
        window.location.href = "login.html";
        return;
    }

    const benutzer = await response.json();

    document.getElementById("vorname").textContent = benutzer.vorname;
    document.getElementById("nachname").textContent = benutzer.nachname;
    document.getElementById("email").textContent = benutzer.email;
}

async function passwortAendern() {
    const neu = document.getElementById("neuPasswort").value;

    await fetch("/api/benutzer/passwort", {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ neuesPasswort: neu })
    });

    alert("Passwort geändert!");
}

async function accountLoeschen() {
    if (!confirm("Willst du deinen Account wirklich löschen?")) return;

    await fetch("/api/benutzer/loeschen", {
        method: "DELETE"
    });

    alert("Account gelöscht.");
    window.location.href = "login.html";
}

async function logout() {
    await fetch("/api/benutzer/logout", { method: "POST" });
    window.location.href = "login.html";
}

loadProfil();
