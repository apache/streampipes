/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

import { TreeNodeUserInput } from '../../model/TreeNodeUserInput';

export class TreeStaticPropertyUtils {
    /**
     * Returns the tree editor
     */
    public static treeEditor() {
        return cy.dataCy('tree-editor');
    }

    /**
     * Return the text editor
     */
    public static textEditor() {
        return cy.dataCy('text-editor');
    }

    /**
     * Opens the text editor
     */
    public static switchToTextEditor() {
        cy.dataCy('editor-mode-text').click();
    }

    /**
     * Opens the tree editor
     */
    public static switchToTreeEditor() {
        cy.dataCy('editor-mode-tree').click();
    }

    /**
     * Appends the @param text to the text editor
     */
    public static typeInTextEditor(text: string) {
        cy.dataCy('static-tree-input-text-editor').type(text + '{enter}');
    }

    /**
     * Returns the content of the text editor
     */
    public static getTextInTextEditor() {
        return cy
            .dataCy('static-tree-input-text-editor')
            .find('.CodeMirror-line')
            .invoke('text');
    }

    /**
     * Selects the @param treeNode in the tree view. If the tree node has
     * children, it will expand the tree node and recursivly navigate through
     * the selected node.
     */
    public static selectTreeNode(treeNode: TreeNodeUserInput) {
        if (!treeNode.isTextConfig) {
            // configure tree node
            if (treeNode.children && treeNode.children.length > 0) {
                TreeStaticPropertyUtils.expandNode(treeNode.name);
                treeNode.children.forEach(child => {
                    this.selectTreeNode(child);
                });
            } else {
                TreeStaticPropertyUtils.selectNode(treeNode.name);
            }
        } else {
            TreeStaticPropertyUtils.switchToTextEditor();
            TreeStaticPropertyUtils.typeInTextEditor(treeNode.name);
        }
    }

    /**
     * Expand the node with @param treeNodeName in the tree view
     */
    public static expandNode(treeNodeName: string) {
        cy.dataCy('expand-' + treeNodeName).click();
    }

    /**
     * Select the node with @param treeNodeName in the tree view
     */
    public static selectNode(treeNodeName: string) {
        cy.dataCy('select-' + treeNodeName).click();
    }

    /**
     * Removes the selected node with the identifier @param nodeIdentifier.
     * dataCy could not be used because often special characters are used in
     * the nodeIdentifier.
     */
    public static removeSelectedNode(nodeIdentifier: string) {
        cy.get('[data-cy="remove-' + nodeIdentifier + '"]').click();
    }

    /**
     * Validates that the amount of nodes shown in the selected tab are equal
     * to @param expectedAmount.
     */
    public static validateAmountOfSelectedNodes(expectedAmount: number) {
        cy.dataCy('selected-node-', {}, true).should(
            'have.length',
            expectedAmount,
        );
    }

    /**
     * Validates that the amount of nodes shown in the browse tab are equal
     * to @param expectedAmount.
     */
    public static validateAmountOfShownBrowseNodes(expectedAmount: number) {
        cy.dataCy('expand-', {}, true).should('have.length', expectedAmount);
    }

    /**
     * Validates the number of node details metadata rows displayed.
     */
    public static validateAmountOfShownNodeDetailsMetaDataRows(
        expectedAmount: number,
    ) {
        cy.dataCy('node-details-metadata-row-', {}, true).should(
            'have.length',
            expectedAmount,
        );
    }

    /**
     * Select node to be shown in node details
     */
    public static showNodeDetails(nodeName: string) {
        cy.dataCy(`show-node-details-${nodeName}`).click();
    }

    /**
     * Unselect the node to be removed from node details view
     */
    public static hideNodeDetails(nodeName: string) {
        cy.dataCy('hide-node-details-' + nodeName).click();
    }

    /**
     * Validates that the @param nodeName is marked as selected in the
     * tree view.
     */
    public static checkThatNodeIsSelectedInTree(nodeName: string) {
        cy.dataCy('tree-node-' + nodeName).within(() => {
            cy.get('i.material-icons')
                .contains('remove_circle')
                .should('exist');
        });
    }

    public static clickClearAndReloadButton() {
        cy.dataCy('clear-tree-node-selection').click();
    }
}
