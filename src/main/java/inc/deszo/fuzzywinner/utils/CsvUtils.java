package inc.deszo.fuzzywinner.utils;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CsvUtils {

  public static void csvWriter(List<LinkedHashMap<String, String>> listOfMap, Writer writer) throws IOException {
    CsvSchema schema = null;
    CsvSchema.Builder schemaBuilder = CsvSchema.builder();
    if (listOfMap != null && !listOfMap.isEmpty()) {
      for (String col : listOfMap.get(0).keySet()) {
        schemaBuilder.addColumn(col);
      }
      schema = schemaBuilder.build().withLineSeparator("\r").withHeader();
    }
    CsvMapper mapper = new CsvMapper();
    mapper.writer(schema).writeValues(writer).writeAll(listOfMap);
    writer.flush();
  }

  public static void csvReader(Reader reader) throws IOException {
    Iterator<Map<String, String>> iterator = new CsvMapper()
        .readerFor(Map.class)
        .with(CsvSchema.emptySchema().withHeader())
        .readValues(reader);
    while (iterator.hasNext()) {
      Map<String, String> keyVals = iterator.next();
      System.out.println(keyVals);
    }
  }
}
