import javax.sound.sampled.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class WAVPlayer {
    private File audioFile;
    private ByteBuffer byteBuffer;
    private byte[] header = new byte[44];

    private boolean local;

    private SourceDataLine line;

    /**
     * Used as a constructor when playing a local file.
     * @param audioFile The file to fetch the header and play the data from.
     * @throws IOException  Throws IOException if the source file is smaller than the required 44 bytes to contain a full header
     */
    WAVPlayer(File audioFile) throws IOException {
        this.audioFile = audioFile;
        DataInputStream inputStream = new DataInputStream(new FileInputStream(audioFile));
        if(audioFile.length() < 44) {
            throw new IOException("File size must be large enough to contain a header");    // Throw IOException if the file
                                                                                            // is smaller than 44 bytes.
        }
        for(int i = 0; i < 44; i++) {
            header[i] = (byte) inputStream.readUnsignedByte();                              // Read the audio file in chunks of 8 bits.
        }
        byteBuffer = ByteBuffer.wrap(header);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);                                          // Set the ByteBuffer to use Little Endian
                                                                                            // since Java uses big endian for some reason.
        local = true;
        init();
    }

    /**
     * Used as a constructor if the player doesn't have access to the source file. i.e. if this class is used to stream audio.
     * @param header The first 44 bytes of a source file
     * @throws IOException Throws if the header doesn't contain 44 bytes.
     */
    WAVPlayer(byte[] header) throws IOException {
        if(header.length == 44) {
            this.header = header;
            byteBuffer = ByteBuffer.wrap(header);
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN);                                      // Set the ByteBuffer to use Little Endian
                                                                                            // since Java uses big endian for some reason.
            init();
            local = false;
        } else {
            throw new IOException("File size must be exactly 44 bytes.");    // Throw IOException if the file
            // is smaller than 44 bytes.
        }
    }

    /**
     * Initialize the audio line and start it up.
     */
    private void init() {
        AudioFormat audioFormat = new AudioFormat(getSampleRate(), getBitsPerSample(), getChannels(), true, false);
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
        try {
            line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(audioFormat, 4096);
            line.start();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    /**
     * Play the local file specified in the constructor.
     * @throws FileNotFoundException
     */
    void playLocal() throws FileNotFoundException {
        if(local) {
            DataInputStream inputStream = new DataInputStream(new FileInputStream(audioFile));
            int firstInput;
            int secondInput;
            int count = 0;
            try {
                while(((firstInput = inputStream.readUnsignedByte()) != -1) && ((secondInput = inputStream.readUnsignedByte()) != -1)) {
                    if(count > 44) {
                        byte[] buffer = new byte[2];
                        buffer[0] = (byte)(firstInput);
                        buffer[1] = (byte)(secondInput);
                        play(buffer);
                    }
                    count++;
                }
            } catch (IOException e) {
                // Throws EOFException, not sure why since it shouldn't.
            }
        }
    }

    /**
     * Play the specified byte array.
     * @param buffer The bytes to play.
     */
    public void play(byte[] buffer) {
        line.write(buffer, 0, buffer.length);
    }

    /**
     * Close the DataLine
     */
    public void close() {
        line.drain();
        line.stop();
        line.close();
    }

    /**
     * Gets the audio format
     * @return The audio format
     */
    int getAudioFormat() {
        return getShortIndex(20);
    }

    /**
     * Gets the amount of channels
     * @return The amount of channels
     */
    int getChannels() {
        return getShortIndex(22);
    }

    /**
     * Gets the sample rate
     * @return The sample rate
     */
    int getSampleRate() {
        return getIntIndex(24);
    }

    /**
     * Gets the amount of bits per sample
     * @return The amount of bits per sample
     */
    int getBitsPerSample() {
        return getShortIndex(34);
    }

    /**
     * Gets two bytes, starting at the specified index.
     * @param index Index to grab the two bytes from.
     * @return The two bytes.
     */
    private int getShortIndex(int index) {
        return byteBuffer.getShort(index);
    }

    /**
     * Gets four bytes, starting at the specified index.
     * @param index Index to grab the four bytes from.
     * @return The four bytes.
     */
    private int getIntIndex(int index) {
        return byteBuffer.getInt(index);
    }

}
