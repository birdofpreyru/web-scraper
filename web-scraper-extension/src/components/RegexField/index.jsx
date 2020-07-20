/* global window */

import React from 'react';
import PT from 'prop-types';
import { v4 as uuid } from 'uuid';
import { useGlobalState } from '@dr.pogodin/react-global-state';

import InputField from '../InputField';
import { sendMessageToPage } from '../../services/utils';

import './style.scss';

export default function RegexField({
  attribute,
  disabled,
  inputClassName,
  onChange,
  regex,
  selector,
  tipClassName,
}) {
  const { current: heap } = React.useRef({});
  const [attrs, setAttrs] = React.useState();
  const [showTip, setShowTip] = React.useState(false);
  const [i18n] = useGlobalState('i18n');

  React.useEffect(() => {
    const onMessage = (message) => {
      switch (message.action) {
        case 'getAttributesResult': {
          if (message.opid === heap.opid) setAttrs(message.result);
          break;
        }
        default:
      }
    };
    window.messageListeners.push(onMessage);
    return () => {
      window.messageListeners = window.messageListeners.filter(
        (item) => item !== onMessage,
      );
    };
  }, []);

  const tip = React.useMemo(() => {
    let res;
    if (attrs && showTip && regex) {
      res = [];
      const rx = new RegExp(regex);
      for (let i = 0; i < attrs.length; ++i) {
        const keys = Object.keys(attrs[i]);
        for (let j = 0; j < keys.length; ++j) {
          const key = keys[j];
          if (!attribute || attribute === key) {
            const value = attrs[i][key];
            const m = value && value.match(rx);
            if (m) res.push(m[0]);
            if (attribute || res.length === 3) { i = attrs.length; break; }
          }
        }
      }
    }
    return res;
  }, [attrs, showTip, attribute, regex]);

  let tipClass = 'tip';
  if (tipClassName) tipClass += ` ${tipClassName}`;

  return (
    <div className="RegexField">
      <InputField
        className={inputClassName}
        disabled={disabled}
        onBlur={() => setShowTip(false)}
        onChange={onChange}
        onFocus={() => {
          if (selector) {
            heap.opid = uuid();
            setShowTip(true);
            sendMessageToPage({
              action: 'getAttributes',
              selector,
              opid: heap.opid,
            });
          }
        }}
        title={i18n('editor.regex')}
        value={regex}
      />
      {
        tip ? (
          <div className={tipClass}>
            {
              tip.length ? (
                tip.map((item) => (
                  <div
                    className="item"
                    key={item}
                  >
                    {`• ${item}`}
                  </div>
                ))
              ) : (
                'No Match'
              )
            }
          </div>
        ) : null
      }
    </div>
  );
}

RegexField.propTypes = {
  attribute: PT.string,
  disabled: PT.bool,
  inputClassName: PT.string,
  onChange: PT.func.isRequired,
  regex: PT.string,
  selector: PT.string,
  tipClassName: PT.string,
};

RegexField.defaultProps = {
  attribute: '',
  disabled: false,
  inputClassName: '',
  regex: '',
  selector: '',
  tipClassName: '',
};
