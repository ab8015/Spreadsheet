package spreadsheet;

import spreadsheet.api.CellLocation;
import spreadsheet.api.value.StringValue;
import spreadsheet.api.value.Value;

/**
 * Created by ab8015 on 16/02/16.
 */
public class Cell{
    final Spreadsheet spreadsheet;
    final CellLocation cell_location;
    String expression;
    Value value;

    public Cell (Spreadsheet spreadsheet, CellLocation cell_location){
        this.spreadsheet= spreadsheet;
        this.cell_location= cell_location;
        this.expression=this.cell_location.toString();
        this.value=new StringValue(expression);
    }

    public void setExpression(String expression){
        this.expression=expression;
    }

    public void setValue(Value value){
        this.value=value;
    }
}
