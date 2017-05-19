
var editor = document.getElementById('editor');

var Tools = {};

var setHtml = function(html){
  editor.innerHTML = html;
}

var getHtml = function() {
  return editor.innerHTML;
}

var setEnabled = function(editable) {
  editor.contentEditable = editable;
}

var focus = function() {
  editor.focus();
}

var setPadding = function(left, top, right, bottom) {
  editor.style.paddingLeft = left + 'px';
  editor.style.paddingTop = top + 'px';
  editor.style.paddingRight = right + 'px';
  editor.style.paddingBottom = bottom + 'px';
}

/** Tools fully implemented with execCommand.
*   Associated functions are generated programmatically for each.
*/
var SIMPLE_TOOLS = ['bold', 'italic', 'underline', 'strikethrough', 'justifyLeft', 'justifyCenter', 'justifyRight', 'justifyFull']
for(tool of SIMPLE_TOOLS) {
  Tools[tool] = (function(tool){
    tool_funcs = {};
    tool_funcs['run'] = function() {
      document.execCommand(tool, false, null);
      updateToolStates();
    }
    tool_funcs['active'] = function() {
      return document.queryCommandState(tool);
    }
    tool_funcs['current_state'] = tool_funcs['active']();
    return tool_funcs;
  }(tool));
}

var onToolTriggeredListener = function(tool_name) {
  Tools[tool_name]['run']();
}

var onBodyChangeListener = function() {
  setHtml(State.jsGetHtml())
}

State.setOnToolTriggeredListener('onToolTriggeredListener');
State.setOnBodyChangeListener('onBodyChangeListener');
onBodyChangeListener();

var updateToolStates = function() {
  var states = []
  for(tool_name in Tools) {
    tool = Tools[tool_name];
    current_state = tool['active']();
    if (tool['current_state'] != current_state) {
      states.push(tool_name + ':' + Number(current_state));
      tool['current_state'] = tool['active']();
    }
  }
  if (states.length !== 0) {
    State.updateToolStates(states.join(';'));
  }
}


//By default pressing enter on a content editable division wraps the new line in
// a div tag - this replaces that to wrap it instead in a p tag.
editor.addEventListener('keypress', function(e){
  if(document.queryCommandValue('formatBlock') == 'div') document.execCommand('formatBlock', false, 'p');
}, false);

editor.addEventListener('input', function(e){
  State.jsSetHtml(getHtml());
  updateToolStates();
})

editor.addEventListener('click', function(e){
  updateToolStates();
})


State.ready();
