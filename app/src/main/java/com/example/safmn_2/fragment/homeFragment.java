package com.example.safmn_2.fragment;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.safmn_2.MainActivity;
import com.example.safmn_2.R;
import com.example.safmn_2.adapter.SpinnerAdapter;
import com.example.safmn_2.cppWrapper.SAFMNncnn;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


public class homeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //用作传给work的函数,指定加载哪个模型
    private int modelNum = 0;

    private static final int PICK_IMAGE_REQUEST = 1;

    private TextView textView;

    private ImageView imageView1 = null;

    private ImageView imageView2 = null;
    private Button selectImageButton1;

    private Button RunButton;

    private Button SaveButton;

    // 声明一个成员变量用于存储 JNI 函数返回的 Bitmap 对象
    private Bitmap processedImageData;

    //加载相册的图片
    private Bitmap bitmap;

    private Bitmap bitmap_s;

    //用来接收处理之后的bitmap，不能用与拿来大小的bitmap去接受，因为模型会把图片放大
    private Bitmap bitmap2;


    // 声明一个成员变量用于保存选定的图片Uri
    private Uri selectedImageUri;

    // 下拉框
    private Spinner spinner;

    private SpinnerAdapter spinnerAdapter;

    //下拉框的选项
    private List<String> datas = new ArrayList<>();

    //加载用于封装cpp函数的类
    private SAFMNncnn safmnncnn = new SAFMNncnn();


    public homeFragment() {
        // Required empty public constructor
    }



    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment homeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static homeFragment newInstance(String param1, String param2) {
        homeFragment fragment = new homeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment




        // 加载 Fragment 的布局文件
        View view = inflater.inflate(R.layout.fragment_home, container, false);


        selectImageButton1 = view.findViewById(R.id.button1);
        RunButton = view.findViewById(R.id.button_run);
        SaveButton = view.findViewById(R.id.button_save);

        imageView1 = view.findViewById(R.id.imageView1); // 初始化 imageView1
        imageView2 = view.findViewById(R.id.imageView2); // 初始化 imageView2

        spinner = view.findViewById(R.id.modelSpinner);

        spinnerAdapter = new SpinnerAdapter(getContext(), datas);
        // 设置适配器
        spinner.setAdapter(spinnerAdapter);


        datas.add("SAFMN_DF2K_x2");
        datas.add("SAFMN_L_Real_LSDIR_x4");

        // 设置默认选项
        spinner.setSelection(0); // 默认选中第1个项（索引从0开始）

        // 设置监听器
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // 在这里处理选中项变化的逻辑
                String selectedItem = (String) parent.getItemAtPosition(position);

                switch (selectedItem) {
                    case "SAFMN_DF2K_x2":
                        // 执行与 SAFMN_L_DF2K_x2 相关的处理
                        Toast.makeText(requireActivity(), "选择了SAFMN_DF2K_x2", Toast.LENGTH_SHORT).show();
                        // 在这里添加相关逻辑

                        modelNum = 0;


                        break;

                    case "SAFMN_L_Real_LSDIR_x4":
                        // 执行与 SAFMN_L_Real_LSDIR_x4 相关的处理
                        Toast.makeText(requireActivity(), "选择了SAFMN_L_Real_LSDIR_x4", Toast.LENGTH_SHORT).show();
                        // 在这里添加相关逻辑
                        modelNum = 1;



                        break;

                    default:
                        // 处理其他选项
                        Toast.makeText(requireActivity(), "未知选项: " + selectedItem, Toast.LENGTH_SHORT).show();
                        break;
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 在这里处理没有选中项的情况
//                Toast.makeText(requireActivity(), "Nothing selected", Toast.LENGTH_SHORT).show();
            }
        });


        // 在 Fragment 视图创建完成后，设置按钮的点击事件监听器
        selectImageButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        //将选择的条目显示处出来
        spinnerAdapter.notifyDataSetChanged();

        //对选择的图片进行处理
        RunButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                work();
            }
        });


        //将处理后的图片保存到相册
        SaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap temp = changeImageFromImageView(imageView2);

                if(temp != null) {
                    //询问是否要保存图片
                    AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                    builder.setMessage("是否保存处理后的图片到相册？");
                    builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            saveImageToGallery(temp);
                        }
                    });
                    builder.setNegativeButton("否", null);
                    builder.show();
                }
            }
        });

        //这里的返回值要和查找控件的view对象是同一个
        return view;
    }

    //打开相册
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // 处理从相册选择图片后的结果
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == requireActivity().RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            Toast.makeText(requireContext(), "挑选图片", Toast.LENGTH_SHORT).show();
            //获得选择的图片:bitmap_s是保留图片的副本
            bitmap_s = getSelectedImageBitmap();
            //把选中的图片显示在imageview中
            imageView1.setImageBitmap(bitmap_s);
        }
    }

    // 将图片保存到相册
    private void saveImageToGallery(Bitmap bitmap) {
        if(bitmap == null) {
        }
        else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            Uri uri = requireContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            OutputStream outputStream = null;
            try {
                outputStream = requireContext().getContentResolver().openOutputStream(uri);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                Toast.makeText(requireContext(), "图片已保存到相册", Toast.LENGTH_SHORT).show();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    // 在需要的地方获取选定的图片的Bitmap对象
    private Bitmap getSelectedImageBitmap() {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), selectedImageUri);
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    //如果选择了图片，对图片进行处理
    private void work() {
        if(selectedImageUri != null && modelNum == 0) {
            Toast.makeText(requireContext(), "正在执行，请稍等", Toast.LENGTH_SHORT).show();

            //加载模型
            AssetManager assetManager = getActivity().getAssets();
            //验证模型加载是否成功
            boolean ret_init = safmnncnn.Init(assetManager);
            if (!ret_init)
            {
                Log.e("load model", "safmnncnn Init failed");
            } else {
                Log.e("load model", "safmnncnn Init succeed");
            }

            //得到用来执行推理的图片:待处理的图片
            bitmap = getSelectedImageBitmap();

            int newWidth = bitmap.getWidth() * 2;
            int newHeight = bitmap.getHeight() * 2;
            bitmap2 = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);

            //执行推理
            processedImageData = safmnncnn.Detect(bitmap,bitmap2);


            //将处理后的图片显示在imageview2中
            imageView2.setImageBitmap(bitmap2);

        }else if(selectedImageUri != null && modelNum == 1){

            Toast.makeText(requireContext(), "正在执行，请稍等", Toast.LENGTH_SHORT).show();

            AssetManager assetManager = getActivity().getAssets();
            //验证模型加载是否成功
            boolean ret_init = safmnncnn.Init2(assetManager);
            if (!ret_init)
            {
                Log.e("load model", "safmnncnn Init failed");
            } else {
                Log.e("load model", "safmnncnn Init succeed");
            }

            //得到用来执行推理的图片:待处理的图片
            bitmap = getSelectedImageBitmap();

            int newWidth = bitmap.getWidth() * 4;
            int newHeight = bitmap.getHeight() * 4;
            bitmap2 = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);

            //执行推理
            processedImageData = safmnncnn.Detect(bitmap,bitmap2);

            //将处理后的图片显示在imageview2中
            imageView2.setImageBitmap(bitmap2);

        }else {
            Toast.makeText(requireContext(), "没有挑选图片", Toast.LENGTH_SHORT).show();
        }
    }

    //从imageview中返回对应的bitmap
    public Bitmap changeImageFromImageView(ImageView imageView) {
        Drawable drawable = imageView.getDrawable();

        if (drawable == null) {
            Toast.makeText(requireContext(), "没有处理之后的图片", Toast.LENGTH_SHORT).show();
            return null;
        }
        return ((BitmapDrawable) drawable).getBitmap();
    }

}