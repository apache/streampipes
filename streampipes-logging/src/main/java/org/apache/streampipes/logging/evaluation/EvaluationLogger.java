/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.apache.streampipes.logging.evaluation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EvaluationLogger {

    private static String FILE_NAME = "evaluation-logs.csv";

    private final File file;
    private String buffer;
    private static EvaluationLogger instance = null;

    public static EvaluationLogger getInstance(){
        if(instance==null) instance = new EvaluationLogger();
        return instance;
    }

    private EvaluationLogger(){
        String filepath = System.getenv("SP_LOGGING_FILE_PATH");
        if(filepath==null) throw new RuntimeException("No Logging Location provided.");
        if(!filepath.endsWith("/")) filepath = filepath + "/";
        String timePrefix = new SimpleDateFormat("yyyy-MM-dd-HHmmss").format(new Date());
        String fileWithPath = new StringBuilder()
                .append(filepath)
                .append(timePrefix)
                .append("_")
                .append(FILE_NAME)
                .toString();
        this.file = new File(fileWithPath);
        try {
            if(!file.exists()){
                if(!file.getParentFile().exists()) file.getParentFile().mkdirs();
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addLine(Object[] elements){
        for(Object element:elements)
            buffer += element + ",";
        buffer += "\n";
    }

    public void writeOut(){
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, true))){
            bufferedWriter.append(buffer);
            bufferedWriter.flush();
            buffer = "";
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
