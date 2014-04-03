package mygame.guiController;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.input.FlyByCamera;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.FogFilter;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import java.util.Vector;
import java.util.concurrent.Callable;
import mygame.GuiGame;
import mygame.Main;
import mygame.Player;
import mygame.Scene;
import mygame.Settings;


public class StartGUIController extends AbstractAppState implements ScreenController{
    private NiftyJmeDisplay nifty;

    private ViewPort guiViewPort; //per la gui
    private ViewPort viewPort; //per gli effetti grafici

    private MapSelectionController mapController;
    private OptionGUIController optionController;
    
    private AssetManager man;
    private SimpleApplication app;
    public boolean menu=true;
    public Main appl;
    public Node rootNode2;
    public Node guiNode2;
    public FlyByCamera flycam;
    public Hud hud;

    public StartGUIController(AppStateManager stateManager, 
                        AssetManager man,
                        SimpleApplication app, 
                        ViewPort port,
                        ViewPort viewPort,
                        Main application,
                        Node rootN,
                        FlyByCamera fly,
                        Node guiN) {
        rootNode2=rootN;
        flycam=fly;
        appl=application;
        super.initialize(stateManager, app);
        this.app=(SimpleApplication)app;
        guiViewPort = port;
        this.viewPort =viewPort;
        guiNode2=guiN;
        optionController = new OptionGUIController(stateManager, app, guiViewPort);
        optionController.setNifty(nifty);
        mapController = new MapSelectionController(stateManager, man ,app, guiViewPort, application, rootN, fly);
        mapController.setNifty(nifty);
        hud = new Hud(stateManager, app, port); 
        this.man = man; 
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
        guiViewPort.addProcessor(nifty);
    }
    public void quitGame(int x, int y){
        nifty.cleanup();
        app.stop();
    }
    
   
    @SuppressWarnings("empty-statement")
    public void startGame(int x, int y){
        guiViewPort.removeProcessor(nifty);
        this.menu=false;  
        
        
        
        
       //inizializzazioni scena
       appl.thread[0]=appl.executor.submit(InitScene);
       appl.thread[1]=appl.executor.submit(InitPg);
       appl.thread[2]=appl.executor.submit(InitKeys);
       appl.thread[3]=appl.executor.submit(InitVectorMob);
       appl.thread[4]=appl.executor.submit(initGameGUI);
       //aspetta che i thread finiscano per attaccare gli spatial (se li attacchi nel thread c'è il rischio di crash)       
       while(!appl.thread[0].isDone() || !appl.thread[1].isDone() || !appl.thread[2].isDone() || !appl.thread[3].isDone() || !appl.thread[4].isDone());
       
       rootNode2.attachChild(appl.scena.SceneModel);
       rootNode2.attachChild(appl.pg.model_node);
       guiNode2.attachChild(appl.GUIg.pointer);
       
       flycam.setDragToRotate(false);
       appl.thread[0]=appl.thread[1]=appl.thread[2]=appl.thread[3]=appl.thread[4]=null;
       appl.coord=appl.getInputManager().getCursorPosition();  
       appl.appoggio=new Vector2f();
       appl.appoggio.x=appl.coord.x; appl.appoggio.y=appl.coord.y;
       guiViewPort.removeProcessor(nifty);  
       
       Nifty Hud = nifty.getNifty();
       Hud.fromXml("Interface/HUD.xml", "start", hud);
       Hud.enableAutoScaling(Settings.system.getWidth(), Settings.system.getHeight());
       guiViewPort.addProcessor(nifty);
        
       /**
        * fa partire la gui di selezione mappe 
       */
/*
        Nifty niftyOption = nifty.getNifty();
        niftyOption.fromXml("Interface/mapSelection.xml", "start", mapController);
        niftyOption.enableAutoScaling(Settings.system.getWidth(), Settings.system.getHeight());
        mapController.setNifty(nifty);
        guiViewPort.addProcessor(nifty);
*/
    }
    
    private Callable InitKeys=new Callable() //thread che inizializza la mappa dei tasti
    {
       public Object call()
       {
           appl.getInputManager().addMapping("W",new KeyTrigger(KeyInput.KEY_W));
           appl.getInputManager().addMapping("S",new KeyTrigger(KeyInput.KEY_S));
           appl.getInputManager().addMapping("D",new KeyTrigger(KeyInput.KEY_D));
           appl.getInputManager().addMapping("A",new KeyTrigger(KeyInput.KEY_A));
           appl.getInputManager().addMapping("right",new MouseAxisTrigger(MouseInput.AXIS_X, false)); //movimento mouse verso destra
           appl.getInputManager().addMapping("left",new MouseAxisTrigger(MouseInput.AXIS_X, true)); //movimento mouse verso sinistra
           appl.getInputManager().addMapping("down",new MouseAxisTrigger(MouseInput.AXIS_Y, true)); //movimento mouse verso il basso
           appl.getInputManager().addMapping("up",new MouseAxisTrigger(MouseInput.AXIS_Y, false)); //movimento mouse verso l'alto
           appl.getInputManager().addMapping("fire",new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
           appl.getInputManager().addMapping("ricarica",new KeyTrigger(KeyInput.KEY_R));
           appl.getInputManager().addMapping("jump",new KeyTrigger(KeyInput.KEY_SPACE));
           appl.getInputManager().addListener(appl.PgMovement,"left","right","up","down");
           appl.getInputManager().addListener(appl.PgMovement2,"W","S","D","A","jump");
           appl.getInputManager().addListener(appl.gun_action,"fire");
           appl.getInputManager().addListener(appl.gun_action2,"ricarica");
           return null;
       }
    };
    
    private Callable InitScene=new Callable() //thread per la scena   
    {
        public Object call()
        {
           appl.scena=new Scene(appl.getAssetManager(),guiViewPort,appl.bullet,0); 
           initSky();
           
            /** Add fog to a scene */
            FilterPostProcessor fpp=new FilterPostProcessor(man);
            FogFilter fog=new FogFilter();
            fog.setFogColor(new ColorRGBA(0.1f, 0.1f, 0.1f, 1.0f));
            fog.setFogDistance(300);
            fog.setFogDensity(2.0f);
            fpp.addFilter(fog);
            viewPort.addProcessor(fpp);

           return null; 
        }
    };
    
    public Callable InitPg=new Callable() //thread per il pg
    {
      public Object call()
      {
        appl.pg=new Player(appl.getAssetManager(),appl.bullet,appl);  
        return null;
      }
    };
    
    public Callable InitVectorMob=new Callable() //thread per il vettore di mob
    {
      public Object call()
      {
        appl.mob=new Vector(0); 
        return null;
      }
    };
    
    public Callable initGameGUI=new Callable()
    {
      public Object call()  
      {
          appl.GUIg=new GuiGame(appl.getAssetManager());
          return null;
      }  
    };
    
    private void initSky(){
        Texture west = appl.getAssetManager().loadTexture("Textures/DarkStormy/DarkStormyRight2048.png");
        Texture east = appl.getAssetManager().loadTexture("Textures/DarkStormy/DarkStormyLeft2048.png");
        Texture north = appl.getAssetManager().loadTexture("Textures/DarkStormy/DarkStormyFront2048.png");
        Texture south = appl.getAssetManager().loadTexture("Textures/DarkStormy/DarkStormyBack2048.png");
        Texture up = appl.getAssetManager().loadTexture("Textures/DarkStormy/DarkStormyUp2048.png");
        Texture down = appl.getAssetManager().loadTexture("Textures/DarkStormy/DarkStormyDown2048.png");
                                                    
        Spatial sky = SkyFactory.createSky(appl.getAssetManager(), west, east, north, south, up, down, Vector3f.UNIT_XYZ);
        appl.getRootNode().attachChild(sky);
    } 
    
    public void bind(Nifty nifty, Screen screen) {
        
    }

    public void setNifty(NiftyJmeDisplay niftyDisplay) {
         nifty = niftyDisplay;
    }
}
