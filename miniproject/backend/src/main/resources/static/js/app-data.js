const APP_DATA_KEY = "lendora-static-data-v1";

const DEFAULT_APP_DATA = {
    customers: [
        { customerId: 1, customerName: "Renjitha", email: "renjitha@gmail.com", phoneNumber: "9876543210", city: "Chennai", creditScore: 780, role: "USER", active: true },
        { customerId: 2, customerName: "John Doe", email: "john@example.com", phoneNumber: "9876543211", city: "Mumbai", creditScore: 820, role: "USER", active: true },
        { customerId: 3, customerName: "Jane Smith", email: "jane@example.com", phoneNumber: "9876543212", city: "Bangalore", creditScore: 750, role: "USER", active: true },
        { customerId: 4, customerName: "Rajesh Kumar", email: "rajesh@example.com", phoneNumber: "9876543213", city: "Delhi", creditScore: 695, role: "USER", active: true },
        { customerId: 5, customerName: "Priya Sharma", email: "priya@example.com", phoneNumber: "9876543214", city: "Chennai", creditScore: 810, role: "USER", active: true }
    ],
    loans: [
        { loanId: 1, customerName: "Renjitha", customerId: 1, loanType: "PERSONAL", principalAmount: 500000, annualInterestRate: 12, tenureMonths: 24, emiAmount: 23536, loanStatus: "ACTIVE" },
        { loanId: 2, customerName: "John Doe", customerId: 2, loanType: "VEHICLE", principalAmount: 800000, annualInterestRate: 10.5, tenureMonths: 60, emiAmount: 16200, loanStatus: "ACTIVE" },
        { loanId: 3, customerName: "Jane Smith", customerId: 3, loanType: "EDUCATION", principalAmount: 300000, annualInterestRate: 7.5, tenureMonths: 120, emiAmount: 3450, loanStatus: "ACTIVE" },
        { loanId: 4, customerName: "Rajesh Kumar", customerId: 4, loanType: "BUSINESS", principalAmount: 2000000, annualInterestRate: 13, tenureMonths: 36, emiAmount: 62000, loanStatus: "CLOSED" },
        { loanId: 5, customerName: "Priya Sharma", customerId: 5, loanType: "PERSONAL", principalAmount: 400000, annualInterestRate: 11.5, tenureMonths: 24, emiAmount: 18800, loanStatus: "ACTIVE" },
        { loanId: 6, customerName: "Renjitha", customerId: 1, loanType: "EDUCATION", principalAmount: 250000, annualInterestRate: 9.25, tenureMonths: 48, emiAmount: 6227, loanStatus: "ACTIVE" },
        { loanId: 7, customerName: "John Doe", customerId: 2, loanType: "PERSONAL", principalAmount: 350000, annualInterestRate: 11.2, tenureMonths: 36, emiAmount: 11491, loanStatus: "ACTIVE" }
    ],
    loanOwners: {
        1: "renjitha@gmail.com",
        2: "john@example.com",
        3: "jane@example.com",
        4: "rajesh@example.com",
        5: "priya@example.com",
        6: "renjitha@gmail.com",
        7: "john@example.com"
    },
    emiSchedules: {
        1: [
            { emiId: 101, loanId: 1, installmentNumber: 1, dueDate: "2024-08-15", amountDue: 23536.74, principalComponent: 18536.74, interestComponent: 5000, amountPaid: 23536.74, paymentDate: "2024-08-14", status: "PAID", penaltyAmount: 0, daysPastDue: 0 },
            { emiId: 102, loanId: 1, installmentNumber: 2, dueDate: "2024-09-15", amountDue: 23536.74, principalComponent: 18700, interestComponent: 4836.74, amountPaid: 0, paymentDate: null, status: "PENDING", penaltyAmount: 0, daysPastDue: 0 },
            { emiId: 103, loanId: 1, installmentNumber: 3, dueDate: "2024-10-15", amountDue: 23536.74, principalComponent: 18867, interestComponent: 4669.74, amountPaid: 0, paymentDate: null, status: "PENDING", penaltyAmount: 0, daysPastDue: 0 }
        ],
        2: [
            { emiId: 201, loanId: 2, installmentNumber: 1, dueDate: "2024-08-20", amountDue: 16200, principalComponent: 10200, interestComponent: 6000, amountPaid: 16200, paymentDate: "2024-08-18", status: "PAID", penaltyAmount: 0, daysPastDue: 0 },
            { emiId: 202, loanId: 2, installmentNumber: 2, dueDate: "2024-09-20", amountDue: 16200, principalComponent: 10350, interestComponent: 5850, amountPaid: 0, paymentDate: null, status: "OVERDUE", penaltyAmount: 2100, daysPastDue: 10 },
            { emiId: 203, loanId: 2, installmentNumber: 3, dueDate: "2024-10-20", amountDue: 16200, principalComponent: 10505, interestComponent: 5695, amountPaid: 0, paymentDate: null, status: "PENDING", penaltyAmount: 0, daysPastDue: 0 }
        ],
        3: [
            { emiId: 301, loanId: 3, installmentNumber: 1, dueDate: "2024-08-30", amountDue: 3450, principalComponent: 2000, interestComponent: 1450, amountPaid: 3450, paymentDate: "2024-08-28", status: "PAID", penaltyAmount: 0, daysPastDue: 0 }
        ],
        6: [
            { emiId: 601, loanId: 6, installmentNumber: 1, dueDate: "2024-08-10", amountDue: 6227, principalComponent: 4300, interestComponent: 1927, amountPaid: 6227, paymentDate: "2024-08-09", status: "PAID", penaltyAmount: 0, daysPastDue: 0 },
            { emiId: 602, loanId: 6, installmentNumber: 2, dueDate: "2024-09-10", amountDue: 6227, principalComponent: 4333, interestComponent: 1894, amountPaid: 0, paymentDate: null, status: "PENDING", penaltyAmount: 0, daysPastDue: 0 },
            { emiId: 603, loanId: 6, installmentNumber: 3, dueDate: "2024-10-10", amountDue: 6227, principalComponent: 4367, interestComponent: 1860, amountPaid: 0, paymentDate: null, status: "PENDING", penaltyAmount: 0, daysPastDue: 0 }
        ],
        7: [
            { emiId: 701, loanId: 7, installmentNumber: 1, dueDate: "2024-08-12", amountDue: 11491, principalComponent: 8200, interestComponent: 3291, amountPaid: 11491, paymentDate: "2024-08-12", status: "PAID", penaltyAmount: 0, daysPastDue: 0 },
            { emiId: 702, loanId: 7, installmentNumber: 2, dueDate: "2024-09-12", amountDue: 11491, principalComponent: 8276, interestComponent: 3215, amountPaid: 0, paymentDate: null, status: "PENDING", penaltyAmount: 0, daysPastDue: 0 },
            { emiId: 703, loanId: 7, installmentNumber: 3, dueDate: "2024-10-12", amountDue: 11491, principalComponent: 8354, interestComponent: 3137, amountPaid: 0, paymentDate: null, status: "PENDING", penaltyAmount: 0, daysPastDue: 0 }
        ]
    },
    adminAuditLog: []
};

function cloneData(value) {
    return JSON.parse(JSON.stringify(value));
}

function readData() {
    try {
        const raw = localStorage.getItem(APP_DATA_KEY);
        if (!raw) {
            const initial = cloneData(DEFAULT_APP_DATA);
            localStorage.setItem(APP_DATA_KEY, JSON.stringify(initial));
            return initial;
        }
        const parsed = JSON.parse(raw);
        const merged = {
            ...cloneData(DEFAULT_APP_DATA),
            ...parsed
        };
        if (!Array.isArray(merged.customers)) merged.customers = cloneData(DEFAULT_APP_DATA.customers);
        if (!Array.isArray(merged.loans)) merged.loans = cloneData(DEFAULT_APP_DATA.loans);
        if (!merged.loanOwners || typeof merged.loanOwners !== "object") merged.loanOwners = cloneData(DEFAULT_APP_DATA.loanOwners);
        if (!merged.emiSchedules || typeof merged.emiSchedules !== "object") merged.emiSchedules = cloneData(DEFAULT_APP_DATA.emiSchedules);
        if (!Array.isArray(merged.adminAuditLog)) merged.adminAuditLog = [];
        return merged;
    } catch (error) {
        const fallback = cloneData(DEFAULT_APP_DATA);
        localStorage.setItem(APP_DATA_KEY, JSON.stringify(fallback));
        return fallback;
    }
}

function writeData(data) {
    localStorage.setItem(APP_DATA_KEY, JSON.stringify(data));
}

function resetData() {
    const reset = cloneData(DEFAULT_APP_DATA);
    writeData(reset);
    return reset;
}

window.AppData = {
    readData,
    writeData,
    resetData,
    cloneData
};
