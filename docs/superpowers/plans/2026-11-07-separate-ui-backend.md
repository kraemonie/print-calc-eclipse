# Separate UI and backend

Goal: preserve the current application while separating Java Swing design from business logic.

Architecture: PrintCalc remains the Eclipse entry point and extends PrintCalcUI. PrintCalcUI owns components, event wiring, receipt presentation, and formatting. PrintCalcBackend owns credentials, paper stock, pricing, orders, checkout, sales, and CSV output, without Swing dependencies.

- Extract business state and operations into PrintCalcBackend; expose snapshots for UI rendering.
- Move the existing window into PrintCalcUI, replacing business operations with backend calls.
- Preserve PrintCalc as the launch class and preserve the existing UI regression test interfaces.
- Compile all sources; run existing UI regression tests and backend-only tests for login, prices, orders, checkout, and stock.
- Commit all source changes together to GitHub so the repository remains buildable.
