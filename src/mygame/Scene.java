package mygame;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.scene.Spatial;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.ViewPort;

public class Scene 
{   
    public Spatial SceneModel;
    public Vector3f spawnPoint[]=new Vector3f[4];
    private CollisionShape SceneShape;
    
    private RigidBodyControl body;
    
    public Scene(AssetManager assetManager,ViewPort port,BulletAppState bullet,int n_mappa)
    {
        
     switch(n_mappa)
     {
      case 0: 
      {
         Material mat=new Material(assetManager ,"Common/MatDefs/Misc/Unshaded.j3md"); //da levare quando ci saranno le texture
         mat.setColor("Color",ColorRGBA.Green); //da levare quando ci saranno le texture
      
         SceneModel=assetManager.loadModel("Models/mappa1.j3o"); //carica modello 
         SceneModel.setMaterial(mat); //da levare quando ci saranno le texture

        /* spawnPoint[0]=new Vector3f(-946,11,957);
         spawnPoint[1]=new Vector3f(936,11,927);
         spawnPoint[2]=new Vector3f(947,11,-984);
         spawnPoint[3]=new Vector3f(-953,11,-947);*/
          spawnPoint[0]=new Vector3f(20,11,20);
         spawnPoint[1]=new Vector3f(20,11,20);
         spawnPoint[2]=new Vector3f(20,11,20);
         spawnPoint[3]=new Vector3f(20,11,20);
      } break;
     }
       
       SceneShape=CollisionShapeFactory.createMeshShape(SceneModel); //crea collision shape prendendo forma del modello della scena
       body=new RigidBodyControl(SceneShape,0); //crea rigid body con massa 0(non è sottoposto a gravità) prendendo forma da SceneShape
       SceneModel.addControl(body); //aggiunge il rigidbody alla scena
       bullet.getPhysicsSpace().add(body); //aggiunge il rigidbody alla fisica del gioco
    }
};
