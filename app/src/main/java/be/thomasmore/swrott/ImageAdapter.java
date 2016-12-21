package be.thomasmore.swrott;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by koenv on 21-12-2016.
 */
public class ImageAdapter extends BaseAdapter {
    private Context _context;
    private List<Bitmap> _images;

    public ImageAdapter(Context c, List<String> paths) {
        _context = c;

        _images = new ArrayList<>();
        for (String p: paths) {
            _images.add(Helper.getPicture(_context, p, 150, 200));
        }
    }

    @Override
    public int getCount() {
        return _images.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(_context);
            imageView.setLayoutParams(new GridView.LayoutParams(250, 250));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageBitmap(_images.get(position));

        return imageView;
    }
}
