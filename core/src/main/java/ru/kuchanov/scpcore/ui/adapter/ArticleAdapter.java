package ru.kuchanov.scpcore.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;
import ru.kuchanov.scpcore.R;
import ru.kuchanov.scpcore.api.ParseHtmlUtils;
import ru.kuchanov.scpcore.db.model.Article;
import ru.kuchanov.scpcore.db.model.ArticleTag;
import ru.kuchanov.scpcore.db.model.RealmString;
import ru.kuchanov.scpcore.ui.holder.ArticleImageHolder;
import ru.kuchanov.scpcore.ui.holder.ArticleSpoilerHolder;
import ru.kuchanov.scpcore.ui.holder.ArticleTableHolder;
import ru.kuchanov.scpcore.ui.holder.ArticleTabsHolder;
import ru.kuchanov.scpcore.ui.holder.ArticleTagsHolder;
import ru.kuchanov.scpcore.ui.holder.ArticleTextHolder;
import ru.kuchanov.scpcore.ui.holder.ArticleTitleHolder;
import ru.kuchanov.scpcore.ui.model.ArticleTextPartViewModel;
import ru.kuchanov.scpcore.ui.model.SpoilerViewModel;
import ru.kuchanov.scpcore.ui.model.TabsViewModel;
import ru.kuchanov.scpcore.ui.util.SetTextViewHTML;
import timber.log.Timber;

/**
 * Created by Dante on 17.01.2016.
 * <p>
 * for scp_ru
 */
public class ArticleAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements ArticleSpoilerHolder.SpoilerClickListener, ArticleTabsHolder.MyTabClickListener {

    //TODO realize via enum
    private static final int TYPE_TEXT = 0;
    private static final int TYPE_SPOILER = 1;
    private static final int TYPE_IMAGE = 2;
    private static final int TYPE_TITLE = 3;
    private static final int TYPE_TABLE = 4;
    private static final int TYPE_TAGS = 5;
    private static final int TYPE_TABS = 6;

    private List<ArticleTextPartViewModel> mViewModels = new ArrayList<>();

    public List<String> getArticlesTextParts() {
        return ArticleTextPartViewModel.convertToStringList(mViewModels);
    }

    private SetTextViewHTML.TextItemsClickListener mTextItemsClickListener;

    public void setTextItemsClickListener(SetTextViewHTML.TextItemsClickListener textItemsClickListener) {
        mTextItemsClickListener = textItemsClickListener;
    }

    //todo pass tabViewModelsAlso
    public void setData(Article article, List<SpoilerViewModel> expandedSpoilers) {
//        Timber.d("setData: %s", article);
        mViewModels.clear();

        List<String> articlesTextParts = new ArrayList<>();
        @ParseHtmlUtils.TextType
        List<String> articlesTextPartsTypes = new ArrayList<>();

//        if (article.hasTabs) {
//            Timber.d("article.text: %s", article.text);
//            mArticlesTextParts.addAll(ParseHtmlUtils.getArticlesTextParts(article.text));
//            mArticlesTextPartsTypes.addAll(ParseHtmlUtils.getListOfTextTypes(mArticlesTextParts));
//        } else {
        articlesTextParts.addAll(RealmString.toStringList(article.textParts));
        articlesTextPartsTypes.addAll(RealmString.toStringList(article.textPartsTypes));
//        }

        articlesTextParts.add(0, article.title);
        articlesTextPartsTypes.add(0, ParseHtmlUtils.TextType.TITLE);
        //DO NOT USE THIS VALUE!!!
        articlesTextParts.add(article.tags.toString());
        articlesTextPartsTypes.add(ParseHtmlUtils.TextType.TAGS);

        Timber.d("mArticlesTextPartsTypes: %s", articlesTextPartsTypes);
//        Timber.d("mArticlesTextParts.size: %s", mArticlesTextParts.size());
//        Timber.d("mArticlesTextPartsTypes.size: %s", mArticlesTextPartsTypes.size());

        for (int order = 0; order < articlesTextParts.size(); order++) {
            @ParseHtmlUtils.TextType
            String type = articlesTextPartsTypes.get(order);
            Object data;
//            boolean isInTab = false;
            switch (type) {
                case ParseHtmlUtils.TextType.SPOILER:
                    List<String> spoilerParts = ParseHtmlUtils.parseSpoilerParts(articlesTextParts.get(order));

                    SpoilerViewModel spoilerViewModel = new SpoilerViewModel();
//                    spoilerViewModel.titles = Collections.singletonList(spoilerParts.get(0));
                    spoilerViewModel.titles = new ArrayList<>(spoilerParts.subList(0, 2));
                    spoilerViewModel.mSpoilerTextParts = ParseHtmlUtils.getArticlesTextParts(spoilerParts.get(2));
                    for (String part : spoilerViewModel.mSpoilerTextParts) {
                        String partCuted = part;
                        if (part.length() > 100) {
                            partCuted = partCuted.substring(0, 100);
                        }
                        Timber.d("partCuted: %s", partCuted);
                    }
                    spoilerViewModel.mSpoilerTextPartsTypes = ParseHtmlUtils.getListOfTextTypes(spoilerViewModel.mSpoilerTextParts);

                    Timber.d("spoilerParts size: %s", spoilerViewModel.mSpoilerTextParts.size());
                    Timber.d("spoilerPartsTypes size: %s", spoilerViewModel.mSpoilerTextPartsTypes.size());

                    spoilerViewModel.isExpanded = expandedSpoilers.contains(spoilerViewModel);

                    data = spoilerViewModel;
                    break;
                case ParseHtmlUtils.TextType.TABS:
                    //create and set ViewModel (as for spoiler)
                    TabsViewModel tabsViewModel = ParseHtmlUtils.parseTabs(articlesTextParts.get(order));

                    //todo get and set state (index of opened tab)
                    tabsViewModel.setCurrentTab(0);

                    data = tabsViewModel;

                    mViewModels.add(new ArticleTextPartViewModel(type, data, false));
                    //add textParts for expanded spoilers
                    List<ArticleTextPartViewModel> viewModels = new ArrayList<>();
                    for (int i = 0; i < tabsViewModel.getTabDataList().get(tabsViewModel.getCurrentTab()).getTextParts().size(); i++) {
                        TabsViewModel.TabData tabData = tabsViewModel.getTabDataList().get(tabsViewModel.getCurrentTab());
                        @ParseHtmlUtils.TextType
                        String typeInSpoiler = tabData.getTextPartsTypes().get(i);
                        String dataInSpoiler = tabData.getTextParts().get(i);
                        viewModels.add(new ArticleTextPartViewModel(typeInSpoiler, dataInSpoiler, true));
                    }
                    mViewModels.addAll(viewModels);
                    return;
                case ParseHtmlUtils.TextType.TAGS:
                    data = article.tags;
                    break;
                default:
                    data = articlesTextParts.get(order);
                    break;
            }

            mViewModels.add(new ArticleTextPartViewModel(type, data, false));
            //add textParts for expanded spoilers
            if (data instanceof SpoilerViewModel && ((SpoilerViewModel) data).isExpanded) {
                SpoilerViewModel spoilerViewModel = ((SpoilerViewModel) data);
                List<ArticleTextPartViewModel> viewModels = new ArrayList<>();
                for (int i = 0; i < spoilerViewModel.mSpoilerTextPartsTypes.size(); i++) {
                    @ParseHtmlUtils.TextType
                    String typeInSpoiler = spoilerViewModel.mSpoilerTextPartsTypes.get(i);
                    String dataInSpoiler = spoilerViewModel.mSpoilerTextParts.get(i);
                    viewModels.add(new ArticleTextPartViewModel(typeInSpoiler, dataInSpoiler, true));
                }
                mViewModels.addAll(viewModels);
            }
        }

        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        @ParseHtmlUtils.TextType
        String type = mViewModels.get(position).type;
        switch (type) {
            case ParseHtmlUtils.TextType.TITLE:
                return TYPE_TITLE;
            case ParseHtmlUtils.TextType.TEXT:
                return TYPE_TEXT;
            case ParseHtmlUtils.TextType.IMAGE:
                return TYPE_IMAGE;
            case ParseHtmlUtils.TextType.SPOILER:
                return TYPE_SPOILER;
            case ParseHtmlUtils.TextType.TABLE:
                return TYPE_TABLE;
            case ParseHtmlUtils.TextType.TAGS:
                return TYPE_TAGS;
            case ParseHtmlUtils.TextType.TABS:
                return TYPE_TABS;
            default:
                throw new IllegalArgumentException("unexpected type: " + type);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case TYPE_TITLE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_title, parent, false);
                return new ArticleTitleHolder(view);
            case TYPE_IMAGE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_r_img, parent, false);
                return new ArticleImageHolder(view, mTextItemsClickListener);
            case TYPE_TEXT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_text, parent, false);
                return new ArticleTextHolder(view, mTextItemsClickListener);
            case TYPE_SPOILER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_spoiler, parent, false);
                return new ArticleSpoilerHolder(view, this);
            case TYPE_TABLE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_table, parent, false);
                return new ArticleTableHolder(view, mTextItemsClickListener);
            case TYPE_TAGS:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_tags, parent, false);
                return new ArticleTagsHolder(view, mTextItemsClickListener);
            case TYPE_TABS:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_tabs, parent, false);
                return new ArticleTabsHolder(view, this);
            default:
                throw new IllegalArgumentException("unexpected type: " + viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_TITLE:
                ((ArticleTitleHolder) holder).bind(mViewModels.get(position));
                break;
            case TYPE_TEXT:
                ((ArticleTextHolder) holder).bind(mViewModels.get(position));
                break;
            case TYPE_IMAGE:
                ((ArticleImageHolder) holder).bind(mViewModels.get(position));
                break;
            case TYPE_SPOILER:
                ((ArticleSpoilerHolder) holder).bind((SpoilerViewModel) mViewModels.get(position).data);
                break;
            case TYPE_TABLE:
                ((ArticleTableHolder) holder).bind(mViewModels.get(position));
                break;
            case TYPE_TAGS:
                ((ArticleTagsHolder) holder).bind((RealmList<ArticleTag>) mViewModels.get(position).data);
                break;
            case TYPE_TABS:
                ((ArticleTabsHolder) holder).bind((TabsViewModel) mViewModels.get(position).data);
                break;
            default:
                throw new IllegalArgumentException("unexpected item type: " + getItemViewType(position));
        }
    }

    @Override
    public int getItemCount() {
        return mViewModels.size();
    }

    @Override
    public long getItemId(int position) {
        return mViewModels.get(position).data.hashCode();
    }

    @Override
    public void onSpoilerExpand(int position) {
        Timber.d("onSpoilerExpand: %s", position);
        if (position == RecyclerView.NO_POSITION) {
            return;
        }

        SpoilerViewModel spoilerViewModel = ((SpoilerViewModel) mViewModels.get(position).data);
//        Timber.d("mSpoilerTextPartsTypes size: %s", spoilerViewModel.mSpoilerTextPartsTypes.size());
//        Timber.d("mSpoilerTextPartsTypes: %s", spoilerViewModel.mSpoilerTextPartsTypes);
//        Timber.d("mSpoilerTextParts size: %s", spoilerViewModel.mSpoilerTextParts.size());
//        Timber.d("mSpoilerTextParts: %s", spoilerViewModel.mSpoilerTextParts);
        List<ArticleTextPartViewModel> viewModels = new ArrayList<>();
        for (int order = 0; order < spoilerViewModel.mSpoilerTextPartsTypes.size(); order++) {
            @ParseHtmlUtils.TextType
            String type = spoilerViewModel.mSpoilerTextPartsTypes.get(order);
            if (type.equals(ParseHtmlUtils.TextType.TABS)) {
                TabsViewModel tabsViewModel = ParseHtmlUtils.parseTabs(spoilerViewModel.mSpoilerTextParts.get(order));

                tabsViewModel.isInSpoiler = true;
                //todo get and set state (index of opened tab)
                tabsViewModel.setCurrentTab(0);

                viewModels.add(new ArticleTextPartViewModel(type, tabsViewModel, true));
                //add textParts for expanded spoilers
                List<ArticleTextPartViewModel> viewModelsTabs = new ArrayList<>();
                for (int i = 0; i < tabsViewModel.getTabDataList().get(tabsViewModel.getCurrentTab()).getTextParts().size(); i++) {
                    TabsViewModel.TabData tabData = tabsViewModel.getTabDataList().get(tabsViewModel.getCurrentTab());
                    @ParseHtmlUtils.TextType
                    String typeInSpoiler = tabData.getTextPartsTypes().get(i);
                    String dataInSpoiler = tabData.getTextParts().get(i);
                    viewModelsTabs.add(new ArticleTextPartViewModel(typeInSpoiler, dataInSpoiler, true));
                }
                viewModels.addAll(viewModelsTabs);
            } else {
                viewModels.add(new ArticleTextPartViewModel(type, spoilerViewModel.mSpoilerTextParts.get(order), true));
            }
        }
        mViewModels.addAll(position + 1, viewModels);

        notifyItemRangeInserted(position + 1, viewModels.size());

        mTextItemsClickListener.onSpoilerExpand(spoilerViewModel);
    }

    @Override
    public void onSpoilerCollapse(int position) {
        Timber.d("onSpoilerCollapse: %s", position);
        if (position == RecyclerView.NO_POSITION) {
            return;
        }

        SpoilerViewModel spoilerViewModel = ((SpoilerViewModel) mViewModels.get(position).data);
        List<ArticleTextPartViewModel> viewModels = new ArrayList<>();
        for (int order = 0; order < spoilerViewModel.mSpoilerTextPartsTypes.size(); order++) {
            @ParseHtmlUtils.TextType
            String type = spoilerViewModel.mSpoilerTextPartsTypes.get(order);

            if (type.equals(ParseHtmlUtils.TextType.TABS)) {
//                TabsViewModel tabsViewModel = ParseHtmlUtils.parseTabs(spoilerViewModel.mSpoilerTextParts.get(order));
//
//                //todo get and set state (index of opened tab)
//                tabsViewModel.setCurrentTab(0);
                List<ArticleTextPartViewModel> subList = new ArrayList<>(mViewModels.subList(position, mViewModels.size()));
                TabsViewModel tabsViewModel = null;
                for (ArticleTextPartViewModel partViewModel: subList){
                    if(partViewModel.type.equals(ParseHtmlUtils.TextType.TABS)){
                        tabsViewModel = (TabsViewModel) partViewModel.data;
                    }
                }
                if(tabsViewModel==null){
                    throw new IllegalStateException("tabsViewModel is null while collapse spoiler!");
                }

                viewModels.add(new ArticleTextPartViewModel(type, tabsViewModel, false));

                for (TabsViewModel.TabData tabData:tabsViewModel.getTabDataList()){
                    List<ArticleTextPartViewModel> viewModelsTabs = new ArrayList<>();
                    for (int i = 0; i < tabData.getTextParts().size(); i++) {
//                        TabsViewModel.TabData tabData = tabsViewModel.getTabDataList().get(tabsViewModel.getCurrentTab());
                        @ParseHtmlUtils.TextType
                        String typeInSpoiler = tabData.getTextPartsTypes().get(i);
                        String dataInSpoiler = tabData.getTextParts().get(i);
                        viewModelsTabs.add(new ArticleTextPartViewModel(typeInSpoiler, dataInSpoiler, true));
                    }
                    if(mViewModels.containsAll(viewModelsTabs)) {
                        viewModels.addAll(viewModelsTabs);
                    }
                }
            } else {
                String data = spoilerViewModel.mSpoilerTextParts.get(order);
                viewModels.add(new ArticleTextPartViewModel(type, data, true));
            }
        }

        mViewModels
                .subList(position + 1, position + 1 + viewModels.size())
                .clear();

        notifyItemRangeRemoved(position + 1, viewModels.size());

        mTextItemsClickListener.onSpoilerCollapse(spoilerViewModel);
    }

    @Override
    public void onTabSelected(int positionInList, int positionInTabs) {
        Timber.d("onTabSelected: %s/%s", positionInList, positionInTabs);

        if (positionInList == RecyclerView.NO_POSITION) {
            return;
        }

        TabsViewModel tabsViewModel = ((TabsViewModel) mViewModels.get(positionInList).data);

        for (TabsViewModel.TabData tabData : tabsViewModel.getTabDataList()) {
            List<ArticleTextPartViewModel> viewModelsTabs = new ArrayList<>();
            for (int i = 0; i < tabData.getTextParts().size(); i++) {
                @ParseHtmlUtils.TextType
                String typeInSpoiler = tabData.getTextPartsTypes().get(i);
                //todo add spoiler support
                String dataInSpoiler = tabData.getTextParts().get(i);
                viewModelsTabs.add(new ArticleTextPartViewModel(typeInSpoiler, dataInSpoiler, true));
            }
            if (mViewModels.containsAll(viewModelsTabs)) {
                mViewModels
                        .subList(positionInList + 1, positionInList + 1 + viewModelsTabs.size())
                        .clear();

                notifyItemRangeRemoved(positionInList + 1, viewModelsTabs.size());
//                break;
            }
        }

        //add new viewModels
        List<ArticleTextPartViewModel> viewModels = new ArrayList<>();
        List<ArticleTextPartViewModel> viewModelsTabs = new ArrayList<>();
        for (int i = 0; i < tabsViewModel.getTabDataList().get(positionInTabs).getTextParts().size(); i++) {
            TabsViewModel.TabData tabData = tabsViewModel.getTabDataList().get(positionInTabs);
            @ParseHtmlUtils.TextType
            String typeInSpoiler = tabData.getTextPartsTypes().get(i);
            //todo add spoiler support
            String dataInSpoiler = tabData.getTextParts().get(i);
            viewModelsTabs.add(new ArticleTextPartViewModel(typeInSpoiler, dataInSpoiler, true));
        }
        viewModels.addAll(viewModelsTabs);

        mViewModels.addAll(positionInList + 1, viewModels);

        notifyItemRangeInserted(positionInList + 1, viewModels.size());

        mTextItemsClickListener.onTabSelected(tabsViewModel);
    }
}