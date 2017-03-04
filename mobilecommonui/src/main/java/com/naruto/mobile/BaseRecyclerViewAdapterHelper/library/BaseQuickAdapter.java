
package com.naruto.mobile.BaseRecyclerViewAdapterHelper.library;

import android.animation.Animator;
import android.content.Context;
import android.support.annotation.IntDef;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import com.naruto.mobile.BaseRecyclerViewAdapterHelper.library.animation.AlphaInAnimation;
import com.naruto.mobile.BaseRecyclerViewAdapterHelper.library.animation.BaseAnimation;
import com.naruto.mobile.BaseRecyclerViewAdapterHelper.library.animation.ScaleInAnimation;
import com.naruto.mobile.BaseRecyclerViewAdapterHelper.library.animation.SlideInBottomAnimation;
import com.naruto.mobile.BaseRecyclerViewAdapterHelper.library.animation.SlideInLeftAnimation;
import com.naruto.mobile.BaseRecyclerViewAdapterHelper.library.animation.SlideInRightAnimation;
import com.naruto.mobile.R;


/**
 * https://github.com/CymChad/BaseRecyclerViewAdapterHelper
 */
public abstract class BaseQuickAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    /**
     * 是否能加载更多的标记，false时表示没有更多数据了，不会加载更多了
     */
    private boolean mNextLoadEnable = false;
    /**
     * 正在加载更多状态的标记,表示加载进行中
     */
    private boolean mLoadingMoreEnable = false;
    private boolean mFirstOnlyEnable = true;

    /**
     * 是否显示动画开关
     */
    private boolean mOpenAnimationEnable = false;

    /**
     * 是否显示空view
     */
    private boolean mEmptyEnable;//

    /**
     * 头和空view是否可同时存在展示
     */
    private boolean mHeadAndEmptyEnable;//

    /**
     * 尾和空view是否可以同时展示
     */
    private boolean mFootAndEmptyEnable;//
    private Interpolator mInterpolator = new LinearInterpolator();

    /**
     * 持续时间
     */
    private int mDuration = 300;

    /**
     * 最后一个item的为位置
     */
    private int mLastPosition = -1;
    private OnRecyclerViewItemClickListener onRecyclerViewItemClickListener;
    private OnRecyclerViewItemLongClickListener onRecyclerViewItemLongClickListener;

    /**
     * 加载更多监听器
     */
    private RequestLoadMoreListener mRequestLoadMoreListener;
    @AnimationType
    private BaseAnimation mCustomAnimation;//默认动画
    private BaseAnimation mSelectAnimation = new AlphaInAnimation();//当前选择的动画样式
    private View mHeaderView;
    private View mFooterView;
    private int pageSize = -1;
    private View mContentView;
    /**
     * View to show if there are no items to show.
     */
    private View mEmptyView;//数据为空时，显示的view

    protected static final String TAG = BaseQuickAdapter.class.getSimpleName();
    protected Context mContext;

    /**
     * item布局资源id
     */
    protected int mLayoutResId;
    protected LayoutInflater mLayoutInflater;
    protected List<T> mData;
    protected static final int HEADER_VIEW = 0x00000111;
    protected static final int LOADING_VIEW = 0x00000222;
    protected static final int FOOTER_VIEW = 0x00000333;
    protected static final int EMPTY_VIEW = 0x00000555;

    /**
     * 上滑到底部出现的加载更多的view
     */
    private View mLoadingView;

    /**
     * 系统库支持的动画类型,需要添加注解库('com.android.support:support-annotations)
     * @IntDef的使用完美替代枚举步骤：
     * １.先定义 常量定义策略
     * 2.@IntDef包住常量,@Retention ,@interface生命构造器
     * 3.使用时：用@AnimationType声明变量
     */
    @IntDef({ALPHAIN, SCALEIN, SLIDEIN_BOTTOM, SLIDEIN_LEFT, SLIDEIN_RIGHT})//包住常量
    @Retention(RetentionPolicy.SOURCE)//定义策略
    public @interface AnimationType {//生命构造器
    }

    /**
     * Use with {@link #openLoadAnimation}
     */
    public static final int ALPHAIN = 0x00000001;
    /**
     * Use with {@link #openLoadAnimation}
     */
    public static final int SCALEIN = 0x00000002;
    /**
     * Use with {@link #openLoadAnimation}
     */
    public static final int SLIDEIN_BOTTOM = 0x00000003;
    /**
     * Use with {@link #openLoadAnimation}
     */
    public static final int SLIDEIN_LEFT = 0x00000004;
    /**
     * Use with {@link #openLoadAnimation}
     */
    public static final int SLIDEIN_RIGHT = 0x00000005;


    /**
     * call the method will not enable the loadMore funcation and the params pageSize is invalid
     * more infomation see{@link  public void openLoadMore(int pageSize, boolean enable),@link  public void setOnLoadMoreListener(RequestLoadMoreListener requestLoadMoreListener)} method
     *{@link #openLoadMore}
     * @param pageSize
     * @param requestLoadMoreListener
     */
    @Deprecated
    public void setOnLoadMoreListener(int pageSize, RequestLoadMoreListener requestLoadMoreListener) {

        setOnLoadMoreListener(requestLoadMoreListener);
    }

    /**
     * 设置加载更多的动作监听
     * @param requestLoadMoreListener
     */
    public void setOnLoadMoreListener(RequestLoadMoreListener requestLoadMoreListener) {
        this.mRequestLoadMoreListener = requestLoadMoreListener;
    }

    public void setDuration(int duration) {
        mDuration = duration;
    }

    /**
     * when adapter's data size than pageSize and enable is true,the loading more function is enable,or disable
     *当adapter中数据量多于一屏的数据量且加载更多标记为true时，　即加载功能函数才有可能被调用
     * @param pageSize
     * @param enable
     */
    public void openLoadMore(int pageSize, boolean enable) {
        this.pageSize = pageSize;
        mNextLoadEnable = enable;

    }

    /**
     * call the method before you should call setPageSize() method to setting up the enablePagerSize value,whether it will  invalid
     * enable the loading more data function if enable's value is true,or disable
     *
     * @param enable
     */
    public void openLoadMore(boolean enable) {
        mNextLoadEnable = enable;

    }

    /**
     * setting up the size to decide the loading more data funcation whether enable
     * enable if the data size than pageSize,or diable
     *
     * @param pageSize
     */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * return the value of pageSize
     *
     * @return
     */
    public int getPageSize() {
        return this.pageSize;
    }

    /**
     * item项的整个条目的点击事件
     * @param onRecyclerViewItemClickListener
     */
    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener onRecyclerViewItemClickListener) {
        this.onRecyclerViewItemClickListener = onRecyclerViewItemClickListener;
    }

    public interface OnRecyclerViewItemClickListener {
        public void onItemClick(View view, int position);
    }

    /**
     * item项的整个条目的长按事件
     * @param onRecyclerViewItemLongClickListener
     */
    public void setOnRecyclerViewItemLongClickListener(OnRecyclerViewItemLongClickListener onRecyclerViewItemLongClickListener) {
        this.onRecyclerViewItemLongClickListener = onRecyclerViewItemLongClickListener;
    }

    public interface OnRecyclerViewItemLongClickListener {
        public boolean onItemLongClick(View view, int position);
    }

    private OnRecyclerViewItemChildClickListener mChildClickListener;

    /**
     * Item项上点击具体某个子view的事件监听
     * @param childClickListener
     */
    public void setOnRecyclerViewItemChildClickListener(OnRecyclerViewItemChildClickListener childClickListener) {
        this.mChildClickListener = childClickListener;
    }

    public interface OnRecyclerViewItemChildClickListener {
        void onItemChildClick(BaseQuickAdapter adapter, View view, int position);
    }

    public class OnItemChildClickListener implements View.OnClickListener {
        public int position;//当用户为item上某个子view设置OnClickListener监听时被赋值，值为当前adapter的holder最新的对应索引

        @Override
        public void onClick(View v) {
            if (mChildClickListener != null)
                mChildClickListener.onItemChildClick(BaseQuickAdapter.this, v, position - getHeaderViewsCount());//position - getHeaderViewsCount():当前位置数据所对应的索引
        }
    }


    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param layoutResId The layout resource id of each item.
     * @param data        A new list is created out of this one to avoid mutable list
     */
    public BaseQuickAdapter( int layoutResId, List<T> data) {
        this.mData = data == null ? new ArrayList<T>() : data;//数据初始化
        if (layoutResId != 0) {
            this.mLayoutResId = layoutResId;//保存布局资源id
        }
    }

    public BaseQuickAdapter( List<T> data) {
        this(0, data);
    }

    public BaseQuickAdapter(View contentView, List<T> data) {
        this( 0, data);
        mContentView = contentView;
    }

    /**
     * 移除某个item
     * @param position
     */
    public void remove(int position) {
        mData.remove(position);
        // Position of the item that has now been removed
        notifyItemRemoved(position + getHeaderViewsCount());//移除的位置修正

    }

    /**
     * 添加数据
     * @param position
     * @param item
     */
    public void add(int position, T item) {
        mData.add(position, item);
        //Position of the newly inserted item in the data set
        notifyItemInserted(position);//
    }


    /**
     * setting up a new instance to data;
     *
     * @param data
     */
    public void setNewData(List<T> data) {
        this.mData = data;
        //reset加载更多的关联参数
        if (mRequestLoadMoreListener != null) {
            mNextLoadEnable = true;
            mFooterView = null;
        }
        mLastPosition = -1;
        notifyDataSetChanged();
    }

    /**
     * additional data;
     *
     * @param data
     */
    public void addData(List<T> data) {
        this.mData.addAll(data);
        notifyDataSetChanged();
    }

    /**
     * 上滑到底部出现的加载更多的view
     * @param loadingView
     */
    public void setLoadingView(View loadingView) {
        this.mLoadingView = loadingView;
    }

    public List getData() {
        return mData;
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    public T getItem(int position) {
        return mData.get(position);
    }

    /**
     * 获取头view的个数
     * @return
     */
    public int getHeaderViewsCount() {
        return mHeaderView == null ? 0 : 1;
    }

    /**
     * 获取尾view的个数
     * @return
     */
    public int getFooterViewsCount() {
        return mFooterView == null ? 0 : 1;
    }

    /**
     * 空view的个数,一般就是一个
     * @return
     */
    public int getmEmptyViewCount() {
        return mEmptyView == null ? 0 : 1;
    }

    /**
     * 系统回调方法
     * @return item是总个数
     */
    @Override
    public int getItemCount() {
        int i = isLoadMore() ? 1 : 0;//有加载更多，加载更多view占用一个个数,值为1
        int count = mData.size() + i + getHeaderViewsCount() + getFooterViewsCount();//数据集size+头和尾view个数+加载更多view数
        if (mData.size() == 0 && mEmptyView != null) {//数据集为空但mEmptyView不为空
            //以下是本人注释掉的，本人以下判断没什么鸟用????
//            /**
//             *  setEmptyView(false) and add emptyView
//             *  {@link #setEmptyView}
//             */
//            if (count == 0 && (!mHeadAndEmptyEnable || !mFootAndEmptyEnable)) {
//                //数据集为空，头和尾view都没有，且
//                count += getmEmptyViewCount();
//                /**
//                 * {@link #setEmptyView(true, true, View)}
//                 */
//            } else if (mHeadAndEmptyEnable || mFootAndEmptyEnable) {
//                count += getmEmptyViewCount();
//            }
//
//            //(头view和空view可以同时存在,且头view个数为1,且adapter中所有的view数量为1)　或者没有当前view显示
//            if ((mHeadAndEmptyEnable && getHeaderViewsCount() == 1 && count == 1) || count == 0) {
//                mEmptyEnable = true;
//                count += getmEmptyViewCount();
//            }

            mEmptyEnable = true;
            count += getmEmptyViewCount();

        }
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        /**
         * mHeaderView不为空，且当前位置索引是0,显示mHeaderView
         */
        if (mHeaderView != null && position == 0) {
            return HEADER_VIEW;
        }
        /**
         * mEmptyView存在的情况
         */
        if (mData.size() == 0 && mEmptyEnable && mEmptyView != null && position <= 2) {
            /**
             * if set {@link #setEmptyView(boolean, boolean, View)}  position = 1
             */
            if ((mHeadAndEmptyEnable || mFootAndEmptyEnable) && position == 1) {
                /**
                 * if user want to show headview and footview and emptyView but not add headview
                 */
                if (mHeaderView == null && mEmptyView != null && mFooterView != null) {
                    //mHeaderView为空，mEmptyView不为空，mFooterVie不为空，且当前位置索引是1
                    //只能显示mFooterView
                    return FOOTER_VIEW;//当前只有mEmptyView和mFooterView，索引1位置只能是mFooterView
                    /**
                     * add headview
                     */
                } /*else*/
                if (mHeaderView != null && mEmptyView != null) {
                    //mHeaderView不为空，mEmptyView不为空，mFooterView为空，且当前位置索引是1
                    //只能是显示mEmptyView
                    return EMPTY_VIEW;//当前只有mHeaderView和mEmptyView，索引1位置只能是mEmptyView
                }
            } /*else */
            if (position == 0) {//此时mHeaderView、mFooterView不能与mEmptyView共存或者position　!= 1
                /**
                 * has no emptyView just add emptyview
                 */
                if (mHeaderView == null) {//第一个索引位置,且没有头view
                    return EMPTY_VIEW;
                } else if (mFooterView != null) {
                    return EMPTY_VIEW;
                }


            } /*else */
            if (position == 2 && (mFootAndEmptyEnable || mHeadAndEmptyEnable) && mHeaderView != null
                    && mEmptyView != null) {
                //当前索引为2，且mHeaderView和mEmptyView都不为空
                return FOOTER_VIEW;

            } /**
             * user forget to set {@link #setEmptyView(boolean, boolean, View)}  but add footview and headview and emptyview
             */
            /*else*/
            if ((!mFootAndEmptyEnable || !mHeadAndEmptyEnable) && position == 1 && mFooterView != null) {
                return FOOTER_VIEW;
            }
        } /*else*/
        if (mEmptyView != null && getItemCount() == (mHeadAndEmptyEnable ? 2 : 1) && mEmptyEnable) {
            return EMPTY_VIEW;
        } /*else*/
        if (position == mData.size() + getHeaderViewsCount()) {
            if (mNextLoadEnable) {
                return LOADING_VIEW;//加载更多
            } else {
                return FOOTER_VIEW;
            }
        }
        return getDefItemViewType(position - getHeaderViewsCount());//除去头的正常contentView
    }

    protected int getDefItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseViewHolder baseViewHolder = null;
        this.mContext = parent.getContext();
        this.mLayoutInflater = LayoutInflater.from(mContext);
        switch (viewType) {
            case LOADING_VIEW:
                baseViewHolder = getLoadingView(parent);
                initItemClickListener(baseViewHolder);
                break;
            case HEADER_VIEW:
                baseViewHolder = new BaseViewHolder(mContext, mHeaderView);
                break;
            case EMPTY_VIEW:
                baseViewHolder = new BaseViewHolder(mContext, mEmptyView);//获取空view的Holder
                break;
            case FOOTER_VIEW:
                baseViewHolder = new BaseViewHolder(mContext, mFooterView);
                break;
            default:
                baseViewHolder = onCreateDefViewHolder(parent, viewType);
                initItemClickListener(baseViewHolder);//设置监听
        }
        return baseViewHolder;

    }

    private BaseViewHolder getLoadingView(ViewGroup parent) {
        if (mLoadingView == null) {//mLoadingView由外部设置进来，如果不设置采用adapter默认
            return createBaseViewHolder(parent, R.layout.def_loading);
        }
        return new BaseViewHolder(mContext, mLoadingView);
    }

    /**
     * 当前item项attach到屏幕时
     * @param holder
     */
    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        int type = holder.getItemViewType();
        if (type == EMPTY_VIEW || type == HEADER_VIEW || type == FOOTER_VIEW || type == LOADING_VIEW) {
            setFullSpan(holder);//设置适应全屏
        }
    }

    /**
     * 处理StaggeredGridLayoutManager专用
     * @param holder
     */
    protected void setFullSpan(RecyclerView.ViewHolder holder) {
        if (holder.itemView.getLayoutParams() instanceof StaggeredGridLayoutManager.LayoutParams) {
            StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
            params.setFullSpan(true);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int positions) {

        switch (holder.getItemViewType()) {
            case 0://系统默认值，普通item
                //子类中去具体实现
                convert((BaseViewHolder) holder, mData.get(holder.getLayoutPosition() - getHeaderViewsCount()));
                addAnimation(holder);//设置动画效果
                break;
            case LOADING_VIEW://加载更多item
                addLoadMore(holder);
                break;
            case HEADER_VIEW://头view
                break;
            case EMPTY_VIEW://空view
                break;
            case FOOTER_VIEW://尾view
                break;
            default:
                convert((BaseViewHolder) holder, mData.get(holder.getLayoutPosition() - getHeaderViewsCount()));
                onBindDefViewHolder((BaseViewHolder) holder, mData.get(holder.getLayoutPosition() - getHeaderViewsCount()));
                break;
        }

    }

    protected BaseViewHolder onCreateDefViewHolder(ViewGroup parent, int viewType) {
        return createBaseViewHolder(parent, mLayoutResId);
    }

    protected BaseViewHolder createBaseViewHolder(ViewGroup parent, int layoutResId) {
        if (mContentView == null) {//mContentView由外部设置进来
            return new BaseViewHolder(mContext, getItemView(layoutResId, parent));
        }
        return new BaseViewHolder(mContext, mContentView);
    }


    public void addHeaderView(View header) {
        this.mHeaderView = header;
        this.notifyDataSetChanged();
    }

    public void addFooterView(View footer) {
        mNextLoadEnable = false;//尾view与加载更多view互斥
        this.mFooterView = footer;
        this.notifyDataSetChanged();
    }

    /**
     * Sets the view to show if the adapter is empty
     */
    public void setEmptyView(View emptyView) {
        setEmptyView(false, false, emptyView);
    }

    public void setEmptyView(boolean isHeadAndEmpty, View emptyView) {
        setEmptyView(isHeadAndEmpty, false, emptyView);
    }

    /**
     * set emptyView show if adapter is empty and want to show headview and footview
     *
     * @param isHeadAndEmpty
     * @param isFootAndEmpty
     * @param emptyView
     */
    public void setEmptyView(boolean isHeadAndEmpty, boolean isFootAndEmpty, View emptyView) {
        mHeadAndEmptyEnable = isHeadAndEmpty;
        mFootAndEmptyEnable = isFootAndEmpty;
        mEmptyView = emptyView;
        mEmptyEnable = true;
    }

    /**
     * When the current adapter is empty, the BaseQuickAdapter can display a special view
     * called the empty view. The empty view is used to provide feedback to the user
     * that no data is available in this AdapterView.
     *
     * @return The view to show if the adapter is empty.
     */
    public View getEmptyView() {
        return mEmptyView;
    }

    /**
     * see more {@link  public void notifyDataChangedAfterLoadMore(boolean isNextLoad)}
     *
     * @param isNextLoad
     */
    @Deprecated
    public void isNextLoad(boolean isNextLoad) {
        mNextLoadEnable = isNextLoad;
        mLoadingMoreEnable = false;
        notifyDataSetChanged();

    }

    /**
     * 没有更多数据时进行的设置
     * @param isNextLoad
     */
    public void notifyDataChangedAfterLoadMore(boolean isNextLoad) {
        mNextLoadEnable = isNextLoad;
        mLoadingMoreEnable = false;
        notifyDataSetChanged();

    }

    public void notifyDataChangedAfterLoadMore(List<T> data, boolean isNextLoad) {
        mData.addAll(data);
        notifyDataChangedAfterLoadMore(isNextLoad);

    }


    private void addLoadMore(RecyclerView.ViewHolder holder) {
        if (isLoadMore() && !mLoadingMoreEnable) {
            mLoadingMoreEnable = true;//正在加载更多状态
            mRequestLoadMoreListener.onLoadMoreRequested();//回调
        }
    }

    private void initItemClickListener(final BaseViewHolder baseViewHolder) {
        if (onRecyclerViewItemClickListener != null) {
            baseViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRecyclerViewItemClickListener.onItemClick(v, baseViewHolder.getLayoutPosition() - getHeaderViewsCount());//校正位置
                }
            });
        }
        if (onRecyclerViewItemLongClickListener != null) {
            baseViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return onRecyclerViewItemLongClickListener.onItemLongClick(v, baseViewHolder.getLayoutPosition() - getHeaderViewsCount());//校正位置
                }
            });
        }
    }

    /**
     * 开启动画,在onBindViewHolder中调用
     *
     * getLayoutPosition和getAdapterPosition：具体区别就是adapter和layout的位置会有时间差(<16ms), 如果你改变了Adapter的数据然后刷新视图, layout需要过一段时间才会更新视图, 在这段时间里面, 这两个方法返回的position会不一样.

     另外答案还提到, 在notifyDataSetChanged之后并不能马上获取Adapter中的position, 要等布局结束之后才能获取到.

     而对于Layout的position, 在notifyItemInserted之后, Layout不能马上获取到新的position, 因为布局还没更新(需要<16ms的时间刷新视图), 所以只能获取到旧的, 但是Adapter中的position就可以马上获取到最新的position.
     * @param holder
     */
    private void addAnimation(RecyclerView.ViewHolder holder) {
        if (mOpenAnimationEnable) {//支持动画,这个值是通过外部openLoadAnimation调用进行设置的
            if (!mFirstOnlyEnable || holder.getLayoutPosition() > mLastPosition) {//当前非最后一个item
                BaseAnimation animation = null;
                if (mCustomAnimation != null) {//默认动画不为空，启用默认动画，否则启用设置的当前选中的动画
                    animation = mCustomAnimation;
                } else {
                    animation = mSelectAnimation;
                }
                for (Animator anim : animation.getAnimators(holder.itemView)) {//遍历动画，一个一个执行
                    startAnim(anim, holder.getLayoutPosition());//开启动画
                }
                mLastPosition = holder.getLayoutPosition();//记录当前item位置
            }
        }
    }

    /**
     * 开启动画
     * @param anim
     * @param index
     */
    protected void startAnim(Animator anim, int index) {
        anim.setDuration(mDuration).start();//动画持续时间为300ms
        anim.setInterpolator(mInterpolator);//
    }

    /**
     * 判断是否有加载更多
     * @return
     */
    private boolean isLoadMore() {
        //mNextLoadEnable这个标记必须是true,pageSize有效，mRequestLoadMoreListener监听有设置
        return mNextLoadEnable && pageSize != -1 && mRequestLoadMoreListener != null && mData.size() >= pageSize;
    }

    protected View getItemView(int layoutResId, ViewGroup parent) {
        return mLayoutInflater.inflate(layoutResId, parent, false);
    }


    /**
     * @see #convert(BaseViewHolder, Object) ()
     * @deprecated This method is deprecated
     * {@link #convert(BaseViewHolder, Object)} depending on your use case.
     */
    @Deprecated
    protected void onBindDefViewHolder(BaseViewHolder holder, T item) {
    }

    /**
     * 加载更多
     */
    public interface RequestLoadMoreListener {

        void onLoadMoreRequested();
    }


    /**
     * Set the view animation type.
     *
     * @param animationType One of {@link #ALPHAIN}, {@link #SCALEIN}, {@link #SLIDEIN_BOTTOM}, {@link #SLIDEIN_LEFT}, {@link #SLIDEIN_RIGHT}.
     */
    public void openLoadAnimation(@AnimationType int animationType) {
        this.mOpenAnimationEnable = true;
        mCustomAnimation = null;//默认动画reset
        switch (animationType) {
            case ALPHAIN:
                mSelectAnimation = new AlphaInAnimation();
                break;
            case SCALEIN:
                mSelectAnimation = new ScaleInAnimation();
                break;
            case SLIDEIN_BOTTOM:
                mSelectAnimation = new SlideInBottomAnimation();
                break;
            case SLIDEIN_LEFT:
                mSelectAnimation = new SlideInLeftAnimation();
                break;
            case SLIDEIN_RIGHT:
                mSelectAnimation = new SlideInRightAnimation();
                break;
            default:
                break;
        }
    }

    /**
     * Set Custom ObjectAnimator
     *
     * @param animation ObjectAnimator
     */
    public void openLoadAnimation(BaseAnimation animation) {
        this.mOpenAnimationEnable = true;
        this.mCustomAnimation = animation;
    }

    /**
     * 控制是否支持显示动画的开关字段
     */
    public void openLoadAnimation() {
        this.mOpenAnimationEnable = true;
    }


    /**
     * true:动画只能从手指上滑的时候出现，手指下滑的时候没有动画，false:都有动画
     * @param firstOnly
     */
    public void isFirstOnly(boolean firstOnly) {
        this.mFirstOnlyEnable = firstOnly;
    }

    /**
     * Implement this method and use the helper to adapt the view to the given item.
     *
     * @param helper A fully initialized helper.
     * @param item   The item that needs to be displayed.
     */
    protected abstract void convert(BaseViewHolder helper, T item);


    @Override
    public long getItemId(int position) {
        return position;
    }


}
