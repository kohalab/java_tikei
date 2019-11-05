import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Color;
import javax.swing.JFrame;

public class java_tikei extends Canvas {

    static int width = 640;
    static int height = 480;

    static double frameRate;

    static Noise noise;

    static int frameCount;

    static long fcoldnano,mf;

    public static void main(String[] args) {
        JFrame frame = new JFrame("java canvas");
        Canvas canvas = new java_tikei();
        canvas.setSize(width, height);
        frame.add(canvas);
        frame.pack();
        frame.setVisible(true);
        long oldnano = System.nanoTime();
        long framenano = (1000*1000*1000)/60;
        long osoldnano = System.nanoTime();
        noise = new Noise();
        setup();
        while(true){
          //
          if(System.nanoTime()-oldnano >= framenano){
            osoldnano =  System.nanoTime()-osoldnano;
            frameRate = 1d/(osoldnano/1000d/1000d/1000d);
            double syori = (mf/1000d/1000d);
            System.out.println("frameRate:"+ String.format("%.3f", frameRate) );
            System.out.println("処理時間  :"+	String.format("%.3f", syori)+"ms");
            canvas.repaint();
            oldnano = System.nanoTime();
            osoldnano = System.nanoTime();
          }
          //
        }
    }

    int[][] map = new int[80][60];

    public static void setup(){

    }

    public void paint(Graphics g) {
      fcoldnano = System.nanoTime();
      //
      for(int y = 0;y < map[0].length;y++){
          for(int x = 0;x < map.length;x++){
            map[x][y] = 0;
            //

            int X = x+(frameCount/2);
            int Y = y;

            if(noise.pnoise(X/60d,Y/30d,0) < (((double)y/map[0].length)/1.5)+0.1 ){
              map[x][y] = 1;
            }
            //
        }
      }
      for(int y = map[0].length-1;y > 0;y--){
          for(int x = 0;x < map.length;x++){
            if(map[x][y] == 1){
              if(map[x][y-1] == 1){
                map[x][y] = 2;
              }
            }
        }
      }
      //
      g.setColor(new Color(0xbb,0xee,0xff));
      g.fillRect(0, 0, width, height);

      for(int y = 0;y < map[0].length;y++){
          for(int x = 0;x < map.length;x++){
            //
            if(map[x][y] == 1){
              g.setColor(new Color(0x66,0xee,0x33));
              g.fillRect(x*8,y*8,8,8);
            }
            if(map[x][y] == 2){
              g.setColor(new Color(0x77,0x44,0x00));
              g.fillRect(x*8,y*8,8,8);
            }
            //
          }
      }

      frameCount = frameCount + 1;
      mf = System.nanoTime()-fcoldnano;
    }

}

class Noise {
  public long rand(long x) {
    x *= 135246;
    for(int i = 0;i < 32;i++){
      x ^= x << 21;
      x ^= x >>> 35;
      x ^= x << 4;
    }
    return x;
  }
  public long rand(long x,long y,long z) {
    return rand(rand(rand(x)+y)+z);
  }
  public double frand(long x){
    return (rand(x)&0xffff)/65535;
  }
  public double frand(long x,long y,long z){
    return (rand(x,y,z)&0xffff)/65535d;
  }
  public double aida(double a,double b,double t){
    return a + ((b-a)*t);
  }
  public double noise(double x,double y,double z){
    //
    //a0|a1
    //--+--
    //a2|a3
    //
    double a0 = frand( (long)x+0,(long)y+0 ,(long)z+0);
    double a1 = frand( (long)x+1,(long)y+0 ,(long)z+0);
    double a2 = frand( (long)x+0,(long)y+1 ,(long)z+0);
    double a3 = frand( (long)x+1,(long)y+1 ,(long)z+0);

    double za0 = frand( (long)x+0,(long)y+0 ,(long)z+1);
    double za1 = frand( (long)x+1,(long)y+0 ,(long)z+1);
    double za2 = frand( (long)x+0,(long)y+1 ,(long)z+1);
    double za3 = frand( (long)x+1,(long)y+1 ,(long)z+1);

    double x1 = aida(a0,a1,x%1d);
    double x2 = aida(a2,a3,x%1d);

    double zx1 = aida(za0,za1,x%1d);
    double zx2 = aida(za2,za3,x%1d);

    return aida(aida(x1,x2,y%1d),aida(zx1,zx2,y%1d),z%1d);
    //
  }
  public double pnoise(double x,double y,double z){
    x += 15;
    y += 15;
    z += 15;
    double all = 0;
    double wari = 0;
    for(int i = 0;i < 3;i++){
      int s = i+1;
      all += ((noise(x*s,y*s,z*s)-0.5)*2)/s*(s/2);
      wari += (double)1/s;
    }
    all /= wari;
    all += 0.5;
    return all;
  }
}
