package com.yang.util.excel.adapter;

import com.yang.util.excel.interfaces.WriteRowMapper;
import com.yang.util.excel.interfaces.WriteSheetMapper;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.Collections;
import java.util.Map;

/**
 * 描述:
 * 作者:杨川东
 * 时间:19:14
 */
public class WriteSheetMapperAdapter implements WriteSheetMapper {
    @Override
    public Map<Integer, WriteRowMapper> obtainWriteRowMappers() {
        return Collections.emptyMap();
    }

    @Override
    public void preHandleSheet(Sheet sheet,Map<Integer,Map<Short,Object>> data,Object extraData) {

    }
}
