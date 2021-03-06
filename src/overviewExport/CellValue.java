package overviewExport;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.*;

/*****
 *
 *          Cell styling and content for export
 *
 */

public class CellValue {



    public CellValue() {

        this.type = Type.EMPTY;
        setDefaultStyle();
    }

    public enum Type {STRING, VALUE, EMPTY}

    private String text = "";
    private int value = 0;
    private Type type;
    private boolean indentation;

    private int fontSize;
    private String fontName = null;

    private boolean bold, italics, wrap, udBorder,lrBorder, center, middle;

    private byte[] backgroundFill = null;

    private static final byte[] borderColour =   {(byte)0xBB, (byte)0xBB, (byte)0xBB};
    private static final byte[] greyBackground = {(byte)0xAA, (byte)0xAA, (byte)0xAA};
    private static final byte[] whiteFont =      {(byte)0x00, (byte)0x00, (byte)0x00};

    public CellValue(String text){

        this.text = text;
        this.type = Type.STRING;
        setDefaultStyle();
        this.indentation = true;

    }

    public CellValue(int v) {

        this.value = v;
        this.type = Type.VALUE;
        setDefaultStyle();
        this.center = true;     // Default is to center all number cells
    }

    private void setDefaultStyle(){

        this.fontSize = 12;     // Default value
        this.bold = false;
        this.italics = false;
        this.wrap = true;       // Default is wrap cell
        this.lrBorder = false;
        this.udBorder = false;
        this.center = false;
        this.middle = false;

    }

    public String getStringValue() {
        return text;
    }

    public int getValue() {
        return value;
    }

    public CellValue asBox() {

        lrBorder = true;
        udBorder = true;
        return this;
    }

    public CellValue asRow() {

        udBorder = true;
        return this;
    }

    public CellValue withFont(int fontSize){

        this.fontSize = fontSize;
        return this;
    }

    public CellValue withFont(String fontName){

        this.fontName = fontName;
        return this;
    }

    public CellValue withFont(String fontName, int fontSize){

        this.fontName = fontName;
        this.fontSize = fontSize;
        return this;
    }

    public CellValue bold(){

        this.bold = true;
        return this;
    }

    public CellValue noWrap(){

        this.wrap = false;
        return this;
    }


    public CellValue italics(){

        this.italics = true;
        return this;
    }

    public CellValue tableHeadline(){

        this.backgroundFill = greyBackground;
        this.fontName = "Proxima Nova Bold";
        this.fontSize = 14;
        this.middle = true;
        return this;
    }

    public CellValue fill(byte[] colour){

        this.backgroundFill = colour;
        return this;
    }


    public XSSFCellStyle getStyle(XSSFSheet sheet) {

        XSSFCellStyle style = sheet.getWorkbook().createCellStyle();
        XSSFFont font = sheet.getWorkbook().createFont();

        if(bold || italics){

            //System.out.println(" --- Setting style " + (bold ? "bold " : "") + (italics ? "italics " : ""));

        }

        if(fontName != null)
            font.setFontName(fontName);

        font.setFontHeightInPoints((short) fontSize);
        font.setBold(bold);
        font.setItalic(italics);
        style.setWrapText(wrap);

        if(backgroundFill != null){
            style.setFillPattern(CellStyle.SOLID_FOREGROUND);
            style.setFillForegroundColor(new XSSFColor(backgroundFill));
            font.setColor(new XSSFColor(whiteFont));         //TODO: This doesn't work
        }

        style.setFont(font);

        if(indentation){

            style.setIndention((short)1);
        }

        if(lrBorder){
            style.setBorderLeft(BorderStyle.THIN);
            style.setLeftBorderColor(new XSSFColor(borderColour));

            style.setBorderRight(BorderStyle.THIN);
            style.setRightBorderColor(new XSSFColor(borderColour));
        }
        if(udBorder){

            style.setBorderTop(BorderStyle.THIN);
            style.setTopBorderColor(new XSSFColor(borderColour));

            style.setBorderBottom(BorderStyle.THIN);
            style.setBottomBorderColor(new XSSFColor(borderColour));


        }

        style.setVerticalAlignment(XSSFCellStyle.VERTICAL_TOP);

        if(center)
            style.setAlignment(HorizontalAlignment.CENTER);

        if(middle)
            style.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);

        return style;
    }

    public Type getType() {
        return type;
    }


/*
public class CellStyle
{
   public static void main(String[] args)throws Exception
   {
      XSSFWorkbook workbook = new XSSFWorkbook();
      XSSFSheet spreadsheet = workbook.createSheet("cellstyle");
      XSSFRow row = spreadsheet.createRow((short) 1);
      row.setHeight((short) 800);
      XSSFCell cell = (XSSFCell) row.createCell((short) 1);
      cell.setCellValue("test of merging");
      //MEARGING CELLS
      //this statement for merging cells
      spreadsheet.addMergedRegion(new CellRangeAddress(
      1, //first row (0-based)
      1, //last row (0-based)
      1, //first column (0-based)
      4 //last column (0-based)
      ));
      //CELL Alignment
      row = spreadsheet.createRow(5);
      cell = (XSSFCell) row.createCell(0);
      row.setHeight((short) 800);
      // Top Left alignment
      XSSFCellStyle style1 = workbook.createCellStyle();
      spreadsheet.setColumnWidth(0, 8000);
      style1.setAlignment(XSSFCellStyle.ALIGN_LEFT);
      style1.setVerticalAlignment(XSSFCellStyle.VERTICAL_TOP);
      cell.setCellValue("Top Left");
      cell.setCellStyle(style1);
      row = spreadsheet.createRow(6);
      cell = (XSSFCell) row.createCell(1);
      row.setHeight((short) 800);
      // Center Align Cell Contents
      XSSFCellStyle style2 = workbook.createCellStyle();
      style2.setAlignment(XSSFCellStyle.ALIGN_CENTER);
      style2.setVerticalAlignment(
      XSSFCellStyle.VERTICAL_CENTER);
      cell.setCellValue("Center Aligned");
      cell.setCellStyle(style2);
      row = spreadsheet.createRow(7);
      cell = (XSSFCell) row.createCell(2);
      row.setHeight((short) 800);
      // Bottom Right alignment
      XSSFCellStyle style3 = workbook.createCellStyle();
      style3.setAlignment(XSSFCellStyle.ALIGN_RIGHT);
      style3.setVerticalAlignment(
      XSSFCellStyle.VERTICAL_BOTTOM);
      cell.setCellValue("Bottom Right");
      cell.setCellStyle(style3);
      row = spreadsheet.createRow(8);
      cell = (XSSFCell) row.createCell(3);
      // Justified Alignment
      XSSFCellStyle style4 = workbook.createCellStyle();
      style4.setAlignment(XSSFCellStyle.ALIGN_JUSTIFY);
      style4.setVerticalAlignment(
      XSSFCellStyle.VERTICAL_JUSTIFY);
      cell.setCellValue("Contents are Justified in Alignment");
      cell.setCellStyle(style4);
      //CELL BORDER
      row = spreadsheet.createRow((short) 10);
      row.setHeight((short) 800);
      cell = (XSSFCell) row.createCell((short) 1);
      cell.setCellValue("BORDER");
      XSSFCellStyle style5 = workbook.createCellStyle();
      style5.setBorderBottom(XSSFCellStyle.BORDER_THICK);
      style5.setBottomBorderColor(
      IndexedColors.BLUE.getIndex());
      style5.setBorderLeft(XSSFCellStyle.BORDER_DOUBLE);
      style5.setLeftBorderColor(
      IndexedColors.GREEN.getIndex());
      style5.setBorderRight(XSSFCellStyle.BORDER_HAIR);
      style5.setRightBorderColor(
      IndexedColors.RED.getIndex());
      style5.setBorderTop(XSSFCellStyle.BIG_SPOTS);
      style5.setTopBorderColor(
      IndexedColors.CORAL.getIndex());
      cell.setCellStyle(style5);
      //Fill Colors
      //background color
      row = spreadsheet.createRow((short) 10 );
      cell = (XSSFCell) row.createCell((short) 1);
      XSSFCellStyle style6 = workbook.createCellStyle();
      style6.setFillBackgroundColor(
      HSSFColor.LEMON_CHIFFON.index );
      style6.setFillPattern(XSSFCellStyle.LESS_DOTS);
      style6.setAlignment(XSSFCellStyle.ALIGN_FILL);
      spreadsheet.setColumnWidth(1,8000);
      cell.setCellValue("FILL BACKGROUNG/FILL PATTERN");
      cell.setCellStyle(style6);
      //Foreground color
      row = spreadsheet.createRow((short) 12);
      cell = (XSSFCell) row.createCell((short) 1);
      XSSFCellStyle style7=workbook.createCellStyle();
      style7.setFillForegroundColor(HSSFColor.BLUE.index);
      style7.setFillPattern( XSSFCellStyle.LESS_DOTS);
      style7.setAlignment(XSSFCellStyle.ALIGN_FILL);
      cell.setCellValue("FILL FOREGROUND/FILL PATTERN");
      cell.setCellStyle(style7);
      FileOutputStream out = new FileOutputStream(
      new File("cellstyle.xlsx"));
      workbook.write(out);
      out.close();
      System.out.println("cellstyle.xlsx written successfully");
   }
}
 */
}
