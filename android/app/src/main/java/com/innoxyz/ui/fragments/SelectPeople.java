package com.innoxyz.ui.fragments;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.innoxyz.global.InnoXYZApp;
import com.innoxyz.ui.customviews.NameCardBox;
import com.innoxyz.ui.customviews.PeopleGridItem;
import com.innoxyz.data.runtime.SimpleObservedData;
import com.innoxyz.ui.utils.DataTag;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import com.innoxyz.R;
/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-8-1
 * Time: 下午1:10
 * To change this template use File | Settings | File Templates.
 */
@SuppressWarnings("unchecked")
public class SelectPeople extends DialogFragment {


    private NameCardBox linkedBox = null;

    public SelectPeople(){}

    public void setNameCardBox(NameCardBox box) {
        linkedBox = box;
    }
    class PeopleGridViewAdapter extends BaseAdapter {
        String[] names;
        PeopleGridItem[] itemViews;
        SimpleObservedData<List<String>> namelistData;

        public PeopleGridViewAdapter(Context context) {
            int n = 9;
            names = new String[n];
            Random random = new Random(System.currentTimeMillis());
            namelistData = (SimpleObservedData<List<String>>) ((InnoXYZApp)(getActivity()).getApplication()).getRuntimeDataManager().get(getActivity()).get(DataTag.makeTag(SelectPeople.class, "", "NameList"));
            List<String> namelist = namelistData.getData();
            itemViews = new PeopleGridItem[n];
            for(int i=0; i<n; i++) {
                names[i] = "name" + i;
                itemViews[i] = new PeopleGridItem(context);
                itemViews[i].getNameView().setText(names[i]);
                itemViews[i].setChecked(namelist.contains(names[i]));
            }
        }

        @Override
        public int getCount() {
            return names.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            return itemViews[i];
        }

        public void updateData() {
            final List<String> list = new LinkedList<String>();
            for(PeopleGridItem pgi : itemViews) {
                if (pgi.isChecked()) {
                    list.add((String) pgi.getNameView().getText());
                }
            }
            namelistData.setData(list, true);
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);    //To change body of overridden methods use File | Settings | File Templates.
        View ret = inflater.inflate(R.layout.fragment_select_people, container, false);
        final GridView grid = (GridView)ret.findViewById(R.id.select_people_grid);
        PeopleGridViewAdapter adapter = new PeopleGridViewAdapter(getActivity());
        grid.setAdapter(adapter);

        // We should limit the height of GrdView by ourself
        grid.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, View.MeasureSpec.AT_MOST));   // What the fuck
        // Such method doesn't look reliable, add an assertion
        assert (grid.getMeasuredHeight() > 100);
        // Limiting the height
        float max_height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 160f, getResources().getDisplayMetrics());
        if ( grid.getMeasuredHeight() > max_height ) {
            grid.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, (int)max_height));
        }

        // Add listener for "confirm button
        ((Button)ret.findViewById(R.id.select_people_confirm)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((PeopleGridViewAdapter)grid.getAdapter()).updateData();
                SelectPeople.this.dismiss();
            }
        });
        return ret;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    //To change body of overridden methods use File | Settings | File Templates.
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Dialog);
    }
}
