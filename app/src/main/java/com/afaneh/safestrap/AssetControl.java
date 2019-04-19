package com.afaneh.safestrap;

import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

class AssetControl {
    private final static String ZIP_FILTER = "assets";
    private static final int BUFSIZE = 5192;
    String LOGTAG = "Unknown App";
    String apkPath = "";
    String mAppRoot = "";

    void unzipAsset(String filename) {
        try {
            File zipFile = new File(apkPath);
            long zipLastModified = zipFile.lastModified();
            ZipFile zip = new ZipFile(apkPath);
            Vector<ZipEntry> files = getAssets(zip);
            int zipFilterLength = ZIP_FILTER.length();

            Enumeration<?> entries = files.elements();
            while (entries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                String path = entry.getName().substring(zipFilterLength);
                if (filename.equals(path)) {
                    File outputFile = new File(mAppRoot, path);
                    File parentDir = outputFile.getParentFile();
                    if(parentDir !=null && ! parentDir.exists() ){
                        if(!parentDir.mkdirs()){
                            throw new IOException("error creating directories");
                        }
                    }

                    if (outputFile.exists() && entry.getSize() == outputFile.length() && zipLastModified < outputFile.lastModified())
                        continue;
                    FileOutputStream fos = new FileOutputStream(outputFile);
                    copyStreams(zip.getInputStream(entry), fos);
                    Runtime.getRuntime().exec("chmod 755 " + outputFile.getAbsolutePath());
                }
            }
        } catch (IOException e) {
            Log.e(LOGTAG, "Error: " + e.getMessage());
        }
    }

    void unzipAssets() {
        try {
            File zipFile = new File(apkPath);
            long zipLastModified = zipFile.lastModified();
            ZipFile zip = new ZipFile(apkPath);
            Vector<ZipEntry> files = getAssets(zip);
            int zipFilterLength = ZIP_FILTER.length();

            Enumeration<?> entries = files.elements();
            while (entries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                String path = entry.getName().substring(zipFilterLength);
                File outputFile = new File(mAppRoot, path);
                File parentDir = outputFile.getParentFile();
                if(parentDir !=null && ! parentDir.exists() ){
                    if(!parentDir.mkdirs()){
                        throw new IOException("error creating directories");
                    }
                }

                if (outputFile.exists() && entry.getSize() == outputFile.length() && zipLastModified < outputFile.lastModified())
                    continue;
                FileOutputStream fos = new FileOutputStream(outputFile);
                copyStreams(zip.getInputStream(entry), fos);
                Runtime.getRuntime().exec("chmod 755 " + outputFile.getAbsolutePath());
            }
        } catch (IOException e) {
            Log.e(LOGTAG, "Error: " + e.getMessage());
        }
    }

    private void copyStreams(InputStream is, FileOutputStream fos) {
        BufferedOutputStream os = null;
        try {
            byte data[] = new byte[BUFSIZE];
            int count;
            os = new BufferedOutputStream(fos, BUFSIZE);
            while ((count = is.read(data, 0, BUFSIZE)) != -1) {
                os.write(data, 0, count);
            }
            os.flush();
        } catch (IOException e) {
            Log.e(LOGTAG, "Exception while copying: " + e);
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e2) {
                Log.e(LOGTAG, "Exception while closing the stream: " + e2);
            }
        }
    }

    private Vector<ZipEntry> getAssets(ZipFile zip) {
        Vector<ZipEntry> list = new Vector<>();
        Enumeration<?> entries = zip.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) entries.nextElement();
            if (entry.getName().startsWith(ZIP_FILTER)) {
                list.add(entry);
            }
        }
        return list;
    }
}