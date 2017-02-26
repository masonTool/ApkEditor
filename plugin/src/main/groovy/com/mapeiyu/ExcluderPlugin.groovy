package com.mapeiyu

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.file.FileTree

import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class ExcluderPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {

        def excluder = project.container(Rule)

        project.extensions.excluder = excluder

        project.afterEvaluate {
            project.android.applicationVariants.all { variant ->
                println "<<<<<<<<<<<<<<<<${variant.name}<<<<<<<<<<<<<<<<"
                def excludeList = []

                //
                // find all flavors and build type of this variont
                //
                def flavors = variant.productFlavors.name
                flavors << variant.buildType.name << variant.name
                println "keywords:  $flavors"

                //
                // detect all excluding rules for this variant
                //

                excluder.findAll { rule ->
                    flavors.any { flavName ->
                        flavName.matches(rule.name)
                    }
                }.each { rule ->
                    println("matched: $rule.name $rule.excludeList")
                    excludeList.addAll(rule.excludeList)
                }

                println "result: $excludeList"

                variant.outputs.each { output ->
                    def packageApplicationTask = output.packageApplication

                    project.task(name: packageApplicationTask.name, overwrite: true, type:) {
                        doLast {
                            println "Hello from the GreetingPlugin"
                        }
                    }



                    def taskInputs = packageApplicationTask.inputs

//                    taskInputs.property('packagingOptions.excludes', taskInputs.properties['packagingOptions.excludes'] + excludeList as String[])
//                    println '------'
//                    println output.packageApplication.inputs.files.files
//                    println '------'
//                    println output.packageApplication.inputs.sourceFiles.files
//                    println '------'
//                    println output.packageApplication.inputs.properties
//                    println '------'



                    packageApplicationTask.doLast { pkgApp ->

                        println("taskSource: [$variant.name]")
//                        println '------'
//                        println output.packageApplication.inputs.properties
//                        println '------'

                        ZipUtil.updateZip(project, pkgApp.outputFile, excludeList as String[])
                    }
                }
            }

        }
    }
}


