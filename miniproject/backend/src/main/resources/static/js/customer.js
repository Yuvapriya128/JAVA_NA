const customerData = window.AppData.readData();
let customersCache = [...customerData.customers];
let adminAuditLog = [...(customerData.adminAuditLog || [])];
let customerModal;

function getToken() {
    return localStorage.getItem("jwtToken");
}

function getUserRole() {
    return localStorage.getItem("userRole") || "USER";
}

function canManageCustomers() {
    return ["ADMIN", "MANAGER"].includes(getUserRole());
}

function canDeactivateCustomers() {
    return getUserRole() === "ADMIN";
}

function persistCustomerState() {
    const data = window.AppData.readData();
    data.customers = customersCache;
    data.adminAuditLog = adminAuditLog;
    window.AppData.writeData(data);
}

function getActiveLoanCountByCustomerId(customerId) {
    const data = window.AppData.readData();
    return (data.loans || []).filter(l => Number(l.customerId) === Number(customerId) && String(l.loanStatus).toUpperCase() === "ACTIVE").length;
}

function renderRoleHelp() {
    const helpText = document.getElementById("customerRoleHelpText");
    if (!helpText) return;
    if (getUserRole() === "ADMIN") {
        helpText.textContent = "You can create customers, view all customers, deactivate eligible customers, and reactivate deactivated users.";
        return;
    }
    helpText.textContent = "You can create and view customers. Deactivate/reactivate actions are admin-only.";
}

function addAdminAuditEntry(message) {
    if (!canDeactivateCustomers()) return;
    adminAuditLog.unshift(`[${new Date().toLocaleString()}] ${message}`);
    if (adminAuditLog.length > 12) {
        adminAuditLog.length = 12;
    }
}

function renderAdminAudit() {
    const section = document.getElementById("adminAuditSection");
    const list = document.getElementById("adminAuditList");
    if (!section || !list) return;
    if (!canDeactivateCustomers()) {
        section.classList.add("d-none");
        return;
    }
    section.classList.remove("d-none");
    if (!adminAuditLog.length) {
        list.innerHTML = '<li class="text-muted">No admin activity recorded yet.</li>';
        return;
    }
    list.innerHTML = adminAuditLog.map(item => `<li>${item}</li>`).join("");
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

function ensureAuth() {
    if (!getToken()) {
        window.location.href = "login.html";
        return false;
    }
    if (!canManageCustomers()) {
        window.location.href = "loans.html";
        return false;
    }
    return true;
}

function logout() {
    if (!window.confirm("Are you sure you want to logout?")) return;
    localStorage.removeItem("jwtToken");
    localStorage.removeItem("userName");
    localStorage.removeItem("userRole");
    localStorage.removeItem("userEmail");
    window.location.href = "login.html";
}

function showCustomerAlert(message, type = "danger") {
    const alertEl = document.getElementById("customerAlert");
    alertEl.className = `alert alert-${type}`;
    alertEl.textContent = message;
    alertEl.classList.remove("d-none");
}

function hideCustomerAlert() {
    document.getElementById("customerAlert").classList.add("d-none");
}

function renderCustomers(list) {
    const tbody = document.getElementById("customersBody");
    if (!list.length) {
        tbody.innerHTML = '<tr><td colspan="8" class="empty-state">No customer found.</td></tr>';
        return;
    }

    tbody.innerHTML = list.map(c => {
        const status = c.active ? '<span class="badge-soft badge-active">ACTIVE</span>' : '<span class="badge-soft badge-closed">DEACTIVATED</span>';
        let action = '<span class="text-muted">No admin action</span>';
        if (canDeactivateCustomers()) {
            action = c.active
                ? `<button class="btn btn-sm btn-outline-danger" onclick="deactivateCustomer(${c.customerId})"><i class="fa-solid fa-user-slash"></i> Deactivate</button>`
                : `<button class="btn btn-sm btn-outline-success" onclick="reactivateCustomer(${c.customerId})"><i class="fa-solid fa-user-check"></i> Reactivate</button>`;
        }
        return `
        <tr>
            <td>${c.customerId}</td>
            <td>${c.customerName ?? "-"}</td>
            <td>${c.email ?? "-"}</td>
            <td>${c.phoneNumber ?? "-"}</td>
            <td>${c.city ?? "-"}</td>
            <td>${c.creditScore ?? "-"}</td>
            <td>${status}</td>
            <td>${action}</td>
        </tr>
    `;
    }).join("");
}

function openAddCustomer() {
    document.getElementById("customerModalTitle").textContent = "Add Customer";
    document.getElementById("customerForm").reset();
    document.getElementById("customerId").value = "";
}

function getCustomerById() {
    hideCustomerAlert();
    const input = document.getElementById("customerIdSearch").value.trim();
    const customerId = Number(input);
    if (!input || !customerId) {
        showCustomerAlert("Enter a valid customer ID.", "warning");
        return;
    }
    const customer = customersCache.find(c => c.customerId === customerId);
    if (!customer) {
        renderCustomers([]);
        showCustomerAlert(`Customer ${customerId} not found.`, "warning");
        return;
    }
    renderCustomers([customer]);
    showCustomerAlert(`Customer ${customerId} loaded successfully.`, "success");
}

function getAllCustomers() {
    hideCustomerAlert();
    renderCustomers(customersCache);
    renderAdminAudit();
    showCustomerAlert(`Loaded ${customersCache.length} customers.`, "success");
}

function deactivateCustomer(customerId) {
    if (!canDeactivateCustomers()) {
        showCustomerAlert("Only ADMIN can deactivate customers.", "warning");
        return;
    }
    const customer = customersCache.find(c => c.customerId === customerId);
    if (!customer) {
        showCustomerAlert("Customer not found.", "warning");
        return;
    }
    if (!customer.active) {
        showCustomerAlert(`Customer ${customerId} is already deactivated.`, "info");
        return;
    }
    if (getActiveLoanCountByCustomerId(customerId) > 0) {
        showCustomerAlert("Cannot deactivate customer with active loans.", "warning");
        return;
    }
    if (!window.confirm(`Deactivate customer ${customer.customerName}?`)) {
        return;
    }
    customer.active = false;
    addAdminAuditEntry(`Deactivated customer #${customerId} (${customer.customerName})`);
    persistCustomerState();
    showCustomerAlert(`Customer ${customerId} deactivated successfully.`, "success");
    getAllCustomers();
}

function reactivateCustomer(customerId) {
    if (!canDeactivateCustomers()) {
        showCustomerAlert("Only ADMIN can reactivate customers.", "warning");
        return;
    }
    const customer = customersCache.find(c => c.customerId === customerId);
    if (!customer) {
        showCustomerAlert("Customer not found.", "warning");
        return;
    }
    if (customer.active) {
        showCustomerAlert(`Customer ${customerId} is already active.`, "info");
        return;
    }
    if (!window.confirm(`Reactivate customer ${customer.customerName}?`)) {
        return;
    }
    customer.active = true;
    addAdminAuditEntry(`Reactivated customer #${customerId} (${customer.customerName})`);
    persistCustomerState();
    showCustomerAlert(`Customer ${customerId} reactivated successfully.`, "success");
    getAllCustomers();
}

if (ensureAuth()) {
    applyRoleBadge();
    renderRoleHelp();
    renderAdminAudit();

    customerModal = new bootstrap.Modal(document.getElementById("customerModal"));
    document.getElementById("logoutBtn").addEventListener("click", logout);
    document.getElementById("addCustomerBtn").addEventListener("click", openAddCustomer);
    document.getElementById("getCustomerBtn").addEventListener("click", getCustomerById);
    document.getElementById("getAllCustomersBtn").addEventListener("click", getAllCustomers);

    document.getElementById("customerForm").addEventListener("submit", event => {
        event.preventDefault();
        hideCustomerAlert();

        const payload = {
            customerName: document.getElementById("customerName").value.trim(),
            email: document.getElementById("customerEmail").value.trim(),
            phoneNumber: document.getElementById("customerPhone").value.trim(),
            city: document.getElementById("customerCity").value.trim(),
            creditScore: Number(document.getElementById("creditScore").value)
        };

        if (!payload.customerName || !payload.email || !payload.phoneNumber || !payload.city || !payload.creditScore) {
            showCustomerAlert("Enter all required customer details.", "warning");
            return;
        }

        const newId = Math.max(...customersCache.map(c => c.customerId), 0) + 1;
        const created = { customerId: newId, ...payload, role: "USER", active: true };
        customersCache.push(created);
        addAdminAuditEntry(`Created customer #${newId} (${created.customerName})`);
        persistCustomerState();

        customerModal.hide();
        showCustomerAlert(`Customer created with ID ${newId}.`, "success");
        renderCustomers([created]);
        renderAdminAudit();
    });

    getAllCustomers();
}
