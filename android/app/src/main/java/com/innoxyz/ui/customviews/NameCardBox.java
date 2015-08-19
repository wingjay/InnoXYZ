package com.innoxyz.ui.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.innoxyz.global.InnoXYZApp;
import com.innoxyz.data.runtime.SimpleObservedData;
import com.innoxyz.data.runtime.interfaces.IDataObserver;
import com.innoxyz.ui.activities.MainActivity;
import com.innoxyz.ui.fragments.SelectPeople;
import com.innoxyz.ui.utils.DataTag;

import java.util.ArrayList;
import java.util.List;
import com.innoxyz.R;
/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-8-2
 * Time: 上午9:21
 * To change this template use File | Settings | File Templates.
 */

@SuppressWarnings("unchecked")
public class NameCardBox extends LinearLayout implements IDataObserver {

    private int maxWidth = -1;

    public NameCardBox(Context context) {
        super(context);
        init();
    }

    public NameCardBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    protected void init() {
        setOrientation(VERTICAL);
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        ((MainActivity)getContext()).addOnFocusChangeOperation(new Runnable() {
            @Override
            public void run() {
                //get user's screen width
                NameCardBox.this.maxWidth = NameCardBox.this.getWidth();
            }
        });
        setNameList(null);
        setClickable(true);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectPeople selectPeople = new SelectPeople();
                selectPeople.setNameCardBox(NameCardBox.this);
                selectPeople.show(((MainActivity) getContext()).getFragmentManager(), "select_people");
            }
        });
        SimpleObservedData<List<String>> namelist = new SimpleObservedData<List<String>>(new ArrayList<String>());
        namelist.registerObserver(this);
        ((InnoXYZApp)((MainActivity) getContext()).getApplication()).getRuntimeDataManager().get(getContext()).put(DataTag.makeTag(NameCardBox.class, "", "NameList"), namelist);
    }

    protected LinearLayout addNewRow() {
        LinearLayout row = new LinearLayout(getContext());
        row.setOrientation(HORIZONTAL);
        row.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        addView(row);
        return row;
    }

    public void setNameList(final List<String> namelist) {
        this.removeAllViews();
        if ( namelist == null || namelist.size()==0 ) {
            ((LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.customviews_namecardbox_empty, this, true);
            return;
        }
        if ( maxWidth < 0 ) {
            ((MainActivity)getContext()).addOnFocusChangeOperation(new Runnable() {
                @Override
                public void run() {
                    NameCardBox.this.setNameList(namelist);
                }
            });
            return;
        }

        final int ms = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);

        final int marginRight = 6;
        LinearLayout lastRow = addNewRow();
        for(String name : namelist) {
            PeopleNameCard card = new PeopleNameCard(getContext());
            card.setText(name);
            card.measure(ms, MeasureSpec.UNSPECIFIED);
            int itemWidth = card.getMeasuredWidth();
            lastRow.measure(ms, MeasureSpec.UNSPECIFIED);
            int rowWidth = lastRow.getMeasuredWidth();
            if ( rowWidth + itemWidth > maxWidth - marginRight ) {
                lastRow = addNewRow();
            }
            lastRow.addView(card);
        }
    }

    @Override
    public void update(Object o) {
        //To change body of implemented methods use File | Settings | File Templates.
        final List<String> namelist = (List<String>) o;
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                NameCardBox.this.setNameList(namelist);
            }
        });
    }
}
