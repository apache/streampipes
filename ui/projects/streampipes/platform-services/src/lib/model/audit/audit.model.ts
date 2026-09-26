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

export interface AuditEntry {
    eventId: string;
    storedAt: string;
    eventType: string;
    outcome: string;
    actor: string;
    resourceType?: string | null;
    resourceId?: string | null;
    locator: string;
}

export interface AuditPage {
    items: AuditEntry[];
    nextCursor: string | null;
}

export interface AuditEntryDetails {
    event: AuditEntry;
    recordedAt: string;
    details: Record<string, unknown>;
}

export interface AuditQuery {
    from: string;
    to: string;
    eventType?: string;
    actor?: string;
    outcome?: string;
    limit: number;
    cursor?: string;
}

export interface AuditStatus {
    enabled: boolean;
    available: boolean;
    failureCount: number;
    lastSuccessfulWrite: string | null;
    queueDepth: number;
    writeFailureCount: number;
    droppedEventCount: number;
    degraded: boolean;
}
