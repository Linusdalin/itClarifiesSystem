package overviewExport;

/*************************************************************************
 *
 *              Define the style and parameters for each export sheet
 */


public class SheetExportStyle {

    public final String sheetName;
    public int currentRow;

    SheetExportStyle(String sheetName, int startRow){

        this.sheetName = sheetName;

        this.currentRow = startRow;
    }
}
