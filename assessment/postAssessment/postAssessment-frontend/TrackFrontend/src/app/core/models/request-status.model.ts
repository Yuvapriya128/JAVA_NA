import { WritableSignal, signal } from '@angular/core';

/**
 * Represents the state of an asynchronous request.
 * - `loading`: true while the request is in flight (UI shows a loading image).
 * - `error`: a user-facing error message, or null when there is none.
 * - `success`: true once the request has completed successfully.
 */
export interface RequestStatus {
  loading: boolean;
  error: string | null;
  success: boolean;
}

/** The initial, idle status. */
export const IDLE_STATUS: RequestStatus = {
  loading: false,
  error: null,
  success: false,
};

/** Creates a writable status signal initialised to the idle state. */
export function createStatus(): WritableSignal<RequestStatus> {
  return signal<RequestStatus>({ ...IDLE_STATUS });
}
