package spreadsheet;

import spreadsheet.api.CellLocation;
import spreadsheet.api.ExpressionUtils;
import spreadsheet.api.observer.Observer;
import spreadsheet.api.observer.Subject;
import spreadsheet.api.value.InvalidValue;
import spreadsheet.api.value.StringValue;
import spreadsheet.api.value.Value;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by ab8015 on 16/02/16.
 */

// subscribing to a cell means that you become an observer of that cell
// unsubscribing from a cell means that you are no longer an observer of that cell

public class Cell implements Observer<Cell>, Subject {
    final Spreadsheet spreadsheet;
    final CellLocation cell_location;
    String expression;
    Value value;
    Set<Cell> iobservethese = new HashSet<Cell>();
    Set<Observer<Cell>> theseobserveme = new HashSet<Observer<Cell>>();

    public Cell (Spreadsheet spreadsheet, CellLocation cell_location){
        this.spreadsheet= spreadsheet;
        this.cell_location= cell_location;
        this.expression=this.cell_location.toString();
        this.value=new StringValue(expression);
    }

    public String getExpression(){
        return expression;
    }

    public Value getValue(){
        return value;
    }

    public void setExpression(String expression){

        // 1
        // unsubscribe from the cells it was depending on
        for (Cell cell : iobservethese){
            cell.removeObserver(this);
        }
        iobservethese.clear(); // forget the cells b1 was depending upon

        //2
        this.expression=expression;
        setValue(new InvalidValue(this.expression));
        addToNeedtoBeRecomputed();

        //3 subscribing to new cells in the expression
        Set<CellLocation> newiobservetheselocs = ExpressionUtils.getReferencedLocations(expression);
        for (CellLocation cl : newiobservetheselocs){
            Cell newcell = spreadsheet.convertLocationToCell(cl);

            iobservethese.add(newcell); // remember new dependent cells
            newcell.theseobserveme.add(this); // subscribe to new cells
        }

        //4 inform all of the observers that this has changed
        // here 'inform' means all the obse
        notifyObservers();

        //update(this); // when a new expression is set then this and all dependent cells become invalid (for now)
    }

    public void setValue(Value value){
        this.value=value;
    }

    public void addToNeedtoBeRecomputed(){
        spreadsheet.addToNeedToBeRecomputed(this);
    }

    public boolean needsToBeRecomputed(){
        return spreadsheet.needsToBeRecomputedSet(this);
    }

    private void removeObserver(Observer<Cell> observer){
        theseobserveme.remove(observer);
    }

    // observer method override
    @Override
    public void update(Cell changed){
        if (!needsToBeRecomputed()) {
            addToNeedtoBeRecomputed();
            this.setValue(new InvalidValue(expression));
        }
    }

    // subject method override
    @Override
    public void notifyObservers(){
        for (Observer<Cell> observer : theseobserveme) {
            observer.update(this);
        }
    }


}
