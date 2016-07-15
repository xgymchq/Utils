package com.gwsoft.library.view;

import java.util.Arrays;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

public class ClickEditText extends EditText {
	// [left, top, right, bottom]
	private OnDrawableClickListener[] onClickListeners = new OnDrawableClickListener[4];
	private int touchDrawableIndex = -1;

	{
		Arrays.fill(onClickListeners, null);
	}

	public ClickEditText(Context context) {
		super(context);
	}

	public ClickEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ClickEditText setOnLeftDrawableClickListener(
			OnDrawableClickListener listener) {
		onClickListeners[0] = listener;
		return this;
	}

	public ClickEditText setOnTopDrawableClickListener(
			OnDrawableClickListener listener) {
		onClickListeners[1] = listener;
		return this;
	}

	public ClickEditText setOnRightDrawableClickListener(
			OnDrawableClickListener listener) {
		onClickListeners[2] = listener;
		return this;
	}

	public ClickEditText setOnBottomDrawableClickListener(
			OnDrawableClickListener listener) {
		onClickListeners[3] = listener;
		return this;
	}

	private boolean isTouchLeftDrawable(MotionEvent event) {
		boolean isTouch = false;
		Drawable drawable = getCompoundDrawables()[0];
		if (drawable != null) {
			Rect bounds = drawable.getBounds();
			Rect rect = new Rect();
			rect.left = getPaddingLeft();
			rect.right = rect.left + bounds.width();
			rect.top = (getHeight() - bounds.height()) / 2;
			rect.bottom = rect.top + bounds.height();
			isTouch = rect.contains((int) event.getX(), (int) event.getY());
		}
		return isTouch;
	}

	private boolean isTouchTopDrawable(MotionEvent event) {
		boolean isTouch = false;
		Drawable drawable = getCompoundDrawables()[1];
		if (drawable != null) {
			Rect bounds = drawable.getBounds();
			Rect rect = new Rect();
			rect.left = (getWidth() - bounds.width()) / 2;
			rect.right = rect.left + bounds.width();
			rect.top = getPaddingTop();
			rect.bottom = rect.top + bounds.height();
			isTouch = rect.contains((int) event.getX(), (int) event.getY());
		}
		return isTouch;
	}

	private boolean isTouchRightDrawable(MotionEvent event) {
		boolean isTouch = false;
		Drawable drawable = getCompoundDrawables()[2];
		if (drawable != null) {
			int width = getWidth();
			Rect bounds = drawable.getBounds();
			Rect rect = new Rect();
			rect.right = width - getPaddingRight();
			rect.left = rect.right - bounds.width();
			rect.top = (getHeight() - bounds.height()) / 2;
			rect.bottom = rect.top + bounds.height();
			isTouch = rect.contains((int) event.getX(), (int) event.getY());
		}
		return isTouch;
	}

	private boolean isTouchBottomDrawable(MotionEvent event) {
		boolean isTouch = false;
		Drawable drawable = getCompoundDrawables()[3];
		if (drawable != null) {
			Rect bounds = drawable.getBounds();
			Rect rect = new Rect();
			rect.left = (getWidth() - bounds.width()) / 2;
			rect.right = rect.right + bounds.width();
			rect.bottom = getHeight() - getPaddingBottom();
			rect.top = rect.bottom - bounds.height();
			isTouch = rect.contains((int) event.getX(), (int) event.getY());
		}
		return isTouch;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			touchDrawableIndex = isTouchLeftDrawable(event) ? 0 : -1;
			touchDrawableIndex = touchDrawableIndex == -1 ? (isTouchTopDrawable(event) ? 1
					: -1)
					: touchDrawableIndex;
			touchDrawableIndex = touchDrawableIndex == -1 ? (isTouchRightDrawable(event) ? 2
					: -1)
					: touchDrawableIndex;
			touchDrawableIndex = touchDrawableIndex == -1 ? (isTouchBottomDrawable(event) ? 3
					: -1)
					: touchDrawableIndex;
			if (touchDrawableIndex != -1) {
				return true;
			}
			break;
		case MotionEvent.ACTION_UP:
			int upIndex = isTouchLeftDrawable(event) ? 0 : -1;
			upIndex = upIndex == -1 ? (isTouchTopDrawable(event) ? 1 : -1)
					: upIndex;
			upIndex = upIndex == -1 ? (isTouchRightDrawable(event) ? 2 : -1)
					: upIndex;
			upIndex = upIndex == -1 ? (isTouchBottomDrawable(event) ? 3 : -1)
					: upIndex;
			if (touchDrawableIndex == upIndex && upIndex != -1) {
				OnDrawableClickListener onClickListener = onClickListeners[upIndex];
				if (onClickListener != null) {
					onClickListener.onClick();
				}
				touchDrawableIndex = -1;
				return true;
			}
			touchDrawableIndex = -1;
			break;
		}
		return super.onTouchEvent(event);
	}

	public interface OnDrawableClickListener {
		void onClick();
	}

	// public void delEmoji() {
	// addTextChangedListener(new TextWatcher() {
	//
	// @Override
	// public void afterTextChanged(Editable editable) {
	// int index = getSelectionStart() - 1;
	// if (index > 0) {
	// if (!isEmojiCharacter(editable.charAt(index))) {
	// Editable edit = getText();
	// edit.delete(index, index + 1);
	// }
	// }
	// }
	//
	// @Override
	// public void beforeTextChanged(CharSequence s, int start, int count,
	// int after) {
	//
	// }
	//
	// @Override
	// public void onTextChanged(CharSequence s, int start, int before,
	// int count) {
	//
	// }
	// });
	// }
	//
	/**
	 * 检测是否有emoji表情
	 *
	 * @param source
	 * @return
	 */
	public static boolean containsEmoji(String source) {
		int len = source.length();
		for (int i = 0; i < len; i++) {
			char codePoint = source.charAt(i);
			if (!isEmojiCharacter(codePoint)) { // 如果不能匹配,则该字符是Emoji表情
				return true;
			}
		}
		return false;
	}

	public static boolean isEmojiCharacter(char codePoint) {
		return (codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA)
				|| (codePoint == 0xD)
				|| ((codePoint >= 0x20) && (codePoint <= 0xD7FF))
				|| ((codePoint >= 0xE000) && (codePoint <= 0xFFFD))
				|| ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF));
	}
}
