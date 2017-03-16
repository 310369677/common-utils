package com.yang.util.excel;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.*;

/**
 * 描述:
 * 作者:杨川东
 * 时间:10:48
 */
public class ExcelUtil {
    /**
     * 解析excel文件
     *
     * @param file       要解析的文件
     * @param readSheetMapper 单元格匹配器
     * @return 解析的结果
     * @throws IOException 异常
     */
    public static Map<String, List> readExcel(File file, ReadSheetMapper readSheetMapper) throws IOException {
        FileInputStream fileInputStream=new FileInputStream(file);
        ExcelType excelType = parseExcelType(file.getName());
       return readExcel(fileInputStream,readSheetMapper,excelType);
    }


    public static Map<String, List> readExcel(InputStream inputStream, ReadSheetMapper readSheetMapper, ExcelType excelType) throws IOException {
        ReadSheetMapper defaultReadSheetMapper = new ReadSheetMapperAdapter();
        if (readSheetMapper != null) {
            defaultReadSheetMapper = readSheetMapper;
        }
        POIFSFileSystem poifsFileSystem = new POIFSFileSystem(inputStream);
        //创建工作簿对象
        Workbook workbook = createWorkBook(excelType, poifsFileSystem);
        //得到工作表的数量
        int sheetNum = workbook.getNumberOfSheets();
        Map<String, List> workbookResult = new HashMap<>();
        int startSheetIndex = 0;  //开始解析sheet表的索引
        if (defaultReadSheetMapper.startParseSheetIndex() >= 0) {
            startSheetIndex = defaultReadSheetMapper.startParseSheetIndex();
        }
        int endSheetIndex = sheetNum;  //结束解析sheet表的索引
        if (defaultReadSheetMapper.endParseSheetIndex() >= 0) {
            endSheetIndex = (defaultReadSheetMapper.endParseSheetIndex())>endSheetIndex?endSheetIndex:(defaultReadSheetMapper.endParseSheetIndex());
        }

        for (int i = startSheetIndex; i < endSheetIndex; i++) {
            Sheet sheet = workbook.getSheetAt(i);  //得到sheet表
            List<Object> sheetResult = parseSheet(sheet, defaultReadSheetMapper);
            workbookResult.put(sheet.getSheetName(), sheetResult);
        }
        return workbookResult;
    }


    /**
     * 写入sheet表
     *
     * @param outputStream     写入的流
     * @param data             数据
     * @param writeSheetMapper 写入匹配器
     * @param excelType        excel的类型
     * @return 错误的信息
     * @throws IOException 读取文件的异常
     */
    public static void writeExcel(OutputStream outputStream, Map<String, Map<Integer, List>> data, WriteSheetMapper writeSheetMapper, ExcelType excelType) throws IOException {
        //创建工作簿
        Workbook workbook = createWorkBook(excelType, null);
        //写入工作表
        for (Map.Entry<String, Map<Integer, List>> entry : data.entrySet()) {
            Sheet sheet = workbook.createSheet(entry.getKey());
            if (writeSheetMapper != null) {
                writeSheetMapper.writeSheet(sheet, entry.getValue());
            }
        }
        workbook.write(outputStream);
    }

    /**
     * 解析sheet表的具体逻辑
     *
     * @param sheet 表
     */
    private static List<Object> parseSheet(Sheet sheet, ReadSheetMapper readSheetMapper) {
        Map<Integer, Map<Short,Object>> resultData = new HashMap<>();
        Map<Integer, String> errorInfo = new HashMap<>();
        List<Object> resultInfo = new ArrayList<>();
        int startRowIndex = sheet.getFirstRowNum();  //默认的起始行
        if (readSheetMapper.startRowIndex() >= 0) {
            startRowIndex = readSheetMapper.startRowIndex();
        }
        int endRowIndex = sheet.getLastRowNum();
        if (readSheetMapper.endRowIndex() >= 0) {
            endRowIndex = (readSheetMapper.endRowIndex()-1)>endRowIndex?endRowIndex:(readSheetMapper.endRowIndex()-1);
        }
        for (int rowIndex = startRowIndex; rowIndex <= endRowIndex; rowIndex++) {
            boolean currentRowIgnore = false;
            int[] ignoreRows = readSheetMapper.ignoreRowIndex();  //忽略行的索引
            if (Arrays.binarySearch(ignoreRows, rowIndex)>=0) {
                currentRowIgnore = true;
            }
            if (currentRowIgnore) {
                continue;
            }
            Row row = sheet.getRow(rowIndex);
            Map<Short,Object> rowDataMap = new LinkedHashMap<>();
            //得到有多少列
            boolean parseError = false;
            short startColumnIndex=row.getFirstCellNum();
            if(readSheetMapper.startColumnIndex()>=0){
                startColumnIndex=readSheetMapper.startColumnIndex();
            }
            short endColumnIndex=row.getLastCellNum();
            if(readSheetMapper.endColumnIndex()>=0){
                endColumnIndex= (readSheetMapper.endColumnIndex())>endColumnIndex?endColumnIndex: (readSheetMapper.endColumnIndex());
            }
            for (short columnIndex = startColumnIndex; columnIndex < endColumnIndex; columnIndex++) {
                boolean currentColumnIgnore=false;
                short[] ignoreColumns=readSheetMapper.ignoreColumnIndex();
                if(Arrays.binarySearch(ignoreColumns,columnIndex)>=0){
                    currentColumnIgnore=true;
                }
                if(currentColumnIgnore){
                    continue;
                }

                Cell cell = row.getCell(columnIndex);  //得到单元格
                try {
                    rowDataMap.put(columnIndex,readSheetMapper.handleCell(cell,rowIndex,columnIndex));
                }catch(RuntimeException e){  //这一列发生错误
                    errorInfo.put(rowIndex,"第"+(rowIndex+1)+"行"+(columnIndex+1)+"列"+e.getMessage());
                    parseError=true;
                    break;
                }
            }
            if (parseError) {  //这一行解析出错了
                continue;
            }
            resultData.put(rowIndex, rowDataMap);

        }
        resultInfo.add(resultData);
        resultInfo.add(errorInfo);
        return resultInfo;
    }

    /**
     * 创建工作簿
     *
     * @param excelType 文件的类型
     * @return 工作簿的类型
     */
    private static Workbook createWorkBook(ExcelType excelType, POIFSFileSystem poifsFileSystem) throws IOException {
        switch (excelType) {
            case XLS:
                return poifsFileSystem == null ? new HSSFWorkbook() : new HSSFWorkbook(poifsFileSystem);
            case XLSX:
                return poifsFileSystem == null ? new XSSFWorkbook() : new HSSFWorkbook(poifsFileSystem);
        }
        throw new RuntimeException("无法解析的excel的表格类型");
    }

    public static ExcelType parseExcelType(String fileName) {
        if (fileName.endsWith(".xls")) {
            return ExcelType.XLS;
        }
        if (fileName.endsWith(".xlsx")) {
            return ExcelType.XLSX;
        }
        return ExcelType.UNKNOWN;
    }

    public enum ExcelType {
        XLS, XLSX, UNKNOWN
    }

    public interface CellMapper {
        Object handleCell(Cell cell, int rowIndex, int columnIndex) throws RuntimeException;
    }

    public interface ReadSheetMapper {
        int startParseSheetIndex();   //开始解析sheet表时的索引

        int endParseSheetIndex();     //结束解析sheet表的索引 "-1"表示不提前结束

        int startRowIndex();         //从多少行开始解析

        int endRowIndex();          //解析到多少行结束

        short startColumnIndex();      //从多少列开始解析

        short endColumnIndex();        //解析到多少列结束

        int[] ignoreRowIndex();      //忽略行的索引

        short[] ignoreColumnIndex();  //忽略的列的索引

        Object handleCell(Cell cell, int rowIndex, int columnIndex) throws RuntimeException; //自定义处理单元格的值信息
    }


    public static class ReadSheetMapperAdapter implements ReadSheetMapper {

        @Override
        public int startParseSheetIndex() {
            return 0;
        }

        @Override
        public int endParseSheetIndex() {
            return -1;
        }

        @Override
        public int startRowIndex() {
            return -1;
        }

        @Override
        public int endRowIndex() {
            return -1;
        }

        @Override
        public short startColumnIndex() {
            return -1;
        }

        @Override
        public short endColumnIndex() {
            return -1;
        }

        @Override
        public int[] ignoreRowIndex() {
            return new int[0];
        }

        @Override
        public short[] ignoreColumnIndex() {
            return new short[0];
        }

        //默认的处理
        @Override
        public Object handleCell(Cell cell, int rowIndex, int columnIndex) throws RuntimeException {
            cell.setCellType(Cell.CELL_TYPE_STRING); //默认处理成字符串
            return cell.getStringCellValue();
        }
    }

    public interface WriteSheetMapper {
        void writeSheet(Sheet sheet, Map<Integer, List> data);
    }

    public static void main(String[] args) throws IOException {
        File file = new File("D:\\test\\test.xls");
        if (!file.exists()) {
            file.createNewFile();
        }
        Map<String, List> result = readExcel(file,new ReadSheetMapperAdapter(){
            @Override
            public int startRowIndex() {
                return 0;
            }

            @Override
            public int endRowIndex() {
                return 3;
            }

            @Override
            public int[] ignoreRowIndex() {
                return new int[]{1};
            }

            @Override
            public short startColumnIndex() {
                return 0;
            }

            @Override
            public short endColumnIndex() {
                return 3;
            }

            @Override
            public short[] ignoreColumnIndex() {
                return new short[]{2};
            }
        });
        System.out.println("结束了");
       /* FileOutputStream fileOutputStream=new FileOutputStream(file);
        Map<String,Map<Integer,List>> map=new HashMap<>();
        Map<Integer,List> sheetData=new HashMap<>();
        List<Object> list=new ArrayList<>();
        list.add("1");
        list.add("李四");
        list.add("20");
        sheetData.put(1,list);
        List<Object> list1=new ArrayList<>();
        list1.add("2");
        list1.add("张三");
        list1.add(18);
        sheetData.put(2,list1);
        map.put("sheetone",sheetData);
        writeExcel(fileOutputStream, map, new WriteSheetMapper() {
            @Override
            public void writeSheet(Sheet sheet, Map<Integer, List> data) {
                   //创建表头
                Row row=sheet.createRow(0);
                CellStyle cellStyle=sheet.getWorkbook().createCellStyle();
                cellStyle.setAlignment(CellStyle.ALIGN_CENTER); //设置居中
                Cell cell=row.createCell(0);
                cell.setCellValue("学号");
                cell.setCellStyle(cellStyle);
                cell=row.createCell(1);
                cell.setCellValue("姓名");
                cell.setCellStyle(cellStyle);
                cell=row.createCell(2);
                cell.setCellValue("年龄");
                cell.setCellStyle(cellStyle);
                //写入数据
                int i=1;
                for(Map.Entry<Integer,List> entry:data.entrySet()){
                    List rowData=entry.getValue();
                    Row row1=sheet.createRow(i);
                    i++;
                    int k=0;
                    for (Object object:rowData){
                        Cell cell1=row1.createCell(k);
                        cell1.setCellValue(object.toString());
                        cell1.setCellStyle(cellStyle);
                        k++;
                    }
                }
            }
        },ExcelType.XLS);*/
    }
}
