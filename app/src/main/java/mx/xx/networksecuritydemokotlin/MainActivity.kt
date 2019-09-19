package mx.xx.networksecuritydemokotlin

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*
import java.net.MalformedURLException
import java.net.URL

class MainActivity : AppCompatActivity() {

    private var downloadButton: Button? = null
    private var urlBarView: TextView? = null
    private var select: Int? = null
    private var selfSignedButton: RadioButton? = null
    private var letsEncryptButton: RadioButton? = null
    private var customCAButton: RadioButton? = null
    private var plainTextButton: RadioButton? = null

    private inner class ReadTask : AsyncTask<String, Int, String>() {
        override fun doInBackground(vararg params: String): String {
            return getResponseFromUrl(params[0])
        }

        private fun getResponseFromUrl(param: String): String {
            try {
                val url = URL(param)
                val urlConnection = url.openConnection()
                val `in` = urlConnection.getInputStream()
                val r = BufferedReader(InputStreamReader(`in`))
                val total = StringBuilder()
                var line: String
                line = r.readLine()
                total.append(line)
                Log.d("MESSAGE RECEIVED: ", total.toString())
                return total.toString()
            } catch (e: MalformedURLException) {
                e.printStackTrace()
                return "M"
            } catch (e: IOException) {
                e.printStackTrace()
                return "ERROR: " + e.message
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        select = 1
        downloadButton = findViewById(R.id.button) as Button
        urlBarView = findViewById(R.id.urlbar) as TextView

        selfSignedButton = findViewById(R.id.selfSignedButton) as RadioButton
        letsEncryptButton = findViewById(R.id.letsEncryptButton) as RadioButton
        customCAButton = findViewById(R.id.customCAButton) as RadioButton
        plainTextButton = findViewById(R.id.plainButton) as RadioButton

        selfSignedButton!!.setOnClickListener {
            urlBarView!!.setText("Self signed cert at https://secure.singleframesecurity.net:444")
            select = 4
        }

        letsEncryptButton!!.setOnClickListener {
            urlBarView!!.setText("Proper cert at https://singleframesecurity.net:443")
            select = 3
        }

        customCAButton!!.setOnClickListener {
            urlBarView!!.setText("Custom CA pinned at https://test.singleframesecurity.net:442")
            select = 2
        }

        plainTextButton!!.setOnClickListener {
            urlBarView!!.setText("Plain text on http://singleframesecurity.net")
            select = 1
        }
        downloadButton!!.setOnClickListener {
            try {
                val rt = ReadTask()
                if (select == 1) {
                    rt.execute("http://singleframesecurity.net/success.html")
                } else if (select == 2) {
                    rt.execute("https://test.singleframesecurity.net:442/success.html")
                } else if (select == 3) {
                    rt.execute("https://singleframesecurity.net/success.html")
                } else if (select == 4) {
                    rt.execute("https://secure.singleframesecurity.net:444/success.html")
                }
                val back = rt.get()
                resultView!!.setText(back)
            } catch (e: Exception) {
                val sw = StringWriter()
                val pw = PrintWriter(sw)
                e.printStackTrace(pw)
                resultView!!.setText(sw.toString())
            }
        }
    }
}
