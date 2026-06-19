document.addEventListener("DOMContentLoaded", () => {

    // NEU: Dark Mode für das Dashboard aktivieren, falls er in den Einstellungen an ist
    if (localStorage.getItem("bora_darkmode") === "true") {
        document.body.classList.add("dark-mode");
    }

    const badge = document.getElementById("laundry-badge");
    const popup = document.getElementById("laundry-popup");
    const closeCross = document.getElementById("close-popup-cross");
    const closeBtn = document.getElementById("close-popup-btn");

    // Hier holen wir das Limit aus deinen Einstellungen (Standardwert ist 5)
    const maxWaescheLimit = localStorage.getItem("bora_waeschelimit") || 5;

    fetch('/api/waechekorb')
        .then(response => response.json())
        .then(data => {
            if (data && data.items) {
                const anzahl = data.items.length;

                if (anzahl > 0) {
                    badge.innerText = anzahl;
                    badge.style.display = "inline-block";
                }

                const schonGesehen = sessionStorage.getItem("laundryWarningSeen");

                // Nutzt jetzt dein variables Limit statt der festen 10
                if (anzahl >= maxWaescheLimit && !schonGesehen) {
                    popup.style.display = "flex";
                }
            }
        })
        .catch(error => {
            console.error("Fehler beim Abrufen des Wäschekorbs:", error);
        });

    function hidePopup() {
        popup.style.display = "none";

        sessionStorage.setItem("laundryWarningSeen", "true");
    }

    closeCross.addEventListener("click", hidePopup);
    closeBtn.addEventListener("click", hidePopup);
});