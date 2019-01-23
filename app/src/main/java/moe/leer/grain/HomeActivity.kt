package moe.leer.grain

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.browser.customtabs.CustomTabsClient
import androidx.core.app.ActivityOptionsCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.snackbar.Snackbar
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
                val navController = (hostFragment as NavHostFragment).navController
                val dest = navController.currentDestination?.id ?: R.id.profile_dest
                navController.navigate(dest)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun initData() {
    }

    override fun initView() {
        super.initView()
        setSupportActionBar(toolbar)

        val navController = (hostFragment as NavHostFragment).navController
        bottomNav.setupWithNavController(navController)

        toolbar.setOnClickListener {
        }

        toolbar.setOnLongClickListener {
            true
        }
    }
}
