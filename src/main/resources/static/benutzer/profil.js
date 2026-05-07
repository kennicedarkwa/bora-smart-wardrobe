async function loadProfile() {
    try {
        const response = await fetch("/api/benutzer/aktueller");

        if (response.ok) {
            const user = await response.json();
            document.getElementById("id").textContent = user.id;
            document.getElementById("name").textContent = user.name;
            document.getElementById("email").textContent = user.email;
        } else {
            document.getElementById("error").textContent = "Kein Benutzer eingeloggt.";
        }
    } catch (e) {
        document.getElementById("error").textContent = "Serverfehler.";
    }
}

function logout() {
    fetch("/api/benutzer/logout", { method: "POST" })
        .then(() => window.location.href = "login.html");
}

loadProfile();
