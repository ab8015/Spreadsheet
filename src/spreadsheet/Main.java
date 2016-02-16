package spreadsheet;
import spreadsheet.gui.SpreadsheetGUI;

public class Main {

    private static final int DEFAULT_NUM_ROWS = 5000;
    private static final int DEFAULT_NUM_COLUMNS = 5000;

    public static void main(String[] args) {

        Spreadsheet spreadsheet = new Spreadsheet();
        SpreadsheetGUI gui = new SpreadsheetGUI(spreadsheet,10,10);

        gui.start();
    }

}
