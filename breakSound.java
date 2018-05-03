
import java.net.*;
import java.applet.Applet.*;
import java.applet.*;
import java.io.*;

// Play a .wav file contained in the current directory 

class breakSound

{
  // Note when the application exits the sound will stop. 
  //      Hence the sleep 
  //      This is not required if the application continues 

  public static void main(  )
  {
    PlayWaveSound ps = new PlayWaveSound( "break.wav" );
    ps.play();
    
    


    
  }
}

class PlayWaveSound
{
  private AudioClip clip;

  public PlayWaveSound( String soundFile )
  {
    try
    {
      File file = new File( soundFile );
      URI uri = file.toURI();
      URL url = uri.toURL();
      clip = Applet.newAudioClip( url );
    } catch ( Exception e )
    {
      System.out.printf( "Did not work <%s>\n", soundFile );
    }
  }

  public void play()
  {
    clip.play();
  }
}