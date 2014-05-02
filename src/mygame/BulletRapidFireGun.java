package mygame;

import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import java.util.concurrent.Callable;


public class BulletRapidFireGun 
{
  Ray bullet_dir;
  float damage;
  int indice=-1; //indice collisioni scena e mob
  float dist_s=-1,dist_m=-1; //distanza collisioni scena e mob
  Main appl;
    BulletRapidFireGun(float dam,Vector3f pos,Vector3f dir,Main app)
    {
       damage=dam; 
       appl=app;
       bullet_dir=new Ray(pos,dir);
    }
    
    public Callable bullMob=new Callable() //collisioni proiettili mob thread[3]
    {
      public Object call()
      {
        CollisionResults result=new CollisionResults();
        for(int i=0; i<appl.round; i++)
        {
          if(appl.mob[i]!=null)
          {
           bullet_dir.clone().collideWith(appl.mob[i].model.clone().getWorldBound(),result); 
            //  appl.mob[i].model.clone().collideWith(bullet_dir.clone(),result);
           if(result.size()>0) //trova collisione più vicina
             for(int j=0; j<result.size(); j++)
               if(result.getCollision(j).getDistance()<dist_m || dist_m==-1)
               {
                  dist_m=result.getCollision(j).getDistance();
                  indice=i;
               }  
          }
        }
        return null;  
      }
    };
    
    public Callable bullScene=new Callable() //collisioni proiettili scena thread[2]
    {
         public Object call()
         {
              CollisionResults result=new CollisionResults(); 
              appl.scena.SceneModel.clone().collideWith(bullet_dir.clone(),result); //calcola collisioni
              if(result.size()>0) //ci sono collisioni
                for(int i=0; i<result.size(); i++) //trova la collisione più vicina
                  if(result.getCollision(i).getDistance()<dist_s || dist_s==-1 )
                    dist_s=result.getCollision(i).getDistance(); 
           return null; 
         }
    };
    
};
