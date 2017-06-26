package inc.deszo.fuzzywinner.utils;

import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import java.io.IOException;
import java.io.InputStream;

public class SoundUtils {

  @SuppressWarnings("FieldCanBeLocal")
  private static final String SOUND_FILENAME = "sounds/gong.au";

  public static void playSound() throws IOException {

    ClassLoader classLoader = SoundUtils.class.getClassLoader();
    InputStream inputStream = classLoader.getResourceAsStream(SOUND_FILENAME);
    AudioStream audioStream = new AudioStream(inputStream);
    AudioPlayer.player.start(audioStream);
  }
}
