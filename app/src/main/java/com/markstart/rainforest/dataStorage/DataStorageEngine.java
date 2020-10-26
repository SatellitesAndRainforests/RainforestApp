package com.markstart.rainforest.dataStorage;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.markstart.rainforest.MainActivity;
import com.markstart.rainforest.model.Track;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;


public class DataStorageEngine {

    private File tracksDirectory;
    private Context dseContext;


   public DataStorageEngine(Context context) {

       dseContext = context;

       tracksDirectory = new File(Environment.getExternalStorageDirectory() + File.separator + "trackFiles");

        if (!tracksDirectory.exists()) {
            tracksDirectory.mkdir();
        } else {
            Log.d("DSE","DID NOT MAKE A FILE FOLDER *************");
        }

    }



    public boolean saveTrackToFile(Track track, String fileName) {

        boolean writeSuccessful = false;
        FileOutputStream fs = null;

        if (trackHasAtLeastOnePoint(track)) {

            try {
                fs = new FileOutputStream(new File(tracksDirectory + File.separator + fileName));
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
        } else {
            return false;
        }
    }


    private boolean trackHasAtLeastOnePoint(Track track) {

        if (track.getTrackPoints().size() > 0) {
            return true;
        } else {
            Toast.makeText(dseContext, " Track needs > 0 Data Points ", Toast.LENGTH_LONG).show();
            return false;
        }

    }


    public Track getTrackFromFile( String fileName) {

        Track track = null;
        FileInputStream fs = null;

        try {
            fs = new FileInputStream(new File( tracksDirectory + File.separator + fileName));
            ObjectInputStream ois = new ObjectInputStream(fs);
            track = (Track) ois.readObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return track;
    }


    public void deleteAllTracks() {

        Boolean deletedFiles = false;
        String[] fileList = allFilesList();

        for (String file : fileList) {
            File f = new File(tracksDirectory + File.separator + file);
            deletedFiles = f.delete();
        }

        if (!deletedFiles) {
            Toast.makeText(dseContext, " There are no files to delete. ", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(dseContext, " Files Deleted ", Toast.LENGTH_LONG).show();
        }

    }


    public ArrayList<Track> getAllTracksFromDisk() {

        ArrayList<Track> tracks = new ArrayList<>();
        String [] fileList = allFilesList();

        for (String file: fileList ) {
               tracks.add(getTrackFromFile(file));
        }

        return tracks;

   }


    private String [] allFilesList() {
       File tracksDir = new File(String.valueOf(tracksDirectory));
       String [] fileList = tracksDir.list();
       return fileList;
    }



}







