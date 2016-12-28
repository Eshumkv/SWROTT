package be.thomasmore.rott;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import be.thomasmore.rott.data.Team;

/**
 * Created by koenv on 8-12-2016.
 */
public class TeamAdapter extends ArrayAdapter<Team> {

    private final Context context;
    private final List<Team> values;

    public TeamAdapter(Context context, List<Team> values) {
        super(context, R.layout.teamlistviewitem, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.teamlistviewitem, parent, false);

        final TextView textViewName = (TextView) rowView.findViewById(R.id.teamname);

        textViewName.setText(values.get(position).toString());

        return rowView;
    }
}
