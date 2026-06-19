document.addEventListener("DOMContentLoaded", async () => {

    // Dark Mode Check
    if (localStorage.getItem("bora_darkmode") === "true") {
        document.body.classList.add("dark-mode");
    }

    const popup = document.getElementById("laundry-popup");
    const closeCross = document.getElementById("close-popup-cross");
    const closeBtn = document.getElementById("close-popup-btn");

    // Limit aus den Einstellungen holen (als echte Zahl)
    const maxWaescheLimit = parseInt(localStorage.getItem("bora_waeschelimit")) || 5;

    try {
        let combinedItems = [];
        const itemIds = new Set();

        // 1. Ehsans API abfragen
        try {
            const response = await fetch("/api/waechekorb");
            if (response.ok) {
                const basket = await response.json();
                const items = basket.items || basket.kleidungsstuecke || [];
                items.forEach(item => {
                    if (!itemIds.has(item.id)) {
                        itemIds.add(item.id);
                        combinedItems.push(item);
                    }
                });
            }
        } catch (e) {
            console.log("Korb-API leer.");
        }

        // 2. Sicherheitsnetz: Kleidungs-DB abfragen
        try {
            const clothesResponse = await fetch("/api/kleidung");
            if (clothesResponse.ok) {
                const allClothes = await clothesResponse.json();
                const statusItems = allClothes.filter(c => c.istImWaeschekorb === true || c.istImWaeschekorb === "true");
                statusItems.forEach(item => {
                    if (!itemIds.has(item.id)) {
                        itemIds.add(item.id);
                        combinedItems.push(item);
                    }
                });
            }
        } catch (e) {
            console.log("Kleidungs-DB Fehler.");
        }

        // --- Auswertung ---
        const anzahl = combinedItems.length;
        const schonGesehen = sessionStorage.getItem("laundryWarningSeen");

        // Popup anzeigen, wenn Limit erreicht UND es in dieser Sitzung noch nicht weggeklickt wurde
        if (anzahl >= maxWaescheLimit && popup && !schonGesehen) {
            popup.style.display = "flex";
        }

    } catch (error) {
        console.error("Fehler beim Abrufen des Wäschekorbs:", error);
    }

    // Funktion zum Schließen des Popups
    function hidePopup() {
        if (popup) popup.style.display = "none";
        // Sobald du klickst, merkt sich der aktuelle Tab das:
        sessionStorage.setItem("laundryWarningSeen", "true");
    }

    // FEHLTE IN DEINEM CODE: Hier werden die Buttons mit der Schließen-Funktion verknüpft!
    if (closeCross) closeCross.addEventListener("click", hidePopup);
    if (closeBtn) closeBtn.addEventListener("click", hidePopup);

});