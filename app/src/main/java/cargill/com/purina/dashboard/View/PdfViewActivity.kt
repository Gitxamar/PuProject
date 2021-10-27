package cargill.com.purina.dashboard.View

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import cargill.com.purina.R
import android.widget.ImageView
import com.github.barteksc.pdfviewer.PDFView
import kotlinx.android.synthetic.main.activity_pdf_view.*
import java.io.File

class PdfViewActivity : AppCompatActivity() {

  lateinit var pdfView: PDFView
  lateinit var ivBack: ImageView

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_pdf_view)

    val rawPathWithLanguage = intent.getStringExtra("absolutePath")
    val filePath = intent.getStringExtra("filePath")
    val header = intent.getStringExtra("header")

    pdfView = findViewById(R.id.pdfView);
    if(rawPathWithLanguage.isNullOrEmpty()){
      pdfViewerHeader.text = header
      pdfView.fromFile(File(filePath)).load()
    }else{
      pdfView.fromAsset(rawPathWithLanguage).load()
    }
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