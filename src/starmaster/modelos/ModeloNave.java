package starmaster.modelos;

import javafx.scene.paint.Color;

public class ModeloNave extends ModeloPadrao {
    private static final Color[] CORES_NAVE = {
        Color.web("#00d9ff"), Color.web("#1d00c2"), Color.web("rgb(0, 4, 211)"), Color.web("#0400ff")
    };

    public ModeloNave() {
        super();
        montarModelo();
    }

    private void montarModelo(){
        // Corpo central
        modelo.getChildren().add(criarPecaBox(3, 2, 12, Color.DARKRED, 0, 0, 0));

        // Ponta frontal
        modelo.getChildren().add(criarPecaBox(2, 1.5, 3, Color.RED, 0, 0, -7));

        // Asas
        modelo.getChildren().addAll(criarPecaBox(7, 1, 4, Color.DARKRED, -4, 0, 1), criarPecaBox(7, 1, 4, Color.DARKRED, 4, 0, 1));

        // Motores
        modelo.getChildren().addAll(criarPecaBox(1.5, 1.5, 4, Color.DODGERBLUE, -5, 0, 4), criarPecaBox(1.5, 1.5, 4, Color.DODGERBLUE, 5, 0, 4));

        // Núcleo energético
        modelo.getChildren().add(criarPecaBox(1.5, 1.5, 2, Color.YELLOW, 0, 0, -4));
    }
}
