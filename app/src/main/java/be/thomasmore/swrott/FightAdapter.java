package be.thomasmore.swrott;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import be.thomasmore.swrott.data.Member;

/**
 * Created by koenv on 26-12-2016.
 */
public class FightAdapter extends BaseAdapter {

    private Context _context;
    private List<Member> _members;
    private boolean _defaultIcons;
    private int _color;
    private int _icon;

    public FightAdapter(Context c, List<Member> members, int colorRes, boolean useDefaultIcons, List<Integer> iconResIds) {
        _context = c;
        _members = members;
        _defaultIcons = useDefaultIcons;
        _color = colorRes;
        _icon = iconResIds.get(Helper.randomBetween(0, iconResIds.size()-1));
    }

    @Override
    public int getCount() {
        return _members.size();
    }

    @Override
    public Object getItem(int position) {
        return _members.get(position);
    }

    @Override
    public long getItemId(int position) {
        return _members.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.fight_team_member, parent, false);

        final ImageView picture = (ImageView) rowView.findViewById(R.id.member_image);
        final TextView memberName = (TextView) rowView.findViewById(R.id.member_name);
        final TextView memberLvl = (TextView) rowView.findViewById(R.id.member_lvl);
        final Member member = _members.get(position);

        rowView.setTag("id_" + member.getId());

        if (!_defaultIcons) {
            picture.setImageBitmap(Helper.getPicture(_context, member.getPicture().getPath(), 120, 120));
        } else {
            picture.setImageBitmap(Helper.getPicture(_context, _icon, 120, 120));
        }

        memberName.setText(member.getPerson().getName());
        memberLvl.setText(String.format("Lvl %d", member.getLevel()));
        picture.setBackgroundResource(_color);

        return rowView;
    }
}
