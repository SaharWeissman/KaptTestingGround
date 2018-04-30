package com.saharw.firstannotationprocessor

import com.saharw.annotationprocessor.GenName

@GenName
class Hello

fun main(args: Array<String>){
    println("Hello ${Generated_Hello().getName()}")
}

