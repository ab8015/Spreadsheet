package spreadsheet;

import spreadsheet.api.CellLocation;
import spreadsheet.api.ExpressionUtils;
import spreadsheet.api.observer.Observer;
import spreadsheet.api.observer.Subject;
import spreadsheet.api.value.InvalidValue;
import spreadsheet.api.value.StringValue;
import spreadsheet.api.value.Value;

import java.util.LinkedHashSet;
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
    LinkedHashSet<Cell> iobservethese = new LinkedHashSet<Cell>();
    LinkedHashSet<Observer<Cell>> theseobserveme = new LinkedHashSet<Observer<Cell>>();

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
        iobservethese.clear(); // forget the cells this cell was depending upon

        //2
        this.expression=expression;
        setValue(new InvalidValue(this.expression));
        addToNeedtoBeRecomputed();

        //3 subscribing to new cells in the expression
        // there was a bug when i was making a newCell everytime i had a cell location, instead of
        // getting the corresponding cell from the spreadsheet hashmap of cells with expressions set

        Set<CellLocation> newiobservetheselocs = ExpressionUtils.getReferencedLocations(expression);
        for (CellLocation cl : newiobservetheselocs){
            Cell correspondingCell = spreadsheet.getCell(cl);

            iobservethese.add(correspondingCell); // remember new dependent cells
            //System.out.println("Cell " + this.cell_location + " is now observing " + "Cell " + newcell.cell_location);
            //System.out.println(this.cell_location + " is now observing " + this.iobservethese.size() + " cells");
            //System.out.println();

            correspondingCell.theseobserveme.add(this); // subscribe to new cells
            //System.out.println("Cell " + newcell.cell_location + " is now being observed by " + "Cell " +
            //                                                                                this.cell_location);
            //System.out.println(newcell.cell_location + " is now being observed by " + newcell.theseobserveme.size());
            //System.out.println();

        }

        //System.out.println("Number of observers of " + this.cell_location +
        //                                             " to be updated is: " + theseobserveme.size());
        System.out.println();
        //4 inform all of the observers that this has changed
        // here 'inform' means for all the observers of the cell (and also their observers), update method is invoked
        this.notifyObservers();

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

    // remember cell location is a class which contains a
    // string field representing its representation
    private Cell convertLocationToCell(CellLocation cellLoc){
        return new Cell(spreadsheet,cellLoc); // it will make a new cell with a string
        // by default its expression is the representation of cellLoc
        // and its value is a string value depending on its expression
    }

    // observer method override
    //sets the value of this cell and all it's observers to invalid
    // i wonder why it needs to take a cell as an argument?
    @Override
    public void update(Cell changed){

            this.addToNeedtoBeRecomputed();
            this.setValue(new InvalidValue(expression));
            //System.out.println("Cell" + this.cell_location +
            //                            "with expression: " + this.expression + "was made invalid");
            //System.out.println();
            this.notifyObservers();

    }

    // subject method override
    @Override
    public void notifyObservers(){
        for (Observer<Cell> observer : theseobserveme) {
            observer.update(this);
        }
    }


}
