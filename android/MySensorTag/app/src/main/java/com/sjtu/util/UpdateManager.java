package com.sjtu.util;



import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import com.ies.mysensortag.R;
import com.sjtu.user.MainActivity;

/**
 *@author coolszy
 *@date 2012-4-26
 *@blog http://blog.92coding.com
 */

public class UpdateManager
{
    private static final int DOWNLOAD = 1;
    private static final int DOWNLOAD_FINISH = 2;
    private String mSavePath;
    private int progress;
    private boolean cancelUpdate = false;
    private Context mContext;
    private ProgressBar mProgress;
    private Dialog mDownloadDialog;
    private Handler mHandler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
            case DOWNLOAD:
                mProgress.setProgress(progress);
                break;
            case DOWNLOAD_FINISH:
                installApk();
                break;
            default:
                break;
            }
        };
    };

    public UpdateManager(Context context)
    {
        this.mContext = context;
    }

    public void checkUpdate(int sv)
    {
        if (isUpdate(sv))
        {
            showNoticeDialog();
        } 
    }

    public boolean isUpdate(int sv)
    {
        int versionCode = getVersionCode(mContext);
            int serviceCode = sv;
            if (serviceCode > versionCode)
            {
                return true;
            }
        return false;
    }

/**
 * ��ȡ����汾��
 * 
 * @param context
 * @return
 */
private int getVersionCode(Context context)
{
    int versionCode = 0;
    try
    {
        // ��ȡ����汾�ţ���ӦAndroidManifest.xml��android:versionCode
        versionCode = context.getPackageManager().getPackageInfo("com.ies.mysensortag", 0).versionCode;
    } catch (NameNotFoundException e)
    {
        e.printStackTrace();
    }
    return versionCode;
}

    /**
     * ��ʾ������¶Ի���
     */
    private void showNoticeDialog()
    {
        // ����Ի���
        AlertDialog.Builder builder = new Builder(mContext);
        builder.setTitle("�����°汾");
        builder.setMessage("����Ҫ����ô��");
        // ����
        builder.setPositiveButton("����", new OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                // ��ʾ���ضԻ���
                showDownloadDialog();
            }
        });
        // �Ժ����
        builder.setNegativeButton("�Ժ�", new OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                Intent intent=new Intent();
			    intent.setClass(mContext, MainActivity.class);
			    mContext.startActivity(intent);
            }
        });
        Dialog noticeDialog = builder.create();
        noticeDialog.show();
    }

    /**
     * ��ʾ������ضԻ���
     */
    private void showDownloadDialog()
    {
        // ����������ضԻ���
        AlertDialog.Builder builder = new Builder(mContext);
        builder.setTitle("���ڸ���");
        // �����ضԻ������ӽ�����
        final LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.softupdate_progress, null);
        mProgress = (ProgressBar) v.findViewById(R.id.update_progress);
        builder.setView(v);
        // ȡ������
        builder.setNegativeButton("ȡ������", new OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                // ����ȡ��״̬
                cancelUpdate = true;
                Intent intent=new Intent();
			    intent.setClass(mContext, MainActivity.class);
			    mContext.startActivity(intent);
            }
        });
        mDownloadDialog = builder.create();
        mDownloadDialog.show();
        // �����ļ�
        downloadApk();
    }

    /**
     * ����apk�ļ�
     */
    private void downloadApk()
    {
        // �������߳��������
        new downloadApkThread().start();
    }

    /**
     * �����ļ��߳�
     * 
     * @author coolszy
     *@date 2012-4-26
     *@blog http://blog.92coding.com
     */
    private class downloadApkThread extends Thread
    {
        @Override
        public void run()
        {
            try
            {
                // �ж�SD���Ƿ���ڣ������Ƿ���ж�дȨ��
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
                {
                    // ��ô洢����·��
                    String sdpath = Environment.getExternalStorageDirectory() + "/";
                    mSavePath = sdpath + "download";
                    URL url = new URL("http://wxtestshit-pic.stor.sinaapp.com/MySensorTag.apk");
                    // ��������
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.connect();
                    // ��ȡ�ļ���С
                    int length = conn.getContentLength();
                    // ����������
                    InputStream is = conn.getInputStream();

                    File file = new File(mSavePath);
                    // �ж��ļ�Ŀ¼�Ƿ����
                    if (!file.exists())
                    {
                        file.mkdir();
                    }
                    File apkFile = new File(mSavePath, "MySensorTag.apk");
                    FileOutputStream fos = new FileOutputStream(apkFile);
                    int count = 0;
                    // ����
                    byte buf[] = new byte[1024];
                    // д�뵽�ļ���
                    do
                    {
                        int numread = is.read(buf);
                        count += numread;
                        // ���������λ��
                        progress = (int) (((float) count / length) * 100);
                        // ���½���
                        mHandler.sendEmptyMessage(DOWNLOAD);
                        if (numread <= 0)
                        {
                            // �������
                            mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
                            break;
                        }
                        // д���ļ�
                        fos.write(buf, 0, numread);
                    } while (!cancelUpdate);// ���ȡ����ֹͣ����.
                    fos.close();
                    is.close();
                }
            } catch (MalformedURLException e)
            {
                e.printStackTrace();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            // ȡ�����ضԻ�����ʾ
            mDownloadDialog.dismiss();
        }
    };

    /**
     * ��װAPK�ļ�
     */
    private void installApk()
    {
        File apkfile = new File(mSavePath, "MySensorTag.apk");
        if (!apkfile.exists())
        {
            return;
        }
        // ͨ��Intent��װAPK�ļ�
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
        mContext.startActivity(i);
    }
    
}