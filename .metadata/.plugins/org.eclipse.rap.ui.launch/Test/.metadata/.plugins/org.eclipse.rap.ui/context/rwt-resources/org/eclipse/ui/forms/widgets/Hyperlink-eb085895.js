qx.Class.define("org.eclipse.ui.forms.widgets.Hyperlink",{extend:qx.ui.basic.Atom,construct:function(b){arguments.callee.base.call(this);this.setAppearance("hyperlink");this.setLabel("(empty)");var a=this.getLabelObject();a.setAppearance("hyperlink-label");a.setMode(qx.constant.Style.LABEL_MODE_HTML);a.setWrap(qx.lang.String.contains(b,"wrap"));this.setLabel("");this._text="";this._underlined=false;this._savedBackgroundColor=null;this._savedTextColor=null;this._activeBackgroundColor=null;this._activeTextColor=null;this._underlineMode=null;this._hover=false;this.addEventListener("mousemove",this._onMouseMove,this);this.addEventListener("mouseout",this._onMouseOut,this)},destruct:function(){this.removeEventListener("mousemove",this._onMouseMove,this);this.removeEventListener("mouseout",this._onMouseOut,this)},statics:{UNDERLINE_NEVER:1,UNDERLINE_HOVER:2,UNDERLINE_ALWAYS:3},members:{setText:function(a){this._text=a;this._updateText()},setUnderlined:function(a){this._underlined=a;this._updateText()},_updateText:function(){var a=this._underlined?"<u>"+this._text+"</u>":this._text;this.setLabel(a)},setActiveBackgroundColor:function(a){this._activeBackgroundColor=a},setActiveTextColor:function(a){this._activeTextColor=a},setUnderlineMode:function(a){this._underlineMode=a},setHasSelectionListener:function(a){if(a){this.addEventListener("click",org.eclipse.swt.EventUtil.widgetDefaultSelected,this)}else{this.removeEventListener("click",org.eclipse.swt.EventUtil.widgetDefaultSelected,this)}},_onMouseMove:function(a){if(!this._hover){this._savedBackgroundColor=this.getBackgroundColor();if(this._activeBackgroundColor!=null){this.setBackgroundColor(this._activeBackgroundColor)}this._savedTextColor=this.getTextColor();if(this._activeTextColor!=null){this.setTextColor(this._activeTextColor)}var b=org.eclipse.ui.forms.widgets.Hyperlink.UNDERLINE_HOVER;if(this._underlineMode==b){this.setStyleProperty("textDecoration","underline")}this._hover=true}},_onMouseOut:function(a){if(this._hover){this._hover=false;this.setBackgroundColor(this._savedBackgroundColor);this.setTextColor(this._savedTextColor);var b=org.eclipse.ui.forms.widgets.Hyperlink.UNDERLINE_HOVER;if(this._underlineMode==b){this.setStyleProperty("textDecoration","none")}}}}});