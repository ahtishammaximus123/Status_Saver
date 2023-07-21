package com.example.stickers.WhatsAppBasedCode;

import static android.content.Context.MODE_PRIVATE;
import static com.example.stickers.ads.AdsExtKt.showInterAd;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.stickers.Activities.SplashActivity;
import com.example.stickers.R;
import com.example.stickers.app.BillingBaseActivity;
import com.example.stickers.app.RemoteDateConfig;
import com.example.stickers.stickers.DataArchiver;
import com.example.stickers.stickers.StickerBook;
import com.google.android.material.button.MaterialButton;
import com.theartofdev.edmodo.cropper.BuildConfig;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class StickerPackListActivityN extends Fragment {

    public static final String EXTRA_STICKER_PACK_LIST_DATA = "sticker_pack_list";
    private static final int STICKER_PREVIEW_DISPLAY_LIMIT = 5;
    private static final String TAG = "StickerPackList";
    private LinearLayoutManager packLayoutManager;
    private static RecyclerView packRecyclerView;
    private static StickerPackListAdapter allStickerPacksListAdapter;
    WhiteListCheckAsyncTask whiteListCheckAsyncTask;
    ArrayList<StickerPack> stickerPackList;
    public static Context context;
    public static String newName, newCreator = "";

    SharedPreferences categoryPref;
    SharedPreferences.Editor editor;
    private static final int REQUEST_PERMISSIONS = 1234;
    private static final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    String[] items_categories;
    List<String> plantsList;
    ArrayAdapter<String> spinnerArrayAdapter;
    int imageHeight = 0;
    int imageWidth = 0;
    long dataSize;
    String tempFileName;
    File resizedImage;
    //todo default category is Funny
    int stickerCategory = R.drawable.happy;


    String fileName;
    //TODO changing save location
    File stickerDirectory;
    ConstraintLayout constraintNoStickerPackYet;
    LottieAnimationView imageView6;
    FileOutputStream outStream = null;

    ProgressDialog progressOfTray;
    boolean isBreak = false;


    MaterialButton imageViewCreateNewpack;


    private int firstLen = 0;
    private int secLen = 0;

    //


    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);

        MenuItem item = menu.findItem(R.id.folder);
        item.setVisible(false);

        MenuItem menuWa = menu.findItem(R.id.action_wa);
        MenuItem menuBa = menu.findItem(R.id.action_ba);
        menuWa.setVisible(false);
        menuBa.setVisible(false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_sticker_pack_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        ic_back = findViewById(R.id.ic_back);
//        layoutCreateNewPack = findViewById(R.id.cardViewCreateNewpack);
        imageViewCreateNewpack = view.findViewById(R.id.btnAddNewPack);

        constraintNoStickerPackYet = view.findViewById(R.id.ConstraintNoStickerPackYet);
        imageView6 = view.findViewById(R.id.imageView6);

        categoryPref = getActivity().getSharedPreferences("category_saved", MODE_PRIVATE);
        editor = categoryPref.edit();

        imageViewCreateNewpack.setOnClickListener(v -> showInterAd(requireActivity(),
                RemoteDateConfig.getRemoteAdSettings().getInter_create_sticker(),
                () -> {
                    addNewStickerPackInInterface();
                    return null;
                }));

        progressOfTray = new ProgressDialog(getContext(), 0);
        progressOfTray.setTitle("Generating Icon...");
        progressOfTray.setMessage("Image is Large, please wait");
        progressOfTray.setCancelable(false);
        progressOfTray.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isBreak = false;
                progressOfTray.dismiss();//dismiss dialog
            }
        });

        StickerBook.init(getContext());

//        Fresco.initialize(this);

        context = getContext();


        packRecyclerView = view.findViewById(R.id.sticker_pack_list);
        stickerPackList = StickerBook.getAllStickerPacks();

        Collections.reverse(stickerPackList);
        if (stickerPackList.size() <= 0) {
            packRecyclerView.setVisibility(View.GONE);
            constraintNoStickerPackYet.setVisibility(View.VISIBLE);
            imageView6.setVisibility(View.VISIBLE);
        } else {
            constraintNoStickerPackYet.setVisibility(View.GONE);
            imageView6.setVisibility(View.GONE);
            packRecyclerView.setVisibility(View.VISIBLE);
        }
        //getIntent().getParcelableArrayListExtra( EXTRA_STICKER_PACK_LIST_DATA);
        showStickerPackList(stickerPackList);

        if (Intent.ACTION_SEND.equals(getActivity().getIntent().getAction())) {
            Bundle extras = getActivity().getIntent().getExtras();
            if (extras.containsKey(Intent.EXTRA_STREAM)) {
                Uri uri = extras.getParcelable(Intent.EXTRA_STREAM);
                if (uri != null) {
                    DataArchiver.importZipFileToStickerPack(uri, getActivity());
                }
            }
        }

    }


    @Override
    public void onResume() {
        super.onResume();
        stickerPackList = StickerBook.getAllStickerPacks();
        if (stickerPackList.size() <= 0) {
            packRecyclerView.setVisibility(View.GONE);
            constraintNoStickerPackYet.setVisibility(View.VISIBLE);
            imageView6.setVisibility(View.VISIBLE);
        } else {
            constraintNoStickerPackYet.setVisibility(View.GONE);
            imageView6.setVisibility(View.GONE);
            packRecyclerView.setVisibility(View.VISIBLE);
        }
        String action = getActivity().getIntent().getAction();
        if (action == null) {
            Log.v("Example", "Force restart");
//            Intent intent = new Intent(getActivity(), StickerPackListActivityN.class);
//            intent.setAction("Already created");
//            startActivity(intent);
//            finish();
        }

        whiteListCheckAsyncTask = new WhiteListCheckAsyncTask(this);
        //noinspection unchecked
        whiteListCheckAsyncTask.execute(stickerPackList);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && arePermissionDenied()) {
//            requestPermissions(PERMISSIONS, REQUEST_PERMISSIONS);
//            return;
//        }
//
//        Common.APP_DIR = Environment.getExternalStorageDirectory().getPath() +
//                File.separator + "StatusDownloader";

    }


    @Override
    public void onPause() {
        super.onPause();
        DataArchiver.writeStickerBookJSON(StickerBook.getAllStickerPacks(), getActivity());
        if (whiteListCheckAsyncTask != null && !whiteListCheckAsyncTask.isCancelled()) {
            whiteListCheckAsyncTask.cancel(true);
        }
    }

    @Override
    public void onDestroy() {
        DataArchiver.writeStickerBookJSON(StickerBook.getAllStickerPacks(), getActivity());
        super.onDestroy();
    }


    public void showStickerPackList(List<StickerPack> stickerPackList) {
        allStickerPacksListAdapter = new StickerPackListAdapter(stickerPackList, getActivity(), onAddButtonClickedListener);
        packRecyclerView.setAdapter(allStickerPacksListAdapter);
        packLayoutManager = new LinearLayoutManager(getContext());
        packLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                packRecyclerView.getContext(),
                packLayoutManager.getOrientation()
        );
        packRecyclerView.addItemDecoration(dividerItemDecoration);
        packRecyclerView.setLayoutManager(packLayoutManager);
        // packRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(this::recalculateColumnCount);
    }


    private final StickerPackListAdapter.OnAddButtonClickedListener onAddButtonClickedListener = new StickerPackListAdapter.OnAddButtonClickedListener() {
        @Override
        public void onAddButtonClicked(StickerPack pack, int pos) {
            Log.e("dffdfdffddf", "clickcall");
            if (pack.getStickers().size() >= 3) {
                Intent intent = new Intent();
                intent.setAction("com.whatsapp.intent.action.ENABLE_STICKER_PACK");
                //Toast.makeText(StickerPackListActivity.this, pos + "", Toast.LENGTH_SHORT).show();
                intent.putExtra(StickerPackDetailsActivity.EXTRA_STICKER_PACK_ID, pack.identifier);
                intent.putExtra(StickerPackDetailsActivity.EXTRA_STICKER_PACK_AUTHORITY, "com.example.mymememaker.WhatsAppLicensedCode.StickerContentProvider");
                intent.putExtra(StickerPackDetailsActivity.EXTRA_STICKER_PACK_NAME, pack.name);
                try {
                    BillingBaseActivity.Companion.setApplovinClicked(true);
                    StickerPackListActivityN.this.startActivityForResult(intent, StickerPackDetailsActivity.ADD_PACK);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(getContext(), R.string.error_adding_sticker_pack, Toast.LENGTH_LONG).show();
                }
            } else {
                AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                        .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).create();
                alertDialog.setTitle("Invalid Action");
                alertDialog.setMessage("In order to be applied to WhatsApp, the sticker pack must have at least 3 stickers. Please add more stickers first.");
                alertDialog.show();
            }
        }

        @Override
        public void onDeleted(boolean flag) {
            stickerPackList = StickerBook.getAllStickerPacks();
            if (stickerPackList.size() <= 0) {
                packRecyclerView.setVisibility(View.GONE);
                constraintNoStickerPackYet.setVisibility(View.VISIBLE);
                imageView6.setVisibility(View.VISIBLE);
            } else {
                constraintNoStickerPackYet.setVisibility(View.GONE);
                imageView6.setVisibility(View.GONE);
                packRecyclerView.setVisibility(View.VISIBLE);
            }
        }
    };

    private void recalculateColumnCount() {
        final int previewSize = getResources().getDimensionPixelSize(R.dimen.sticker_pack_list_item_preview_image_size);
        int firstVisibleItemPosition = packLayoutManager.findFirstVisibleItemPosition();
        StickerPackListItemViewHolder viewHolder = (StickerPackListItemViewHolder) packRecyclerView.findViewHolderForAdapterPosition(firstVisibleItemPosition);
        if (viewHolder != null) {
            final int max = Math.max(viewHolder.imageRowView.getMeasuredWidth() / previewSize, 1);
            int numColumns = Math.min(STICKER_PREVIEW_DISPLAY_LIMIT, max);
            allStickerPacksListAdapter.setMaxNumberOfStickersInARow(numColumns);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == StickerPackDetailsActivity.ADD_PACK) {
            if (resultCode == Activity.RESULT_CANCELED && data != null) {
                final String validationError = data.getStringExtra("validation_error");
                if (validationError != null) {
                    if (BuildConfig.DEBUG) {
                        //validation error should be shown to developer only, not users.
//                        BaseActivity.MessageDialogFragment.newInstance(R.string.title_validation_error, validationError).show(getSupportFragmentManager(), "validation error");
                    }
                    Log.e(TAG, "Validation failed:" + validationError);
                }
            }
        }
    }

    private void saveImage(Uri imageUri) throws IOException {
        Uri imageUriNew = imageUri;

        //TODO creating a new Directory for STICKERS
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            stickerDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Stickers");
            stickerDirectory.mkdirs();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 11);
        }


        //createNewStickerPackAndOpenIt(newName, newCreator, uri);
        fileName = "tray" + System.currentTimeMillis() + ".webp";
        File outFile = new File(stickerDirectory, fileName);
        outStream = new FileOutputStream(outFile);
        File f = new File(outFile.getAbsolutePath());
        Bitmap bitmap;
        long length = f.length();

        do {
            if (isBreak) {
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUriNew);
                    bitmap = Bitmap.createScaledBitmap(bitmap, 96, 96, true);
                    bitmap.compress(Bitmap.CompressFormat.WEBP, 80, outStream);

                    outStream.flush();
                    outStream.close();
                    length = f.length();
                    //Toast.makeText(context, "tray icon " + fileName, Toast.LENGTH_SHORT).show();
                    Log.e("checkcccheckcccheck", "tray icon : " + fileName);
                    //createNewStickerPackAndOpenIt(newName, newCreator, Uri.parse(outFile.getAbsolutePath()));
                    Log.d("checkcccheckcccheck", "onPictureTaken - wrote to " + outFile.getAbsolutePath());
                    if (length < 49000) {
                        createNewStickerPackAndOpenIt(newName, newCreator, Uri.parse(outFile.getAbsolutePath()));
                        progressOfTray.dismiss();
                        isBreak = false;
                        break;
                    } else {

                        imageUriNew = Uri.fromFile(new File(outFile.getAbsolutePath()));
                        saveImage(imageUriNew);
                        //outFile.delete();
                    }


                } catch (FileNotFoundException e) {
                    Toast.makeText(context, "checkcccheckcccheck " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            } else
                break;


        } while (true);


    }


    static class WhiteListCheckAsyncTask extends AsyncTask<List<StickerPack>, Void, List<StickerPack>> {
        private final WeakReference<StickerPackListActivityN> stickerPackListActivityWeakReference;

        WhiteListCheckAsyncTask(StickerPackListActivityN stickerPackListActivity) {
            this.stickerPackListActivityWeakReference = new WeakReference<>(stickerPackListActivity);
        }

        @SafeVarargs
        @Override
        protected final List<StickerPack> doInBackground(List<StickerPack>... lists) {
            List<StickerPack> stickerPackList = lists[0];
            final StickerPackListActivityN stickerPackListActivity = stickerPackListActivityWeakReference.get();
            if (stickerPackListActivity == null) {
                return stickerPackList;
            }
            for (StickerPack stickerPack : stickerPackList) {
                stickerPack.setIsWhitelisted(WhitelistCheck.isWhitelisted(stickerPackListActivity.requireContext(), stickerPack.identifier));
            }
            return stickerPackList;
        }

        @Override
        protected void onPostExecute(List<StickerPack> stickerPackList) {
            final StickerPackListActivityN stickerPackListActivity = stickerPackListActivityWeakReference.get();
            if (stickerPackListActivity != null) {
                allStickerPacksListAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                        .setPositiveButton("Let's Go", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //verifyStoragePermissions(StickerPackListActivity.this);
                            }
                        })
                        .create();
                alertDialog.setTitle("Notice!");
                alertDialog.setMessage("We've recognized you denied the storage access permission for this app."
                        + "\n\nIn order for this app to work, storage access is required.");
                alertDialog.show();
            }
        }

    }

    private void addNewStickerPackInInterface() {
        showDialog("Create New Sticker Pack", 0);
    }

    private void showDialog(String title, int action) {
        final Dialog dialog = new Dialog(getContext(), android.R.style.Theme_DeviceDefault_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.add_new_pack_dialog);

        TextView dialogTitle = dialog.findViewById(R.id.title);
        EditText packnameET = dialog.findViewById(R.id.packnameET);
        EditText creatornameET = dialog.findViewById(R.id.creatornameET);
        Spinner spinnerCategory = dialog.findViewById(R.id.spinnerCategory);
        TextView btnOK = dialog.findViewById(R.id.btnOK);
        btnOK.setClickable(false);
        TextView btnCANCEL = dialog.findViewById(R.id.btnCANCEL);
        dialogTitle.setText(title);
        if (action == 0) {
            btnOK.setText("CREATE");
        }
//        else if (action == 1) {
//            btnOK.setText("RENAME");
//        }

        // String[] items_categories = new String[]{"Funny", "Happy", "Angry", "Blessed", "Crazy"};
        //ArrayAdapter<String> adapterCategories = new ArrayAdapter<>(this, R.layout.color_spinner_layout, items_categories);
        //.setAdapter(adapterCategories);

        items_categories = new String[]{"Funny", "Happy", "Angry", "Blessed", "Crazy"};
        plantsList = new ArrayList<>(Arrays.asList(items_categories));
        // Initializing an ArrayAdapter
        spinnerArrayAdapter = new ArrayAdapter<String>(getContext(), R.layout.color_spinner_layout, plantsList);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.color_spinner_layout);
        spinnerCategory.setAdapter(spinnerArrayAdapter);

//        creatornameET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
//                    btnOK.performClick();
//                }
//                return false;
//            }
//        });

        packnameET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
//                firstLen = s.length();
//                if (secLen > 0 && firstLen > 0) {
//                    btnOK.setClickable(true);
//                    btnOK.setBackground(getResources().getDrawable(R.drawable.btn_shape));
//                } else {
//                    btnOK.setClickable(false);
//                    btnOK.setBackground(getResources().getDrawable(R.drawable.btn_shape_light));
//                }
            }
        });
        /*creatornameET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                secLen = s.length();
                if (secLen > 0 && firstLen > 0) {
                    btnOK.setClickable(true);
                    btnOK.setBackground(getResources().getDrawable(R.drawable.btn_shape));
                } else {
                    btnOK.setClickable(false);
                    btnOK.setBackground(getResources().getDrawable(R.drawable.btn_shape_light));
                }
            }
        });*/

        btnCANCEL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = packnameET.getText().toString();
                String createrName = creatornameET.getText().toString();
                if (TextUtils.isEmpty(name)) {
                    packnameET.setError("Pack name is required!");
                    return;
                }
                if (TextUtils.isEmpty(createrName)) {
                    creatornameET.setError("Creator is required!");
                    return;
                } else {
                    dialog.dismiss();
                    newName = packnameET.getText().toString();
                    newCreator = creatornameET.getText().toString();
                    editor.putInt((newName + newCreator), stickerCategory);
                    editor.apply();
                    createNewStickerPackAndOpenIt(newName, newCreator, Uri.parse("android.resource://" + getActivity().getPackageName() + "/drawable/" + stickerCategory));
                    Log.e("sds21ds", categoryPref.getInt((newName + newCreator), R.drawable.happy) + "");
                }
                dialog.dismiss();
            }
        });


        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                switch (selectedItem) {
                    case "Funny":
                        stickerCategory = R.drawable.funny;
                        break;
                    case "Happy":
                        stickerCategory = R.drawable.happy;
                        break;
                    case "Angry":
                        stickerCategory = R.drawable.angry;
                        break;
                    case "Blessed":
                        stickerCategory = R.drawable.blessed;
                        break;
                    case "Crazy":
                        stickerCategory = R.drawable.crazy;
                        break;
                }


            } // to close the onItemSelected

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        dialog.show();

    }

    private void createNewStickerPackAndOpenIt(String name, String creator, Uri trayImage) {


        if (SplashActivity.getFbAnalytics() != null)
            SplashActivity.getFbAnalytics().sendEvent("StkrPckActy_Create");
        String newId = UUID.randomUUID().toString();
//        Uri newTray = Uri.parse(resourseToConcat + stickerCategory);zz
        // Toast.makeText(StickerPackListActivity.this, "URI "+trayImage, Toast.LENGTH_SHORT).show();

        StickerPack sp = new StickerPack(
                newId,
                name,
                creator,
                trayImage,
                "",
                "",
                "",
                "",
                getContext());
        StickerBook.addStickerPackExisting(sp);

        Intent intent = new Intent(getContext(), StickerPackDetailsActivity.class);
        intent.putExtra(StickerPackDetailsActivity.EXTRA_SHOW_UP_BUTTON, true);
        intent.putExtra(StickerPackDetailsActivity.EXTRA_STICKER_PACK_DATA, newId);
        intent.putExtra("isNewlyCreated", true);
        this.startActivity(intent);
    }

    private void openFileTray(String name, String creator) {
        newName = name;
        newCreator = creator;

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(96, 96)
                .setFixAspectRatio(true)
                .start(getActivity());

    }

}
