package com.dwarf.mystoryapp.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.dwarf.mystoryapp.R

class MyButton : AppCompatButton {

    private lateinit var enableBackground : Drawable
    private lateinit var disableBackground : Drawable
    private var txtColor: Int = 0

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        background = if (isEnabled) enableBackground else disableBackground
        setTextColor(txtColor)
        textSize = 20f
        gravity = Gravity.CENTER
        text = if (isEnabled) context.getString(R.string.text_submit) else context.getString(R.string.text_input_first)
    }

    private fun init() {
        txtColor = ContextCompat.getColor(context,android.R.color.background_light)
        enableBackground = ContextCompat.getDrawable(context, R.drawable.bg_button) as Drawable
        disableBackground = ContextCompat.getDrawable(context, R.drawable.bg_button_disable) as Drawable
    }

}