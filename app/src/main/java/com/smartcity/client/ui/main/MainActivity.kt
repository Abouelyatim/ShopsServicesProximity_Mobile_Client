package com.smartcity.client.ui.main

import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.messaging.FirebaseMessaging
import com.smartcity.client.BaseApplication
import com.smartcity.client.R
import com.smartcity.client.models.AUTH_TOKEN_BUNDLE_KEY
import com.smartcity.client.models.AuthToken
import com.smartcity.client.ui.BaseActivity
import com.smartcity.client.ui.auth.AuthActivity
import com.smartcity.client.ui.main.account.orders.notification.MyFirebaseMessagingService
import com.smartcity.client.util.BOTTOM_NAV_BACKSTACK_KEY
import com.smartcity.client.util.BottomNavController
import com.smartcity.client.util.BottomNavController.*
import com.smartcity.client.util.PreferenceKeys
import com.smartcity.client.util.setUpNavigation
import kotlinx.android.synthetic.main.activity_main.*
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
    @Named("ProductFragmentFactory")
    lateinit var productFragmentFactory: FragmentFactory

    @Inject
    @Named("CartFragmentFactory")
    lateinit var cartFragmentFactory: FragmentFactory

    @Inject
    @Named("FlashFragmentFactory")
    lateinit var flashFragmentFactory: FragmentFactory

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
        expandAppBar()
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

        setFlashBadge(sharedPreferences)
    }

    private fun setFlashBadge(sharedPreferences: SharedPreferences) {
        MyFirebaseMessagingService.Events.flashBadgeEvent.observe(this, Observer<Boolean> { profile ->
            val newFlashNotification = sharedPreferences.getBoolean(PreferenceKeys.NEW_FLASH_NOTIFICATION, false)
            displayBadgeBottomNavigationFlash(newFlashNotification)
        })
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

                FirebaseMessaging.getInstance().subscribeToTopic(topic).addOnCompleteListener { task ->
                    if (task.isSuccessful)
                        Log.d(TAG, "Subscription successful to "+topic)
                    else
                        Log.e(TAG, "Subscription failed. Error: " + task.exception?.localizedMessage)
                }
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

    override fun displayBadgeBottomNavigationFlash(bool: Boolean) {
        if(bool){
            val badge =bottom_navigation_view.getOrCreateBadge(R.id.menu_nav_flash_notification)
            badge?.let {
                badge.isVisible = true
                //badge.number=99
            }
        }
        else{
            val badgeDrawable =bottom_navigation_view.getBadge(R.id.menu_nav_flash_notification)
            badgeDrawable?.let {
                badgeDrawable.isVisible = false
                badgeDrawable.clearNumber()
            }
        }
    }

    override fun displayBottomNavigation(bool: Boolean) {
        if(bool){
            bottom_navigation_view.visibility = View.VISIBLE
        }
        else{
            bottom_navigation_view.visibility = View.GONE
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun updateStatusBarColor(statusBarColor: Int,statusBarTextColor:Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            ContextCompat.getColor(this, statusBarColor)
            window.statusBarColor = ContextCompat.getColor(this, statusBarColor)
            if(statusBarTextColor){
                window.decorView.systemUiVisibility = window.decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            }else{
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
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
