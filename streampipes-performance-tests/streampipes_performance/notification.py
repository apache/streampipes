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

"""Opt-in SMTP delivery of the standalone HTML report, with a separate delivery record."""

import json
import smtplib
import ssl
from datetime import datetime, timezone
from email.message import EmailMessage
from pathlib import Path

from streampipes_performance.reporting import atomic_json


def send_report(directory, settings):
    settings.require_email()
    directory = Path(directory)
    report = directory / "report.html"
    if not report.is_file():
        raise ValueError("report.html is missing; build the report before sending it")
    manifest_path = directory / "manifest.json"
    manifest = json.loads(manifest_path.read_text()) if manifest_path.is_file() else {}
    run_id = manifest.get("run_id", directory.name)
    status = manifest.get("status", "unknown")
    message = EmailMessage()
    message["From"] = settings.email_from
    message["To"] = ", ".join(settings.email_to)
    message["Subject"] = f"Performance report: {status} — {run_id}"
    message.set_content(f"Run: {run_id}\nStatus: {status}\nThe HTML report is attached.\n")
    message.add_attachment(report.read_bytes(), maintype="text", subtype="html", filename="report.html")
    delivery = {"attempted_at": datetime.now(timezone.utc).isoformat(), "recipients": settings.email_to,
                "status": "failed"}
    try:
        context = ssl.create_default_context()
        factory = smtplib.SMTP_SSL if settings.smtp_security == "ssl" else smtplib.SMTP
        kwargs = {"timeout": settings.smtp_timeout}
        if settings.smtp_security == "ssl":
            kwargs["context"] = context
        with factory(settings.smtp_host, settings.smtp_port, **kwargs) as smtp:
            if settings.smtp_security == "starttls":
                smtp.starttls(context=context)
            if settings.smtp_username:
                smtp.login(settings.smtp_username, settings.smtp_password.get_secret_value())
            refused = smtp.send_message(message)
            if refused:
                raise smtplib.SMTPRecipientsRefused(refused)
        delivery["status"] = "sent"
        return True
    except (OSError, smtplib.SMTPException) as error:
        # SMTP responses can contain credentials or addresses; keep only the type.
        delivery["error_type"] = type(error).__name__
        return False
    finally:
        atomic_json(directory / "email-delivery.json", delivery)
