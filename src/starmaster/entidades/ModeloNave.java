package starmaster.entidades;

import javafx.scene.paint.Color;
import javafx.scene.Group;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;

public class ModeloNave {
    private static final Color[] CORES_NAVE = {
        Color.web("#00d9ff"), Color.web("#1d00c2"), Color.web("rgb(0, 4, 211)"), Color.web("#0400ff")
    };
    
    private Group modelo;

    public ModeloNave() {
        modelo = new Group();
        montarModelo();
    }

    private void montarModelo(){
        // Corpo central
        modelo.getChildren().add(criarPeca(3, 2, 12, Color.DARKRED, 0, 0, 0));

        // Ponta frontal
        modelo.getChildren().add(criarPeca(2, 1.5, 3, Color.RED, 0, 0, -7));

        // Asas
        modelo.getChildren().addAll(criarPeca(7, 1, 4, Color.DARKRED, -4, 0, 1), criarPeca(7, 1, 4, Color.DARKRED, 4, 0, 1));

        // Motores
        modelo.getChildren().addAll(criarPeca(1.5, 1.5, 4, Color.DODGERBLUE, -5, 0, 4), criarPeca(1.5, 1.5, 4, Color.DODGERBLUE, 5, 0, 4));

        // Núcleo energético
        modelo.getChildren().add(criarPeca(1.5, 1.5, 2, Color.YELLOW, 0, 0, -4));
    }

    private Box criarPeca(double largura, double altura, double profundidade, Color cor, double x, double y, double z){
        Box peca = new Box(largura, altura, profundidade);

        peca.setMaterial(new PhongMaterial(cor));

        peca.setTranslateX(x);
        peca.setTranslateY(y);
        peca.setTranslateZ(z);

        return peca;
    }

    public Group getModelo() {
        return modelo;
    }

}
