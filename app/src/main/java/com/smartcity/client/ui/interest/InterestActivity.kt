package com.smartcity.client.ui.interest

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import com.smartcity.client.BaseApplication
import com.smartcity.client.R
import com.smartcity.client.fragments.interest.InterestNavHostFragment
import com.smartcity.client.ui.BaseActivity
import kotlinx.android.synthetic.main.activity_auth.*
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
}