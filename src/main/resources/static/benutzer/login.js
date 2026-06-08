async function login() {
    // 1. Get email and password from your input fields
    const email = document.getElementById("email").value;       // Make sure your input HTML id is "email"
    const passwort = document.getElementById("passwort").value; // Make sure your input HTML id is "passwort"

    if (!email || !passwort) {
        document.getElementById("error").textContent = "Bitte Email und Passwort eingeben.";
        return;
    }

    try {
        // 2. Your backend expects @RequestParam, which means URL-encoded parameters
        const urlParams = new URLSearchParams();
        urlParams.append('email', email);
        urlParams.append('passwort', passwort);

        // 3. Send a POST request to the matching Java endpoint
        const response = await fetch('/api/benutzer/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: urlParams
        });

        // 4. Handle the response
        if (response.ok) {
            // Success! Redirect to profile page
            window.location.href = "profil.html";
        } else {
            const errorText = await response.text();
            document.getElementById("error").textContent = errorText || "Login fehlgeschlagen.";
        }
    } catch (e) {
        console.error(e);
        document.getElementById("error").textContent = "Serverfehler.";
    }
}