import com.yang.util.excel.ExcelUtil;
import com.yang.util.excel.adapter.WriteSheetMapperAdapter;
import com.yang.util.excel.enums.ExcelType;
import com.yang.util.excel.interfaces.WriteSheetMapper;
import com.yang.util.excel.interfaces.WriteWorkBookMapper;
import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * 描述:
 * 作者:杨川东
 * 时间:23:09
 */
public class ExcelUtilTest {

    @Test
    public void testWrite() throws IOException {
        //模拟数据库的数据
        Map<String,Map<Integer,Map<Short,Object>>> data=new HashMap<>();
        String sheetName="销售部";
        Map<Integer,Map<Short,Object>> salSheet=new HashMap<>();
        data.put(sheetName,salSheet);
        makeData(salSheet);
        String[] position=new String[]{"销售经理","销售员"};
        //文件
        File file=new File("d:/testExcelUtil.xls");
        if(!file.exists()){
            file.createNewFile();
        }
        FileOutputStream fileOutputStream=new FileOutputStream(file);
        ExcelUtil.writeExcel(fileOutputStream, data, position, new WriteWorkBookMapper() {
            @Override
            public Map<String, List<CellRangeAddress>> sheetsCellRangeAddresses() {
                Map<String,List<CellRangeAddress>> map=new HashMap<>();
                List<CellRangeAddress> list=new ArrayList<>();
                map.put(sheetName,list);
                list.add(new CellRangeAddress(0,0,2,4));
                list.add(new CellRangeAddress(1,1,4,5));
                return  map;
            }

            //设置单元格的样式
            @Override
            public Map<String, Map<Integer, Map<Short, CellStyle>>> sheetsCellStyles(Workbook workbook) {
                Map<String,Map<Integer,Map<Short,CellStyle>>> cellStylesMap=new HashMap<>();
                Map<Integer,Map<Short,CellStyle>> salCellStyleMap=new HashMap<>();
                cellStylesMap.put(sheetName,salCellStyleMap);
                //第一行的样式
                Map<Short,CellStyle> rowCellStyle=new HashMap<>();
                rowCellStyle.put((short) 2,createRedFontStyle(workbook));
                salCellStyleMap.put(0,rowCellStyle);
                rowCellStyle=new HashMap<>();
                //第二行样式
                for(short i=0;i<8;i++){
                    CellStyle cellStyle=createDefaultStyle(workbook);
                    cellStyle.setLocked(true);
                    rowCellStyle.put(i,cellStyle);
                }
                salCellStyleMap.put(1,rowCellStyle);
                //第四行的样式
                rowCellStyle=new HashMap<>();
                for (short i=0;i<4;i++){
                    rowCellStyle.put(i,createRedFontStyle(workbook));
                }
                for (short i=4;i<8;i++){
                    CellStyle cellStyle=createDefaultStyle(workbook);
                    cellStyle.setLocked(true);
                    rowCellStyle.put(i,cellStyle);
                }
                salCellStyleMap.put(3,rowCellStyle);

                return cellStylesMap;
            }

            private CellStyle createDefaultStyle(Workbook workbook) {
                CellStyle cellStyle=workbook.createCellStyle();
                cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
                cellStyle.setLocked(false);
                return cellStyle;
            }

            private CellStyle createRedFontStyle(Workbook workbook) {
                 CellStyle redFontStyle=workbook.createCellStyle();
                Font font=workbook.createFont();
                font.setColor(Font.COLOR_RED);
                redFontStyle.setFont(font);
                redFontStyle.setAlignment(CellStyle.ALIGN_CENTER);
                return redFontStyle;
            }

            @Override
            public Map<String, WriteSheetMapper> obtainWriteSheetMappers() {
                Map<String,WriteSheetMapper> writeSheetMapperMap=new HashMap<>();
                WriteSheetMapper writeSheetMapper=new WriteSheetMapperAdapter(){
                    @Override
                    public void preHandleSheet(Sheet sheet, Map<Integer, Map<Short, Object>> data, Object extraData) {
                        sheet.protectSheet("test");
                        DataValidationConstraint dataValidationConstraint = DVConstraint.createExplicitListConstraint((String[]) extraData);
                        CellRangeAddressList cellRangeAddressList = new CellRangeAddressList(4, 1000, 7, 7);
                        DataValidation dataValidation = new HSSFDataValidation(cellRangeAddressList, dataValidationConstraint);
                        dataValidation.setShowErrorBox(false);
                        sheet.addValidationData(dataValidation);
                        for (int k = 0; k < 8; k++) {
                            sheet.setDefaultColumnStyle(k, createDefaultStyle(sheet.getWorkbook()));
                            sheet.setColumnWidth(k, 4000);
                        }
                    }
                };
                writeSheetMapperMap.put(sheetName,writeSheetMapper);
                return writeSheetMapperMap;
            }
        }, ExcelType.XLS);

        fileOutputStream.close();
    }

    private void makeData(Map<Integer, Map<Short, Object>> salSheet) {
        //第一行的数据
        Map<Short,Object> rowData=new HashMap<>();
        rowData.put((short)2,"说明:标红的列是必填项");
        salSheet.put(0,rowData);
        rowData=new HashMap<>();
        rowData.put((short) 0,"");
        rowData.put((short) 1,"部门名称");
        rowData.put((short) 2,"销售部");
        rowData.put((short) 3,"部门的标识");
        rowData.put((short) 4,"f5ccfc4e4fd346198964727e4ef56808");
        salSheet.put(1,rowData);
        rowData=new HashMap<>();
        rowData.put((short) 0,"姓名");
        rowData.put((short) 1,"用户名");
        rowData.put((short) 2,"密码");
        rowData.put((short) 3,"联系手机");
        rowData.put((short) 4,"工号");
        rowData.put((short) 5,"联系座机");
        rowData.put((short) 6,"电子邮件");
        rowData.put((short) 7,"职位");
        salSheet.put(3,rowData);
    }
}
