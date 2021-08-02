package com.smartcity.client.ui.auth

import android.content.Intent
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
import com.smartcity.client.fragments.auth.AuthNavHostFragment
import com.smartcity.client.ui.BaseActivity
import com.smartcity.client.ui.auth.state.AuthStateEvent
import com.smartcity.client.ui.interest.InterestActivity
import com.smartcity.client.util.StateMessageCallback
import com.smartcity.client.util.SuccessHandling.Companion.RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE
import kotlinx.android.synthetic.main.activity_auth.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject
import javax.inject.Named

@FlowPreview
@ExperimentalCoroutinesApi
class AuthActivity : BaseActivity()
{

    @Inject
    @Named("AuthFragmentFactory")
    lateinit var fragmentFactory: FragmentFactory

    @Inject
    lateinit var providerFactory: ViewModelProvider.Factory

    val viewModel: AuthViewModel by viewModels {
        providerFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        inject()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        subscribeObservers()
        onRestoreInstanceState()
    }

    fun onRestoreInstanceState(){
        val host = supportFragmentManager.findFragmentById(R.id.auth_fragments_container)
        host?.let {
            // do nothing
        } ?: createNavHost()
    }

    private fun createNavHost(){
        val navHost = AuthNavHostFragment.create(
            R.navigation.auth_nav_graph
        )
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.auth_fragments_container,
                navHost,
                getString(R.string.AuthNavHost)
            )
            .setPrimaryNavigationFragment(navHost)
            .commit()
    }

    override fun onResume() {
        super.onResume()
        checkPreviousAuthUser()
    }

    private fun subscribeObservers(){
        viewModel.stateMessage.observe(this, Observer { stateMessage ->//must

            stateMessage?.let {

                if(stateMessage.response.message.equals(RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE)){
                    onFinishCheckPreviousAuthUser()
                }

                onResponseReceived(
                    response = it.response,
                    stateMessageCallback = object: StateMessageCallback {
                        override fun removeMessageFromStack() {
                            viewModel.clearStateMessage()
                        }
                    }
                )
            }
        })

        viewModel.numActiveJobs.observe(this, Observer { jobCounter ->//must
            displayProgressBar(viewModel.areAnyJobsActive())
        })

        viewModel.viewState.observe(this, Observer{ viewState ->
            Log.d(TAG, "AuthActivity, subscribeObservers: AuthViewState: ${viewState}")
            viewState.authToken?.let{
                sessionManager.login(it)
            }
        })

        sessionManager.cachedToken.observe(this, Observer{ token ->
            Log.d(TAG, "AuthActivity, subscribeObservers: AuthToken: ${token}")
            token.let{ authToken ->
                if(authToken != null && authToken.account_pk != -1 && authToken.token != null){
                    navInterestActivity()
                }
            }
        })
    }

    fun navInterestActivity(){
        Log.d(TAG, "navInterestActivity: called.")
        val intent = Intent(this, InterestActivity::class.java)
        startActivity(intent)
        finish()
        (application as BaseApplication).releaseAuthComponent()
    }



    private fun checkPreviousAuthUser(){
        viewModel.setStateEvent(AuthStateEvent.CheckPreviousAuthEvent())
    }

    private fun onFinishCheckPreviousAuthUser(){
        fragment_container.visibility = View.VISIBLE
    }

    override fun inject() {
        (application as BaseApplication).authComponent()
            .inject(this)
    }

    override fun displayProgressBar(bool: Boolean){
        if(bool){
            progress_bar.visibility = View.VISIBLE
        }
        else{
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

    override fun displayRetryView() {
        
    }

    override fun displayFragmentContainerView() {
        
    }

    override fun displayAppBar(bool: Boolean) {
        
    }

    override fun setAppBarLayout(fragment: Fragment) {
        
    }

    override fun updateStatusBarColor(statusBarColor: Int, statusBarTextColor: Boolean) {
        
    }
}

















