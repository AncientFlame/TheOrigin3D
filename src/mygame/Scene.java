package mygame;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.scene.Spatial;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.scene.Node;


public class Scene 
{
    private Spatial SceneModel;
    private CollisionShape SceneShape;
    private RigidBodyControl body;
    
    Scene(AssetManager assetManager,BulletAppState bullet,Node root)
    {
       SceneModel=assetManager.loadModel(""); //carica modello 
       SceneShape=CollisionShapeFactory.createMeshShape(SceneModel); //crea collision shape prendendo forma del modello della scena
       body=new RigidBodyControl(SceneShape,0); //crea rigid body con massa 0(non è sottoposto a gravità) prendendo forma da SceneShape
       SceneModel.addControl(body); //aggiunge il rigidbody alla scena
       root.attachChild(SceneModel); //aggiunge la scena al rootNode
       bullet.getPhysicsSpace().add(body); //aggiunge il rigidbody alla fisica del gioco
    }
};
