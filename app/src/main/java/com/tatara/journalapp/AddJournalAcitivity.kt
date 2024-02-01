package com.tatara.journalapp

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.databinding.DataBindingUtil
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.tatara.journalapp.databinding.ActivityAddJournalAcitivityBinding
import java.util.Date

class AddJournalAcitivity : AppCompatActivity() {
    lateinit var binding: ActivityAddJournalAcitivityBinding

    var currentUserId: String = ""
    var currentUserName: String = ""

    lateinit var auth: FirebaseAuth
    lateinit var user: FirebaseUser

    var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    lateinit var storageReference: StorageReference

    var collectionReference: CollectionReference = db.collection("Journal")
    lateinit var img: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_journal_acitivity)

        storageReference = FirebaseStorage.getInstance().getReference()

        auth = Firebase.auth

        binding.apply {
            postProgressBar.visibility = View.INVISIBLE

            if (JournalUser.instance != null) {
                currentUserId = auth.currentUser?.uid.toString()
                currentUserName = auth.currentUser?.displayName.toString()

                postUsernameTextview.text = currentUserName
            }

            postCameraButton.setOnClickListener {
                var i : Intent = Intent(Intent.ACTION_GET_CONTENT)
                i.setType("image/*")
                startActivityForResult(i, 1)
            }

            postSaveJournalButton.setOnClickListener {
                SaveJournal()
            }
        }

    }

    private fun SaveJournal() {
        val title: String = binding.postTitleEt.text.toString().trim()
        val description: String = binding.postDescriptionEt.text.toString().trim()

        binding.postProgressBar.visibility = View.VISIBLE

        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(description) && img != null) {
            // saving the path of images in storage
            // .../journal_images/our_image.png
            val filePath: StorageReference = storageReference
                .child("journal_images")
                .child("my_image_" + Timestamp.now().seconds)

            // uploading the images
            filePath.putFile(img)
                .addOnSuccessListener {
                    filePath.downloadUrl.addOnSuccessListener {
                        val img: Uri = it
                        val timeStamp: Timestamp = Timestamp(Date())

                        // creating object of journal
                        val journal: Journal = Journal(
                            title,
                            description,
                            img.toString(),
                            currentUserId,
                            timeStamp,
                            currentUserName
                        )
                        collectionReference.add(journal)
                            .addOnSuccessListener {
                                binding.postProgressBar.visibility = View.INVISIBLE
                                val i = Intent(this, JournalList::class.java)
                                startActivity(i)
                                finish()
                            }
                    }
                }.addOnFailureListener {
                    binding.postProgressBar.visibility = View.INVISIBLE
                }
        } else {
            binding.postProgressBar.visibility = View.INVISIBLE
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data != null) {
                img = data.data!!
                binding.postImageView.setImageURI(img)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        user = auth.currentUser!!
    }

    override fun onStop() {
        super.onStop()
        if (auth != null) {

        }
    }
}