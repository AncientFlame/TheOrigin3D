package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
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
    
    public static void main(String[] args) 
    {
        Main app = new Main();
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
//aspetta che i thread finiscano per attaccare gli spatial (se li attacchi nel thread c'è il rischio di crash)       
       while(!thread[0].isDone() || !thread[1].isDone() || !thread[2].isDone()) {}
       
       rootNode.attachChild(scena.SceneModel);
       rootNode.attachChild(pg.model);
          
       flyCam.setEnabled(true);
       Vector3f app=pg.model.getLocalTranslation();
       cam.setLocation(new Vector3f(app.x,app.y+2,app.z-5));
       cam.setRotation(pg.model.getLocalRotation());
    }

    @Override
    public void simpleUpdate(float tpf)
    {
      
    }

    @Override
    public void simpleRender(RenderManager rm) 
    {
      pg.updateModelPosition();
    }
      
    private Callable InitScene=new Callable() //thread per la scena   
    {
        public Object call()
        {
           scena=new Scene(assetManager,viewPort,bullet); 
           return null; 
        }
    };
    
    private Callable InitPg=new Callable() //thread per il pg
    {
      public Object call()
      {
        pg=new Player(assetManager,bullet);  
        return null;
      }
    };
    
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
           inputManager.addListener(PgMovement,"W","S","D","A","left","right");
           return null;
       }
    };
    
    private AnalogListener PgMovement=new AnalogListener() //analog listener per il movimento del pg
    {
        public void onAnalog(String name, float value, float tpf) 
        {
           if(name.equals("right")) //rotazione braccia verso destra
           {
              if(pg.gradi+1.5<=360) pg.gradi+=1.5; else pg.gradi=0; //1.5 gradi -> 45°/30 358.5 -> 360-1.5
              Quaternion quad=pg.model.getLocalRotation();
              quad.fromAngleAxis(FastMath.PI*pg.gradi/180,Vector3f.UNIT_Y); //ruota il quaternione di pg.gradi lungo l'asse y
              pg.model.setLocalRotation(quad);
              pg.setCamera(cam);
           } else
           if(name.equals("left")) //rotazione braccia verso sinistra
           {
              if(pg.gradi-1.5>=0) pg.gradi-=0.75; else pg.gradi=360; 
              Quaternion quad=pg.model.getLocalRotation();
              quad.fromAngleAxis(FastMath.PI*pg.gradi/180,Vector3f.UNIT_Y); //ruota il quaternione di pg.gradi lungo l'asse y
              pg.model.setLocalRotation(quad);
              pg.setCamera(cam);
           }
        }          
    };
    

    
    @Override
    public void destroy()
    {
      super.destroy();
      executor.shutdown(); //stoppa i thread
    }
};
