package media.musicplayer.songs.mp3player.audio.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import media.musicplayer.songs.mp3player.audio.model.EqualizerInfo;
import media.musicplayer.songs.mp3player.audio.model.Playlist;
import media.musicplayer.songs.mp3player.audio.model.Song;
import media.musicplayer.songs.mp3player.audio.utils.Constants;

public class SQLiteDataController extends SQLiteOpenHelper {

    // BÃ¡ÂºÂ£ng nhÃƒÂ¢n viÃƒÂªn
    public static final String tb_Employee = "Employee";

    private static String DB_PATH = "/data/data/com.example.android.uamp/"
            + "databases/";
    private static String DB_NAME = Constants.MUSIC_PLAYER;

    // --------------------Table Folder--------------------------------
    private static String TB_FOLDER = "folder";
    private static String FOLDER_ID = "id_folder";
    private static String FOLDER_NAME = "foldername";

    // ----------------------Table File-------------------------------------
    private final static String TB_FILE = "file";
    private final static String FILE_ID = "id_file";
    private final static String FILE_IDFOLDER = "idfolder";
    private final static String FILE_NAME = "songname";
    private final static String FILE_SONGALBUM = "songalbum";
    private final static String FILE_SONGURI = "songuri";
    private final static String FILE_SONGARTIST = "songartist";
    private final static String FILE_ALBUMART = "songalbumart";
    private final static String FILE_FAVORITE = "songfavorite";
    // ----------------------Table Equalizer-------------------------------------
    private final static String EQUALIZER_PRESETS_TABLE = "equalizer";
    public static final String EQ_ID = "_id";
    public static final String EQ_50_HZ = "eq_50_hz";
    public static final String EQ_130_HZ = "eq_130_hz";
    public static final String EQ_320_HZ = "eq_320_hz";
    public static final String EQ_800_HZ = "eq_800_hz";
    public static final String EQ_2000_HZ = "eq_2000_hz";
    public static final String VIRTUALIZER = "eq_virtualizer";
    public static final String BASS_BOOST = "eq_bass_boost";
    public static final String PRESET_NAME = "presetname";

    private SQLiteDatabase database;
    private final Context mContext;
    public static SQLiteDataController mInstance;
    private static final int DATABASE_VERSION = 8;

    public SQLiteDataController(Context con) {
        super(con, DB_NAME, null, DATABASE_VERSION);
        this.mContext = con;

    }

    public static SQLiteDataController getInstance(Context ctx) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (mInstance == null) {
            mInstance = new SQLiteDataController(ctx.getApplicationContext());
        }
        return mInstance;
    }

    /**
     * copy database from assets to the device if not existed
     *
     * @return true if not exist and create database success
     * @throws IOException
     */
    public boolean isCreatedDatabase() throws IOException {
        // Default lÃƒÂ  Ã„â€˜ÃƒÂ£ cÃƒÂ³ DB
        boolean result = true;
        // NÃ¡ÂºÂ¿u chÃ†Â°a tÃ¡Â»â€œn tÃ¡ÂºÂ¡i DB
        // thÃƒÂ¬ copy tÃ¡Â»Â« Asses
        // vÃƒÂ o Data
        if (!checkExistDataBase()) {
            this.getReadableDatabase();
            try {
                copyDataBase();
                result = false;
            } catch (Exception e) {
                throw new Error("Error copying database");
            }
        }
        String upgradeQuery = "ALTER TABLE " + TB_FILE + " ADD COLUMN " + FILE_FAVORITE + " INTEGER";
        if (!checkColumnExit(getDatabase(),TB_FILE, FILE_FAVORITE))
            getDatabase().execSQL(upgradeQuery);
        return result;
    }
//
//    public void deleteAllData() {
//
//        deleteData_From_Table(TB_FOLDER);
//        deleteData_From_Table(TB_FILE);
//
//    }

    /**
     * check whether database exist on the device?
     *
     * @return true if existed
     */

    private boolean checkExistDataBase() {
        DB_PATH = "/data/data/" + mContext.getPackageName() + "/"
                + "databases/";
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("recreateDB", mContext.MODE_PRIVATE);
        boolean isCreate = sharedPreferences.getBoolean("isCreate", false);
        try {
            String myPath = DB_PATH + DB_NAME;
            File fileDB = new File(myPath);

            if (fileDB.exists() && isCreate) {
                return true;
            } else {
                sharedPreferences.edit().putBoolean("isCreate", true).commit();
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * copy database from assets folder to the device
     *
     * @throws IOException
     */
    private void copyDataBase() throws IOException {
        InputStream myInput = mContext.getAssets().open(DB_NAME);
        OutputStream myOutput = new FileOutputStream(DB_PATH + DB_NAME);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    /**
     * delete database file
     *
     * @return
     */
    public boolean deleteDatabase() {
        File file = new File(DB_PATH + DB_NAME);
        return file.delete();
    }

    /**
     * open database
     *
     * @throws SQLException
     */
//    public void openDataBase() throws SQLException {
//        database = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null,
//                SQLiteDatabase.OPEN_READWRITE);
//    }
    @Override
    protected void finalize() {
        try {
            getDatabase().close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
//    @Override
//    public synchronized void close() {
//        if (database != null)
//            database.close();
//        super.close();
//    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // do nothing
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // do nothing
//       if(oldVersion == 8){
//           String upgradeQuery = "ALTER TABLE file ADD COLUMN "+FILE_FAVORITE+" INTEGER";
//           db.execSQL(upgradeQuery);
//       }
    }

    //
//    public int deleteData_From_Table(String tbName) {
//
//        int result = 0;
//        try {
//            getDatabase().beginTransaction();
//            result = getDatabase().delete(tbName, null, null);
//            if (result >= 0) {
//                getDatabase().setTransactionSuccessful();
//            }
//        } catch (Exception e) {
//            getDatabase().endTransaction();
//            //close();
//        } finally {
//            getDatabase().endTransaction();
//            //close();
//        }
//
//        return result;
//    }
//
    public int deletefolder(int idfolder) {

        int result = 0;
        try {
            //openDataBase();
            getDatabase().beginTransaction();
            result = getDatabase().delete(TB_FOLDER, FOLDER_ID + "=" + idfolder,
                    null);
            if (result >= 0) {
                getDatabase().setTransactionSuccessful();
            }
        } catch (Exception e) {
            //getDatabase().endTransaction();
            // close();
        } finally {
            getDatabase().endTransaction();
            // close();
        }

        return result;
    }

    public int deletefileById(int idfolder) {

        int result = 0;
        try {
//            openDataBase();
            getDatabase().beginTransaction();
            result = getDatabase().delete(TB_FILE, FILE_ID + "=" + idfolder, null);
            if (result >= 0) {
                getDatabase().setTransactionSuccessful();
            }
        } catch (Exception e) {
            //getDatabase().endTransaction();
        } finally {
            getDatabase().endTransaction();
        }

        return result;
    }

    public int deletefileByIdFolder(int idfolder) {

        int result = 0;
        try {
//            openDataBase();
            getDatabase().beginTransaction();
            result = getDatabase().delete(TB_FILE, FILE_IDFOLDER + "=" + idfolder,
                    null);
            if (result >= 0) {
                getDatabase().setTransactionSuccessful();
            }
        } catch (Exception e) {
            //getDatabase().endTransaction();
            //close();
        } finally {
            getDatabase().endTransaction();
            //close();
        }

        return result;
    }

    public ArrayList<Playlist> getAllFolder() {

        ArrayList<Playlist> rs = new ArrayList<Playlist>();

        try {
            // Má»Ÿ káº¿t ná»‘i
            //openDataBase();

            String[] columns = {"*"};
            // Truy váº¥n
            Cursor cursor = getDatabase().query(TB_FOLDER, columns, null, null,
                    null, null, null);
            while (cursor.moveToNext()) {
                Playlist item = new Playlist();
                // String path_image = cursor.getString(1);
                // item.setImageBitmap(BitmapFactory.decodeFile(path_image,opt));
                item.setId(cursor.getInt(0));
                item.setPlaylistName(cursor.getString(1));
                // item.setSelected(true);

                rs.add(item);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //close();
        }
        return rs;
    }

    // Adding new contact
    public void addNewFolderTable(Playlist folder) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FOLDER_ID, folder.getId()); // folder Name
        values.put(FOLDER_NAME, folder.getPlaylistName()); // folder Phone Number

        // Inserting Row
        db.insert(TB_FOLDER, null, values);
        db.close(); // Closing database connection
    }

    // Adding new contact
    public void addNewFileTable(Playlist folder) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FOLDER_ID, folder.getId()); // folder Name
        values.put(FOLDER_NAME, folder.getPlaylistName()); // folder Phone Number
        // Number
        // Number

        // Inserting Row
        db.insert(TB_FOLDER, null, values);
        db.close(); // Closing database connection
    }

    // Getting contacts Count
    public int getFoldersCount() {
        String countQuery = "SELECT  * FROM " + TB_FOLDER;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

    public int getFileCountByFolderId(int id) {
        int countFile = 0;
        try {
            // Má»Ÿ káº¿t ná»‘i
            //openDataBase();

            String[] columns = {"*"};
            // Truy váº¥n
            Cursor cursor = getDatabase().query(TB_FILE, columns, FILE_IDFOLDER
                            + " = ?", new String[]{String.valueOf(id)}, null, null,
                    null);

            countFile = cursor.getCount();
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //close();
        }
        return countFile;
    }


    /**
     * get border by id
     *
     * @return
     */
    public ArrayList<Song> getAllFileByID(int id) {
        ArrayList<Song> rs = new ArrayList<Song>();

        try {
            // Má»Ÿ káº¿t ná»‘i
            //openDataBase();

            String[] columns = {"*"};
            // Truy váº¥n
            Cursor cursor = getDatabase().query(TB_FILE, columns, FILE_IDFOLDER
                            + " = ?", new String[]{String.valueOf(id)}, null, null,
                    null);

            // Ä�á»�c tá»«ng dÃ²ng
            while (cursor.moveToNext()) {
                Song file = new Song();
                file.setIdSong(cursor.getInt(0));
                file.setIdPlaylist(cursor.getInt(1));
                file.setSongName(cursor.getString(2));
                file.setPath(cursor.getString(3));
                file.setAlbum(cursor.getString(4));
                file.setArtist(cursor.getString(5));
                file.setAlbum_art_uri(cursor.getString(6));
                File song = new File(file.getPath());
                if (song.exists())
                    rs.add(file);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //// close();
        }
        return rs;
    }

    // insert file
    public long insertFilePlaylist(Song file) {
        long id = -1;
        try {
            //openDataBase();

            ContentValues content = new ContentValues();
            content.put(FILE_IDFOLDER, file.getIdPlaylist());
            content.put(FILE_NAME, file.getSongName());
            content.put(FILE_SONGURI, file.getPath());
            content.put(FILE_SONGALBUM, file.getAlbum());
            content.put(FILE_SONGARTIST, file.getArtist());
            content.put(FILE_ALBUMART, file.getAlbum_art_uriDB());

            id = getDatabase().insert(TB_FILE, null, content);
            Log.e("insertFolder : ", "" + id);
            if (id != -1) {
                //// close();
                return id;
            }

        } catch (Exception e) {
            Log.w("insertFolder shadow", "Error");
        } finally {
            //close();
        }
        return -1;
    }

    // insert file
    public long insertListFilePlaylist(ArrayList<Song> files) {
        long id = -1;
        try {
            //openDataBase();
            for (Song file : files) {

                ContentValues content = new ContentValues();
                content.put(FILE_IDFOLDER, file.getIdPlaylist());
                content.put(FILE_NAME, file.getSongName());
                content.put(FILE_SONGURI, file.getPath());
                content.put(FILE_SONGALBUM, file.getAlbum());
                content.put(FILE_SONGARTIST, file.getArtist());
                content.put(FILE_ALBUMART, file.getAlbum_art_uriDB());
                id = getDatabase().insert(TB_FILE, null, content);
            }
            Log.e("insertFolder : ", "" + id);
            if (id != -1) {
                //// close();
                return id;
            }

        } catch (Exception e) {
            Log.w("insertFolder shadow", "Error");
        } finally {
            //// close();
        }
        return -1;
    }

    // delete file
    public int deleteItem(Song file) {
        int result = 0;
        try {
            //openDataBase();
            getDatabase().beginTransaction();
            result = getDatabase().delete(TB_FILE, FILE_ID + " = " + file.getIdSong(),
                    null);
            if (result >= 0) {
                getDatabase().setTransactionSuccessful();
            }
        } catch (Exception e) {
            // getDatabase().endTransaction();
            //// close();
        } finally {
            getDatabase().endTransaction();
            //// close();
        }

        return result;
    }

    public long insertFolder(Playlist folder) {
        long id = -1;
        try {
            //  openDataBase();

            ContentValues content = new ContentValues();

            content.put(FOLDER_NAME, folder.getPlaylistName());

            id = getDatabase().insert(TB_FOLDER, null, content);
            Log.e("insertFolder : ", "" + id);
            if (id != -1) {
                //// close();
                return id;
            }

        } catch (Exception e) {
            Log.w("insertFolder shadow", "Error");
        } finally {
            //// close();
        }
        return id;
    }

    public long updateFolder(Playlist folder) {
        long id = -1;
        try {
            //openDataBase();

            ContentValues content = new ContentValues();

            content.put(FOLDER_NAME, folder.getPlaylistName());

            Log.e("insertFolder : ", "" + folder.getId());
            id = getDatabase().update(TB_FOLDER, content,
                    FOLDER_ID + " = " + folder.getId(), null);
            if (id != -1) {
                //close();
                return id;
            }
        } catch (Exception e) {
            Log.w("insertFolder shadow", "Error");
        } finally {
            //close();
        }
        return id;
    }


    /**
     * Adds a new EQ preset to the table.
     */
    public void addNewEQPreset(String presetName,
                               int fiftyHertz,
                               int oneThirtyHertz,
                               int threeTwentyHertz,
                               int eightHundredHertz,
                               int twoKilohertz,
                               short virtualizer,
                               short bassBoost) {

        ContentValues values = new ContentValues();
        values.put(PRESET_NAME, presetName);
        values.put(EQ_50_HZ, fiftyHertz);
        values.put(EQ_130_HZ, oneThirtyHertz);
        values.put(EQ_320_HZ, threeTwentyHertz);
        values.put(EQ_800_HZ, eightHundredHertz);
        values.put(EQ_2000_HZ, twoKilohertz);
        values.put(VIRTUALIZER, virtualizer);
        values.put(BASS_BOOST, bassBoost);

        getDatabase().insert(EQUALIZER_PRESETS_TABLE, null, values);

    }

    /**
     * Returns a writable instance of the database. Provides an additional
     * null check for additional stability.
     */
    private synchronized SQLiteDatabase getDatabase() {
        if (database == null)
            database = getWritableDatabase();
        return database;
    }

    private boolean checkColumnExit(SQLiteDatabase database,String tablename, String columnName) {
        String query = "select " + columnName + " from " + tablename;
        try {
            Cursor cursor = database.rawQuery(query, null);
            int index = cursor.getColumnIndex(columnName);
            cursor.close();
            if (index < 0)
                return false;
            return true;
        }catch (Exception ex){
            return false;
        }

    }

    /**
     * get border by id
     *
     * @return
     */
    public int[] getEQValueById(int id) {
        int[] eqValues = new int[7];

        try {
            // Má»Ÿ káº¿t ná»‘i
//            openDataBase();

            String[] columns = {"*"};
            // Truy váº¥n
            Cursor cursor = getDatabase().query(EQUALIZER_PRESETS_TABLE, columns, EQ_ID
                            + " = ?", new String[]{String.valueOf(id)}, null, null,
                    null);

            if (cursor != null && cursor.getCount() != 0) {
                eqValues[1] = cursor.getInt(cursor.getColumnIndex(EQ_50_HZ));
                eqValues[2] = cursor.getInt(cursor.getColumnIndex(EQ_130_HZ));
                eqValues[3] = cursor.getInt(cursor.getColumnIndex(EQ_320_HZ));
                eqValues[4] = cursor.getInt(cursor.getColumnIndex(EQ_800_HZ));
                eqValues[5] = cursor.getInt(cursor.getColumnIndex(EQ_2000_HZ));
                eqValues[6] = cursor.getInt(cursor.getColumnIndex(VIRTUALIZER));
                eqValues[7] = cursor.getInt(cursor.getColumnIndex(BASS_BOOST));

                cursor.close();

            } else {
                eqValues[1] = 16;
                eqValues[2] = 16;
                eqValues[3] = 16;
                eqValues[4] = 16;
                eqValues[5] = 16;
                eqValues[6] = 0;
                eqValues[7] = 0;

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // close();
        }
        return eqValues;

    }

    /**
     * Returns a cursor with all EQ presets in the table.
     */
    public Cursor getAllEQPresets() {
//        openDataBase();
        String query = "SELECT * FROM " + EQUALIZER_PRESETS_TABLE;
        return getDatabase().rawQuery(query, null);

    }

    /**
     * Returns a cursor with all EQ presets in the table.
     */
    public ArrayList<EqualizerInfo> getAllListEQPresets() {
//        openDataBase();
        ArrayList<EqualizerInfo> arrayList = new ArrayList<>();
        String query = "SELECT * FROM " + EQUALIZER_PRESETS_TABLE;
        Cursor cursor = getDatabase().rawQuery(query, null);
        while (cursor.moveToNext()) {
            EqualizerInfo equalizerInfo = new EqualizerInfo();
            equalizerInfo.setNameEqualizer(cursor.getString(cursor.getColumnIndex(SQLiteDataController.PRESET_NAME)));
            equalizerInfo.setBand1Value(cursor.getInt(cursor.getColumnIndex(SQLiteDataController.EQ_50_HZ)));
            equalizerInfo.setBand2Value(cursor.getInt(cursor.getColumnIndex(SQLiteDataController.EQ_130_HZ)));
            equalizerInfo.setBand3Value(cursor.getInt(cursor.getColumnIndex(SQLiteDataController.EQ_320_HZ)));
            equalizerInfo.setBand4Value(cursor.getInt(cursor.getColumnIndex(SQLiteDataController.EQ_800_HZ)));
            equalizerInfo.setBand5Value(cursor.getInt(cursor.getColumnIndex(SQLiteDataController.EQ_2000_HZ)));
            equalizerInfo.setVirtualValue(cursor.getShort(cursor.getColumnIndex(SQLiteDataController.VIRTUALIZER)));
            equalizerInfo.setBassValue(cursor.getShort(cursor.getColumnIndex(SQLiteDataController.BASS_BOOST)));

            arrayList.add(equalizerInfo);
        }
        cursor.close();
        return arrayList;
    }

    /**
     * Saves premade equalizer presets to the database.
     */
    public void saveEQPresets() {
        Cursor eqPresetsCursor = getAllEQPresets();
        //Check if this is the first startup (eqPresetsCursor.getCount() will be 0).
        if (eqPresetsCursor != null && eqPresetsCursor.getCount() == 0) {
            addNewEQPreset("Flat", 16, 16, 16, 16, 16, (short) 0, (short) 0);
            addNewEQPreset("Bass Only", 31, 31, 31, 0, 0, (short) 0, (short) 0);
            addNewEQPreset("Treble Only", 0, 0, 0, 31, 31, (short) 0, (short) 0);
            addNewEQPreset("Rock", 16, 18, 16, 17, 19, (short) 0, (short) 0);
            addNewEQPreset("Grunge", 13, 16, 18, 19, 20, (short) 0, (short) 0);
            addNewEQPreset("Metal", 12, 16, 16, 16, 20, (short) 0, (short) 0);
            addNewEQPreset("Dance", 14, 18, 20, 17, 16, (short) 0, (short) 0);
            addNewEQPreset("Country", 16, 16, 18, 20, 17, (short) 0, (short) 0);
            addNewEQPreset("Jazz", 16, 16, 18, 18, 18, (short) 0, (short) 0);
            addNewEQPreset("Speech", 14, 16, 17, 14, 13, (short) 0, (short) 0);
            addNewEQPreset("Classical", 16, 18, 18, 16, 16, (short) 0, (short) 0);
            addNewEQPreset("Blues", 16, 18, 19, 20, 17, (short) 0, (short) 0);
            addNewEQPreset("Opera", 16, 17, 19, 20, 16, (short) 0, (short) 0);
            addNewEQPreset("Swing", 15, 16, 18, 20, 18, (short) 0, (short) 0);
            addNewEQPreset("Acoustic", 17, 18, 16, 19, 17, (short) 0, (short) 0);
            addNewEQPreset("New Age", 16, 19, 15, 18, 16, (short) 0, (short) 0);

        }

        //Close the cursor.
        if (eqPresetsCursor != null)

            eqPresetsCursor.close();

    }

}
