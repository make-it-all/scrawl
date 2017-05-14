/** DO NOT LOAD ON THE APP
This file adds the same javascript interface methods for testing purposes only.
Use this as a guide as to what methods the interface needs - it is one to one!
double underscored methods are private
single underscore are *not* JavascriptInterfaces
*/

var State = {
  mBodyHtml: '',
  mToolstates: []//map<String, boolean>
}

// @JavascriptInterface
State.mOnBodyChangeListener = null;
State.setOnBodyChangeListener = function(callback) {
  State.mOnBodyChangeListener = callback;
}

// @JavascriptInterface
State.mOnToolTriggeredListener = null;
State.setOnToolTriggeredListener = function(callback) {
  State.mOnToolTriggeredListener = callback;
}

//Called by javascript when the html updates.
// @JavascriptInterface
State.jsSetHtml = function(html) {
  this.mBodyHtml = html;
}

// @JavascriptInterface
State.jsGetHtml = function() {
  return this.mBodyHtml;
}

//states in the form : state_name:value;state_name:value
// @JavascriptInterface
State.updateToolStates = function(states) {
  console.log(states);
  // String[] toolStatePairs = states.split('[;:]');
  // for (int i=0; i<toolStatePairs.length; i+=2) {
  //   mToolstates.put(toolStatePairs[i], toolStatePairs[i+1]);
  // }
}

State._triggerTool = function(tool_name) {
  if (this.mOnToolTriggeredListener !== null) {
    window.location.href = "javascript: " + this.mOnToolTriggeredListener + "('" + tool_name +"')"
  }
}

State._setHtml = function(html) {
  this.mBodyHtml = html;
  if (this.mOnBodyChangeListener !== null) {
    window.location.href = "javascript: " + this.mOnBodyChangeListener + "()"
  }
}

State._getHtml = function() {
  return this.mBodyHtml;
}
