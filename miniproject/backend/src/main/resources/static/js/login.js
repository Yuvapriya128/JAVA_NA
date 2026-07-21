const loginForm = document.getElementById("loginForm");
const loginAlert = document.getElementById("loginAlert");
const loginBtn = document.getElementById("loginBtn");

// Demo credentials - static login (no backend)
const DEMO_USERS = [
    { email: "admin@example.com", password: "Password@123", name: "Admin User", role: "ADMIN" },
    { email: "manager@example.com", password: "Manager@123", name: "Manager User", role: "MANAGER" },
    { email: "renjitha@gmail.com", password: "Password@123", name: "Renjitha", role: "USER" },
    { email: "john@example.com", password: "John@123", name: "John Doe", role: "USER" },
    { email: "priya@example.com", password: "Priya@123", name: "Priya Sharma", role: "USER" }
];

function getLandingPage(role) {
    if (role === "ADMIN" || role === "MANAGER") {
        return "dashboard.html";
    }
    return "loans.html";
}

function showLoginAlert(message, type = "danger") {
    if (!loginAlert) return;
    loginAlert.className = `alert alert-${type}`;
    loginAlert.textContent = message;
    loginAlert.classList.remove("d-none");
}

function hideLoginAlert() {
    if (loginAlert) loginAlert.classList.add("d-none");
}

function login(event) {
    event.preventDefault();
    hideLoginAlert();

    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value;

    if (!email || !password) {
        showLoginAlert("Please enter email and password.");
        return;
    }

    loginBtn.disabled = true;
    loginBtn.textContent = "Signing in...";

    // Simulate delay
    setTimeout(() => {
        const user = DEMO_USERS.find(u => u.email === email && u.password === password);
        
        if (!user) {
            showLoginAlert("Invalid email or password.");
            loginBtn.disabled = false;
            loginBtn.innerHTML = '<i class="fa-solid fa-right-to-bracket me-1"></i>Login';
            return;
        }

        // Store dummy JWT token
        const dummyToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        localStorage.setItem("jwtToken", dummyToken);
        localStorage.setItem("userName", user.name);
        localStorage.setItem("userRole", user.role);
        localStorage.setItem("userEmail", user.email);
        
        showLoginAlert("Login successful!", "success");
        setTimeout(() => {
            window.location.href = getLandingPage(user.role);
        }, 800);
    }, 1200);
}

if (localStorage.getItem("jwtToken")) {
    const role = localStorage.getItem("userRole") || "USER";
    window.location.href = getLandingPage(role);
}

if (loginForm) {
    loginForm.addEventListener("submit", login);
}
