package com.practicum.playlistmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val buttonArrowLeft = findViewById<ImageView>(R.id.arrow_left_settings)
        val buttonShare = findViewById<ImageView>(R.id.share)
        val buttonSupport = findViewById<ImageView>(R.id.support)
        val buttonTermsUse = findViewById<ImageView>(R.id.terms_use)

        buttonArrowLeft.setOnClickListener { finish() }

        buttonShare.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.link_share))
            shareIntent.type = "text/plain"
            startActivity(Intent.createChooser(shareIntent, getString(R.string.message_share)))
        }

        buttonSupport.setOnClickListener {
            val supportIntent = Intent(Intent.ACTION_SENDTO)
            supportIntent.data = Uri.parse("mailto:")
            supportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.mail_support)))
            supportIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.subject_support))
            supportIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.text_support))
            startActivity(supportIntent)
        }

        buttonTermsUse.setOnClickListener {
            val url = Uri.parse(getString(R.string.link_terms_use))
            val termsUseIntent = Intent(Intent.ACTION_VIEW, url)
            startActivity(termsUseIntent)
        }

    }
}