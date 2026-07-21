let storeData = window.AppData.readData();
let emiSchedulesStore = { ...storeData.emiSchedules };
let loanOwners = { ...storeData.loanOwners };

let emiDataCache = [];
let currentLoanId = null;
let payEmiModal;

function getToken() {
    return localStorage.getItem("jwtToken");
}

function getUserRole() {
    return localStorage.getItem("userRole") || "USER";
}

function applyRoleBadge() {
    const role = getUserRole();
    const badge = document.getElementById("roleBadge");
    if (!badge) return;
    badge.textContent = role;
    badge.className = "role-badge";
    if (role === "ADMIN") badge.classList.add("role-admin");
    else if (role === "MANAGER") badge.classList.add("role-manager");
    else badge.classList.add("role-user");
}

function getUserEmail() {
    return (localStorage.getItem("userEmail") || "").toLowerCase();
}

function canAccessLoan(loanId) {
    if (getUserRole() !== "USER") {
        return true;
    }
    const ownerEmail = (loanOwners[loanId] || "").toLowerCase();
    return ownerEmail && ownerEmail === getUserEmail();
}

function persistEmiState() {
    const data = window.AppData.readData();
    data.emiSchedules = emiSchedulesStore;

    if (currentLoanId) {
        const loanSchedules = emiSchedulesStore[String(currentLoanId)] || [];
        const allPaid = loanSchedules.length > 0 && loanSchedules.every(e => e.status === "PAID");
        const loan = (data.loans || []).find(l => Number(l.loanId) === Number(currentLoanId));
        if (loan) {
            loan.loanStatus = allPaid ? "CLOSED" : "ACTIVE";
        }
    }

    window.AppData.writeData(data);
}

function applyUserNavigationRestrictions() {
    const role = getUserRole();
    let restrictedLinks = [];
    if (role === "USER") {
        restrictedLinks = ["dashboard.html", "customers.html", "emi.html"];
    }
    restrictedLinks.forEach(link => {
        const navLink = document.querySelector(`a.nav-link[href="${link}"]`);
        if (navLink) {
            const navItem = navLink.closest(".nav-item");
            if (navItem) {
                navItem.classList.add("d-none");
            }
        }
    });
}

function renderRoleHelp() {
    const helpText = document.getElementById("emiRoleHelpText");
    if (!helpText) return;
    const role = getUserRole();
    if (role === "USER") {
        helpText.innerHTML = "This page opens from <strong>View EMI</strong> in My Loans. You can pay installments for your own loans only.";
        return;
    }
    if (role === "MANAGER") {
        helpText.textContent = "You can load EMI schedules for any loan and process payments.";
        return;
    }
    helpText.textContent = "You can load and manage EMI payments for all loans.";
}

function ensureAuth() {
    if (!getToken()) {
        window.location.href = "login.html";
        return false;
    }
    return true;
}

function logout() {
    if (!window.confirm("Are you sure you want to logout?")) {
        return;
    }
    localStorage.removeItem("jwtToken");
    localStorage.removeItem("userName");
    localStorage.removeItem("userRole");
    localStorage.removeItem("userEmail");
    window.location.href = "login.html";
}

function showEmiAlert(message, type = "danger") {
    const alertEl = document.getElementById("emiAlert");
    alertEl.className = `alert alert-${type}`;
    alertEl.textContent = message;
    alertEl.classList.remove("d-none");
}

function hideEmiAlert() {
    document.getElementById("emiAlert").classList.add("d-none");
}

function formatMoney(value) {
    return new Intl.NumberFormat("en-IN", { style: "currency", currency: "INR", maximumFractionDigits: 0 }).format(value || 0);
}

function statusBadge(status) {
    if (status === "PAID") {
        return `<span class="badge-soft badge-active">PAID</span>`;
    }
    if (status === "OVERDUE") {
        return `<span class="badge-soft badge-overdue">OVERDUE</span>`;
    }
    return `<span class="badge-soft badge-closed">PENDING</span>`;
}

function renderEmiTable(items) {
    const tbody = document.getElementById("emiBody");

    if (!items.length) {
        tbody.innerHTML = '<tr><td colspan="8" class="empty-state">No EMI schedules found for this loan.</td></tr>';
        return;
    }

    tbody.innerHTML = items.map(emi => `
        <tr>
            <td>${emi.installmentNumber}</td>
            <td>${emi.dueDate || "-"}</td>
            <td>${formatMoney(emi.amountDue)}</td>
            <td>${formatMoney(emi.principalComponent)}</td>
            <td>${formatMoney(emi.interestComponent)}</td>
            <td>${formatMoney(emi.penaltyAmount)}</td>
            <td>${statusBadge(emi.status)}</td>
            <td>
                ${emi.status !== "PAID" ? `<button class="btn btn-sm btn-primary" onclick="openPayModal(${emi.emiId}, ${emi.amountDue || 0})">
                    <i class="fa-solid fa-credit-card me-1"></i>Pay EMI
                </button>` : `<span class="badge bg-success">Paid</span>`}
            </td>
        </tr>
    `).join("");
}

function loadEmi() {
    hideEmiAlert();
    const loanId = parseInt(document.getElementById("emiLoanId").value.trim(), 10);

    if (!loanId || Number.isNaN(loanId)) {
        showEmiAlert("Enter a valid Loan ID (e.g., 1001) to load the EMI schedule.");
        return;
    }

    if (!canAccessLoan(loanId)) {
        emiDataCache = [];
        currentLoanId = null;
        renderEmiTable(emiDataCache);
        showEmiAlert("You can only view EMI schedule for your own loans.", "warning");
        return;
    }

    currentLoanId = loanId;
    emiDataCache = emiSchedulesStore[String(loanId)] || [];

    if (!emiDataCache.length) {
        showEmiAlert(`No EMI schedules found for Loan #${loanId}.`);
    } else {
        showEmiAlert(`Loaded ${emiDataCache.length} EMI schedules for Loan #${loanId}.`, "success");
    }

    renderEmiTable(emiDataCache);
}

function openPayModal(emiId, defaultAmount) {
    document.getElementById("payEmiId").value = emiId;
    document.getElementById("payAmount").value = defaultAmount;
    document.getElementById("payMode").value = "";
    document.getElementById("referenceNumber").value = "";
    payEmiModal.show();
}

function payEmi(payload) {
    const emiIdx = emiDataCache.findIndex(e => e.emiId === payload.emiId);
    if (emiIdx < 0) return;

    const emi = emiDataCache[emiIdx];
    const newPaid = (emi.amountPaid || 0) + payload.amount;
    if (newPaid >= emi.amountDue + emi.penaltyAmount) {
        emiDataCache[emiIdx].status = "PAID";
        emiDataCache[emiIdx].amountPaid = newPaid;
        emiDataCache[emiIdx].paymentDate = new Date().toISOString().split("T")[0];
        emiDataCache[emiIdx].penaltyAmount = 0;
        emiDataCache[emiIdx].daysPastDue = 0;
    } else {
        emiDataCache[emiIdx].status = "PENDING";
        emiDataCache[emiIdx].amountPaid = newPaid;
        emiDataCache[emiIdx].paymentDate = new Date().toISOString().split("T")[0];
    }
    persistEmiState();
}

function setLoanIdFromQuery() {
    const params = new URLSearchParams(window.location.search);
    const loanId = params.get("loanId");
    if (loanId) {
        document.getElementById("emiLoanId").value = loanId;
    }
}

if (ensureAuth()) {
    applyRoleBadge();
    applyUserNavigationRestrictions();
    renderRoleHelp();
    payEmiModal = new bootstrap.Modal(document.getElementById("payEmiModal"));
    document.getElementById("logoutBtn").addEventListener("click", logout);
    document.getElementById("loadEmiBtn").addEventListener("click", loadEmi);
    setLoanIdFromQuery();

    document.getElementById("payEmiForm").addEventListener("submit", event => {
        event.preventDefault();
        hideEmiAlert();

        const payload = {
            emiId: Number(document.getElementById("payEmiId").value),
            amount: Number(document.getElementById("payAmount").value),
            paymentMode: document.getElementById("payMode").value,
            referenceNumber: document.getElementById("referenceNumber").value.trim()
        };

        try {
            if (!payload.amount || payload.amount <= 0) {
                throw new Error("Enter valid payment amount.");
            }
            if (!payload.paymentMode) {
                throw new Error("Select payment mode.");
            }
            if (!payload.referenceNumber) {
                throw new Error("Enter reference number.");
            }

            payEmi(payload);
            payEmiModal.hide();
            showEmiAlert("EMI payment processed successfully.", "success");
            renderEmiTable(emiDataCache);
        } catch (error) {
            showEmiAlert(error.message);
        }
    });

    if (document.getElementById("emiLoanId").value) {
        loadEmi();
    }
}
