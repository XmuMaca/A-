package wxm.com.androiddesign.utils;

import android.graphics.Bitmap;
import android.support.v7.graphics.Palette;

import com.squareup.picasso.Transformation;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Created by Zero on 10/18/2015.
 */
public class PaletteTransformation implements Transformation{
    private static final PaletteTransformation INSTANCE=new PaletteTransformation();
    private static final Map<Bitmap,Palette> CACHE=new WeakHashMap<>();

    public static PaletteTransformation getInstance(){
        return INSTANCE;
    }

    public static Palette getPalette(Bitmap bitmap){
        return CACHE.get(bitmap);
    }

    private PaletteTransformation(){}

    @Override
    public Bitmap transform(Bitmap source) {
        Palette palette=Palette.from(source).generate();
        CACHE.put(source,palette);
        return source;
    }

    @Override
    public String key() {
        return "";
    }
}
