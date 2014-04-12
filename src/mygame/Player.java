package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.concurrent.Callable;


public class Player 
{
  public Spatial model[]=new Spatial[1]; //modello 3d pg (braccia)
  
  public int arma,munizioni[],caricatori[],munizioni_max[],caricatori_max[];
  
  public BetterCharacterControl control;
  public float gradi,gradi2; //gradi->rotazione mouse su asse x gradi2->rotazione mouse su asse y
  public Vector3f pos; //posizione pg
  public Vector3f cam_pos; //posizione camera
  public Quaternion rot; //rotazione pg
  public boolean w,a,s,d; 
  public int healt;
  public BulletRapidFireGun bullet1; //proiettile per mitragliatrici
  public Node model_node;
  Main appl;
  
   public Player(AssetManager asset,BulletAppState bullet,Main app)
   {
      appl=app; 
      gradi=gradi2=0;
      w=a=s=d=false;
      healt=100;

      munizioni=new int[1];
      caricatori=new int[1];
      munizioni_max=new int[1];
      caricatori_max=new int[1];
      munizioni[0]=25; caricatori[0]=100; munizioni_max[0]=25; caricatori_max[0]=100; //arma 0
      arma=0;
      model_node=new Node("nodopg");
      cam_pos=new Vector3f(0,0,0);
      pos=new Vector3f(0,0,0);
      
      model[0]=asset.loadModel("Models/braccio/braccio2.j3o");
      
      control=new BetterCharacterControl(1.5f,6f,0.5f); //crea character control 1° raggio 2° altezza 3° massa
      model[0].setLocalTranslation(10,11,10); 
      model_node.setLocalTranslation(10,1.8f,10);
      model_node.addControl(control); 
      //model_node.attachChild(model[0]);
      control.setGravity(new Vector3f(0,-98f,0)); //gravità 

      bullet.getPhysicsSpace().add(control);
   }
   
   void FirstPersonCamera(Camera cam)
   {
       Vector3f app=model_node.getLocalTranslation();
       model[arma].setLocalTranslation(app.x,app.y+10,app.z);
       cam_pos.set(0,-0.3f,-5); //la posizione della camera è spostata indietro di 5 e in basso 0.3 rispetto alle coordinate globali del modello
       cam.setLocation(model[arma].localToWorld(cam_pos,cam_pos));
       cam.setRotation(model[arma].getLocalRotation()); //da la rotazione alla camera (la stessa del pg)
   }
   
    public void pgMov() //gestisce il movimento (usa il future thread[0])
    {
      if(appl.thread[0]==null) //se il future è null fa partire il thread
        appl.thread[0]=appl.executor.submit(pgMov_thread);
      else
       if(appl.thread[0].isDone()) //se il thread è finito
       {
         control.setWalkDirection(pos); //viene settato il walkdirection del character control
         appl.thread[0]=null; //il future viene rimesso a null
       }
    }
    
    private Callable pgMov_thread=new Callable() //thread per calcolare l'incremento del vettore posizione 
    {
        public Object call() 
        {  
  
           Vector3f app=appl.getCamera().getDirection(); //prende direzione della telecamera 
           app.set(app.x,0,app.z).multLocal(40f); 
           Vector3f app2=appl.getCamera().getLeft().multLocal(40f); //prende direzione sinistra della telecamera 
           pos.set(0,0,0); //viene inizializzato il vettore a 0
           if(w) pos.addLocal(app); //se w è premuto si aumenta somma al vettore pos il vettore app (aumenta z)
           if(s) pos.addLocal(app.negate()); //se s è premuto si sottrae al vettore pos il vettore app (diminuisce z)
           if(a) pos.addLocal(app2); //se a è premuto si somma al vettore pos il vettore app2 (aumenta x)
           if(d) pos.addLocal(app2.negate()); //se d è premuto si sottrae al vettore pos il vettore app2 (diminuisce x)
           
           return 1; 
        }
    };
   
     public void setKeys(boolean pressed,int tpf)
     {   
       switch(tpf)
       {
           case 0: w=pressed; break;  
           case 1: s=pressed; break;
           case 2: d=pressed; break;
           case 3: a=pressed; break;
       }
     }
     
    public void ric() //funzione per ricaricare
    {
      if(munizioni[arma]<munizioni_max[arma]) //se non si hanno il massimo dei proiettili
      {
        int app=munizioni_max[arma]-munizioni[arma]; //trova quanti proiettili mancano
        if(caricatori[arma]-app>=0) //se nel caricatore ce ne sono abbastanza 
        {
           caricatori[arma]-=app; //prende proiettili dal caricatore
           munizioni[arma]+=app; //li mette nelle munizioni disponibili per sparare
        } else {  //i proiettili rimasti non bastano a ricaricare tutta l'arma... si usano tutti i proiettili rimasti
                 munizioni[arma]+=caricatori[arma];
                 caricatori[arma]=0;
               }
      }
    }
     
};
