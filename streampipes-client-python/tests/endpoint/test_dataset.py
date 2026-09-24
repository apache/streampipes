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

from datetime import datetime, timezone
from json import loads
from unittest import TestCase
from urllib.parse import parse_qs

from pydantic import ValidationError

from streampipes.endpoint.api.data_lake_measure import (
    DataLakeMeasureEndpoint,
    StreamPipesQueryValidationError,
)
from streampipes.model.query import (
    AggregationFunction,
    Column,
    FilterCondition,
    FilterGroup,
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
        now = datetime.now(timezone.utc)

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

    def test_aggregation_query(self):
        config = DataLakeMeasureEndpoint._validate_query_params(
            {
                "columns": ["temperature"],
                "aggregation_function": "MEAN",
                "group_by": ["sensorId", "location"],
                "time_interval": "1m",
                "fill": "previous",
            }
        )
        self.assertEqual(
            parse_qs(config.build_query_string()[1:]),
            {
                "columns": ["temperature"],
                "limit": ["1000"],
                "aggregationFunction": ["MEAN"],
                "groupBy": ["sensorId,location"],
                "timeInterval": ["1m"],
                "fill": ["previous"],
            },
        )

    def test_per_column_aggregations(self):
        columns = ["[temperature;MEAN;average]", "[pressure;MAX]"]
        config = DataLakeMeasureEndpoint._validate_query_params({"columns": columns})
        self.assertEqual(parse_qs(config.build_query_string()[1:])["columns"], [",".join(columns)])

    def test_advanced_aliases(self):
        params = {
            "aggregationFunction": "SUM",
            "groupBy": ["sensor"],
            "timeInterval": "500ms",
            "fill": "-1.5",
            "countOnly": False,
            "autoAggregate": True,
            "missingValueBehaviour": "ignore",
            "maximumAmountOfEvents": -1,
        }
        config = DataLakeMeasureEndpoint._validate_query_params(params)
        result = parse_qs(config.build_query_string()[1:])
        self.assertEqual(
            result,
            {
                "limit": ["1000"],
                "aggregationFunction": ["SUM"],
                "groupBy": ["sensor"],
                "timeInterval": ["500ms"],
                "fill": ["-1.5"],
                "countOnly": ["false"],
                "autoAggregate": ["true"],
                "missingValueBehaviour": ["ignore"],
                "maximumAmountOfEvents": ["-1"],
            },
        )

    def test_filter_encoding(self):
        params = {
            "filter": "[sensor;=;A&B + #ü%]",
            "filter_expression": '{"operator":"AND","children":[]}',
        }
        config = DataLakeMeasureEndpoint._validate_query_params(params)
        self.assertEqual(
            parse_qs(config.build_query_string()[1:]),
            {
                "limit": ["1000"],
                "filter": [params["filter"]],
                "filterExpression": [params["filter_expression"]],
            },
        )

    def test_optional_advanced_params(self):
        config = DataLakeMeasureEndpoint._validate_query_params(
            {
                "group_by": None,
                "aggregation_function": None,
                "time_interval": None,
                "fill": None,
                "count_only": None,
                "auto_aggregate": False,
                "filter": None,
                "filter_expression": None,
                "missing_value_behaviour": None,
                "maximum_amount_of_events": None,
            }
        )
        self.assertEqual(config.build_query_string(), "?limit=1000")

    def test_invalid_advanced_params(self):
        for params in [
            {"aggregation_function": "AVERAGE"},
            {"group_by": []},
            {"group_by": "sensor"},
            {"group_by": ["sensor", 1]},
            {"group_by": ["sensor,bad"]},
            {"time_interval": "1x"},
            {"fill": "invalid"},
            {"count_only": "invalid"},
            {"auto_aggregate": "invalid"},
            {"missing_value_behaviour": "invalid"},
            {"maximum_amount_of_events": -2},
            {"columns": ["[temperature;INVALID]"]},
            {"columns": ["[temperature;MEAN;alias;extra]"]},
        ]:
            with self.subTest(params=params), self.assertRaises(StreamPipesQueryValidationError):
                DataLakeMeasureEndpoint._validate_query_params(params)

    def test_typed_columns(self):
        columns = [
            "sensorId",
            Column(name="mass_flow"),
            Column(name="temperature", aggregation=AggregationFunction.MAX, alias="peak_temperature"),
            Column(name="temperature", aggregation=AggregationFunction.MEAN),
            "[mass_flow;SUM;total_flow]",
        ]
        config = DataLakeMeasureEndpoint._validate_query_params({"columns": columns})
        self.assertEqual(
            parse_qs(config.build_query_string()[1:])["columns"],
            ["sensorId,mass_flow,[temperature;MAX;peak_temperature],[temperature;MEAN],[mass_flow;SUM;total_flow]"],
        )
        self.assertIsInstance(columns[1], Column)

    def test_aggregation_enum(self):
        for function in AggregationFunction:
            with self.subTest(function=function):
                config = DataLakeMeasureEndpoint._validate_query_params(
                    {
                        "columns": [Column(name="temperature")],
                        "aggregation_function": function,
                    }
                )
                self.assertEqual(parse_qs(config.build_query_string()[1:])["aggregationFunction"], [function.value])
                self.assertEqual(
                    Column(name="temperature", aggregation=function).to_query_string(),
                    f"[temperature;{function.value}]",
                )

    def test_invalid_typed_columns(self):
        for options in [
            {"name": "temperature", "alias": "renamed"},
            {"name": ""},
            {"name": "temperature;MAX"},
            {"name": "temperature", "aggregation": "UNKNOWN"},
            {"name": "temperature", "aggregation": "MAX", "alias": "bad;alias"},
            {"name": "temperature", "aggregation": "MAX", "alias": ""},
        ]:
            with self.subTest(options=options), self.assertRaises(ValidationError):
                Column.model_validate(options)
        with self.assertRaises(StreamPipesQueryValidationError):
            DataLakeMeasureEndpoint._validate_query_params({"group_by": [Column(name="sensorId")]})

    def test_typed_column_string_function(self):
        column = Column.model_validate({"name": "temperature", "aggregation": "MAX", "alias": "peak"})
        self.assertEqual(column.aggregation, AggregationFunction.MAX)
        self.assertEqual(column.to_query_string(), "[temperature;MAX;peak]")

    def test_nested_typed_filter(self):
        expression = FilterGroup.all_of(
            FilterCondition(field="temperature", operator=">", value=45),
            FilterGroup.any_of(
                FilterCondition(field="mass_flow", operator="<", value=2.5),
                FilterCondition(field="sensorId", operator="=", value="A&B + #ü%"),
            ),
        )
        config = DataLakeMeasureEndpoint._validate_query_params({"filter_expression": expression})
        payload = loads(parse_qs(config.build_query_string()[1:])["filterExpression"][0])
        self.assertEqual(
            payload,
            {
                "type": "group",
                "operator": "AND",
                "children": [
                    {"type": "condition", "field": "temperature", "operator": ">", "condition": 45},
                    {
                        "type": "group",
                        "operator": "OR",
                        "children": [
                            {"type": "condition", "field": "mass_flow", "operator": "<", "condition": 2.5},
                            {"type": "condition", "field": "sensorId", "operator": "=", "condition": "A&B + #ü%"},
                        ],
                    },
                ],
            },
        )

    def test_single_typed_filter_preserves_value_types(self):
        for value in [True, False, 123, 1.5, "123", "true", "'123'"]:
            with self.subTest(value=value):
                condition = FilterCondition(field="value", operator="=", value=value)
                config = DataLakeMeasureEndpoint._validate_query_params({"filterExpression": condition})
                payload = loads(parse_qs(config.build_query_string()[1:])["filterExpression"][0])
                self.assertEqual(payload["operator"], "AND")
                actual = payload["children"][0]["condition"]
                self.assertEqual(actual, value)
                self.assertIs(type(actual), type(value))

    def test_invalid_typed_filters(self):
        for options in [
            {"field": "", "operator": "=", "value": 1},
            {"field": "x", "operator": "INVALID", "value": 1},
            {"field": "x", "operator": "=", "value": None},
            {"field": "x", "operator": "=", "value": []},
            {"field": "x", "operator": "=", "value": float("nan")},
            {"field": "x", "operator": "=", "value": float("inf")},
        ]:
            with self.subTest(options=options), self.assertRaises(ValidationError):
                FilterCondition.model_validate(options)
        for options in [
            {"operator": "AND", "children": []},
            {"operator": "XOR", "children": [{"field": "x", "operator": "=", "value": 1}]},
            {"operator": "OR", "children": ["invalid"]},
        ]:
            with self.subTest(options=options), self.assertRaises(ValidationError):
                FilterGroup.model_validate(options)

    def test_typed_legacy_filter(self):
        for value, expected in [
            (45, "45"),
            (2.5, "2.5"),
            (True, "true"),
            (False, "false"),
            (1e-7, "0.0000001"),
            ("A&B + #ü%", "A&B + #ü%"),
            ("'123'", "'123'"),
            ("", "''"),
        ]:
            with self.subTest(value=value):
                config = DataLakeMeasureEndpoint._validate_query_params(
                    {
                        "filter": FilterCondition(field="value", operator="=", value=value),
                    }
                )
                self.assertEqual(parse_qs(config.build_query_string()[1:])["filter"], [f"[value;=;{expected}]"])

    def test_typed_legacy_filter_rejects_delimiters(self):
        for delimiter in ",;[]":
            for field, value in [("sensor", f"a{delimiter}b"), (f"a{delimiter}b", "sensor")]:
                condition = FilterCondition(field=field, operator="=", value=value)
                with self.subTest(field=field, value=value):
                    with self.assertRaisesRegex(StreamPipesQueryValidationError, "require filter_expression"):
                        DataLakeMeasureEndpoint._validate_query_params({"filter": condition})
                    config = DataLakeMeasureEndpoint._validate_query_params({"filter_expression": condition})
                    payload = loads(parse_qs(config.build_query_string()[1:])["filterExpression"][0])
                    self.assertEqual(payload["children"][0]["condition"], value)
