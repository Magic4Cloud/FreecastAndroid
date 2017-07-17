/**
 * Copyright(c) 2014 DRAWNZER.ORG PROJECTS -> ANURAG
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p>
 * anurag.dev1512@gmail.com
 */

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
