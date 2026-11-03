package starmaster.entidades;

import javafx.geometry.Point3D;

import javafx.scene.Group;
import starmaster.uteis.*;
//import starmaster.interfaces.Danificavel;
import starmaster.gerenciadores.EstadoInimigo;
import starmaster.modelos.ModeloNave;

public class NaveInimiga extends Nave {
    public static final int ESCUDO_MAXIMO = 100;
    public static final int PODER_DE_FOGO = 10;


    public static final double VELOCIDADE_INICIAL = 10;
    public static final double VELOCIDADE_MAXIMA = 80;
    public static final double ACELARACAO = 15;

    public static final double DISTANCIA_DE_AVANCO = 200.0;
    public static final double DISTANCIA_DE_RECUO = 160.0;
    private static final double DISTANCIA_DE_ATAQUE = 200.0;
    private static final double TAXA_DE_GIRO=8.0;

    private static final double TEMPO_DE_ATAQUE = 5.0;
    private static final double INTERVALO_TIRO = 0.25;
    private static final double INTERVALO_REGENERACAO = 1.0;

    private EstadoInimigo estado = EstadoInimigo.PERSEGUICAO;
    private double tempoDeEstado = 0.0;

    private double tempoDesdeTiro = INTERVALO_TIRO;
    private double tempoRegeneracao;

    private ModeloNave modelo = new ModeloNave();
    private Point3D direcao = new Point3D(0, 0, -1);
    private Point3D direcaoDesejada = new Point3D(0,0,0);
    private double distanciaAlvo = 0.0;

    public NaveInimiga(){
        super(ESCUDO_MAXIMO, VELOCIDADE_INICIAL, PODER_DE_FOGO, GerarAleatorio.gerarPonto());
    }

    @Override
    public void atualizar(double tpf, Point3D novaPosicao) {
        tempoDesdeTiro += tpf;

        Point3D posicaoAlvo = definirAlvo(novaPosicao);

        distanciaAlvo = posicao.distance(posicaoAlvo);

        direcaoDesejada = posicaoAlvo.subtract(posicao);

        atualizarEstado(tpf);

        /*System.out.println("Direção atual: " + direcao);
        System.out.println("Direção desejada: " + direcaoDesejada);
        System.out.println("Produto escalar: " + direcao.dotProduct(direcaoDesejada.normalize()));*/

        if (direcaoDesejada.magnitude() > 0.001){
            direcaoDesejada = direcaoDesejada.normalize();

            double fatorGiro = Math.min(1.0, TAXA_DE_GIRO * tpf);

            atualizarAcao(tpf, fatorGiro, direcaoDesejada);
        }

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

    private Point3D definirAlvo(Point3D posicaoJogador){
        Point3D posicaoAlvo = posicaoJogador;

        if (!posicoesBase.isEmpty()){
            for (Point3D posicaoBase : posicoesBase){
                if(posicao.distance(posicaoBase) < posicao.distance(posicaoAlvo)){
                    posicaoAlvo = posicaoBase;
                }
            }
        }

        return posicaoAlvo;
    }

    private void atualizarEstado(double tpf){
        tempoDeEstado += tpf;

        switch (estado){
            case PERSEGUICAO:
                if (distanciaAlvo <= DISTANCIA_DE_ATAQUE){
                    mudarEstado(EstadoInimigo.ATAQUE);
                }
                break;
            case ATAQUE:
                if (tempoDeEstado >= TEMPO_DE_ATAQUE || distanciaAlvo < DISTANCIA_DE_RECUO){
                    mudarEstado(EstadoInimigo.RETIRADA);
                }
                break;
            case RETIRADA:
                if (distanciaAlvo > DISTANCIA_DE_AVANCO){
                    mudarEstado(EstadoInimigo.PERSEGUICAO);
                }
                break;
        }
    }

    private void atualizarAcao(double tpf, double fatorGiro, Point3D direcaoDesejada){
        switch (estado){
            case PERSEGUICAO:
                avancar(fatorGiro, direcaoDesejada);
                acelerar(tpf);
                break;
            case ATAQUE:
                avancar(fatorGiro, direcaoDesejada);
                desacelerar(tpf);
                break;
            case RETIRADA:
                fugir(fatorGiro, direcaoDesejada);
                acelerar(tpf);
                break;
        }
    }

    private void mudarEstado(EstadoInimigo novoEstado){
        estado = novoEstado;
        tempoDeEstado = 0.0;
    }

    private void fugir(double fatorGiro, Point3D direcaoDesejada){
        direcao = direcao.multiply(1-fatorGiro).add(direcaoDesejada.multiply(-fatorGiro)).normalize();
    }

    private void avancar(double fatorGiro, Point3D direcaoDesejada){
        direcao = direcao.multiply(1-fatorGiro).add(direcaoDesejada.multiply(fatorGiro)).normalize();
    }

    private void acelerar(double tpf){
        velocidade = Math.max(VELOCIDADE_INICIAL, Math.min(VELOCIDADE_MAXIMA, velocidade + ACELARACAO * tpf));
    }

    private void desacelerar(double tpf){
        velocidade = Math.max(VELOCIDADE_INICIAL, Math.min(VELOCIDADE_MAXIMA, velocidade - ACELARACAO * tpf));
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
        return modelo.getModelo();
    }

    public EstadoInimigo getEstado() {
        return estado;
    }
}
