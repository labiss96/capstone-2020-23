package com.capstone.moayo.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.capstone.moayo.R;
import com.capstone.moayo.service.dto.CategoryNodeDto;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class BottomSheetFragment extends BottomSheetDialogFragment implements View.OnClickListener {
    public static BottomSheetFragment getInstance() { return new BottomSheetFragment(); }

    public interface OnEditNodeListener {
        void onAddNode(CategoryNodeDto node);
        void onRemoveNode(CategoryNodeDto node);
    }

    private CategoryNodeDto node;
    private CategoryNodeDto parentNode;
    private String word;
    private Button cancel_btn, save_btn;
    private ImageButton delete_btn, add_tag_btn;
    private TextView keyword;
    private EditText input_tag_et;
    private OnEditNodeListener callback;

    private String FORM_MODE;

    private ListView listview;
    private ArrayAdapter adapter;
    private ArrayList<String> synonym_tags;

    public void setOnEditNodeListener(OnEditNodeListener callback) {
        this.callback = callback;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final BottomSheetDialog d = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        // view hierarchy is inflated after dialog is shown
        d.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                //Disables outside touch
                d.getWindow().findViewById(R.id.touch_outside).setOnClickListener(null);
                //Prevents dragging behavior
                View content = d.getWindow().findViewById(R.id.design_bottom_sheet);
                ((CoordinatorLayout.LayoutParams) content.getLayoutParams()).setBehavior(null);
            }
        });
        return d;
    }


    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            View bottomSheet = dialog.findViewById(R.id.design_bottom_sheet);
            bottomSheet.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT; //setting full size Dialog.
        }
//        final View view = getView();
//        view.post(new Runnable() {
//            @Override
//            public void run() {
//                View parent = (View) view.getParent();
//                CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) (parent).getLayoutParams();
//                CoordinatorLayout.Behavior behavior = params.getBehavior();
//                BottomSheetBehavior bottomSheetBehavior = (BottomSheetBehavior) behavior;
//                bottomSheetBehavior.setPeekHeight(0);
//                parent.setBackgroundColor(Color.WHITE);
//            }
//        });
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_hashtag_form, container,false);

        keyword = (TextView) view.findViewById(R.id.dialog_tag_tv_keyword);
        cancel_btn = (Button) view.findViewById(R.id.dialog_tag_btn_cancel);
        save_btn = (Button) view.findViewById(R.id.dialog_tag_btn_save);
        delete_btn = (ImageButton) view.findViewById(R.id.dialog_tag_btn_delete);
        add_tag_btn = (ImageButton) view.findViewById(R.id.dialog_tag_ib_add_hashtag);

        input_tag_et = (EditText) view.findViewById(R.id.dialog_tag_et_input);

        FORM_MODE = getArguments().getString("MODE");
        parentNode = (CategoryNodeDto) getArguments().getSerializable("parentNode");

        //검색된 연관 키워드들을 ArrayList에 담아 ListView에 보여줌.
        synonym_tags = new ArrayList<String>();

        synonym_tags.add("hashtag1");
        synonym_tags.add("hashtag2");
        synonym_tags.add("hashtag3");
        synonym_tags.add("hashtag4");
        synonym_tags.add("hashtag5");



        adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_multiple_choice, synonym_tags);
        listview = (ListView) view.findViewById(R.id.dialog_tag_lv_hashtags);
        listview.setAdapter(adapter);

        switch (FORM_MODE) {
            case "ADD":
                word = getArguments().getString("keyword");
                keyword.setText(word);
                delete_btn.setVisibility(View.INVISIBLE);
                break;
            case "EDIT":
                node = (CategoryNodeDto) getArguments().getSerializable("selectedNode");
                keyword.setText(node.getTitle());
                save_btn.setText("Update");

                //node에 저장된 태그정보들을 불러와 일치하는 hashtag setItemChecked 처리.
                ArrayList<String> selected_tags =  (ArrayList<String>) node.getHashtags();
                for(String tag:selected_tags) {
                    int idx = synonym_tags.indexOf(tag);

                    if( idx != -1) listview.setItemChecked(idx, true);
                    else addHashTag(tag); //만일 저장된 태그가 리스트에 없을 시 추가.
                }
                
                break;
        }



        cancel_btn.setOnClickListener(this);
        save_btn.setOnClickListener(this);
        delete_btn.setOnClickListener(this);
        add_tag_btn.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_tag_btn_cancel:
                dismiss();
                break;

            case R.id.dialog_tag_btn_save:
                if (FORM_MODE == "ADD") {
                    addKeyword();
                } else {
                    editKeyword();
                }
                dismiss();
                break;
            case R.id.dialog_tag_btn_delete:
                deleteKeyword();
                dismiss();
                break;
            case R.id.dialog_tag_ib_add_hashtag:
                String custom_tag = input_tag_et.getText().toString();
                if (!custom_tag.isEmpty()) {
                    addHashTag(custom_tag);
                    input_tag_et.setText(""); //입력폼 초기화
                } else { Toast.makeText(getContext(), "검색될 해시태그를 입력해주세요.", Toast.LENGTH_SHORT).show(); }
        }
    }

    private void addHashTag(String add_tag) {
        synonym_tags.add(add_tag); // 해시태그를 리스트에 추가.
        listview.setItemChecked(synonym_tags.indexOf(add_tag), true); //추가된 해시태그를 자동 체크.

        adapter.notifyDataSetChanged(); //업데이트된 리스트를 adapter에 적용.
    }


    private void addKeyword() {
        node = new CategoryNodeDto(word, parentNode, parentNode.getLevel()+1);

        //node 객체에 선택된 해시태그들을 담음
        ArrayList<String> hashTags = new ArrayList<>();
        SparseBooleanArray checkedTags = listview.getCheckedItemPositions();
        for (int i = adapter.getCount()-1; i >= 0; i--) {
            if (checkedTags.get(i)) {
                hashTags.add(synonym_tags.get(i));
            }
        }
        node.setHashtags((List) hashTags);
//        Log.d("node_tags", node.getHashtags().toString());
        listview.clearChoices(); // 모든 선택상태 초기화.

        callback.onAddNode(node);
    }

    private void editKeyword() {
        //edit logic

    }

    private void deleteKeyword() {
        callback.onRemoveNode(node);
    }


}
