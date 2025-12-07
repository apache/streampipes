<!--
  ~ Licensed to the Apache Software Foundation (ASF) under one or more
  ~ contributor license agreements.  See the NOTICE file distributed with
  ~ this work for additional information regarding copyright ownership.
  ~ The ASF licenses this file to You under the Apache License, Version 2.0
  ~ (the "License"); you may not use this file except in compliance with
  ~ the License.  You may obtain a copy of the License at
  ~
  ~    http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  ~
  -->

# UI Styleguide for B2B Angular Application

Welcome to the UI Styleguide for Apache StreamPipes.  
This guide defines the visual language, interaction patterns, components, and coding standards that ensure a consistent and scalable user experience.

---

### Design Tokens

- Color tokens: \_theme-colors.scss (can be overridden by users for individual theming)
- Custom variables: \_custom-variables.scss (can be overriden by users for individual theming)
- General variables: \_variables.scss
- Typography: \_typography.scss
- Spacing: \_spacing.scss

### 4. UI Components

#### Basic Layouts

Each page either starts with one of the following components:

```html
<sp-basic-view></sp-basic-view> <sp-basic-nav-tabs></sp-basic-nav-tabs>
```

The basic view renders a full-height panel. It also has a navbar.

#### Headers and Titles

There is a predefined component for showing page titles:

```html
<sp-basic-header-title-component [title]="A" [description]="B" [level]="1">
</sp-basic-header-title-component>
```

Level can be either 1, 2 or 3. Use level 1 for page titles, level 2 for pages with multiple headers such as the configuration page.

#### Sections

In some views it might make sense to organize the layout based on panels.

```html
<sp-split-section [title]="A" [subtitle]="B" [level]="2"> </sp-split-section>
```

Use level to control the size of the section header and margins.
In views with enough space, we use level=2. In dialogs and in dense layouts, we use level=3.

#### Buttons

Buttons are defined as follows:

```html
<button mat-flat-button>
  optional icon: use <mat-icon></mat-icon> wrap the text in a span blog:
  <span>Text</span>
</button>
```

Always use `mat-flat-button` style.

There are different forms of buttons that we can use:

- Primary buttons are rendered when no other CSS classes are applied. Primary buttons serve to identify an action.
- Secondary buttons are applied with the `btn-secondary` css class (for legacy reasons, also `mat-basic` is possible)
- Warning/Error buttons are applied with the `btn-warn` css class.

Smaller buttons can be applied with the `small-button` css class. Use small buttons only in dense layouts.

#### Forms

Never use the Angular Material `mat-label` and floating labels.

To show form inputs, we can wrap a form element into a `sp-form-field` block:

```html
<sp-form-field [level]="2" [label]="Label" [description]="Description">
  form content
</sp-form-field>
```

To render smaller inputs in a dense layout, assign the CSS class `form-field-small`.

You can also define an optional tooltip which is shown above the label.

#### Label

Use form labels to ensure a consistent layout of forms and labels.
In cases where the `sp-form-field` wrapper is not used, the label component can also be accessed directly:

```html
<sp-form-label [level]="2" [label]="Label" [description]="Description">
</sp-form-label>
```

#### Alert Banners

Alert banners are used to show error/info/warning/success messages.
Use it as follows:

```html
<sp-alert-banner type="info" [title]="Hello" [description]="World">
  Additional content
</sp-alert-banner>
```

Allowed types are `info`, `warning`, `error` and `success`.
You can also add additional content to the banner.

#### Tables

For rendering tables, always use the `sp-table` component which comes with pre-defined features for paging, sorting and layout.
In most cases, table actions should be shown in a popup menu to ensure a clean UI.
Check the examples to see how to add table actions.

### Localization & Internationalization (i18n)

Always prepare strings for translation:

```html
{{ 'XYZ' | translate }}
<my-component [label]="'ABC' | translate"></my-component>
```
