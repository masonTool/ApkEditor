package com.mapeiyu

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project

class ApkEditorPlugin implements Plugin<Project> {

    static String PLUGIN_SCOPE = 'apkeditor'
    static String SPECIFY = 'com.mapeiyu.apkeditor/apkeditor'

    //Used to forbid the Up-to-date caches when this plugin is updated. So I record the classpath in plugin user's config
    String versionInfo

    @Override
    void apply(Project project) {

        versionInfo = Utils.getUserBuildScriptClassPath(project, SPECIFY)
        project.logger.quiet "apkeditor's version file: $versionInfo"

        //used for container's exclude. means effect for specified variant. i.g.
        //apkeditor {
        //  debug {
        //      exclude '**/**'
        //  }
        //}
        def container = project.container(ExcludeRule)
        project.extensions."$PLUGIN_SCOPE" = container

        def original = []
        //used for delegate method execute for exclude. means effect for all. i.g.
        //apkeditor {
        //  exclude '**/**'
        //}
        project.metaClass.'exclude' = { a ->
            original += Arrays.asList(a)
        }

        project.afterEvaluate {
            if (!project.plugins.findPlugin('com.android.application')) {
                throw new GradleException(
                        'You must apply the android application plugin for the ApkEditorPlugin')
            }

            project.android.applicationVariants.all { variant ->
                def excludeList = [] + original
                project.logger.debug "get the public excludes $excludeList"

                // find all flavors and build type of this variont
                def flavors = variant.productFlavors.name
                flavors << variant.buildType.name << variant.name
                project.logger.debug "match keywords:  $flavors"

                // detect all excluding rules for this variant
                container.findAll { rule ->
                    flavors.any { flavName ->
                        flavName.matches(rule.name)
                    }
                }.each { rule ->
                    project.logger.debug "matched: $rule.name $rule.excludeList"
                    excludeList.addAll(rule.excludeList)
                }
                project.logger.debug "get $variant.name's result is $excludeList"

                variant.outputs.each { output ->
                    def packageApplicationTask = output.packageApplication
                    //Up-to-date checks  {https://docs.gradle.org/current/userguide/more_about_tasks.html}
                    packageApplicationTask.inputs.property("exlude", excludeList)
                    packageApplicationTask.inputs.property("versionInfo", versionInfo)

                    packageApplicationTask.doFirst {task ->
                        doExclude(project, task, excludeList)
                    }
                }
            }

        }
    }

    /**
     * 执行过滤删除操作
     * @param task
     * @param excludeList
     */
    def void doExclude(def project, def task, def excludeList) {
        if (excludeList.empty) {
            return
        }
        project.logger.quiet "$task - The excludeList: $excludeList"

        def jniExcludeList = []
        def resExcludeList = []
        def dexExcludeList = []
        def assetExcludeList = []

        excludeList.each {String rule ->
            if (rule.startsWith('/')) {
                rule = rule.substring(1)
            }

            if (rule.startsWith('lib/')) {
                jniExcludeList << rule
            } else if (rule.startsWith('res/')) {
                resExcludeList << rule
            } else if (rule.startsWith('assets/')) {
                assetExcludeList << rule.substring(7)
            } else {
                jniExcludeList << rule
                resExcludeList << rule
                dexExcludeList << rule
                assetExcludeList << rule
            }
        }


// TODO Collection<File> javaRes = task.javaResourceFiles

        if (!jniExcludeList.empty) {
            task.jniFolders.each {folder ->
                project.fileTree(dir:folder, includes:jniExcludeList).each {
                    project.logger.quiet "$task - lib delete: $it"
                    it.delete()
                }
            }
        }

        if (!resExcludeList.empty) {
            project.logger.quiet "$task - resource exclude: $resExcludeList"
            Utils.updateZip(project, task.resourceFile, resExcludeList)
        }

        if (!dexExcludeList.empty) {
            task.dexFolders.each {folder ->
                project.fileTree(dir:folder, includes:dexExcludeList).each {
                    project.logger.quiet "$task - dex delete: $it"
                    it.delete()
                }
            }
        }

        if (!assetExcludeList.empty) {
            project.fileTree(dir: task.assets, includes:assetExcludeList).each {
                project.logger.quiet "$task - assets delete: $it"
                it.delete()
            }
        }
    }
}


