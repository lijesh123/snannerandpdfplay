package com.snpdfp.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import com.itextpdf.text.pdf.PdfCopyFields;
import com.itextpdf.text.pdf.PdfReader;
import com.snpdfp.layout.FolderLayout;
import com.snpdfp.layout.IFolderItemListener;
import com.snpdfp.utils.SAPDFCContstants;
import com.snpdfp.utils.SAPDFPathManager;
import com.snpdfp.utils.SAPDFUtils;

public class CopyEncryptedActivity extends SNPDFActivity implements
		IFolderItemListener {
	Logger logger = Logger.getLogger(CopyEncryptedActivity.class.getName());

	FolderLayout localFolders;
	File selectedFile;

	String password;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.folders);
		localFolders = (FolderLayout) findViewById(R.id.localfolders);
		localFolders.setIFolderItemListener(this);
	}

	// Your stuff here for Cannot open Folder
	public void OnCannotFileRead(File file) {
		getAlertDialog().setTitle("Invalid selection")
				.setMessage("[" + file.getName() + "] folder can't be read!")
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).show();

	}

	// Your stuff here for file Click
	public void OnFileClicked(File file) {
		selectedFile = file;
		if (!file.getName().toLowerCase().endsWith(".pdf")) {
			getAlertDialog()
					.setTitle("Invalid selection")
					.setMessage("Please select a valid protected .pdf file!")
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}

							}).show();
		} else {
			copyPDF();
		}

	}

	private void copyPDF() {
		PdfReader pdfReader = null;
		try {
			pdfReader = new PdfReader(selectedFile.getAbsolutePath());
			if (pdfReader.isEncrypted()) {
				Intent pickPassword = new Intent(this,
						PickPasswordActivity.class);
				startActivityForResult(pickPassword,
						SAPDFCContstants.PICK_PASSWORD_REQUEST);

			} else {
				new CopyPDFExecutor().execute();
			}

		} catch (Exception e) {
			Intent pickPassword = new Intent(this, PickPasswordActivity.class);
			startActivityForResult(pickPassword,
					SAPDFCContstants.PICK_PASSWORD_REQUEST);

		} finally {
			if (pdfReader != null)
				pdfReader.close();
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == SAPDFCContstants.PICK_PASSWORD_REQUEST) {
				password = data.getStringExtra(SAPDFCContstants.TEXT);
				new CopyPDFExecutor().execute();
			}

		} else {
			operationCancelled();
		}
	}

	private class CopyPDFExecutor extends AsyncTask<String, Void, Boolean> {

		private ProgressDialog progressDialog;

		@Override
		protected void onPreExecute() {
			progressDialog = new ProgressDialog(CopyEncryptedActivity.this);
			progressDialog
					.setMessage("Copying encrypted PDF to non-encrypted one...");
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.show();

		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (progressDialog != null && progressDialog.isShowing())
				progressDialog.dismiss();

			displayResult(result);

		}

		@Override
		protected Boolean doInBackground(String... params) {
			logger.info("****** starting to copy encrypted PDF **********");
			boolean error = false;

			PrintWriter out = null;
			PdfReader pdfReader = null;
			PdfCopyFields copy = null;

			mainFile = SAPDFPathManager.getSavePDFPath("COPY_"
					+ selectedFile.getName());
			try {
				if (password != null) {
					pdfReader = new PdfReader(selectedFile.getAbsolutePath(),
							password.getBytes());
				} else {
					pdfReader = new PdfReader(selectedFile.getAbsolutePath());
				}

				copy = new PdfCopyFields(new FileOutputStream(mainFile));
				copy.addDocument(pdfReader);

			} catch (Exception e) {
				logger.log(Level.SEVERE, "Unable to extract Text from PDF", e);
				error = true;
			} finally {
				// close the writer
				if (pdfReader != null)
					pdfReader.close();

				if (copy != null) {
					copy.close();
				}

			}

			return error;

		}

	}

	public void displayResult(Boolean error) {
		setContentView(R.layout.activity_copy_encrypted);
		TextView textView = (TextView) findViewById(R.id.message);

		if (error) {
			SAPDFUtils.setErrorText(
					textView,
					"Unable to extract text from file "
							+ selectedFile.getName());
			disableButtons();

		} else {
			SAPDFUtils.setSuccessText(textView,
					"TXT file successfully created.", mainFile);
		}
	}

}
