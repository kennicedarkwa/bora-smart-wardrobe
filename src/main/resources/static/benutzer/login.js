async function login() {
    const id = document.getElementById("id").value;

    if (!id) {
        document.getElementById("error").textContent = "Bitte eine ID eingeben.";
        return;
    }

    try {
        const response = await fetch(`/api/benutzer/${id}`);

        if (response.ok) {
            window.location.href = "profil.html";
        } else {
            document.getElementById("error").textContent = "Benutzer nicht gefunden.";
        }
    } catch (e) {
        document.getElementById("error").textContent = "Serverfehler.";
    }
}
