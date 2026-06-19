// --- Dark Mode Check ---
document.addEventListener("DOMContentLoaded", () => {
    if (localStorage.getItem("bora_darkmode") === "true") {
        document.body.classList.add("dark-mode");
    }
});

const clothesList = document.getElementById("clothesList");
const clearBtn = document.getElementById("removeSelectedBtn");
const clearAllBtn = document.getElementById("clearAllBtn");

// HILFSFUNKTION: Steuert den Zustand der Buttons basierend auf dem Listeninhalt
function updateButtonStates(isEmpty) {
    if (isEmpty) {
        clearBtn.disabled = true;
        clearAllBtn.disabled = true;
    } else {
        clearBtn.disabled = false;
        clearAllBtn.disabled = false;
    }
}

async function loadBasket() {
    try {
        let combinedItems = [];
        const itemIds = new Set();

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
            console.log("Ehsans Korb-API noch leer.");
        }

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
            console.log("Kleidungs-DB nicht erreichbar.");
        }

        clothesList.innerHTML = "";

        // WENN LEER: Buttons deaktivieren und Text anzeigen
        if (combinedItems.length === 0) {
            clothesList.innerHTML = "<p style='text-align: center; font-weight: bold; width: 100%; grid-column: 1 / -1; margin-top: 20px;'>Der Wäschekorb ist leer.</p>";
            updateButtonStates(true); // Schaltet Buttons aus (greift auf CSS :disabled zu)
            return;
        }

        // WENN ELEMENTE DA SIND: Buttons aktivieren
        updateButtonStates(false);

        combinedItems.forEach(item => {
            const li = document.createElement("li");
            li.className = "cloth-item";

            // NEU: Standard-Bildpfad setzen, falls die Kategorie nicht matcht
            let imageSrc = "bilder/klamotten/default.png";

            // Pfade zu deinen Bildern im Repository anpassen!
            if (item.hauptkategorie === "OBERTEIL") {
                imageSrc = "bilder/klamotten/oberteil.png";
            } else if (item.hauptkategorie === "UNTERTEIL") {
                imageSrc = "bilder/klamotten/unterteil.png";
            } else if (item.hauptkategorie === "SCHUHE") {
                imageSrc = "bilder/klamotten/schuhe.png";
            } else if (item.hauptkategorie === "KOPFBEDECKUNG") {
                imageSrc = "bilder/klamotten/kopfbedeckung.png";
            }

            // Falls du spezifische Bilder pro Kleidungsstück hast, könntest du sogar das nutzen:
            // if (item.bildUrl) imageSrc = item.bildUrl;

            li.innerHTML = `
                <div class="cloth-left" style="display: flex; align-items: center; gap: 15px; width: 100%;">
                    <input type="checkbox" value="${item.id}" style="transform: scale(1.3); cursor: pointer;">
                    
                    <img src="${imageSrc}" alt="${item.name}" class="cloth-img" style="width: 40px; height: 40px; object-fit: cover; border-radius: 4px;">
                    
                    <span class="cloth-name" style="font-size: 16px;">${item.name}</span>
                </div>
            `;
            clothesList.appendChild(li);
        });
    } catch (error) {
        console.error("Fehler im Wäschekorb:", error);
    }
}

clearBtn.addEventListener("click", async () => {
    // Sicherheitsnetz, falls der Button trotz 'disabled' getriggert wird
    if (clearBtn.disabled) return;

    const checkedBoxes = document.querySelectorAll("input[type='checkbox']:checked");
    const ids = Array.from(checkedBoxes).map(cb => Number(cb.value));

    if (ids.length === 0) {
        alert("Bitte wähle zuerst Kleidungsstücke aus!");
        return;
    }

    try {
        await fetch("/api/waechekorb/items", {
            method: "DELETE",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ ids: ids })
        }).catch(() => {});

        const clothesResponse = await fetch("/api/kleidung");
        if (clothesResponse.ok) {
            const allClothes = await clothesResponse.json();
            for (const id of ids) {
                const originalItem = allClothes.find(c => c.id === id);
                if (originalItem) {
                    originalItem.istImWaeschekorb = false;
                    await fetch(`/api/kleidung/${id}`, {
                        method: "PUT",
                        headers: { "Content-Type": "application/json" },
                        body: JSON.stringify(originalItem)
                    });
                }
            }
        }
        loadBasket();
    } catch (error) {
        console.error("Fehler beim Leeren ausgewählter Teile:", error);
    }
});

async function logout() {
    const bestaetigung = confirm("Möchtest du dich wirklich ausloggen?");
    if (bestaetigung) {
        await fetch("/api/benutzer/logout", { method: "POST" });
        window.location.href = "/benutzer/login.html";
    }
}

clearAllBtn.addEventListener("click", async () => {
    // Sicherheitsnetz
    if (clearAllBtn.disabled) return;

    if (!confirm("Möchtest du wirklich alle Kleidungsstücke auf einmal aus dem Wäschekorb entfernen?")) {
        return;
    }

    try {
        await fetch("/api/waechekorb/clear", {
            method: "DELETE"
        }).catch(() => {});

        const clothesResponse = await fetch("/api/kleidung");
        if (clothesResponse.ok) {
            const allClothes = await clothesResponse.json();
            const basketItems = allClothes.filter(c => c.istImWaeschekorb === true || c.istImWaeschekorb === "true");

            for (const item of basketItems) {
                item.istImWaeschekorb = false;
                await fetch(`/api/kleidung/${item.id}`, {
                    method: "PUT",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(item)
                });
            }
        }
        loadBasket();
    } catch (error) {
        console.error("Fehler beim kompletten Leeren des Wäschekorbs:", error);
    }
});

loadBasket();