import java.io.File;
import java.io.IOException;

public class TestLocalPlay {
    public static void main(String[] args) throws IOException {
        WAVPlayer player = new WAVPlayer(new File("rhgroove.wav"));
        player.playLocal();
    }
}
