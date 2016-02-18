package spreadsheet;

import spreadsheet.api.CellLocation;
import spreadsheet.api.SpreadsheetInterface;
import spreadsheet.api.value.StringValue;
import spreadsheet.api.value.Value;

import java.util.*;

public class Spreadsheet implements SpreadsheetInterface {

    private HashMap<CellLocation,Cell>  cellswithexpressionsset = new HashMap<CellLocation,Cell>();
    private Set<Cell> needtoberecomputed = new HashSet<Cell>();


    // here containsKey uses deep equality checking if the fields(the string representation) of the locations
    // are the same, the strings slso being compared with deep equality?? i think so.
    // don't need if else test really
    // everytime an expression is set the celllocation with a new Cell of type StringValue is added to the hashmap
    // if that celllocation is already in the hashmap, then it's setExpression method is called
    public void setExpression(CellLocation location, String expression){
        if (!cellswithexpressionsset.containsKey(location)) {
            Cell cell = new Cell(this, location); // by default the value is a string value
            cell.setExpression(expression); // but then the cells setExpression method changes it to an invalid value
            //cell.setValue(new StringValue(expression));
            cellswithexpressionsset.put(location,cell);
        }
        else{
            cellswithexpressionsset.get(location).setExpression(expression);
            //cells.get(location).value=new StringValue(expression);
        }
    }


    // why can't we use a for loop???
    public void recompute(){

     Iterator<Cell> it = needtoberecomputed.iterator();
        while (it.hasNext()){
            Cell cell = it.next();

            String expr = cell.getExpression();
            cell.setValue(new StringValue(expr));
            //recomputeCell(cell);

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

    private void recomputeCell(Cell c){
        LinkedHashSet<Cell> cellsSeen = new LinkedHashSet<Cell>();
        cellsSeen.add(c);
        checkLoops(c,cellsSeen);
    }

    private void checkLoops(Cell c, LinkedHashSet<Cell> cellsSeen){

    }

    private void markAsLoop(Cell startcell, LinkedHashSet<Cell> cells){

    }





    public String getExpression(CellLocation location){
        // every location when created by the gui is given a correct string expression
        // , i.e. the current location which it is at, like "a4", "b2", etc. ;  by the gui itself.
        if(!cellswithexpressionsset.containsKey(location)) {
            return location.toString();
        }
        return cellswithexpressionsset.get(location).expression;
    }

    public Value getValue(CellLocation location){
        if (!cellswithexpressionsset.containsKey(location)) {
            return new StringValue(" ");
        }
        return cellswithexpressionsset.get(location).value;
    }

    public void addToNeedToBeRecomputed(Cell cell){
        needtoberecomputed.add(cell);
    }

    public boolean needsToBeRecomputedSet(Cell cell){
        return needtoberecomputed.contains(cell);
    }

    public Cell getCell(CellLocation cellLoc){
        return cellswithexpressionsset.get(cellLoc);
    }

}
