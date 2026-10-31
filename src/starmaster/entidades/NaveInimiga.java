package starmaster.entidades;

import java.util.Random;
import javafx.geometry.Point3D;

import javafx.scene.paint.Color;
import javafx.scene.Group;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import starmaster.uteis.*;

public class NaveInimiga extends Nave {
    private static final Random SORTEIO = new Random();
    private static final Color[] CORES_ROCHA = {
        Color.web("#00d9ff"), Color.web("#1d00c2"), Color.web("rgb(0, 4, 211)"), Color.web("#0400ff")
    };

    public static final int ESCUDO_MAXIMO = 100;
    public static final int PODER_DE_FOGO = 50;


    public static final double VELOCIDADE_INICIAL = 20;
    public static final double VELOCIDADE_MAXIMA = 120;
    public static final double ACELARACAO = 55;

    public static final double INTERVALO_TIRO = 0.25;
    public static final double INTERVALO_REGENERACAO = 1.0;

    private double tempoDesdeTiro = INTERVALO_TIRO;
    private double tempoRegeneracao;

    private Group modelo = new Group();

    public NaveInimiga(){
        super(ESCUDO_MAXIMO, VELOCIDADE_INICIAL, PODER_DE_FOGO, GerarPontoAleatorio.gerarPonto());
        //System.out.println("Nave inimiga criada na posição: " + posicao);
        montarModelo();
    }

    private void montarModelo(){
        Box nucleo = caixa(3.0);
        modelo.getChildren().add(nucleo);
    }

    private Box caixa(double lado) {
        Box b = new Box(lado, lado * (0.7 + SORTEIO.nextDouble() * 0.5), lado * (0.7 + SORTEIO.nextDouble() * 0.5));
        b.setMaterial(new PhongMaterial(CORES_ROCHA[SORTEIO.nextInt(CORES_ROCHA.length)]));
        b.getTransforms().add(new Rotate(SORTEIO.nextDouble() * 360, eixoAleatorio()));
        return b;
    }

    private static Point3D eixoAleatorio() {
        Point3D p;
        do {
            p = new Point3D(SORTEIO.nextDouble() * 2 - 1, SORTEIO.nextDouble() * 2 - 1, SORTEIO.nextDouble() * 2 - 1);
        } while (p.magnitude() < 0.1);
        return p.normalize();
    }

    @Override
    public void atualizar(double tpf, Point3D novaPosicao) {
        tempoDesdeTiro += tpf;

        velocidade = Math.max(VELOCIDADE_INICIAL, Math.min(VELOCIDADE_MAXIMA, velocidade + ACELARACAO * tpf));

        tempoRegeneracao += tpf;
        if (tempoRegeneracao >= INTERVALO_REGENERACAO) {
            tempoRegeneracao = 0;
            if (escudo < escudoMaximo) {
                escudo++;
            }
        }
    }

    @Override
    public boolean atirar() {
        return true;
    }

    @Override 
    public void receberDano(int quantidade){

    }

    @Override 
    public boolean estaDestruido(){
        return this.escudo <= 0;
    }

    public Point3D getPosicao() {
        return posicao;
    }

    public Group getModelo() {
        return modelo;
    }
}
