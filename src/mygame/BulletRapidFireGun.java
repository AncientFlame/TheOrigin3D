package mygame;

import com.jme3.math.Ray;
import com.jme3.math.Vector3f;


public class BulletRapidFireGun 
{
  Ray bullet_dir;
  float damage;
  int indice=-1; //indice collisioni scena e mob
  float dist_s=-1; //distanza collisioni scena e mob
    BulletRapidFireGun(float dam,Vector3f pos,Vector3f dir)
    {
       damage=dam; 
       bullet_dir=new Ray(pos,dir);
    }
};
