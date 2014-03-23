package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Spatial;


public class Player 
{
  Spatial model; //modello 3d pg (braccia)
  CapsuleCollisionShape Shape;
  CharacterControl control;
  int gradi;
  
  
   Player(AssetManager asset,BulletAppState bullet)
   {
      gradi=0; 
      model=asset.loadModel("Models/birillomigliorato/birillo.migliorato.j3o");
      Shape=new CapsuleCollisionShape(1.5f, 6f); //primo parametro raggio,secondo altezza della capsula
      control=new CharacterControl(Shape,0.5f); //crea character control
      control.setPhysicsLocation(new Vector3f(30,5,50)); //posizione character control
      model.setLocalTranslation(30,5+Shape.getHeight()*3f/4,50); //y = 3/4 dell'altezza della capsula
      control.setGravity(98f); //gravità 
      bullet.getPhysicsSpace().add(control);
   }
   
   public void updateModelPosition()
   {
      Vector3f app=control.getPhysicsLocation();
      model.setLocalTranslation(new Vector3f(app.x,app.y+Shape.getHeight()*3f/4,app.z)); 
   }
   
   public void setCamera(Camera cam)
   {
      cam.setRotation(model.getLocalRotation());
      Vector3f app=new Vector3f(0,-1f,-5);
      cam.setLocation(model.localToWorld(app,app));
   }   
};
