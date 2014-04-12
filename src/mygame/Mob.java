package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.Random;

public class Mob 
{
  Spatial model;
  BetterCharacterControl control;
  int attack,healt=50;
  
  Mob(AssetManager asset,BulletAppState bullet,Vector3f spawnPoint)
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
    bullet.getPhysicsSpace().add(control);   
  }
  
};
