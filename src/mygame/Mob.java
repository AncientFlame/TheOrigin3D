package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
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
    Material mat=new Material(asset ,"Common/MatDefs/Misc/Unshaded.j3md");
    mat.setColor("Color", ColorRGBA.Blue);
    attack=rand.nextInt(9)+1; //compreso tra 1 e 10
    model=asset.loadModel("Models/inizio maschio3/inizio maschio3.j3o");
    model.setMaterial(mat);
    Shape=new CapsuleCollisionShape(1.5f, 6f); //primo parametro raggio,secondo altezza della capsula
    control=new CharacterControl(Shape,0.5f); //crea character control
    control.setPhysicsLocation(spawnPoint); //posizione character control
    model.setLocalTranslation(control.getPhysicsLocation()); 
    control.setGravity(98f); //gravità 
    bullet.getPhysicsSpace().add(control);   
  }
 /* public void updatemob()
  {
     model.setLocalTranslation(control.getPhysicsLocation()); 
  }*/
  
};
