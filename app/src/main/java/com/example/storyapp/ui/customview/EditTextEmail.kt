package com.example.storyapp.ui.customview

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.example.storyapp.R

class EditTextEmail : AppCompatEditText, View.OnFocusChangeListener {

    var isEmailValid = false
    private lateinit var emailSame: String
    private var isEmailHasTaken = false
    private lateinit var emailIconDrawable: Drawable

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        emailIconDrawable = ContextCompat.getDrawable(context, R.drawable.ic_email) as Drawable
        inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        compoundDrawablePadding = 16

        setHint(R.string.hint_email)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setAutofillHints(AUTOFILL_HINT_EMAIL_ADDRESS)
        }
        setDrawable(emailIconDrawable)

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Email validation
                validateEmail()
                if (isEmailHasTaken) {
                    validateEmailHasTaken()
                }
            }
        })
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        if (!hasFocus) {
            validateEmail()
            if (isEmailHasTaken) {
                validateEmailHasTaken()
            }
        }
    }

    private fun validateEmail() {
        isEmailValid = Patterns.EMAIL_ADDRESS.matcher(text.toString().trim()).matches()
        error = if (!isEmailValid) {
            resources.getString(R.string.et_email_error_message)
        } else {
            null
        }
    }

    private fun validateEmailHasTaken() {
        error = if (isEmailHasTaken && text.toString().trim() == emailSame) {
            resources.getString(R.string.email_taken)
        } else {
            null
        }
    }

    fun setErrorMessage(message: String, email: String) {
        emailSame = email
        isEmailHasTaken = true
        error = if (text.toString().trim() == emailSame) {
            message
        } else {
            null
        }
    }

    private fun setDrawable(
        start: Drawable? = null,
        top: Drawable? = null,
        end: Drawable? = null,
        bottom: Drawable? = null
    ) {
        setCompoundDrawablesWithIntrinsicBounds(start, top, end, bottom)
    }
}