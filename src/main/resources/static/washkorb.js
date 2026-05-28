const basketId = 1;

const clothesList = document.getElementById("clothesList");
const clearBtn = document.getElementById("clearBtn");

async function loadBasket() {

    const response = await fetch(
        `http://localhost:1531/api/waechekorb/${basketId}`
    );

    const basket = await response.json();

    clothesList.innerHTML = "";

    basket.items.forEach(item => {

        const li = document.createElement("li");
        li.className = "cloth-item";

        li.innerHTML = `
            <div class="cloth-left">

                <input type="checkbox" value="${item.id}">

                <span class="cloth-emoji">👕</span>

                <span class="cloth-name">
                    ${item.name}
                </span>

            </div>
        `;

        clothesList.appendChild(li);
    });
}

clearBtn.addEventListener("click", async () => {

    const checkedBoxes =
        document.querySelectorAll("input[type='checkbox']:checked");

    const ids = Array.from(checkedBoxes).map(cb => Number(cb.value));

    if (ids.length === 0) {
        alert("Keine Kleidung ausgewählt");
        return;
    }

    await fetch(
        `http://localhost:1531/api/waechekorb/${basketId}/items`,
        {
            method: "DELETE",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                ids: ids
            })
        }
    );

    loadBasket();
});

// initial load
loadBasket();