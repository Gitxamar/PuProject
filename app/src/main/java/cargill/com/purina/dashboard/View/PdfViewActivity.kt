package cargill.com.purina.dashboard.View

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import cargill.com.purina.R
import java.io.File
import java.nio.file.Path
import android.os.Environment
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import cargill.com.purina.databinding.FragmentAccountBinding
import com.github.barteksc.pdfviewer.PDFView
import java.io.InputStream


class PdfViewActivity : AppCompatActivity() {

  lateinit var pdfView: PDFView
  lateinit var ivBack: ImageView

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_pdf_view)

    val rawPathWithLanguage = intent.getStringExtra("absolutePath")

    pdfView = findViewById(R.id.pdfView);
    pdfView.fromAsset(rawPathWithLanguage).load()

    ivBack = findViewById(R.id.back)
    ivBack.setOnClickListener {
      finish()
    }

  }

  override fun onBackPressed() {
    super.onBackPressed()
    finish()
  }

}