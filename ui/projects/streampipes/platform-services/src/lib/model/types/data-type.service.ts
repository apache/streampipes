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

export class DataTypeService {
    public static readonly XSD: string = 'http://www.w3.org/2001/XMLSchema#';

    public static readonly Integer = this.XSD + 'integer';
    public static readonly Long = this.XSD + 'long';
    public static readonly Float = this.XSD + 'float';
    public static readonly Boolean = this.XSD + 'boolean';
    public static readonly String = this.XSD + 'string';
    public static readonly Double = this.XSD + 'double';

    // Should we support data type number?
    public static readonly Number = this.XSD + 'number';

    public static isNumberType(datatype: string): boolean {
        return (
            datatype === DataTypeService.Double ||
            datatype === DataTypeService.Integer ||
            datatype === DataTypeService.Long ||
            datatype === DataTypeService.Float ||
            datatype === DataTypeService.Number
        );
    }

    public static isBooleanType(datatype: string): boolean {
        return datatype === DataTypeService.Boolean;
    }
}
