package spreadsheet.api.value;

import spreadsheet.ValueHolder;

/**
 * A value for spreadsheet cells that are currently invalid.
 * 
 */
public final class InvalidValue implements Value {

    private final String expression;

    public InvalidValue(String expression) {
        this.expression = expression;
    }

    @Override
    public void visit(ValueVisitor visitor, ValueHolder vh) {
        visitor.visitInvalid(expression);
    }

    public String toString() {
        return "{" + expression + "}";
    }

}
