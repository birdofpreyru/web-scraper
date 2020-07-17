/* global window */

import _ from 'lodash';
import { VALID_SCRAPING_TYPES } from '../config/dropdown-list';
import getI18T from '../i18nSetup';

const selector = require('./selector-helper');

const { browser, chrome } = window;

/**
 * log info
 * @param msg
 */
export function logInfo(msg) {
  if (window.log) {
    window.log(JSON.stringify(msg));
  }
  console.log(msg);
}

/**
 * Convert purchase history JSON to UI data structure.
 * @param site the site
 * @return {{meta}|*}
 */
function convertPurchaseHistoryToFrontend(site) {
  if (site.meta) {
    return site;
  }
  const processRows = (obj) => {
    /* eslint-disable no-param-reassign */
    const keys = _.keys(_.omit(obj, ['url_element', 'parent', 'purchase_product']));
    obj.rows = [];
    _.each(keys, (k) => {
      const v = obj[k];
      v.type = k;
      delete obj[k];
      obj.rows.push(v);
    });
    /* eslint-enable no-param-reassign */
  };

  processRows(site.purchase_order);
  processRows(site.purchase_order.purchase_product);
  return {
    ...site,
    meta: {
      expanded: {
        history: true,
        order: true,
        product: true,
        next: true,
      },
      advancedExpanded: {},
      highlight: '',
    },
  };
}

/**
 * Converts JSON object from API to UI format.
 * @param {object} data
 * @param {string} type Data type (see VALID_SCRAPING_TYPES).
 * @return {object}
 */
export function convertToFrontend(data, type) {
  switch (type) {
    case VALID_SCRAPING_TYPES.PURCHASE_HISTORY:
      return convertPurchaseHistoryToFrontend(data);
    default: return undefined;
  }
}

/**
 * convert to backend struct
 * @param site the site
 * @return {*}
 */
export const convertToBackend = (site) => {
  const request = _.cloneDeep(site);
  delete request.meta;

  const processRows = (arr, path) => {
    _.each(arr, (row) => {
      /* eslint-disable no-param-reassign */
      if (_.isNil(row.type) || _.isEmpty(row.type)) {
        return;
      }
      const { type } = row;
      delete row.type;
      _.set(request, `${path}.${type}`, row);
      /* eslint-enable no-param-reassign */
    });
  };

  processRows(request.purchase_order.rows, 'purchase_order');
  processRows(request.purchase_order.purchase_product.rows, 'purchase_order.purchase_product');

  delete request.purchase_order.rows;
  delete request.purchase_order.purchase_product.rows;
  return request;
};

function getNative() {
  if (!chrome && !browser) {
    return {
      runtime: { lastError: null, onMessage: { addListener: () => null } },
      tabs: { query: () => null },
      storage: {
        local: {
          get: (key, cb) => cb({ [key]: window.localStorage.getItem(key) }),
          set: (obj, cb) => {
            const k = _.keys(obj)[0];
            window.localStorage.setItem(k, obj[k]);
            cb();
          },
        },
      },
    };
  }
  return chrome || browser;
}

if (getNative().runtime.onMessage) {
  getNative().runtime.onMessage.addListener((request) => {
    if (window.onMessage) {
      window.onMessage(request);
    } else {
      logInfo('uncached message from page');
    }
  });
}

/**
 *
 * @param key
 */
export function storageGet(key) {
  return new Promise((resolve, reject) => {
    getNative().storage.local.get([key], (result) => {
      // runtime.lastError will be defined during an API method callback if there was an error
      const error = getNative().runtime.lastError;
      if (error) {
        reject(error);
      } else {
        resolve(result[key]);
      }
    });
  });
}

/**
 * set key
 * @param key
 * @param value
 */
export function storageSet(key, value) {
  return new Promise((resolve, reject) => {
    getNative().storage.local.set({ [key]: value }, () => {
      // runtime.lastError will be defined during an API method callback if there was an error
      const error = getNative().runtime.lastError;
      if (error) {
        reject(error);
      } else {
        resolve();
      }
    });
  });
}

/**
 * process error
 * @param e the error
 */
export function processError(e) {
  if (window.log) {
    window.log(JSON.stringify(e.message));
  }
  console.error(e.message);
}

/**
 * send message to page
 * @param args the args
 */
export function sendMessageToPage(args) {
  /* eslint-disable no-param-reassign */
  args.messageId = Date.now() + Math.random();
  getNative().tabs.query({ active: true, currentWindow: true }, (tabs) => {
    logInfo(`send message = ${JSON.stringify(args)}`);
    getNative().tabs.sendMessage(tabs[0].id, args);
  });
  /* eslint-enable no-param-reassign */
}

/**
 * selector methods
 */
export const getCommonParent = (p1, p2) => selector.getCommonParent(p1, p2, getI18T);
export const { getPathParent } = selector;
export const { removeParent } = selector;
export const { getCommonClass } = selector;
export const { removeDifferentAndAdditional } = selector;
