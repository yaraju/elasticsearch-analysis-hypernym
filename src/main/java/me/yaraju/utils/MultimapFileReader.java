package me.yaraju.utils;
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Reader for files that hold multivalue maps in the format:
 * a=b1|b2
 *
 * Sourced from Apache Lucas:
 * https://apache.googlesource.com/uima-addons/+/bd3841fa3daaee2555ef807316eb6195eba52e84/Lucas/src/main/java/org/apache/uima/lucas/indexer/util/MultimapFileReader.java
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
