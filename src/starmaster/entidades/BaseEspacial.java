package starmaster.entidades;

import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.transform.Rotate;
import starmaster.uteis.GerarAleatorio;
import javafx.scene.shape.Box;

public class BaseEspacial extends EntidadeJogo{
    private static final Color[] CORES_NAVE = {
        Color.web("#d400ff"), Color.web("#991687"), Color.web("rgb(193, 22, 216)"), Color.web("rgb(223, 139, 240)")
    };

    private Group modelo = new Group();
    private final int ESCUDO_MAXIMO = 1000;
    private final int VIDA_MAXIMA = 500;
    private final double INTERVALO_REGENERACAO = 1.5;
    
    private int escudo;
    private int vida;
    private Point3D posicao = GerarAleatorio.gerarPonto();

    private double tempoRegeneracao;

    public BaseEspacial(){
        escudo = ESCUDO_MAXIMO;
        vida = VIDA_MAXIMA;
        montarModelo();
    }

    @Override 
    public void atualizar(double tpf, Point3D novaPosicao){
        tempoRegeneracao += tpf;
        if(tempoRegeneracao >= INTERVALO_REGENERACAO){
            tempoRegeneracao = 0;
            if(escudo < ESCUDO_MAXIMO){
                escudo++;
            }
        }
    }

    private void montarModelo(){
        Box nucleo = caixa(5.0);
        modelo.getChildren().add(nucleo);
    }

    private Box caixa(double lado) {
        Box b = new Box(lado, lado * (0.7 + GerarAleatorio.gerarReal() * 0.5), lado * (0.7 + GerarAleatorio.gerarReal() * 0.5));
        b.setMaterial(new PhongMaterial(CORES_NAVE[GerarAleatorio.gerarInteiro(CORES_NAVE.length)]));
        b.getTransforms().add(new Rotate(GerarAleatorio.gerarReal() * 360, eixoAleatorio()));
        return b;
    }

    private static Point3D eixoAleatorio() {
        Point3D p;
        do {
            p = new Point3D(GerarAleatorio.gerarReal() * 2 - 1, GerarAleatorio.gerarReal() * 2 - 1, GerarAleatorio.gerarReal() * 2 - 1);
        } while (p.magnitude() < 0.1);
        return p.normalize();
    }

    public int getVida(){
        return vida;
    }

    public int getEscudo(){
        return escudo;
    }

    public Point3D getPosicao(){
        return posicao;
    }

    public Group getModelo(){
        return modelo;
    }
}