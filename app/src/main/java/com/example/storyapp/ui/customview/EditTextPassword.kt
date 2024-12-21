package com.example.storyapp.ui.customview
import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.example.storyapp.R

    class EditTextPassword : AppCompatEditText, View.OnTouchListener {
        private lateinit var passwordIconDrawable: Drawable

        var isPasswordValid: Boolean = false

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

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            transformationMethod = PasswordTransformationMethod.getInstance()
        }

        private fun init() {
            passwordIconDrawable =
                ContextCompat.getDrawable(context, R.drawable.ic_password) as Drawable
            inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
            compoundDrawablePadding = 16

            setHint(R.string.hint_password)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                setAutofillHints(AUTOFILL_HINT_PASSWORD)
            }

            setDrawable(passwordIconDrawable)
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(p0: Editable?) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // Password validation
                        validatePassword()
                }
            })
        }

        override fun onTouch(v: View?, event: MotionEvent): Boolean {
            return false
        }

        override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
            super.onFocusChanged(focused, direction, previouslyFocusedRect)
            if (!focused) {
                validatePassword()
            }
        }

        private fun validatePassword() {
            isPasswordValid = (text?.length ?: 0) >= 8
            error = if (!isPasswordValid) {
                resources.getString(R.string.et_password_error_message)
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