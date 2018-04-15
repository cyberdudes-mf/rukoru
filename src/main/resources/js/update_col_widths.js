var colWidth = $('#col_width input');
colWidth.val('%s');
var evt = $.Event('keyup');
evt.which = 13;
colWidth.trigger(evt);