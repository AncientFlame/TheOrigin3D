package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
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
//aspetta che i thread finiscano per attaccare gli spatial (se li attacchi nel thread c'è il rischio di crash)       
       while(!thread[0].isDone() || !thread[1].isDone())
          if(thread[0].isDone() && thread[1].isDone())
          {
           rootNode.attachChild(scena.SceneModel);
           rootNode.attachChild(pg.model);
          }
       
       flyCam.setMoveSpeed(150f);
    }

    @Override
    public void simpleUpdate(float tpf)
    {
       // cam.setLocation(pg.control.getPhysicsLocation());
       // System.out.println(pg.model.getLocalTranslation());
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
    
    @Override
    public void destroy()
    {
      super.destroy();
      executor.shutdown(); //stoppa i thread
    }
};
