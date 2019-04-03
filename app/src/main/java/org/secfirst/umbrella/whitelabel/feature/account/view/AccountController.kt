package org.secfirst.umbrella.whitelabel.feature.account.view

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import com.bluelinelabs.conductor.RouterTransaction
import kotlinx.android.synthetic.main.account_password_alert.view.*
import kotlinx.android.synthetic.main.account_reset_password_alert.view.*
import kotlinx.android.synthetic.main.account_skip_alert.view.*
import kotlinx.android.synthetic.main.account_view.*
import kotlinx.android.synthetic.main.account_view.view.*
import kotlinx.android.synthetic.main.mask_app_info_view.view.*
import org.jetbrains.anko.toast
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.UmbrellaApplication
import org.secfirst.umbrella.whitelabel.feature.account.DaggerAccountComponent
import org.secfirst.umbrella.whitelabel.feature.account.interactor.AccountBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.account.presenter.AccountBasePresenter
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseController
import org.secfirst.umbrella.whitelabel.feature.maskapp.view.CalculatorController
import org.secfirst.umbrella.whitelabel.misc.checkPasswordStrength
import org.secfirst.umbrella.whitelabel.misc.doRestartApplication
import org.secfirst.umbrella.whitelabel.misc.setMaskMode
import javax.inject.Inject

class AccountController : BaseController(), AccountView {

    @Inject
    internal lateinit var presenter: AccountBasePresenter<AccountView, AccountBaseInteractor>
    private lateinit var passwordAlertDialog: AlertDialog
    private lateinit var maskAppAlertDialog: AlertDialog
    private lateinit var skipPasswordDialog: AlertDialog
    private lateinit var resetPasswordDialog: AlertDialog
    private lateinit var passwordView: View
    private lateinit var resetPasswordView: View
    private lateinit var maskAppView: View
    private lateinit var skipPasswordView: View
    private lateinit var accountView: View
    private var isMaskMode = false

    override fun onInject() {
        DaggerAccountComponent.builder()
                .application(UmbrellaApplication.instance)
                .build()
                .inject(this)
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        setUpToolbar()
        enableNavigation(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {

        accountView = inflater.inflate(R.layout.account_view, container, false)
        passwordView = inflater.inflate(R.layout.account_password_alert, container, false)
        skipPasswordView = inflater.inflate(R.layout.account_skip_alert, container, false)
        maskAppView = inflater.inflate(R.layout.mask_app_info_view, container, false)
        resetPasswordView = inflater.inflate(R.layout.account_reset_password_alert, container, false)

        maskAppAlertDialog = AlertDialog
                .Builder(activity)
                .setView(maskAppView)
                .create()
        skipPasswordDialog = AlertDialog
                .Builder(activity)
                .setView(skipPasswordView)
                .create()
        passwordAlertDialog = AlertDialog
                .Builder(activity)
                .setView(passwordView)
                .create()
        resetPasswordDialog = AlertDialog
                .Builder(activity)
                .setView(resetPasswordView)
                .create()

        presenter.onAttach(this)
        presenter.submitIsMaskApp()

        accountView.accountSettings.setOnClickListener { clickOnSettings() }
        accountView.accountPassword.setOnClickListener { passwordAlertDialog.show() }
        accountView.accountMask.setOnClickListener { clickOnMaskApp() }
        accountView.resetPassword.setOnClickListener { resetPasswordDialog.show() }

        passwordView.alertPasswordSkip.setOnClickListener { clickOnSkipAlert() }
        passwordView.alertPasswordOk.setOnClickListener { passwordAlertOk() }
        passwordView.alertPasswordCancel.setOnClickListener { passwordAlertDialog.dismiss() }

        skipPasswordView.cancel.setOnClickListener { skipPasswordDialog.dismiss() }
        skipPasswordView.ok.setOnClickListener { skipAlertOk() }

        maskAppView.handsShakeCancel.setOnClickListener { maskAppAlertDialog.dismiss() }
        maskAppView.handsShakeOk.setOnClickListener { maskAppOk() }

        resetPasswordView.resetCancel.setOnClickListener { resetPasswordDialog.dismiss() }
        resetPasswordView.resetOk.setOnClickListener { resetAlertOk() }

        presenter.submitIsLogged()
        return accountView
    }

    private fun maskAppOk() {
        presenter.submitFakeView(isMaskMode)
        if (isMaskMode)
            disableMask()
        else
            enableMask()
    }

    private fun enableMask() {
        activity?.let {
            setMaskMode(it, true)
            maskAppAlertDialog.dismiss()
            presenter.setMaskApp(true)
            presenter.submitFakeView(true)
            router.pushController(RouterTransaction.with(CalculatorController()))
            accountView.maskModeTitle.text = context.getString(R.string.disable_mask_message)
        }
    }

    private fun disableMask() {
        activity?.let {
            setMaskMode(it, false)
            presenter.setMaskApp(false)
            presenter.submitFakeView(false)
            accountView.maskModeTitle.text = context.getString(R.string.enable_masking_mode)
        }
    }

    override fun getMaskApp(isMaskApp: Boolean) {
        isMaskMode = isMaskApp
        if (isMaskApp)
            accountView.maskModeTitle.text = context.getString(R.string.account_disable_mask)
        else
            accountView.maskModeTitle.text = context.getString(R.string.enable_masking_mode)
    }

    private fun clickOnMaskApp() {
        if (!isMaskMode)
            maskAppAlertDialog.show()
        else {
            disableMask()
            context.toast(context.getString(R.string.disable_mask_message))
        }
    }

    private fun skipAlertOk() {
        presenter.setSkipPassword(true)
        skipPasswordDialog.dismiss()
    }

    private fun passwordAlertOk() {
        val token = passwordView.alertPwText.text.toString()
        val confirmToken = passwordView.alertPwConfirm.text.toString()
        if (token == confirmToken) {
            if (token.checkPasswordStrength(context))
                presenter.submitChangeDatabaseAccess(token)
        } else
            context.toast(context.getString(R.string.confirm_password_error_message))
    }

    private fun clickOnSkipAlert() {
        skipPasswordDialog.show()
        passwordAlertDialog.dismiss()
    }

    private fun setUpToolbar() {
        accountToolbar?.let {
            mainActivity.setSupportActionBar(it)
            mainActivity.supportActionBar?.title = context.getString(R.string.settings_title)
        }
    }

    private fun resetAlertOk() {
        resetPasswordDialog.dismiss()
        presenter.submitCleanDatabase()
    }

    private fun clickOnSettings() = router.pushController(RouterTransaction.with(SettingsController()))

    override fun isUserLogged(res: Boolean) {
        if (res) accountView.resetPassword.visibility = VISIBLE
    }

    override fun isTokenChanged(res: Boolean) {
        if (res) {
            passwordAlertDialog.dismiss()
            presenter.setUserLogIn()
            Toast.makeText(context, context.getString(R.string.password_success), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResetContent(res: Boolean) {
        if (res)
            doRestartApplication(context)
    }
}