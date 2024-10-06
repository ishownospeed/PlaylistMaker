package com.practicum.playlistmaker.sharing.data

import android.app.Application
import android.content.Intent
import android.net.Uri
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.sharing.domain.SharingInteractor

class SharingProviderImpl(private val application: Application) : SharingInteractor {

    override fun shareApp() {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.putExtra(Intent.EXTRA_TEXT, application.getString(R.string.link_share))
        shareIntent.type = "text/plain"
        application.startActivity(
            Intent.createChooser(
                shareIntent,
                application.getString(R.string.message_share)
            ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    override fun openSupport() {
        val supportIntent = Intent(Intent.ACTION_SENDTO)
        supportIntent.data = Uri.parse("mailto:")
        supportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(application.getString(R.string.mail_support)))
        supportIntent.putExtra(Intent.EXTRA_SUBJECT, application.getString(R.string.subject_support))
        supportIntent.putExtra(Intent.EXTRA_TEXT, application.getString(R.string.text_support))
        application.startActivity(supportIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    override fun openTermsUse() {
        val url = Uri.parse(application.getString(R.string.link_terms_use))
        val termsUseIntent = Intent(Intent.ACTION_VIEW, url)
        application.startActivity(termsUseIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

}