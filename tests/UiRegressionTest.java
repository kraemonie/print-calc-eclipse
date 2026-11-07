package printcalc;
import javax.swing.*;
import java.awt.*;
import java.util.*;
public class UiRegressionTest {
 static class App extends PrintCalc {
  String error; double paid;
  void err(String message){error=message;}
  void showReceipt(String date,String time,double total,double cash,double change){paid=total;}
 }
 static java.util.List<Component> all(Container root){
  java.util.List<Component> out=new ArrayList<>();
  for(Component c:root.getComponents()){out.add(c);if(c instanceof Container)out.addAll(all((Container)c));}return out;
 }
 static JButton button(Container root,String text){return (JButton)all(root).stream().filter(c->c instanceof JButton && ((JButton)c).getText().equals(text)).findFirst().orElseThrow();}
 static void check(boolean value,String message){if(!value)throw new AssertionError(message);}
 public static void main(String[] args)throws Exception{
  SwingUtilities.invokeAndWait(()->{
   App app=new App();app.setVisible(true);
   try{
    JPanel root=(JPanel)app.getContentPane().getComponent(0);
    JPanel login=(JPanel)root.getComponent(0);
    java.util.List<Component> controls=all(login);
    JTextField user=(JTextField)controls.stream().filter(c->c instanceof JTextField && !(c instanceof JPasswordField)).findFirst().orElseThrow();
    JPasswordField password=(JPasswordField)controls.stream().filter(c->c instanceof JPasswordField).findFirst().orElseThrow();
    user.setText("wrong");password.setText("wrong");button(login,"LOGIN").doClick();
    JLabel error=(JLabel)controls.stream().filter(c->c instanceof JLabel && ((JLabel)c).getText().contains("Invalid username")).findFirst().orElseThrow();
    app.validate();check(error.isShowing()&&error.getHeight()>=30,"Login error must be visible");
    user.setText("cashier");password.setText("cashier123");button(login,"LOGIN").doClick();
    JPanel cashier=(JPanel)root.getComponent(2);
    java.util.List<Component> cc=all(cashier);
    java.util.Map<String,JToggleButton> choices=new HashMap<>();
    java.util.List<JTextField> fields=new ArrayList<>();
    java.util.List<JTable> tables=new ArrayList<>();
    for(Component c:cc){if(c instanceof JToggleButton)choices.put(c.getName(),(JToggleButton)c);if(c instanceof JTextField)fields.add((JTextField)c);if(c instanceof JTable)tables.add((JTable)c);}
    double[][] expected={{3,5,7},{5,7,10},{2,3,5},{5,6,8}};
    for(int service=0;service<4;service++)for(int paper=0;paper<3;paper++){
     check(PrintCalc.pricePerPage(service,paper)==expected[service][paper],"Price mismatch");
     choices.get("service"+service).doClick();choices.get("paper"+paper).doClick();
     check(choices.get("service"+service).isSelected()&&choices.get("paper"+paper).isSelected(),"Tapped cards must remain selected");
     fields.get(0).setText("2");button(cashier,"Add to order").doClick();
     String amount=tables.get(0).getValueAt(tables.get(0).getRowCount()-1,3).toString();
     check(amount.equals(String.format("Php %.2f",expected[service][paper]*2)),"Order subtotal mismatch: "+amount);
    }
    check(tables.get(0).getRowCount()==12,"All service and paper combinations added");
    fields.get(1).setText("500");button(cashier,"Complete payment").doClick();
    check(app.paid==132,"Checkout total should be Php 132");
    check(app.getPaperCount("Short")==492&&app.getPaperCount("A4")==492&&app.getPaperCount("Long")==492,"Paper deduction mismatch");
    check(tables.get(0).getRowCount()==0,"Order cleared after checkout");
    for(Component c:all(root)){String text=c instanceof JLabel?((JLabel)c).getText():c instanceof JButton?((JButton)c).getText():"";check(!text.toLowerCase().contains("ink"),"Ink control remains");}
    System.out.println("PASS: visible invalid login, 12 price combinations, order subtotals, checkout total, paper deduction, ink controls removed");
   }finally{app.dispose();}
  });
 }
}

