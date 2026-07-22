/** A music track as returned by the backend API. */
export interface Track {
  id: number;
  title: string;
  albumName: string;
  /** ISO date string, formatted as yyyy-MM-dd. */
  releaseDate: string;
  playCount: number;
}

/** Payload used when creating a new track (no server-generated id). */
export type TrackRequest = Omit<Track, 'id'>;
