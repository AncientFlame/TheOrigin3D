package mygame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioRenderer;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;


public class StartGUIController extends AbstractAppState implements ScreenController{
    private NiftyJmeDisplay nifty;

    private ViewPort viewPort;
    

    
    private OptionGUIController optionController;
    //private Screen screen;
    private SimpleApplication app;
    boolean menu=true;
    Main appl;
    Node rootNode2;
    FlyByCamera flycam;
    

    StartGUIController(AppStateManager stateManager, 
                        AssetManager man,
                        InputManager IOMan,
                        AudioRenderer rederer,
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
        viewPort.removeProcessor(nifty);
        
        Nifty niftyOption = nifty.getNifty();
        niftyOption.fromXml("Interface/option.xml", "start", optionController);
        
        viewPort.addProcessor(nifty);
        
    }
    public void quitGame(int x, int y){
        app.stop();
    }
    public void startGame(int x, int y){
        this.menu=false;  
        //inizializzazioni scena
       appl.thread[0]=appl.executor.submit(appl.InitScene);
       appl.thread[1]=appl.executor.submit(appl.InitPg);
       appl.thread[2]=appl.executor.submit(appl.InitKeys);
       appl.thread[3]=appl.executor.submit(appl.InitVectorMob);
//aspetta che i thread finiscano per attaccare gli spatial (se li attacchi nel thread c'è il rischio di crash)       
       while(!appl.thread[0].isDone() || !appl.thread[1].isDone() || !appl.thread[2].isDone() || !appl.thread[3].isDone()) {}
       
       rootNode2.attachChild(appl.scena.SceneModel);
       rootNode2.attachChild(appl.pg.model);
       flycam.setDragToRotate(false);
       appl.thread[0]=appl.thread[1]=appl.thread[2]=null;
       
       viewPort.removeProcessor(nifty);      
    }

    public void bind(Nifty nifty, Screen screen) {
        
    }

    public void setNifty(NiftyJmeDisplay niftyDisplay) {
         nifty = niftyDisplay;
    }
}
