package b3nac.injuredandroid

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DeepLinkActivity : AppCompatActivity() {
    internal var click = 0
    private val TAG = "DeepLinkActivity"
    private var mAuth: FirebaseAuth? = null

    val refDirectory = "/binary"

    var database = FirebaseDatabase.getInstance().reference
    var childRef = database.child(refDirectory)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deep_link)

        val intentToUri = getIntent()
        val data = intentToUri.data
        val appSchema = "flag11" == data!!.getScheme()

        if (appSchema) {

            //val convertScheme = "https://" + data.host

            //startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(convertScheme)))
            startActivity(Intent(Intent.ACTION_VIEW))

        } else {
            startActivity(Intent(Intent.ACTION_VIEW))
        }

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener { view ->
            if (click == 0) {
                Snackbar.make(view, "This is one part of the puzzle.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
                //Figure out how to login anonymously on click
                click++
            } else if (click == 1) {
                Snackbar.make(view, "Find the compiled treasure.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
                click = 0
            }
        }
    }

    fun submitFlag(@Suppress("UNUSED_PARAMETER")view: View?) {
        val editText5 = findViewById<EditText>(R.id.editText5)
        val post = editText5.text.toString()
        signInAnonymously()

        childRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val value = dataSnapshot.value as String?
                val settings = getSharedPreferences("b3nac.injuredandroid", Context.MODE_PRIVATE)
                if (post == value) {
                    FlagsOverview.flagElevenButtonColor = true
                    val editor: SharedPreferences.Editor = settings.edit()
                    editor.putBoolean("flagElevenButtonColor", true).apply()
                    correctFlag()
                } else {
                    Toast.makeText(this@DeepLinkActivity, "Try again! :D",
                            Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException())
            }
        })
    }

    fun signInAnonymously() {
        mAuth = FirebaseAuth.getInstance()
        mAuth!!.signInAnonymously()
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this@DeepLinkActivity, "Authentication succeeded.",
                                Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@DeepLinkActivity, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()

                    }
                }
    }

    fun correctFlag() {
        val intent = Intent(this, FlagOneSuccess::class.java)
        startActivity(intent)
    }

}



