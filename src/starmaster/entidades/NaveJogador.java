package starmaster.entidades;

import javafx.geometry.Point3D;

public class NaveJogador extends Nave {

    public static final int ENERGIA_MAXIMA = 1000;
    public static final int ESCUDO_MAXIMO = 100;
    public static final int PODER_DE_FOGO = 20;

    public static final double VELOCIDADE_INICIAL = 0;     
    public static final double VELOCIDADE_MAXIMA = 10;
    public static final double ACELERACAO = 40;            
    private static final double VELOCIDADE_SEM_ENERGIA = 15;

    private static final int CUSTO_TIRO = 5;
    private static final double INTERVALO_TIRO = 0.15;    
    private static final double CONSUMO_VOO = 3;          
    private static final double INTERVALO_REGENERACAO = 0.5;

    private int energia;
    private final int energiaMaxima;
    private double tempoDesdeTiro = INTERVALO_TIRO;
    private double consumoAcumulado;
    private double tempoRegeneracao;

    public NaveJogador() {
        super(ESCUDO_MAXIMO, VELOCIDADE_INICIAL, PODER_DE_FOGO, new Point3D(0,0,0));
        this.energiaMaxima = ENERGIA_MAXIMA;
        this.energia = ENERGIA_MAXIMA;
    }

    @Override
    public void atualizar(double tpf, Point3D novaPosicao) {
        posicao = novaPosicao;
        System.out.println("Nave na posição " + posicao);
        tempoDesdeTiro += tpf;
        if (semEnergia()) {
        
            velocidade = Math.max(VELOCIDADE_SEM_ENERGIA, velocidade - ACELERACAO * tpf);
            return;
        }
        velocidade = Math.min(VELOCIDADE_MAXIMA, velocidade + ACELERACAO * tpf);

       
        consumoAcumulado += CONSUMO_VOO * (velocidade / VELOCIDADE_MAXIMA) * tpf;
        if (consumoAcumulado >= 1) {
            int inteiro = (int) consumoAcumulado;
            consumirEnergia(inteiro);
            consumoAcumulado -= inteiro;
        }


        tempoRegeneracao += tpf;
        if (tempoRegeneracao >= INTERVALO_REGENERACAO) {
            tempoRegeneracao = 0;
            if (escudo < escudoMaximo) {
                escudo++;
                consumirEnergia(1);
            }
        }
    }

    @Override
    public boolean atirar() {
        if (energia <= CUSTO_TIRO || tempoDesdeTiro < INTERVALO_TIRO) {
            return false;
        }
        consumirEnergia(CUSTO_TIRO);
        tempoDesdeTiro = 0;
        return true;
    }

    
    @Override
    public void receberDano(int quantidade) {
        if (quantidade <= 0) {
            return;
        }
        int absorvido = Math.min(escudo, quantidade);
        escudo -= absorvido;
        int excedente = quantidade - absorvido;
        consumirEnergia(quantidade / 2 + excedente * 3);
    }


    @Override
    public boolean estaDestruido() {
        return semEnergia();
    }

  
    public void recarregar() {
        energia = energiaMaxima;
        escudo = escudoMaximo;
        ativo = true;
    }

    public boolean semEnergia() {
        return energia <= 0;
    }

    private void consumirEnergia(int quantidade) {
        energia = Math.max(0, energia - quantidade);
    }

    public int getEnergia() {
        return energia;
    }

    public int getEnergiaMaxima() {
        return energiaMaxima;
    }
}
