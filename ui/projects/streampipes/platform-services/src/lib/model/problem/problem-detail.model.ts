/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

/**
 * The error contract of the StreamPipes REST API: every non-2xx response carries an RFC 9457
 * problem detail. `type` is a stable URN (`urn:streampipes:problem:<slug>`), `detail` the
 * sentence to show, `errors` the individual inputs that were rejected. Problems raised from an
 * extension service additionally carry the `SpLogMessage` members `level`, `cause` and
 * `fullStackTrace`.
 */
export interface ProblemDetail {
    type: string;
    title: string;
    status: number;
    detail?: string;
    instance?: string;
    errors?: ProblemError[];
    [extension: string]: unknown;
}

export interface ProblemError {
    detail: string;
    pointer?: string;
}

export const PROBLEM_TYPE_PREFIX = 'urn:streampipes:problem:';

export type ProblemTypeSlug =
    | 'bad-request'
    | 'validation-failed'
    | 'unauthorized'
    | 'forbidden'
    | 'not-found'
    | 'conflict'
    | 'operation-failed'
    | 'service-unavailable'
    | 'internal-error';

export function isProblemDetail(body: unknown): body is ProblemDetail {
    return (
        typeof body === 'object' &&
        body !== null &&
        typeof (body as ProblemDetail).status === 'number' &&
        typeof (body as ProblemDetail).title === 'string'
    );
}

/**
 * The problem detail carried by an `HttpErrorResponse` (its `error` member), or `undefined`
 * when the response body is not a problem detail.
 */
export function problemOf(error: unknown): ProblemDetail | undefined {
    if (isProblemDetail(error)) {
        return error;
    }
    const body = (error as { error?: unknown })?.error;
    return isProblemDetail(body) ? body : undefined;
}

export function problemTypeOf(error: unknown): ProblemTypeSlug | undefined {
    const type = problemOf(error)?.type;
    return type?.startsWith(PROBLEM_TYPE_PREFIX)
        ? (type.substring(PROBLEM_TYPE_PREFIX.length) as ProblemTypeSlug)
        : undefined;
}

/**
 * The sentence to show a user for a failed request: the problem's `detail`, else its `title`,
 * else the response's own message, else the fallback.
 */
export function problemDetailMessage(error: unknown, fallback = ''): string {
    const problem = problemOf(error);
    const message =
        problem?.detail ||
        problem?.title ||
        (typeof (error as { error?: unknown })?.error === 'string'
            ? ((error as { error: string }).error as string)
            : undefined) ||
        (error as { message?: string })?.message;
    return message || fallback;
}

/**
 * The `errors` of a problem detail as plain sentences.
 */
export function problemErrors(error: unknown): string[] {
    return (problemOf(error)?.errors ?? []).map(e => e.detail);
}
