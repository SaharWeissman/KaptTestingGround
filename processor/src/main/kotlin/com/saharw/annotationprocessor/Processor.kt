package com.saharw.annotationprocessor

import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeSpec
import java.io.File
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement

@AutoService(Processor::class)
class Processor : AbstractProcessor() {

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }

    /**
     * this method is used to define which annotation types this processor can handle
     */
    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        println("getSupportedAnnotationTypes")
        return mutableSetOf(GenName::class.java.name)
    }

    /**
     * specify which source version (e.g. java version) is supported by this annotation processor.
     * apparently leaving a default value is bad practice - better to specify value / state latest
     */
    override fun getSupportedSourceVersion(): SourceVersion {
        println("getSupportedSourceVersion")
        return SourceVersion.latest()
    }

    /**
     * were the actual processing takes place..
     */
    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment?): Boolean {
        println("process")
        roundEnv?.getElementsAnnotatedWith(GenName::class.java)
                ?.forEach({
                    val className = it.simpleName.toString()
                    println("processing $className")
                    val pack = processingEnv.elementUtils.getPackageOf(it).toString()
                    generateClass(className, pack)
                })
        return true
    }

    private fun generateClass(className: String, pack: String){
        val outputFileName = "Generated_$className"
        val file = FileSpec.builder(pack, outputFileName)
                .addType(TypeSpec.classBuilder(outputFileName)
                    .addFunction(FunSpec.builder("getName")
                        .addStatement("return \"World\"")
                        .build())
                    .build())
                .build()

        val kaptKotlinGeneratedDir = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]
        file.writeTo(File(kaptKotlinGeneratedDir, "$outputFileName.kt"))
    }
}