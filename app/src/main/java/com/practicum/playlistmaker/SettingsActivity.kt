package com.practicum.playlistmaker

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textview.MaterialTextView

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val tbSettings = findViewById<MaterialToolbar>(R.id.tbSettings)
        val tvShareApp = findViewById<MaterialTextView>(R.id.tvShareApp)
        val tvWriteToSupport =
            findViewById<MaterialTextView>(R.id.tvWriteToSupport)
        val tvUserAgreement = findViewById<MaterialTextView>(R.id.tvUserAgreement)

        tbSettings.setNavigationOnClickListener {
            finish()
        }

        tvShareApp.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(
                Intent.EXTRA_TEXT,
                getString(R.string.share_app_url)
            )
            intent.type = "text/plain"
            startActivity(intent)
        }

        tvWriteToSupport.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = "mailto:".toUri()
            intent.putExtra(
                Intent.EXTRA_EMAIL,
                arrayOf(getString(R.string.support_email))
            )
            intent.putExtra(
                Intent.EXTRA_SUBJECT,
                getString(R.string.support_subject)
            )
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.support_text))
            startActivity(intent)
        }

        tvUserAgreement.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = getString(R.string.user_agreement_url).toUri()
            startActivity(intent)
        }
    }
}