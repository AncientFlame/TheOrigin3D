package mygame;


import mygame.guiController.StartGUIController;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.collision.CollisionResults;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Spatial;
import com.jme3.system.JmeContext;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;
import de.lessvoid.nifty.Nifty;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class Main extends SimpleApplication 
{
    private NiftyJmeDisplay niftyDisplay;
    private StartGUIController startController;
 
    
    public ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(10); //per ora ho messo massimo 10 thread contemporaneamente
    public Future thread[]=new Future[10]; //nel gameplay usati thread[0]~thread[2]
    private BulletAppState bullet; //serve per la fisica
    public Scene scena; //scena principale del gioco
    public Player pg;
    public GuiGame GUIg;
    
 //variabili proiettile   
    BulletRapidFireGun bullet1;
  
    //variabili per gestire i mob
    public Vector<Mob>mob;
    private int round; //round attuale
    private int n_mob; //numero mob creati
    private int r_mob; //mob rimasti 
    private Vector3f spawnPoint[]=new Vector3f[4];
    private static Main app;

    public static void main(String[] args) 
    {
        app = new Main();
        Settings sys = new Settings();
        app.setSettings(sys.get_settings());
        app.start(JmeContext.Type.Display);
    }
    
    @Override
    public void simpleInitApp(){        
        //inizializzo il diplay
        niftyDisplay = new NiftyJmeDisplay( assetManager, 
                                            inputManager, 
                                            audioRenderer,
                                            guiViewPort);
        //inizializzo il controller
        startController = new StartGUIController(stateManager,assetManager, app, guiViewPort, this, rootNode, flyCam,guiNode);
        initStartGUI();
        startController.setNifty(niftyDisplay);

       //inizializza la fisica del gioco 
       bullet=new BulletAppState();
       stateManager.attach(bullet);
       
       r_mob=round=1; n_mob=0;
       flyCam.setEnabled(true);
       flyCam.setMoveSpeed(0.0f);
       cam.setFrustumFar(3000); //distanza di visibilità della camera
    }

    @Override
    public void simpleUpdate(float tpf)
    { 
       if(startController.menu==false)
       { 
         pgMov(); //movimento character control thread[0]
         
         if(n_mob<round) //se i mob creati sono inferiori ai mob da creare
           mobCreate(); //crea un mob
    
         if(r_mob>0) //ci sono mob vivi
         {
           mobFollowPg();  
           collisionMobPg(); //collisioni mob-pg thread[1]
         }
         pg.FirstPersonCamera(cam);
         System.out.println(pg.munizioni[pg.arma]+" "+pg.caricatori[pg.arma]);
       }
    }
    
    
    @Override
    public void simpleRender(RenderManager rm) 
    {  
    }
//---------------------scena      
    public Callable InitScene=new Callable() //thread per la scena   
    {
        public Object call()
        {
           scena=new Scene(assetManager,viewPort,bullet); 
           initSky();
           return null; 
        }
    };
//---------------------pg    
    public Callable InitPg=new Callable() //thread per il pg
    {
      public Object call()
      {
        pg=new Player(assetManager,bullet);  
        return null;
      }
    };
    
    private Callable pgMov_thread=new Callable() //thread per calcolare l'incremento del vettore posizione 
    {
        public Object call() 
        {  
           Vector3f app=cam.getDirection().multLocal(0.9f); //prende direzione della telecamera e la moltiplica per 0.6 (accorcia lunghezza del vettore)
           Vector3f app2=cam.getLeft().multLocal(0.7f); //prende direzione sinistra della telecamera e la moltiplica per 0.4 (accorcia lunghezza vettore)
           pg.pos.set(0,0,0); //viene inizializzato il vettore a 0
           if(pg.w) pg.pos.addLocal(app); //se w è premuto si aumenta somma al vettore pos il vettore app (aumenta z)
           if(pg.s) pg.pos.addLocal(app.negate()); //se s è premuto si sottrae al vettore pos il vettore app (diminuisce z)
           if(pg.a) pg.pos.addLocal(app2); //se a è premuto si somma al vettore pos il vettore app2 (aumenta x)
           if(pg.d) pg.pos.addLocal(app2.negate()); //se d è premuto si sottrae al vettore pos il vettore app2 (diminuisce x)
           
           return 1; 
        }
    };
    
    private void pgMov() //gestisce il movimento (usa il future thread[0])
    {
      if(thread[0]==null) //se il future è null fa partire il thread
        thread[0]=executor.submit(pgMov_thread);
      else
       if(thread[0].isDone()) //se il thread è finito
       {
         pg.control.setWalkDirection(pg.pos); //viene settato il walkdirection del character control
         Vector3f app3=pg.control.getPhysicsLocation(); //settata nuova posizione delle braccia
         pg.model[pg.arma].setLocalTranslation(new Vector3f(app3.x,app3.y+pg.Shape.getHeight()*3f/4,app3.z)); 
         thread[0]=null; //il future viene rimesso a null
       }
    }
    
    private AnalogListener PgMovement=new AnalogListener() //analog listener per il movimento delle braccia
    {
        public void onAnalog(String name, float value, float tpf) 
        { 
           if(name.equals("right")) //rotazione braccia verso destra
              if(pg.gradi+2.5<=360) pg.gradi+=2.5f; else pg.gradi=0; //1.5 gradi -> 45°/30 

           if(name.equals("left")) //rotazione braccia verso sinistra
              if(pg.gradi-1.5>=0) pg.gradi-=1.5; else pg.gradi=360; 

           if(name.equals("up")) //rotazione braccia verso l'alto
              if(pg.gradi2-0.55>=-25) pg.gradi2-=0.55;

           if(name.equals("down")) //rotazione braccia verso il basso
              if(pg.gradi2+0.55<=40) pg.gradi2+=0.55;

           Quaternion quat=new Quaternion();
           Quaternion quat2=new Quaternion();
           quat.fromAngleAxis(FastMath.PI*pg.gradi/180,Vector3f.UNIT_Y); //ruota il quaternione di pg.gradi sull'asse y
           quat2.fromAngleAxis(FastMath.PI*pg.gradi2/180,Vector3f.UNIT_X);  //ruota il quaternione di pg.gradi2 sull'asse x
           pg.rot=quat.mult(quat2); //combina le rotazioni su y e su x in un terzo quaternione 
           pg.model[pg.arma].setLocalRotation(pg.rot);  
        }         
    };
    
    private ActionListener PgMovement2=new ActionListener() //action listener gestione WASD
    {
        public void onAction(String key,boolean pressed,float tpf)
        {
            if(key.equals("W"))
             pg.w=pressed; 

            if(key.equals("S"))
             pg.s=pressed; 

            if(key.equals("D"))
              pg.d=pressed;
 
            if(key.equals("A"))
              pg.a=pressed; 
        }
    };
    
 //--------------------------listener   
    public Callable InitKeys=new Callable() //thread che inizializza la mappa dei tasti
    {
       public Object call()
       {
           inputManager.addMapping("W",new KeyTrigger(KeyInput.KEY_W));
           inputManager.addMapping("S",new KeyTrigger(KeyInput.KEY_S));
           inputManager.addMapping("D",new KeyTrigger(KeyInput.KEY_D));
           inputManager.addMapping("A",new KeyTrigger(KeyInput.KEY_A));
           inputManager.addMapping("right",new MouseAxisTrigger(MouseInput.AXIS_X, true)); //movimento mouse verso destra
           inputManager.addMapping("left",new MouseAxisTrigger(MouseInput.AXIS_X, false)); //movimento mouse verso sinistra
           inputManager.addMapping("down",new MouseAxisTrigger(MouseInput.AXIS_Y, true)); //movimento mouse verso il basso
           inputManager.addMapping("up",new MouseAxisTrigger(MouseInput.AXIS_Y, false)); //movimento mouse verso l'alto
           inputManager.addMapping("fire",new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
           inputManager.addMapping("ricarica",new KeyTrigger(KeyInput.KEY_R));
           inputManager.addListener(PgMovement,"left","right","up","down");
           inputManager.addListener(PgMovement2,"W","S","D","A");
           inputManager.addListener(gun_action,"fire");
           inputManager.addListener(gun_action2,"ricarica");
           return null;
       }
    };
 //--------------------arma
    private AnalogListener gun_action=new AnalogListener() 
    {
      @SuppressWarnings("empty-statement")
      public void onAnalog(String key,float value,float tpf)
      {
         if(key.equals("fire")) //input sparo
         {
           if(pg.munizioni[pg.arma]>0)
           {
             if(pg.arma==0) //mitragliatrice->usa semiretta 
                bullet1=new BulletRapidFireGun(10f,pg.model[pg.arma].getLocalTranslation(),cam.getDirection());
  
             thread[2]=executor.submit(bullScene);
             while(!thread[2].isDone());
             pg.munizioni[pg.arma]--;
           } else
               ric();
         }
      }
    };
    
    private ActionListener gun_action2=new ActionListener()
    {
       public void onAction(String key,boolean pressed,float tpf)
       {
          if(key.equals("ricarica") && !pressed)
            ric();
       }
    };
    
    private void ric() //funzione per ricaricare
    {
      if(pg.munizioni[pg.arma]<pg.munizioni_max[pg.arma]) //se non si hanno il massimo dei proiettili
      {
        int app=pg.munizioni_max[pg.arma]-pg.munizioni[pg.arma]; //trova quanti proiettili mancano
        if(pg.caricatori[pg.arma]-app>=0) //se nel caricatore ce ne sono abbastanza 
        {
           pg.caricatori[pg.arma]-=app; //prende proiettili dal caricatore
           pg.munizioni[pg.arma]+=app; //li mette nelle munizioni disponibili per sparare
        } else {  //i proiettili rimasti non bastano a ricaricare tutta l'arma... si usano tutti i proiettili rimasti
                 pg.munizioni[pg.arma]+=pg.caricatori[pg.arma];
                 pg.caricatori[pg.arma]=0;
               }
      }
    }
    
    private Callable bullScene=new Callable() //collisioni proiettili scena thread[2]
    {
         public Object call()
         {
              CollisionResults result=new CollisionResults(); 
              if(pg.arma==0) 
               scena.SceneModel.clone().collideWith(bullet1.bullet_dir,result); //calcola collisioni
              if(result.size()>0) //ci sono collisioni
                for(int i=0; i<result.size(); i++) //trova la collisione più vicina
                  switch(pg.arma)
                  {
                     case 0:  
                             if(result.getCollision(i).getDistance()<bullet1.dist_s || bullet1.dist_s==-1 )
                               bullet1.dist_s=result.getCollision(i).getDistance(); 
                           break;
                 }
               return null; 
         }
    };
    
 //-----------------------mob   
    
    public Callable InitVectorMob=new Callable() //thread per il vettore di mob
    {
      public Object call()
      {
        mob=new Vector(0); 
        spawnPoint[0]=new Vector3f(-946,11,957);
        spawnPoint[1]=new Vector3f(936,11,927);
        spawnPoint[2]=new Vector3f(947,11,-984);
        spawnPoint[3]=new Vector3f(-953,11,-947);
        return null;
      }
    };
    
    private void mobCreate() //gestisce la creazione dei mob 
    {  
       Random rand=new Random(); 
       mob.addElement(new Mob(assetManager,bullet,spawnPoint[rand.nextInt(4)]));
       rootNode.attachChild(mob.elementAt(n_mob).model);
       n_mob++;
    }
    
    private void mobFollowPg() 
    {
      
    }
    
    private void collisionMobPg() //usa thread[1]
    {
      if(thread[1]==null)
        thread[1]=executor.submit(collisionMobPg_thread);
      else
        if(thread[1].isDone())
        {
          if(pg.healt<=0)
            System.out.println("morto");  
          thread[1]=null;      
        }
    }
    
    private Callable collisionMobPg_thread=new Callable() //usa thread[1]
    {
      public Object call()
      {
         CollisionResults result=new CollisionResults(); 
         for(int i=0; i<mob.capacity(); i++)
         { 
//le collisioni vengono calcolate con i cloni dei modelli per evitare l'effetto flash del modello 
            pg.model[pg.arma].clone().collideWith(mob.elementAt(i).model.clone().getWorldBound(),result); 
            if(result.size()>0)
              pg.healt-=mob.elementAt(i).attack;
         }
         return null; 
      }
    };

//----------------------gui    
    private void initStartGUI(){
        Nifty nifty = niftyDisplay.getNifty();
        nifty.fromXml("Interface/start.xml", "start", startController);
        nifty.enableAutoScaling(Settings.system.getWidth(), Settings.system.getHeight());
        guiViewPort.addProcessor(niftyDisplay);
        flyCam.setDragToRotate(true);
    }
    
    public Callable initGameGUI=new Callable()
    {
      public Object call()  
      {
          GUIg=new GuiGame(assetManager);
          return null;
      }  
    };

    @Override
    public void destroy()
    {
      super.destroy();
      executor.shutdown(); //stoppa i thread
    }
    
    private void initSky(){
        Texture west = assetManager.loadTexture("Textures/DarkStormy/DarkStormyRight2048.png");
        Texture east = assetManager.loadTexture("Textures/DarkStormy/DarkStormyLeft2048.png");
        Texture north = assetManager.loadTexture("Textures/DarkStormy/DarkStormyFront2048.png");
        Texture south = assetManager.loadTexture("Textures/DarkStormy/DarkStormyBack2048.png");
        Texture up = assetManager.loadTexture("Textures/DarkStormy/DarkStormyUp2048.png");
        Texture down = assetManager.loadTexture("Textures/DarkStormy/DarkStormyDown2048.png");
       
        Spatial sky = SkyFactory.createSky(assetManager, west, east, north, south, up, down, Vector3f.UNIT_XYZ);
        rootNode.attachChild(sky);
    } 
};
