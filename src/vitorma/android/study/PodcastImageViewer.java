package vitorma.android.study;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class PodcastImageViewer extends Activity {
	/** Called when the activity is first created. */

	public Bitmap getImageFromMp3File(String path) {

		// load data file
		MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
		metaRetriever.setDataSource(path);

		// Getting picture
		byte[] artByteArray = metaRetriever.getEmbeddedPicture();
		if (artByteArray != null) {
			Bitmap artBitmap = BitmapFactory.decodeByteArray(artByteArray, 0,
					artByteArray.length);

			// release memory
			metaRetriever.release();

			return artBitmap;
		}
		return null;

	}

	Uri mp3URI;
	ImageView iv;
	TextView tv;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		iv = (ImageView) findViewById(R.id.imageView);

		((Button) findViewById(R.id.button))
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent();

						intent.setType("audio/*");
						intent.setAction(Intent.ACTION_GET_CONTENT);

						startActivityForResult(Intent.createChooser(intent,
								"Complete action using"), 0);

					}
				});

	}

	public synchronized void onActivityResult(final int requestCode,
			int resultCode, final Intent data) {

		String filePath = "";

		if (resultCode == Activity.RESULT_OK) {

			filePath = data.getDataString();

			mp3URI = data.getData();
			filePath = getRealPathFromURI(mp3URI); // from Music

			if (filePath == null)
				filePath = mp3URI.getPath(); // from File Manager

			Bitmap artWork = getImageFromMp3File(filePath);

			if (artWork == null) {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage("this podcast have no image");
				builder.setNeutralButton("OK", null);
				AlertDialog alert = builder.create();
				alert.show();

			} else {

				// set output
				iv.setImageBitmap(artWork);

			}

		}
	}

	public String getRealPathFromURI(Uri contentUri) {
		String[] proj = { MediaStore.Audio.Media.DATA };
		Cursor cursor = managedQuery(contentUri, proj, null, null, null);

		if (cursor == null)
			return null;

		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);

		cursor.moveToFirst();

		return cursor.getString(column_index);
	}

}