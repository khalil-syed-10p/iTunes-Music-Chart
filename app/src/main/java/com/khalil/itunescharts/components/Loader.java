package com.khalil.itunescharts.components;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.khalil.itunescharts.R;


/**
 * Provides the user interface which indicates that an operation is in progress
 * e.g. an I/O operation on network/disk. Typically, displays a
 * {@link ProgressDialog} and provides methods to interact with it.
 *
 * 
 */
@SuppressWarnings("WeakerAccess")
public final class Loader {

	/** The progress dialog. */
	private static ProgressDialog progressDialog;

	private Loader() {
	}

	/**
	 * Shows a {@link ProgressDialog} with the provided title and description.
	 * 
	 * @param activity Activity context
	 * @param message Text to be shown as the dialog title
	 * @param title Text to be shown as the dialog message
	 */
	public static void showLoader (Activity activity, CharSequence message, String title) {

		LayoutInflater inflater = LayoutInflater.from (activity);
		View progressDialogView = inflater.inflate(R.layout.view_progress_dialog, null);
		TextView pm = progressDialogView.findViewById (R.id.txtTitle);

		progressDialog = new ProgressDialog (activity);
		progressDialog.setMessage(message);
		pm.setText(message);

		if (!TextUtils.isEmpty(title)) {
			progressDialog.setTitle (title);
		}

		try {
			progressDialog.setCancelable( false );
			progressDialog.setCanceledOnTouchOutside (false);
			progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

		} catch (NullPointerException ex) {
			ex.printStackTrace();
		}

		progressDialog.show ();
		progressDialog.setContentView (progressDialogView);

	}

	/**
	 * Hides the {@link ProgressDialog} if being displayed. Internally handles
	 * possible exceptions.
	 */

	public static void hideLoader () {
		try {
			if (progressDialog.isShowing ()) {
				progressDialog.dismiss();
			}
		}
		catch (Exception ignored) {

		}

	}

	/**
	 * Returns true if Loader is showing
	 * 
	 * @return {@code true}/{@code false}
	 */
	public static boolean isLoaderShowing () {

		return progressDialog.isShowing ();
	}
}
