package infinity.to.loop.betteryoutube.main

import android.arch.lifecycle.Observer
import android.content.Intent
import android.content.SharedPreferences
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import dagger.Provides
import dagger.Subcomponent
import dagger.android.AndroidInjector
import dagger.android.support.DaggerAppCompatActivity
import infinity.to.loop.betteryoutube.R
import infinity.to.loop.betteryoutube.common.AuthConfigurationModule
import infinity.to.loop.betteryoutube.databinding.ActivityMainBinding
import infinity.to.loop.betteryoutube.home.HomeActivity
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationService
import javax.inject.Inject
import javax.inject.Named

class MainActivity : DaggerAppCompatActivity() {

    @Inject lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        binding.viewModel = viewModel

        viewModel.authenticated.observe(this, Observer { authenticated ->
            authenticated?.let {
                if (it) {
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                } else {
                    binding.signIn.animate()
                            .alpha(1f)
                            .setStartDelay(1000)
                            .setInterpolator(AccelerateDecelerateInterpolator())
                            .setDuration(500)
                            .start()
                }
            }
        })

        viewModel.loading.observe(this, Observer {
            binding.signIn.visibility = View.GONE
            binding.logo.animate()
                    .scaleXBy(-1f)
                    .scaleYBy(-1f)
                    .setDuration(300)
                    .setInterpolator(AccelerateDecelerateInterpolator())
                    .start()
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        viewModel.onActivityResult(requestCode, resultCode, data)
    }

    @dagger.Module()
    abstract class Module {

        @dagger.Module
        companion object {

            @JvmStatic
            @Provides
            fun viewModel(activity: MainActivity,
                          @Named("clientID") clientID: String,
                          state: AuthState,
                          service: AuthorizationService,
                          sharedPrefs: SharedPreferences) =
                    MainViewModel(activity, clientID, state, service, sharedPrefs)
        }
    }

    @Subcomponent(modules = [Module::class, AuthConfigurationModule::class])
    interface Component : AndroidInjector<MainActivity> {
        @Subcomponent.Builder
        abstract class Builder : AndroidInjector.Builder<MainActivity>()

    }
}