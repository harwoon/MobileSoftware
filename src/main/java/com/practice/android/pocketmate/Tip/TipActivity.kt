package com.practice.android.pocketmate.Tip

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.practice.android.pocketmate.Adapter.CommentAdapter
import com.practice.android.pocketmate.Model.BoardModel
import com.practice.android.pocketmate.Model.CommentModel
import com.practice.android.pocketmate.R
import com.practice.android.pocketmate.databinding.ActivityTipBinding
import com.practice.android.pocketmate.util.FBAuth
import com.practice.android.pocketmate.util.FBAuth.Companion.getNickname
import com.practice.android.pocketmate.util.FBRef

class TipActivity : AppCompatActivity() {

    lateinit var binding : ActivityTipBinding
    val commentList = mutableListOf<CommentModel>()
    var agreed = false
    var disagreed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTipBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val key = intent.getStringExtra("key").toString()

        getTipData(key)
        getCommentData(key)
        handleBtns(key)

        binding.commentArea.adapter = CommentAdapter(commentList)
        binding.commentArea.layoutManager = LinearLayoutManager(this)
    }

    private fun getTipData(key : String) {
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val tip = dataSnapshot.getValue(BoardModel::class.java)!!
                binding.title.text = tip.title
                binding.date.text = tip.date
                binding.content.text = tip.content
                binding.content.setTextColor(tip.color)
                getNickname(FBAuth.getUid()) { nickname -> binding.nickname.text = nickname }
                binding.agreeNumber.text = tip.agree.toString()
                binding.disagreeNumber.text = tip.disagree.toString()
                
                //프로필 이미지

                if (tip.image == 0) {
                    binding.image.visibility = View.GONE
                }
                if (tip.uid != FBAuth.getUid()) {
                    binding.editBtn.visibility = View.GONE
                    binding.deleteBtn.visibility = View.GONE
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
            }
        }
        FBRef.tipRef.child(key).addValueEventListener(postListener)
    }

    private fun handleBtns(key: String) {
        binding.writeCommentBtn.setOnClickListener {
            writeComment(key)
            binding.writeCommentArea.text.clear()
        }
        binding.editBtn.setOnClickListener {
            showEditDialog(key)
        }
        binding.deleteBtn.setOnClickListener {
            showDeleteDialog(key)
        }
        binding.agreeBtn.setOnClickListener {
            if (agreed) {
                reduceAgree(key)
            }
            else {
                raiseAgree(key)
            }
            agreed = !agreed
        }
        binding.disagreeBtn.setOnClickListener {
            if (disagreed) {
                reduceDisagree(key)
            }
            else{
                raiseDisagree(key)
            }
            disagreed = !disagreed
        }
    }

    private fun showEditDialog(key: String) {
        MaterialAlertDialogBuilder(this)
            .setMessage("수정하시겠습니까?")
            .setNegativeButton("취소") { dialog, which ->
                //do nothing.
            }
            .setPositiveButton("수정") { dialog, which ->
                val intent = Intent(this, EditTipActivity::class.java)
                intent.putExtra("key", key)
                startActivity(intent)
                finish()
            }
            .show()
    }

    private fun showDeleteDialog(key: String) {
        MaterialAlertDialogBuilder(this)
            .setMessage("삭제하시겠습니까?")
            .setNegativeButton("취소") { dialog, which ->
                //do nothing.
            }
            .setPositiveButton("삭제") { dialog, which ->
                switchScreen(this, TipBoardActivity::class.java)
                FBRef.tipRef.child(key).removeValue()
                finish()
            }
            .show()
    }

    private fun raiseAgree(key: String) {
        val agree = binding.agreeNumber.text.toString().toInt() + 1
        val childUpdates = hashMapOf<String, Any>(
                "/TipBoard/$key/agree" to agree,
            )
        val databaseRef = Firebase.database.reference
        databaseRef.updateChildren(childUpdates).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                binding.agreeNumber.text = agree.toString()
            } else {
                Log.e("TipActivity", "Failed to update agree count.")
            }
        }
    }

    private fun reduceAgree(key: String) {
        val agree = binding.agreeNumber.text.toString().toInt() - 1
        val childUpdates = hashMapOf<String, Any>(
            "/TipBoard/$key/agree" to agree,
        )
        val databaseRef = Firebase.database.reference
        databaseRef.updateChildren(childUpdates).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                binding.agreeNumber.text = agree.toString()
            } else {
                Log.e("TipActivity", "Failed to update agree count.")
            }
        }
    }

    private fun raiseDisagree(key: String) {
        val disagree = binding.disagreeNumber.text.toString().toInt() + 1
        val childUpdates = hashMapOf<String, Any>(
            "/TipBoard/$key/disagree" to disagree,
        )
        val databaseRef = Firebase.database.reference
        databaseRef.updateChildren(childUpdates).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                binding.disagreeNumber.text = disagree.toString()
            } else {
                Log.e("TipActivity", "Failed to update disagree count.")
            }
        }
    }

    private fun reduceDisagree(key: String) {
        val disagree = binding.disagreeNumber.text.toString().toInt() - 1
        val childUpdates = hashMapOf<String, Any>(
            "/TipBoard/$key/disagree" to disagree,
        )
        val databaseRef = Firebase.database.reference
        databaseRef.updateChildren(childUpdates).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                binding.disagreeNumber.text = disagree.toString()
            } else {
                Log.e("TipActivity", "Failed to update disagree count.")
            }
        }
    }

    private fun getCommentData(key: String) {
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                commentList.clear()

                for (data in dataSnapshot.children) {
                    val comment = data.getValue(CommentModel::class.java)!!
                    binding.emptyCommentText.visibility = View.GONE
                    getNickname(FBAuth.getUid()) { nickname ->
                        binding.nickname.text = nickname
                    }
                    commentList.add(comment!!)
                }
                binding.commentArea.adapter?.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
            }
        }
        FBRef.commentRef.child(key).addValueEventListener(postListener)
    }

    private fun writeComment(key: String) {
        val commentKey = FBRef.commentRef.child(key).push().key.toString()
        val commentContent = binding.writeCommentArea.text.toString().trim()
        val comment = CommentModel(0, FBAuth.getUid(), commentContent)
        FBRef.commentRef.child(key).child(commentKey).setValue(comment)
    }

    private fun switchScreen(from: AppCompatActivity, to: Class<out AppCompatActivity>) {
        val intent = Intent(from, to)
        from.startActivity(intent)
    }
}