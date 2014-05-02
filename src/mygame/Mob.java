package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import java.util.Random;
import java.util.concurrent.Callable;

public class Mob 
{
  Spatial model;
  BetterCharacterControl control;
  int attack,healt;
  Main appl;
  
  Mob(AssetManager asset,BulletAppState bullet,Vector3f spawnPoint,int round,Main appl)
  {
    Random rand=new Random();
    attack=rand.nextInt(9)+1; //compreso tra 1 e 10
    model=asset.loadModel("Models/inizio maschio3/inizio maschio3.j3o");
    model.setLocalScale(3,3,3);
    control=new BetterCharacterControl(1.5f,6f,2f); //crea character control
    model.setLocalTranslation(spawnPoint);
    //model.setLocalTranslation(app.x,app.y+2,app.z);
    control.setGravity(new Vector3f(0,-98f,0)); //gravità 
    model.addControl(control);
    healt=10*round;
    this.appl=appl;
    bullet.getPhysicsSpace().add(control);   
  }
    
    public void collisionMobPg_thread() 
    {
        
        CollisionResults result=new CollisionResults(); 
//le collisioni vengono calcolate con i cloni dei modelli per evitare l'effetto flash del modello 
// pg.model[pg.arma].clone().collideWith(mob.elementAt(i).model.clone().getWorldBound(),result); 
        appl.pg.model[appl.pg.arma].clone().collideWith(model.clone().getWorldBound(),result); 
          if(result.size()>0)   
           appl.pg.healt-=attack; 
    }
    
    public Callable FollowPg=new Callable()
    {
      public Object call()
      {         
         appl.mobwalkdir.set(model.getLocalTranslation());
         Vector3f app=appl.pg.model[appl.pg.arma].getLocalTranslation();
         float x,z;
         
         x = (app.x-appl.mobwalkdir.x);
         z = (app.z-appl.mobwalkdir.z);
         //Math.abs(float) restituisce valore assoluto del float
         if(Math.abs(x)>10)
          x/=2;
         else
           if(Math.abs(x)<3)
             x*=3;
         if(Math.abs(z)>10)
          z/=2;
         else
           if(Math.abs(z)<3)
             z*=3;
         
         appl.mobwalkdir.set(x,0,z);
         return 1; 
      }
    };
  
};
