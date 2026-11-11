package starmaster.entidades;

import javafx.geometry.Point3D;
import javafx.scene.Group;
import starmaster.gerenciadores.EstadoInimigo;
import starmaster.modelos.ModeloNave;
import starmaster.uteis.GerarAleatorio;

public class NaveInimiga extends Nave {
    private static final int ESCUDO_MAXIMO = 100;
    private static final int PODER_DE_FOGO = 10;

    private static final double VELOCIDADE_INICIAL = 10;
    private static final double VELOCIDADE_MAXIMA = 140;
    private static final double ACELARACAO = 25;

    private static final double DISTANCIA_DE_AVANCO = 300.0;
    private static final double DISTANCIA_DE_RECUO = 140.0;
    private static final double DISTANCIA_MAXIMA_BASE = 50.0;
    private static final double DISTANCIA_DE_ATAQUE = 200.0;
    private static final double TAXA_DE_GIRO=10.0;

    private boolean desviar = false;
    private Point3D direcaoDesvio = new Point3D(0,0,0);

    private static final double TEMPO_DE_ATAQUE = 8.0;
    private static final double INTERVALO_TIRO = 0.15;
    private static final double INTERVALO_REGENERACAO = 1.0;

    private EstadoInimigo estado = EstadoInimigo.PERSEGUICAO;
    private double tempoDeEstado = 0.0;

    private double tempoDesdeTiro = INTERVALO_TIRO;
    private double tempoRegeneracao;

    private ModeloNave modelo = new ModeloNave();
    private Point3D direcao = new Point3D(0, 0, 0);
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

    
        direcaoDesejada = direcaoDesejada.normalize();

        double fatorGiro = Math.min(1.0, TAXA_DE_GIRO * tpf);

        atualizarAcao(tpf, fatorGiro, direcaoDesejada);

        System.out.println("Velocidade: " + velocidade);
    

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
                    tempoDeEstado = 0.0;
                    mudarEstado(EstadoInimigo.ATAQUE);
                }
                break;
            case ATAQUE:
                if (tempoDeEstado >= TEMPO_DE_ATAQUE || distanciaAlvo < DISTANCIA_DE_RECUO){
                    tempoDeEstado = 0.0;
                    mudarEstado(EstadoInimigo.RETIRADA);
                }
                break;
            case RETIRADA:
                if (distanciaAlvo > DISTANCIA_DE_AVANCO){
                    tempoDeEstado = 0.0;
                    mudarEstado(EstadoInimigo.PERSEGUICAO);
                }
                if (distanciaAlvo <= DISTANCIA_MAXIMA_BASE){
                    tempoDeEstado = 0.0;
                    mudarEstado(EstadoInimigo.DESVIO);
                }
                break;
            case DESVIO:
                if (distanciaAlvo > DISTANCIA_DE_AVANCO){
                    tempoDeEstado = 0.0;
                    mudarEstado(EstadoInimigo.PERSEGUICAO);
                }
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
            case DESVIO:
                desviar(fatorGiro, direcaoDesejada);
                acelerar(tpf);
                break;
        }
    }

    private void mudarEstado(EstadoInimigo novoEstado){
        estado = novoEstado;
        tempoDeEstado = 0.0;
    }

    private void desviar(double fatorGiro, Point3D direcaoDesejada){
        if (!desviar){
            direcaoDesvio = direcao.crossProduct(direcaoDesejada).normalize();
            desviar = true;
        }
        direcao = direcao.multiply(1-fatorGiro).add(direcaoDesvio.multiply(fatorGiro)).normalize();
        
        /*if(!desviar){
            direcao = direcao.crossProduct(direcaoDesejada.multiply(-fatorGiro)).normalize();
            desviar = true;
        }
        else{
            //direcao = direcao.multiply(1-fatorGiro).add(direcaoDesejada.multiply(-fatorGiro)).normalize();
            System.out.println("Se funcionar, só deus sabe como");
        }*/
    }

    private void fugir(double fatorGiro, Point3D direcaoDesejada){
        desviar = false;
        direcao = direcao.multiply(1-fatorGiro).add(direcaoDesejada.multiply(-fatorGiro)).normalize();
    }

    private void avancar(double fatorGiro, Point3D direcaoDesejada){
        desviar = false;
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

        if (angulo >= 0.99 && estado == EstadoInimigo.ATAQUE){
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
