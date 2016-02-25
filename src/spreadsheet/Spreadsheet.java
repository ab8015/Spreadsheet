package spreadsheet;

import spreadsheet.api.CellLocation;
import spreadsheet.api.ExpressionUtils;
import spreadsheet.api.SpreadsheetInterface;
import spreadsheet.api.value.*;

import java.util.*;

public class Spreadsheet implements SpreadsheetInterface {

    private HashMap<CellLocation,Cell>  cellswithexpressionsset = new HashMap<CellLocation,Cell>();
    private Set<Cell> needtoberecomputed = new HashSet<Cell>();
    LinkedList<Cell> toBeRemoved = new LinkedList<Cell>();


    public void setExpression(CellLocation location, String expression){
        if (!cellswithexpressionsset.containsKey(location)) {
            Cell cell = new Cell(this, location); // by default the value is a string value
            cell.setExpression(expression); // but then the cells setExpression method changes it to an invalid value
            cellswithexpressionsset.put(location,cell);
        }

        else{
            cellswithexpressionsset.get(location).setExpression(expression);
        }
    }


    public void recompute(){
     Iterator<Cell> itneedtoberecomputed = needtoberecomputed.iterator();

        while (itneedtoberecomputed.hasNext()){
            Cell cell = itneedtoberecomputed.next();
            recomputeCell(cell);

        }

     Iterator<Cell> ittoBeRemoved = toBeRemoved.iterator();
        while (ittoBeRemoved.hasNext()){
            Cell cellToBeRemoved = ittoBeRemoved.next();

            needtoberecomputed.remove(cellToBeRemoved);

        }

        toBeRemoved.clear();
    }

    private void recomputeCell(Cell c){

        LinkedList<Cell> seencells = new LinkedList<Cell>();
        checkLoops(c,seencells);

        //System.out.println("Cell at location: "  + c.cell_location + "
        // needs " +
        //        "to be removed is " + toBeRemoved.contains(c) );

        if (!toBeRemoved.contains(c)) {
            LinkedList<Cell> tobecomputedinorder = new LinkedList<Cell>();
            tobecomputedinorder.add(c);

            while (!tobecomputedinorder.isEmpty()){

                Cell currentcell = tobecomputedinorder.getFirst();

                for (Cell publishercell : currentcell.iobservethese){

                    if (!toBeRemoved.contains(publishercell)){
                        tobecomputedinorder.addFirst(publishercell);
                    }
                }
                // publisher cell is added to tobecomputedinorder
                // and then we will get the firstcell of tobecomputedinorder,
                // calculate it's value and take it out of toBeremoved, so in
                // this way toBeRemoved takes care of whether the cell needs
                // to be recomputed or not, remember we can't use
                // needstoberecomputed because the cells which have been
                // computed can't be taken out of needtoberecomputed
                // directly, so they will still be in needtoberecomputed

                Cell firstcell = tobecomputedinorder.getFirst();
                if (firstcell.allPublishersHaveBeenRecomputed()){
                    calculateCellValue(firstcell); // even if it has already
                    // been computed before(i.e. not in need to be recomputed),
                    // it will be computed again.

                    toBeRemoved.add(firstcell);
                    tobecomputedinorder.remove(firstcell);
                }
            }

        }
    }

    private void calculateCellValue(Cell cell){
        cell.setValue(ExpressionUtils.computeValue(cell.getExpression(),
                generateMapforDependents(cell)) );
    }


    private Map<CellLocation, Double> generateMapforDependents(Cell cell){
        Map <CellLocation,Double> dependentcellmap = new
                HashMap<CellLocation,Double>();

        ValueHolder vh = new ValueHolder(0d);

        for (Cell publishercell : cell.iobservethese){

            Double[] outervaluetoset = new Double[1];

            publishercell.getValue().visit(new ValueVisitor(){

                @Override
                public void visitString(String expression) {
                    //publishercell.realvalue=null;
                }

                @Override
                public void visitLoop() {
                    //publishercell.realvalue=null;
                    System.out.println("Its a LOOP. " +
                            "Shouldn't come here");
                }

                @Override
                public void visitDouble(double value, ValueHolder vh) {
                    vh.setValue(value);
                }

                @Override
                public void visitInvalid(String expression) {
                    //publishercell.realvalue=null;
                    System.out.println("Its an invalid string" +
                            "Shouldnt come here");
                }

            },vh);

            dependentcellmap.put(publishercell.cell_location,
                    vh.getDoubleValue());
        }

        return dependentcellmap;
    }


    private void checkLoops(Cell cell, LinkedList<Cell> seencells){

        // recursively calls itself so depth first search,

        if (seencells.contains(cell)){
            markAsLoop(cell, seencells);
            return;
        }

        else {
            seencells.add(cell);

            Iterator<Cell> itiObserveThese = cell.iobservethese.iterator();
            while (itiObserveThese.hasNext()){
                Cell dependentcell = itiObserveThese.next();
                checkLoops(dependentcell,seencells);
            }
            seencells.remove(cell);
        }
    }

    private void markAsLoop(Cell startcell, LinkedList<Cell> seencells){

        boolean reachedstartcell = false;

        Iterator<Cell> itSeenCells = seencells.iterator();
        while (itSeenCells.hasNext()){
            Cell currentcell = itSeenCells.next();

            if (currentcell == startcell){
                reachedstartcell = true;
            }

            if (reachedstartcell){
                currentcell.setValue(LoopValue.INSTANCE);
            }

            toBeRemoved.add(currentcell);
        }
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

    public boolean needsToBeRecomputedCheck(Cell cell){
        return needtoberecomputed.contains(cell);
    }

    public Cell getCell(CellLocation cellLoc){
        return cellswithexpressionsset.get(cellLoc);
    }

    public void addNewCellToHashMap(CellLocation cellLoc, Cell cell){
        cellswithexpressionsset.put(cellLoc, cell);
    }

}
