package org.elasticsearch.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yar on 6/6/15.
 */
public class MultimapFileReader extends Reader {

    private BufferedReader reader;

    public MultimapFileReader(BufferedReader reader) {
        super();
        this.reader = reader;
    }

    public Map<String, List<String>> readMultimap() throws IOException {
        Map<String, List<String>> multimap = new HashMap<String, List<String>>();

        String line = reader.readLine();
        while (line != null) {
            String[] keyValue = line.split("=");
            String term = keyValue[0];
            String[] values = keyValue[1].split("\\|");
            List<String> valueList = new ArrayList<String>();
            for (String hypernym : values)
                valueList.add(hypernym);

            multimap.put(term, valueList);
            line = reader.readLine();
        }
        return multimap;
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {

        return reader.read(cbuf, off, len);
    }
}
