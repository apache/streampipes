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

const encoder = new TextEncoder();

interface ZipEntry {
    name: string;
    contents: Uint8Array;
}

/** Creates a minimal XLSX workbook using only browser platform APIs. */
export function createXlsx(rows: unknown[][], sheetName: string): ArrayBuffer {
    return createZip([
        {
            name: '[Content_Types].xml',
            contents:
                encode(`<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types"><Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/><Default Extension="xml" ContentType="application/xml"/><Override PartName="/xl/workbook.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml"/><Override PartName="/xl/worksheets/sheet1.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml"/></Types>`),
        },
        {
            name: '_rels/.rels',
            contents:
                encode(`<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships"><Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="xl/workbook.xml"/></Relationships>`),
        },
        {
            name: 'xl/workbook.xml',
            contents:
                encode(`<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<workbook xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main" xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships"><sheets><sheet name="${escapeXml(sheetName)}" sheetId="1" r:id="rId1"/></sheets></workbook>`),
        },
        {
            name: 'xl/_rels/workbook.xml.rels',
            contents:
                encode(`<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships"><Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet" Target="worksheets/sheet1.xml"/></Relationships>`),
        },
        {
            name: 'xl/worksheets/sheet1.xml',
            contents: encode(worksheetXml(rows)),
        },
    ]);
}

function worksheetXml(rows: unknown[][]): string {
    const sheetData = rows
        .map(
            (row, rowIndex) =>
                `<row r="${rowIndex + 1}">${row
                    .map(
                        (value, columnIndex) =>
                            `<c r="${columnName(columnIndex)}${rowIndex + 1}" t="inlineStr"><is><t xml:space="preserve">${escapeXml(String(value ?? ''))}</t></is></c>`,
                    )
                    .join('')}</row>`,
        )
        .join('');

    return `<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<worksheet xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main"><sheetData>${sheetData}</sheetData></worksheet>`;
}

function columnName(index: number): string {
    let name = '';
    let column = index + 1;
    while (column > 0) {
        const remainder = (column - 1) % 26;
        name = String.fromCharCode(65 + remainder) + name;
        column = Math.floor((column - 1) / 26);
    }
    return name;
}

function escapeXml(value: string): string {
    return value
        .replace(/[\u0000-\u0008\u000B\u000C\u000E-\u001F]/g, '')
        .replaceAll('&', '&amp;')
        .replaceAll('<', '&lt;')
        .replaceAll('>', '&gt;')
        .replaceAll('"', '&quot;')
        .replaceAll("'", '&apos;');
}

function createZip(entries: ZipEntry[]): ArrayBuffer {
    const localFiles: Uint8Array[] = [];
    const centralDirectory: Uint8Array[] = [];
    let offset = 0;

    for (const entry of entries) {
        const name = encode(entry.name);
        const checksum = crc32(entry.contents);
        const localFile = new Uint8Array(
            30 + name.length + entry.contents.length,
        );
        const localView = new DataView(localFile.buffer);
        localView.setUint32(0, 0x04034b50, true);
        localView.setUint16(4, 20, true);
        localView.setUint16(6, 0x0800, true);
        localView.setUint32(14, checksum, true);
        localView.setUint32(18, entry.contents.length, true);
        localView.setUint32(22, entry.contents.length, true);
        localView.setUint16(26, name.length, true);
        localFile.set(name, 30);
        localFile.set(entry.contents, 30 + name.length);
        localFiles.push(localFile);

        const centralFile = new Uint8Array(46 + name.length);
        const centralView = new DataView(centralFile.buffer);
        centralView.setUint32(0, 0x02014b50, true);
        centralView.setUint16(4, 20, true);
        centralView.setUint16(6, 20, true);
        centralView.setUint16(8, 0x0800, true);
        centralView.setUint32(16, checksum, true);
        centralView.setUint32(20, entry.contents.length, true);
        centralView.setUint32(24, entry.contents.length, true);
        centralView.setUint16(28, name.length, true);
        centralView.setUint32(42, offset, true);
        centralFile.set(name, 46);
        centralDirectory.push(centralFile);
        offset += localFile.length;
    }

    const centralDirectorySize = centralDirectory.reduce(
        (size, file) => size + file.length,
        0,
    );
    const end = new Uint8Array(22);
    const endView = new DataView(end.buffer);
    endView.setUint32(0, 0x06054b50, true);
    endView.setUint16(8, entries.length, true);
    endView.setUint16(10, entries.length, true);
    endView.setUint32(12, centralDirectorySize, true);
    endView.setUint32(16, offset, true);

    const result = new Uint8Array(offset + centralDirectorySize + end.length);
    let position = 0;
    for (const part of [...localFiles, ...centralDirectory, end]) {
        result.set(part, position);
        position += part.length;
    }
    return result.buffer;
}

function encode(value: string): Uint8Array {
    return encoder.encode(value);
}

function crc32(value: Uint8Array): number {
    let checksum = 0xffffffff;
    for (const byte of value) {
        checksum ^= byte;
        for (let bit = 0; bit < 8; bit++) {
            checksum =
                checksum & 1 ? (checksum >>> 1) ^ 0xedb88320 : checksum >>> 1;
        }
    }
    return (checksum ^ 0xffffffff) >>> 0;
}
