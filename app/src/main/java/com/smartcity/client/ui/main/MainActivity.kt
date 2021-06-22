package com.smartcity.client.ui.main

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import com.smartcity.client.BaseApplication
import com.smartcity.client.R
import com.smartcity.client.models.AUTH_TOKEN_BUNDLE_KEY
import com.smartcity.client.models.AuthToken
import com.smartcity.client.ui.BaseActivity
import com.smartcity.client.ui.auth.AuthActivity
import com.smartcity.client.ui.main.account.BaseAccountFragment
import com.smartcity.client.ui.main.blog.*

import com.smartcity.client.util.BOTTOM_NAV_BACKSTACK_KEY
import com.smartcity.client.util.BottomNavController
import com.smartcity.client.util.BottomNavController.*
import com.smartcity.client.util.setUpNavigation
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.messaging.FirebaseMessaging
import com.smartcity.client.ui.main.cart.BaseCartFragment
import com.smartcity.client.util.PreferenceKeys

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.progress_bar
import javax.inject.Inject
import javax.inject.Named

class MainActivity : BaseActivity(),
    OnNavigationGraphChanged,
    OnNavigationReselectedListener
{

    @Inject
    @Named("AccountFragmentFactory")
    lateinit var accountFragmentFactory: FragmentFactory

    @Inject
    @Named("BlogFragmentFactory")
    lateinit var blogFragmentFactory: FragmentFactory

    @Inject
    @Named("CartFragmentFactory")
    lateinit var cartFragmentFactory: FragmentFactory

    @Inject
    @Named("GetSharedPreferences")
    lateinit var sharedPreferences: SharedPreferences

    private lateinit var bottomNavigationView: BottomNavigationView

    private val bottomNavController by lazy(LazyThreadSafetyMode.NONE) {
        BottomNavController(
            this,
            R.id.main_fragments_container,
            R.id.menu_nav_blog,
            this)
    }

    override fun onGraphChange() {
        cancelActiveJobs()
        expandAppBar()
    }

    private fun cancelActiveJobs(){
        val fragments = bottomNavController.fragmentManager
            .findFragmentById(bottomNavController.containerId)
            ?.childFragmentManager
            ?.fragments
        if(fragments != null){
            for(fragment in fragments){
                if(fragment is BaseAccountFragment){
                    fragment.cancelActiveJobs()
                }
                if(fragment is BaseBlogFragment){
                    fragment.cancelActiveJobs()
                }
                if(fragment is BaseCartFragment){
                    fragment.cancelActiveJobs()
                }
            }
        }
        displayProgressBar(false)
    }

    override fun onReselectNavItem(
        navController: NavController,
        fragment: Fragment
    ){
        Log.d(TAG, "logInfo: onReSelectItem")
        when(fragment){

           /* is ViewBlogFragment -> {
                navController.navigate(R.id.action_viewBlogFragment_to_home)
            }

            is UpdateBlogFragment -> {
                navController.navigate(R.id.action_updateBlogFragment_to_home)
            }*/



            else -> {
                // do nothing
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when(item?.itemId){
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun inject() {
        (application as BaseApplication).mainComponent()
            .inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        inject()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupActionBar()
        setupBottomNavigationView(savedInstanceState)

        subscribeObservers()
        restoreSession(savedInstanceState)

        //todo subscribe only once- with token
        subscribeNotificationTopic(sharedPreferences)

        //todo use fcm token to identify user

        /* FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
             if (!task.isSuccessful) {
                 Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                 return@OnCompleteListener
             }
             val token = task.result
             Log.d("tokenid", "${token}")
         })*/
    }

    private fun subscribeNotificationTopic(sharedPreferences: SharedPreferences) {
        val previousAuthUserEmail: String? = sharedPreferences.getString(PreferenceKeys.PREVIOUS_AUTH_USER, null)
        previousAuthUserEmail?.let {
            val topic="user-"+it.replace("@","")
            Log.d(TAG, topic)
            FirebaseMessaging.getInstance().subscribeToTopic(topic)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful)
                        Log.d(TAG, "Email topic subscription successful")
                    else
                        Log.e(TAG, "Email topic subscription failed. Error: " + task.exception?.localizedMessage)
                }
        }

        val interestCenter=sharedPreferences.getStringSet(PreferenceKeys.USER_INTEREST_CENTER, null)
        interestCenter?.let {
            it.map {topic ->
                FirebaseMessaging.getInstance().subscribeToTopic(topic)
            }
        }
    }

    override fun displayRetryView() {
        //TODO
    }

    override fun displayFragmentContainerView() {
        //TODO
    }

    private fun setupBottomNavigationView(savedInstanceState: Bundle?){
        bottomNavigationView = findViewById(R.id.bottom_navigation_view)
        bottomNavigationView.setUpNavigation(bottomNavController, this)
        if (savedInstanceState == null) {
            bottomNavController.setupBottomNavigationBackStack(null)
            bottomNavController.onNavigationItemSelected()
        }
        else{
            (savedInstanceState[BOTTOM_NAV_BACKSTACK_KEY] as IntArray?)?.let { items ->
                val backstack = BackStack()
                backstack.addAll(items.toTypedArray())
                bottomNavController.setupBottomNavigationBackStack(backstack)
            }
        }
    }

    private fun restoreSession(savedInstanceState: Bundle?){
        savedInstanceState?.get(AUTH_TOKEN_BUNDLE_KEY)?.let{ authToken ->
            sessionManager.setValue(authToken as AuthToken)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        // save auth token
        outState.putParcelable(AUTH_TOKEN_BUNDLE_KEY, sessionManager.cachedToken.value)

        // save backstack for bottom nav
        outState.putIntArray(BOTTOM_NAV_BACKSTACK_KEY, bottomNavController.navigationBackStack.toIntArray())
    }

    fun subscribeObservers(){
        sessionManager.cachedToken.observe(this, Observer{ authToken ->
            Log.d(TAG, "MainActivity, subscribeObservers: ViewState: ${authToken}")
            if(authToken == null || authToken.account_pk == -1 || authToken.token == null){
                navAuthActivity()
                finish()
            }
        })
    }

    override fun expandAppBar() {
        findViewById<AppBarLayout>(R.id.app_bar).setExpanded(true)
    }

    override fun displayBottomNavigation(bool: Boolean) {
        if(bool){
            bottom_navigation_view.visibility = View.VISIBLE
        }
        else{
            bottom_navigation_view.visibility = View.GONE
        }
    }

    override fun onBackPressed() = bottomNavController.onBackPressed()

    private fun setupActionBar(){
        setSupportActionBar(tool_bar)
    }

    private fun navAuthActivity(){
        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
        finish()
        (application as BaseApplication).releaseMainComponent()
    }

    override fun displayProgressBar(bool: Boolean){
        if(bool){
            progress_bar.visibility = View.VISIBLE
        }
        else{
            progress_bar.visibility = View.GONE
        }
    }

}
