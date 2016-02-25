package spreadsheet;

import spreadsheet.api.CellLocation;
import spreadsheet.api.ExpressionUtils;
import spreadsheet.api.observer.Observer;
import spreadsheet.api.observer.Subject;
import spreadsheet.api.value.InvalidValue;
import spreadsheet.api.value.StringValue;
import spreadsheet.api.value.Value;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
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
    Double realvalue;
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
            Cell correspondingCell;

            if (spreadsheet.getCell(cl) != null) {
                correspondingCell = spreadsheet.getCell(cl);
            }

            else {
                correspondingCell = new Cell(spreadsheet, cl); // here a new
                // cell is being created with default string value equal to
                // expression and its expression is equal to the cell_location
                // the expression is used by computeValue in expressionUtils
                // to get the correct value
                spreadsheet.addNewCellToHashMap(cl,correspondingCell);
            }

            // what happens when adding to a cell whose expression in the
            // spreadsheet has not yet been set

            iobservethese.add(correspondingCell); // remember new dependent cells

            correspondingCell.theseobserveme.add(this); // subscribe to new cells

        }

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
        return spreadsheet.needsToBeRecomputedCheck(this);

    }

    private void removeObserver(Observer<Cell> observer){
        theseobserveme.remove(observer);
    }

    public boolean allPublishersHaveBeenRecomputed(){
        for (Cell publishercell: iobservethese){
            if (!spreadsheet.toBeRemoved.contains(publishercell)){
                return false;
            }
        }
        return true;
    }
    // observer method override
    //sets the value of this cell and all it's observers to invalid
    // i wonder why it needs to take a cell as an argument?
    @Override
    public void update(Cell changed){
            this.addToNeedtoBeRecomputed();
            this.setValue(new InvalidValue(expression));
            this.notifyObservers();

    }

    // subject method override
    @Override
    public void notifyObservers(){
        for (Observer<Cell> observer : theseobserveme) {
            Cell observercell = (Cell) observer;

            if (!observercell.needsToBeRecomputed()){
                observercell.update(this);
            }
        }

    }

}
