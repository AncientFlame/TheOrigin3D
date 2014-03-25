package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class Main extends SimpleApplication 
{
    ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(10); //per ora ho messo massimo 10 thread contemporaneamente
    Future thread[]=new Future[10];
    BulletAppState bullet; //serve per la fisica
    Scene scena; //scena principale del gioco
    Player pg;
    
    //variabili per gestire i mob
    Vector<Mob>mob;
    int round; //round attuale
    int n_mob; //numero mob creati
    int r_mob; //mob rimasti 
    Vector3f spawnPoint[]=new Vector3f[4];
    
    
    public static void main(String[] args) 
    {
        Main app = new Main();
        Settings sys = new Settings();
        app.setSettings(sys.get_settings());
        app.start();
    }
    
    @Override
    public void simpleInitApp()
    {
       //inizializza la fisica del gioco 
       bullet=new BulletAppState();
       stateManager.attach(bullet);
       //inizializzazioni scena
       thread[0]=executor.submit(InitScene);
       thread[1]=executor.submit(InitPg);
       thread[2]=executor.submit(InitKeys);
       thread[3]=executor.submit(InitVectorMob);
//aspetta che i thread finiscano per attaccare gli spatial (se li attacchi nel thread c'è il rischio di crash)       
       while(!thread[0].isDone() || !thread[1].isDone() || !thread[2].isDone() || !thread[3].isDone()) {}
       
       rootNode.attachChild(scena.SceneModel);
       rootNode.attachChild(pg.model);
       
       thread[0]=null;
       r_mob=round=1; n_mob=0;
       flyCam.setEnabled(true);
       flyCam.setMoveSpeed(0.0f);
    }

    @Override
    public void simpleUpdate(float tpf)
    { 
        pgMov(); //movimento character control thread[0]
        if(n_mob<round) //se i mob creati sono inferiori ai mob da creare
          mobCreate(); //crea un mob
        if(r_mob>0) //ci sono mob vivi
          mobFollowPg();  
    }

    @Override
    public void simpleRender(RenderManager rm) 
    {  
    }
//---------------------scena      
    private Callable InitScene=new Callable() //thread per la scena   
    {
        public Object call()
        {
           scena=new Scene(assetManager,viewPort,bullet); 
           return null; 
        }
    };
//---------------------pg    
    private Callable InitPg=new Callable() //thread per il pg
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
         pg.model.setLocalTranslation(new Vector3f(app3.x,app3.y+pg.Shape.getHeight()*3f/4,app3.z)); 
         //posizione e rotazione camera
         cam.setRotation(pg.model.getLocalRotation()); 
         pg.cam_pos.set(0,-0.3f,-5); 
         cam.setLocation(pg.model.localToWorld(pg.cam_pos,pg.cam_pos));
         thread[0]=null; //il future viene rimesso a null
       }
    }
 //--------------------------listener   
    private Callable InitKeys=new Callable() //thread che inizializza la mappa dei tasti
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
           inputManager.addListener(PgMovement,"left","right","up","down");
           inputManager.addListener(PgMovement2,"W","S","D","A");
           return null;
       }
    };
    
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
           pg.model.setLocalRotation(pg.rot);  
           //posizione e rotazione camera
           cam.setRotation(pg.model.getLocalRotation()); 
           pg.cam_pos.set(0,-0.3f,-5); 
           cam.setLocation(pg.model.localToWorld(pg.cam_pos,pg.cam_pos));
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
 //-----------------------mob   
    
    private Callable InitVectorMob=new Callable() //thread per il vettore di mob
    {
      public Object call()
      {
        mob=new Vector(100); //massimo 100 mob
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
    
    @Override
    public void destroy()
    {
      super.destroy();
      executor.shutdown(); //stoppa i thread
    }
};
