package cargill.com.purina.dashboard.View

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import cargill.com.purina.R
import android.widget.ImageView
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.source.DocumentSource
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_pdf_view.*
import kotlinx.android.synthetic.main.fragment_detail_catalogue.*
import java.io.File

class PdfViewActivity : AppCompatActivity() {

  lateinit var pdfView: PDFView
  lateinit var ivBack: ImageView

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_pdf_view)

    val rawPathWithLanguage = intent.getStringExtra("absolutePath")
    var filePath = intent.getStringExtra("filePath")
    val header = intent.getStringExtra("header")
    //Log.i("filePath",filePath)

    pdfView = findViewById(R.id.pdfView);
    if(rawPathWithLanguage.isNullOrEmpty()){
      pdfViewerHeader.text = header
      if(filePath.isNullOrEmpty()){
        Snackbar.make(window.decorView,"No Proper File path", Snackbar.LENGTH_LONG).show()
      }else{
        Log.i("filepathhhhh", filePath)

        if(filePath.endsWith(".pdf")){
          Log.i("filepathhhhh", ".pdf")
        }else{
          Log.i("filepathhhhh", "No")
          filePath = filePath + ".pdf"
        }

        pdfView.fromFile(File(filePath)).load()
      }
    }else{
      pdfView.fromAsset(rawPathWithLanguage).load()
    }
    ivBack = findViewById(R.id.backbtn)
    ivBack.setOnClickListener {
      finish()
    }
  }
  override fun onBackPressed() {
    super.onBackPressed()
    finish()
  }
}