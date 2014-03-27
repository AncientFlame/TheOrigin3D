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
import mygame.Settings;


public class StartGUIController extends AbstractAppState implements ScreenController{
    private NiftyJmeDisplay nifty;

    private ViewPort viewPort;
    

    private MapSelectionController mapController;
    private OptionGUIController optionController;
    //private Screen screen;
    private SimpleApplication app;
    public boolean menu=true;
    public Main appl;
    public Node rootNode2;
    public FlyByCamera flycam;
    

    public StartGUIController(AppStateManager stateManager, 
                        AssetManager man,
                        SimpleApplication app, 
                        ViewPort port,
                        Main application,
                        Node rootN,
                        FlyByCamera fly) {
        rootNode2=rootN;
        flycam=fly;
        appl=application;
        super.initialize(stateManager, app);
        this.app=(SimpleApplication)app;
        viewPort = port;
        optionController = new OptionGUIController(stateManager, app, viewPort);
        optionController.setNifty(nifty);
        mapController = new MapSelectionController(stateManager, man ,app, viewPort, application, rootN, fly);
        mapController.setNifty(nifty);
        
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
      
    }
    public void option(int x, int y){
        Nifty niftyOption = nifty.getNifty();
        niftyOption.fromXml("Interface/option.xml", "start", optionController);
        niftyOption.enableAutoScaling(Settings.system.getWidth(), Settings.system.getHeight());
        viewPort.addProcessor(nifty);
    }
    public void quitGame(int x, int y){
        nifty.cleanup();
        app.stop();
    }
    
   
    public void startGame(int x, int y){
        viewPort.removeProcessor(nifty);
        
        Nifty niftyOption = nifty.getNifty();
        niftyOption.fromXml("Interface/mapSelection.xml", "start", mapController);
        niftyOption.enableAutoScaling(Settings.system.getWidth(), Settings.system.getHeight());
        mapController.setNifty(nifty);
        viewPort.addProcessor(nifty);
    }

    public void bind(Nifty nifty, Screen screen) {
        
    }

    public void setNifty(NiftyJmeDisplay niftyDisplay) {
         nifty = niftyDisplay;
    }
}
