package moe.leer.grain.profile


import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.google.gson.Gson
import moe.leer.grain.*
import moe.leer.grain.model.User


class ProfileFragment : PreferenceFragmentCompat() {

    private lateinit var colorListPreference: ListPreference
    private lateinit var languageListPreference: ListPreference
    private lateinit var reloginPreference: SwitchPreference
    private lateinit var logoutButton: Preference
    private lateinit var librarySummary: Preference
    private lateinit var userInfoPreference: Preference
    private lateinit var authorPreference: Preference
    private lateinit var versionPreference: Preference

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = "settings"
        preferenceManager.sharedPreferencesMode = Context.MODE_PRIVATE

        setPreferencesFromResource(R.xml.profile, null)

        colorListPreference = findPreference("key_transcript") as ListPreference
        languageListPreference = findPreference("key_language") as ListPreference
        reloginPreference = findPreference("key_relogin") as SwitchPreference
        logoutButton = findPreference("key_logout")
        librarySummary = findPreference("key_library")
        userInfoPreference = findPreference("key_info")
        authorPreference = findPreference("key_authors")
        versionPreference = findPreference("key_version")

        if (!App.getApplication(requireContext().applicationContext).isLogin) {
            // change to login
            logoutButton.setIcon(R.drawable.ic_send_24dp)
            logoutButton.setTitle(R.string.text_login)
        }

        logoutButton.setOnPreferenceClickListener {
            if (!App.getApplication(requireContext().applicationContext).isLogin) {
                (requireActivity() as HomeActivity).startLoginActivity()
                return@setOnPreferenceClickListener true
            }
            FuckSchoolApi.getInstance(requireContext()).logout(
                onComplete = {
                    toast(getString(R.string.hint_in_logout))
                    //navigate to Login Activity and clear SP
                    if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                        Handler().postDelayed({
                            App.getApplication(requireContext().applicationContext).isLogin = false
                            requireContext().getSPEdit(Constant.SP_NAME) {
                                putString(Constant.SP_TRANSCRIPT, "")
                                putString(Constant.SP_USER_INFO, "")
                                apply()
                            }
                            (requireActivity() as HomeActivity).startLoginActivity()
                        }, 100)
                    }

                }, onError = {
                    toast(getString(R.string.hint_logout_failed))
                })
            true
        }

        FuckSchoolApi.getInstance(requireContext()).getUserInfo()
            .subscribe(object : NetworkObserver<User>(requireContext()) {
                override fun onNetworkNotAvailable() {
                    toast(R.string.hint_check_network)
                }

                override fun onNext(user: User) {
                    if (user.id == 0 || !lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                        return
                    }
                    requireContext().getSPEdit(Constant.SP_NAME) {
                        putString(Constant.SP_USER_INFO, Gson().toJson(user))
                        apply()
                    }

                    userInfoPreference.title = user.name
                    userInfoPreference.summaryProvider = Preference.SummaryProvider<Preference> {
                        user.className
                    }

                    librarySummary.summaryProvider = Preference.SummaryProvider<Preference> {
                        String.format(
                            resources.getString(R.string.library_summary),
                            user.bookRentNum,
                            user.bookRentOutDataNum
                        )
                    }
                }
            })

        authorPreference.setOnPreferenceClickListener {
            val customTabIntent = CustomTabsIntent.Builder()
                .setStartAnimations(requireContext(), R.anim.slide_in_right, R.anim.slide_out_left)
                .setExitAnimations(requireContext(), R.anim.slide_in_left, R.anim.slide_out_right)
                .setShowTitle(true)
                .setCloseButtonIcon(
                    Util.getBitmapFromVectorDrawable(
                        requireContext(),
                        R.drawable.ic_arrow_back_white_24dp
                    )
                )
                .setToolbarColor(ContextCompat.getColor(requireContext(), R.color.blue))
                .build()
            customTabIntent.launchUrl(requireContext(), Uri.parse("https://github.com/LeeReindeer/Grain"))
//            startActivity(Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://github.com/LeeReindeer/Grain")))
            true
        }

        versionPreference.summaryProvider = Preference.SummaryProvider<Preference> {
            BuildConfig.VERSION_NAME
        }

    }
}
