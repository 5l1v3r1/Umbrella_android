package org.secfirst.umbrella.whitelabel.feature.main

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.bluelinelabs.conductor.Conductor
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.github.tbouron.shakedetector.library.ShakeDetector
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.main_view.*
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.data.disk.isRepository
import org.secfirst.umbrella.whitelabel.data.preferences.AppPreferenceHelper.Companion.EXTRA_LOGGED_IN
import org.secfirst.umbrella.whitelabel.data.preferences.AppPreferenceHelper.Companion.EXTRA_MASK_APP
import org.secfirst.umbrella.whitelabel.data.preferences.AppPreferenceHelper.Companion.EXTRA_SHOW_MOCK_VIEW
import org.secfirst.umbrella.whitelabel.data.preferences.AppPreferenceHelper.Companion.PREF_NAME
import org.secfirst.umbrella.whitelabel.feature.account.view.AccountController
import org.secfirst.umbrella.whitelabel.feature.checklist.view.controller.HostChecklistController
import org.secfirst.umbrella.whitelabel.feature.form.view.controller.HostFormController
import org.secfirst.umbrella.whitelabel.feature.lesson.view.LessonController
import org.secfirst.umbrella.whitelabel.feature.login.view.LoginController
import org.secfirst.umbrella.whitelabel.feature.maskapp.view.CalculatorController
import org.secfirst.umbrella.whitelabel.feature.reader.view.HostReaderController
import org.secfirst.umbrella.whitelabel.feature.tour.view.TourController
import org.secfirst.umbrella.whitelabel.misc.*
import org.secfirst.umbrella.whitelabel.misc.AppExecutors.Companion.uiContext
import java.util.logging.Logger


class MainActivity : AppCompatActivity() {

    lateinit var router: Router
    private fun performDI() = AndroidInjection.inject(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(org.secfirst.umbrella.whitelabel.R.layout.main_view)
        setSupportActionBar(searchToolbar)
        performDI()
        initRoute(savedInstanceState)
        isDeepLink()
    }

    override fun onResume() {
        super.onResume()
        ShakeDetector.start()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the options menu from XML
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)

        // Get the SearchView and set the searchable configuration
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        (menu.findItem(R.id.menu_search).actionView as SearchView).apply {
            val searchEditText = this.findViewById<View>(androidx.appcompat.R.id.search_src_text) as EditText
            searchEditText.setTextColor(resources.getColor(R.color.white))
            searchEditText.setHintTextColor(resources.getColor(R.color.white))
            // Assumes current activity is the searchable activity
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
            setIconifiedByDefault(false) // Do not iconify the widget; expand it by default
            isSubmitButtonEnabled = true
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(p0: String?): Boolean {
                    p0?.let {
                        val i = Intent(this@MainActivity, SearchActivity::class.java)
                        i.action = Intent.ACTION_SEARCH
                        i.putExtra(SearchManager.QUERY, it)
                        startActivity(i)
                        return true
                    }
                    return false
                }

                override fun onQueryTextChange(p0: String?): Boolean {
                    return false
                }

            })
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val name = this@MainActivity.resources.getResourceEntryName(item.itemId)
        Logger.getLogger("bbb").info("id $name")
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            hideKeyboard()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initRoute(savedInstanceState: Bundle?) {
        navigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener)
        router = Conductor.attachRouter(this, baseContainer, savedInstanceState)

        if (isMaskMode() && isShowMockView()) {
            router.setRoot(RouterTransaction.with(CalculatorController()))
        } else {
            setShowMockView()
            when {
                isLoggedUser() -> {
                    router.setRoot(RouterTransaction.with(LoginController()))
                    navigation.menu.getItem(2).isChecked = true
                }
                isRepository() -> {
                    router.setRoot(RouterTransaction.with(HostChecklistController()))
                    navigation.menu.getItem(2).isChecked = true
                }
                else -> router.setRoot(RouterTransaction.with(TourController()))
            }
        }
    }

    private val navigationItemSelectedListener =
            BottomNavigationView.OnNavigationItemSelectedListener { item ->

                when (item.itemId) {
                    org.secfirst.umbrella.whitelabel.R.id.navigation_feeds -> {
                        router.pushController(RouterTransaction.with(HostReaderController()))
                        return@OnNavigationItemSelectedListener true
                    }
                    org.secfirst.umbrella.whitelabel.R.id.navigation_forms -> {
                        router.pushController(RouterTransaction.with(HostFormController()))
                        return@OnNavigationItemSelectedListener true
                    }
                    org.secfirst.umbrella.whitelabel.R.id.navigation_checklists -> {
                        router.pushController(RouterTransaction.with(HostChecklistController()))
                        return@OnNavigationItemSelectedListener true
                    }
                    org.secfirst.umbrella.whitelabel.R.id.navigation_lessons -> {
                        router.pushController(RouterTransaction.with(LessonController()))
                        return@OnNavigationItemSelectedListener true
                    }
                    org.secfirst.umbrella.whitelabel.R.id.navigation_account -> {
                        router.pushController(RouterTransaction.with(AccountController()))
                        return@OnNavigationItemSelectedListener true
                    }
                }
                false
            }

    override fun onStop() {
        super.onStop()
        ShakeDetector.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        ShakeDetector.destroy()
    }

    override fun onBackPressed() {
        if (!router.handleBack())
            super.onBackPressed()
    }

    fun hideNavigation() = navigation?.let { it.visibility = INVISIBLE }

    fun showNavigation() = navigation?.let { it.visibility = VISIBLE }

    fun navigationPositionToCenter() {
        navigation.menu.getItem(2).isChecked = true
    }

    private fun isLoggedUser(): Boolean {
        val shared = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return shared.getBoolean(EXTRA_LOGGED_IN, false)
    }

    private fun setShowMockView() {
        if (isMaskMode()) {
            val shared = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            shared.edit().putBoolean(EXTRA_SHOW_MOCK_VIEW, true).apply()
        }
    }

    private fun isMaskMode(): Boolean {
        val shared = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val isMask = shared.getBoolean(EXTRA_MASK_APP, false)
        if (!isMask)
            setMaskMode(this, false)
        return isMask
    }

    private fun isShowMockView(): Boolean {
        val shared = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return shared.getBoolean(EXTRA_SHOW_MOCK_VIEW, false)
    }

    private fun isDeepLink() {
        if (ACTION_VIEW == intent.action) {
            val uri = intent.data
            val uriString = uri?.toString() ?: ""
            val path = uriString.substringAfterLast(SCHEMA)
            val uriSplitted = path.split("/")
            when (uri?.host) {
                FEED_HOST -> openFeedByUrl(router, navigation)
                FORM_HOST -> openFormByUrl(router, navigation, uriString)
//                CHECKLIST_HOST -> openChecklistByUrl(router, navigation, uriString)
                SEARCH_HOST -> {
                    val i = Intent(this@MainActivity, SearchActivity::class.java)
                    i.action = Intent.ACTION_SEARCH
                    intent?.data?.lastPathSegment?.let {
                        i.putExtra(SearchManager.QUERY, it)
                    }
                    startActivity(i)
                }
                else -> {
                    launchSilent(uiContext) {
                        if (isLessonDeepLink(uriSplitted))
                            openSpecificLessonByUrl(router, navigation, uriString)
                        else
                            openDifficultyByUrl(router, navigation, path)
                    }
                }
            }
        }
    }
}
