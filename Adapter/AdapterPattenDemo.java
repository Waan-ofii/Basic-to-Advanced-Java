// Adapter Design Pattern - Single File Example

// Target Interface
interface MediaPlayer {
    void play(String audioType, String fileName);
}

// Adaptee Interface
interface AdvancedMediaPlayer {
    void playVlc(String fileName);
    void playMp4(String fileName);
}

// Concrete Adaptee - VLC
class VlcPlayer implements AdvancedMediaPlayer {
    public void playVlc(String fileName) {
        System.out.println("Playing VLC file: " + fileName);
    }
    public void playMp4(String fileName) {}
}

// Concrete Adaptee - MP4
class Mp4Player implements AdvancedMediaPlayer {
    public void playVlc(String fileName) {}
    public void playMp4(String fileName) {
        System.out.println("Playing MP4 file: " + fileName);
    }
}

// Adapter Class
class MediaAdapter implements MediaPlayer {
    AdvancedMediaPlayer player;

    public MediaAdapter(String audioType) {
        if (audioType.equalsIgnoreCase("vlc"))
            player = new VlcPlayer();
        else if (audioType.equalsIgnoreCase("mp4"))
            player = new Mp4Player();
    }

    public void play(String audioType, String fileName) {
        if (audioType.equalsIgnoreCase("vlc"))
            player.playVlc(fileName);
        else if (audioType.equalsIgnoreCase("mp4"))
            player.playMp4(fileName);
    }
}

// Client Class
class AudioPlayer implements MediaPlayer {
    MediaAdapter adapter;

    public void play(String audioType, String fileName) {
        if (audioType.equalsIgnoreCase("mp3")) {
            System.out.println("Playing MP3 file: " + fileName);
        } else if (audioType.equalsIgnoreCase("vlc") || audioType.equalsIgnoreCase("mp4")) {
            adapter = new MediaAdapter(audioType);
            adapter.play(audioType, fileName);
        } else {
            System.out.println("Unsupported format: " + audioType);
        }
    }
}

// Main Class
public class AdapterPatternDemo {
    public static void main(String[] args) {
        AudioPlayer player = new AudioPlayer();

        player.play("mp3", "song.mp3");
        player.play("mp4", "video.mp4");
        player.play("vlc", "movie.vlc");
        player.play("avi", "file.avi");
    }
}