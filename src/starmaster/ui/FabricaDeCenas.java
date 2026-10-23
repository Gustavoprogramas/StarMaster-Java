package starmaster.ui;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.SceneFactory;


public class FabricaDeCenas extends SceneFactory {

    @Override
    public FXGLMenu newMainMenu() {
        return new MenuPrincipal();
    }
}
