import java.lang.Math;
import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Color;
import javax.swing.JFrame;
import java.beans.Expression;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

import java.io.*;
import javax.imageio.*;
import java.awt.image.*;
import java.awt.Font;

public class java_tikei extends Canvas implements KeyListener {

    static int w = 40;
    static int h = 30;

    static int width = w*16;
    static int height = h*16;

    static double frameRate;

    static Noise noise;

    static int frameCount;

    static long fcoldnano,mf;

    static final int ESC = 243;
    static final int LEFT = 37;
    static final int RIGHT = 39;
    static final int UP = 38;
    static final int DOWN = 40;
    static final int BACKSPACE = 8;
    static final int DELETE = 46;
    static final int ENTER = 13;
    static final int SHIFT = 16;
    static final int CTRL = 17;
    static final int ALT = 18;
    static final int SPACE = 32;

    public java_tikei(){
      addKeyListener(this);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("tikei");
        Canvas canvas = new java_tikei();
        canvas.setSize(width, height);
        //frame.pack();
        long oldnano = System.nanoTime();
        long framenano = (1000*1000*1000)/30;
        long osoldnano = System.nanoTime();
        frame.setVisible(true);
        noise = new Noise();
        for(int i = 0;i < 10;i++){
          frame.add(canvas);
          frame.pack();
        }
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
            //frame.setVisible(true);
          }
          //
        }
    }

    static int[][] copy(int[][] in){
      int[][] out = new int[in.length][in[0].length];
      for(int i = 0;i < in.length;i++){
        for(int f = 0;f < in[i].length;f++){
          out[i][f] = in[i][f];
        }
      }
      return out;
    }

    static BufferedImage[] data = new BufferedImage[256];

    public static BufferedImage loadBufferedImage(String path){
      BufferedImage a = null;
      try{
        a = ImageIO.read(new File(path));
      }catch(Exception e){
        e.printStackTrace();
      }
      if(a == null){
        System.out.println(path+" is notfound");
      }
      return a;
    }

    static Font loadFont(String path){
      Font font = null;
      try{
        font = Font.createFont(Font.TRUETYPE_FONT,new File(path));
      }catch(Exception e){
        e.printStackTrace();
      }
      return font;
    }

    static Color bgcolor;

    static KGraphics bgg;

    static double xscr;
    static double yscr;
    static double xscrs;
    static double yscrs;

    static Font font_w0;
    static Font font_w1;
    static Font font_w2;
    static Font font_w3;
    static Font font_w4;
    static Font font_w5;
    static Font font_w6;

    static int[][] map_old = new int[w*16][h*8];
    static int[][] map = new int[w*16][h*8];

    public static void setup(){
      bgcolor = new Color(224, 240, 255);
      bgg = new KGraphics();
      bgg.createGraphics(map.length*16,map[0].length*16);
      bgg.g.setColor(bgcolor);
      bgg.g.fillRect(0, 0, width, height);
      data[0] = loadBufferedImage("tex/air.png");
      data[1] = loadBufferedImage("tex/kusa.png");
      data[2] = loadBufferedImage("tex/tuti.png");
      data[3] = loadBufferedImage("tex/isi.png");
      data[4] = loadBufferedImage("tex/woodf.png");
      data[5] = loadBufferedImage("tex/ha.png");

      font_w0 = loadFont("font/mplus-1p-thin.ttf");
      font_w1 = loadFont("font/mplus-1p-light.ttf");
      font_w2 = loadFont("font/mplus-1p-regular.ttf");
      font_w3 = loadFont("font/mplus-1p-medium.ttf");
      font_w4 = loadFont("font/mplus-1p-heavy.ttf");
      font_w5 = loadFont("font/mplus-1p-thin.ttf");
      font_w6 = loadFont("font/mplus-1p-black.ttf");

      yscr = map[0].length/2;

      for(int y = 0;y < map[0].length;y++){
          for(int x = 0;x < map.length;x++){
            map_old[x][y] = -1;
            map[x][y] = 0;
            //

            int X = x;
            int Y = y;

            double k = ((double)Y/map[0].length);
            k -= 0.5;
            k *= (noise.pnoise(X/128d,0,0)*1)*0.5;
            k += 0.5;

            if(noise.pnoise(X/60d,Y/30d,0) < k ){
              map[x][y] = 1;
              if(noise.pnoise(X/60d,Y/60d,135) < (k*2)-0.6 ){
                map[x][y] = 3;
              }
            }
            //
        }
        double p = (double)y/(map[0].length-1) *100d;
        System.out.println("生成中 "+String.format("%3.0f", p));
      }
      for(int y = map[0].length-1;y >= 0;y--){
          for(int x = 0;x < map.length;x++){
            //
            if(y > 0){
              if(map[x][y] == 1){
                if(map[x][y-1] != 0){
                  map[x][y] = 2;
                }
              }
            }
            //
        }
      }

    }

    public void paint(Graphics g) {
      //xscr = (frameCount/2d);
      fcoldnano = System.nanoTime();
      //
      //

      for(int y = 0;y < map[0].length;y++){
          for(int x = 0;x < map.length;x++){
            //
            if(( map[x][y] != map_old[x][y] )){
              BufferedImage n = data[ map[x][y] ];
              if(n != null){
                bgg.g.drawImage(n,x*16,y*16,null);
              }
            }
            //
          }
      }
      map_old = copy(map);
      BufferedImage bgi = bgg.get();
      g.drawImage(
      get(bgi,(int)(xscr*16d),(int)(yscr*16d),width,height)
      ,0,0,null);

      frameCount = frameCount + 1;
      font_w3 = font_w3.deriveFont(24.0f);
      g.setFont(font_w3);
      g.setColor(new Color(0));
      g.drawString("frameRate:"+String.format("%2.2f", frameRate) , 5 , 24);

      if(keyCodes[UP]){
        yscrs -= 0.1;
      }
      if(keyCodes[DOWN]){
        yscrs += 0.1;
      }
      if(keyCodes[LEFT]){
        xscrs -= 0.1;
      }
      if(keyCodes[RIGHT]){
        xscrs += 0.1;
      }

    xscrs /= 1.2;
    yscrs /= 1.2;
    xscr += xscrs;
    yscr += yscrs;
    mf = System.nanoTime()-fcoldnano;
  }

    //	getKeyChar()
    //	getKeyCode()

    static int key,keyCode;
    static boolean keyPressed;

    static boolean[] keys = new boolean[256*256];
    static boolean[] keyCodes= new boolean[256*256];

    public void keyPressed(KeyEvent event) {
      keyPressed = true;
      key = event.getKeyChar();
      keyCode = event.getKeyCode();
      keys[key] = true;
      keyCodes[keyCode] = true;
    }
    public void keyReleased(KeyEvent event) {
      keyPressed = false;
      key = event.getKeyChar();
      keyCode = event.getKeyCode();
      keys[key] = false;
      keyCodes[keyCode] = false;
    }
    public void keyTyped(KeyEvent event) {
        //event.VK_LEFT
    }
    public BufferedImage get(BufferedImage i,int a0,int a1,int a2,int a3){
      int iw = i.getWidth();
      int ih = i.getHeight();
      int x = a0;
      int y = a1;
      int w = a2;
      int h = a3;
      if(x >= (iw-w)-1)x = (iw-w)-1;
      if(y >= (ih-h)-1)y = (ih-h)-1;
      if(x < 0)x = 0;
      if(y < 0)y = y;
      System.out.print("iw"+iw+" ");
      System.out.print("ih"+ih+" ");
      System.out.print("x"+x+" ");
      System.out.print("y"+y+" ");
      System.out.print("w"+w+" ");
      System.out.println("h"+h);
      return i.getSubimage(x,y,w,h);
    }


}

class Noise {
  public static long seed = 2398;

  public long rand(long x) {
    x *= 135246;

      x ^= x << 21;
      x ^= x >>> 35;
      x ^= x << 4;
      x ^= x << 21;
      x ^= x >>> 35;
      x ^= x << 4;
      x ^= x << 21;
      x ^= x >>> 35;
      x ^= x << 4;
      x ^= x << 21;
      x ^= x >>> 35;
      x ^= x << 4;
      x ^= x << 21;
      x ^= x >>> 35;
      x ^= x << 4;
      x ^= x << 21;
      x ^= x >>> 35;
      x ^= x << 4;
      x ^= x << 21;
      x ^= x >>> 35;
      x ^= x << 4;
      x ^= x << 21;
      x ^= x >>> 35;
      x ^= x << 4;
      x ^= x << 21;
      x ^= x >>> 35;
      x ^= x << 4;
      x ^= x << 21;
      x ^= x >>> 35;
      x ^= x << 4;

    return x;
  }
  public long rand(long x,long y,long z) {
    return rand(rand(rand(rand(x+seed))+y)+z);
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
    for(int i = 0;i < 8;i++){
      int s = i+1;
      all += ((noise(x*s,y*s,z*s)-0.5)*2)/(s*7);
      wari += (double)1/s;
    }
    all /= wari;
    all += 0.5;
    return all;
  }
}

class KGraphics{
  public static BufferedImage buffer;
  public static Graphics g;
  public Graphics createGraphics(int w,int h){
    buffer = new BufferedImage(w, h,BufferedImage.TYPE_INT_ARGB);
    g = buffer.getGraphics();
    return g;
  }
  public BufferedImage get(){
    return buffer;
  }

}
