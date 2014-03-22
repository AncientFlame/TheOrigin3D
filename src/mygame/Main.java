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
    }

    @Override
    public void simpleUpdate(float tpf)
    {
       
    }

    @Override
    public void simpleRender(RenderManager rm) 
    {
      
    }
      
    private Callable InitScene=new Callable() //thread per la scena   
    {
        public Object call()
        {
           scena=new Scene(assetManager,bullet,rootNode); 
           return null; 
        }
    };
};
