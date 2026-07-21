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

function ensureAuth() {
    if (!getToken()) {
        window.location.href = "login.html";
        return false;
    }
    if (!["ADMIN", "MANAGER"].includes(getUserRole())) {
        window.location.href = "loans.html";
        return false;
    }
    return true;
}

function formatMoney(value) {
    return new Intl.NumberFormat("en-IN", { style: "currency", currency: "INR", maximumFractionDigits: 0 }).format(value || 0);
}

function statusBadge(status) {
    if (status === "ACTIVE") {
        return `<span class="badge-soft badge-active">${status}</span>`;
    }
    if (status === "CLOSED") {
        return `<span class="badge-soft badge-closed">${status}</span>`;
    }
    return `<span class="badge-soft badge-overdue">${status || "N/A"}</span>`;
}

function safePercent(numerator, denominator) {
    if (!denominator) return 0;
    return numerator / denominator;
}

function loadDashboard() {
    const data = window.AppData.readData();
    const customers = data.customers || [];
    const loans = data.loans || [];
    const emiSchedules = Object.values(data.emiSchedules || {}).flat();

    const totalCustomers = customers.length;
    const totalLoans = loans.length;
    const activeLoans = loans.filter(l => l.loanStatus === "ACTIVE").length;
    const closedLoans = loans.filter(l => l.loanStatus === "CLOSED").length;
    const overdueEMIs = emiSchedules.filter(e => e.status === "OVERDUE").length;
    const totalEMICollected = emiSchedules.reduce((sum, e) => sum + (e.amountPaid || 0), 0);
    const totalPenaltyCollected = emiSchedules.reduce((sum, e) => sum + (e.penaltyAmount || 0), 0);
    const averageInterestRate = safePercent(loans.reduce((sum, l) => sum + (l.annualInterestRate || 0), 0), loans.length);

    document.getElementById("totalCustomers").textContent = totalCustomers;
    document.getElementById("totalLoans").textContent = totalLoans;
    document.getElementById("activeLoans").textContent = activeLoans;
    document.getElementById("closedLoans").textContent = closedLoans;
    document.getElementById("overdueEMIs").textContent = overdueEMIs;
    document.getElementById("totalEMICollected").textContent = formatMoney(totalEMICollected);
    document.getElementById("totalPenaltyCollected").textContent = formatMoney(totalPenaltyCollected);
    document.getElementById("averageInterestRate").textContent = `${averageInterestRate.toFixed(2)}%`;

    if (getUserRole() === "ADMIN") {
        const adminInsights = document.getElementById("adminInsightsSection");
        adminInsights.classList.remove("d-none");

        const highestOutstanding = [...loans].sort((a, b) => (b.principalAmount || 0) - (a.principalAmount || 0))[0];
        document.getElementById("highestOutstandingLoan").textContent = highestOutstanding
            ? `Loan#${highestOutstanding.loanId} (${highestOutstanding.loanType})`
            : "N/A";

        const loanById = loans.reduce((acc, loan) => {
            acc[loan.loanId] = loan;
            return acc;
        }, {});
        const paidByCustomerId = {};
        emiSchedules.forEach(emi => {
            const loan = loanById[emi.loanId];
            if (!loan) return;
            const cid = loan.customerId;
            paidByCustomerId[cid] = (paidByCustomerId[cid] || 0) + (emi.amountPaid || 0);
        });

        let highestCustomerLabel = "N/A";
        const ranked = Object.entries(paidByCustomerId).sort((a, b) => b[1] - a[1])[0];
        if (ranked) {
            const customer = customers.find(c => Number(c.customerId) === Number(ranked[0]));
            highestCustomerLabel = customer
                ? `Customer#${customer.customerId} (${customer.customerName})`
                : `Customer#${ranked[0]}`;
        }
        document.getElementById("highestPayingCustomer").textContent = highestCustomerLabel;

        const overdueCustomerIds = new Set();
        emiSchedules.forEach(emi => {
            if (emi.status === "OVERDUE" && (emi.daysPastDue || 0) >= 90) {
                const loan = loanById[emi.loanId];
                if (loan) overdueCustomerIds.add(loan.customerId);
            }
        });
        document.getElementById("npaAccounts").textContent = overdueCustomerIds.size;
    }
}

function loadRecentLoans() {
    const tbody = document.getElementById("recentLoansBody");
    const loans = [...(window.AppData.readData().loans || [])]
        .sort((a, b) => (b.loanId || 0) - (a.loanId || 0))
        .slice(0, 10);

    tbody.innerHTML = loans.map(loan => `
        <tr>
            <td>${loan.loanId}</td>
            <td>${loan.customerName}</td>
            <td>${loan.loanType}</td>
            <td>${formatMoney(loan.principalAmount)}</td>
            <td>${statusBadge(loan.loanStatus)}</td>
        </tr>
    `).join("");
}

if (ensureAuth()) {
    applyRoleBadge();
    document.getElementById("logoutBtn").addEventListener("click", logout);
    loadDashboard();
    loadRecentLoans();
}
