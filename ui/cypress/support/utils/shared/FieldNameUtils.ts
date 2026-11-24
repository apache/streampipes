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

/** Browser assertions for complete, visibly wrapped field names. */
export class FieldNameUtils {
    public static expectWrapped<T extends HTMLElement>(
        field: Cypress.Chainable<JQuery<T>>,
        name: string,
    ) {
        field.scrollIntoView().should($field => {
            const element = $field[0];
            expect(element.textContent.trim()).to.equal(name);
            const range = element.ownerDocument.createRange();
            range.selectNodeContents(element);
            const lines = new Set(
                Array.from(range.getClientRects()).map(rect =>
                    Math.round(rect.top),
                ),
            );
            expect(
                lines.size,
                'field name wraps onto multiple lines',
            ).to.be.greaterThan(1);
            expect(
                element.scrollWidth,
                'no hidden horizontal text',
            ).to.be.at.most(element.clientWidth + 1);
            expect(
                element.scrollHeight,
                'no hidden vertical text',
            ).to.be.at.most(element.clientHeight + 1);
        });
    }
}
