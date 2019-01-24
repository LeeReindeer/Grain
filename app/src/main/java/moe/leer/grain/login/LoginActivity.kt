package moe.leer.grain.login

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_login.*
import moe.leer.grain.App
import moe.leer.grain.Constant.SP_LASTID
import moe.leer.grain.Constant.SP_NAME
import moe.leer.grain.FuckSchoolApi
import moe.leer.grain.R
import moe.leer.grain.Util
import moe.leer.grain.base.BaseActivity
import moe.leer.grain.getSP
import moe.leer.grain.getSPEdit
import moe.leer.grain.toast


class LoginActivity : BaseActivity() {

    private lateinit var loginViewModel: LoginViewModel
    private var loging = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initData()
        initView()
    }

    override fun onDestroy() {
        super.onDestroy()
        loginBtn.dispose()
    }

    override fun initData() {
        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        // restore last try login id
        usernameEdit.setText(this.getSP(SP_NAME).getString(SP_LASTID, ""))
    }

    override fun initView() {
        super.initView()
        // show keyboard at password edittext
        if (usernameEdit.text!!.isNotEmpty()) {
            Util.showKeyboard(this, passwordEdit)
        }

        val handler = Handler()
        loginViewModel.getLoginStatus()
            .observe(this, Observer { status ->
                Log.d(TAG, "statusChange: $status")
                loging = false
                toggleInteraction(true)
                when (status) {
                    FuckSchoolApi.LOGIN_PROCESS -> {
                    }

                    FuckSchoolApi.LOGIN_ID_PASS_ERROR -> {
                        loginBtn.revertAnimation {
                            loginBtn.setBackgroundResource(R.drawable.circular_button)
                        }
                        passwordInputLayout.error = "ID or password error"
                        Util.showKeyboard(this, passwordEdit)
                    }

                    FuckSchoolApi.LOGIN_NET_ERROR -> {
                        handler.postDelayed({
                            loginBtn.revertAnimation {
                                loginBtn.setBackgroundResource(R.drawable.circular_button)
                            }
                            toast(R.string.hint_check_network)
                        }, 600)
                    }

                    FuckSchoolApi.LOGIN_SUCCESS -> {
                        loginBtn.doneLoadingAnimation(
                            ContextCompat.getColor(this, R.color.green),
                            Util.getBitmapFromVectorDrawable(this, R.drawable.ic_check_white_24dp)
                        )
                        (application as App).isLogin = true

                        handler.postDelayed({
                            setResult(Activity.RESULT_OK)
                            finishAfterTransition()
                        }, 500)
                    }
                }
            })

        loginBtn.setOnClickListener {
            usernameInputLayout.error = ""
            passwordInputLayout.error = ""

            if (inputValid()) {
                loginBtn.startAnimation()

                this.getSPEdit(SP_NAME) {
                    putString(SP_LASTID, usernameEdit.text.toString())
                    apply()
                }
                loging = true
                toggleInteraction(false)
                loginViewModel.doLogin(usernameEdit.text.toString().toInt(), passwordEdit.text.toString())
            }
        }
        //  If enabled, a button is displayed to toggle between the password being displayed as plain-text or disguised, when your EditText is set to display a password.
        passwordInputLayout.isPasswordVisibilityToggleEnabled = true

        // just for joke
        textPrivacyPolicy.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(this)
                .setTitle(getString(R.string.privacy_policy))
                .setPositiveButton("Ok") { dialog, id ->
                }
                .setMessage(getString(R.string.hint_privacy_policy) + Util.getEmojiByUnicode(0x1F61B))
                .create()
                .show()
        }
    }

    override fun onBackPressed() {
        if (loging) {
            loginBtn.dispose()
            loginViewModel.cancel()
        } else {
            super.onBackPressed()
        }
    }

    private fun inputValid(): Boolean {
        val idInput = usernameEdit.text.toString()
        val passwordInput = passwordEdit.text.toString()
        if (idInput.isEmpty()) {
            usernameInputLayout.error = "Fill ID field"
            Util.showKeyboard(this, usernameEdit)
            return false
        }

        if (passwordInput.isEmpty()) {
            passwordInputLayout.error = "Fill password field"
            Util.showKeyboard(this, passwordEdit)
            return false
        }
        try {
            idInput.toInt()
        } catch (e: NumberFormatException) {
            usernameInputLayout.error = "ID must be digit"
            Util.showKeyboard(this, usernameEdit)
            return false
        }
        if (idInput.length != 9) {
            usernameInputLayout.error = "ID must be 9 digits"
            Util.showKeyboard(this, usernameEdit)
            return false
        }
        return true
    }
}
