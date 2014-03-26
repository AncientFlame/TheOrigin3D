
package mygame;


import com.jme3.system.AppSettings;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author Lorenzo
 */
public final class Settings {
    public static AppSettings system = new AppSettings(true);
    private static String Title = "The Origin 3D";
   
    
    Settings() {
        
        set_default_settings();
    }
    
    /**
     * impostazioni di default
    */
    public void set_default_settings(){
      
        set_icon_image();
       
        system.setTitle(Title);
        system.setUseInput(true);
        system.setFrameRate(60);
        system.setStereo3D(false);
        system.setFullscreen(true);
        system.setResolution(1600, 900);
        //system.setSettingsDialogImage("Interface/SplashScreen.jpg");
        system.setSamples(0);
    }
    /**
     * imposta la risoluzione dello schermo
     * N.B: resettare l'applicazione dopo aver impostato questa opzione
    */
    public void set_resolution(int width, int height ){
        system.setResolution(width, height);
    }
    public float getResolutionHeight(){
        return system.getHeight();
    }
    public float getResolutionWidth(){
        return system.getWidth();
    }
    /**
     * Restituisce le impostazioni
    */
    AppSettings get_settings(){
        return system;
    }
    private void set_icon_image(){
        try {
            system.setIcons(new BufferedImage[]{
                    ImageIO.read(getClass().getResourceAsStream("/Interface/IconImage/IconImage256.jpg")),
                    ImageIO.read(getClass().getResourceAsStream("/Interface/IconImage/IconImage128.jpg")),
                    ImageIO.read(getClass().getResourceAsStream("/Interface/IconImage/IconImage32.jpg")),
                    ImageIO.read(getClass().getResourceAsStream("/Interface/IconImage/IconImage16.jpg")),
            });
        } catch (IOException e) {
            System.out.println(e);
        }
    }
    
}
