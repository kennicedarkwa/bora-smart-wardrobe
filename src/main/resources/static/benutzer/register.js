// --- NEU: Dark Mode Check ganz am Anfang ---
document.addEventListener("DOMContentLoaded", () => {
    if (localStorage.getItem("bora_darkmode") === "true") {
        document.body.classList.add("dark-mode");
    }
});

async function registerUser() {
    const vorname = document.getElementById("vorname").value.trim();
    const nachname = document.getElementById("nachname").value.trim();
    const email = document.getElementById("email").value.trim();
    const passwort = document.getElementById("passwort").value;
    const passwort2 = document.getElementById("passwort2").value;

    // 1. Validierung: Alle Felder müssen ausgefüllt sein
    if (!vorname || !nachname || !email || !passwort || !passwort2) {
        document.getElementById("error").textContent = "Bitte alle Felder ausfüllen.";
        return;
    }

    // NEU: Validierung - Ist die E-Mail syntaktisch korrekt?
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
        document.getElementById("error").textContent = "Bitte gib eine gültige E-Mail-Adresse ein (z.B. max@beispiel.de)!";
        return;
    }

    // 2. Validierung: Passwörter müssen übereinstimmen
    if (passwort !== passwort2) {
        document.getElementById("error").textContent = "Passwörter stimmen nicht überein.";
        return;
    }

    // 3. Korrektur: Wir kombinieren Vor- und Nachnamen zu 'benutzername',
    // da dein Java-Backend genau dieses Feld erwartet (statt zwei getrennte).
    const benutzer = {
        benutzername: `${vorname} ${nachname}`,
        email: email,
        passwort: passwort
    };

    // 4. API Call
    const response = await fetch("/api/benutzer/registrieren", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(benutzer)
    });

    if (response.ok) {
        // Bei Erfolg weiter zum Login
        window.location.href = "login.html";
    } else {
        // Fehler anzeigen
        document.getElementById("error").textContent = "Registrierung fehlgeschlagen. Vielleicht ist die Email bereits vergeben?";
    }
}

// NEU: Passwort ein-/ausblenden toggeln (Universell für beide Felder)
function togglePasswortSichtbarkeit(inputId, btnId) {
    const passwortInput = document.getElementById(inputId);
    const toggleBtn = document.getElementById(btnId);

    if (passwortInput.type === "password") {
        passwortInput.type = "text";
        toggleBtn.textContent = "🙈";
    } else {
        passwortInput.type = "password";
        toggleBtn.textContent = "👁️";
    }
}