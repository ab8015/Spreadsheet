package spreadsheet.api.value;

import spreadsheet.ValueHolder;

/**
 * Interface representing the values that a particular spreadsheet cell can
 * take.
 */
public interface Value {

    public void visit(ValueVisitor visitor, ValueHolder vh);

}
