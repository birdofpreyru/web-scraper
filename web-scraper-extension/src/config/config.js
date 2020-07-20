/**
 * i18next config
 * follow ../locales/en.json write new translation file and import here
 * https://react.i18next.com/getting-started
 */

export const I18_CONFIG = {
  resources: {
    /* eslint-disable global-require */
    en: require('../locales/en.json'),
    //    cn: require('../locales/cn.json'),
    jp: require('../locales/jp.json'),
    /* eslint-enable global-require */
  },
  lng: 'en',
  fallbackLng: 'en',
  interpolation: {
    escapeValue: false,
  },
};

/**
 * default base api
 */
export const DEFAULT_API = 'https://scraper-stub-api.herokuapp.com/scrapers';
