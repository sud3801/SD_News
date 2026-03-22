package com.sd.sdnews;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class WebViewActivity extends AppCompatActivity {

    public static final String EXTRA_URL        = "article_url";
    public static final String EXTRA_TITLE      = "article_title";
    public static final String EXTRA_SOURCE     = "article_source";
    public static final String EXTRA_DATE       = "article_date";

    private static final String PREFS_NAME   = "SDNewsPrefs";
    private static final String KEY_DARK_MODE = "darkMode";

    private WebView webView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        // Toolbar — back button
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            // Show source name as toolbar title
            String source = getIntent().getStringExtra(EXTRA_SOURCE);
            getSupportActionBar().setTitle(source != null ? source : "Article");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        progressBar = findViewById(R.id.progressBar);
        webView     = findViewById(R.id.webView);

        // Required for Readability.js to run
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);

        // Show/hide progress bar as page loads
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setProgress(newProgress);
                progressBar.setVisibility(newProgress < 100 ? View.VISIBLE : View.GONE);
            }
        });

        // Once page finishes loading, inject Readability + our reader CSS
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                injectReaderMode(view);
            }

            // Keep all navigation inside the WebView
            @Override
            public boolean shouldOverrideUrlLoading(WebView view,
                                                    WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return true;
            }
        });

        String url = getIntent().getStringExtra(EXTRA_URL);
        if (url != null && !url.isEmpty()) {
            webView.loadUrl(url);
        } else {
            finish(); // nothing to show
        }
    }

    private void injectReaderMode(WebView view) {
        // Read dark mode preference
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isDark = prefs.getBoolean(KEY_DARK_MODE, false);
        String darkClass = isDark ? "dark" : "";

        // Safely escape metadata for JavaScript
        String title  = escapeJs(getIntent().getStringExtra(EXTRA_TITLE));
        String source = escapeJs(getIntent().getStringExtra(EXTRA_SOURCE));
        String date   = escapeJs(getIntent().getStringExtra(EXTRA_DATE));

        // Build the JS injection:
        // 1. Load Readability.js from assets
        // 2. Run it on the current document
        // 3. Replace page content with clean reader HTML + our CSS
        String js = "(function() {" +

                // --- Load Readability.js from assets ---
                "var script = document.createElement('script');" +
                "script.src = 'file:///android_asset/Readability.js';" +
                "script.onload = function() {" +

                // --- Run Readability ---
                "var documentClone = document.cloneNode(true);" +
                "var reader = new Readability(documentClone);" +
                "var article = reader.parse();" +
                "var body = article ? article.content : " +
                "'<p>Could not extract article. " +
                "Try the original link.</p>';" +

                // --- Build clean reader page ---
                "var html = '<!DOCTYPE html><html><head>" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">" +
                "<link rel=\"stylesheet\" href=\"file:///android_asset/reader.css\">" +
                "</head>" +
                "<body class=\"" + darkClass + "\">" +
                "<div id=\"article-container\">" +
                "<div id=\"article-source\">" + source + "</div>" +
                "<div id=\"article-title\">" + title + "</div>" +
                "<div id=\"article-meta\">" + date + "</div>" +
                "<div id=\"article-body\">' + body + '</div>" +
                "</div></body></html>';" +

                // --- Replace entire page with reader HTML ---
                "document.open();" +
                "document.write(html);" +
                "document.close();" +
                "};" +
                "document.head.appendChild(script);" +
                "})();";

        view.evaluateJavascript(js, null);
    }

    /**
     * Escapes a string for safe embedding inside a JavaScript string literal.
     */
    private String escapeJs(String input) {
        if (input == null) return "";
        return input
                .replace("\\", "\\\\")
                .replace("'",  "\\'")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "");
    }

    @Override
    public void onBackPressed() {
        // If WebView can go back (e.g. user tapped a link), go back in WebView
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}