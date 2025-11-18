import '@vaadin/polymer-legacy-adapter/style-modules.js';
import '@vaadin/vertical-layout/src/vaadin-vertical-layout.js';
import '@vaadin/common-frontend/ConnectionIndicator.js';
import '@vaadin/vaadin-lumo-styles/color-global.js';
import '@vaadin/vaadin-lumo-styles/typography-global.js';
import '@vaadin/vaadin-lumo-styles/sizing.js';
import '@vaadin/vaadin-lumo-styles/spacing.js';
import '@vaadin/vaadin-lumo-styles/style.js';
import '@vaadin/vaadin-lumo-styles/vaadin-iconset.js';
import 'Frontend/generated/jar-resources/ReactRouterOutletElement.tsx';

const loadOnDemand = (key) => {
  const pending = [];
  if (key === '25043b587aa49611815946c5ba0417cb7645edf2e4e04911532f060144dbf055') {
    pending.push(import('./chunks/chunk-87b5c20cc98f1a51faa93a47da4c491852726a544b8e3b0c6f0cb0346b581fc6.js'));
  }
  if (key === 'a32bb74ca20853547c4731c3ab85cc0a9dcb1526e03916fb056d514f53ebeb6a') {
    pending.push(import('./chunks/chunk-5e5da9d19e3e7719bc0a7e146a3afb42741f9c5ae1eca6c0ab50f75c95b496bb.js'));
  }
  if (key === 'a1964580f297e81a50ae5e9f74617ffda63b153457a4b082eb76fca88a01b376') {
    pending.push(import('./chunks/chunk-74dfbad985d6db5f7000023174f2fa47067a0a8d304338d27320e6877098cc28.js'));
  }
  if (key === 'e0ad99a5815943b954f783d556d3fdc0dcb12a35ca6b7f17cfa9e0ae56cfc894') {
    pending.push(import('./chunks/chunk-04fbf17b70a0a964b963e6c8bb55ee4a3418f7f2c10f5c4987a20ef6e690e7de.js'));
  }
  if (key === 'b0a21f4e381b64ee4b36e2e8642d75ba56be1c83d7a4812724aefd4eb916b78b') {
    pending.push(import('./chunks/chunk-2c4753881efe1933e92bbd9c213b70f0083200550f1a2aaf9d96e80fe98fc6ec.js'));
  }
  return Promise.all(pending);
}

window.Vaadin = window.Vaadin || {};
window.Vaadin.Flow = window.Vaadin.Flow || {};
window.Vaadin.Flow.loadOnDemand = loadOnDemand;
window.Vaadin.Flow.resetFocus = () => {
 let ae=document.activeElement;
 while(ae&&ae.shadowRoot) ae = ae.shadowRoot.activeElement;
 return !ae || ae.blur() || ae.focus() || true;
}