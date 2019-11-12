import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;

public class Main
{
    static final float SCAN_HEIGHT = 0.25f;
    static final float MAX_CHAR_WIDTH = 0.05f;

public static void main(String[] args)
{
    //Read in image to buffered image
    BufferedImage frame = null;
    try {
        frame = ImageIO.read(new File("subtitles.png"));
    } catch (IOException e) {
    }

    int data[][] = new int[frame.getWidth()][frame.getHeight()];
    //This is horrible, replace with something like this [https://stackoverflow.com/questions/6524196/java-get-pixel-array-from-image]
    for(int x = 0; x < frame.getWidth(); ++x)
    {
        for(int y = 0; y < frame.getHeight(); ++y)
        {
            data[x][y] = frame.getRGB(x, y);
        }
    }
    ScanForSubs(data);

    BufferedImage bi = new BufferedImage( frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_RGB );

    for(int x = 0; x < frame.getWidth(); ++x)
    {
        for(int y = 0; y < frame.getHeight(); ++y)
        {
            bi.setRGB(x, y, data[x][y]);
        }
    }

    try {
        File outputfile = new File("saved.bmp");
        ImageIO.write(bi, "bmp", outputfile);
    } catch (IOException e) {
        System.out.println("Fuck off");
    }


}



private static void ScanForSubs(int[][] i_frame)
{
    final int frameH = i_frame[0].length;
    final int scanHeight = (int)(SCAN_HEIGHT * frameH);

    for(int startV = 0; startV < frameH - scanHeight; startV += frameH - scanHeight - 1) {
        for (int i = 0; i < scanHeight; ++i) {
            ProcessSlice(i_frame, startV + i);
        }
    }
}

private static void ProcessSlice(int[][] i_frame, int i_sliceH)
{
    final int frameW = i_frame.length;
    final int maxWidth = (int)(frameW * MAX_CHAR_WIDTH);
    boolean inChar = false;
    int curCharWidth = 0;

    for(int j = 0; j < frameW; ++j)
    {
        if(IsChar(i_frame[j][i_sliceH]))
        {
            inChar = true;
            ++curCharWidth;
        }
        else
        {
            if(inChar && curCharWidth <= maxWidth)
            {
                FillSlice(i_frame, j, curCharWidth, i_sliceH);
            }

            inChar =  false;
            curCharWidth = 0;
        }
    }
}

private static boolean IsChar(int i_pixel)
{
    int r = (i_pixel >> 16) & 0x000000FF;
    int g = (i_pixel >>8 ) & 0x000000FF;
    int b = i_pixel & 0x000000FF;

    return (r > 250 && g > 250 && b > 250);

}

private static void FillSlice(int[][] i_frame, int i_endX, int i_length, int i_sliceH)
{
    int loopStart = i_endX - i_length;
    assert loopStart >= 0;

    for(int j = loopStart; j < loopStart + i_length; ++j)
    {
        i_frame[j][i_sliceH] = new Color(255, 0, 0).getRGB();
    }
}

}
