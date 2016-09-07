'use strict';
// Source: dist/controllers/MlhrTableController.js
/*
* Copyright (c) 2013 DataTorrent, Inc. ALL Rights Reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
angular.module('datatorrent.mlhrTable.controllers.MlhrTableController', [
  'datatorrent.mlhrTable.services.mlhrTableSortFunctions',
  'datatorrent.mlhrTable.services.mlhrTableFilterFunctions',
  'datatorrent.mlhrTable.services.mlhrTableFormatFunctions'
]).controller('MlhrTableController', [
  '$scope',
  '$element',
  'mlhrTableFormatFunctions',
  'mlhrTableSortFunctions',
  'mlhrTableFilterFunctions',
  '$log',
  '$window',
  '$filter',
  '$timeout',
  function ($scope, $element, formats, sorts, filters, $log, $window, $filter, $timeout) {
    // SCOPE FUNCTIONS
    $scope.getSelectableRows = function () {
      var tableRowFilter = $filter('mlhrTableRowFilter');
      return angular.isArray($scope.rows) ? tableRowFilter($scope.rows, $scope.columns, $scope.searchTerms, $scope.filterState) : [];
    };
    $scope.isSelectedAll = function () {
      if (!angular.isArray($scope.rows) || !angular.isArray($scope.selected)) {
        return false;
      }
      var rows = $scope.getSelectableRows();
      return rows.length > 0 && rows.length === $scope.selected.length;
    };
    $scope.selectAll = function () {
      $scope.deselectAll();
      // Get a list of filtered rows
      var rows = $scope.getSelectableRows();
      if (rows.length <= 0)
        return;
      var columns = $scope.columns;
      var selectorKey = null;
      var selectObject = null;
      // Search for selector key in selector column
      for (var i = 0; i < columns.length; i++) {
        if (columns[i].selector) {
          selectorKey = columns[i].key;
          selectObject = columns[i].selectObject;
          break;
        }
      }
      // Verify that selectorKey was found
      if (!selectorKey) {
        throw new Error('Unable to find selector column key for selectAll');
      }
      //select key or entire object from all rows
      for (var i = 0; i < rows.length; i++) {
        $scope.selected.push(selectObject ? rows[i] : rows[i][selectorKey]);
      }
    };
    $scope.deselectAll = function () {
      while ($scope.selected.length > 0) {
        $scope.selected.pop();
      }
    };
    $scope.toggleSelectAll = function ($event) {
      var checkbox = $event.target;
      if (checkbox.checked) {
        $scope.selectAll();
      } else {
        $scope.deselectAll();
      }
    };
    $scope.addSort = function (id, dir) {
      var idx = $scope.sortOrder.indexOf(id);
      if (idx === -1) {
        $scope.sortOrder.push(id);
      }
      $scope.sortDirection[id] = dir;
    };
    $scope.removeSort = function (id) {
      var idx = $scope.sortOrder.indexOf(id);
      if (idx !== -1) {
        $scope.sortOrder.splice(idx, 1);
      }
      delete $scope.sortDirection[id];
    };
    $scope.clearSort = function () {
      $scope.sortOrder = [];
      $scope.sortDirection = {};
    };
    // Checks if columns have any filter fileds
    $scope.hasFilterFields = function () {
      for (var i = $scope.columns.length - 1; i >= 0; i--) {
        if (typeof $scope.columns[i].filter !== 'undefined') {
          return true;
        }
      }
      return false;
    };
    // Clears search field for column, focus on input
    $scope.clearAndFocusSearch = function (columnId) {
      $scope.searchTerms[columnId] = '';
      $element.find('tr.mlhr-table-filter-row th.column-' + columnId + ' input').focus();
    };
    // Toggles column sorting
    $scope.toggleSort = function ($event, column) {
      // check if even sortable
      if (!column.sort) {
        return;
      }
      if ($event.shiftKey) {
        // shift is down, ignore other columns
        // but toggle between three states
        switch ($scope.sortDirection[column.id]) {
        case '+':
          // Make descending
          $scope.sortDirection[column.id] = '-';
          break;
        case '-':
          // Remove from sortOrder and direction
          $scope.removeSort(column.id);
          break;
        default:
          // Make ascending
          $scope.addSort(column.id, '+');
          break;
        }
      } else {
        // shift is not down, disable other
        // columns but toggle two states
        var lastState = $scope.sortDirection[column.id];
        $scope.clearSort();
        if (lastState === '+') {
          $scope.addSort(column.id, '-');
        } else {
          $scope.addSort(column.id, '+');
        }
      }
      $scope.saveToStorage();
    };
    // Retrieve className for given sorting state
    $scope.getSortClass = function (sorting) {
      var classes = $scope.options.sortClasses;
      if (sorting === '+') {
        return classes[1];
      }
      if (sorting === '-') {
        return classes[2];
      }
      return classes[0];
    };
    $scope.setColumns = function (columns) {
      $scope.columns = columns;
      $scope.columns.forEach(function (column) {
        // formats
        var format = column.format;
        if (typeof format !== 'function') {
          if (typeof format === 'string') {
            if (typeof formats[format] === 'function') {
              column.format = formats[format];
            } else {
              try {
                column.format = $filter(format);
              } catch (e) {
                delete column.format;
                $log.warn('format function reference in column(id=' + column.id + ') ' + 'was not found in built-in format functions or $filters. ' + 'format function given: "' + format + '". ' + 'Available built-ins: ' + Object.keys(formats).join(',') + '. ' + 'If you supplied a $filter, ensure it is available on this module');
              }
            }
          } else {
            delete column.format;
          }
        }
        // sort
        var sort = column.sort;
        if (typeof sort !== 'function') {
          if (typeof sort === 'string') {
            if (typeof sorts[sort] === 'function') {
              column.sort = sorts[sort](column.key);
            } else {
              delete column.sort;
              $log.warn('sort function reference in column(id=' + column.id + ') ' + 'was not found in built-in sort functions. ' + 'sort function given: "' + sort + '". ' + 'Available built-ins: ' + Object.keys(sorts).join(',') + '. ');
            }
          } else {
            delete column.sort;
          }
        }
        // filter
        var filter = column.filter;
        if (typeof filter !== 'function') {
          if (typeof filter === 'string') {
            if (typeof filters[filter] === 'function') {
              column.filter = filters[filter];
            } else {
              delete column.filter;
              $log.warn('filter function reference in column(id=' + column.id + ') ' + 'was not found in built-in filter functions. ' + 'filter function given: "' + filter + '". ' + 'Available built-ins: ' + Object.keys(filters).join(',') + '. ');
            }
          } else {
            delete column.filter;
          }
        }
      });
    };
    $scope.startColumnResize = function ($event, column) {
      // Stop default so text does not get selected
      $event.preventDefault();
      $event.originalEvent.preventDefault();
      $event.stopPropagation();
      // init variable for new width
      var new_width = false;
      // store initial mouse position
      var initial_x = $event.pageX;
      // create marquee element
      var $m = $('<div class="column-resizer-marquee"></div>');
      // append to th
      var $th = $($event.target).parent('th');
      $th.append($m);
      // set initial marquee dimensions
      var initial_width = $th.outerWidth();
      function mousemove(e) {
        // calculate changed width
        var current_x = e.pageX;
        var diff = current_x - initial_x;
        new_width = initial_width + diff;
        // update marquee dimensions
        $m.css('width', new_width + 'px');
      }
      $m.css({
        width: initial_width + 'px',
        height: $th.outerHeight() + 'px'
      });
      // set mousemove listener
      $($window).on('mousemove', mousemove);
      // set mouseup/mouseout listeners
      $($window).one('mouseup', function (e) {
        e.stopPropagation();
        // remove marquee, remove window mousemove listener
        $m.remove();
        $($window).off('mousemove', mousemove);
        // set new width on th
        // if a new width was set
        if (new_width === false) {
          delete column.width;
        } else {
          column.width = Math.max(new_width, 0);
        }
        $scope.$apply();
      });
    };
    $scope.sortableOptions = {
      axis: 'x',
      handle: '.column-text',
      helper: 'clone',
      placeholder: 'mlhr-table-column-placeholder',
      distance: 5
    };
    $scope.getActiveColCount = function () {
      var count = 0;
      $scope.columns.forEach(function (col) {
        if (!col.disabled) {
          count++;
        }
      });
      return count;
    };
    $scope.saveToStorage = function () {
      if (!$scope.storage) {
        return;
      }
      // init object to stringify/save
      var state = {};
      // save state objects
      [
        'sortOrder',
        'sortDirection',
        'searchTerms'
      ].forEach(function (prop) {
        state[prop] = $scope[prop];
      });
      // serialize columns
      state.columns = $scope.columns.map(function (col) {
        return {
          id: col.id,
          disabled: !!col.disabled
        };
      });
      // save non-transient options
      state.options = {};
      [
        'rowLimit',
        'pagingScheme',
        'storageHash'
      ].forEach(function (prop) {
        state.options[prop] = $scope.options[prop];
      });
      // Save to storage
      $scope.storage.setItem($scope.storageKey, JSON.stringify(state));
    };
    $scope.loadFromStorage = function () {
      if (!$scope.storage) {
        return;
      }
      // Attempt to parse the storage
      var stateString = $scope.storage.getItem($scope.storageKey);
      // Was it there?
      if (!stateString) {
        return;
      }
      // Try to parse it
      var state;
      try {
        state = JSON.parse(stateString);
        // if mimatched storage hash, stop loading from storage
        if (state.options.storageHash !== $scope.options.storageHash) {
          return;
        }
        // load state objects
        [
          'sortOrder',
          'sortDirection',
          'searchTerms'
        ].forEach(function (prop) {
          $scope[prop] = state[prop];
        });
        // validate (compare ids)
        // reorder columns and merge
        var column_ids = state.columns.map(function (col) {
            return col.id;
          });
        $scope.columns.sort(function (a, b) {
          var aNotThere = column_ids.indexOf(a.id) === -1;
          var bNotThere = column_ids.indexOf(b.id) === -1;
          if (aNotThere && bNotThere) {
            return 0;
          }
          if (aNotThere) {
            return 1;
          }
          if (bNotThere) {
            return -1;
          }
          return column_ids.indexOf(a.id) - column_ids.indexOf(b.id);
        });
        $scope.columns.forEach(function (col, i) {
          ['disabled'].forEach(function (prop) {
            col[prop] = state.columns[i][prop];
          });
        });
        // load options
        [
          'rowLimit',
          'pagingScheme',
          'storageHash'
        ].forEach(function (prop) {
          $scope.options[prop] = state.options[prop];
        });
      } catch (e) {
        $log.warn('Loading from storage failed!');
      }
    };
    $scope.calculateRowLimit = function () {
      var rowHeight = $scope.scrollDiv.find('.mlhr-table-rendered-rows tr').height();
      $scope.rowHeight = rowHeight || $scope.options.defaultRowHeight;
      $scope.rowLimit = Math.ceil($scope.options.bodyHeight / rowHeight) + $scope.options.rowPadding * 2;
    };
  }
]);
// Source: dist/directives/mlhrTable.js
/*
* Copyright (c) 2013 DataTorrent, Inc. ALL Rights Reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
angular.module('datatorrent.mlhrTable.directives.mlhrTable', [
  'datatorrent.mlhrTable.controllers.MlhrTableController',
  'datatorrent.mlhrTable.directives.mlhrTableRows',
  'datatorrent.mlhrTable.directives.mlhrTableDummyRows'
]).directive('mlhrTable', [
  '$log',
  '$timeout',
  '$q',
  function ($log, $timeout, $q) {
    function debounce(func, wait, immediate) {
      var timeout, args, context, timestamp, result;
      var later = function () {
        var last = Date.now() - timestamp;
        if (last < wait && last > 0) {
          timeout = $timeout(later, wait - last);
        } else {
          timeout = null;
          if (!immediate) {
            result = func.apply(context, args);
            if (!timeout) {
              context = args = null;
            }
          }
        }
      };
      return function () {
        context = this;
        args = arguments;
        timestamp = Date.now();
        var callNow = immediate && !timeout;
        if (!timeout) {
          timeout = $timeout(later, wait);
        }
        if (callNow) {
          result = func.apply(context, args);
          context = args = null;
        }
        return result;
      };
    }
    function defaults(obj) {
      if (typeof obj !== 'object') {
        return obj;
      }
      for (var i = 1, length = arguments.length; i < length; i++) {
        var source = arguments[i];
        for (var prop in source) {
          if (obj[prop] === void 0) {
            obj[prop] = source[prop];
          }
        }
      }
      return obj;
    }
    function link(scope, element) {
      // Prevent following user input objects from being modified by making deep copies of originals
      scope.columns = angular.copy(scope._columns);
      // Look for built-in filter, sort, and format functions
      if (scope.columns instanceof Array) {
        scope.setColumns(scope.columns);
      } else {
        throw new Error('"columns" array not found in mlhrTable scope!');
      }
      if (scope.options !== undefined && {}.hasOwnProperty.call(scope.options, 'getter')) {
        if (typeof scope.options.getter !== 'function') {
          throw new Error('"getter" in "options" should be a function!');
        }
      }
      // Check for rows
      // if ( !(scope.rows instanceof Array) ) {
      //   throw new Error('"rows" array not found in mlhrTable scope!');
      // }
      // Object that holds search terms
      scope.searchTerms = {};
      // Array and Object for sort order+direction
      scope.sortOrder = [];
      scope.sortDirection = {};
      // Holds filtered rows count
      scope.filterState = { filterCount: scope.rows ? scope.rows.length : 0 };
      // Offset and limit
      scope.rowOffset = 0;
      scope.rowLimit = 10;
      // Default Options, extend provided ones
      scope.options = scope.options || {};
      defaults(scope.options, {
        bgSizeMultiplier: 1,
        rowPadding: 10,
        bodyHeight: 300,
        fixedHeight: false,
        defaultRowHeight: 40,
        scrollDebounce: 100,
        scrollDivisor: 1,
        loadingText: 'loading',
        loadingError: false,
        noRowsText: 'no rows',
        trackBy: scope.trackBy,
        sortClasses: [
          'glyphicon glyphicon-sort',
          'glyphicon glyphicon-chevron-up',
          'glyphicon glyphicon-chevron-down'
        ],
        onRegisterApi: function (api) {
        }
      });
      // Look for initial sort order
      if (scope.options.initialSorts) {
        angular.forEach(scope.options.initialSorts, function (sort) {
          scope.addSort(sort.id, sort.dir);
        });
      }
      // Check for localStorage persistence
      if (scope.options.storage && scope.options.storageKey) {
        // Set the storage object on the scope
        scope.storage = scope.options.storage;
        scope.storageKey = scope.options.storageKey;
        // Try loading from storage
        scope.loadFromStorage();
        // Watch various things to save state
        //  Save state on the following action:
        //  - sort change
        //  occurs in $scope.toggleSort
        //  - column order change 
        scope.$watchCollection('columns', scope.saveToStorage);
        //  - search terms change
        scope.$watchCollection('searchTerms', scope.saveToStorage);
        //  - paging scheme
        scope.$watch('options.pagingScheme', scope.saveToStorage);
        //  - row limit
        scope.$watch('options.bodyHeight', function () {
          scope.calculateRowLimit();
          scope.tbodyNgStyle = {};
          scope.tbodyNgStyle[scope.options.fixedHeight ? 'height' : 'max-height'] = scope.options.bodyHeight + 'px';
          scope.saveToStorage();
        });
        scope.$watch('filterState.filterCount', function () {
          scope.onScroll();
        });
        scope.$watch('rowHeight', function (size) {
          element.find('tr.mlhr-table-dummy-row').css('background-size', 'auto ' + size * scope.options.bgSizeMultiplier + 'px');
        });  //  - when column gets enabled or disabled
             //  TODO
      }
      var scrollDeferred;
      var debouncedScrollHandler = debounce(function () {
          scope.calculateRowLimit();
          var scrollTop = scope.scrollDiv[0].scrollTop;
          var rowHeight = scope.rowHeight;
          if (rowHeight === 0) {
            return false;
          }
          scope.rowOffset = Math.max(0, Math.floor(scrollTop / rowHeight) - scope.options.rowPadding);
          scrollDeferred.resolve();
          scrollDeferred = null;
          scope.options.scrollingPromise = null;
          scope.$digest();
        }, scope.options.scrollDebounce);
      scope.onScroll = function () {
        if (!scrollDeferred) {
          scrollDeferred = $q.defer();
          scope.options.scrollingPromise = scrollDeferred.promise;
        }
        debouncedScrollHandler();
      };
      scope.scrollDiv = element.find('.mlhr-rows-table-wrapper');
      scope.scrollDiv.on('scroll', scope.onScroll);
      // Wait for a render
      $timeout(function () {
        // Calculates rowHeight and rowLimit
        scope.calculateRowLimit();
      }, 0);
      scope.api = {
        isSelectedAll: scope.isSelectedAll,
        selectAll: scope.selectAll,
        deselectAll: scope.deselectAll,
        toggleSelectAll: scope.toggleSelectAll,
        setLoading: function (isLoading, triggerDigest) {
          scope.options.loading = isLoading;
          if (triggerDigest) {
            scope.$digest();
          }
        }
      };
      // Register API
      scope.options.onRegisterApi(scope.api);
      //Check if loadingPromise was supplied and appears to be a promise object
      if (angular.isObject(scope.options.loadingPromise) && typeof scope.options.loadingPromise.then === 'function') {
        scope.options.loadingPromise.then(function () {
          scope.options.loadingError = false;
          scope.api.setLoading(false);
        }, function (reason) {
          scope.options.loadingError = true;
          scope.api.setLoading(false);
          $log.warn('Failed loading table data: ' + reason);
        });
      }
    }
    return {
      templateUrl: 'src/templates/mlhrTable.tpl.html',
      restrict: 'EA',
      replace: true,
      scope: {
        _columns: '=columns',
        rows: '=',
        classes: '@tableClass',
        selected: '=',
        options: '=?',
        trackBy: '@?'
      },
      controller: 'MlhrTableController',
      compile: function (tElement) {
        var trackBy = tElement.attr('track-by');
        if (trackBy) {
          tElement.find('.mlhr-table-rendered-rows').attr('track-by', trackBy);
        }
        return link;
      }
    };
  }
]);
// Source: dist/directives/mlhrTableCell.js
/*
* Copyright (c) 2013 DataTorrent, Inc. ALL Rights Reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
angular.module('datatorrent.mlhrTable.directives.mlhrTableCell', ['datatorrent.mlhrTable.directives.mlhrTableSelector']).directive('mlhrTableCell', [
  '$compile',
  function ($compile) {
    function link(scope, element) {
      var column = scope.column;
      var cellMarkup = '';
      if (column.template) {
        cellMarkup = column.template;
      } else if (column.templateUrl) {
        cellMarkup = '<div ng-include="\'' + column.templateUrl + '\'"></div>';
      } else if (column.selector === true) {
        cellMarkup = '<input type="checkbox" ng-checked="selected.indexOf(column.selectObject ? row : row[column.key]) >= 0" mlhr-table-selector class="mlhr-table-selector" />';
      } else if (column.ngFilter) {
        cellMarkup = '{{ row[column.key] | ' + column.ngFilter + '}}';
      } else if (column.format) {
        cellMarkup = '{{ column.format(row[column.key], row, column) }}';
      } else if (scope.options !== undefined && {}.hasOwnProperty.call(scope.options, 'getter')) {
        cellMarkup = '{{ options.getter(column.key, row) }}';
      } else {
        cellMarkup = '{{ row[column.key] }}';
      }
      element.html(cellMarkup);
      $compile(element.contents())(scope);
    }
    return {
      scope: true,
      link: link
    };
  }
]);
// Source: dist/directives/mlhrTableDummyRows.js
/*
* Copyright (c) 2013 DataTorrent, Inc. ALL Rights Reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
/**
 * @ngdoc directive
 * @name datatorrent.mlhrTable.directive:mlhrTableDummyRows
 * @restrict A
 * @description inserts dummy <tr>s for non-rendered rows
 * @element tbody
 * @example <tbody mlhr-table-dummy-rows="[number]" columns="[column array]"></tbody>
**/
angular.module('datatorrent.mlhrTable.directives.mlhrTableDummyRows', []).directive('mlhrTableDummyRows', function () {
  return {
    template: '<tr class="mlhr-table-dummy-row" ng-style="{ height: dummyRowHeight + \'px\'}"><td ng-show="dummyRowHeight" ng-attr-colspan="{{columns.length}}"></td></tr>',
    scope: true,
    link: function (scope, element, attrs) {
      scope.$watch(attrs.mlhrTableDummyRows, function (count) {
        scope.dummyRowHeight = count * scope.rowHeight;
      });
    }
  };
});
// Source: dist/directives/mlhrTableRows.js
/*
* Copyright (c) 2013 DataTorrent, Inc. ALL Rights Reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
angular.module('datatorrent.mlhrTable.directives.mlhrTableRows', [
  'datatorrent.mlhrTable.directives.mlhrTableCell',
  'datatorrent.mlhrTable.filters.mlhrTableRowFilter',
  'datatorrent.mlhrTable.filters.mlhrTableRowSorter'
]).directive('mlhrTableRows', [
  '$filter',
  function ($filter) {
    var tableRowFilter = $filter('mlhrTableRowFilter');
    var tableRowSorter = $filter('mlhrTableRowSorter');
    var limitTo = $filter('limitTo');
    function calculateVisibleRows(scope) {
      // scope.rows
      var visible_rows;
      // | tableRowFilter:columns:searchTerms:filterState 
      visible_rows = tableRowFilter(scope.rows, scope.columns, scope.searchTerms, scope.filterState, scope.options);
      // | tableRowSorter:columns:sortOrder:sortDirection 
      visible_rows = tableRowSorter(visible_rows, scope.columns, scope.sortOrder, scope.sortDirection, scope.options);
      // | limitTo:rowOffset - filterState.filterCount 
      visible_rows = limitTo(visible_rows, Math.floor(scope.rowOffset) - scope.filterState.filterCount);
      // | limitTo:rowLimit
      visible_rows = limitTo(visible_rows, scope.rowLimit + Math.ceil(scope.rowOffset % 1));
      return visible_rows;
    }
    function link(scope) {
      var updateHandler = function () {
        if (scope.rows) {
          scope.visible_rows = calculateVisibleRows(scope);
        }
      };
      scope.$watch('searchTerms', updateHandler, true);
      scope.$watchGroup([
        'filterState.filterCount',
        'rowOffset',
        'rowLimit'
      ], updateHandler);
      scope.$watch('sortOrder', updateHandler, true);
      scope.$watch('sortDirection', updateHandler, true);
      scope.$watch('rows', updateHandler, true);
    }
    return {
      restrict: 'A',
      templateUrl: 'src/templates/mlhrTableRows.tpl.html',
      compile: function (tElement, tAttrs) {
        var tr = tElement.find('tr');
        var repeatString = tr.attr('ng-repeat');
        repeatString += tAttrs.trackBy ? ' track by row[options.trackBy]' : ' track by $index';
        tr.attr('ng-repeat', repeatString);
        return link;
      }
    };
  }
]);
// Source: dist/directives/mlhrTableSelector.js
/*
* Copyright (c) 2013 DataTorrent, Inc. ALL Rights Reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
angular.module('datatorrent.mlhrTable.directives.mlhrTableSelector', []).directive('mlhrTableSelector', function () {
  return {
    restrict: 'A',
    scope: false,
    link: function postLink(scope, element) {
      var selected = scope.selected;
      var row = scope.row;
      var column = scope.column;
      element.on('click', function () {
        // Retrieve position in selected list
        var idx = selected.indexOf(column.selectObject ? row : row[column.key]);
        // it is selected, deselect it:
        if (idx >= 0) {
          selected.splice(idx, 1);
        }  // it is not selected, push to list
        else {
          selected.push(column.selectObject ? row : row[column.key]);
        }
        scope.$apply();
      });
    }
  };
});
// Source: dist/filters/mlhrTableRowFilter.js
/*
* Copyright (c) 2013 DataTorrent, Inc. ALL Rights Reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
angular.module('datatorrent.mlhrTable.filters.mlhrTableRowFilter', ['datatorrent.mlhrTable.services.mlhrTableFilterFunctions']).filter('mlhrTableRowFilter', [
  'mlhrTableFilterFunctions',
  '$log',
  function (tableFilterFunctions, $log) {
    return function tableRowFilter(rows, columns, searchTerms, filterState, options) {
      var enabledFilterColumns, result = rows;
      // gather enabled filter functions
      enabledFilterColumns = columns.filter(function (column) {
        // check search term
        var term = searchTerms[column.id];
        if (searchTerms.hasOwnProperty(column.id) && typeof term === 'string') {
          // filter empty strings and whitespace
          if (!term.trim()) {
            return false;
          }
          // check search filter function
          if (typeof column.filter === 'function') {
            return true;
          }
          // not a function, check for predefined filter function
          var predefined = tableFilterFunctions[column.filter];
          if (typeof predefined === 'function') {
            column.filter = predefined;
            return true;
          }
          $log.warn('mlhrTable: The filter function "' + column.filter + '" ' + 'specified by column(id=' + column.id + ').filter ' + 'was not found in predefined tableFilterFunctions. ' + 'Available filters: "' + Object.keys(tableFilterFunctions).join('","') + '"');
        }
        return false;
      });
      // loop through rows and filter on every enabled function
      if (enabledFilterColumns.length) {
        result = rows.filter(function (row) {
          for (var i = enabledFilterColumns.length - 1; i >= 0; i--) {
            var col = enabledFilterColumns[i];
            var filter = col.filter;
            var term = searchTerms[col.id];
            var value = options !== undefined && {}.hasOwnProperty.call(options, 'getter') ? options.getter(col.key, row) : row[col.key];
            var computedValue = typeof col.format === 'function' ? col.format(value, row) : value;
            if (!filter(term, value, computedValue, row)) {
              return false;
            }
          }
          return true;
        });
      }
      filterState.filterCount = result.length;
      return result;
    };
  }
]);
// Source: dist/filters/mlhrTableRowSorter.js
/*
* Copyright (c) 2013 DataTorrent, Inc. ALL Rights Reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
angular.module('datatorrent.mlhrTable.filters.mlhrTableRowSorter', []).filter('mlhrTableRowSorter', function () {
  var column_cache = {};
  function getColumn(columns, id) {
    if (column_cache.hasOwnProperty(id)) {
      return column_cache[id];
    }
    for (var i = columns.length - 1; i >= 0; i--) {
      if (columns[i].id === id) {
        column_cache[id] = columns[i];
        return columns[i];
      }
    }
  }
  return function tableRowSorter(rows, columns, sortOrder, sortDirection, options) {
    if (!sortOrder.length) {
      return rows;
    }
    var arrayCopy = [];
    for (var i = 0; i < rows.length; i++) {
      arrayCopy.push(rows[i]);
    }
    return arrayCopy.sort(function (a, b) {
      for (var i = 0; i < sortOrder.length; i++) {
        var id = sortOrder[i];
        var column = getColumn(columns, id);
        var dir = sortDirection[id];
        if (column && column.sort) {
          var fn = column.sort;
          var result = dir === '+' ? fn(a, b, options) : fn(b, a, options);
          if (result !== 0) {
            return result;
          }
        }
      }
      return 0;
    });
  };
});
// Source: dist/mlhr-table.js
/*
* Copyright (c) 2013 DataTorrent, Inc. ALL Rights Reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
angular.module('datatorrent.mlhrTable', [
  'datatorrent.mlhrTable.templates',
  'ui.sortable',
  'ngSanitize',
  'datatorrent.mlhrTable.directives.mlhrTable'
]);
// Source: dist/services/mlhrTableFilterFunctions.js
/*
* Copyright (c) 2013 DataTorrent, Inc. ALL Rights Reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
angular.module('datatorrent.mlhrTable.services.mlhrTableFilterFunctions', []).service('mlhrTableFilterFunctions', function () {
  function like(term, value) {
    term = term.toLowerCase().trim();
    value = value.toLowerCase();
    var first = term[0];
    // negate
    if (first === '!') {
      term = term.substr(1);
      if (term === '') {
        return true;
      }
      return value.indexOf(term) === -1;
    }
    // strict
    if (first === '=') {
      term = term.substr(1);
      return term === value.trim();
    }
    // remove escaping backslashes
    term = term.replace('\\!', '!');
    term = term.replace('\\=', '=');
    return value.indexOf(term) !== -1;
  }
  function likeFormatted(term, value, computedValue, row) {
    return like(term, computedValue, computedValue, row);
  }
  like.placeholder = likeFormatted.placeholder = 'string search';
  like.title = likeFormatted.title = 'Search by text, eg. "foo". Use "!" to exclude and "=" to match exact text, e.g. "!bar" or "=baz".';
  function number(term, value) {
    value = parseFloat(value);
    term = term.trim();
    var first_two = term.substr(0, 2);
    var first_char = term[0];
    var against_1 = term.substr(1) * 1;
    var against_2 = term.substr(2) * 1;
    if (first_two === '<=') {
      return value <= against_2;
    }
    if (first_two === '>=') {
      return value >= against_2;
    }
    if (first_char === '<') {
      return value < against_1;
    }
    if (first_char === '>') {
      return value > against_1;
    }
    if (first_char === '~') {
      return Math.round(value) === against_1;
    }
    if (first_char === '=') {
      return against_1 === value;
    }
    return value.toString().indexOf(term.toString()) > -1;
  }
  function numberFormatted(term, value, computedValue) {
    return number(term, computedValue);
  }
  number.placeholder = numberFormatted.placeholder = 'number search';
  number.title = numberFormatted.title = 'Search by number, e.g. "123". Optionally use comparator expressions like ">=10" or "<1000". Use "~" for approx. int values, eg. "~3" will match "3.2"';
  var unitmap = {};
  unitmap.second = unitmap.sec = unitmap.s = 1000;
  unitmap.minute = unitmap.min = unitmap.m = unitmap.second * 60;
  unitmap.hour = unitmap.hr = unitmap.h = unitmap.minute * 60;
  unitmap.day = unitmap.d = unitmap.hour * 24;
  unitmap.week = unitmap.wk = unitmap.w = unitmap.day * 7;
  unitmap.month = unitmap.week * 4;
  unitmap.year = unitmap.yr = unitmap.y = unitmap.day * 365;
  var clauseExp = /(\d+(?:\.\d+)?)\s*([a-z]+)/;
  function parseDateFilter(string) {
    // split on clauses (if any)
    var clauses = string.trim().split(',');
    var total = 0;
    // parse each clause
    for (var i = 0; i < clauses.length; i++) {
      var clause = clauses[i].trim();
      var terms = clauseExp.exec(clause);
      if (!terms) {
        continue;
      }
      var count = terms[1] * 1;
      var unit = terms[2].replace(/s$/, '');
      if (!unitmap.hasOwnProperty(unit)) {
        continue;
      }
      total += count * unitmap[unit];
    }
    return total;
  }
  function date(term, value) {
    // today
    // yesterday
    // 1 day ago
    // 2 days ago
    // < 1 day ago
    // < 10 minutes ago
    // < 10 min ago
    // < 10 minutes, 50 seconds ago
    // > 10 min, 30 sec ago
    // > 2 days ago
    // >= 1 day ago
    term = term.trim();
    if (!term) {
      return true;
    }
    value *= 1;
    var nowDate = new Date();
    var now = +nowDate;
    var first_char = term[0];
    var other_chars = term.substr(1).trim();
    var lowerbound, upperbound;
    if (first_char === '<') {
      lowerbound = now - parseDateFilter(other_chars);
      return value > lowerbound;
    }
    if (first_char === '>') {
      upperbound = now - parseDateFilter(other_chars);
      return value < upperbound;
    }
    if (term === 'today') {
      return new Date(value).toDateString() === nowDate.toDateString();
    }
    if (term === 'yesterday') {
      return new Date(value).toDateString() === new Date(now - unitmap.d).toDateString();
    }
    var supposedDate = new Date(term);
    if (!isNaN(supposedDate)) {
      return new Date(value).toDateString() === supposedDate.toDateString();
    }
    return false;
  }
  date.placeholder = 'date search';
  date.title = 'Search by date. Enter a date string (RFC2822 or ISO 8601 date). You can also type "today", "yesterday", "> 2 days ago", "< 1 day 2 hours ago", etc.';
  return {
    like: like,
    likeFormatted: likeFormatted,
    number: number,
    numberFormatted: numberFormatted,
    date: date
  };
});
// Source: dist/services/mlhrTableFormatFunctions.js
/*
* Copyright (c) 2013 DataTorrent, Inc. ALL Rights Reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
angular.module('datatorrent.mlhrTable.services.mlhrTableFormatFunctions', []).service('mlhrTableFormatFunctions', function () {
  // TODO: add some default format functions
  return {};
});
// Source: dist/services/mlhrTableSortFunctions.js
/*
* Copyright (c) 2013 DataTorrent, Inc. ALL Rights Reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
angular.module('datatorrent.mlhrTable.services.mlhrTableSortFunctions', []).service('mlhrTableSortFunctions', function () {
  return {
    number: function (field) {
      return function (row1, row2, options) {
        var val1, val2;
        if (options !== undefined && {}.hasOwnProperty.call(options, 'getter')) {
          val1 = options.getter(field, row1);
          val2 = options.getter(field, row2);
        } else {
          val1 = row1[field];
          val2 = row2[field];
        }
        return val1 * 1 - val2 * 1;
      };
    },
    string: function (field) {
      return function (row1, row2, options) {
        var val1, val2;
        if (options !== undefined && {}.hasOwnProperty.call(options, 'getter')) {
          val1 = options.getter(field, row1);
          val2 = options.getter(field, row2);
        } else {
          val1 = row1[field];
          val2 = row2[field];
        }
        if (val1.toString().toLowerCase() === val2.toString().toLowerCase()) {
          return 0;
        }
        return val1.toString().toLowerCase() > val2.toString().toLowerCase() ? 1 : -1;
      };
    }
  };
});
// Source: dist/templates.js
angular.module('datatorrent.mlhrTable.templates', [
  'src/templates/mlhrTable.tpl.html',
  'src/templates/mlhrTableDummyRows.tpl.html',
  'src/templates/mlhrTableRows.tpl.html'
]);
angular.module('src/templates/mlhrTable.tpl.html', []).run([
  '$templateCache',
  function ($templateCache) {
    $templateCache.put('src/templates/mlhrTable.tpl.html', '<div class="mlhr-table-wrapper">\n' + '  <table ng-class="classes" class="mlhr-table mlhr-header-table">\n' + '    <thead>\n' + '      <tr ui-sortable="sortableOptions" ng-model="columns">\n' + '        <th \n' + '          scope="col" \n' + '          ng-repeat="column in columns" \n' + '          ng-click="toggleSort($event,column)" \n' + '          ng-class="{\'sortable-column\' : column.sort, \'select-column\': column.selector}" \n' + '          ng-attr-title="{{ column.title || \'\' }}"\n' + '          ng-style="{ width: column.width, \'min-width\': column.width, \'max-width\': column.width }"\n' + '        >\n' + '          <span class="column-text">\n' + '            <input ng-if="column.selector" type="checkbox" ng-checked="isSelectedAll()" ng-click="toggleSelectAll($event)" />\n' + '            {{column.hasOwnProperty(\'label\') ? column.label : column.id }}\n' + '            <span \n' + '              ng-if="column.sort" \n' + '              title="This column is sortable. Click to toggle sort order. Hold shift while clicking multiple columns to stack sorting."\n' + '              class="sorting-icon {{ getSortClass( sortDirection[column.id] ) }}"\n' + '            ></span>\n' + '          </span>\n' + '          <span \n' + '            ng-if="!column.lockWidth"\n' + '            ng-class="{\'discreet-width\': !!column.width, \'column-resizer\': true}"\n' + '            title="Click and drag to set discreet width. Click once to clear discreet width."\n' + '            ng-mousedown="startColumnResize($event, column)"\n' + '          >\n' + '            &nbsp;\n' + '          </span>\n' + '        </th>\n' + '      </tr>\n' + '      <tr ng-if="hasFilterFields()" class="mlhr-table-filter-row">\n' + '        <td ng-repeat="column in columns" ng-class="\'column-\' + column.id">\n' + '          <input \n' + '            type="text"\n' + '            ng-if="(column.filter)"\n' + '            ng-model="searchTerms[column.id]"\n' + '            ng-attr-placeholder="{{ column.filter && column.filter.placeholder }}"\n' + '            ng-attr-title="{{ column.filter && column.filter.title }}"\n' + '            ng-class="{\'active\': searchTerms[column.id] }"\n' + '          >\n' + '          <button \n' + '            ng-if="(column.filter)"\n' + '            ng-show="searchTerms[column.id]"\n' + '            class="clear-search-btn"\n' + '            role="button"\n' + '            type="button"\n' + '            ng-click="clearAndFocusSearch(column.id)"\n' + '          >\n' + '            &times;\n' + '          </button>\n' + '\n' + '        </td>\n' + '      </tr>\n' + '    </thead>\n' + '  </table>\n' + '  <div class="mlhr-rows-table-wrapper" ng-style="tbodyNgStyle">\n' + '    <table ng-class="classes" class="mlhr-table mlhr-rows-table">\n' + '      <thead>\n' + '        <th \n' + '            scope="col"\n' + '            ng-repeat="column in columns" \n' + '            ng-style="{ width: column.width, \'min-width\': column.width, \'max-width\': column.width }"\n' + '          ></th>\n' + '        </tr>\n' + '      </thead>\n' + '      <tbody>\n' + '        <tr ng-if="visible_rows.length === 0">\n' + '          <td ng-attr-colspan="{{columns.length}}" class="space-holder-row-cell">\n' + '            <div ng-if="options.loadingError">\n' + '              <div ng-if="!options.loading && options.loadingErrorTemplateUrl" ng-include="options.loadingErrorTemplateUrl"></div>\n' + '              <div ng-if="!options.loading && !options.loadingErrorTemplateUrl">{{ options.loadingErrorText }}</div>\n' + '            </div>\n' + '            <div ng-if="!options.loadingError">\n' + '              <div ng-if="options.loading && options.loadingTemplateUrl" ng-include="options.loadingTemplateUrl"></div>\n' + '              <div ng-if="options.loading && !options.loadingTemplateUrl">{{ options.loadingText }}</div>\n' + '              <div ng-if="!options.loading && options.noRowsTemplateUrl" ng-include="options.noRowsTemplateUrl"></div>\n' + '              <div ng-if="!options.loading && !options.noRowsTemplateUrl">{{ options.noRowsText }}</div>\n' + '            </div>\n' + '          </td>\n' + '        </tr>\n' + '      </tbody>\n' + '      <tbody mlhr-table-dummy-rows="rowOffset" columns="columns" cell-content="..."></tbody>\n' + '      <tbody mlhr-table-rows class="mlhr-table-rendered-rows"></tbody>\n' + '      <tbody mlhr-table-dummy-rows="filterState.filterCount - rowOffset - visible_rows.length" columns="columns" cell-content="..."></tbody>\n' + '    </table>\n' + '  </div>\n' + '</div>\n' + '');
  }
]);
angular.module('src/templates/mlhrTableDummyRows.tpl.html', []).run([
  '$templateCache',
  function ($templateCache) {
    $templateCache.put('src/templates/mlhrTableDummyRows.tpl.html', '');
  }
]);
angular.module('src/templates/mlhrTableRows.tpl.html', []).run([
  '$templateCache',
  function ($templateCache) {
    $templateCache.put('src/templates/mlhrTableRows.tpl.html', '<tr ng-repeat="row in visible_rows" ng-attr-class="{{ (rowOffset + $index) % 2 ? \'odd\' : \'even\' }}">\n' + '  <td ng-repeat="column in columns track by column.id" class="mlhr-table-cell" mlhr-table-cell></td>\n' + '</tr>');
  }
]);