package com.francescobonizzi.invoiceEstimateReceiptMaker.pages

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigator
import com.francescobonizzi.invoiceEstimateReceiptMaker.R
import com.francescobonizzi.invoiceEstimateReceiptMaker.databinding.AMainBinding
import com.francescobonizzi.invoiceEstimateReceiptMaker.logging.ILogger
import com.francescobonizzi.invoiceEstimateReceiptMaker.logic.BillingHandler
import com.francescobonizzi.invoiceEstimateReceiptMaker.onboarding.OnboardingFragment
import com.francescobonizzi.invoiceEstimateReceiptMaker.pages.settings.SettingsActivity
import com.francescobonizzi.invoiceEstimateReceiptMaker.utilities.IntentHelpers
import com.francescobonizzi.invoiceEstimateReceiptMaker.utilities.SharedPreferencesHelper
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity()
{
    private lateinit var _navigationHost: NavController
    private val _logger: ILogger by inject()

    private var _alreadyCheckedForPurchases: Boolean = false
    private lateinit var _billingHandler: BillingHandler

    private val _onboardingDoneSharedKey = "onboardingDone"

    // Gestione custom del back: faccio sempre tornare alla home,
    // non mi piace che scorra tutti i tab.
    override fun onBackPressed()
    {
        super.onBackPressed()

        val firstItem: MenuItem = binding.bottomNavigation.menu.getItem(0)
        if (binding.bottomNavigation.selectedItemId != firstItem.itemId)
        {
            _navigationHost.navigate(R.id.navigationInvoicesFragment)
            binding.bottomNavigation.selectedItemId = firstItem.itemId
        }
        else
        {
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)

        // Quando torni alla pagina principale dai settings, rimetto sulla scheda invoices in automatico
        if (requestCode == IntentHelpers.IntentSettings)
        {
            val firstItem: MenuItem = binding.bottomNavigation.menu.getItem(0)
            if (binding.bottomNavigation.selectedItemId != firstItem.itemId)
            {
                _navigationHost.navigate(R.id.navigationInvoicesFragment)
                binding.bottomNavigation.selectedItemId = firstItem.itemId
            }
        }
    }

    fun onboardingCompleted()
    {
        _navigationHost.navigate(R.id.action_onboardingFragment_to_navigationInvoicesFragment)
        binding.bottomNavigation.visibility = View.VISIBLE
        SharedPreferencesHelper.putBool(_onboardingDoneSharedKey, true)
    }

    private fun startOnboarding()
    {
        // Varie volte è crashata perché era già nell'onboarding e non sono mai riuscito a riprodurre il problema.
        // In particolare crashava la navigazione.
        // Ho provato col flag per vedere se non ricapita.
        if ((_navigationHost.currentDestination as FragmentNavigator.Destination?)?.className != OnboardingFragment::class.qualifiedName)
        {
            binding.bottomNavigation.visibility = View.GONE
            _navigationHost.navigate(R.id.action_navigationInvoicesFragment_to_onboardingFragment)
        }
    }

    private fun isOnboardingAlreadyDone(): Boolean
    {
        return SharedPreferencesHelper.getOrPutBool(_onboardingDoneSharedKey, false)
    }

    private lateinit var binding: AMainBinding
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = AMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Nascondo la support bar perché è brutta
        supportActionBar?.hide()
        _navigationHost = findNavController(R.id.navigation_host)

        // Inizializzo l'oggetto che uso per gestire in singleton le shared preferences
        SharedPreferencesHelper.init(this, _logger)

        // Verifico gli acquisti allo startup dell'activity
        if (!_alreadyCheckedForPurchases)
        {
            _billingHandler = BillingHandler(
                _activity = this,
                _onError = { _logger.error("From MainActivity: $it") },
                _onNotPurchasedUnlimitedInvoicesLoaded = {
                    BillingHandler.setUnlimitedInvoicesNOTPurchased()
                },
                _onAlreadyPurchasedUnlimitedInvoicesLoaded = {
                    BillingHandler.setUnlimitedInvoicesPurchased()
                    _alreadyCheckedForPurchases = true
                    it.destroy()
                },
                _onPurchasedItem = {},
                _onPurchasedItemCancel = {})
        }

        binding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId)
            {
                R.id.menuFragmentInvoices ->
                {
                    _navigationHost.navigate(R.id.navigationInvoicesFragment)
                }

//                R.id.menuFragmentQuotations ->
//                {
//                    _navigationHost.navigate(R.id.navigationQuotationsFragment)
//                }

                R.id.menuFragmentProducts ->
                {
                    _navigationHost.navigate(R.id.navigationArticlesFragment)
                }

                R.id.menuFragmentCustomers ->
                {
                    _navigationHost.navigate(R.id.navigationCustomersFragment)
                }

                R.id.menuFragmentSettings ->
                {
                    val intent = Intent(this, SettingsActivity::class.java)
                    startActivityForResult(intent, IntentHelpers.IntentSettings)
                }

            }

            true
        }

        if (!isOnboardingAlreadyDone())
        {
            startOnboarding()
        }
    }
}
