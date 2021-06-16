package com.vs.meetly

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import com.bumptech.glide.Glide
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.auth.FirebaseAuth
import com.vs.meetly.daos.UserDao
import com.vs.meetly.modals.User
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.header_layout.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {



    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle

    private lateinit var auth : FirebaseAuth

    private lateinit var user : User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        hideDefaultUI()
        setUpViews()

        auth = FirebaseAuth.getInstance()
        testProgress.visibility = View.VISIBLE

            GlobalScope.launch(Dispatchers.IO) {
                val currentUserId = auth.currentUser!!.uid
                val userDao = UserDao()
                user = userDao.getUserById(currentUserId).await().toObject(User::class.java)!!
                withContext(Dispatchers.Main){
                    header_title.text = user.displayName
                    user_id.text  = user.uid
                    loadImage(user.imageUrl)
                }
            }

        displayCalender.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker().build()
            datePicker.show(supportFragmentManager, "DatePicker")
            datePicker.addOnPositiveButtonClickListener {
                Toast.makeText(this, "+ ${datePicker.headerText}", Toast.LENGTH_SHORT).show()
            }
            datePicker.addOnNegativeButtonClickListener {
                Toast.makeText(this, "-", Toast.LENGTH_SHORT).show()
            }
            datePicker.addOnCancelListener {
                Toast.makeText(this, "<- Back", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadImage(imageUrl: String) {
        Glide.with(this).load(imageUrl).into(header_image)
        testProgress.visibility = View.GONE
    }

    private fun setUpViews() {
        setUpDrawerLayout()
    }

    private fun setUpDrawerLayout() {
        setSupportActionBar(appBar)
        actionBarDrawerToggle = ActionBarDrawerToggle(this, mainDrawer, R.string.app_name, R.string.app_name)
        actionBarDrawerToggle.syncState()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    private fun hideDefaultUI(){
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN)
    }
}