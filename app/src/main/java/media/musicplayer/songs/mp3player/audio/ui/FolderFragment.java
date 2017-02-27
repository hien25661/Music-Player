package media.musicplayer.songs.mp3player.audio.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import org.apache.commons.io.FilenameUtils;
import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;
import media.musicplayer.songs.mp3player.audio.R;
import media.musicplayer.songs.mp3player.audio.adapter.FolderAdapter;
import media.musicplayer.songs.mp3player.audio.model.Item;
import media.musicplayer.songs.mp3player.audio.model.Song;
import media.musicplayer.songs.mp3player.audio.model.TransitPlaySong;
import media.musicplayer.songs.mp3player.audio.task.AsynTaskGetSongLocal;

/**
 * Created by NewBie on 6/30/16.
 */
public class FolderFragment extends Fragment {
    Context mContext;
    ArrayList<File> mListDirectory = new ArrayList<>();
    @Bind(R.id.lv_folder)
    ListView listViewFolder;
    ArrayList<File> mListFile = new ArrayList<>();
    FolderAdapter adapter;
    ArrayList<File> RootFiles = new ArrayList<>();
    @Bind(R.id.tv_directory)
    TextView tv_directory;

    // Stores names of traversed directories
    ArrayList<String> str = new ArrayList<String>();

    // Check if the first level of the directory structure is the one showing
    private Boolean firstLvl = true;

    private static final String TAG = "F_PATH";

    private Item[] fileList;
    private File path;
    private String chosenFile;
    ArrayList<Song> mListSongs = new ArrayList<>();
    public static final int REQUEST_CODE_ASK_PERMISSIONS = 11;

    public FolderFragment() {
    }

    @SuppressLint("ValidFragment")
    public FolderFragment(ArrayList<Song> mlist) {
        mListSongs = mlist;
        try {
            for (Song song : mlist) {
                boolean isExist = false;
                String root[] = song.getPath().split("/");
                Log.e("BOA", "" + song.getPath());
                for (File a : RootFiles) {
                    if (a.getPath().equalsIgnoreCase(root[1])) {
                        path = new File(root[1]);
                        isExist = true;
                        break;
                    }
                }
                if (!isExist) {
                    File file = new File(root[1]);
                    RootFiles.add(new File(root[1]));
                }
            }
        } catch (Exception ex) {
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_folder, container, false);
        mContext = view.getContext();
        ButterKnife.bind(this, view);
        Fabric.with(mContext, new Crashlytics());
        try {
            tv_directory.setText(path.getAbsolutePath());
            loadFileList();
            listViewFolder.setAdapter(adapter);
            listViewFolder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        chosenFile = fileList[position].file;
                        if (chosenFile.equalsIgnoreCase("emulated")) {
                            chosenFile += "/0";
                        }
                        File sel = new File(path + "/" + chosenFile);
                        if (sel.isDirectory()) {
                            firstLvl = false;
                            // Adds chosen directory to list
                            str.add(chosenFile);
                            fileList = null;
                            path = new File(sel + "");
                            tv_directory.setText(path.getAbsolutePath());
                            loadFileList();
                            listViewFolder.setAdapter(adapter);
                        }

                        // Checks if 'up' was clicked
                        else if (chosenFile.equalsIgnoreCase("Back") && !sel.exists()) {
                            // present directory removed from list
                            String s = str.remove(str.size() - 1);
                            // path modified to exclude present directory
                            path = new File(path.toString().substring(0,
                                    path.toString().lastIndexOf(s)));
                            tv_directory.setText(path.getAbsolutePath());
                            fileList = null;
                            // if there are no more directories in the list, then
                            // its the first level
                            if (str.isEmpty()) {
                                firstLvl = true;
                            }
                            loadFileList();
                            listViewFolder.setAdapter(adapter);
                        } else {
                            int index = getInSongList(sel);
                            if (index >= 0) {
                                EventBus.getDefault().post(new TransitPlaySong(mListSongs, index, false));
                            }

                        }
                    } catch (Exception exx) {
                    }
                }
            });
        } catch (Exception ex) {

        }
        return view;
    }

    private void loadFileAfter() {
        try {
            path.mkdirs();
        } catch (SecurityException e) {
            Log.e(TAG, "unable to write on the sd card ");
        }

        // Checks whether path exists
        if (path.exists()) {
            FilenameFilter filter = new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    File sel = new File(dir, filename);
                    if (!sel.isHidden()) {
                        /*int numberFile = 0;
                        numberFile = getFile(sel.getAbsolutePath());
                        if (sel.isDirectory()) {
                            return (numberFile > 0 ? true : false);
                        } else if (sel.length() > 0 && checkExistInSongList(sel)) {
                            return true;
                        }*/
                        return checkPathExistInSongList(sel);
                    }
                    return false;
                }

            };
            String[] fList = path.list(filter);
            File[] fListFile = path.listFiles(filter);
            try {
                fileList = new Item[fList.length];
                for (int i = 0; i < fList.length; i++) {
                    fileList[i] = new Item(fListFile[i], fList[i], R.drawable.disc_mini);

                    // Convert into file path
                    File sel = new File(path, fList[i]);

                    // Set drawables
                    if (sel.isDirectory()) {
                        fileList[i].icon = R.drawable.music_folder;
                        int numberofFiles = getFile(fileList[i].mFile.getAbsolutePath());
                        fileList[i].numbofFiles = numberofFiles;
                        Log.d("DIRECTORY", fileList[i].file);
                    } else {
                        Log.d("FILE", fileList[i].file);
                    }
                }

                if (!firstLvl) {
                    Item temp[] = new Item[fileList.length + 1];
                    for (int i = 0; i < fileList.length; i++) {
                        temp[i + 1] = fileList[i];
                    }
                    temp[0] = new Item(null, "Back", R.drawable.back_arrow);
                    fileList = temp;
                }
                adapter = new FolderAdapter(mContext, fileList, mListSongs);
            } catch (Exception ex) {

            }
        } else {
            Log.e(TAG, "path does not exist");
        }
    }

    private void loadFileList() {
        int hasWriteContactsPermission = ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_ASK_PERMISSIONS);
                return;
            }
        }
        loadFileAfter();


    }

    private int getFile(String dirPath) {
        File f = new File(dirPath);
        int numberFile = 0;

        for (int i = 0; i < mListSongs.size(); i++) {
            if (mListSongs.get(i).getPath().contains(dirPath)) {
                numberFile++;
            }
        }
        return numberFile;
    }

    private boolean checkExistInSongList(File file) {
        String path = file.getAbsolutePath();
        for (Song song : mListSongs) {
            if (song.getPath().equals(file.getAbsolutePath()))
                return true;
        }
        return false;
    }

    private int getInSongList(File f) {
        int index = -1;
        if (f != null && f.exists()) {
            for (int i = 0; i < mListSongs.size(); i++) {
                if (mListSongs.get(i).getPath().equals(f.getAbsolutePath())) {
                    return i;
                }
            }
        }
        return index;
    }

    private boolean checkPathExistInSongList(File file) {
        String path = file.getAbsolutePath();
        for (Song song : mListSongs) {
            if (song.getPath().contains(path))
                return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadFileAfter();
                } else {
                    // Permission Denied
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
