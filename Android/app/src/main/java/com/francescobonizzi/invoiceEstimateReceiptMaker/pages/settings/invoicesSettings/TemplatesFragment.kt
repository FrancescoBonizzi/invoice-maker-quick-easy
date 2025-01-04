package com.francescobonizzi.invoiceEstimateReceiptMaker.pages.settings.invoicesSettings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.francescobonizzi.invoiceEstimateReceiptMaker.R
import com.francescobonizzi.invoiceEstimateReceiptMaker.adapters.TemplateColorsAdapter
import com.francescobonizzi.invoiceEstimateReceiptMaker.adapters.TemplateLayoutsAdapter
import com.francescobonizzi.invoiceEstimateReceiptMaker.databinding.FSettingsInvoicesTemplatesBinding
import com.francescobonizzi.invoiceEstimateReceiptMaker.domain.Invoice
import com.francescobonizzi.invoiceEstimateReceiptMaker.domain.TemplateColor
import com.francescobonizzi.invoiceEstimateReceiptMaker.domain.TemplateLayout
import com.francescobonizzi.invoiceEstimateReceiptMaker.logging.ILogger
import com.francescobonizzi.invoiceEstimateReceiptMaker.logic.InvoiceEditor
import com.francescobonizzi.invoiceEstimateReceiptMaker.pages.invoices.invoiceDetail.InvoiceDetailActivity
import com.francescobonizzi.invoiceEstimateReceiptMaker.staticData.Fakes
import com.francescobonizzi.invoiceEstimateReceiptMaker.staticData.TemplatesData
import com.francescobonizzi.invoiceEstimateReceiptMaker.storage.InMemoryInvoicePreviewBundler
import com.francescobonizzi.invoiceEstimateReceiptMaker.storage.abstractions.IAppConfigurationRepository
import com.francescobonizzi.invoiceEstimateReceiptMaker.utilities.Constants
import com.francescobonizzi.invoiceEstimateReceiptMaker.utilities.DialogsHelpers
import com.francescobonizzi.invoiceEstimateReceiptMaker.utilities.Helpers
import com.francescobonizzi.invoiceEstimateReceiptMaker.utilities.ItemClickSupport
import org.koin.android.ext.android.inject

class TemplatesFragment : Fragment()
{
    private lateinit var _navigationHost: NavController

    private lateinit var _listLayoutRecyclerView: RecyclerView
    private lateinit var _listLayoutViewAdapter: TemplateLayoutsAdapter
    private lateinit var _templateLayouts: Array<TemplateLayout>

    private lateinit var _listColorsRecyclerView: RecyclerView
    private lateinit var _listColorsViewAdapter: TemplateColorsAdapter
    private lateinit var _templateColors: Array<TemplateColor>

    private val _logger: ILogger by inject()
    private val _appConfigurationRepository: IAppConfigurationRepository by inject()
    private val _previewInvoiceBundler: InMemoryInvoicePreviewBundler by inject()

    private var _isForInvoice: Boolean = false

    private var _binding: FSettingsInvoicesTemplatesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View
    {
        _binding = FSettingsInvoicesTemplatesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        try
        {
            // Significa che sono arrivato qui per scegliere un template per l'invoice
            _isForInvoice = arguments?.getBoolean(Constants.IsForInvoiceBundleVariable) == true

            _navigationHost = requireView().findNavController()
            _templateColors = TemplatesData.getAllColors()
            _templateLayouts = TemplatesData.getAllLayouts()

            setPageData()
            setHandlers()
        }
        catch (exception: Exception)
        {
            _logger.error(exception)
            DialogsHelpers.showError(requireActivity(), getString(R.string.generic_error_load))
        }
    }

    private fun setPageData()
    {
       binding.navigationTopBar.txtBack.text = Helpers.getPreviousPageTitle(_navigationHost)

        // Mostro il salva solo se sono nei settings, dall'invoice vige il back
        if (!_isForInvoice)
        {
            binding.navigationTopBar.btnAdditionalTopRightButton.visibility = View.VISIBLE
            binding.navigationTopBar.btnAdditionalTopRightButton.isEnabled = false
        }

        val appConfiguration = _appConfigurationRepository.get()

        // Lista layout
        _listLayoutViewAdapter = TemplateLayoutsAdapter(_templateLayouts, requireActivity().assets)
        _listLayoutRecyclerView = binding.listLayouts.apply {
            // Migliora le performance se gli oggetti non hanno cambi di layout in corso
            setHasFixedSize(true)
            adapter = _listLayoutViewAdapter
        }

        // Lista colori
        _listColorsViewAdapter = TemplateColorsAdapter(_templateColors)
        _listColorsRecyclerView = binding.listColors.apply {
            // Migliora le performance se gli oggetti non hanno cambi di layout in corso
            setHasFixedSize(true)
            adapter = _listColorsViewAdapter
        }

        if (!_isForInvoice)
        {
            _listColorsViewAdapter.selectedPosition = appConfiguration.template.colorId!!
            _listColorsViewAdapter.notifyDataSetChanged()

            _listLayoutViewAdapter.selectedPosition = appConfiguration.template.layoutId!!
            _listLayoutViewAdapter.notifyDataSetChanged()
        }
        else
        {
            val currentInvoice =
                (requireActivity() as InvoiceDetailActivity).getCurrentInvoiceEditor().invoice

            _listColorsViewAdapter.selectedPosition = currentInvoice.template!!.colorId!!
            _listColorsViewAdapter.notifyDataSetChanged()

            _listLayoutViewAdapter.selectedPosition = currentInvoice.template!!.layoutId!!
            _listLayoutViewAdapter.notifyDataSetChanged()
        }

    }

    /** Salva nel caso in cui sia qui per i settings e non per creare una nuova invoice */
    private fun saveNotForInvoice()
    {
        val appConfiguration = _appConfigurationRepository.get()
        val thisColorBundle = _templateColors[_listColorsViewAdapter.selectedPosition]

        appConfiguration.template.layoutId = _listLayoutViewAdapter.selectedPosition
        appConfiguration.template.colorId = _listColorsViewAdapter.selectedPosition
        appConfiguration.template.primaryColor = thisColorBundle.primaryColor
        appConfiguration.template.primaryColorLighter =
            thisColorBundle.primaryColorLighter
        appConfiguration.template.commonTextColor = thisColorBundle.commonTextColor
        appConfiguration.template.keyValueLabelColor =
            thisColorBundle.keyValueLabelColor
        appConfiguration.template.textColorOverPrimaryColor =
            thisColorBundle.textColorOverPrimaryColor

        _appConfigurationRepository.save()
    }

    private fun setHandlers()
    {
        binding.navigationTopBar.btnBack.setOnClickListener {

            if (_isForInvoice)
            {
                try
                {
                    // Se è per l'invoice, imposto layout e colore selezionati nell'invoice corrente
                    val currentInvoice = (requireActivity() as InvoiceDetailActivity).getCurrentInvoiceEditor().invoice
                    setTemplateToInvoice(currentInvoice)
                }
                catch (exception: Exception)
                {
                    _logger.error(exception)
                    DialogsHelpers.showError(
                        requireActivity(),
                        getString(R.string.generic_error_save))
                }
            }

            _navigationHost.popBackStack()
        }

        binding.navigationTopBar.btnAdditionalTopRightButton.setOnClickListener {

            // Se è per l'invoice, imposto layout e colore selezionati nei settings dell'App
            if (!_isForInvoice)
            {
                try
                {
                    saveNotForInvoice()
                }
                catch (exception: Exception)
                {
                    _logger.error(exception)
                    DialogsHelpers.showError(
                        requireActivity(),
                        getString(R.string.generic_error_save))
                }
            }

            _navigationHost.popBackStack()
        }

        ItemClickSupport.addTo(_listLayoutRecyclerView).onItemClick { position ->
            _listLayoutViewAdapter.selectedPosition = position
            _listLayoutViewAdapter.notifyDataSetChanged()
            binding.navigationTopBar.btnAdditionalTopRightButton.isEnabled = true
        }

        ItemClickSupport.addTo(_listColorsRecyclerView).onItemClick { position ->
            _listColorsViewAdapter.selectedPosition = position
            _listColorsViewAdapter.notifyDataSetChanged()
            binding.navigationTopBar.btnAdditionalTopRightButton.isEnabled = true
        }

        binding.btnInvoicePreview.setOnClickListener {

            try
            {
                // In questa schermata utilizzo sempre una fattura fake per la preview
                val thisColorBundle = _templateColors[_listColorsViewAdapter.selectedPosition]

                val fakeInvoice = Fakes.createFakeInvoice(
                    layoutId = _listLayoutViewAdapter.selectedPosition,
                    colorId = _listColorsViewAdapter.selectedPosition,
                    templateColor = thisColorBundle,
                    regionalSettings = _appConfigurationRepository.get().regionalSettings)
                val invoiceEditor = InvoiceEditor(fakeInvoice)
                _previewInvoiceBundler.set(invoiceEditor)

                val bundle = Bundle()

                // Anche se vengo dall'invoice detail - template, nascondo comunque i pulsanti di share
                // nella preview. Perchè dalla pagina dei template, voglio sempre e solo un esempio di fattura.
                // Quella ufficiale la mostro solo se arrivo alla preview dall'invoice detail direttamente
                bundle.putBoolean(Constants.ShowSharePrintButtonsVariable, false)

                if (!_isForInvoice)
                {
                    saveNotForInvoice()
                    _navigationHost.navigate(
                        R.id.action_templatesFragment_to_invoicePreviewFragment,
                        bundle)
                }
                else
                {
                    // Se è per l'invoice, imposto layout e colore selezionati nell'invoice corrente
                    // anche da qui, altrimenti al back ho perso tutto
                    val currentInvoice =
                        (requireActivity() as InvoiceDetailActivity).getCurrentInvoiceEditor().invoice
                    setTemplateToInvoice(currentInvoice)

                    _navigationHost.navigate(
                        R.id.action_templatesFragment2_to_invoicePreviewFragment2,
                        bundle)
                }

            }
            catch (exception: Exception)
            {
                _logger.error(exception)
                DialogsHelpers.showError(requireActivity(), getString(R.string.generic_error))
            }

        }
    }

    private fun setTemplateToInvoice(currentInvoice: Invoice)
    {
        val thisColorBundle = _templateColors[_listColorsViewAdapter.selectedPosition]
        currentInvoice.template!!.layoutId = _listLayoutViewAdapter.selectedPosition
        currentInvoice.template!!.colorId = _listColorsViewAdapter.selectedPosition
        currentInvoice.template!!.primaryColor = thisColorBundle.primaryColor
        currentInvoice.template!!.primaryColorLighter = thisColorBundle.primaryColorLighter
        currentInvoice.template!!.commonTextColor = thisColorBundle.commonTextColor
        currentInvoice.template!!.keyValueLabelColor = thisColorBundle.keyValueLabelColor
        currentInvoice.template!!.textColorOverPrimaryColor = thisColorBundle.textColorOverPrimaryColor
    }

}