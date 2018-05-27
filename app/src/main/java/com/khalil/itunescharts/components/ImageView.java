package com.khalil.itunescharts.components;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.io.File;

public class ImageView extends android.support.v7.widget.AppCompatImageView{


	public ImageView(Context context) {

		super (context);
	}

	public ImageView(Context context, AttributeSet attrs) {

		super (context, attrs);
	}

	public ImageView(Context context, AttributeSet attrs, int defStyle) {

		super(context, attrs, defStyle);
	}

	@Override
	public void setImageResource(int resId) {

		if(isInEditMode()) {
			super.setImageResource(resId);
			return;
		}

		imageLoader(resId).load();
	}

	protected RequestCreator applyTransform(RequestCreator requestCreator) {
		return requestCreator;
	}

	public ImageLoader imageLoader(Uri uri) {
		return new ImageLoader(uri);
	}

	public ImageLoader imageLoader(String url) {
		return new ImageLoader(url);
	}

	public ImageLoader imageLoader(int resID) {
		return new ImageLoader(resID);
	}

	public ImageLoader imageLoader(File file) {
		return new ImageLoader(file);
	}

	public final class ImageLoader {
		private RequestCreator requestCreator;

		private ImageLoader(Uri uri) {
			requestCreator = Picasso.get().load(uri);
		}

		private ImageLoader(String url) {
			if(TextUtils.isEmpty(url)) {
				return;
			}
			requestCreator = Picasso.get().load(url);
		}

		private ImageLoader(int resID) {
			if(resID < 1) {
				return;
			}
			requestCreator = Picasso.get().load(resID);
		}

		private ImageLoader(File file) {
			if(file == null) {
				return;
			}
			requestCreator = Picasso.get().load(file);
		}

		public ImageLoader resize(int dimenResWidth, int dimenResHeight) {
			if(requestCreator != null && dimenResHeight > 0 && dimenResWidth > 0) {
				requestCreator = requestCreator.resizeDimen(dimenResWidth, dimenResHeight);
			}
			return this;
		}

		public ImageLoader placeholder(int resID) {
			if(resID < 1) {
				return this;
			}

			if(requestCreator == null) {
				requestCreator = Picasso.get().load(resID);
			}
			requestCreator = requestCreator.placeholder(resID);
			return this;
		}

		public ImageLoader placeholder(Drawable drawable) {
			if(requestCreator != null && drawable != null) {
				requestCreator = requestCreator.placeholder(drawable);
			}
			return this;
		}

		public ImageLoader errorPlaceHolder(int resID) {
			if(resID < 1) {
				return this;
			}

			if(requestCreator == null) {
				requestCreator = Picasso.get().load(resID);
			}
			requestCreator = requestCreator.error(resID);
			return this;
		}

		public ImageLoader errorPlaceHolder(Drawable drawable) {
			if(requestCreator != null && drawable != null) {
				requestCreator = requestCreator.error(drawable);
			}
			return this;
		}

		public void load() {
			if(requestCreator == null) {
				return;
			}
			applyTransform(requestCreator).into(ImageView.this);
		}
	}
}
