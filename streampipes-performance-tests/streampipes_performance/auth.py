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

"""Create and revoke an API key scoped to a single CLI run."""

import logging
from contextlib import contextmanager
from urllib.parse import quote
from uuid import uuid4

import requests


def _request(session, method, url, timeout, operation, **kwargs):
    # Never expose response bodies or request exceptions, which can contain credentials.
    try:
        response = session.request(method, url, timeout=timeout, **kwargs)
        response.raise_for_status()
        data = response.json()
    except (requests.RequestException, ValueError):
        raise RuntimeError(f"StreamPipes {operation} failed") from None
    if not isinstance(data, dict) or data.get("success") is False:
        raise RuntimeError(f"StreamPipes {operation} failed")
    return data


def _login(session, base_url, username, password, timeout, operation="login"):
    data = _request(
        session, "POST", f"{base_url}/auth/login", timeout, operation,
        json={"username": username, "password": password},
    )
    if not isinstance(data.get("accessToken"), str) or not data["accessToken"]:
        raise RuntimeError(f"StreamPipes {operation} did not return an access token")
    return {"Authorization": f"Bearer {data['accessToken']}"}


@contextmanager
def temporary_api_key(origin, username, password, timeout):
    """Keep the key in memory and revoke only this run's token, even on errors."""
    base_url = f"{origin.rstrip('/')}/streampipes-backend/api/v2"
    user_path = quote(username, safe="")
    logger = logging.getLogger(__name__)
    with requests.Session() as session:
        headers = _login(session, base_url, username, password, timeout)
        token = _request(
            session, "POST", f"{base_url}/users/{user_path}/tokens", timeout, "API key creation",
            headers=headers, json={"tokenName": f"performance-tests-{uuid4()}"},
        )
        token_id = token.get("tokenId")
        if not isinstance(token_id, str) or not token_id:
            raise RuntimeError("StreamPipes API key creation did not return a token ID")
        try:
            if not isinstance(token.get("rawToken"), str) or not token["rawToken"]:
                raise RuntimeError("StreamPipes API key creation did not return an API key")
            logger.info("Created temporary StreamPipes API key")
            yield token["rawToken"]
        finally:
            # Login again because the original JWT may expire during a long benchmark.
            headers = _login(session, base_url, username, password, timeout, "API key revocation login")
            profile = _request(
                session, "GET", f"{base_url}/users/username/{user_path}", timeout,
                "API key revocation profile lookup", headers=headers,
            )
            principal_id = profile.get("principalId")
            tokens = profile.get("userApiTokens")
            if not isinstance(principal_id, str) or not isinstance(tokens, list):
                raise RuntimeError("StreamPipes API key revocation received an invalid profile")
            profile["userApiTokens"] = [item for item in tokens if item["tokenId"] != token_id]
            _request(
                session, "PUT", f"{base_url}/users/user/{quote(principal_id, safe='')}", timeout,
                "API key revocation", headers=headers, json=profile,
            )
            logger.info("Revoked temporary StreamPipes API key")
