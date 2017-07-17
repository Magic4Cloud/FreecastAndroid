package com.cloud4magic.freecast.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Date   2017/7/17
 * Editor  Misuzu
 */

public class FilesUtils {

    private ArrayList<File> filelist = new ArrayList<>();

    public ArrayList<File> getFilelist() {
        return filelist;
    }

    public  List<File> getFileList(String strPath) {

        File dir = new File(strPath);
        File[] files = dir.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                String fileName = files[i].getName();
                if (files[i].isDirectory()) {
                    getFileList(files[i].getAbsolutePath());
                } else  {
                    String strFileName = files[i].getAbsolutePath();
                    Logger.e("Misuzu","FileName --->" + strFileName);
                    filelist.add(files[i]);
                }
            }

        }
        return filelist;
    }

}
