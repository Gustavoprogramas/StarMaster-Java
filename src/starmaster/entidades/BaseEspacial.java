package starmaster.entidades;

import javafx.geometry.Point3D;
import starmaster.uteis.GerarAleatorio;

public class BaseEspacial extends EntidadeJogo{
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

    public int getVida(){
        return vida;
    }

    public int getEscudo(){
        return escudo;
    }
}
