
function calculateNetWorth() {
    const totalAssets = Array.from(document.querySelectorAll('#assets input[type="number"]'))
      .reduce((sum, input) => sum + parseFloat(input.value || 0), 0);

    const totalDebts = Array.from(document.querySelectorAll('#debts input[type="number"]'))
      .reduce((sum, input) => sum + parseFloat(input.value || 0), 0);

    const netWorth = totalAssets - totalDebts;

    document.getElementById('totalAssets').textContent = `Total Assets: $${totalAssets.toFixed(2)}`;
    document.getElementById('totalDebts').textContent = `Total Debts: $${totalDebts.toFixed(2)}`;
    document.getElementById('netWorth').textContent = `Net Worth: $${netWorth.toFixed(2)}`;

    document.getElementById('headerTotalAssets').textContent = `$${totalAssets.toFixed(2)}`;
    document.getElementById('headerTotalDebts').textContent = `$${totalDebts.toFixed(2)}`;
    document.getElementById('headerNetWorth').textContent = `$${netWorth.toFixed(2)}`;
  }


function addAsset() {
  const name = document.getElementById('newAssetName').value.trim();
  const value = parseFloat(document.getElementById('newAssetValue').value) || 0;

  if (name) {
    const assetSection = document.getElementById('assets');
    const newItem = document.createElement('div');
    newItem.classList.add('item');
    newItem.innerHTML = `
      <label>${name}:</label>
      <input type="number" value="${value}">
    `;
    assetSection.insertBefore(newItem, assetSection.querySelector('.add-item'));
    document.getElementById('newAssetName').value = '';
    document.getElementById('newAssetValue').value = '';
  }
}

function addDebt() {
  const name = document.getElementById('newDebtName').value.trim();
  const value = parseFloat(document.getElementById('newDebtValue').value) || 0;

  if (name) {
    const debtSection = document.getElementById('debts');
    const newItem = document.createElement('div');
    newItem.classList.add('item');
    newItem.innerHTML = `
      <label>${name}:</label>
      <input type="number" value="${value}">
    `;
    debtSection.insertBefore(newItem, debtSection.querySelector('.add-item'));
    document.getElementById('newDebtName').value = '';
    document.getElementById('newDebtValue').value = '';
  }
}

calculateNetWorth();

document.querySelectorAll('#assets input, #debts input').forEach(input => {
  input.addEventListener('input', calculateNetWorth);
});


function fetchAssets() {
  fetch("http://localhost:8080/networth/current-month-assets")
      .then(response => response.json())
      .then(data => {
          const assetsList = document.getElementById("assets-list");
          assetsList.innerHTML = ""; 
          let totalAssets = 0;

          data.forEach(asset => {
              totalAssets += asset.value;
              const item = document.createElement("div");
              item.classList.add("item");
              item.innerHTML = `
                  <span>${asset.name}: $${asset.value.toFixed(2)}</span>
                  <button class="delete-button" onclick="deleteEntry(${asset.id}, 'asset')">
                      <i class="fa-solid fa-trash" aria-label="Delete"></i>
                  </button>
              `;
              assetsList.appendChild(item);
          });

          document.getElementById("headerTotalAssets").textContent = `$${totalAssets.toFixed(2)}`;
          document.getElementById("totalAssets").textContent = `Total Assets: $${totalAssets.toFixed(2)}`;
          calculateNetWorth();
      })
      .catch(error => console.error("Error fetching assets:", error));
}


function fetchDebts() {
  fetch("http://localhost:8080/networth/current-month-debts")
      .then(response => response.json())
      .then(data => {
          const debtsList = document.getElementById("debts-list");
          debtsList.innerHTML = ""; 
          let totalDebts = 0;

          data.forEach(debt => {
              totalDebts += debt.value;
              const item = document.createElement("div");
              item.classList.add("item");
              item.innerHTML = `
                  <span>${debt.name}: $${debt.value.toFixed(2)}</span>
                  <button class="delete-button" onclick="deleteEntry(${debt.id}, 'debt')">
                      <i class="fa-solid fa-trash" aria-label="Delete"></i>
                  </button>
              `;
              debtsList.appendChild(item);
          });

          document.getElementById("headerTotalDebts").textContent = `$${totalDebts.toFixed(2)}`;
          document.getElementById("totalDebts").textContent = `Total Debts: $${totalDebts.toFixed(2)}`;
          calculateNetWorth();
      })
      .catch(error => console.error("Error fetching debts:", error));
}

function deleteEntry(id, type) {
  fetch(`http://localhost:8080/networth/delete/${id}`, {
      method: "DELETE"
  })
      .then(response => {
          if (!response.ok) {
              throw new Error("Failed to delete entry.");
          }
          return response.text();
      })
      .then(message => {
          alert(message);
          if (type === "asset") fetchAssets();
          else fetchDebts();
      })
      .catch(error => alert(`Error: ${error.message}`));
}


function addEntry(type) {
    const nameInput = type === "asset" ? "newAssetName" : "newDebtName";
    const valueInput = type === "asset" ? "newAssetValue" : "newDebtValue";

    const name = document.getElementById(nameInput).value.trim();
    const value = parseFloat(document.getElementById(valueInput).value);

    if (!name || isNaN(value) || value <= 0) {
        alert(`Please enter a valid ${type} name and value.`);
        return;
    }

    const payload = {
        name: name,
        value: value,
        type: type
    };

    fetch("http://localhost:8080/networth/add", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)
    })
    .then(response => {
        if (!response.ok) {
            throw new Error("Failed to add entry.");
        }
        return response.text();
    })
    .then(message => {
        alert(message);
        if (type === "asset") fetchAssets();
        else fetchDebts();

        document.getElementById(nameInput).value = "";
        document.getElementById(valueInput).value = "";
    })
    .catch(error => alert(`Error: ${error.message}`));
}


function calculateNetWorth() {
  const totalAssets = parseFloat(document.getElementById("headerTotalAssets").textContent.replace("$", "")) || 0;
  const totalDebts = parseFloat(document.getElementById("headerTotalDebts").textContent.replace("$", "")) || 0;
  const netWorth = totalAssets - totalDebts;

  document.getElementById("headerNetWorth").textContent = `$${netWorth.toFixed(2)}`;
  document.getElementById("netWorth").textContent = `Net Worth: $${netWorth.toFixed(2)}`;
}

function initializePage() {
  fetchAssets();
  fetchDebts();
}

document.addEventListener("DOMContentLoaded", initializePage);

document.addEventListener("DOMContentLoaded", () => {
  const userImage = document.getElementById("userImage");
  const dropdownMenu = document.getElementById("dropdownMenu");

  userImage.addEventListener("click", (event) => {
    event.stopPropagation(); 
    dropdownMenu.style.display =
      dropdownMenu.style.display === "block" ? "none" : "block";
  });

  document.addEventListener("click", () => {
    dropdownMenu.style.display = "none";
  });

  const logoutButton = document.getElementById("logoutButton");
  logoutButton.addEventListener("click", () => {
    alert("Logged out!");
    window.location.href = "/User_login/login.html"; 
  });
});