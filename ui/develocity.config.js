/*

    Licensed to the Apache Software Foundation (ASF) under one
    or more contributor license agreements.  See the NOTICE file
    distributed with this work for additional information
    regarding copyright ownership.  The ASF licenses this file
    to you under the Apache License, Version 2.0 (the
    "License"); you may not use this file except in compliance
    with the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on an
    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied.  See the License for the
    specific language governing permissions and limitations
    under the License.

*/
const {
    fromPropertiesFile,
    inGradleUserHome,
} = require('@gradle-tech/develocity-agent/api/config');
const { execSync } = require('node:child_process');

const develocityUrl =
    process.env.DEVELOCITY_URL !== undefined
        ? process.env.DEVELOCITY_URL
        : 'https://develocity.apache.org';

module.exports = {
    server: {
        url: develocityUrl,
    },
    buildScan: buildScanUserData(),
};

function buildScanUserData() {
    const links = {};
    const tags = [];
    const values = {};

    // Add environment information
    const isCI = process.env.CI !== undefined;
    tags.push(isCI ? 'CI' : 'Local');

    // Add Git information if available
    const gitRepo = outputOf('git config --get remote.origin.url');
    const gitCommitId = outputOf('git rev-parse --verify HEAD');
    const gitCommitShortId = outputOf('git rev-parse --short=8 --verify HEAD');
    const gitBranch = outputOf('git branch --show-current');
    const gitStatus = outputOf('git status --porcelain');
    if (gitRepo !== undefined) {
        values['Git repository'] = gitRepo;
    }
    if (gitCommitId !== undefined) {
        values['Git commit id'] = gitCommitId;
        // use your Develocity URL to add proper links
        links['Git Commit Build Scans'] =
            `${develocityUrl}/scans?search.names=Git+commit+id&search.values=${gitCommitId}`;
    }
    if (gitCommitShortId !== undefined) {
        values['Git commit id short'] = gitCommitShortId;
    }
    if (gitBranch !== undefined) {
        tags.push(gitBranch);
        values['Git branch'] = gitBranch;
    }
    if (gitStatus) {
        tags.push('Dirty');
        values['Git status'] = gitStatus;
    }

    // Add CI information if available
    if (process.env.GITHUB_ACTIONS) {
        values['CI provider'] = 'GitHub Actions';
        if (process.env.GITHUB_WORKFLOW !== undefined) {
            values['CI workflow'] = process.env.GITHUB_WORKFLOW;
        }
        if (process.env.GITHUB_JOB !== undefined) {
            values['CI job'] = process.env.GITHUB_JOB;
        }
        if (process.env.GITHUB_ACTION !== undefined) {
            values['CI step'] = process.env.GITHUB_ACTION;
        }
        if (process.env.GITHUB_RUN_ID !== undefined) {
            values['CI run'] = process.env.GITHUB_RUN_ID;
        }
        if (process.env.GITHUB_RUN_ATTEMPT !== undefined) {
            values['CI run attempt'] = process.env.GITHUB_RUN_ATTEMPT;
        }
        if (process.env.GITHUB_RUN_NUMBER !== undefined) {
            values['CI run number'] = process.env.GITHUB_RUN_NUMBER;
        }
        if (
            process.env.GITHUB_HEAD_REF !== undefined &&
            process.env.GITHUB_HEAD_REF !== ''
        ) {
            values['PR branch'] = process.env.GITHUB_HEAD_REF;
        }
    }

    return {
        links,
        tags,
        values,
    };
}

function outputOf(command) {
    try {
        return execSync(command, { encoding: 'utf-8' }).toString().trim();
    } catch {
        // ignore errors, most likely the command is not available
        return undefined;
    }
}
