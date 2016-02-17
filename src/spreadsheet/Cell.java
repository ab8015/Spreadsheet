package spreadsheet;

import spreadsheet.api.CellLocation;
import spreadsheet.api.observer.Observer;
import spreadsheet.api.value.InvalidValue;
import spreadsheet.api.value.StringValue;
import spreadsheet.api.value.Value;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by ab8015 on 16/02/16.
 */
public class Cell implements Observer<Cell>{
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
        this.expression=expression;
        this.setValue(new InvalidValue(this.expression));

        update(this); // when a new expression is set then this and all dependent cells become invalid (for now)
    }

    public void setValue(Value value){
        this.value=value;
    }

    @Override
    public void update(Cell changed){
        this.setValue(new InvalidValue(expression));

        //for ()
    }



}
