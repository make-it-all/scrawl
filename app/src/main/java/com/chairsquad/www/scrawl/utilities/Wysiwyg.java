package com.chairsquad.www.scrawl.utilities;

import android.content.Context;
import android.content.res.TypedArray;
import android.databinding.ObservableArrayMap;
import android.databinding.ObservableMap;
import android.databinding.ObservableMap.OnMapChangedCallback;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.chairsquad.www.scrawl.R;

/**
 * Created by henry on 18/04/17.
 */

public class Wysiwyg extends WebView {

    private static final String BASE_HTML = "file:///android_asset/base_editor.html";

    private WysiwygState mState;
    private boolean scriptLoaded = false;


    // callback listeners
    public interface OnToolChangeListener { void onToolChange(String tool, boolean enabled); }
    private OnToolChangeListener mOnToolChangeListener;

    public interface onBodyChangeListener { void onBodyChange(String body); }
    private onBodyChangeListener mOnBodyChangeListener;

    // Constructors ///////

    public Wysiwyg(Context context) {
        this(context, null);
    }

    public Wysiwyg(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.webViewStyle);
    }

    public Wysiwyg(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //setup basic webview stuff
        setVerticalScrollBarEnabled(false);
        setHorizontalScrollBarEnabled(false);
        getSettings().setJavaScriptEnabled(true);
        setWebViewClient(new WebViewClient());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setWebContentsDebuggingEnabled(true);
        }

        //load wysiwyg script
        mState = new WysiwygState();
        addJavascriptInterface(mState, "State");
        loadUrl(BASE_HTML);

        //setup attributes
        processAttributes(context, attrs);

        mState.getToolStates().addOnMapChangedCallback(new OnMapChangedCallback<ObservableMap<String, Boolean>, String, Boolean>() {
            @Override
            public void onMapChanged(ObservableMap<String, Boolean> stringBooleanObservableMap, String s) {
                triggerOnToolChange(s, stringBooleanObservableMap.get(s));
            }
        });
    }

    private void processAttributes(Context context, AttributeSet attrs) {
        int [] attributes = new int [] {
                android.R.attr.padding,
                android.R.attr.paddingLeft,
                android.R.attr.paddingTop,
                android.R.attr.paddingBottom,
                android.R.attr.paddingRight,
                R.styleable.Wysiwyg_enabled
        };
        TypedArray arr = context.obtainStyledAttributes(attrs, attributes);

        //handle padding
        int left, top, right, bottom;
        left = top = right = bottom = arr.getDimensionPixelOffset(0, 0);
        left = arr.getDimensionPixelOffset(1, left);
        top = arr.getDimensionPixelOffset(2, top);
        right = arr.getDimensionPixelOffset(3, right);
        bottom = arr.getDimensionPixelOffset(4, bottom);
        setPadding(left, top, right, bottom);

        //handle enabled/disabled
        boolean enabled = arr.getBoolean(R.styleable.Wysiwyg_enabled, true);
        setEnabled(enabled);

        arr.recycle();
    }

    public void setEnabled(boolean enabled) {
        mState.setEnabled(enabled);
    }

    public void setOnToolChangeListener(OnToolChangeListener listener) {
        mOnToolChangeListener = listener;
    }



    public void setHtml(String html) {
        Log.d("SCRAWL", "SETTING BODY" + html);
        mState.setHtml(html);
    }

    public String getHtml() {
        return mState.getHtml();
    }

    public void setBold() {
        mState.triggerTool("bold");
    }
    public void setItalic() {
        mState.triggerTool("italic");
    }
    public void setUnderline() {
        mState.triggerTool("underline");
    }
    public void setStrikethough() {
        mState.triggerTool("strikethrough");
    }


    private void runScript(final String script) {
        if (scriptLoaded) {
            post(new Runnable() {
                public void run() {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        evaluateJavascript(script, null);
                    } else {
                        loadUrl("javascript:" + script);
                    }
                }
            });
        } else {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    runScript(script);
                }
            }, 50);
        }

    }


    public void setPadding(int left, int top, int right, int bottom) {
        super.setPadding(left, top, right, bottom);
        mState.setPadding(left, top, right, bottom);
    }


    private void triggerOnToolChange(final String tool, final boolean state) {
        if (mOnToolChangeListener != null) {
            post(new Runnable() {
                @Override
                public void run() {
                    mOnToolChangeListener.onToolChange(tool, state);
                }
            });
        }
    }

    private void triggerOnBodyChange() {
        if (mOnBodyChangeListener != null) {
            mOnBodyChangeListener.onBodyChange(getHtml());
        }
    }

    public boolean focus() {
        if (super.requestFocus()) {
            mState.focus();
            return true;
        }
        return false;
    }


    /**
     * Handles all interactions with the script.
     */

    private class WysiwygState {

        private String mBodyHtml = "";

        private ObservableArrayMap<String, Boolean> mToolStates;

        public WysiwygState() {
            mToolStates = new ObservableArrayMap<>();
        }

        private String mOnBodyChangeListener;
        @JavascriptInterface
        public void setOnBodyChangeListener(String callback_name) {
            mOnBodyChangeListener = callback_name;
        }

        public String mOnToolTriggeredListener;
        @JavascriptInterface
        public void setOnToolTriggeredListener(String callback_name) {
            mOnToolTriggeredListener = callback_name;
        }

        @JavascriptInterface
        public void jsSetHtml(String html) {
            mBodyHtml = html;
        }

        @JavascriptInterface
        public String jsGetHtml() {
            return mBodyHtml;
        }

        @JavascriptInterface
        public void updateToolStates(String states) {
             String[] toolStatePairs = states.split("[;:]");
             for (int i=0; i<toolStatePairs.length; i+=2) {
               mToolStates.put(toolStatePairs[i], toolStatePairs[i+1].equals("1"));
             }
        }

        @JavascriptInterface
        public void triggerTool(String tool_name) {

            if (mOnToolTriggeredListener != null) {
                String script = mOnToolTriggeredListener + "('" + tool_name + "');";
                runScript(script);
            }
        }

        @JavascriptInterface
        public void ready() {
            scriptLoaded = true;
        }




        public ObservableArrayMap<String, Boolean> getToolStates() {
            return mToolStates;
        }

        public void setEnabled(boolean enabled) {
            runScript("setEnabled(" + enabled + ")");
        }

        public void focus() {
            runScript("focus()");
        }

        public void setHtml(String html) {
            mBodyHtml = html;
            if (mOnBodyChangeListener != null) {
                String script = mOnBodyChangeListener + "();";
                runScript(script);
            }
        }

        public String getHtml() {
            return mBodyHtml;
        }

        public void setPadding(int left, int top, int right, int bottom) {
            String script = "setPadding(" + left + ","  + top + ","  + right+ ","  + bottom + ")";
            runScript(script);
        }

    }

}
