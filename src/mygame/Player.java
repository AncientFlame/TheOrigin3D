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
  public Spatial model[]=new Spatial[1]; //modello 3d pg (braccia)
  public int arma,munizioni[],caricatori[],munizioni_max[],caricatori_max[];
  public CapsuleCollisionShape Shape;
  public CharacterControl control;
  public float gradi,gradi2; //gradi->rotazione mouse su asse x gradi2->rotazione mouse su asse y
  public Vector3f pos; //posizione pg
  public Vector3f cam_pos; //posizione camera
  public Quaternion rot; //rotazione pg
  public boolean w,a,s,d; 
  public int healt;
  
   Player(AssetManager asset,BulletAppState bullet)
   {
      gradi=gradi2=0;
      w=a=s=d=false;
      healt=100;
      munizioni=new int[1];
      caricatori=new int[1];
      munizioni_max=new int[1];
      caricatori_max=new int[1];
      munizioni[0]=25; caricatori[0]=100; munizioni_max[0]=25; caricatori_max[0]=100; //arma 0
      arma=0;
      cam_pos=new Vector3f(0,0,0);
      pos=new Vector3f(0,0,0);
      model[0]=asset.loadModel("Models/braccio/braccio2.j3o");
      Shape=new CapsuleCollisionShape(1.5f, 6f); //primo parametro raggio,secondo altezza della capsula
      control=new CharacterControl(Shape,0.5f); //crea character control
      control.setPhysicsLocation(new Vector3f(10,5,10)); //posizione character control
      model[0].setLocalTranslation(10,5+Shape.getHeight()*3/4,10); 
      control.setGravity(98f); //gravità 
      bullet.getPhysicsSpace().add(control);
   }
   
   void FirstPersonCamera(Camera cam)
   {
       Vector3f app=control.getPhysicsLocation(); //posizioni le braccia ai 3/4 dell'altezza della capsula
       model[arma].setLocalTranslation(app.x,app.y+Shape.getHeight()*3f/4,app.z);
       cam.setRotation(model[arma].getLocalRotation()); //da la rotazione alla camera (la stessa del pg)
       cam_pos.set(0,-0.3f,-5); //la posizione della camera è spostata indietro di 5 e in basso 0.3 rispetto alle coordinate globali del modello
       cam.setLocation(model[arma].localToWorld(cam_pos,cam_pos));
   }
      
};
