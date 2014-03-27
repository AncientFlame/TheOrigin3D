/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mygame.guiController;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.input.FlyByCamera;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import mygame.Main;




/**
 *
 * @author Lorenzo
 */
public class MapSelectionController extends AbstractAppState implements ScreenController{
    
    private NiftyJmeDisplay nifty;
    private ViewPort viewPort;
    private SimpleApplication app;
    public Main appl;
    public Node rootNode2;
    public FlyByCamera flycam;
    public boolean menu=true;

    MapSelectionController(AppStateManager stateManager, 
                        AssetManager man,
                        SimpleApplication app, 
                        ViewPort port,
                        Main application,
                        Node rootN,
                        FlyByCamera fly) {
        super.initialize(stateManager, app);
        rootNode2=rootN;
        flycam=fly;
        appl=application;
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

    @SuppressWarnings("empty-statement")
    public void start(int x, int y){
       
        
       
    }

}
