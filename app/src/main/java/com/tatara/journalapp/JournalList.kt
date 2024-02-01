package com.tatara.journalapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.tatara.journalapp.databinding.ActivityJournalListBinding

class JournalList : AppCompatActivity() {
    private lateinit var binding: ActivityJournalListBinding

    lateinit var firebaseAuth: FirebaseAuth
    lateinit var user: FirebaseUser
    var db = FirebaseFirestore.getInstance()
    lateinit var storageReference: StorageReference
    var collectionReference: CollectionReference = db.collection("Journal")

    lateinit var journalList: MutableList<Journal>
    lateinit var adapter: JournalRecyclerAdapter

    lateinit var noPostsTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_journal_list)

        firebaseAuth = Firebase.auth
        user = firebaseAuth.currentUser!!

        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager =
            LinearLayoutManager(this)

        journalList = arrayListOf<Journal>()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add -> if (user != null && firebaseAuth != null) {
                val intent = Intent(this, AddJournalAcitivity::class.java)
                startActivity(intent)
            }

            R.id.action_signout -> if (user != null && firebaseAuth != null) {
                firebaseAuth.signOut()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()

        collectionReference.whereEqualTo("userId", user.uid)
            .get()
            .addOnSuccessListener {
                Log.i("TAGY", "size: ${it.size()}")
                if (!it.isEmpty) {
                    Log.i("TAGY", "Elements: $it")
                    for (document in it) {
                        val journal = Journal(
                            document.data["title"].toString(),
                            document.data["description"].toString(),
                            document.data["img"].toString(),
                            document.data["userId"].toString(),
                            document.data["date"] as Timestamp,
                            document.data["username"].toString()
                        )
                        journalList.add(journal)
                    }
                    adapter = JournalRecyclerAdapter(
                        this,
                        journalList
                    )
                    binding.recyclerView.adapter = adapter
                    adapter.notifyDataSetChanged()
                } else {
                    binding.textNoPosts.visibility = View.VISIBLE
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Oops, something went wrong.", Toast.LENGTH_LONG).show()
            }
    }
}