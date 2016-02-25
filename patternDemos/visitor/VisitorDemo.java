package visitor;

import spreadsheet.ValueHolder;
import spreadsheet.api.value.DoubleValue;
import spreadsheet.api.value.LoopValue;
import spreadsheet.api.value.StringValue;
import spreadsheet.api.value.Value;
import spreadsheet.api.value.ValueVisitor;

public class VisitorDemo {

    public static void main(String[] args) {
        Value stringValue = new StringValue("= 2 * 3");
        Value doubleValue = new DoubleValue(7);
        Value loopValue = LoopValue.INSTANCE;

        printMessage(stringValue);
        printMessage(doubleValue);
        printMessage(loopValue);
    }

    public static void printMessage(Value v) {
        /*
         * This method wants to print different things depending on the value
         * given.
         * 
         * Instead of using instanceof to see what kind of value we have, we can
         * use the visitor pattern. We call .visit(...) on the value, and pass
         * in an anonymous inner class, which is an instance of the interface
         * ValueVisitor. ValueVisitor has a method "visitBlah" for each Blah
         * that is an possible Value.
         * 
         * Anonymous inner classes are created with the syntax \\
         * "new Thingy(constructor args) { ... method definitions ... }" where
         * Thingy is an existing class/interface that you want to
         * extend/implement.
         */

        // we are overriding the valuevisitor methods of the new ValueVisitor() class
        // to do different things with different values
        // we can then pass in this value visitor to any value and
        // we will get the behaviour for that corresponding value

        v.visit(new ValueVisitor() {

            @Override
            public void visitString(String expression) {
                System.out.println("Its a string!");
            }

            @Override
            public void visitLoop() {
                System.out.println("Its a LOOP.");
            }

            @Override
            public void visitDouble(double value) {
                System.out.println("Its a double.");
            }

            @Override
            public void visitInvalid(String expression) {
                System.out.println("Its an invalid string");
            }

        });
    }
}
