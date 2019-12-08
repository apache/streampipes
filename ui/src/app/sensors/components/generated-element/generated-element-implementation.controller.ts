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

import * as angular from 'angular';
import * as JSZip from 'jszip';
import * as FileSaver from 'file-saver';

export class GeneratedElementImplementationController {

    extractedFiles: any;
    currentFileName: any;
    currentFileContents: any;
    loadingCompleted: any;
    new_zip: any;
    zipFile: any;
    element: any;

    constructor() {
        this.extractedFiles = [];
        this.currentFileName = "";
        this.currentFileContents = "";
        this.loadingCompleted = false;

        this.new_zip = new JSZip();
    }

    $onInit() {
        this.new_zip.loadAsync(this.zipFile)
            .then(zip => {
                angular.forEach(zip.files, file => {
                    var filename = file.name;
                    this.extractedFiles.push({
                        "fileNameLabel": this.getFileName(filename),
                        "fileNameDescription": this.getDirectory(filename),
                        "fileName": filename,
                        "fileContents": file
                    });
                })
            });
    }

    openFile(file) {
        this.loadingCompleted = false;
        this.currentFileName = file.fileName;
        file.fileContents.async("string")
            .then(content => {
                this.currentFileContents = content;
                this.loadingCompleted = true;
            });
        ;
    }

    getLanguage(filename) {
        if (filename.endsWith("java")) return "java";
        else if (filename.endsWith("xml")) return "xml";
        else return "";
    }

    getFileName(filename) {
        if (/.+\\/gi.test(filename))
            return filename.replace(/.+\\/g, "");
        else if (/.+\//gi.test(filename))
            return filename.replace(/.+\//g, "");
        else
            return filename;
    }

    getDirectory(filename) {
        if (/.+\\/gi.test(filename)) {
            var directory = /.+\\/gi.exec(filename)[0];
            return directory.replace(/\\/g, "/");
        }
        else if (/.+\//gi.test(filename)) {
            var directory = /.+\//gi.exec(filename)[0];
            return directory.replace(/\//g, "/");
        }
        else return "/";
    }

    downloadZip() {
        this.openSaveAsDialog(this.element.name + ".zip", this.zipFile, "application/zip");
    }

    openSaveAsDialog(filename, content, mediaType) {
        var blob = new Blob([content], {type: mediaType})
        FileSaver.saveAs(blob, filename);
    }

}