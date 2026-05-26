function getOutfit() {
    let temp = document.getElementById("temp").value;
    let weather = document.getElementById("weather").value;

    let outfit = "";

    if (weather === "rain") {
        outfit = "🌧 Regenjacke + lange Hose + wasserdichte Schuhe";
    }
    else if (temp <= 5) {
        outfit = "❄️ Winterjacke + Schal + Handschuhe";
    }
    else if (temp <= 15) {
        outfit = "🧥 Hoodie + Jeans";
    }
    else if (temp > 15 && weather === "sun") {
        outfit = "☀️ T-Shirt + Shorts";
    }
    else {
        outfit = "👕 T-Shirt + lange Hose";
    }

    let result = document.getElementById("result");

    result.innerHTML = `
        <div style="border:1px solid black; padding:10px; border-radius:10px;">
            <h3>Empfehlung</h3>
            <p>${outfit}</p>
        </div>
    `;
}
``