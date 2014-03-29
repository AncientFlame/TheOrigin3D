package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.ui.Picture;

public class GuiGame 
{
  public Picture pointer;  
  GuiGame(AssetManager asset)
  {
    pointer=new Picture("pointerimagine");
    pointer.setImage(asset,"Interface/pointer.jpg", true);
    pointer.setPosition(Settings.system.getWidth()/2,Settings.system.getHeight()/2); 
    pointer.setWidth(10);
    pointer.setHeight(10);
  }
};
