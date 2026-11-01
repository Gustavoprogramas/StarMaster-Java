package starmaster.uteis;

import javafx.geometry.Point3D;
import java.util.Random;

public class GerarAleatorio {
    public static Random random = new Random();

    public static Point3D gerarPonto(){
        return new Point3D(random.nextDouble()*200-100,random.nextDouble()*200-100,random.nextDouble()*200-100);
    }

    public static double gerarReal(){
        return random.nextDouble();
    }

    public static int gerarInteiro(int max){
        return random.nextInt(max);
    }
}
