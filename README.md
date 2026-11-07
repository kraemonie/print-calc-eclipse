# PrintCalc Eclipse

Run `src/printcalc/PrintCalc.java` as a Java application in Eclipse.

## Source files

- `PrintCalc.java`: application entry point and all backend logic: login, paper stock, pricing, orders, payments, sales, and CSV export.
- `PrintCalcUI.java`: Swing screens, layout, colors, buttons, receipt display, and UI events.

To change the appearance, edit `PrintCalcUI.java`. To change prices or business rules, edit `PrintCalc.java`.

## Compile and verify

Use JDK 25, matching the Eclipse project settings.

```sh
javac -d bin src/module-info.java src/printcalc/*.java
javac -cp bin -d bin tests/BackendTest.java tests/UiRegressionTest.java
java -cp bin printcalc.BackendTest
java -cp bin printcalc.UiRegressionTest
java -cp bin printcalc.PrintCalc
```

The UI test needs a desktop display. The backend test can run without one.

## Four OOP pillars in the backend

All service classes are nested inside `PrintCalc.java`, keeping the application in two files.

- **Encapsulation:** private stock, orders, sales, and immutable service names; public operations control changes, and snapshots protect stored data.
- **Abstraction:** `PrintService` defines `pricePerPage()` and shares the validated `costForPages()` operation.
- **Inheritance:** `BlackWhitePrinting`, `ColorPrinting`, `BlackWhiteXerox`, and `ColorXerox` extend `PrintService`.
- **Polymorphism:** service objects are stored as `PrintService` references. Pricing and order creation call the overridden method of the selected concrete service at runtime.

The UI calls the same backend API, so the screens and prices remain unchanged.
