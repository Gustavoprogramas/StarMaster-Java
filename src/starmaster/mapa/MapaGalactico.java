package starmaster.mapa;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MapaGalactico {
    private static final int[][] DIRECOES = {{-1,0}, {1,0}, {0, -1}, {0, 1}};
    private final Setor [][] grade;
    
    public MapaGalactico(int linhas, int colunas){
        grade = new Setor[linhas][colunas];
        for (int l = 0; l<linhas; l++){
            for (int c=0; c<colunas; c++){
                grade[l][c] = new Setor(l, c, TipoDeSetor.VAZIO);
            }
        }
    }

    public static MapaGalactico deTexto (String... linhas){
        MapaGalactico mapa = new MapaGalactico(linhas.length, linhas[0].length());
        for (int l = 0; l<linhas.length; l++){
            for(int c=0; c<linhas[l].length(); c++) {
                mapa.grade[l][c].setTipo(TipoDeSetor.doSimbolo(linhas[l].charAt(c)));

            }
        }
        return mapa;

    }

    //fazer sorteio do mapa.

    public static MapaGalactico sortear(int linhas, int colunas, int base, int inimigos, int meteotos, Random aleatorio){
        if(base + inimigos + meteotos >= linhas *  colunas){
            throw new IllegalArgumentException("Muita coisa no mapa");
        }
        MapaGalactico mapa = new MapaGalactico(linhas, colunas);
        mapa.espalhar(TipoDeSetor.BASE, base, aleatorio);
        mapa.espalhar(TipoDeSetor.INIMIGO, inimigos, aleatorio);
        mapa.espalhar(TipoDeSetor.METEOROS, meteotos, aleatorio);
        return mapa;
    }
    private void espalhar (TipoDeSetor tipo, int quantidade, Random aleatorio){
        for (int i = 0; i<quantidade; i++) {
            SorteioMapa(aleatorio).setTipo(tipo);
        }
    }
    public Setor SorteioMapa (Random aleatorio) {
        Setor s;
        do {
            s = grade[aleatorio.nextInt(getLinhas())][aleatorio.nextInt(getColunas())];
        }while (s.getTipo() !=TipoDeSetor.VAZIO);
        return s;
    }
   
    

    public List<Setor> vizinhos(Setor setor){
        List<Setor> resultado = new ArrayList<>();
        for (int[] d: DIRECOES){
            int l= setor.getLinha() + d[0];
            int c= setor.getColuna() + d[1];
            if (l>=0 && l<getLinhas() && c>=0 && c < getColunas()){
                resultado.add(grade[l][c]);
            }
        }
        return resultado;
    }

    public List<Setor> getBases() {
        List<Setor> bases = new ArrayList<>();
        for(Setor[] linha : grade){
            for (Setor s:linha){
                if (s.getTipo() == TipoDeSetor.BASE){
                    bases.add(s);
                }

            }
        }
        return bases;
    }
    public Setor getSetor(int linha, int coluna){
        return grade[linha][coluna];
    }
    public int getLinhas(){
        return grade.length;
    }
    public int getColunas(){
        return grade[0].length;
    }
}
