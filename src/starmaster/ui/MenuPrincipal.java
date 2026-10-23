package starmaster.ui;

import static com.almasb.fxgl.dsl.FXGL.getUIFactoryService;

import java.util.Random;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import starmaster.Main;
import starmaster.ModoDeJogo;


public class MenuPrincipal extends FXGLMenu {

    public MenuPrincipal() {
        super(MenuType.MAIN_MENU);
        double largura = getAppWidth();
        double altura = getAppHeight();

        Rectangle fundo = new Rectangle(largura, altura, Color.BLACK);
        Group estrelas = new Group();
        Random aleatorio = new Random(1);
        for (int i = 0; i < 160; i++) {
            estrelas.getChildren().add(new Circle(aleatorio.nextDouble() * largura, aleatorio.nextDouble() * altura,
                    0.4 + aleatorio.nextDouble() * 1.2, Color.gray(0.4 + aleatorio.nextDouble() * 0.6)));
        }

        Text titulo = getUIFactoryService().newText("STARMASTER", Color.WHITE, 46);
        Text subtitulo = getUIFactoryService().newText("TESTES", Color.GRAY, 16);
        Region espaco = new Region();
        espaco.setPrefHeight(30);

        Button botaoTeste = criarBotao("VOAR");
        botaoTeste.setOnAction(e -> {
            Main.setModo(ModoDeJogo.TESTE_DE_VOO);
            fireNewGame();   
        });
        Button botaoSair = criarBotao("SAIR");
        botaoSair.setOnAction(e -> fireExit());

        VBox caixa = new VBox(16, titulo, subtitulo, espaco, botaoTeste, botaoSair);
        caixa.setAlignment(Pos.CENTER);
        caixa.setPrefSize(largura, altura);

        getContentRoot().getChildren().addAll(fundo, estrelas, caixa);
    }

    private Button criarBotao(String texto) {
        Button botao = getUIFactoryService().newButton(texto);
        botao.setPrefWidth(260);
        return botao;
    }
}
