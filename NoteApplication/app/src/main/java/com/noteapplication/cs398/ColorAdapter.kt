package com.noteapplication.cs398

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class ColorAdapter(context: Context, list: List<ColorObject>) : ArrayAdapter<ColorObject>(context, 0, list) {
    private var layoutInflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view : View = layoutInflater.inflate(R.layout.color_item_background, null, true)
        return view(view, position)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        var cv =  convertView
        if(cv == null){
            cv = layoutInflater.inflate(R.layout.color_spinner, parent, false)
        }
        return view(cv!!, position)
    }

    private fun view(view: View, position: Int): View
    {
        val colorObject : ColorObject = getItem(position) ?: return view

        val colorBlob = view.findViewById<View>(R.id.abc)

        colorBlob?.background?.setTint(Color.parseColor(colorObject.hexHash))

        return view
    }



}