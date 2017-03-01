package com.mapeiyu

import org.gradle.api.Project
import org.gradle.api.file.FileTree

import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class Utils {

    static void updateZip(Project project, File target, List<String> excludes) {
        project.logger.debug "updatezip: $target exclude: $excludes"
        if (excludes.empty) {
            return
        }

        File tmp = new File(target.toString() + ".tmp")
        target.renameTo(tmp)

        FileTree ziptree = project.zipTree(tmp).matching {
            for (String str : excludes) {
                exclude str
            }
        }
        ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(target));
        byte[] buf = new byte[1024];
        ziptree.visit {
            InputStream input = new FileInputStream(it.file);
            zipOut.putNextEntry(new ZipEntry(it.relativePath.toString()));
            def len
            while((len = input.read(buf)) != -1){
                zipOut.write(buf, 0, len);
            }
            input.close()
        }
        zipOut.close()
        tmp.delete()
    }

    /**
     * We should expand it with a more flexible method.
     * Perhaps write another gradle plugin to intervene the groovy compile process.
     * Just like the android.application plugin do. They generate a class Named BuidConfig.class to store the static values.
     * @param project
     * @return
     */
    static String getUserBuildScriptClassPath(Project project, String specify) {
        def path = project.buildscript.configurations.classpath.find {
            it.absolutePath.contains(specify)
        }
        if (!path) {
            path = project.rootProject.buildscript.configurations.classpath.find {
                it.absolutePath.contains(specify)
            }
        }
        return path.toString()
    }


}
