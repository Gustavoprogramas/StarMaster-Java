package starmaster;

import static com.almasb.fxgl.dsl.FXGL.onKey;
import static com.almasb.fxgl.dsl.FXGL.onKeyDown;
import static com.almasb.fxgl.dsl.FXGL.tpf;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;

import javafx.scene.input.KeyCode;
import starmaster.gerenciadores.ModoTesteDeVoo;
import starmaster.ui.FabricaDeCenas;

public class Main extends GameApplication {

    public static final int LARGURA = 800;
    public static final int ALTURA = 600;

    private static ModoDeJogo modo = ModoDeJogo.TESTE_DE_VOO;
    private ModoTesteDeVoo jogo;

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
        settings.setSceneFactory(new FabricaDeCenas());
        settings.setIntroEnabled(false);
    }

    @Override
    protected void initInput() {
        onKey(KeyCode.LEFT, () -> {
            if (jogo != null) {
                jogo.girar(-1, tpf());
            }
        });
        onKey(KeyCode.RIGHT, () -> {
            if (jogo != null) {
                jogo.girar(1, tpf());
            }
        });
        onKey(KeyCode.UP, () -> {
            if (jogo != null) {
                jogo.inclinar(1, tpf());
            }
        });
        onKey(KeyCode.DOWN, () -> {
            if (jogo != null) {
                jogo.inclinar(-1, tpf());
            }
        });
        onKey(KeyCode.W, () -> {
            if (jogo != null) {
                jogo.ajustarVelocidade(1, tpf());
            }
        });
        onKey(KeyCode.S, () -> {
            if (jogo != null) {
                jogo.ajustarVelocidade(-1, tpf());
            }
        });
        onKey(KeyCode.SPACE, () -> {
            if (jogo != null) {
                jogo.atirar();
            }
        });
        onKeyDown(KeyCode.Q, () -> {
            if (jogo != null) {
                jogo.alternarAtracagem();
            }
        });
        onKeyDown(KeyCode.M, () -> {
            if (jogo != null) {
                jogo.alternarMapa();
            }
        });
    }

    @Override
    protected void initGame() {
        jogo = new ModoTesteDeVoo();
        jogo.iniciar();
    }

    @Override
    protected void initUI() {
        jogo.criarHud();
    }

    @Override
    protected void onUpdate(double tpf) {
        if (jogo != null) {
            jogo.atualizar(tpf);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
