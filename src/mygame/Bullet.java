package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

public class Bullet 
{
  int vel;
  Vector3f dir;
  Spatial model;
  
    Bullet(int vel,String percorso,AssetManager asset)
    {
       this.vel=vel;
       model=asset.loadModel(percorso);
    }
};
