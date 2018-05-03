
import java.net.*;
import java.applet.Applet.*;
import java.applet.*;
import java.io.*;
import java.util.concurrent.ThreadLocalRandom;


// Play a .wav file contained in the current directory 

class Sound
{
    public static  int n = ThreadLocalRandom.current().nextInt(1, 10 + 1);
    public static String a = "";
    // Note when the application exits the sound will stop. 
    //      Hence the sleep 
    //      This is not required if the application continues 

    public static void main( String args[] )
    {
        if (n == 1 || n == 3 || n == 5 || n == 7 || n == 9   ){
            a = "1";
        }
        if ( n == 2 || n == 4 || n == 6 || n == 8 || n == 10){
            a = "2";
        }
        PlayWaveSound ps = new PlayWaveSound( a+".wav" );
        
        ps.play();

        try { Thread.sleep( 4000 ); } catch ( Exception err ) { }
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