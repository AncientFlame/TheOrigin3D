package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Spatial;


public class Player 
{
  Spatial model[]=new Spatial[1]; //modello 3d pg (braccia)
  int arma,munizioni,caricatori;
  CapsuleCollisionShape Shape;
  CharacterControl control;
  float gradi,gradi2; //gradi->rotazione mouse su asse x gradi2->rotazione mouse su asse y
  Vector3f pos; //posizione pg
  Vector3f cam_pos; //posizione camera
  Quaternion rot; //rotazione pg
  boolean w,a,s,d; 
  int healt;
  
   Player(AssetManager asset,BulletAppState bullet)
   {
      gradi=gradi2=0;
      w=a=s=d=false;
      healt=100;
      arma=0;
      cam_pos=new Vector3f(0,0,0);
      pos=new Vector3f(0,0,0);
      model[0]=asset.loadModel("Models/birillomigliorato/birillo.migliorato.j3o");
      Shape=new CapsuleCollisionShape(1.5f, 6f); //primo parametro raggio,secondo altezza della capsula
      control=new CharacterControl(Shape,0.5f); //crea character control
      control.setPhysicsLocation(new Vector3f(10,5,10)); //posizione character control
      model[0].setLocalTranslation(10,5+Shape.getHeight()*3f/4,10); //y = 3/4 dell'altezza della capsula
      control.setGravity(98f); //gravità 
      bullet.getPhysicsSpace().add(control);
   }
   
   void FirstPersonCamera(Camera cam)
   {
       cam.setRotation(model[arma].getLocalRotation()); 
       cam_pos.set(0,-0.3f,-5); 
       cam.setLocation(model[arma].localToWorld(cam_pos,cam_pos));
   }
      
};
