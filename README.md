# PrintCalc Eclipse

Run `src/printcalc/PrintCalc.java` as a Java application in Eclipse.

## Source files

- `PrintCalc.java`: application entry point and startup theme settings.
- `PrintCalcUI.java`: Swing screens, layout, colors, buttons, receipt display, and UI events.
- `PrintCalcBackend.java`: login, paper stock, pricing, orders, payments, sales records, and CSV export. Does not depend on Swing.

To change the appearance, edit `PrintCalcUI.java`. To change prices or business rules, edit `PrintCalcBackend.java`.

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
