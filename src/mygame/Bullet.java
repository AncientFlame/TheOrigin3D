package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

public class Bullet 
{
  int vel;
  CharacterControl control;
  SphereCollisionShape shape;
  Vector3f dir;
  Spatial model;
  
    Bullet(int vel,String percorso,AssetManager asset)
    {
       this.vel=vel;
       model=asset.loadModel(percorso);
       shape=new SphereCollisionShape(0.2f);
       control=new CharacterControl(shape,0.05f);
       control.setSpatial(model);
       control.setGravity(2f);
    }
};
