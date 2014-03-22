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
import com.jme3.scene.Node;
import com.jme3.util.SkyFactory;


public class Scene 
{
    private Spatial SceneModel;
    private CollisionShape SceneShape;
    private RigidBodyControl body;
    
    Scene(AssetManager assetManager,ViewPort port,BulletAppState bullet,Node root)
    {
       Material mat=new Material(assetManager ,"Common/MatDefs/Misc/Unshaded.j3md"); //da levare quando ci saranno le texture
       mat.setColor("Color",ColorRGBA.Green); //da levare quando ci saranno le texture
       SceneModel=assetManager.loadModel("Models/untitled/untitled.j3o"); //carica modello 
       
       SceneModel.setMaterial(mat); //da levare quando ci saranno le texture
       
       SceneShape=CollisionShapeFactory.createMeshShape(SceneModel); //crea collision shape prendendo forma del modello della scena
       body=new RigidBodyControl(SceneShape,0); //crea rigid body con massa 0(non è sottoposto a gravità) prendendo forma da SceneShape
       SceneModel.addControl(body); //aggiunge il rigidbody alla scena
       root.attachChild(SceneModel); //aggiunge la scena al rootNode
       bullet.getPhysicsSpace().add(body); //aggiunge il rigidbody alla fisica del gioco
       
       //creo colore e lo metto come sfondo della visione
       ColorRGBA color = new ColorRGBA(0.15f,0.15f,1f,1f);
       port.setBackgroundColor(color);
       
       
    }
};
