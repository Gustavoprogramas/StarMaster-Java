package starmaster.entidades;
import starmaster.mapa.Setor;

public class NaveJogador extends Nave {
  public static final int ENERGIA_MAXIMA = 1000;
  public static final int ESCUDO_MAXIMO = 100;
  public static final int PODER_DE_FOGO = 10;
  public static final double VELOCIDADE = 2.0;

  private static final int CUSTO_TIRO = 5;
  private static final double VELOCIDADE_TIRO = 8.0;
  private static final double ALCANCE_TIRO = 500.0;
  private static final int VELOCIDADE_REGENERACAO = 60;

  private int energia;
  private int energiaMaxima;
  private Setor setorAtual;
  private boolean naBase;
  private double direcao;
  private int fps;

  public NaveJogador(Setor setorInicial) {
    super(0,0,0, ESCUDO_MAXIMO, VELOCIDADE, PODER_DE_FOGO);
    this.energiaMaxima = ENERGIA_MAXIMA;
    this.energia = ENERGIA_MAXIMA;
    this.setorAtual = setorInicial;
    this.naBase = false;
    this.direcao = 0;
  }

  //@Override
  public Projetil atirar() {
    if (naopodeatirar) {
      return null;
    }
    consumirEnergia(CUSTO_TIRO);
    double vx = seno * direcao * VELOCIDADE_TIRO;

    double vz = cosseno * direcao * VELOCIDADE_TIRO
    return new Projetil(x,y,z,vx,vz,poderDeFogo, ALCANCE_TIRO);
    
  }
  //public void girar
  //public boolean viajarParaSucesso
  //public void naBase
  //public void sairBase
  public void recarregar() {
    if (!naBase) { return; }
    energia = energiaMaxima;
    escudo = escudoMaximo;
  }
  //public void receberDano
  //public void atualizar
  public boolean semEnergia() { return energia<=0; }
  //private void consumirEnergia
  // getters:
  public int getEnergia() { return energia; }
  public int getEnergiaMaxima() { return energiaMaxima; }
  public Setor getSetorAtual() { return setorAtual; }
  public boolean isnaBase() { return naBase; }
  public double getDirecao() { return direcao; }
}
