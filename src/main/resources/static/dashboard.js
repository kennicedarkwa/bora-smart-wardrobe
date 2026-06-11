document.addEventListener("DOMContentLoaded", () => {

    const badge = document.getElementById("laundry-badge");
    const popup = document.getElementById("laundry-popup");
    const closeCross = document.getElementById("close-popup-cross");
    const closeBtn = document.getElementById("close-popup-btn");


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

                // Nur zeigen, wenn >= 10 UND noch nicht in dieser Session weggedrückt
                if (anzahl >= 10 && !schonGesehen) {
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