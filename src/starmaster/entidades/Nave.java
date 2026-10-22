package starmaster.entidades;

import starmaster.interfaces.Danificavel;


public abstract class Nave extends EntidadeJogo implements Danificavel {

    protected int escudo;
    protected int escudoMaximo;
    protected double velocidade;
    protected int poderDeFogo;

    protected Nave(int escudoMaximo, double velocidadeInicial, int poderDeFogo) {
        this.escudoMaximo = escudoMaximo;
        this.escudo = escudoMaximo;
        this.velocidade = velocidadeInicial;
        this.poderDeFogo = poderDeFogo;
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
