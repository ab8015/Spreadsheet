package spreadsheet.api.value;

import spreadsheet.ValueHolder;

/**
 * A value for spreadsheet cells that represented an expression that evaluated
 * to a double.
 * 
 */
public final class DoubleValue implements Value {

    private final double value;

    public DoubleValue(double d) {
        this.value = d;
    }

    // for example when we call the visit method of a double value, we will invoke the visitDouble method
    // of the valuevisitor argument which was passed in and do a similar thing for other values
    @Override
    public void visit(ValueVisitor visitor, ValueHolder vh) {
        visitor.visitDouble(value,vh);
    }

    @Override
    public String toString() {
        return Double.toString(value);
    }

}
