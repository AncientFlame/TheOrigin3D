package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.ui.Picture;

public class GuiGame 
{
  public Picture pointer;  
  GuiGame(AssetManager asset)
  {
    pointer=new Picture("pointerimagine");
    pointer.setImage(asset,"Textures/asphalt.jpg", true);
    pointer.setPosition(Settings.system.getWidth()/2,Settings.system.getHeight()/2); 
    pointer.setWidth(5);
    pointer.setHeight(5);
  }
};
