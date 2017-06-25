import java.applet.Applet;
import java.applet.AudioClip;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;

/* This class is responsible for the handling of sound effects. At the moment
 * it uses the applet AudioClip interface, but it will be replaced by the more
 * advanced sound API soon. */

public class GameSounds {

	public static final String SOUND_ASTEROID = "asteroid.wav";
	public static final String SOUND_MUSIC = "bg_music.wav";
	public static final String SOUND_COLLISION = "collision.wav";
	public static final String SOUND_LASER = "laser.wav";
	public static final String SOUNDS[] = {
		SOUND_ASTEROID, SOUND_MUSIC,
		SOUND_COLLISION, SOUND_LASER
	};
	private static final String DIR = "sound/";

	// map containing all the available sounds
	public HashMap<String, AudioClip> sounds;
	// list containing sounds currently playing
	public LinkedList<AudioClip> playing;

	public GameSounds() {
		sounds = new HashMap<>();
		playing = new LinkedList<>();
		// load all the sounds
		for (String sound : SOUNDS) {
			AudioClip clip = null;
			try {
				clip = Applet.newAudioClip(new File(DIR + sound).toURI().toURL());
			} catch (Exception e) {}
			if (clip == null) {
				System.out.println("Warning: Could not load " + sound);
			} else {
				sounds.put(sound, clip);
			}
		}
	}

	public void playSound(String sound, boolean loop) {
		AudioClip clip = sounds.get(sound);
		if (clip != null) {
			clip.stop();
			if (loop) {
				clip.loop();
			} else {
				clip.play();
			}
			// yes, clips are not removed when they are done
			if (!playing.contains(clip)) {
				playing.add(clip);
			}
		}
	}

	public void stop() {
		for (AudioClip clip : playing) {
			clip.stop();
		}
	}

}
