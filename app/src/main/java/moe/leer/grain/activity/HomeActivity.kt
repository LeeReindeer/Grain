package moe.leer.grain.activity

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.core.app.ActivityOptionsCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_home.*
import moe.leer.grain.App
import moe.leer.grain.Constant
import moe.leer.grain.FuckSchoolApi
import moe.leer.grain.NetworkObserver
import moe.leer.grain.R
import moe.leer.grain.getSPEdit
import moe.leer.grain.login.LoginActivity
import moe.leer.grain.model.User
import moe.leer.grain.toast
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

class HomeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        initData()
        initView()

    }

    override fun onResume() {
        super.onResume()
        if (!(this.application as App).isLogin) {
            Snackbar.make(constraintLayout, "请先登录", Snackbar.LENGTH_LONG)
                .setAction(R.string.text_login) { view ->
                    val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, logo, "logo")
                    startActivity<LoginActivity>(options.toBundle())
                }
                .show()
        }
    }

    override fun initData() {
        // get user info on app start, can be refreshed in user profile page
        //  save info to SP. So first load data from SP, meantime, fetch fresh data on the background
        FuckSchoolApi.getInstance(this).getUserInfo()
            .subscribe(object : NetworkObserver<User>(this.applicationContext) {
                override fun onNetworkNotAvailable() {
                }

                override fun onNext(user: User) {
                    this@HomeActivity.getSPEdit(Constant.SP_NAME) {
                        putString(Constant.SP_USER_INFO, Gson().toJson(user))
                        apply()
                    }
                }

            })
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_logout -> {
                FuckSchoolApi.getInstance(this).logoutAsync(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                    }

                    override fun onResponse(call: Call, response: Response) {
                        runOnUiThread {
                            Log.d(TAG, "onResponse: ${response.code()}")
                            toast(if (response.isSuccessful) "logout" else "logout failed")
                            if (response.isSuccessful) {
                                App.getApplication(applicationContext).isLogin = false
                            }
                        }
                    }
                })
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}
