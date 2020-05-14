package com.capstone.moayo;

import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

//import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.capstone.moayo.entity.CategoryNode;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class BottomSheetFragment extends BottomSheetDialogFragment {
    public static BottomSheetFragment getInstance() { return new BottomSheetFragment(); }

    public interface OnAddNodeListener {
        void onAddNode(CategoryNode add_word);
    }
    private CategoryNode newNode;
    private CategoryNode parentNode;
    private String word;
    private Button cancel_btn, add_btn;
    private TextView keyword;
    private OnAddNodeListener callback;

    public void setOnAddNodeListener(OnAddNodeListener callback) {
        this.callback = callback;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottom_sheet, container,false);

        word = getArguments().getString("keyword");
        parentNode = (CategoryNode) getArguments().getSerializable("parentNode");
        //추가하려는 키워드
        keyword = (TextView) view.findViewById(R.id.title_keyword);
        keyword.setText(word);


        //검색된 연관 키워드들을 ArrayList에 담아 ListView에 보여줌.
        final ArrayList<String> tags = new ArrayList<String>();
        tags.add("hashtag1");
        tags.add("hashtag2");
        tags.add("hashtag3");
        final ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_multiple_choice, tags);
        final ListView listview = (ListView) view.findViewById(R.id.hashtag_list);

        listview.setAdapter(adapter);

//        Cancel Button
        cancel_btn = (Button) view.findViewById(R.id.cancel_btn);
        cancel_btn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

//        Add Button
        add_btn = (Button) view.findViewById(R.id.add_btn);
        add_btn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                newNode = new CategoryNode(word, parentNode, parentNode.getLevel()+1);

                //node 객체에 선택된 해시태그들을 담음
                ArrayList<String> hashtags = new ArrayList<>();
                SparseBooleanArray checkedTags = listview.getCheckedItemPositions();
                for (int i = adapter.getCount()-1; i >= 0; i--) {
                    if (checkedTags.get(i)) {
                        hashtags.add(tags.get(i));
                    }
                }
                newNode.setHashtags((List) hashtags);
                listview.clearChoices(); // 모든 선택상태 초기화.

                adapter.notifyDataSetChanged();

                callback.onAddNode(newNode);
                dismiss();

            }
        });

        return view;
    }


}

