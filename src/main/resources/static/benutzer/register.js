async function registerUser() {
    const name = document.getElementById("name").value;
    const email = document.getElementById("email").value;
    const passwort = document.getElementById("passwort").value;

    if (!name || !email || !passwort) {
        document.getElementById("error").textContent = "Bitte alle Felder ausfüllen.";
        return;
    }

    const benutzer = {
        name: name,
        email: email,
        passwort: passwort
    };

    try {
        const response = await fetch("/api/benutzer", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(benutzer)
        });

        if (response.ok) {
            window.location.href = "login.html";
        } else {
            document.getElementById("error").textContent = "Registrierung fehlgeschlagen.";
        }
    } catch (e) {
        document.getElementById("error").textContent = "Serverfehler.";
    }
}
