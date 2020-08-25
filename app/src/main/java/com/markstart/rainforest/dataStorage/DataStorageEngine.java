package com.markstart.rainforest.dataStorage;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.markstart.rainforest.model.Track;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;


public class DataStorageEngine {

    private File tracksDirectory;


   public DataStorageEngine() {
        tracksDirectory = new File(Environment.getExternalStorageDirectory() + File.separator + "trackFiles");

        boolean folderThere = true;
        if (!tracksDirectory.exists()) {
            folderThere = tracksDirectory.mkdir();
        } else {
            Log.d("folder not created", "no no");
        }
    }



    public boolean saveTrackToFile(Context mcoContext, Track track, String fileName) {

        boolean writeSuccessful = false;
        FileOutputStream fs = null;

        try {
            fs = new FileOutputStream( new File(tracksDirectory + File.separator + fileName));
            ObjectOutputStream oos = new ObjectOutputStream(fs);
            oos.writeObject(track);
            writeSuccessful = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fs != null) {
                try {
                    fs.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return writeSuccessful;
    }

    public Track getTrackFromFile(Context mcoContext, String fileName) {

        Track track = null;
        FileInputStream fs = null;

        try {
            fs = new FileInputStream(new File( tracksDirectory + File.separator + fileName));
            ObjectInputStream ois = new ObjectInputStream(fs);
            track = (Track) ois.readObject();
            Log.d("read", "successful");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return track;
    }


    public void deleteAllTracks( Context context ) {
       String [] fileList = allFilesList( context );
        for (String file : fileList ) {
            File f = new File( tracksDirectory + File.separator + file);
            f.delete();
        }
    }

    public ArrayList<Track> getAllTracksFromDisk( Context context ) {

        ArrayList<Track> tracks = new ArrayList<Track>();

        String [] fileList = allFilesList( context );

        for (String file: fileList ) {
               tracks.add(getTrackFromFile( context, file));
        }
        return tracks;
    }


    public String [] allFilesList(Context mcoContext) {
       File tracksDir = new File(String.valueOf(tracksDirectory));
       String [] fileList = tracksDir.list();
       return fileList;
    }



}









