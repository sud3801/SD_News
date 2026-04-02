package com.sd.sdnews;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class WebViewActivity extends AppCompatActivity {

    public static final String EXTRA_URL    = "article_url";
    public static final String EXTRA_TITLE  = "article_title";
    public static final String EXTRA_SOURCE = "article_source";
    public static final String EXTRA_DATE   = "article_date";

    private static final String PREFS_NAME    = "SDNewsPrefs";
    private static final String KEY_DARK_MODE = "darkMode";

    private WebView webView;
    private ProgressBar progressBar;

    // Store these for use in share
    private String articleUrl;
    private String articleTitle;
    private String articleSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        // Cache extras for share
        articleUrl    = getIntent().getStringExtra(EXTRA_URL);
        articleTitle  = getIntent().getStringExtra(EXTRA_TITLE);
        articleSource = getIntent().getStringExtra(EXTRA_SOURCE);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(
                    articleSource != null ? articleSource : "Article");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        progressBar = findViewById(R.id.progressBar);
        webView     = findViewById(R.id.webView);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setProgress(newProgress);
                progressBar.setVisibility(
                        newProgress < 100 ? View.VISIBLE : View.GONE);
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                injectReaderMode(view);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view,
                                                    WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return true;
            }
        });

        if (articleUrl != null && !articleUrl.isEmpty()) {
            webView.loadUrl(articleUrl);
        } else {
            finish();
        }
    }

    // ── Share button in toolbar ──────────────────────────────────────────────

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.webview_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_share) {
            shareArticle();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void shareArticle() {
        StringBuilder sb = new StringBuilder();

        // Source
        if (articleSource != null && !articleSource.isEmpty()) {
            sb.append("📰 ").append(articleSource).append("\n\n");
        }

        // Headline
        if (articleTitle != null && !articleTitle.isEmpty()) {
            sb.append(articleTitle).append("\n\n");
        }

        // Article URL
        if (articleUrl != null && !articleUrl.isEmpty()) {
            sb.append("Read more: ").append(articleUrl).append("\n\n");
        }

        // SD News branding
        sb.append("Shared via SD News\n");
        sb.append("Get it on Google Play: ");
        sb.append("https://play.google.com/store/apps/details?id=com.sd.sdnews");

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, sb.toString());
        startActivity(Intent.createChooser(shareIntent, "Share via..."));
    }

    // ── Reader Mode injection ────────────────────────────────────────────────

    private void injectReaderMode(WebView view) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isDark = prefs.getBoolean(KEY_DARK_MODE, false);

        // JS-escape for embedding inside the JS string itself
        String title  = escapeJs(articleTitle);
        String source = escapeJs(articleSource);
        String date   = escapeJs(getIntent().getStringExtra(EXTRA_DATE));

        // darkClass is safe as-is but let's be explicit
        String darkClass = isDark ? "dark" : "";

        String js = "(function() {" +
                "var script = document.createElement('script');" +
                "script.src = 'file:///android_asset/Readability.js';" +
                "script.onload = function() {" +
                "  var documentClone = document.cloneNode(true);" +
                "  var reader = new Readability(documentClone);" +
                "  var article = reader.parse();" +
                "  var body = article ? article.content : " +
                "    '<p>Could not extract article.</p>';" +

                // Build the HTML entirely inside JS using DOM APIs —
                // no string concatenation of user data into HTML at all
                "  var html = '<!DOCTYPE html><html><head>" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">" +
                "    <link rel=\"stylesheet\" href=\"file:///android_asset/reader.css\">" +
                "  </head>" +
                "  <body class=\"" + darkClass + "\">" +
                "    <div id=\"article-container\">" +
                "      <div id=\"article-source\"></div>" +
                "      <div id=\"article-title\"></div>" +
                "      <div id=\"article-meta\"></div>" +
                "      <div id=\"article-body\"></div>" +
                "    </div></body></html>';" +

                // Write the safe HTML skeleton first
                "  document.open();" +
                "  document.write(html);" +
                "  document.close();" +

                // Then set user data via textContent (never innerHTML) —
                // this is safe because textContent never interprets HTML tags
                "  document.getElementById('article-source').textContent = '" + source + "';" +
                "  document.getElementById('article-title').textContent  = '" + title  + "';" +
                "  document.getElementById('article-meta').textContent   = '" + date   + "';" +

                // article body comes from Readability so we trust it as HTML
                "  document.getElementById('article-body').innerHTML = body;" +
                "};" +
                "document.head.appendChild(script);" +
                "})();";

        view.evaluateJavascript(js, null);
    }

    private String escapeJs(String input) {
        if (input == null) return "";
        return input
                .replace("\\", "\\\\")
                .replace("'",  "\\'")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "")
                .replace("<",  "\\u003C")   // prevents </script> injection
                .replace(">",  "\\u003E");
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}