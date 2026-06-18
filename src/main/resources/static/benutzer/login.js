console.log("login.js wurde geladen");

async function login() {
    const email = document.getElementById("email").value;
    const passwort = document.getElementById("passwort").value;

    if (!email || !passwort) {
        document.getElementById("error").textContent = "Bitte Email und Passwort eingeben.";
        return;
    }

    const params = new URLSearchParams();
    params.append("email", email);
    params.append("passwort", passwort);

    const response = await fetch("/api/benutzer/login", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: params
    });

    if (response.ok) {
        // ⭐ Jetzt: Login → Kleidung
        window.location.href = "/dashboard.html";
    } else {
        document.getElementById("error").textContent = "Login fehlgeschlagen.";
    }
}

// Passwort ein-/ausblenden toggeln
function togglePasswortSichtbarkeit() {
    const passwortInput = document.getElementById("passwort");
    const toggleBtn = document.getElementById("togglePasswort");

    if (passwortInput.type === "password") {
        passwortInput.type = "text";
        toggleBtn.textContent = "🙈";
    } else {
        passwortInput.type = "password";
        toggleBtn.textContent = "👁️";
    }
}