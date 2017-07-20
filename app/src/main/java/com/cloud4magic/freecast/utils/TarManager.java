
package com.cloud4magic.freecast.utils;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.utils.IOUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

import static java.lang.System.in;


/**
 * 解压TAR
 * Date   2017/7/17
 * Editor  Misuzu
 */

public class TarManager {


    /**
     * 解压tar文件
     */
    public static void deTarArchive(File input, String dir)
            throws Exception {

        TarArchiveInputStream tin = new TarArchiveInputStream(new BufferedInputStream(new FileInputStream(input)));
        TarArchiveEntry entry = tin.getNextTarEntry();

        while (entry != null) {

            File archiveEntry = new File(dir, entry.getName());
            archiveEntry.getParentFile().mkdirs();

            if (entry.isDirectory()) {
                archiveEntry.mkdir();
                entry = tin.getNextTarEntry();
                continue;
            }

            OutputStream out = new FileOutputStream(archiveEntry);
            IOUtils.copy(tin, out);
            out.close();
            entry = tin.getNextTarEntry();
        }
        in.close();
        tin.close();
    }

}
