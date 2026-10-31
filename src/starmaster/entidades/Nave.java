package starmaster.entidades;

import javafx.geometry.Point3D;
import starmaster.interfaces.Danificavel;


public abstract class Nave extends EntidadeJogo implements Danificavel {

    protected int escudo;
    protected int escudoMaximo;
    protected double velocidade;
    protected int poderDeFogo;
    protected Point3D posicao;

    protected Nave(int escudoMaximo, double velocidadeInicial, int poderDeFogo, Point3D posicao) {
        this.escudoMaximo = escudoMaximo;
        this.escudo = escudoMaximo;
        this.velocidade = velocidadeInicial;
        this.poderDeFogo = poderDeFogo;
        this.posicao = posicao;
    }


    public abstract boolean atirar();

    @Override
    public void receberDano(int quantidade) {
        if (quantidade <= 0 || estaDestruido()) {
            return;
        }
        escudo = Math.max(0, escudo - quantidade);
        if (escudo == 0) {
            desativar();
        }
    }

    @Override
    public boolean estaDestruido() {
        return escudo <= 0;
    }

    public int getEscudo() {
        return escudo;
    }

    public int getEscudoMaximo() {
        return escudoMaximo;
    }

    public double getVelocidade() {
        return velocidade;
    }

    public int getPoderDeFogo() {
        return poderDeFogo;
    }
}
