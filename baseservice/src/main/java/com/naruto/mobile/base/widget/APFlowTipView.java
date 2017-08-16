package com.naruto.mobile.base.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.naruto.mobile.base.R;

/**
 *
 * @author fengpingwufpalipaycom
 * @version $Id: APFlowTipView.java, v 0.1 2014-1-23 上午10:52:42 fengpingwufpalipaycom Exp $
 */
public class APFlowTipView extends LinearLayout {
    public static final int TYPE_NETWORK_ERROR = 0x10;
    public static final int TYPE_EMPTY = 0x11;
    public static final int TYPE_WARNING = 0x12;

    private Button mAction;
    private TextView mTips;
    private TextView mSubTips;
    private ImageView mIcon;
    private boolean isSimpleMode = false;

    private int mType;

    public APFlowTipView(Context context) {
        super(context);
    }

    public APFlowTipView(Context context, AttributeSet set) {
        super(context, set);

        LayoutInflater.from(context).inflate(R.layout.ap_flow_tip_view, this, true);

        TypedArray a = context.obtainStyledAttributes(set, R.styleable.FlowTipView);
        mType = a.getInt(R.styleable.FlowTipView_flow_tip_view_type, TYPE_NETWORK_ERROR);
        isSimpleMode = a.getBoolean(R.styleable.FlowTipView_isSimpleMode, false);
        a.recycle();

        setOrientation(VERTICAL);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mAction = (Button) findViewById(R.id.action);
        mTips = (TextView) findViewById(R.id.tips);
        mSubTips = (TextView) findViewById(R.id.sub_tips);
        mIcon = (ImageView) findViewById(R.id.icon);
        resetFlowTipType(mType);
    }

	public void setIsSimpleType(boolean isSimple) {
		isSimpleMode = isSimple;
	}

	public void resetFlowTipType(int type) {
		mType = type;
		if (!isSimpleMode) {
			switch (mType) {
			case TYPE_NETWORK_ERROR:
				mIcon.setImageResource(R.drawable.flow_network_signals);
				break;
			case TYPE_EMPTY:
				mIcon.setImageResource(R.drawable.flow_empty);
				break;
			case TYPE_WARNING:
				mIcon.setImageResource(R.drawable.flow_warning);
				break;
			default:
				break;
			}
		} else {
			switch (mType) {
			case TYPE_NETWORK_ERROR:
				mIcon.setImageResource(R.drawable.flow_network_signals_simple);
				break;
			case TYPE_EMPTY:
				mIcon.setImageResource(R.drawable.flow_empty_simple);
				break;
			case TYPE_WARNING:
				mIcon.setImageResource(R.drawable.flow_warning_simple);
				break;
			default:
				break;
			}
		}
	}

    /**
     * 设置按钮的属性
     *
     * @param text
     * @param clickListener
     */
    public void setAction(String text, OnClickListener clickListener) {
        mAction.setText(text);
        mAction.setOnClickListener(clickListener);
        mAction.setVisibility(View.VISIBLE);
    }

    /**
     * 取消按钮
     */
    public void setNoAction(){
    	mAction.setVisibility(View.GONE);
    }

    /**
     * 设置提示信息
     *
     * @param text
     */
    public void setTips(String text) {
        mTips.setText(Html.fromHtml(text));
        mTips.setVisibility(View.VISIBLE);
    }

    public void setSubTips(String text) {
        mSubTips.setText(Html.fromHtml(text));
        mSubTips.setVisibility(View.VISIBLE);
    }


    public Button getActionButton(){
    	return mAction;
    }

    public ImageView getIcon(){
    	return mIcon;
    }

}
