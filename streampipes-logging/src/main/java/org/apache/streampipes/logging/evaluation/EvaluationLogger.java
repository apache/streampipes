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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class EvaluationLogger {

    private final File file;
    private String buffer;
    private static EvaluationLogger instance = null;

    public static EvaluationLogger getInstance(){
        if(instance==null) instance = new EvaluationLogger();
        return instance;
    }

    private EvaluationLogger(){
        String filepath = System.getenv("LoggingFilepath");
        if(filepath==null) throw new RuntimeException("No Logging Location provided.");
        this.file = new File(filepath);
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
        try (FileWriter fw = new FileWriter(file)){
            fw.append(buffer);
            fw.flush();
            buffer = "";
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
