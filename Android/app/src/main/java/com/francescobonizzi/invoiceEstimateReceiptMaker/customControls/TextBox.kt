package com.francescobonizzi.invoiceEstimateReceiptMaker.customControls

import android.content.Context
import android.text.InputType
import android.text.method.DigitsKeyListener
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import com.francescobonizzi.invoiceEstimateReceiptMaker.R
import com.francescobonizzi.invoiceEstimateReceiptMaker.databinding.CTextboxBinding
import com.francescobonizzi.invoiceEstimateReceiptMaker.utilities.focusAndShowKeyboard

/** A custom Textbox which handles an horizontal label, readonly text, only numbers input */
class TextBox @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr)
{
    // Ho dovuto duplicare il controllo perché non ho trovato un modo
    // di non far rubare il click all'EditText (Vedi Invoice Header)
    private var _txtReadonlyText: TextView
    private var _txtText: EditText
    private var _txtPlaceholderText: TextView

    private var _isReadonly: Boolean = false
    private var _allowOnlyNumbers: Boolean = false
    private var _isEmailField: Boolean = false
    private var _isPhoneNumberField: Boolean = false

    private var _binding: CTextboxBinding? = null
    private val binding get() = _binding!!

    init
    {
        _binding = CTextboxBinding.inflate(LayoutInflater.from(context), this)

        _txtText = binding.customControlsTextboxText
        _txtPlaceholderText = binding.customControlsTextboxPlaceholderText
        _txtReadonlyText = binding.customControlsTextboxReadonlyText

        // Per la gestione degli attributi
        if (attrs != null)
        {
            val a = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.TextBoxAttributes,
                0, 0)

            try
            {
                setText(a.getString(R.styleable.TextBoxAttributes_text))
                setPlaceholderText(a.getString(R.styleable.TextBoxAttributes_placeholderText))
                setIsReadonly(a.getBoolean(R.styleable.TextBoxAttributes_isReadOnly, false))

                setAllowOnlyNumbers(
                    a.getBoolean(
                        R.styleable.TextBoxAttributes_allowOnlyNumbers,
                        false))

                setIsEmailField(
                    a.getBoolean(
                        R.styleable.TextBoxAttributes_isEmailField,
                        false))

                setIsPhoneNumberField(
                    a.getBoolean(
                        R.styleable.TextBoxAttributes_isPhoneNumber,
                        false
                    )
                )

                // La get mi dà già il valore calcolato in dp
                val customLabelWidth =
                    a.getDimensionPixelSize(R.styleable.TextBoxAttributes_labelWidth, 0)
                if (customLabelWidth > 0)
                {
                    binding.customControlsTextboxPlaceholderText.layoutParams.width = customLabelWidth
                }

            }
            finally
            {
                a.recycle()
            }
        }

        binding.customControlsTextboxBtnClearText.setOnClickListener {
            _txtText.text = null
            _txtReadonlyText.text = null
        }

        binding.customControlsTextboxBtnClearText.visibility = View.INVISIBLE

        _txtText.setOnFocusChangeListener { _, hasFocus ->

            if (!hasFocus)
            {
                binding.customControlsTextboxBtnClearText.visibility = View.INVISIBLE
            }
            else
            {
                if (!_txtText.text.isNullOrBlank())
                {
                    binding.customControlsTextboxBtnClearText.visibility = View.VISIBLE
                }
                else
                {
                    binding.customControlsTextboxBtnClearText.visibility = View.INVISIBLE
                }
            }
        }

        _txtText.doOnTextChanged { _, _, _, _ ->

            if (_txtText.hasFocus())
            {
                if (!_txtText.text.isNullOrBlank())
                {
                    binding.customControlsTextboxBtnClearText.visibility = View.VISIBLE
                }
                else
                {
                    binding.customControlsTextboxBtnClearText.visibility = View.INVISIBLE
                }
            }
        }

        _txtPlaceholderText.setOnClickListener {
            _txtText.focusAndShowKeyboard()
        }
    }

    fun text(): String
    {
        return if (_isReadonly)
        {
            _txtReadonlyText.text.toString()
        }
        else
        {
            _txtText.text.toString()
        }
    }

    fun setText(text: String?)
    {
        if (text.isNullOrEmpty())
        {
            _txtText.text = null
            _txtReadonlyText.text = null
        }
        else
        {
            _txtText.setText(text)
            _txtReadonlyText.text = text
        }
    }

    fun setNumber(number: Int?)
    {
        if (number == null)
        {
            setText(null)
        }
        else
        {
            setText(number.toString())
        }
    }

    fun placeholderText(): String
    {
        return _txtPlaceholderText.text.toString()
    }

    fun setPlaceholderText(placeholderText: String?)
    {
        if (placeholderText.isNullOrEmpty())
        {
            _txtPlaceholderText.text = null
            _txtPlaceholderText.visibility = View.GONE

            // L'hint va solo se non metti la label
            //_txtText.hint = null
        }
        else
        {
            _txtPlaceholderText.text = placeholderText
            _txtPlaceholderText.visibility = View.VISIBLE

            // L'hint va solo se non metti la label
            //_txtText.hint = placeholderText
        }
    }

    private fun setIsReadonly(isReadOnly: Boolean)
    {
        _isReadonly = isReadOnly

        if (isReadOnly)
        {
            _txtReadonlyText.visibility = View.VISIBLE
            _txtText.visibility = View.GONE
        }
        else
        {
            _txtReadonlyText.visibility = View.GONE
            _txtText.visibility = View.VISIBLE
        }
    }

    private fun setAllowOnlyNumbers(allowOnlyNumbers: Boolean)
    {
        _allowOnlyNumbers = allowOnlyNumbers

        if (allowOnlyNumbers)
        {
            _txtText.inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL
            _txtText.keyListener = DigitsKeyListener.getInstance("0123456789.,")
        }
        //else
        //{
        // Lascio quello di default
        // _txtText.inputType = InputType.TYPE_CLASS_TEXT
        //throw NotImplementedError("Don't know how to make the undo")
        //}
    }

    private fun setIsEmailField(isEmailField: Boolean)
    {
        _isEmailField = isEmailField

        if (_isEmailField)
        {
            _txtText.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        }
    }

    private fun setIsPhoneNumberField(isPhoneNumberField: Boolean)
    {
        _isPhoneNumberField = isPhoneNumberField

        if (_isPhoneNumberField)
        {
            _txtText.inputType = InputType.TYPE_CLASS_PHONE
        }
    }

    fun setError(message: String)
    {
        _txtText.error = message
    }

    fun clearErrors()
    {
        _txtText.error = null
    }

    fun doOnTextChanged(action: () -> Unit)
    {
        _txtText.doOnTextChanged { _, _, _, _ ->
            action()
        }
    }

}