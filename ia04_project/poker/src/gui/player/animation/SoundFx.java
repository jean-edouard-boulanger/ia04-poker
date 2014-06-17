package gui.player.animation;

import javafx.application.Application;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class SoundFx {
	public static void launchSound(Application app, String path)
	{
		try
		{
			String source = app.getClass().getResource(path).toURI().toString();
			Media media = null;
			media = new Media(source);
			MediaPlayer mediaPlayer = new MediaPlayer(media);
			mediaPlayer.play();
		}
		catch(Exception e) {
			System.out.println("[PlayerWindow] " + e.getMessage());
		}
	}
}
