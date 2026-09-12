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

"""Regression checks for fresh installs and persistent upgrade credentials."""

import os
from pathlib import Path
import subprocess
import sys
import tempfile
import unittest


CONFIGURE = Path(__file__).resolve().with_name("configure")
SETTINGS = (
    "SP_SERVICE_SECRET", "SP_COUCHDB_PASSWORD", "SP_TS_STORAGE_TOKEN",
    "SP_INFLUXDB_ADMIN_PASSWORD", "SP_ENCRYPTION_PASSCODE", "SP_INITIAL_ADMIN_PASSWORD",
    "SP_NATS_TOKEN", "SP_JWT_SECRET",
)


class ConfigureTest(unittest.TestCase):
    def setUp(self):
        self.directory = tempfile.TemporaryDirectory()
        self.addCleanup(self.directory.cleanup)
        self.path = Path(self.directory.name) / ".env"

    def run_configure(self, success=True):
        result = subprocess.run([sys.executable, str(CONFIGURE)], cwd=self.directory.name,
                                capture_output=True, text=True)
        self.assertEqual(result.returncode, 0 if success else 1, result.stderr)
        return result

    def test_first_install_and_restart_preserve_secret(self):
        first = self.run_configure()
        contents = self.path.read_text()
        values = dict(line.split("=", 1) for line in contents.splitlines() if "=" in line and not line.startswith("#"))
        self.assertEqual(len({values[name] for name in SETTINGS}), len(SETTINGS))
        for name in SETTINGS:
            secret = values[name]
            self.assertEqual(len(secret), 64)
            self.assertEqual(len(bytes.fromhex(secret)), 32)
            self.assertNotIn(secret, first.stdout + first.stderr)
        self.run_configure()
        self.assertEqual(contents, self.path.read_text())
        if os.name != "nt":
            self.assertEqual(self.path.stat().st_mode & 0o777, 0o600)

    def test_custom_secret_and_unrelated_settings_are_preserved(self):
        content = "SP_VERSION=custom\nSP_SERVICE_SECRET='" + "a" * 48 + "'\nSP_NATS_TOKEN=keep\n"
        self.path.write_text(content)
        self.run_configure()
        self.assertTrue(self.path.read_text().startswith(content))
        completed = self.path.read_text()
        self.run_configure()
        self.assertEqual(completed, self.path.read_text())

    def test_append_to_existing_installation(self):
        self.path.write_text("SP_VERSION=custom")
        self.run_configure()
        self.assertTrue(self.path.read_text().startswith("SP_VERSION=custom\n"))

    def test_blank_and_legacy_secrets_are_replaced(self):
        for value in ("", "my-apache-streampipes-secret-key-change-me",
                      "my-apache-streampipes-secret-key-change-me # old value",
                      "'my-apache-streampipes-secret-key-change-me' # old value"):
            self.path.write_text("SP_SERVICE_SECRET=" + value + "\nKEEP=yes\n")
            self.run_configure()
            self.assertIn("KEEP=yes\n", self.path.read_text())
            self.assertEqual(len(self.path.read_text().splitlines()[0].split("=")[1]), 64)

    def test_invalid_configuration_is_not_overwritten(self):
        for content in ("SP_SERVICE_SECRET=short\n", "SP_JWT_SECRET=short\n",
                        "SP_SERVICE_SECRET=\nSP_SERVICE_SECRET=\n"):
            self.path.write_text(content)
            self.run_configure(success=False)
            self.assertEqual(content, self.path.read_text())

    def test_preserves_all_custom_credentials_verbatim(self):
        content = "".join(name + "='custom-" + "a" * 40 + "' # keep\n" for name in SETTINGS)
        self.path.write_text(content)
        self.run_configure()
        self.assertEqual(content, self.path.read_text())

    def test_fills_only_missing_credentials(self):
        for missing in SETTINGS:
            content = "".join(name + "=" + "a" * 40 + "\n" for name in SETTINGS if name != missing)
            self.path.write_text(content + missing + "=\n")
            self.run_configure()
            self.assertTrue(self.path.read_text().startswith(content))
            generated = self.path.read_text().splitlines()[-1].split("=", 1)[1]
            self.assertEqual(len(bytes.fromhex(generated)), 32)

    def test_duplicate_infrastructure_setting_does_not_modify_file(self):
        for name in SETTINGS:
            content = name + "=\n" + name + "=\n"
            self.path.write_text(content)
            self.run_configure(success=False)
            self.assertEqual(content, self.path.read_text())

    def test_installations_have_independent_credentials(self):
        self.run_configure()
        first = self.path.read_text()
        self.path.unlink()
        self.run_configure()
        for name in SETTINGS:
            value = next(line for line in first.splitlines() if line.startswith(name + "="))
            self.assertNotIn(value, self.path.read_text())

    def test_dotenv_contents_are_not_executed(self):
        self.path.write_text("OTHER=$(touch executed)\n")
        self.run_configure()
        self.assertFalse((self.path.parent / "executed").exists())

    @unittest.skipIf(os.name == "nt", "Symbolic-link permissions differ on Windows")
    def test_symlinks_are_rejected(self):
        target = self.path.parent / "target"
        target.write_text("untouched")
        self.path.symlink_to(target)
        self.run_configure(success=False)
        self.assertEqual(target.read_text(), "untouched")


if __name__ == "__main__":
    unittest.main()
