package com.smartcity.client.ui.interest

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.smartcity.client.BaseApplication
import com.smartcity.client.R
import com.smartcity.client.fragments.interest.InterestNavHostFragment
import com.smartcity.client.models.product.Category
import com.smartcity.client.ui.BaseActivity
import com.smartcity.client.ui.interest.viewmodel.InterestViewModel
import com.smartcity.client.ui.main.MainActivity
import com.smartcity.client.util.PreferenceKeys
import com.smartcity.client.util.StateMessageCallback
import com.smartcity.client.util.SuccessHandling
import kotlinx.android.synthetic.main.activity_auth.fragment_container
import kotlinx.android.synthetic.main.activity_auth.progress_bar
import kotlinx.android.synthetic.main.activity_interest.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject
import javax.inject.Named

@FlowPreview
@ExperimentalCoroutinesApi
class InterestActivity : BaseActivity() {
    @Inject
    @Named("InterestFragmentFactory")
    lateinit var fragmentFactory: FragmentFactory

    @Inject
    lateinit var providerFactory: ViewModelProvider.Factory

    @Inject
    @Named("GetSharedPreferences")
    lateinit var sharedPreferences: SharedPreferences

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
        val interestCenter=sharedPreferences.getStringSet(PreferenceKeys.USER_INTEREST_CENTER, null)
        if(!interestCenter.isNullOrEmpty()){
            navMainActivity()
        }

        viewModel.stateMessage.observe(this, Observer { stateMessage ->//must

            stateMessage?.let {

                if(stateMessage.response.message.equals(SuccessHandling.CREATED_DONE)){//after save interest center navigate to main
                    onResponseReceived(
                        response = it.response,
                        stateMessageCallback = object: StateMessageCallback {
                            override fun removeMessageFromStack() {
                                viewModel.clearStateMessage()
                            }
                        }
                    )
                    navMainActivity()
                }
            }
        })

        viewModel.numActiveJobs.observe(this, Observer { jobCounter ->//must
            displayProgressBar(viewModel.areAnyJobsActive())
        })

        viewModel.viewState.observe(this, Observer{ viewState ->
            Log.d(TAG, "AuthActivity, subscribeObservers: AuthViewState: ${viewState}")
            viewState.categoryFields.userInterestList?.let {
                if(it.isNotEmpty()){
                    saveInterestCenter(sharedPreferences,it)
                    navMainActivity()
                }else{
                    onFinishCheckPreviousAuthUser()
                }
            }
        })
    }

    @SuppressLint("CommitPrefEdits")
    private fun saveInterestCenter(sharedPreferences: SharedPreferences, list: List<Category>) {
        val editor=sharedPreferences.edit()
        editor.putStringSet(PreferenceKeys.USER_INTEREST_CENTER,list.map { it.name }.toMutableSet())
        editor.apply()
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

    override fun updateStatusBarColor(statusBarColor: Int,statusBarTextColor:Boolean) {

    }
}