document.addEventListener("DOMContentLoaded", () => {
    loadAnlaesse();
});

async function loadAnlaesse() {
    const container = document.getElementById("tags");

    try {
        const response = await fetch("/api/anlass");

        if (!response.ok) {
            container.innerHTML = "❌ Fehler beim Laden der Anlässe";
            return;
        }

        const data = await response.json();

        container.className = "tag-container";
        container.innerHTML = "";

        data.forEach(tag => {
            const card = document.createElement("div");
            card.className = "tag";

            card.innerHTML = `
                <span></span>
                <b>${tag}</b>
            `;

            container.appendChild(card);
        });

    } catch (error) {
        console.error("API Error:", error);
        container.innerHTML = "⚠️ Server nicht erreichbar";
    }
}

