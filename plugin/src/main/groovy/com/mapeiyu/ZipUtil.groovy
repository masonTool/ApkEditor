package com.mapeiyu

import org.gradle.api.Project
import org.gradle.api.file.FileTree

import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class ZipUtil {

    static void updateZip(Project project, File target, String...excludes) {
        println "updatezip: $target exclude: $excludes"
        if (excludes.size() == 0) {
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
    }


}
