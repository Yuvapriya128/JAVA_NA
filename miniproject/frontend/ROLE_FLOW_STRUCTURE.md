# LoanHub Production Frontend Redesign Blueprint

**Context**

- Frontend: Angular 20 standalone
- Backend: Spring Boot
- Constraints:
  - Do **not** change backend APIs
  - Do **not** change database schema
  - Do **not** change authentication flow
- Objective: enterprise-grade banking/lending UX like HDFC/ICICI/Bajaj/Tata Capital/Northern Arc/LendingKart/CredAvenue/Axis IB

---

## 1. New Information Architecture

Use a compact, domain-driven IA with role-aware visibility:

1. **Dashboard**
2. **Loans**
3. **Applications**
4. **Customers**
5. **Payments**
6. **Reports**
7. **Administration**
8. **Profile**

### Why each menu belongs

- **Dashboard**: single command center per role; first screen after login.
- **Loans**: portfolio + lifecycle + loan-level operations in one place.
- **Applications**: onboarding/review/disposition pipeline separated from active loans.
- **Customers**: customer 360, relationship history, servicing actions.
- **Payments**: EMI dues, collections, receipts, reconciliation.
- **Reports**: operational and risk analytics exports.
- **Administration**: policies, rate controls, users, audit.
- **Profile**: self settings, password, preferences, help/support links.

---

## 2. Navigation Map

## Global shell
- Top app bar (brand, global search for staff, notifications, profile)
- Left rail/sidebar (Manager/Admin), bottom nav (User mobile)
- Context header per screen (title + KPI chips + primary action)

## Role-wise primary nav

## USER
- Dashboard
- Loans
- Applications
- Pay
- Profile

## MANAGER
- Dashboard
- Applications
- Loans
- Customers
- Payments
- Reports
- Profile

## ADMIN
- Dashboard
- Applications
- Loans
- Customers
- Payments
- Reports
- Administration
- Profile

---

## 3. Screen Hierarchy

## Dashboard
- Role KPI cards
- Alerts/risk widgets
- Timeline/queue widgets
- Quick actions rail

## Loans
- Loans List
- Loan Details (tabbed):
  - Overview
  - EMI Schedule
  - Payments
  - Documents
  - History

## Applications
- Application Queue (manager/admin)
- My Applications (user)
- Application Detail:
  - Applicant snapshot
  - Product/eligibility
  - Documents
  - Decision timeline
  - Approval drawer actions

## Customers
- Customers List
- Customer Detail tabs:
  - Overview
  - Loans
  - Applications
  - Payments
  - Notifications
  - Audit

## Payments
- Pay EMI wizard (user)
- Collections/Receipts list (staff)
- Receipt detail + download

## Reports
- Portfolio, collections, delinquencies, conversion dashboards
- Export center

## Administration
- Interest Policies
- User Management
- Audit Logs
- Branch/organization settings

## Profile
- Account info
- Password/security
- Preferences
- Help/Support

---

## 4. Dashboard Wireflow

## USER dashboard wireflow
1. KPI strip: Total Outstanding, EMI Due, Credit Score, Active Loans
2. Recent payments card + mini chart
3. Loan progress/timeline widget
4. Upcoming EMI card with CTA
5. Quick actions:
   - Apply Loan
   - Pay EMI
   - Track Application
   - Download Statement
   - View Schedule
   - Support

## MANAGER dashboard wireflow
1. KPI strip: Pending Applications, Today Collections, Loans Created, Overdue EMIs
2. Approval queue widget
3. Risk alerts panel
4. Collection trend chart
5. Quick actions:
   - Approve
   - Reject
   - Create Loan
   - Customer Search
   - Collection Dashboard

## ADMIN dashboard wireflow
1. KPI strip: Total Customers, Total Loans, Revenue, NPA %, Collection %
2. Risk heatmap + branch performance
3. Audit log stream
4. Interest policy impact card
5. User management highlights

---

## 5. User Flow (target)

## Guided borrowing + servicing flow
1. **Apply Loan**
2. **Eligibility**
3. **Choose Product**
4. **Review**
5. **Submit**
6. **Application Timeline**
7. **Approval**
8. **Loan Created**
9. **EMI Schedule**
10. **Payments**

### UX pattern
- Stepper at top
- Save draft on every step
- Inline validation
- Summary sidebar on desktop / collapsible summary on mobile

---

## 6. Manager Flow (target)

1. Open **Applications Queue**
2. Select application -> side preview
3. Open **Approval Drawer**
4. Actions:
   - Approve
   - Reject (mandatory reason)
   - Need More Documents
   - Send Back
5. Timeline auto-updates
6. If approved -> guided Create Loan prefilled from application
7. Post-creation -> linked EMI schedule + customer timeline update

---

## 7. Admin Flow (target)

1. Monitor enterprise KPIs and risk widgets
2. Configure interest policies and controls
3. Monitor collections and NPA trends
4. Manage users/roles/access
5. Audit all critical changes through logs and trace views

---

## 8. Component Tree (proposed)

## Core layout
- `AppShellComponent`
  - `TopNavComponent`
  - `SideNavComponent`
  - `ContextHeaderComponent`
  - `NotificationPanelComponent`
  - `RouterOutlet`

## Domain screens
- `DashboardPageComponent`
  - `KpiStripComponent`
  - `QuickActionsPanelComponent`
  - `RoleInsightsWidgetComponent`
- `LoansPageComponent`
  - `LoanTableComponent`
  - `LoanPreviewDrawerComponent`
- `LoanDetailPageComponent`
  - `LoanOverviewTabComponent`
  - `LoanScheduleTabComponent`
  - `LoanPaymentsTabComponent`
  - `LoanDocumentsTabComponent`
  - `LoanHistoryTabComponent`
- `ApplicationsPageComponent`
  - `ApplicationBoardComponent`
  - `ApplicationTimelineComponent`
  - `ApprovalDrawerComponent`
- `PaymentsPageComponent`
  - `EmiPaymentWizardComponent`
  - `PendingEmiCardsComponent`
  - `PaymentSuccessComponent`
  - `ReceiptViewerComponent`
- `CustomersPageComponent`
  - `CustomerTableComponent`
  - `CustomerDetailTabsComponent`

---

## 9. Folder Structure (Angular standalone, feature-first)

```text
src/app/
  core/
    layout/
      app-shell/
      top-nav/
      side-nav/
      context-header/
    guards/
    interceptors/
    services/
    models/
  shared/
    ui/
      summary-card/
      stat-card/
      status-chip/
      timeline/
      stepper/
      approval-drawer/
      confirmation-dialog/
      search-toolbar/
      filter-panel/
      table-toolbar/
      empty-state/
      loading-state/
      error-state/
      notification-panel/
    pipes/
    directives/
    utils/
  features/
    dashboard/
      pages/
      components/
      services/
      models/
    loans/
      pages/
      components/
      services/
      models/
    applications/
      pages/
      components/
      services/
      models/
    payments/
      pages/
      components/
      services/
      models/
    customers/
      pages/
      components/
      services/
      models/
    reports/
      pages/
      components/
      services/
    administration/
      pages/
      components/
      services/
    profile/
      pages/
      components/
```

---

## 10. UI Improvements

1. Angular Material + enterprise dashboard tokens
2. Consistent card system (elevation, radius, spacing scale)
3. Stepper-based guided forms
4. Timeline-based application tracking (icons + color states)
5. Drawer-based preview and approval actions
6. Rich tables:
   - sticky header
   - column chooser
   - saved filters
   - status chips
   - inline row actions
   - export
7. Skeleton loaders and optimistic transitions
8. Success/failure completion pages for critical actions (apply/pay/approve)

---

## 11. Mobile Improvements

## USER mobile
- Bottom nav: Dashboard / Loans / Pay / Applications / Profile
- EMI pay flow as full-screen wizard cards
- Sticky action footer for critical submit actions

## MANAGER/ADMIN mobile/tablet
- Collapsible sidebar + compact action rail
- Data tables shift to card-list with key-value rows
- Drawer becomes full-screen modal sheet

---

## 12. Migration Plan (no backend/API/auth/schema changes)

## Phase 1: Shell + IA foundation
1. Build new app shell (top nav, side nav, context header)
2. Introduce route grouping by feature
3. Keep existing APIs/services intact

## Phase 2: Role dashboards
1. Replace current dashboard with role widgets
2. Add quick actions and role-specific KPI cards

## Phase 3: Applications + approvals
1. Separate Applications experience from Loan Products catalog
2. Implement timeline + approval drawer
3. Remove prompt-based interactions

## Phase 4: Loan and customer 360
1. Tabbed loan details
2. Tabbed customer details
3. Drawer previews from list screens

## Phase 5: Payments modernization
1. Finalize EMI card-based selection
2. Confirm/receipt success flow
3. Harden retry/idempotency UX on frontend

## Phase 6: Reporting + administration
1. Replace placeholders with operational pages
2. Add audit and policy management UX

## Phase 7: Mobile optimization
1. USER bottom nav
2. Responsive staff shell
3. Final responsive QA pass

---

## Reusable components checklist

- Summary cards
- Stat cards
- Timeline
- Stepper
- Approval drawer
- Confirmation dialog
- Search toolbar
- Filter panel
- Table toolbar
- Status chip
- Empty state
- Loading state
- Error state
- Notification panel

