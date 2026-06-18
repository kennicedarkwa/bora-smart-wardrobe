// --- NEU: Dark Mode Check  ---
document.addEventListener("DOMContentLoaded", () => {
    if (localStorage.getItem("bora_darkmode") === "true") {
        document.body.classList.add("dark-mode");
    }
});

const clothesList = document.getElementById("clothesList");
const clearBtn = document.getElementById("removeSelectedBtn");
const clearAllBtn = document.getElementById("clearAllBtn"); // Neuer Button-Spion

async function loadBasket() {
    try {
        let combinedItems = [];
        const itemIds = new Set();


        try {
            const response = await fetch("http://localhost:1531/api/waechekorb");
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
            const clothesResponse = await fetch("http://localhost:1531/api/kleidung");
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

        if (combinedItems.length === 0) {
            clothesList.innerHTML = "<p style='text-align: center; font-weight: bold; width: 100%;'>Der Wäschekorb ist leer.</p>";
            return;
        }

        combinedItems.forEach(item => {
            const li = document.createElement("li");
            li.className = "cloth-item";

            let emoji = "👕";
            if (item.hauptkategorie === "UNTERTEIL") emoji = "👖";
            if (item.hauptkategorie === "SCHUHE") emoji = "👟";
            if (item.hauptkategorie === "KOPFBEDECKUNG") emoji = "🧢";

            li.innerHTML = `
                <div class="cloth-left">
                    <input type="checkbox" value="${item.id}">
                    <span class="cloth-emoji">${emoji}</span>
                    <span class="cloth-name">${item.name}</span>
                </div>
            `;
            clothesList.appendChild(li);
        });
    } catch (error) {
        console.error("Fehler im Wäschekorb:", error);
    }
}


clearBtn.addEventListener("click", async () => {
    const checkedBoxes = document.querySelectorAll("input[type='checkbox']:checked");
    const ids = Array.from(checkedBoxes).map(cb => Number(cb.value));

    if (ids.length === 0) {
        alert("Bitte wähle zuerst Kleidungsstücke aus!");
        return;
    }

    try {
        await fetch("http://localhost:1531/api/waechekorb/items", {
            method: "DELETE",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ ids: ids })
        }).catch(() => {});

        const clothesResponse = await fetch("http://localhost:1531/api/kleidung");
        if (clothesResponse.ok) {
            const allClothes = await clothesResponse.json();
            for (const id of ids) {
                const originalItem = allClothes.find(c => c.id === id);
                if (originalItem) {
                    originalItem.istImWaeschekorb = false;
                    await fetch(`http://localhost:1531/api/kleidung/${id}`, {
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


clearAllBtn.addEventListener("click", async () => {

    if (!confirm("Möchtest du wirklich alle Kleidungsstücke auf einmal aus dem Wäschekorb entfernen?")) {
        return;
    }

    try {

        await fetch("http://localhost:1531/api/waechekorb/clear", {
            method: "DELETE"
        }).catch(() => {});


        const clothesResponse = await fetch("http://localhost:1531/api/kleidung");
        if (clothesResponse.ok) {
            const allClothes = await clothesResponse.json();


            const basketItems = allClothes.filter(c => c.istImWaeschekorb === true || c.istImWaeschekorb === "true");


            for (const item of basketItems) {
                item.istImWaeschekorb = false;
                await fetch(`http://localhost:1531/api/kleidung/${item.id}`, {
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