import java.io.File;
import java.io.IOException;

public class TestHeader {
    public static void main(String[] args) throws IOException {
        WAVPlayer player = new WAVPlayer(new File("rhgroove.wav")); // Simple audio test file, 44100 sample rate,
        // 16 bits per audio sample, mono audio.

        System.out.println(player.getSampleRate());     // Should output 44100
        System.out.println(player.getAudioFormat());    // Should 100% output 1, anything else means the file is compressed
        System.out.println(player.getChannels());       // Should output 1, else the file has stereo sound.
        System.out.println(player.getBitsPerSample());  // Should output 16
    }
}
