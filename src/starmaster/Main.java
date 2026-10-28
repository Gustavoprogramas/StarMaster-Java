package starmaster;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import static com.almasb.fxgl.dsl.FXGL.onKey;
import static com.almasb.fxgl.dsl.FXGL.onKeyDown;
import static com.almasb.fxgl.dsl.FXGL.tpf;

import javafx.scene.input.KeyCode;
import starmaster.gerenciadores.ModoTesteDeVoo;
import starmaster.ui.FabricaDeCenas;


public class Main extends GameApplication {

    public static final int LARGURA = 800;
    public static final int ALTURA = 600;

    private static ModoDeJogo modo = ModoDeJogo.TESTE_DE_VOO;
    private ModoTesteDeVoo testeDeVoo;

    public static void setModo(ModoDeJogo novoModo) {
        modo = novoModo;
    }

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("StarMaster Java");
        settings.setVersion("0.1");
        settings.setWidth(LARGURA);
        settings.setHeight(ALTURA);
        settings.set3D(true);                         
        settings.setMainMenuEnabled(true);            
        settings.setGameMenuEnabled(true);            
        settings.setSceneFactory(new FabricaDeCenas()); // troca o menu padrão do FXGL pelo nosso
        settings.setIntroEnabled(false);
    }

    @Override
    protected void initInput() {
        onKey(KeyCode.LEFT, () -> {
            if (testeDeVoo != null) {
                testeDeVoo.girar(-1, tpf());
            }
        });
        onKey(KeyCode.RIGHT, () -> {
            if (testeDeVoo != null) {
                testeDeVoo.girar(1, tpf());
            }
        });
        onKey(KeyCode.UP, () -> {
            if (testeDeVoo != null) {
                testeDeVoo.inclinar(1, tpf());
            }
        });
        onKey(KeyCode.DOWN, () -> {
            if (testeDeVoo != null) {
                testeDeVoo.inclinar(-1, tpf());
            }
        });
        onKey(KeyCode.SPACE, () -> {
            if (testeDeVoo != null) {
                testeDeVoo.atirar();
            }
        });
        onKeyDown(KeyCode.H, () -> {
            if (testeDeVoo != null) {
                testeDeVoo.simularDano();
            }
        });
        onKeyDown(KeyCode.R, () -> {
            if (testeDeVoo != null) {
                testeDeVoo.recarregar();
            }
        });
    }

    @Override
    protected void initGame() {
        testeDeVoo = null;
        if (modo == ModoDeJogo.TESTE_DE_VOO) {
            testeDeVoo = new ModoTesteDeVoo();
            testeDeVoo.iniciar();
        }
      
    }

    @Override
    protected void initUI() {
        if (testeDeVoo != null) {
            testeDeVoo.criarHud();
        }
    }

    @Override
    protected void onUpdate(double tpf) {
        if (testeDeVoo != null) {
            testeDeVoo.atualizar(tpf);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
