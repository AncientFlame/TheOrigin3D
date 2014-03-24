package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;


public class Player 
{
  Spatial model; //modello 3d pg (braccia)
  CapsuleCollisionShape Shape;
  CharacterControl control;
  float gradi,gradi2; //gradi->rotazione mouse su asse x gradi2->rotazione mouse su asse y
  Vector3f pos; //posizione pg
  Vector3f cam_pos; //posizione camera
  Quaternion rot; //rotazione pg
  boolean w,a,s,d; 
  
   Player(AssetManager asset,BulletAppState bullet)
   {
      gradi=gradi2=0;
      w=a=s=d=false;
      cam_pos=new Vector3f(0,0,0);
      pos=new Vector3f(0,0,0);
      model=asset.loadModel("Models/birillomigliorato/birillo.migliorato.j3o");
      Shape=new CapsuleCollisionShape(1.5f, 6f); //primo parametro raggio,secondo altezza della capsula
      control=new CharacterControl(Shape,0.5f); //crea character control
      control.setPhysicsLocation(new Vector3f(30,5,50)); //posizione character control
      model.setLocalTranslation(30,5+Shape.getHeight()*3f/4,50); //y = 3/4 dell'altezza della capsula
      control.setGravity(98f); //gravità 
      bullet.getPhysicsSpace().add(control);
   }
};
