package moe.leer.grain.profile


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import moe.leer.grain.*
import moe.leer.grain.model.User
import java.util.*


class ProfileFragment : PreferenceFragmentCompat(), Preference.OnPreferenceChangeListener {

    private lateinit var colorListPreference: ListPreference
    private lateinit var languageListPreference: ListPreference
    private lateinit var reloginPreference: SwitchPreference
    private lateinit var logoutButton: Preference
    private lateinit var librarySummary: Preference
    private lateinit var userInfoPreference: Preference
    private lateinit var authorPreference: Preference
    private lateinit var versionPreference: Preference
    private lateinit var resetPreference: Preference
    private lateinit var sharePreference: Preference


    private lateinit var viewModel: ProfileViewModel

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = "settings"
        preferenceManager.sharedPreferencesMode = Context.MODE_PRIVATE

        setPreferencesFromResource(R.xml.profile, null)

        viewModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)

        colorListPreference = findPreference("key_transcript") as ListPreference
        languageListPreference = findPreference("key_language") as ListPreference
        reloginPreference = findPreference("key_relogin") as SwitchPreference
        logoutButton = findPreference("key_logout")
        librarySummary = findPreference("key_library")
        userInfoPreference = findPreference("key_info")
        authorPreference = findPreference("key_authors")
        versionPreference = findPreference("key_version")
        sharePreference = findPreference("key_share")
        resetPreference = findPreference("key_reset")

        languageListPreference.onPreferenceChangeListener = this

        if (!App.getApplication(requireContext().applicationContext).isLogin) {
            // change to login
            logoutButton.setIcon(R.drawable.ic_send_24dp)
            logoutButton.setTitle(R.string.text_login)
        }

        reloginPreference.setOnPreferenceChangeListener { preference, newValue ->
            toast(R.string.toast_in_dev)
            true
        }

        logoutButton.setOnPreferenceClickListener {
            doLogout(R.string.hint_in_logout, R.string.hint_logout_failed, true)
            true
        }

        if ((requireContext().applicationContext as App).isLogin) {

            // show loading hint
            userInfoPreference.title = getString(R.string.hint_loading)
            userInfoPreference.summaryProvider = Preference.SummaryProvider<Preference> {
                getString(R.string.hint_loading)
            }

            librarySummary.summaryProvider = Preference.SummaryProvider<Preference> {
                getString(R.string.hint_loading)
            }

            viewModel.userData().observe(this, Observer<User?> { user ->
                if (user == null || user.id == 0) {
                    return@Observer
                }

                (requireActivity() as HomeActivity).logUserToCrashlytics(user)

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
            })
        }


        authorPreference.summaryProvider = Preference.SummaryProvider<Preference> {
            "Created by LeeR ${Util.getEmojiByUnicode(0x1F491)} Kwok"
        }
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

        sharePreference.setOnPreferenceClickListener {
            var shareIntent = Intent(Intent.ACTION_SEND)
                .putExtra(Intent.EXTRA_TEXT, "推荐应用【海大助手】查成绩，校园卡，图书馆：https://github.com/LeeReindeer/Grain")
                .setType("text/plain")
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            shareIntent = Intent.createChooser(shareIntent, resources.getString(R.string.title_share))
            requireActivity().startActivity(shareIntent)
            true
        }

        resetPreference.setOnPreferenceClickListener {
            viewModel.nukeData()
            doLogout(R.string.hint_in_reset, R.string.hint_reset_failed, false)
            updateLocale(LocaleManager.EN_LANG)
            return@setOnPreferenceClickListener true
        }
    }

    private fun doLogout(@StringRes stringId: Int, @StringRes failedId: Int, jumpToLogin: Boolean) {
        if (!App.getApplication(requireContext().applicationContext).isLogin) {
            if (jumpToLogin) {
                (requireActivity() as HomeActivity).startLoginActivity()
            }
            return
        }
        toast(stringId)

        App.getApplication(requireContext().applicationContext).isLogin = false

        FuckSchoolApi.getInstance(requireContext()).logout(
            onComplete = {
                //navigate to Login Activity and clear SP
                viewModel.deleteAllData()
                if (jumpToLogin) {
                    (requireActivity() as HomeActivity).startLoginActivity()
                }
            }, onError = {
                toast(failedId)
            })
    }

    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
        if (preference?.key == "key_language") {
            when (newValue as String) {
                "0" -> updateLocale(LocaleManager.EN_LANG)
                else -> updateLocale(LocaleManager.ZH_LANG)
            }
            return true
        } else {
            return false
        }
    }

    @Deprecated("for <= SDK 17")
    fun setLocale(lang: String) {
        val myLocale = Locale(lang)
        val res = resources
        val dm = res.displayMetrics
        val conf = res.configuration
        conf.locale = myLocale
        res.updateConfiguration(conf, dm)
        val refresh = Intent(this.requireActivity(), HomeActivity::class.java)
        requireActivity().finish()
        startActivity(refresh)
    }

    fun updateLocale(lang: String) {
        val context = requireContext().applicationContext
        (context as App).localeManager.updateLocale(requireContext(), lang)
        startActivity(
            Intent(this.requireActivity(), HomeActivity::class.java).addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            )
        )

    }

}
