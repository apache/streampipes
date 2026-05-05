# OPC UA Events

This adapter subscribes to OPC UA server events and emits read-only OPC UA event payloads.

## Current scope

- Reuses the same OPC UA connection, security, and authentication settings as the OPC UA value adapter
- Subscribes either server-wide or to a selected notifier node
- Uses a selected OPC UA event type as a server-side `OfType(...)` filter
- Lets users add optional event fields derived from the selected event type
- Reads events only; acknowledge, confirm, and shelving actions are not part of this adapter yet

## Preview behavior

The preview step waits for a live event. If the server does not emit an alarm or condition during the preview window, the connection can still be valid and the preview will time out.
