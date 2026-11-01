package starmaster.mapa;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import starmaster.entidades.Nave;

public class Navegador {
    private final MapaGalactico mapa;
    public Navegador (MapaGalactico mapa) {
        this.mapa = mapa;

    }

    public List<Setor> encontrarCaminho (Setor origem, Setor destino){
        PriorityQueue<No> abertos = new PriorityQueue<>(
            Comparator.comparingInt(No::f).thenComparingInt(No::h));
            Set<Setor> fechados = new HashSet<>();
            Map<Setor, Integer> melhorG = new HashMap<>();
            abertos.add(new No(origem, 0, origem.distanciaManhattan(destino), null));
            melhorG.put(origem, 0 );

            
            while(!abertos.isEmpty()){
                No atual = abertos.poll();
                if(atual.setor() == destino) {
                    return reconstruirRota(atual);
                }
                if (!fechados.add(atual.setor())){
                    continue;
                }
                for(Setor vizinho:mapa.vizinhos(atual.setor())){ //criar func vizinhos no mapagalactiooc
                    if (vizinho.isBloqueado()||fechados.contains(vizinho)){
                        continue;
                    }
                    int gNovo = atual.g() + vizinho.getCusto();
                    Integer gAntigo = melhorG.get(vizinho);
                    if (gAntigo == null || gNovo < gAntigo){
                        melhorG.put(vizinho, gNovo);
                        abertos.add(new No(vizinho, gNovo, vizinho.distanciaManhattan(destino), atual));

                        
                    }
                }
            }
            return Collections.emptyList();


    }//
    public List <Setor> rotaAteBaseMaisProxima(Setor origem) {
        int menorCusto = Integer.MAX_VALUE;
        List<Setor> melhor = Collections.emptyList();
        for (Setor base : mapa.getBases()){
            List<Setor> rota = encontrarCaminho(origem, base);
             if(!rota.isEmpty() && custoDaRota(rota) < menorCusto) {
                melhor = rota;
                menorCusto = custoDaRota(rota);
             }
        }
        return  melhor;
    }
    public static int custoDaRota(List<Setor> rota){
        int total = 0;
        for (int i=0; i<rota.size(); i++){
            total += rota.get(i).getCusto();
        }
        return total;
    }




    private static List<Setor> reconstruirRota(No fim) {
        LinkedList<Setor> rota = new LinkedList<>();
        for (No no=fim; no!=null; no=no.pai()){
            rota.addFirst(no.setor());
        }
        return rota;
    }

    private record No(Setor setor, int g, int h, No pai){
        int f() {
            return g+h;
        }
    }
}
