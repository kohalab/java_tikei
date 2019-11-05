import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Color;
import javax.swing.JFrame;

public class java_tikei extends Canvas {

    static int width = 640;
    static int height = 480;

    static int frameRate;

    static Noise noise;

    static int frameCount;

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
        int fcc = 0;
        noise = new Noise();
        while(true){
          //
          if(System.nanoTime()-oldnano >= framenano){
            canvas.repaint();
            oldnano = System.nanoTime();
            fcc++;
          }
          //
          if(System.nanoTime()-osoldnano >= 1000*1000*1000){
            osoldnano = System.nanoTime();
            frameRate = fcc;
            System.out.println("frameRate "+frameRate);
            fcc = 0;
          }
          //
        }
    }

    public void paint(Graphics g) {
      g.setColor(Color.black);
      g.fillRect(0, 0, width, height);
      for(int i = 0;i < width;i++){
        int A = i;
        int h = 0;
        h += (int)(noise.pnoise(A/100d,frameCount/100d,0)*height);
        g.setColor(Color.white);
        g.fillRect(i, height-h, 1, h);
      }

      frameCount = frameCount + 1;
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
    x += 100000;
    y += 100000;
    z += 100000;
    double all = 0;
    double wari = 0;
    for(int i = 0;i < 5;i++){
      int s = i+1;
      all += ((noise(x*s,y*s,z*s)-0.5)*2)/s;
      wari += 1;
    }
    all /= wari;
    all += 0.5;
    return all;
  }
}
