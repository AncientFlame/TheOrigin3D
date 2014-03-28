package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import java.util.Random;

public class Mob 
{
  Spatial model;
  CharacterControl control;
  CapsuleCollisionShape Shape;
  int attack,healt=50;
  
  Mob(AssetManager asset,BulletAppState bullet,Vector3f spawnPoint)
  {
    Random rand=new Random();
    attack=rand.nextInt(9)+1; //compreso tra 1 e 10
    model=asset.loadModel("Models/birillomigliorato/birillo.migliorato.j3o");
    model.setName("Mob"); //serve per le collisioni
    Shape=new CapsuleCollisionShape(1.5f, 6f); //primo parametro raggio,secondo altezza della capsula
    control=new CharacterControl(Shape,0.5f); //crea character control
    control.setPhysicsLocation(spawnPoint); //posizione character control
    model.setLocalTranslation(control.getPhysicsLocation()); 
    control.setGravity(98f); //gravità 
    bullet.getPhysicsSpace().add(control);   
  }
  
};
