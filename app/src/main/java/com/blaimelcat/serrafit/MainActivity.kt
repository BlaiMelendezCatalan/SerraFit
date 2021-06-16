package com.blaimelcat.serrafit

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.blaimelcat.serrafit.databinding.ActivityMainBinding
import com.blaimelcat.serrafit.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        binding.appBarMain.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_reservations, R.id.nav_log_out,
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Set username and email values for nav header
        val db = FirebaseFirestore.getInstance()
        val bundle:Bundle? = intent.extras
        setNavHeaderInfo(navView, db, bundle)

        //val analytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        //val bundle = Bundle()
        //bundle.putString("message", "Firebase integration complete")
        //analytics.logEvent("InitScreen", bundle)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun setNavHeaderInfo(navView: NavigationView, db: FirebaseFirestore, bundle: Bundle?) {
        // Set username and email values for nav header
        val headerView = navView.getHeaderView(0)
        val usernameMain = headerView.findViewById<TextView>(R.id.username_main)
        val emailMain = headerView.findViewById<TextView>(R.id.email_main)

        val db = FirebaseFirestore.getInstance()

        val bundle:Bundle? = intent.extras
        val bundleEmail:String? = bundle?.getString("email")
        if (bundleEmail != null) {
            db.collection("users").document(
                bundleEmail).get().addOnSuccessListener {
                emailMain.text = bundleEmail
                usernameMain.text = it.get("username") as String?
                emailMain.invalidate()
                usernameMain.invalidate()
            }
        }
    }

    fun logOut(item: MenuItem) {
        FirebaseAuth.getInstance().signOut()
        inflateLoginActivity()
    }

    private fun inflateLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}