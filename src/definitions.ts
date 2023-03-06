export interface AudioTogglePlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
