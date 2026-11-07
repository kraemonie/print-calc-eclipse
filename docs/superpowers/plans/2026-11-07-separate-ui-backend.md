# Separate UI and backend

Goal: preserve the current application while separating Java Swing design from business logic.

Architecture: PrintCalc is the Eclipse entry point and owns all backend logic. PrintCalcUI owns components, event wiring, receipt presentation, formatting, and startup theme settings. There are exactly two application source files.

- Extract business state and operations into PrintCalc; expose snapshots for UI rendering.
- Move the existing window into PrintCalcUI, replacing business operations with backend calls.
- Keep PrintCalc as the launch class and preserve the existing UI regression test interfaces.
- Compile all sources; run existing UI regression tests and backend-only tests for login, prices, orders, checkout, and stock.
- Commit all source changes together to GitHub so the repository remains buildable.
