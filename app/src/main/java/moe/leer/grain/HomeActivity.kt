package moe.leer.grain

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.browser.customtabs.CustomTabsClient
import androidx.core.app.ActivityOptionsCompat
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.crashlytics.android.Crashlytics
import com.google.android.material.snackbar.Snackbar
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.activity_home.*
import moe.leer.grain.base.BaseActivity
import moe.leer.grain.login.LoginActivity

class HomeActivity : BaseActivity() {

    private val LOGIN_RESULT = 9

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        initData()
        initView()

    }

    override fun onStart() {
        super.onStart()
        // warm up chrome
        CustomTabsClient.connectAndInitialize(this, "com.android.chrome")
    }

    override fun onResume() {
        super.onResume()
        if (!(this.application as App).isLogin) {
            Snackbar.make(constraintLayout, getString(R.string.hint_login), Snackbar.LENGTH_LONG)
                .setAction(R.string.text_login) { view ->
                    startLoginActivity()
                }
                .show()
        }
    }

    fun startLoginActivity() {
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, logo, "logo")
//        startActivity<LoginActivity>(options.toBundle())
        startActivityForResult<LoginActivity>(options.toBundle(), LOGIN_RESULT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d(TAG, "onActivityResult: requestCode: $requestCode , resultCode: $resultCode")
        if (requestCode == LOGIN_RESULT) {
            if (resultCode == Activity.RESULT_OK) {
                Log.d(TAG, "onActivityResult: login successful")
            }
            //refresh current fragment
            val navController = (hostFragment as NavHostFragment).navController
            val startFragmentId = when (getSP(Constant.SP_SETTING_NAME).getString("key_start_page", "2")) {
                "0" -> R.id.transcript_dest
                "1" -> R.id.card_dest
                else -> R.id.profile_dest
            }
            val dest = navController.currentDestination?.id ?: startFragmentId
            navController.navigate(dest, null, NavOptions.Builder().setPopUpTo(startFragmentId, true).build())
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun initData() {
        Fabric.with(this, Crashlytics())
    }

    override fun initView() {
        super.initView()
        setSupportActionBar(toolbar)

        val navController = (hostFragment as NavHostFragment).navController
        bottomNav.setupWithNavController(navController)

        Log.d(TAG, "startPage: ${getSP(Constant.SP_SETTING_NAME).getString("key_start_page", "2")}")
        when (getSP(Constant.SP_SETTING_NAME).getString("key_start_page", "2")) {
            "0" -> navController.navigate(R.id.action_profile_to_transcript)
            "1" -> navController.navigate(R.id.action_profile_to_card)
            else -> {
            }
        }

        //https://stackoverflow.com/questions/51929290/is-it-possible-to-set-startdestination-conditionally-using-android-navigation-ar
//        val graph = navController.navInflater.inflate(R.navigation.navigation)
//        when (getSP(Constant.SP_SETTING_NAME).getString("key_start_page", "2")) {
//            "0" -> graph.startDestination = R.id.transcript_dest
//            "1" -> graph.startDestination = R.id.card_dest
//            else -> graph.startDestination = R.id.profile_dest
//        }
//        navController.graph = graph

        toolbar.setOnClickListener {
        }

        toolbar.setOnLongClickListener {
            true
        }
    }
}
