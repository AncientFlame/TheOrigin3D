/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mygame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.ViewPort;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;




/**
 *
 * @author Lorenzo
 */
public class OptionGUIController extends AbstractAppState implements ScreenController{
    
    private boolean music;
    private boolean sfx;
    private boolean audio3D;
    private NiftyJmeDisplay nifty;
    private ViewPort viewPort;
    private SimpleApplication app;
    

    OptionGUIController(AppStateManager stateManager, SimpleApplication app, ViewPort port) {
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
    
    public void Music(){
        music = !music;
    }
    public void SFX(){
        sfx = !sfx;
    }
    public void Audio3D(){
        audio3D = !audio3D;
    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
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
