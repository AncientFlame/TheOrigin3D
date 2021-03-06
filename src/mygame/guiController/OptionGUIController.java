package mygame.guiController;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



import mygame.guiController.StartGUIController;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.ViewPort;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.dropdown.DropDownControl;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;




/**
 *
 * @author Lorenzo
 */
public class OptionGUIController extends AbstractAppState implements ScreenController{
    DropDownControl dropDown1 ;
    Screen screen;
    private boolean music;
    private boolean sfx;
    private boolean audio3D;
    private NiftyJmeDisplay nifty;
    private ViewPort viewPort;
    private SimpleApplication app;
    private StartGUIController startController;

    OptionGUIController(AppStateManager stateManager, SimpleApplication app, ViewPort port) {
        
        
        //dropDown1 = screen.findControl("dropDown1", DropDownControl.class);
        super.initialize(stateManager, app);
        this.app=(SimpleApplication)app;
        viewPort = port;
    }    
    public void setNifty(NiftyJmeDisplay niftyDisplay) {
         nifty = niftyDisplay;
        
    }
 
    
    public void onStartScreen() {
        
    }

    public void onEndScreen() {
        
    }
    
    public void Music(int x, int y){
        music = !music;
    }
    public void SFX(int x, int y){
        sfx = !sfx;
        
    }
    public void Audio3D(int x, int y){
        audio3D = !audio3D;
        mygame.Settings.system.setStereo3D(audio3D);
    }
    public void quit(int x, int y){
        viewPort.removeProcessor(nifty);
       
    }
    
    public void initialize(AppStateManager stateManager, SimpleApplication app) {
        super.initialize(stateManager, app);
        this.app=(SimpleApplication)app;
    }
 
    @Override
    public void update(float tpf) { 
      /** jME update loop! */ 
    }
    
    public void bind(Nifty nifty, Screen screen) {
        
    }

    

}
