package media.musicplayer.songs.mp3player.audio.model;

import java.util.ArrayList;

/**
 * Created by hiennguyen on 4/7/16.
 */
public class TransitNextSong {
   private ArrayList<Song> listSong;
   public int posClick;
   public boolean isAddQueue;
//   public TransitPlaySong(ArrayList<Song> song, int posClick) {
//      this.song = song;
//      this.posClick = posClick;
//   }

   public TransitNextSong(ArrayList<Song> listSong, int posClick, boolean isAddQueue) {
      this.listSong = listSong;
      this.posClick = posClick;
      this.isAddQueue = isAddQueue;
   }

   public boolean isAddQueue() {
      return isAddQueue;
   }

   public void setIsAddQueue(boolean isAddQueue) {
      this.isAddQueue = isAddQueue;
   }

   public int getPosClick() {
      return posClick;
   }

   public void setPosClick(int posClick) {
      this.posClick = posClick;
   }

   public ArrayList<Song> getListSong() {
      return listSong;
   }

   public void setListSong(ArrayList<Song> song) {
      this.listSong = song;
   }
}
