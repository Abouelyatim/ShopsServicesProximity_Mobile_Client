package com.smartcity.client.ui.interest

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.smartcity.client.BaseApplication
import com.smartcity.client.R
import com.smartcity.client.fragments.interest.InterestNavHostFragment
import com.smartcity.client.ui.BaseActivity
import com.smartcity.client.ui.main.MainActivity
import com.smartcity.client.util.ErrorHandling
import com.smartcity.client.util.SuccessHandling
import kotlinx.android.synthetic.main.activity_auth.*
import kotlinx.android.synthetic.main.activity_auth.fragment_container
import kotlinx.android.synthetic.main.activity_auth.progress_bar
import kotlinx.android.synthetic.main.activity_interest.*
import javax.inject.Inject
import javax.inject.Named

class InterestActivity : BaseActivity() {
    @Inject
    @Named("InterestFragmentFactory")
    lateinit var fragmentFactory: FragmentFactory

    @Inject
    lateinit var providerFactory: ViewModelProvider.Factory

    val viewModel: InterestViewModel by viewModels {
        providerFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        inject()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interest)
        onRestoreInstanceState()
        subscribeObservers()
    }

    override fun displayRetryView() {
        retry_button.visibility=View.VISIBLE
        retry_button.setOnClickListener {
            handelNetworkError.resendNetworkRequest()
            retry_button.visibility=View.GONE
        }
    }

    override fun displayFragmentContainerView() {
        onFinishCheckPreviousAuthUser()
    }


    private fun subscribeObservers() {
        viewModel.dataState.observe(this, Observer { dataState ->
            onDataStateChange(dataState)

            dataState.data?.let { data ->
                data.response?.let{event ->
                    //after save interest center navigate to main
                    event.peekContent().let{ response ->
                        response.message?.let{ message ->
                            if(message.equals(SuccessHandling.CREATED_DONE)){
                                navMainActivity()
                            }

                        }
                    }
                    //check if user set interest center
                    event.peekContent().let{ response ->
                        response.message?.let{ message ->
                            if(message.equals(SuccessHandling.DONE_User_Interest_Center)){
                                data.data?.let {
                                    if(it.peekContent().categoryFields.categoryList.isNotEmpty()){
                                        navMainActivity()
                                    }else{
                                        onFinishCheckPreviousAuthUser()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        })

        //check previous set of interest center
        sessionManager.cachedToken.observe(this, Observer{ dataState ->
            Log.d(TAG, "AuthActivity, subscribeObservers: AuthDataState: ${dataState}")
            dataState.let{ authToken ->
                authToken?.let {
                    it.interest?.let {interest->
                        if(interest){
                            navMainActivity()
                        }
                    }
                }

            }
        })
    }
    private fun onFinishCheckPreviousAuthUser(){
        fragment_container.visibility = View.VISIBLE
    }

    fun navMainActivity(){
        Log.d(TAG, "navMainActivity: called.")
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
        (application as BaseApplication).releaseInterestComponent()
    }

    fun onRestoreInstanceState() {
        val host = supportFragmentManager.findFragmentById(R.id.interest_fragments_container)
        host?.let {
            // do nothing
        } ?: createNavHost()
    }

    private fun createNavHost() {
        val navHost = InterestNavHostFragment.create(
            R.navigation.interest_nav_graph
        )
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.interest_fragments_container,
                navHost,
                getString(R.string.InterestNavHost)
            )
            .setPrimaryNavigationFragment(navHost)
            .commit()
    }

    override fun inject() {
        (application as BaseApplication).interestComponent()
            .inject(this)
    }

    override fun displayProgressBar(bool: Boolean) {
        if (bool) {
            progress_bar.visibility = View.VISIBLE
        } else {
            progress_bar.visibility = View.GONE
        }
    }

    override fun expandAppBar() {
        // ignore
    }

    override fun displayBadgeBottomNavigationFlash(bool: Boolean) {

    }

    override fun displayBottomNavigation(bool: Boolean) {

    }
}