package com.sharitoo.launcher

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebView

class MainActivity : Activity() {
    private lateinit var web: WebView

    // Часть названия игры для поиска среди установленных приложений (без учёта регистра)
    private val gameLabel = "bear adventure"

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        web = WebView(this)
        web.settings.javaScriptEnabled = true
        web.settings.domStorageEnabled = true
        web.addJavascriptInterface(Bridge(), "Android")
        web.loadUrl("file:///android_asset/index.html")
        setContentView(web)
    }

    inner class Bridge {
        /** Запускает установленную Super Bear Adventure. Возвращает "ok" или "notfound". */
        @JavascriptInterface
        fun launchGame(): String {
            val pm = packageManager
            val q = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
            val app = pm.queryIntentActivities(q, 0)
                .firstOrNull { it.loadLabel(pm).toString().lowercase().contains(gameLabel) }
                ?: return "notfound"
            val i = pm.getLaunchIntentForPackage(app.activityInfo.packageName) ?: return "notfound"
            startActivity(i)
            return "ok"
        }

        @JavascriptInterface
        fun copy(text: String) {
            val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            cm.setPrimaryClip(ClipData.newPlainText("Sharitoo", text))
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (web.canGoBack()) web.goBack() else super.onBackPressed()
    }
}
