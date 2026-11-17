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

"""Discover, configure and run benchmarks locally or in Docker."""

import argparse
import json
import logging
import signal
import sys
from pathlib import Path
from urllib.parse import urlsplit

from pydantic import ValidationError

from streampipes_performance.auth import temporary_api_key
from streampipes_performance.catalog import configurations, config_path, describe_test, execution_plan, print_plan
from streampipes_performance.config import load
from streampipes_performance.logging import console_logging
from streampipes_performance.settings import ENVIRONMENT, SECRET_FIELDS, RuntimeSettings, resolve_settings


def connection_url(env_name="STREAMPIPES_URL"):
    """Compatibility helper for callers resolving a backend origin from an environment variable."""
    import os
    value = os.environ.get(env_name)
    if not value:
        raise ValueError(f"{env_name} must be set for run")
    try:
        return urlsplit(RuntimeSettings(url=value).url)
    except ValueError:
        raise ValueError(f"{env_name} must be an HTTP(S) origin without credentials or a path") from None


def settings_arguments(parser):
    parser.add_argument("--settings", type=Path, help="JSON deployment settings; also PERFORMANCE_SETTINGS")
    for field, env in ENVIRONMENT.items():
        if field in ("wait_ready", "email_to"):
            continue
        parser.add_argument("--" + field.replace("_", "-"), default=None,
                            help=f"Override {field}; environment: {env}")
    for name in ("password", "influx_token", "smtp_password"):
        parser.add_argument("--" + name.replace("_", "-") + "-file", help=f"Read {name} from a file")
    parser.add_argument("--wait-ready", action=argparse.BooleanOptionalAction, default=None,
                        help="Wait for required services before run (default: enabled)")
    email = parser.add_mutually_exclusive_group()
    email.add_argument("--email-to", "--to", action="append", help="Report recipient; repeat for multiple recipients")
    email.add_argument("--no-email", action="store_true", help="Disable delivery configured by settings or environment")


def parser_for_cli():
    parser = argparse.ArgumentParser(prog="sp-performance", description=__doc__)
    commands = parser.add_subparsers(dest="command", required=True)
    tests = commands.add_parser("tests", help="Discover test types and their parameters")
    tests.add_argument("action", choices=("list", "show"))
    tests.add_argument("test", nargs="?")
    configs = commands.add_parser("configs", help="List supplied configurations")
    configs.add_argument("action", choices=("list",))
    for command in ("validate", "preview", "run"):
        sub = commands.add_parser(command, add_help=False, help=f"{command.capitalize()} a configuration")
        sub.add_argument("config", nargs="?", help="Supplied name (e.g. medium) or JSON path")
        sub.add_argument("-h", "--help", action="store_true", help="Show syntax and the selected configuration's plan")
        sub.add_argument("--case", dest="case_ids", action="append", help="Select a case ID; repeat to select more")
        sub.add_argument("--set", dest="overrides", action="append", metavar="CASE.PARAMETER=JSON",
                         help="Replace a workload parameter before expanding alternatives")
        if command != "run":
            sub.add_argument("--json", action="store_true", help="Print the resolved plan as JSON")
        else:
            sub.set_defaults(json=False)
        settings_arguments(sub)
    report = commands.add_parser("report", help="Build or email an existing HTML report")
    report.add_argument("directory_or_send", help="Result directory, or 'send'")
    report.add_argument("directory", nargs="?", help="Result directory for report send")
    settings_arguments(report)
    return parser, commands.choices


def resolved_settings(args, config=None, path=None):
    values = {key: getattr(args, key, None) for key in RuntimeSettings.model_fields}
    for key in ("password_file", "smtp_password_file", "influx_token_file"):
        values[key] = getattr(args, key, None)
    if getattr(args, "no_email", False):
        values["email_to"] = []
    secrets = SECRET_FIELDS if args.command == "run" and not args.help else ()
    if args.command == "report" and args.directory_or_send == "send":
        secrets = ("smtp_password",)
    return resolve_settings(connection=config.connection if config else None, config_path=path,
                            output_directory=config.output_directory if config else None,
                            settings_path=getattr(args, "settings", None), overrides=values, secret_fields=secrets)


def error_text(error):
    if isinstance(error, ValidationError):
        return "ValidationError: " + "; ".join(
            f"{'.'.join(map(str, item['loc']))}: {item['msg']}" for item in error.errors(include_input=False)
        )
    if isinstance(error, (ValueError, FileNotFoundError, TimeoutError)):
        return f"{type(error).__name__}: {error}"
    return type(error).__name__


def execute(config, scenarios, settings):
    from streampipes.client.credential_provider import StreamPipesApiKeyCredentials
    from streampipes_performance.clients.streampipes import StreamPipesClient, StreamPipesClientConfig
    from streampipes_performance.notification import send_report
    from streampipes_performance.readiness import wait_ready
    from streampipes_performance.runner import execute_run_config

    settings.require_run_credentials()
    if settings.email_to:
        settings.require_email()
    previous_handler = signal.getsignal(signal.SIGTERM)
    def interrupt(signum, frame):
        raise KeyboardInterrupt
    signal.signal(signal.SIGTERM, interrupt)
    directory = None
    exit_code = 2
    try:
        try:
            if settings.wait_ready:
                wait_ready(settings, scenarios)
            url = urlsplit(settings.url)
            with temporary_api_key(settings.url, settings.username, settings.password.get_secret_value(),
                                   config.execution.timing.request_timeout_seconds) as api_key:
                credentials = StreamPipesApiKeyCredentials(username=settings.username, api_key=api_key)
                client = StreamPipesClient(StreamPipesClientConfig(
                    credential_provider=credentials, host_address=url.hostname,
                    https_disabled=url.scheme == "http", port=url.port or (443 if url.scheme == "https" else 80),
                    request_timeout=config.execution.timing.request_timeout_seconds,
                ), logging_level=logging.WARNING)
                try:
                    directory, passed = execute_run_config(
                        config, scenarios, client, output_directory=settings.output_directory,
                        runtime_settings=settings,
                    )
                    exit_code = 0 if passed else 1
                finally:
                    client.request_session.close()
        except KeyboardInterrupt:
            exit_code = 130
        except Exception as error:
            logging.getLogger(__name__).error("Run failed: %s", error_text(error))
            exit_code = 2
        if directory is not None:
            directory = Path(directory)
            manifest_path = directory / "manifest.json"
            if manifest_path.is_file():
                manifest = json.loads(manifest_path.read_text())
                if manifest.get("status") == "interrupted" and exit_code != 2:
                    exit_code = 130
                if exit_code == 2:
                    # Include finalization failures (e.g. key revocation) in the emailed report.
                    from streampipes_performance.reporting import atomic_json
                    from streampipes_performance.report.run_report import write_report
                    manifest["status"] = "failed"
                    manifest["finalization_failed"] = True
                    atomic_json(manifest_path, manifest)
                    write_report(directory)
            print(f"{'Passed' if exit_code == 0 else 'Interrupted' if exit_code == 130 else 'Failed'}: {directory}")
            if settings.email_to:
                try:
                    delivered = send_report(directory, settings)
                except Exception as error:
                    logging.getLogger(__name__).error("Email delivery failed: %s", type(error).__name__)
                    delivered = False
                print("Email sent" if delivered else "Email delivery failed; local report retained")
                if not delivered and exit_code == 0:
                    exit_code = 3
        return exit_code
    finally:
        signal.signal(signal.SIGTERM, previous_handler)


def main(argv=None):
    parser, commands = parser_for_cli()
    args = parser.parse_args(argv)
    try:
        if args.command == "tests":
            from streampipes_performance.cases import REGISTRY
            if args.action == "show":
                if not args.test:
                    raise ValueError("tests show requires a test name")
                describe_test(args.test)
            else:
                for kind, case in REGISTRY.items():
                    print(f"{kind}: {case.documentation.description}")
            return 0
        if args.command == "configs":
            for name, path in configurations().items():
                print(f"{name}: {path}")
            return 0
        if args.command == "report":
            from streampipes_performance.report.run_report import write_report
            if args.directory_or_send == "send":
                from streampipes_performance.notification import send_report
                if not args.directory:
                    raise ValueError("report send requires a result directory")
                settings = resolved_settings(args)
                sent = send_report(args.directory, settings)
                print("Email sent" if sent else "Email delivery failed; local report retained")
                return 0 if sent else 3
            if args.directory:
                raise ValueError("Use report DIRECTORY or report send DIRECTORY")
            print(f"Report: {write_report(args.directory_or_send)}")
            return 0
        if args.help:
            commands[args.command].print_help()
            if not args.config:
                return 0
        if not args.config:
            raise ValueError(f"{args.command} requires a configuration name or JSON path")
        path = config_path(args.config)
        config, scenarios = load(path, case_ids=args.case_ids, overrides=args.overrides)
        settings = resolved_settings(args, config, path)
        plan = execution_plan(config, scenarios, settings, path)
        if args.command == "preview" or args.help or args.json:
            if args.json and not args.help:
                print(json.dumps(plan, indent=2))
            else:
                print_plan(plan)
            return 0
        if args.command == "validate":
            print(f"Valid: {len(scenarios)} scenarios")
            return 0
        print_plan(plan)
        with console_logging(settings.log_level):
            return execute(config, scenarios, settings)
    except KeyboardInterrupt:
        return 130
    except Exception as error:
        print(f"Could not {args.command}: {error_text(error)}", file=sys.stderr)
        return 2


if __name__ == "__main__":
    raise SystemExit(main())
