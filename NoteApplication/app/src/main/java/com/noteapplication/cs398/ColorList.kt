package com.noteapplication.cs398

class ColorList {

    val defaultColor : ColorObject = Color()[0]

    fun Position(colorObject: ColorObject) : Int
    {
        for(i in Color().indices){
            if(colorObject == Color()[i]){
                return 1
            }
        }
        return 0
    }

    fun Color() : List<ColorObject> {
        return listOf(
            ColorObject("green", "#5CADAD"),
            ColorObject("blue", "#1096AD"),
            ColorObject("purple", "#AD508D"),
            ColorObject("brown", "#AD8610")
        )
    }
}