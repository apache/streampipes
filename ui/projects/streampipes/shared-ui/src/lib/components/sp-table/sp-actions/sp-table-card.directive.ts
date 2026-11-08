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

import { Directive } from '@angular/core';

/**
 * Marks the template of a table row in the card view. Declaring it enables the
 * card view mode of `sp-table`. The template context contains the row as
 * `$implicit` and the table's `actions` and `assetContext` cell templates, which
 * both expect the row as their `$implicit` context.
 */
@Directive({ selector: 'ng-template[spTableCard]' })
export class SpTableCardDirective {}
