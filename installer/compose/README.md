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
# StreamPipes Compose - The User's Choice
StreamPipes Compose is a simple collection of user-friendly `docker-compose` files that easily lets gain first-hand experience with Apache StreamPipes.

<!-- BEGIN do not edit: set via ../upgrade_versions.sh -->
**Current version:** 0.99.0-SNAPSHOT
<!-- END do not edit -->

> **NOTE**: We recommend StreamPipes Compose to only use for initial try-out and testing. If you are a developer and want to develop new pipeline elements or core feature, use the [StreamPipes CLI](../cli).

#### TL;DR: A one-liner to rule them all :tada: :tada: :tada:

```bash
./configure
docker compose up -d
```
Go to http://localhost and sign in with `admin@streampipes.apache.org` and the
`SP_INITIAL_ADMIN_PASSWORD` from `.env`. Once finished, switch to the pipeline editor and start the interactive tour or check the [online tour](https://streampipes.apache.org/docs/user-guide-introduction/) to learn how to create your first pipeline!

The configure helper requires Python 3 and prepares a **new deployment**. Run it from
this folder before starting any Compose variant. It generates independent random values
for these settings and writes them to `.env` with restricted file permissions:

| Setting | Used by |
| --- | --- |
| `SP_SERVICE_SECRET` | Backend and extensions for service-account authentication |
| `SP_COUCHDB_PASSWORD` | CouchDB, backend, and extensions |
| `SP_TS_STORAGE_TOKEN` | InfluxDB initialization, backend, and extensions |
| `SP_INFLUXDB_ADMIN_PASSWORD` | InfluxDB's initial administrator account |
| `SP_ENCRYPTION_PASSCODE` | Backend and extensions for stored encrypted secrets |
| `SP_INITIAL_ADMIN_PASSWORD` | Initial StreamPipes administrator account |
| `SP_NATS_TOKEN` | NATS, backend, and extensions when a NATS-auth override is enabled |
| `SP_JWT_SECRET` | Backend HMAC signing of user JWTs |

The backend already generates and persists a JWT signing secret when none is configured.
The installer supplies an explicit value so it is backed up with the other credentials.
Keep it distinct from the service-account secret and do not pass it to extensions.
Changing it invalidates existing HMAC user JWTs; it is not used for RSA signing.

The helper fills missing or empty values and preserves existing custom values on reruns.
The rejected historical service-secret default is replaced if present. To supply your
own credentials, copy `.env.example` to `.env` and set the desired values before running
`./configure`. The helper never prints passwords or tokens.

Keep `.env` with your deployment backups and do not commit or share it. Reuse it when
restarting or upgrading this deployment. In particular, generating a different encryption
passcode would make existing encrypted secrets unreadable; this helper does not rotate
credentials for databases that already contain data.

On Windows, run `python configure` instead of `./configure`.
Without Python, copy `.env.example` to `.env`, use `openssl rand -hex 32` separately for
each setting in the table, and restrict access to `.env`.

For deployments using the repository-root Compose file, run
`./installer/compose/configure` from the repository root. That development Compose file
currently consumes only the generated service secret; the complete credential wiring
described above applies to the Compose variants in this installer directory.

## Prerequisite
* Docker >= 17.06.0
* Docker-Compose >= 1.17.0 (Compose file format: 3.4)
* Google Chrome (recommended), Mozilla Firefox, Microsoft Edge

Tested on: **macOS, Linux, Windows** (CMD, PowerShell, GitBash)

**macOS** and **Windows** users can easily get Docker and Docker-Compose on their systems by installing **Docker for Mac/Windows** (recommended).

> **NOTE**: On purpose, we disabled all port mappings except of http port **80** to access the StreamPipes UI to provide minimal surface for conflicting ports.

## Usage
We provide several options to get you going:

- **default**: the standard installation, uses NATS as internal message broker (recommended for new installations)
- **kafka**: starts the Kafka-based setup via `docker-compose.kafka.yml`
- **minimal**: contains only a minimal set of adapters, processors and sinks for iiot use cases

The NATS-based setup is the recommended default. If you previously relied on Kafka, the Kafka compose file is still available as `docker-compose.kafka.yml`.

**Starting** the **default (NATS)** option is as easy as simply running:
> **NOTE**: Starting might take a while since `docker-compose up` also initially pulls all Docker images from Dockerhub.

```bash
docker-compose up -d
# go to `http://localhost` after all services are started
```

Optional: enable token-based NATS auth using the generated `SP_NATS_TOKEN` and the auth override
(the token alone does not enable authentication):

```bash
docker-compose -f docker-compose.yml -f docker-compose.nats-auth.yml up -d
```
After all containers are successfully started just got to your browser and visit http://localhost to finish the installation. Once finished, switch to the pipeline editor and start the interactive tour or check the [documentation](https://streampipes.apache.org/docs/user-guide-introduction/) to learn more about StreamPipes!

**Stopping** the **default** option is similarly easy:
```bash
docker-compose down
# if you want to remove mapped data volumes, run:
# docker-compose down -v
```

If you need the Kafka-based setup, start it with the dedicated Kafka compose file:

```bash
docker-compose -f docker-compose.kafka.yml up -d
# go to `http://localhost` after all services are started
```

Stopping the **kafka** option:

```bash
docker-compose -f docker-compose.kafka.yml down
```

Starting the **minimal** option is almost the same, just specify the `docker-compose.minimal.yml` file:
```bash
docker-compose -f docker-compose.minimal.yml up -d
# go to `http://localhost` after all services are started
```

Optional: enable token-based NATS auth in minimal mode:

```bash
docker-compose -f docker-compose.minimal.yml -f docker-compose.minimal.nats-auth.yml up -d
```
Stopping the **minimal** option:
```bash
docker-compose -f docker-compose.minimal.yml down
```

## Update services
To actively pull the latest available Docker images use:
```bash
docker-compose pull
# docker-compose -f docker-compose.full.yml pull
```

## Upgrade
To upgrade to another StreamPipes version, simply edit the `SP_VERSION` in the `.env` file.
```
SP_VERSION=<VERSION>
```

### Service credentials in existing deployments

Existing deployments should retain their deployment configuration and persisted
infrastructure credentials. The configure helper is not an infrastructure migration tool.

For the service-account security update, generate a random service secret and configure
the same value as `SP_INITIAL_SERVICE_USER_SECRET` on the backend and `SP_CLIENT_SECRET`
on the backend and extensions. Recreate those containers during a maintenance window so
they receive the changed environment variables.

If the configured bootstrap service account still has the old shipped default, the
backend migrates that stored secret. Accounts with custom secrets are left unchanged:
keep using their existing credentials. Changing an initial-installation variable does
not otherwise change stored credentials.

If no valid replacement is configured, authentication using the old default is rejected.
Human administrator login remains available. Set the backend and extension variables and
restart, or edit the service user's Client Secret in the security configuration and set
that same value on all its clients. Additional service accounts using the old default
must be updated through the security configuration; the migration only changes the
configured bootstrap account. Service users retain their existing roles and permissions.

JWT verification also rejects disabled, locked, or expired accounts and tokens without
an expiration. Existing clients using the StreamPipes JWT generator remain supported.
Custom JWT clients must use the HMAC algorithm selected for their UTF-8 secret length:
HS256 for 32–47 bytes, HS384 for 48–63 bytes, and HS512 for 64 bytes or more. RSA deployments
must have working signing keys; the backend no longer falls back to HMAC on a key error.

## Bugs and Feature Requests

If you've found a bug or have a feature that you'd love to see in StreamPipes, feel free to create an issue i on [GitHub](https://github.com/apache/streampipes/issues).

## Get help
Since we purely leverage Docker Compose, please see their [documentation](https://docs.docker.com/compose/) in case you want to find out more about their available [commands](https://docs.docker.com/compose/reference/overview/).

If you have any problems during the installation or questions around StreamPipes, you'll get help through one of our community channels:

- [Slack](https://slack.streampipes.org)
- [Mailing Lists](https://streampipes.apache.org/community/mailing-lists/)

And don't forget to follow us on [Twitter](https://twitter.com/streampipes)!

## Contribute
We welcome contributions to StreamPipes. If you are interested in contributing to StreamPipes, let us know! You'll
 get to know an open-minded and motivated team working together to build the next IIoT analytics toolbox.

Here are some first steps in case you want to contribute:
* Subscribe to our dev mailing list [dev-subscribe@streampipes.apache.org](dev-subscribe@streampipes.apache.org)
* Send an email, tell us about your interests and which parts of StreamPipes you'd like to contribute (e.g., core or UI)!
* Ask for a mentor who helps you to understand the code base and guides you through the first setup steps
* Find an issue on [GitHub](https://github.com/apache/streampipes/issues). which is tagged with a _good first issue_ tag
* Have a look at our developer wiki at [https://cwiki.apache.org/confluence/display/STREAMPIPES](https://cwiki.apache.org/confluence/display/STREAMPIPES) to learn more about StreamPipes development.

Have fun!

## Feedback
We'd love to hear your feedback! Subscribe to [users@streampipes.apache.org](mailto:users@streampipes.apache.org)

## License
[Apache License 2.0](../LICENSE)
