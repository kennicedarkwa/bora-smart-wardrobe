async function registerUser() {
    const vorname = document.getElementById("vorname").value;
    const nachname = document.getElementById("nachname").value;
    const email = document.getElementById("email").value;
    const passwort = document.getElementById("passwort").value;
    const passwort2 = document.getElementById("passwort2").value;

    if (!vorname || !nachname || !email || !passwort || !passwort2) {
        document.getElementById("error").textContent = "Bitte alle Felder ausfüllen.";
        return;
    }

    if (passwort !== passwort2) {
        document.getElementById("error").textContent = "Passwörter stimmen nicht überein.";
        return;
    }

    const benutzer = {
        vorname: vorname,
        nachname: nachname,
        email: email,
        passwort: passwort
    };

    const response = await fetch("/api/benutzer/registrieren", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(benutzer)
    });

    if (response.ok) {
        window.location.href = "login.html";
    } else {
        document.getElementById("error").textContent = "Registrierung fehlgeschlagen.";
    }
}
