package moe.leer.grain.profile


import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import moe.leer.grain.App
import moe.leer.grain.BuildConfig
import moe.leer.grain.FuckSchoolApi
import moe.leer.grain.HomeActivity
import moe.leer.grain.R
import moe.leer.grain.Util
import moe.leer.grain.model.User
import moe.leer.grain.toast


class ProfileFragment : PreferenceFragmentCompat() {

    private lateinit var colorListPreference: ListPreference
    private lateinit var languageListPreference: ListPreference
    private lateinit var reloginPreference: SwitchPreference
    private lateinit var logoutButton: Preference
    private lateinit var librarySummary: Preference
    private lateinit var userInfoPreference: Preference
    private lateinit var authorPreference: Preference
    private lateinit var versionPreference: Preference

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
            toast(getString(R.string.hint_in_logout))

            App.getApplication(requireContext().applicationContext).isLogin = false

            FuckSchoolApi.getInstance(requireContext()).logout(
                onComplete = {
                    //navigate to Login Activity and clear SP
                    viewModel.deleteAllData()
                    (requireActivity() as HomeActivity).startLoginActivity()
                }, onError = {
                    toast(getString(R.string.hint_logout_failed))
                })
            true
        }

        viewModel.userData().observe(this, Observer<User?> { user ->
            if (user == null || user.id == 0) {
                return@Observer
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
