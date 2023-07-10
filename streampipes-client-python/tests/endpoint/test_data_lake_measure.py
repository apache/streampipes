#
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

from datetime import datetime
from unittest import TestCase

from streampipes.endpoint.api.data_lake_measure import (
    DataLakeMeasureEndpoint,
    StreamPipesQueryValidationError,
)


class TestMeasurementGetQueryConfig(TestCase):
    def test_default(self):
        config_dict = {}
        measurement_config = DataLakeMeasureEndpoint._validate_query_params(query_params=config_dict)
        result = measurement_config.build_query_string()

        self.assertEqual("?limit=1000", result)

    def test_additional_param_given(self):
        config_dict = {"columns": ["time", "value_25"]}

        measurement_config = DataLakeMeasureEndpoint._validate_query_params(query_params=config_dict)
        result = measurement_config.build_query_string()

        self.assertEqual("?columns=time,value_25&limit=1000", result)

    def test_extra_param(self):
        config_dict = {"foo": "bar"}

        with self.assertRaises(StreamPipesQueryValidationError):
            DataLakeMeasureEndpoint._validate_query_params(query_params=config_dict)

    def test_alias_as_query_param(self):
        config_dict = {"page_no": 5}

        measurement_config = DataLakeMeasureEndpoint._validate_query_params(query_params=config_dict)
        result = measurement_config.build_query_string()

        self.assertEqual("?limit=1000&page=5", result)

    def test_datetime_validation(self):
        now = datetime.utcnow()

        config_dict = {"start_date": now, "end_date": now}
        measurement_config = DataLakeMeasureEndpoint._validate_query_params(query_params=config_dict)
        result = measurement_config.build_query_string()

        expected_ts = int(datetime.timestamp(now) * 1000)
        expected = f"?endDate={expected_ts}&limit=1000&startDate={expected_ts}"

        self.assertEqual(expected, result)

    def test_datetime_validation_no_datetime(self):
        config_dict = {"start_date": "test"}

        with self.assertRaises(StreamPipesQueryValidationError):
            DataLakeMeasureEndpoint._validate_query_params(query_params=config_dict)

    def test_columns_validation(self):
        # Column parameter validation tests:

        # 1. Valid column parameter values
        config_dict_one_col = {"columns": ["col1"]}
        config_dict_mul_col = {"columns": ["col1", "col2", "col3"]}
        config_dict_default_value = {"columns": None}

        # 2. Invalid column parameter values
        config_dict_whitespace_ending = {"columns": ["col1 "]}
        config_dict_semicolon = {"columns": "col1;col2"}
        config_dict_empty_list = {"columns": []}
        config_dict_string = {"columns": "colstring"}
        config_dict_integer = {"columns": 1}
        config_dict_tuple = {"columns": ("col1", "col2")}
        config_dict_list_with_non_strings = {"columns": ["col1", "col2", 12, ["co3"]]}
        config_dict_list_with_elems_containing_invalid_chars = {"columns": ["col1", "col2", "col,3", "col_4", "col 5"]}

        self.assertEqual(
            "?columns=col1&limit=1000",
            DataLakeMeasureEndpoint._validate_query_params(query_params=config_dict_one_col).build_query_string(),
        )
        self.assertEqual(
            "?columns=col1,col2,col3&limit=1000",
            DataLakeMeasureEndpoint._validate_query_params(query_params=config_dict_mul_col).build_query_string(),
        )
        self.assertEqual(
            "?limit=1000",
            DataLakeMeasureEndpoint._validate_query_params(query_params=config_dict_default_value).build_query_string(),
        )
        with self.assertRaises(StreamPipesQueryValidationError):
            DataLakeMeasureEndpoint._validate_query_params(query_params=config_dict_whitespace_ending)
        with self.assertRaises(StreamPipesQueryValidationError):
            DataLakeMeasureEndpoint._validate_query_params(query_params=config_dict_empty_list)
        with self.assertRaises(StreamPipesQueryValidationError):
            DataLakeMeasureEndpoint._validate_query_params(query_params=config_dict_semicolon)
        with self.assertRaises(StreamPipesQueryValidationError):
            DataLakeMeasureEndpoint._validate_query_params(query_params=config_dict_string)
        with self.assertRaises(StreamPipesQueryValidationError):
            DataLakeMeasureEndpoint._validate_query_params(query_params=config_dict_integer)
        with self.assertRaises(StreamPipesQueryValidationError):
            DataLakeMeasureEndpoint._validate_query_params(query_params=config_dict_tuple)
        with self.assertRaises(StreamPipesQueryValidationError):
            DataLakeMeasureEndpoint._validate_query_params(query_params=config_dict_list_with_non_strings)
        with self.assertRaises(StreamPipesQueryValidationError):
            DataLakeMeasureEndpoint._validate_query_params(
                query_params=config_dict_list_with_elems_containing_invalid_chars
            )

    def test_minium_parameter_values(self):
        config_dict_happy_path = {"limit": 15, "page_no": 3}

        config_dict_limit_too_low = {"limit": 0}

        config_dict_page_no_too_low = {"page_no": -2}

        measurement_config_happy = DataLakeMeasureEndpoint._validate_query_params(query_params=config_dict_happy_path)
        result_happy = measurement_config_happy.build_query_string()

        self.assertEqual("?limit=15&page=3", result_happy)

        with self.assertRaises(StreamPipesQueryValidationError):
            DataLakeMeasureEndpoint._validate_query_params(query_params=config_dict_limit_too_low)

        with self.assertRaises(StreamPipesQueryValidationError):
            DataLakeMeasureEndpoint._validate_query_params(query_params=config_dict_page_no_too_low)

    def test_literal_validation(self):
        config_invalid_order = {"order": "UP"}

        with self.assertRaises(StreamPipesQueryValidationError):
            DataLakeMeasureEndpoint._validate_query_params(query_params=config_invalid_order)
