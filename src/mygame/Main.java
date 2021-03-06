package mygame;


import mygame.guiController.StartGUIController;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.RenderManager;
import com.jme3.system.JmeContext;
import de.lessvoid.nifty.Nifty;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class Main extends SimpleApplication 
{
    private NiftyJmeDisplay niftyDisplay;
    private StartGUIController startController;

    
    public ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(10); 
    public Future thread[]=new Future[10]; //nel gameplay usati thread[0]~thread[4]
    public BulletAppState bullet; //serve per la fisica
    public Scene scena; //scena principale del gioco
    public Player pg;
    public GuiGame GUIg;
    
    public Vector2f coord,appoggio; 
  
    //variabili per gestire i mob
   // public Vector<Mob>mob;
    public Mob mob[]=new Mob[100];
    public Vector3f mobwalkdir;
    int indexMob=0;
    public int round; //round attuale
    public int n_mob; //numero mob creati
    public int r_mob; //mob rimasti 
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
        startController = new StartGUIController(stateManager,assetManager, app, guiViewPort,viewPort, this, rootNode, flyCam,guiNode);
        initStartGUI();
        startController.setNifty(niftyDisplay);

       //inizializza la fisica del gioco 
       bullet=new BulletAppState();
       stateManager.attach(bullet);
       
       mobwalkdir=new Vector3f(0,0,0);
       r_mob=round=1; n_mob=0;
       flyCam.setEnabled(false);
       flyCam.setMoveSpeed(0.0f);
       flyCam.setZoomSpeed(0.0f);
       cam.setFrustumFar(3000); //distanza di visibilità della camera
    }

    @Override
    public void simpleUpdate(float tpf)
    { 
       if(startController.menu==false)
       {     
         pg.pgMov(); //thread[0]
         
         if(n_mob<round) //se i mob creati sono inferiori ai mob da creare
           mobCreate(); //crea un mob

         if(r_mob>0) //ci sono mob vivi
         {
           mobFollowPg();  //thread[4]
           collisionMobPg(); //collisioni mob-pg thread[1]
         }
         updateround();  
         pg.FirstPersonCamera(cam); 
       //  System.out.println(cam.getDirection()+" "+pg.model[0].getLocalRotation().mult(pg.model[0].getLocalTranslation()).normalize());
       }
    }
    
    
    @Override
    public void simpleRender(RenderManager rm) 
    { 
    }
    
    public AnalogListener PgMovement=new AnalogListener() //analog listener per la rotazione delle braccia
    {
        public void onAnalog(String name, float value, float tpf) 
        { 
           if(name.equals("left")) //rotazione braccia verso sinistra
           {
             if(pg.gradi+(coord.x-appoggio.x)/4<=360) pg.gradi+=(appoggio.x-coord.x)/4; else pg.gradi=0; 
             appoggio.x=coord.x;
           }
           if(name.equals("right")) //rotazione braccia verso destra
           {
             if(pg.gradi-(coord.x-appoggio.x)/4>=0) pg.gradi-=(coord.x-appoggio.x)/4; else pg.gradi=360; 
             appoggio.x=coord.x;
           }
           if(name.equals("up")) //rotazione braccia verso l'alto
           { 
              if(pg.gradi2-(coord.y-appoggio.y)/6>=-35) pg.gradi2-=(coord.y-appoggio.y)/6;  
              appoggio.y=coord.y;
           }
           if(name.equals("down")) //rotazione braccia verso il basso
           {
             if(pg.gradi2-(coord.y-appoggio.y)/6<=40) pg.gradi2-=(coord.y-appoggio.y)/6;  
             appoggio.y=coord.y;
           }
           Quaternion quat=new Quaternion();
           Quaternion quat2=new Quaternion();
           quat.fromAngleAxis(FastMath.PI*pg.gradi/180,Vector3f.UNIT_Y); //ruota il quaternione di pg.gradi sull'asse y
           quat2.fromAngleAxis(FastMath.PI*pg.gradi2/180,Vector3f.UNIT_X);  //ruota il quaternione di pg.gradi2 sull'asse x
           pg.rot=quat.mult(quat2); //combina le rotazioni su y e su x in un terzo quaternione 
           pg.model[pg.arma].setLocalRotation(pg.rot); 
        }         
    };
    
    public ActionListener PgMovement2=new ActionListener() //action listener gestione WASD
    {
        public void onAction(String key,boolean pressed,float tpf)
        {
           if(key.equals("W"))
            pg.setKeys(pressed,0);
           if(key.equals("S"))
            pg.setKeys(pressed,1);
           if(key.equals("D"))
            pg.setKeys(pressed,2);
           if(key.equals("A"))
            pg.setKeys(pressed,3);
           if(key.equals("jump"))
             pg.control.jump();

        }
    };
    
 //--------------------arma
    public AnalogListener gun_action=new AnalogListener() 
    {
      @SuppressWarnings("empty-statement")
      public void onAnalog(String key,float value,float tpf)
      {
         if(key.equals("fire")) //input sparo
         {
           if(pg.munizioni[pg.arma]>0)
           {
             if(pg.arma==0) //se si sta usando una mitragliatrice le collisioni vengono verificate subito
             {   
               pg.bullet1=new BulletRapidFireGun(10f*(pg.arma+1),pg.model[pg.arma].getLocalTranslation(),cam.getDirection(),app);
               thread[2]=executor.submit(pg.bullet1.bullScene);
               thread[3]=executor.submit(pg.bullet1.bullMob);
               while(!thread[2].isDone() || !thread[3].isDone());  
                  //se la collisione più vicina è quella con il mob o se è entrato in collisione solo con il mob
               if( (pg.bullet1.dist_m<pg.bullet1.dist_s && pg.bullet1.dist_m!=-1) || (pg.bullet1.dist_m!=-1 && pg.bullet1.dist_s==-1) ) 
               { 
                  mob[pg.bullet1.indice].healt-=pg.bullet1.damage; //decremento vita mob
                  pg.punteggio+=10;
                  if(mob[pg.bullet1.indice].healt<=0) //mob morto
                  { 
                     rootNode.detachChild(mob[pg.bullet1.indice].model); //leva il mob dal vettore e dal rootNode
                     bullet.getPhysicsSpace().remove(mob[pg.bullet1.indice].control);
                     mob[pg.bullet1.indice]=null;
                     r_mob--; //mob rimasti
                  }
               }
             }
             //pg.munizioni[pg.arma]--;
           } else
               pg.ric();
         }
      }
    };
    
    public ActionListener gun_action2=new ActionListener()
    {
       public void onAction(String key,boolean pressed,float tpf)
       {
          if(key.equals("ricarica") && !pressed)
            pg.ric();
       }
    };
   
 //-----------------------mob   
    
    private void mobCreate() //gestisce la creazione dei mob 
    {  
       Random rand=new Random(); 
       for(int i=0; i<100; i++)
       {
          if(mob[i]==null)
          {
             mob[i]=new Mob(assetManager,bullet,scena.spawnPoint[rand.nextInt(4)],round,this);
             rootNode.attachChild(mob[i].model);
             n_mob++;  
             i=101;
          }
       }
    }
    
    private void collisionMobPg() //usa thread[1]
    {
      if(thread[1]==null)
        thread[1]=executor.submit(collisionMobPgThread);
      else
        if(thread[1].isDone())
        {
          if(pg.healt<=0)
            System.out.println("morto");  
          thread[1]=null;   
        }
    }
    
    private Callable collisionMobPgThread=new Callable()
    {
      public Object call()
      {
         for(int i=0; i<round; i++)
           if(mob[i]!=null)  
             mob[i].collisionMobPg_thread();
         return null; 
      }
    };

    private void mobFollowPg() 
    {  
       if(thread[4]==null) 
       {
         if(mob[indexMob]!=null)  
          thread[4]=executor.submit(mob[indexMob].FollowPg);
         else { indexMob++; if(indexMob>round) indexMob=0; }
       }
       else if(thread[4].isDone())
            {
              if(mob[indexMob]!=null)  
               mob[indexMob].control.setWalkDirection(mobwalkdir);
               thread[4]=null; //rimesso a null per ripartire
               indexMob++;
                if(indexMob>round) 
                 indexMob=0;
            }  
    }
    
//----------------------gui    
    private void initStartGUI(){
        Nifty nifty = niftyDisplay.getNifty();
        nifty.fromXml("Interface/start.xml", "start", startController);
        nifty.enableAutoScaling(Settings.system.getWidth(), Settings.system.getHeight());
        guiViewPort.addProcessor(niftyDisplay);
        flyCam.setDragToRotate(true);
    }
    
//----------gameplay
    private void updateround()
    {  
       if(r_mob==0)
       { 
          round++;
          n_mob=0;
          r_mob=round;
       }
    }
  
    
    @Override
    public void destroy()
    {
      super.destroy();
      executor.shutdown(); //stoppa i thread
    }
                               
};
