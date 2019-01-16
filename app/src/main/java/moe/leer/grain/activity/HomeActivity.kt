package moe.leer.grain.activity

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import kotlinx.android.synthetic.main.activity_home.*
import moe.leer.grain.App
import moe.leer.grain.FuckSchoolApi
import moe.leer.grain.R
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

        initView()
//        initData()
    }

    override fun initData() {
        FuckSchoolApi.getInstance(applicationContext).loginAsync(User(160410218, "01282038"), object : Callback {
            override fun onFailure(call: Call, e: IOException) {
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    toast(if (response.isSuccessful()) "login successful" else "login failed")
                    if (response.isSuccessful) {
                        App.getApplication(applicationContext).isLogin = true
                    }
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
            initData()
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
