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
    private static final Color[] CORES_NAVE = {
        Color.web("#00d9ff"), Color.web("#1d00c2"), Color.web("rgb(0, 4, 211)"), Color.web("#0400ff")
    };

    public static final int ESCUDO_MAXIMO = 100;
    public static final int PODER_DE_FOGO = 10;


    public static final double VELOCIDADE_INICIAL = 10;
    public static final double VELOCIDADE_MAXIMA = 50;
    public static final double ACELARACAO = 10;

    public static final double INTERVALO_TIRO = 0.25;
    public static final double INTERVALO_REGENERACAO = 1.0;

    private double tempoDesdeTiro = INTERVALO_TIRO;
    private double tempoRegeneracao;

    private Group modelo = new Group();
    private Point3D direcao = new Point3D(0, 0, -1);
    private Point3D direcaoDesejada = new Point3D(0,0,0);
    private double distanciaAlvo = 0.0;
    private static final double DISTANCIA_DE_ATAQUE = 100.0;
    private static final double TAXA_DE_GIRO=20.0;

    public NaveInimiga(){
        super(ESCUDO_MAXIMO, VELOCIDADE_INICIAL, PODER_DE_FOGO, /*new Point3D(0,0,100)*/GerarPontoAleatorio.gerarPonto());
        //System.out.println("Nave inimiga criada na posição: " + posicao);
        montarModelo();
    }

    private void montarModelo(){
        Box nucleo = caixa(3.0);
        modelo.getChildren().add(nucleo);
    }

    private Box caixa(double lado) {
        Box b = new Box(lado, lado * (0.7 + SORTEIO.nextDouble() * 0.5), lado * (0.7 + SORTEIO.nextDouble() * 0.5));
        b.setMaterial(new PhongMaterial(CORES_NAVE[SORTEIO.nextInt(CORES_NAVE.length)]));
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

        distanciaAlvo = posicao.distance(novaPosicao);

        direcaoDesejada = novaPosicao.subtract(posicao);

        /*System.out.println("Direção atual: " + direcao);
        System.out.println("Direção desejada: " + direcaoDesejada);
        System.out.println("Produto escalar: " + direcao.dotProduct(direcaoDesejada.normalize()));*/

        if (direcaoDesejada.magnitude() > 0.001){
            direcaoDesejada = direcaoDesejada.normalize();

            double fatorGiro = Math.min(1.0, TAXA_DE_GIRO * tpf);

            direcao = direcao.multiply(1-fatorGiro).add(direcaoDesejada.multiply(fatorGiro)).normalize();
        }

        velocidade = Math.max(VELOCIDADE_INICIAL, Math.min(VELOCIDADE_MAXIMA, velocidade + ACELARACAO * tpf));

        posicao = posicao.add(direcao.multiply(velocidade * tpf));

        /*System.out.println("Nave inimiga na posição " + posicao);
        System.out.println("Nave inimiga se movendo em direção: " + direcao);
        System.out.println("Nave Aliada na posição " + novaPosicao);*/

        tempoRegeneracao += tpf;
        if (tempoRegeneracao >= INTERVALO_REGENERACAO) {
            tempoRegeneracao = 0;
            if (escudo < escudoMaximo) {
                escudo++;
            }
        }


    }

    private boolean podeAtirar(Point3D direcaoAlvo){
        if (direcaoAlvo.magnitude() < 0.001){
            return false;
        }

        double angulo = this.direcao.dotProduct(direcaoAlvo.normalize());

        if (angulo >= 0.90 && distanciaAlvo < DISTANCIA_DE_ATAQUE){
            //System.out.println("Nave do Jogador avistada! Abrindo FOGO!");
            return true;
        }

        return false;
    }

    @Override
    public boolean atirar() {
        if (podeAtirar(direcaoDesejada) && tempoDesdeTiro >= INTERVALO_TIRO){
            tempoDesdeTiro = 0;
            //System.out.println("Nave inimiga atirou!");
            return true;
        }
        return false;
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

    public Point3D getDirecao() {
        return direcao;
    }

    public Group getModelo() {
        return modelo;
    }
}
