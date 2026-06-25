document.addEventListener("DOMContentLoaded", () => {

    // NEU: Dark Mode für das Dashboard aktivieren, falls er in den Einstellungen an ist
    if (localStorage.getItem("bora_darkmode") === "true") {
        document.body.classList.add("dark-mode");
    }

    const badge = document.getElementById("laundry-badge");
    const popup = document.getElementById("laundry-popup");
    const closeCross = document.getElementById("close-popup-cross");
    const closeBtn = document.getElementById("close-popup-btn");

    // Limit aus Einstellungen (Standard = 5)
    const maxWaescheLimit =
        Number(localStorage.getItem("bora_waeschelimit")) || 5;

    fetch('/api/waechekorb')
        .then(response => response.json())
        .then(data => {

            console.log("API-Daten:", data);

            // robuste Datenstruktur
            const items = data.items || data.kleidungsstuecke || [];
            const anzahl = items.length;

            console.log("Anzahl:", anzahl);
            console.log("Limit:", maxWaescheLimit);

            // Badge anzeigen
            if (badge && anzahl > 0) {
                badge.innerText = anzahl;
                badge.style.display = "inline-block";
            }

            // POPUP LOGIK
            if (anzahl >= maxWaescheLimit) {
                popup.style.display = "flex";
            }
        })
        .catch(error => {
            console.error("Fehler beim Abrufen des Wäschekorbs:", error);
        });

    // Popup schließen
    function hidePopup() {
        popup.style.display = "none";
    }

    closeCross.addEventListener("click", hidePopup);
    closeBtn.addEventListener("click", hidePopup);

});

