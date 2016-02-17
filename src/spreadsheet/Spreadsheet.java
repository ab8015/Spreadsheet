package spreadsheet;

import spreadsheet.api.CellLocation;
import spreadsheet.api.SpreadsheetInterface;
import spreadsheet.api.value.StringValue;
import spreadsheet.api.value.Value;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Spreadsheet implements SpreadsheetInterface {

    private HashMap<CellLocation,Cell>  cells = new HashMap<CellLocation,Cell>();
    private Set<Cell> needtobeupdated = new HashSet<Cell>();

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
    }

    public String getExpression(CellLocation location){
        // every location when created by the gui is given a correct string expression
        // , i.e. the current location which it is at like "a4", "b2", etc. ;  by the gui itself.
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
}
