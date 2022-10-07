// tslint:disable-next-line:no-implicit-dependencies
import { defineConfig } from 'cypress';

export default defineConfig({
  projectId: 'q1jdu2',
  downloadsFolder: 'cypress/downloads',
  env: {
    TAKE_SCREENSHOT: 'false',
  },
  retries: {
    runMode: 1,
    openMode: 0,
  },
  trashAssetsBeforeRuns: true,
  videoCompression: false,
  viewportWidth: 1920,
  viewportHeight: 1080,
  e2e: {
    // We've imported your old cypress plugins here.
    // You may want to clean this up later by importing these.
    setupNodeEvents(on, config) {
      return require('./cypress/plugins/index.ts')(on, config);
    },
    specPattern: 'cypress/tests/**/*.{js,jsx,ts,tsx}',
    baseUrl: 'http://localhost:8082',
  },
});
