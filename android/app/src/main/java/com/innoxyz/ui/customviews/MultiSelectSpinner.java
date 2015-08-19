package com.innoxyz.ui.customviews;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Checkable;

import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;



import com.innoxyz.data.runtime.model.ParcelableUser;
import com.innoxyz.ui.adapters.NetworkimageTextAdapter;
/**
 * Created by laborish on 14-3-16.
 */


/**
 * A Spinner view that does not dismiss the dialog displayed when the control is "dropped down"
 * and the user presses it. This allows for the selection of more than one option.
 */
public class MultiSelectSpinner extends Spinner implements AdapterView.OnItemClickListener {
    String[] _items = null;
    Integer[] _ids = null;
    boolean[] _selection = null;
    String _hint = null;

    ArrayAdapter<String> _proxyAdapter;

    /**
     * Constructor for use when instantiating directly.
     * @param context
     */
    public MultiSelectSpinner(Context context) {
        super(context);

        _proxyAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item);
        super.setAdapter(_proxyAdapter);
    }

    /**
     * Constructor used by the layout inflater.
     * @param context
     * @param attrs
     */
    public MultiSelectSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);

        _proxyAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item);
        super.setAdapter(_proxyAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int which, long id) {
        if (_selection != null && which < _selection.length) {
            //CheckedTextView checkedTextView = (CheckedTextView)view.findViewById(R.id.listitem_involver_name);
            Checkable checkable = (Checkable)view;
            _selection[which] = checkable.isChecked();

            _proxyAdapter.clear();
            String s = buildSelectedItemString();
            if(s.length() == 0){
                _proxyAdapter.add(_hint);
            }
            else{
                _proxyAdapter.add(s);
            }
            setSelection(0);
        }
        else {
            throw new IllegalArgumentException("Argument 'which' is out of bounds.");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean performClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setPositiveButton("确定",null);
        builder.setAdapter(new NetworkimageTextAdapter(getContext(), _items, _ids), null);
        //builder.setMultiChoiceItems(_items, _selection, this);
        AlertDialog alertDialog = builder.create();
        ListView listView = alertDialog.getListView();
        listView.setItemsCanFocus(false);
        //listView.setAdapter(new NetworkimageTextAdapter(getContext()));
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setOnItemClickListener(this);

        alertDialog.show();
        return true;
    }

    /**
     * MultiSelectSpinner does not support setting an adapter. This will throw an exception.
     * @param adapter
     */
    @Override
    public void setAdapter(SpinnerAdapter adapter) {
        throw new RuntimeException("setAdapter is not supported by MultiSelectSpinner.");
    }

    /**
     * Sets the options for this spinner.
     * @param items
     */
    public void setItems(String[] items) {
        _items = items;
        _selection = new boolean[_items.length];

        Arrays.fill(_selection, false);

        _ids = new Integer[_items.length];

        Arrays.fill(_ids, -1);
    }

    /**
     * Sets the options for this spinner.
     * @param items
     */
    public void setItems(List<String> items) {
        _items = items.toArray(new String[items.size()]);
        _selection = new boolean[_items.length];

        Arrays.fill(_selection, false);

        _ids = new Integer[_items.length];

        Arrays.fill(_ids, -1);
    }

    /**
     * Sets the options for this spinner.
     * @param users
     */
    public void setItemsAndIds(List<ParcelableUser> users) {
        _items = new String[users.size()];
        _ids = new Integer[users.size()];

        for(int i=0;i<users.size();i++){
            _items[i] = users.get(i).getName();
            _ids[i] = users.get(i).getId();
        }

        _selection = new boolean[_items.length];

        Arrays.fill(_selection, false);

    }

    /**
     * Sets the options for this spinner.
     * @param ids
     */
    public void setIds(Integer[] ids) {
        _ids = ids;
    }

    /**
     * Sets the options for this spinner.
     * @param ids
     */
    public void setIds(List<Integer> ids) {
        _ids = ids.toArray(new Integer[ids.size()]);
    }

    /**
     * Sets the selected options based on an array of string.
     * @param selection
     */
    public void setSelection(String[] selection) {
        for (String sel : selection) {
            for (int j = 0; j < _items.length; ++j) {
                if (_items[j].equals(sel)) {
                    _selection[j] = true;
                }
            }
        }
    }

    /**
     * Sets the selected options based on a list of string.
     * @param selection
     */
    public void setSelection(List<String> selection) {
        for (String sel : selection) {
            for (int j = 0; j < _items.length; ++j) {
                if (_items[j].equals(sel)) {
                    _selection[j] = true;
                }
            }
        }
    }

    /**
     * Sets the selected options based on an array of positions.
     * @param selectedIndicies
     */
    public void setSelection(int[] selectedIndicies) {
        for (int index : selectedIndicies) {
            if (index >= 0 && index < _selection.length) {
                _selection[index] = true;
            }
            else {
                throw new IllegalArgumentException("Index " + index + " is out of bounds.");
            }
        }
    }

    /**
     * Returns a list of strings, one for each selected item.
     * @return
     */
    public List<String> getSelectedStrings() {
        List<String> selection = new LinkedList<String>();
        for (int i = 0; i < _items.length; ++i) {
            if (_selection[i]) {
                selection.add(_items[i]);
            }
        }
        return selection;
    }

    /**
     * Returns a list of positions, one for each selected item.
     * @return
     */
    public List<Integer> getSelectedIndicies() {
        List<Integer> selection = new LinkedList<Integer>();
        for (int i = 0; i < _items.length; ++i) {
            if (_selection[i]) {
                selection.add(i);
            }
        }
        return selection;
    }

    /**
     * Builds the string for display in the spinner.
     * @return comma-separated list of selected items
     */
    private String buildSelectedItemString() {
        StringBuilder sb = new StringBuilder();
        boolean foundOne = false;

        for (int i = 0; i < _items.length; ++i) {
            if (_selection[i]) {
                if (foundOne) {
                    sb.append(", ");
                }
                foundOne = true;

                sb.append(_items[i]);
            }
        }

        return sb.toString();
    }

    public void setHint(int resid){
        setHint(getContext().getResources().getText(resid));
    }

    public void setHint(CharSequence hint){
        _hint = new String(hint.toString());
        _proxyAdapter.clear();
        _proxyAdapter.add(_hint);
        setSelection(0);
    }


}
