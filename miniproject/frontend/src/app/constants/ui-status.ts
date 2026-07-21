export interface UiStatusState {
  loading: boolean;
  success: boolean;
  error: string;
}

export const defaultUiStatus = (): UiStatusState => ({
  loading: false,
  success: false,
  error: ''
});
