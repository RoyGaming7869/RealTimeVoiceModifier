package com.real.time.voice.modifier

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.real.time.voice.modifier.databinding.ActivityPrivacyPolicyBinding


class PrivacyPolicyActivity : AppCompatActivity() {
    lateinit var binding: ActivityPrivacyPolicyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        binding= ActivityPrivacyPolicyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.backrelative.setOnClickListener {
            finish()
        }
        binding.webview.getSettings().setJavaScriptEnabled(true);
        binding.webview.loadUrl(getString(R.string.privavy_url));
        binding.webview.setWebChromeClient(object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, progress: Int) {
                binding.progressBar.setProgress(progress)
            }
        })
        binding.webview.setWebViewClient(object : WebViewClient() {


            override fun onReceivedError(
                view: WebView,
                errorCode: Int,
                description: String,
                failingUrl: String
            ) {
                binding.webview.loadUrl(getString(R.string.privavy_url));
            }

            override fun onLoadResource(view: WebView, url: String) { //Doesn't work
                //swipe.setRefreshing(true);
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                binding.progressBar.setVisibility(View.VISIBLE)

            }

            override fun onPageFinished(view: WebView, url: String) {

                //Hide the SwipeReefreshLayout
                binding.progressBar.setVisibility(View.GONE)
            }
        })

    }

    override fun onBackPressed() {

        if (binding.webview.canGoBack()) {
            binding.webview.goBack();
        } else {
            super.onBackPressed()
        }
    }
}