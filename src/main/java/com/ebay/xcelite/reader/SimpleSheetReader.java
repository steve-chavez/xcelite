/*
  Copyright [2013-2014] eBay Software Foundation

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package com.ebay.xcelite.reader;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import com.ebay.xcelite.sheet.XceliteSheet;
import com.google.common.collect.Lists;

/**
 * Class description...
 * 
 * @author kharel (kharel@ebay.com)
 * @creation_date Nov 8, 2013
 * 
 */
public class SimpleSheetReader extends SheetReaderAbs<Collection<Object>> {

  public SimpleSheetReader(XceliteSheet sheet) {
    super(sheet, false);
  }

  @Override
  public Collection<Collection<Object>> read() {
    List<Collection<Object>> rows = Lists.newArrayList();
    Iterator<Row> rowIterator = sheet.getNativeSheet().iterator();
    boolean firstRow = true;
    short cellsNum = -1;
    while (rowIterator.hasNext()) {      
      Row excelRow = rowIterator.next();
      if(firstRow){
        cellsNum = excelRow.getLastCellNum();
        firstRow = false;
        if(skipHeader)
            continue;
      }
      List<Object> row = Lists.newArrayList();

      boolean blankRow = true;
      for(int i=0; i<cellsNum; i++) {
        Object value = readValueFromCell(excelRow.getCell(i, 
            Row.MissingCellPolicy.RETURN_NULL_AND_BLANK));

        if (blankRow && value != null && !String.valueOf(value).isEmpty()) {
          blankRow = false;
        }
        row.add(value);
      }
      if (blankRow) continue;
      boolean keepRow = true;
      for (RowPostProcessor<Collection<Object>> rowPostProcessor : rowPostProcessors) {
        keepRow = rowPostProcessor.process(row);
        if (!keepRow) break;
      }
      if (keepRow) {
        rows.add(row);
      }
    }
    return rows;
  }
}
