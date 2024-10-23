package com.practicum.playlistmaker.sharing.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.sharing.domain.SharingInteractor

class SharingProviderImpl(private val context: Context) : SharingInteractor {

    override fun shareApp() {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.link_share))
        shareIntent.type = "text/plain"
        context.startActivity(
            Intent.createChooser(
                shareIntent,
                context.getString(R.string.message_share)
            ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    override fun openSupport() {
        val supportIntent = Intent(Intent.ACTION_SENDTO)
        supportIntent.data = Uri.parse("mailto:")
        supportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(context.getString(R.string.mail_support)))
        supportIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.subject_support))
        supportIntent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.text_support))
        context.startActivity(supportIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    override fun openTermsUse() {
        val url = Uri.parse(context.getString(R.string.link_terms_use))
        val termsUseIntent = Intent(Intent.ACTION_VIEW, url)
        context.startActivity(termsUseIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

}