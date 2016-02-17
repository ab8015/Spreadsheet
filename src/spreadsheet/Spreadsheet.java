package spreadsheet;

import spreadsheet.api.CellLocation;
import spreadsheet.api.SpreadsheetInterface;
import spreadsheet.api.value.StringValue;
import spreadsheet.api.value.Value;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Spreadsheet implements SpreadsheetInterface {

    private HashMap<CellLocation,Cell>  cells = new HashMap<CellLocation,Cell>();
    private Set<Cell> needtoberecomputed = new HashSet<Cell>();


    // here containsKey uses deep equality checking if the fields(the string representation) of the locations
    // are the same, the strings slso being compared with deep equality??
    // don't need if else test really
    public void setExpression(CellLocation location, String expression){
        if (!cells.containsKey(location)) {
            Cell cell = new Cell(this, location); // by default the value is a string value
            cell.setExpression(expression);
            //cell.setValue(new StringValue(expression));
            cells.put(location,cell);
        }
        else{
            cells.get(location).setExpression(expression);
            //cells.get(location).value=new StringValue(expression);
        }
    }


    public void recompute(){

     Iterator<Cell> it = needtoberecomputed.iterator();
        while (it.hasNext()){
            Cell cell = it.next();

            String expr = cell.getExpression();
            cell.setValue(new StringValue(expr));

            it.remove();
        }

     // this doesn't work because of the way the list is set, don't fully understand why..
     /*
     for (Cell cell : needtoberecomputed){
         String expr = cell.getExpression();
         cell.setValue(new StringValue(expr));
         needtoberecomputed.remove(cell);
     }
     */
    }

    public String getExpression(CellLocation location){
        // every location when created by the gui is given a correct string expression
        // , i.e. the current location which it is at, like "a4", "b2", etc. ;  by the gui itself.
        if(!cells.containsKey(location)) {
            return location.toString();
        }
        return cells.get(location).expression;
    }

    public Value getValue(CellLocation location){
        if (!cells.containsKey(location)) {
            return new StringValue(" ");
        }
        return cells.get(location).value;
    }

    public void addToNeedToBeRecomputed(Cell cell){
        needtoberecomputed.add(cell);
    }

    public boolean needsToBeRecomputedSet(Cell cell){
        return needtoberecomputed.contains(cell);
    }

    // remember cell location is a class which contains a
    // string field representing its representation
    public Cell convertLocationToCell(CellLocation cellLoc){
        return new Cell(this,cellLoc); // it will make a new cell with a string
                                       // by default its expression is the representation of cellLoc
                                       // and its value is a string value depending on its expression
    }
}
