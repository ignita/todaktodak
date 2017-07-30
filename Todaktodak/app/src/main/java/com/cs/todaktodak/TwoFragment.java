package com.cs.todaktodak;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.CardProvider;
import com.dexafree.materialList.card.OnActionClickListener;
import com.dexafree.materialList.card.action.WelcomeButtonAction;
import com.dexafree.materialList.listeners.RecyclerItemClickListener;
import com.dexafree.materialList.view.MaterialListView;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.github.javiersantos.materialstyleddialogs.enums.Style;
import com.squareup.picasso.RequestCreator;

/**
 * Created by yjchoi on 2017. 7. 17..
 */

public class TwoFragment extends Fragment {

    MaterialListView mListView;

    String description = null;
    String title;

    Card cardWelcome;
    Card[] petCards = new Card[4];
    String[] pets = {"도베르만 핀셔", "골든 리트리버", "고양이", "푸들"};
    String[] petsE = {"Dobermann Pinscher", "Golden Retriever", "Cat", "Poodle"};

    public TwoFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i("googleMap", "TwoFragmentOnCreateView");

        View view = inflater.inflate(R.layout.fragment_two, container, false);
        mListView = (MaterialListView) view.findViewById(R.id.material_listview);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1);

        // 뒷배경색
        String back = "#002f6c";
        int backInt = Color.parseColor(back);

        // 첫번째 카드
        cardWelcome = new Card.Builder(getActivity())
                .withProvider(new CardProvider())
                .setLayout(R.layout.material_welcome_card_layout)
                .setTitle("반려동물로 알아보는 당신의 성격은?")
                .setTitleColor(Color.WHITE)
                .setDescription("※ 사실과 다를 수 있습니다.(출처: 포스트쉐어)")
                .setDescriptionColor(Color.WHITE)
                .setSubtitle("아래의 사진 중 기르고 있거나 좋아하는 반려동물을 선택하세요.")
                .setSubtitleColor(Color.WHITE)
                .setBackgroundColor(backInt)
                .addAction(R.id.ok_button, new WelcomeButtonAction(getActivity())
                        .setText("Okay!")
                        .setTextColor(Color.WHITE)
                        .setListener(new OnActionClickListener() {
                            @Override
                            public void onActionClicked(View view, Card card) {
                                Toast.makeText(getActivity(), "아래의 사진을 선택하세요!", Toast.LENGTH_SHORT).show();
                            }
                        }))
                .endConfig()
                .build();
        mListView.getAdapter().add(cardWelcome);

        petCards();
        for (int i = 0; i < 4; i++)
            mListView.getAdapter().add(petCards[i]);


        mListView.addOnItemTouchListener(new RecyclerItemClickListener.OnItemClickListener() {

            @Override
            public void onItemClick(Card card, int position) {

                title = card.getProvider().getTitle();

                if (!title.equals(cardWelcome.getProvider().getTitle())) {
                    // 결과 설명
                    switch (title) {
                        case "도베르만 핀셔":
                            description = getString(R.string.dobermman);
                            break;
                        case "골든 리트리버":
                            description = getString(R.string.gold);
                            break;
                        case "고양이":
                            description = getString(R.string.cat);
                            break;
                        case "푸들":
                            description = getString(R.string.poodle);
                            break;
                    }

                    new MaterialStyledDialog.Builder(getActivity())
                            .setTitle(card.getProvider().getTitle())
                            .setDescription(description)
                            .setStyle(Style.HEADER_WITH_TITLE)
                            .setCancelable(true)
                            .setHeaderColor(R.color.colorPrimary)
                            .withDialogAnimation(true)
                            .withDarkerOverlay(true)
                            .show();

                }
            }

            @Override
            public void onItemLongClick(Card card, int position) {
            }
        });
        return view;
    }

    private void petCards() {

        int[] pics = {R.drawable.dobermann, R.drawable.golden_retriever, R.drawable.cat, R.drawable.poodle};

        for (int i = 0; i < 4; i++) {
            petCards[i] = new Card.Builder(getActivity())
                    .withProvider(new CardProvider())
                    .setLayout(R.layout.material_basic_image_buttons_card_layout)
                    .setTitle(pets[i])
                    .setTitleGravity(Gravity.START)
                    .setDescription(petsE[i])
                    .setDescriptionGravity(Gravity.START)
                    .setDrawable(pics[i])
                    .setDrawableConfiguration(new CardProvider.OnImageConfigListener() {
                        @Override
                        public void onImageConfigure(@NonNull RequestCreator requestCreator) {
                            requestCreator.fit();
                        }
                    })
                    .endConfig()
                    .build();
        }

    }

}


