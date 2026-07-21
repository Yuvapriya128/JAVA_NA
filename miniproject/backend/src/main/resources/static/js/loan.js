const DUMMY_LOAN_PRODUCTS = [
    { code: "PERSONAL", displayName: "Personal Loan", defaultRate: 10.0 },
    { code: "HOME", displayName: "Home Loan", defaultRate: 6.5 },
    { code: "VEHICLE", displayName: "Vehicle Loan", defaultRate: 8.0 },
    { code: "AUTO", displayName: "Auto Loan", defaultRate: 8.0 },
    { code: "EDUCATION", displayName: "Education Loan", defaultRate: 7.5 },
    { code: "BUSINESS", displayName: "Business Loan", defaultRate: 12.0 },
    { code: "GOLD", displayName: "Gold Loan", defaultRate: 12.0 },
    { code: "SECURED", displayName: "Secured Loan", defaultRate: 9.0 },
    { code: "UNSECURED", displayName: "Unsecured Loan", defaultRate: 14.0 }
];

const initialData = window.AppData.readData();
let customersCache = [...initialData.customers];
let loansCache = [...initialData.loans];
let loanOwners = { ...initialData.loanOwners };

let loanModal;
let reviseRateModal;
let loanDetailsModal;
let profileModal;

function getToken() {
    return localStorage.getItem("jwtToken");
}

function getUserRole() {
    return localStorage.getItem("userRole") || "USER";
}

function getUserEmail() {
    return (localStorage.getItem("userEmail") || "").toLowerCase();
}

function persistLoanState() {
    const data = window.AppData.readData();
    data.loans = loansCache;
    data.loanOwners = loanOwners;
    window.AppData.writeData(data);
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

function isUserRole() {
    return getUserRole() === "USER";
}

function canCreateLoans() {
    return ["ADMIN", "MANAGER"].includes(getUserRole());
}

function canReviseInterestRates() {
    return ["ADMIN", "MANAGER"].includes(getUserRole());
}

function canDeleteLoans() {
    return getUserRole() === "ADMIN";
}

function getCurrentUserProfile() {
    const email = getUserEmail();
    return customersCache.find(c => (c.email || "").toLowerCase() === email);
}

function loanBelongsToCurrentUser(loan) {
    const ownerEmail = (loanOwners[loan.loanId] || "").toLowerCase();
    return ownerEmail && ownerEmail === getUserEmail();
}

function getVisibleLoans() {
    if (!isUserRole()) {
        return loansCache;
    }
    return loansCache.filter(loanBelongsToCurrentUser);
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

function showLoanAlert(message, type = "danger") {
    const alertEl = document.getElementById("loanAlert");
    alertEl.className = `alert alert-${type}`;
    alertEl.textContent = message;
    alertEl.classList.remove("d-none");
}

function hideLoanAlert() {
    document.getElementById("loanAlert").classList.add("d-none");
}

function formatMoney(value) {
    return new Intl.NumberFormat("en-IN", { style: "currency", currency: "INR", maximumFractionDigits: 0 }).format(value || 0);
}

function statusBadge(status) {
    if (status === "ACTIVE") {
        return `<span class="badge-soft badge-active">ACTIVE</span>`;
    }
    if (status === "CLOSED") {
        return `<span class="badge-soft badge-closed">CLOSED</span>`;
    }
    return `<span class="badge-soft badge-overdue">${status || "N/A"}</span>`;
}

function calculateEMI(principal, annualRate, months) {
    const monthlyRate = annualRate / (12 * 100);
    if (monthlyRate === 0) return principal / months;
    const factor = Math.pow(1 + monthlyRate, months);
    return (principal * monthlyRate * factor) / (factor - 1);
}

function applyRoleNavigationRestrictions() {
    const role = getUserRole();
    let restrictedLinks = [];
    if (role === "USER") {
        restrictedLinks = ["dashboard.html", "customers.html", "emi.html"];
    }
    restrictedLinks.forEach(link => {
        const navLink = document.querySelector(`a.nav-link[href="${link}"]`);
        if (!navLink) return;
        const navItem = navLink.closest(".nav-item");
        if (navItem) {
            navItem.classList.add("d-none");
        }
    });
}

function renderRoleHelp() {
    const helpTextEl = document.getElementById("loanRoleHelpText");
    if (!helpTextEl) return;
    const role = getUserRole();
    if (role === "USER") {
        helpTextEl.innerHTML = "You are viewing <strong>your loans only</strong>. Use <strong>View EMI</strong> on each loan to open repayment schedule and payments.";
        return;
    }
    if (role === "MANAGER") {
        helpTextEl.innerHTML = "You can create loans, revise rates, view all customers' loans, and open EMI schedules. Delete is reserved for admin.";
        return;
    }
    helpTextEl.innerHTML = "You have full access: create/update loans, revise rates, delete loans, and manage customer lifecycle actions.";
}

function applyRolePageContext() {
    const pageTitle = document.querySelector(".section-title");
    const myProfileBtn = document.getElementById("myProfileBtn");
    if (isUserRole()) {
        if (pageTitle) pageTitle.textContent = "My Loans";
        if (myProfileBtn) myProfileBtn.classList.remove("d-none");
    } else if (pageTitle) {
        pageTitle.textContent = "Loan Management";
    }
}

function openMyProfile() {
    const body = document.getElementById("profileDetailsBody");
    if (!body) return;
    const profile = getCurrentUserProfile();
    if (!profile) {
        body.innerHTML = '<div class="text-muted">Profile details are not available.</div>';
        profileModal.show();
        return;
    }
    body.innerHTML = `
        <div class="row g-2">
            <div class="col-6"><strong>Name:</strong> ${profile.customerName}</div>
            <div class="col-6"><strong>Email:</strong> ${profile.email}</div>
            <div class="col-6"><strong>Phone:</strong> ${profile.phoneNumber}</div>
            <div class="col-6"><strong>City:</strong> ${profile.city}</div>
            <div class="col-6"><strong>Credit Score:</strong> ${profile.creditScore}</div>
            <div class="col-6"><strong>Customer ID:</strong> ${profile.customerId}</div>
        </div>
    `;
    profileModal.show();
}

function applyRoleActionVisibility() {
    const createLoanBtn = document.getElementById("createLoanBtn");
    const reviseRateBtn = document.getElementById("reviseRateBtn");
    if (createLoanBtn && !canCreateLoans()) {
        createLoanBtn.classList.add("d-none");
    }
    if (reviseRateBtn && !canReviseInterestRates()) {
        reviseRateBtn.classList.add("d-none");
    }
}

function renderLoans(list) {
    const tbody = document.getElementById("loansBody");
    if (!list.length) {
        tbody.innerHTML = '<tr><td colspan="9" class="empty-state">No loans found.</td></tr>';
        return;
    }

    tbody.innerHTML = list.map(l => {
        const actionButtons = [
            `<button class="btn btn-sm btn-outline-secondary action-btn" onclick="viewLoanDetails(${l.loanId})"><i class="fa-solid fa-eye me-1"></i>Details</button>`,
            `<a class="btn btn-sm btn-outline-primary action-btn" href="emi.html?loanId=${l.loanId}"><i class="fa-solid fa-list me-1"></i>View EMI</a>`
        ];

        if (canReviseInterestRates()) {
            actionButtons.push(`<button class="btn btn-sm btn-outline-secondary action-btn" onclick="updateLoanInterest(${l.loanId})"><i class="fa-solid fa-pen-to-square me-1"></i>Rate</button>`);
        }

        if (canDeleteLoans()) {
            actionButtons.push(`<button class="btn btn-sm btn-danger action-btn" onclick="deleteLoan(${l.loanId})"><i class="fa-solid fa-trash me-1"></i>Delete</button>`);
        }

        return `
        <tr>
            <td>${l.loanId}</td>
            <td>${l.customerName || l.customerId || "-"}</td>
            <td>${l.loanType || "-"}</td>
            <td>${formatMoney(l.principalAmount)}</td>
            <td>${(l.annualInterestRate ?? 0).toFixed(2)}%</td>
            <td>${l.tenureMonths ?? "-"} months</td>
            <td>${formatMoney(l.emiAmount)}</td>
            <td>${statusBadge(l.loanStatus)}</td>
            <td class="action-cell"><div class="action-group">${actionButtons.join("")}</div></td>
        </tr>
    `;
    }).join("");
}

function loadLoans() {
    hideLoanAlert();
    renderLoans(getVisibleLoans());
}

function filterLoans() {
    const query = document.getElementById("loanSearch").value.trim().toLowerCase();
    const source = getVisibleLoans();
    if (!query) {
        renderLoans(source);
        return;
    }
    const filtered = source.filter(l =>
        String(l.loanId).includes(query) ||
        (l.customerName || "").toLowerCase().includes(query) ||
        (l.loanType || "").toLowerCase().includes(query) ||
        (l.loanStatus || "").toLowerCase().includes(query)
    );
    renderLoans(filtered);
}

function viewLoanDetails(loanId) {
    const loan = loansCache.find(l => l.loanId === loanId);
    if (!loan) {
        showLoanAlert("Loan not found.", "warning");
        return;
    }
    const detailBody = document.getElementById("loanDetailsBody");
    detailBody.innerHTML = `
        <div class="row g-2">
            <div class="col-6"><strong>Loan ID:</strong> ${loan.loanId}</div>
            <div class="col-6"><strong>Customer:</strong> ${loan.customerName}</div>
            <div class="col-6"><strong>Loan Type:</strong> ${loan.loanType}</div>
            <div class="col-6"><strong>Status:</strong> ${loan.loanStatus}</div>
            <div class="col-6"><strong>Principal:</strong> ${formatMoney(loan.principalAmount)}</div>
            <div class="col-6"><strong>Interest:</strong> ${loan.annualInterestRate.toFixed(2)}%</div>
            <div class="col-6"><strong>Tenure:</strong> ${loan.tenureMonths} months</div>
            <div class="col-6"><strong>EMI:</strong> ${formatMoney(loan.emiAmount)}</div>
        </div>
    `;
    loanDetailsModal.show();
}

function updateLoanInterest(loanId) {
    if (!canReviseInterestRates()) {
        showLoanAlert("Only ADMIN and MANAGER can update loan interest.", "warning");
        return;
    }
    const loan = loansCache.find(l => l.loanId === loanId);
    if (!loan) return;

    const input = window.prompt(`Enter new annual rate for Loan ${loanId} (%)`, String(loan.annualInterestRate));
    if (input === null) return;
    const rate = Number(input);
    if (!rate || rate <= 0) {
        showLoanAlert("Enter a valid positive interest rate.", "warning");
        return;
    }
    loan.annualInterestRate = rate;
    loan.emiAmount = calculateEMI(loan.principalAmount, loan.annualInterestRate, loan.tenureMonths);
    persistLoanState();
    showLoanAlert(`Loan ${loanId} interest updated to ${rate.toFixed(2)}%.`, "success");
    loadLoans();
}

function deleteLoan(loanId) {
    if (!canDeleteLoans()) {
        showLoanAlert("Only ADMIN can delete loans.", "warning");
        return;
    }
    if (!window.confirm(`Delete Loan ${loanId}? This cannot be undone.`)) {
        return;
    }
    loansCache = loansCache.filter(l => l.loanId !== loanId);
    delete loanOwners[loanId];
    persistLoanState();
    showLoanAlert(`Loan ${loanId} deleted.`, "success");
    loadLoans();
}

function populateLoanProductsTable() {
    const tbody = document.getElementById("loanProductsBody");
    tbody.innerHTML = DUMMY_LOAN_PRODUCTS.map(p => `
        <tr>
            <td>${p.code}</td>
            <td>${p.displayName}</td>
            <td>${p.defaultRate.toFixed(2)}%</td>
        </tr>
    `).join("");
}

function populateLoanTypeSelect() {
    const loanTypeSelect = document.getElementById("loanType");
    loanTypeSelect.innerHTML = '<option value="">Choose...</option>' +
        DUMMY_LOAN_PRODUCTS.map(p => `<option value="${p.code}">${p.code}</option>`).join("");
}

function populateCustomerSelect() {
    const customerSelect = document.getElementById("loanCustomerId");
    customerSelect.innerHTML = '<option value="">Choose customer...</option>' +
        customersCache.filter(c => c.active !== false).map(c => `<option value="${c.customerId}">${c.customerName} (ID: ${c.customerId})</option>`).join("");
}

function populateLoanTypeCheckboxes() {
    const container = document.getElementById("loanTypeCheckboxes");
    container.innerHTML = DUMMY_LOAN_PRODUCTS.map(p => `
        <div class="col-6">
            <div class="form-check">
                <input class="form-check-input revise-loan-type" type="checkbox" value="${p.code}" id="rt-${p.code}">
                <label class="form-check-label" for="rt-${p.code}">${p.code}</label>
            </div>
        </div>
    `).join("");
}

function applyBulkInterestRevision(event) {
    event.preventDefault();
    hideLoanAlert();
    if (!canReviseInterestRates()) {
        showLoanAlert("Only ADMIN and MANAGER can revise interest rates.", "warning");
        return;
    }

    const selectedLoanTypes = Array.from(document.querySelectorAll(".revise-loan-type:checked")).map(el => el.value);
    const revisedRate = Number(document.getElementById("revisedInterestRate").value);

    if (!selectedLoanTypes.length) {
        showLoanAlert("Select at least one loan type.", "warning");
        return;
    }
    if (!revisedRate || revisedRate <= 0) {
        showLoanAlert("Enter a valid interest rate.", "warning");
        return;
    }

    let updated = 0;
    loansCache.forEach(loan => {
        if (selectedLoanTypes.includes(loan.loanType)) {
            loan.annualInterestRate = revisedRate;
            loan.emiAmount = calculateEMI(loan.principalAmount, loan.annualInterestRate, loan.tenureMonths);
            updated += 1;
        }
    });

    persistLoanState();
    reviseRateModal.hide();
    event.target.reset();
    showLoanAlert(`Interest rate revised for ${updated} loan(s).`, "success");
    loadLoans();
}

if (ensureAuth()) {
    applyRoleBadge();
    applyRoleNavigationRestrictions();
    applyRolePageContext();
    renderRoleHelp();
    applyRoleActionVisibility();

    loanModal = new bootstrap.Modal(document.getElementById("loanModal"));
    reviseRateModal = new bootstrap.Modal(document.getElementById("reviseRateModal"));
    loanDetailsModal = new bootstrap.Modal(document.getElementById("loanDetailsModal"));
    profileModal = new bootstrap.Modal(document.getElementById("profileModal"));

    document.getElementById("logoutBtn").addEventListener("click", logout);
    document.getElementById("searchLoanBtn").addEventListener("click", filterLoans);
    document.getElementById("loanSearch").addEventListener("input", filterLoans);
    document.getElementById("reviseRateForm").addEventListener("submit", applyBulkInterestRevision);
    const myProfileBtn = document.getElementById("myProfileBtn");
    if (myProfileBtn) {
        myProfileBtn.addEventListener("click", openMyProfile);
    }

    document.getElementById("loanForm").addEventListener("submit", event => {
        event.preventDefault();
        hideLoanAlert();

        if (!canCreateLoans()) {
            showLoanAlert("Only ADMIN and MANAGER can create loans.", "warning");
            return;
        }

        const payload = {
            customerId: Number(document.getElementById("loanCustomerId").value),
            loanType: document.getElementById("loanType").value,
            principalAmount: Number(document.getElementById("principalAmount").value),
            annualInterestRate: Number(document.getElementById("annualInterestRate").value),
            tenureMonths: Number(document.getElementById("tenureMonths").value)
        };

        if (!payload.customerId || !payload.loanType || payload.principalAmount <= 0 || payload.tenureMonths <= 0 || payload.annualInterestRate <= 0) {
            showLoanAlert("Enter valid loan details.", "warning");
            return;
        }

        const newId = Math.max(...loansCache.map(l => l.loanId), 0) + 1;
        const customer = customersCache.find(c => c.customerId === payload.customerId);
        loansCache.push({
            loanId: newId,
            customerId: payload.customerId,
            customerName: customer ? customer.customerName : "Unknown",
            loanType: payload.loanType,
            principalAmount: payload.principalAmount,
            annualInterestRate: payload.annualInterestRate,
            tenureMonths: payload.tenureMonths,
            emiAmount: calculateEMI(payload.principalAmount, payload.annualInterestRate, payload.tenureMonths),
            loanStatus: "ACTIVE"
        });

        if (customer && customer.email) {
            loanOwners[newId] = customer.email.toLowerCase();
        }

        persistLoanState();
        showLoanAlert("Loan created successfully.", "success");
        event.target.reset();
        loanModal.hide();
        loadLoans();
    });

    populateLoanProductsTable();
    populateLoanTypeSelect();
    populateCustomerSelect();
    populateLoanTypeCheckboxes();
    loadLoans();
}
