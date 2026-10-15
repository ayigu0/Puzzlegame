package tool;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.GlyphVector;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;

public class ButtonMaker {
    static final int W = 128, H = 47;
    static final int R = 20; // 圆角
    static final String TEXT = "返回";

    public static void main(String[] args) throws Exception {
        String outDir = args.length == 0 ? "pictures" : args[0];
        new File(outDir).mkdirs();

        // 普通态
        BufferedImage normal = drawButton(false);
        ImageIO.write(normal, "png", new File(outDir, "btn_fanhui_green.png"));

        // 按下态
        BufferedImage pressed = drawButton(true);
        ImageIO.write(pressed, "png", new File(outDir, "btn_fanhui_green_pressed.png"));

        System.out.println("生成完成：btn_fanhui_green.png / btn_fanhui_green_pressed.png");
    }

    static BufferedImage drawButton(boolean pressed) {
        BufferedImage img = new BufferedImage(W, H, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // —— 绿色渐变（上亮下深），按下态稍暗
        Color top = pressed ? new Color(112, 223, 122) : new Color(132, 243, 142);
        Color mid = pressed ? new Color(60, 185, 86)  : new Color(29, 241, 65);
        Color bot = pressed ? new Color(32, 135, 50)  : new Color(42, 211, 72);

        RoundRectangle2D.Float rr = new RoundRectangle2D.Float(1, 1, W-2, H-2, R, R);

        // 上半段渐变
        GradientPaint gp1 = new GradientPaint(0, 0, top, 0, H/2f, mid);
        g.setPaint(gp1);
        g.fill(rr);
        // 下半段叠加渐变
        GradientPaint gp2 = new GradientPaint(0, H/2f, mid, 0, H, bot);
        g.setPaint(gp2);
        g.fill(rr);

        // 外描边（深绿）
        g.setStroke(new BasicStroke(3f));
        g.setColor(new Color(20, 100, 40));
        g.draw(rr);

        // 顶部高光带
        Shape gloss = new RoundRectangle2D.Float(3, 3, W-6, (float)(H*0.48), R*0.8f, R*0.8f);
        g.setComposite(AlphaComposite.SrcOver.derive(0.43f));
        g.setColor(Color.WHITE);
        g.fill(gloss);
        g.setComposite(AlphaComposite.SrcOver);

        // 底部内阴影
        BufferedImage shadow = new BufferedImage(W, H, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gs = shadow.createGraphics();
        gs.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Shape inner = new RoundRectangle2D.Float(4, H-16, W-8, 12, R*0.6f, R*0.6f);
        gs.setColor(new Color(0,0,0,70));
        gs.fill(inner);
        gs.dispose();
        shadow = gaussianBlur(shadow, 2);  // 简易模糊
        g.drawImage(shadow, 0, 0, null);

        // —— 文字（白色填充 + 棕色描边，Q版更饱满）
        // 推荐字体：PingFang SC / Microsoft YaHei / 思源黑体（任选你系统有的）
        Font font = new Font("PingFang SC", Font.BOLD, 26); // 你也可以换成“站酷快乐体”等更Q的字体
        g.setFont(font);

        GlyphVector gv = font.createGlyphVector(g.getFontRenderContext(), TEXT);
        Shape textShape = gv.getOutline();
        Rectangle tb = textShape.getBounds();

        float tx = (W - tb.width) / 2f - tb.x;
        float ty = (H - tb.height) / 2f - tb.y - (pressed ? 0f : 1f);

        g.translate(tx, ty);

        // 文字描边（棕色）— 粗一点更卡通
        g.setColor(new Color(29, 241, 65));
        g.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(textShape);

        // 文字填充（白色）
        g.setColor(Color.WHITE);
        g.fill(textShape);

        // 轻微内阴影（让字更立体）
        g.translate(1, 2);
        g.setColor(new Color(0,0,0,60));
        g.fill(textShape);

        g.dispose();
        return img;
    }

    // 非精准高斯，仅用于按钮内阴影的柔化（避免引第三方库）
    static BufferedImage gaussianBlur(BufferedImage src, int radius) {
        if (radius <= 0) return src;
        float[] kernel = makeKernel(radius);
        BufferedImage tmp = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
        convolve(src, tmp, kernel, radius, true);
        BufferedImage dst = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
        convolve(tmp, dst, kernel, radius, false);
        return dst;
    }
    static float[] makeKernel(int r) {
        float[] kernel = new float[r * 2 + 1];
        float sigma = r / 1.5f, sum = 0;
        for (int i = 0; i < kernel.length; i++) {
            float x = i - r;
            kernel[i] = (float)Math.exp(-(x*x) / (2*sigma*sigma));
            sum += kernel[i];
        }
        for (int i = 0; i < kernel.length; i++) kernel[i] /= sum;
        return kernel;
    }
    static void convolve(BufferedImage src, BufferedImage dst, float[] kernel, int r, boolean horizontal) {
        int w = src.getWidth(), h = src.getHeight();
        int[] in = src.getRGB(0,0,w,h,null,0,w);
        int[] out = new int[w*h];
        int kw = kernel.length;
        if (horizontal) {
            for (int y=0; y<h; y++) {
                for (int x=0; x<w; x++) {
                    float a=0, rC=0, gC=0, bC=0;
                    for (int k=-r; k<=r; k++) {
                        int ix = Math.min(w-1, Math.max(0, x+k));
                        int argb = in[y*w + ix];
                        float f = kernel[k+r];
                        a  += ((argb>>>24)&0xff)*f;
                        rC += ((argb>>>16)&0xff)*f;
                        gC += ((argb>>>8 )&0xff)*f;
                        bC += ( argb       &0xff)*f;
                    }
                    out[y*w + x] = ((int)a<<24)|((int)rC<<16)|((int)gC<<8)|((int)bC);
                }
            }
        } else {
            for (int x=0; x<w; x++) {
                for (int y=0; y<h; y++) {
                    float a=0, rC=0, gC=0, bC=0;
                    for (int k=-r; k<=r; k++) {
                        int iy = Math.min(h-1, Math.max(0, y+k));
                        int argb = out[iy*w + x];
                        float f = kernel[k+r];
                        a  += ((argb>>>24)&0xff)*f;
                        rC += ((argb>>>16)&0xff)*f;
                        gC += ((argb>>>8 )&0xff)*f;
                        bC += ( argb       &0xff)*f;
                    }
                    in[y*w + x] = ((int)a<<24)|((int)rC<<16)|((int)gC<<8)|((int)bC);
                }
            }
            dst.setRGB(0,0,w,h,in,0,w);
            return;
        }
        dst.setRGB(0,0,w,h,out,0,w);
    }
}

