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

## Contributing to StreamPipes

*Before opening a pull request*, review the [Get Involved](https://streampipes.apache.org/community/get-involved/) page.
It lists information that is required for contributing to StreamPipes.

When you contribute code, you affirm that the contribution is your original work and that you
license the work to the project under the project's open source license. Whether or not you
state this explicitly, by submitting any copyrighted material via pull request, email, or
other means you agree to license the material under the project's open source license and
warrant that you have the legal authority to do so.

---
### Description
This PR fixes a misleading variable name in the DataLakeManagementController class.

The variable `measurementID` in the `dropMeasurementSeries` method actually represents a measure name instead of a measurement ID.  
It has been renamed to `measureName` to improve clarity and maintain consistency.

### Type of Change
- Code refactoring (non-breaking change)

### Checklist
- [x] Code compiles successfully
- [x] No functional changes introduced
- [x] Variable renamed consistently across usages